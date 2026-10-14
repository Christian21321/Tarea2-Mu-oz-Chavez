package cl.ufro.isoft.tarea2.model;

import java.util.Locale;

import cl.ufro.isoft.tarea2.exception.UnidadInvalidaException;

/**
 * Unidades en que se registran las compras.
 * <ul>
 * <li>codigoApi: nombre del indicador en mindicador.cl y en la URL /compras/{unidad}</li>
 * <li>codigoCsv: valor de la columna "unidad" del archivo compras.csv</li>
 * </ul>
 */
public enum Unidad {

	UF("uf", "UF"),
	DOLAR("dolar", "USD");

	private final String codigoApi;
	private final String codigoCsv;

	Unidad(String codigoApi, String codigoCsv) {
		this.codigoApi = codigoApi;
		this.codigoCsv = codigoCsv;
	}

	public String getCodigoApi() {
		return codigoApi;
	}

	public String getCodigoCsv() {
		return codigoCsv;
	}

	/** Unidad indicada en la URL: "uf" o "dolar" (también se acepta "usd"). */
	public static Unidad desdeUrl(String valor) {
		String normalizado = valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
		for (Unidad unidad : values()) {
			if (unidad.codigoApi.equals(normalizado) || unidad.codigoCsv.toLowerCase(Locale.ROOT).equals(normalizado)) {
				return unidad;
			}
		}
		throw new UnidadInvalidaException(valor);
	}

	/** Unidad indicada en el CSV: "UF" o "USD". */
	public static Unidad desdeCsv(String valor) {
		String normalizado = valor == null ? "" : valor.trim();
		for (Unidad unidad : values()) {
			if (unidad.codigoCsv.equalsIgnoreCase(normalizado)) {
				return unidad;
			}
		}
		throw new IllegalArgumentException("Unidad desconocida en el CSV: " + valor);
	}

}
