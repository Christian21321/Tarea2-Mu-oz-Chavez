package cl.ufro.isoft.tarea2.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import cl.ufro.isoft.tarea2.indicador.IndicadorClient;
import cl.ufro.isoft.tarea2.model.Compra;

/** Convierte compras a pesos chilenos según el valor de su unidad en la fecha de la compra. */
@Service
public class ConversionService {

	private final IndicadorClient indicadorClient;

	public ConversionService(IndicadorClient indicadorClient) {
		this.indicadorClient = indicadorClient;
	}

	public double valorUnidad(Compra compra) {
		return indicadorClient.valor(compra.unidad(), compra.fecha());
	}

	/** monto × valor de la unidad, redondeado al peso más cercano. */
	public long montoPesos(Compra compra, double valorUnidad) {
		return compra.monto()
			.multiply(BigDecimal.valueOf(valorUnidad))
			.setScale(0, RoundingMode.HALF_UP)
			.longValueExact();
	}

}
