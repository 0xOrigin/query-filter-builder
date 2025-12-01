package io.github._0xorigin.queryfilterbuilder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * An exception thrown when there is a server-side configuration error in the Query Filter Builder.
 * This exception is typically handled by a global exception handler to return a 500 Internal Server Error response.
 * It encapsulates a {@link MethodArgumentNotValidException} to provide detailed validation error information.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class QueryBuilderConfigurationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5807320529587725676L;
    private final MethodArgumentNotValidException methodArgumentNotValidException;

    /**
     * Constructs a new exception with a specified message.
     *
     * @param message                         The detail message.
     * @param methodArgumentNotValidException The underlying validation exception.
     */
    public QueryBuilderConfigurationException(String message, MethodArgumentNotValidException methodArgumentNotValidException) {
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
    public QueryBuilderConfigurationException(String message, Throwable cause, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message, cause);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    /**
     * Constructs a new exception, deriving the message from the underlying validation exception.
     *
     * @param methodArgumentNotValidException The underlying validation exception.
     */
    public QueryBuilderConfigurationException(MethodArgumentNotValidException methodArgumentNotValidException) {
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
