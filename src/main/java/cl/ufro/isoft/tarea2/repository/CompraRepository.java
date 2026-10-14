package cl.ufro.isoft.tarea2.repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import cl.ufro.isoft.tarea2.exception.ArchivoComprasException;
import cl.ufro.isoft.tarea2.model.Compra;
import cl.ufro.isoft.tarea2.model.Unidad;

/**
 * Lee las compras desde el archivo CSV indicado en CSV_PATH.
 * El archivo se lee en cada consulta, así los cambios se ven sin reiniciar la aplicación.
 */
@Repository
public class CompraRepository {

	private final Path rutaCsv;

	public CompraRepository(@Value("${app.csv-path}") String rutaCsv) {
		this.rutaCsv = Path.of(rutaCsv);
	}

	/** Todas las compras, en el mismo orden del CSV. */
	public List<Compra> findAll() {
		List<String> lineas;
		try {
			lineas = Files.readAllLines(rutaCsv, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			throw new ArchivoComprasException("No se pudo leer el archivo de compras: " + rutaCsv, e);
		}

		List<Compra> compras = new ArrayList<>();
		for (int i = 1; i < lineas.size(); i++) { // la línea 0 es el encabezado
			String linea = lineas.get(i).strip();
			if (!linea.isEmpty()) {
				compras.add(parsear(linea, i + 1));
			}
		}
		return compras;
	}

	public List<Compra> findByUnidad(Unidad unidad) {
		return findAll().stream().filter(compra -> compra.unidad() == unidad).toList();
	}

	public Optional<Compra> findById(long id) {
		return findAll().stream().filter(compra -> compra.id() == id).findFirst();
	}

	private Compra parsear(String linea, int numeroLinea) {
		String[] columnas = linea.split(",");
		if (columnas.length != 5) {
			throw new ArchivoComprasException("Línea " + numeroLinea + " del CSV no tiene 5 columnas: " + linea, null);
		}
		try {
			return new Compra(
					Long.parseLong(columnas[0].strip()),
					columnas[1].strip(),
					new BigDecimal(columnas[2].strip()),
					Unidad.desdeCsv(columnas[3]),
					LocalDate.parse(columnas[4].strip()));
		}
		catch (RuntimeException e) {
			throw new ArchivoComprasException("Línea " + numeroLinea + " del CSV no es válida: " + linea, e);
		}
	}

}
