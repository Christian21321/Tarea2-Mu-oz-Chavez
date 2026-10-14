package cl.ufro.isoft.tarea2.exception;

public class CompraNoEncontradaException extends RuntimeException {

	public CompraNoEncontradaException(long id) {
		super("No existe una compra con id " + id);
	}

}
