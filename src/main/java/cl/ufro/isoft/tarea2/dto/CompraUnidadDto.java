package cl.ufro.isoft.tarea2.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Compra convertida a pesos dentro de la respuesta de /compras/{unidad}. */
public record CompraUnidadDto(
		long id,
		String producto,
		double monto,
		LocalDate fecha,
		@JsonProperty("valor_unidad") double valorUnidad,
		@JsonProperty("monto_pesos") long montoPesos) {
}
