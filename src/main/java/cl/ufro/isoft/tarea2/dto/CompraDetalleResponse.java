package cl.ufro.isoft.tarea2.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta de GET /compras/detalle/{id}.
 */

public record CompraDetalleResponse(
		long id,
		String producto,
		double monto,
		String unidad,
		LocalDate fecha,
		@JsonProperty("valor_unidad") double valorUnidad,
		@JsonProperty("monto_pesos") long montoPesos) {
}
