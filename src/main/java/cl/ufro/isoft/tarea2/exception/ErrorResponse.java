package cl.ufro.isoft.tarea2.exception;

/** Cuerpo de las respuestas de error. */
public record ErrorResponse(int status, String error, String mensaje) {
}
