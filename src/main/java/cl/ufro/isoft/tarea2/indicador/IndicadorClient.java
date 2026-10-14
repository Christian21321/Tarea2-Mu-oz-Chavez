package cl.ufro.isoft.tarea2.indicador;

import java.time.LocalDate;

import cl.ufro.isoft.tarea2.model.Unidad;

/** Entrega el valor en pesos de una unidad en una fecha. */
public interface IndicadorClient {

	double valor(Unidad unidad, LocalDate fecha);

}
