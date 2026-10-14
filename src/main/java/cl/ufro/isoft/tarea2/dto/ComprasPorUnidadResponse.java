package cl.ufro.isoft.tarea2.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Respuesta de GET /compras/{unidad}. */
public record ComprasPorUnidadResponse(
		String unidad,
		List<CompraUnidadDto> compras,
		@JsonProperty("total_pesos") long totalPesos) {
}
