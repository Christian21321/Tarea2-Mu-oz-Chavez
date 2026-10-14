package cl.ufro.isoft.tarea2.indicador;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import cl.ufro.isoft.tarea2.exception.IndicadorNoDisponibleException;
import cl.ufro.isoft.tarea2.model.Unidad;

/**
 * Cliente de la API pública https://mindicador.cl.
 * <p>
 * Consulta usada: {@code GET /api/{tipo_indicador}/{dd-mm-yyyy}}, que entrega el valor
 * del indicador (uf o dolar) en esa fecha. El dólar no se publica los fines de semana ni
 * feriados; en ese caso la serie viene vacía y se usa el último valor publicado antes de
 * la fecha, consultando {@code GET /api/{tipo_indicador}/{yyyy}}.
 */
@Component
public class MindicadorClient implements IndicadorClient {

	private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final ZoneId ZONA_CHILE = ZoneId.of("America/Santiago");

	private final RestClient restClient;
	private final Map<String, Double> cache = new ConcurrentHashMap<>();

	public MindicadorClient(@Value("${mindicador.url}") String urlBase) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(Duration.ofSeconds(5));
		requestFactory.setReadTimeout(Duration.ofSeconds(15));
		this.restClient = RestClient.builder().baseUrl(urlBase).requestFactory(requestFactory).build();
	}

	@Override
	public double valor(Unidad unidad, LocalDate fecha) {
		// Los valores de fechas pasadas no cambian, así que se guardan para no repetir consultas.
		return cache.computeIfAbsent(unidad.name() + "@" + fecha, clave -> consultar(unidad, fecha));
	}

	private double consultar(Unidad unidad, LocalDate fecha) {
		try {
			Respuesta delDia = restClient.get()
				.uri("/{tipo}/{fecha}", unidad.getCodigoApi(), fecha.format(FORMATO_FECHA))
				.retrieve()
				.body(Respuesta.class);
			if (delDia != null && delDia.serie() != null && !delDia.serie().isEmpty()) {
				return delDia.serie().get(0).valor();
			}
			return ultimoValorAnterior(unidad, fecha, fecha.getYear())
				.or(() -> ultimoValorAnterior(unidad, fecha, fecha.getYear() - 1))
				.orElseThrow(() -> new IndicadorNoDisponibleException(
						"mindicador.cl no tiene valor de " + unidad.getCodigoApi() + " para " + fecha, null));
		}
		catch (RestClientException e) {
			throw new IndicadorNoDisponibleException(
					"No se pudo consultar " + unidad.getCodigoApi() + " para " + fecha + " en mindicador.cl", e);
		}
	}

	private Optional<Double> ultimoValorAnterior(Unidad unidad, LocalDate fecha, int anio) {
		Respuesta delAnio = restClient.get()
			.uri("/{tipo}/{anio}", unidad.getCodigoApi(), anio)
			.retrieve()
			.body(Respuesta.class);
		if (delAnio == null || delAnio.serie() == null) {
			return Optional.empty();
		}
		return delAnio.serie()
			.stream()
			.filter(punto -> !punto.fechaLocal().isAfter(fecha))
			.max(Comparator.comparing(Punto::fechaLocal))
			.map(Punto::valor);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Respuesta(List<Punto> serie) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Punto(String fecha, double valor) {

		/** mindicador entrega la fecha en UTC (ej. 2026-09-01T03:00:00.000Z), que corresponde al día en Chile. */
		LocalDate fechaLocal() {
			return Instant.parse(fecha).atZone(ZONA_CHILE).toLocalDate();
		}

	}

}
