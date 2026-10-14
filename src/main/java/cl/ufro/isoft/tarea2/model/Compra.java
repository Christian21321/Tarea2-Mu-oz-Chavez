package cl.ufro.isoft.tarea2.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Una fila del archivo compras.csv. */
public record Compra(long id, String producto, BigDecimal monto, Unidad unidad, LocalDate fecha) {
}
