package io.github._0xorigin.queryfilterbuilder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * An exception thrown when an invalid query parameter is provided by the client.
 * This exception is typically handled by a global exception handler to return a 400 Bad Request response.
 * It encapsulates a {@link MethodArgumentNotValidException} to provide detailed validation error information.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidQueryParameterException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2456821264780421595L;
    private final MethodArgumentNotValidException methodArgumentNotValidException;

    /**
     * Constructs a new exception with a specified message.
     *
     * @param message                         The detail message.
     * @param methodArgumentNotValidException The underlying validation exception.
     */
    public InvalidQueryParameterException(String message, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    /**
     * Constructs a new exception with a specified message and cause.
     *
     * @param message                         The detail message.
     * @param cause                           The cause.
     * @param methodArgumentNotValidException The underlying validation exception.
     */
    public InvalidQueryParameterException(String message, Throwable cause, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message, cause);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    /**
     * Constructs a new exception, deriving the message from the underlying validation exception.
     *
     * @param methodArgumentNotValidException The underlying validation exception.
     */
    public InvalidQueryParameterException(MethodArgumentNotValidException methodArgumentNotValidException) {
        super(methodArgumentNotValidException.getLocalizedMessage());
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    /**
     * Gets the {@link BindingResult} from the encapsulated validation exception.
     * This contains the detailed field errors.
     *
     * @return The binding result with validation errors.
     */
    public BindingResult getBindingResult() {
        return methodArgumentNotValidException.getBindingResult();
    }
}
