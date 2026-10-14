package cl.ufro.isoft.tarea2.exception;

public class UnidadInvalidaException extends RuntimeException {

	public UnidadInvalidaException(String unidad) {
		super("La unidad '" + unidad + "' no existe. Unidades válidas: uf, dolar");
	}

}
