package cl.ufro.isoft.tarea2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UnidadInvalidaException.class)
	public ResponseEntity<ErrorResponse> unidadInvalida(UnidadInvalidaException e) {
		return respuesta(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(CompraNoEncontradaException.class)
	public ResponseEntity<ErrorResponse> compraNoEncontrada(CompraNoEncontradaException e) {
		return respuesta(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> parametroInvalido(MethodArgumentTypeMismatchException e) {
		return respuesta(HttpStatus.BAD_REQUEST,
				"El valor '" + e.getValue() + "' no es válido para '" + e.getName() + "'");
	}

	@ExceptionHandler(IndicadorNoDisponibleException.class)
	public ResponseEntity<ErrorResponse> indicadorNoDisponible(IndicadorNoDisponibleException e) {
		return respuesta(HttpStatus.BAD_GATEWAY, e.getMessage());
	}

	@ExceptionHandler(ArchivoComprasException.class)
	public ResponseEntity<ErrorResponse> archivoCompras(ArchivoComprasException e) {
		return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	private ResponseEntity<ErrorResponse> respuesta(HttpStatus status, String mensaje) {
		return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), mensaje));
	}

}
