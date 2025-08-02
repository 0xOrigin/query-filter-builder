package io.github._0xorigin.queryfilterbuilder.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.Serial;

public class InvalidQueryFilterValueException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2456821264780421595L;
    private final MethodArgumentNotValidException methodArgumentNotValidException;

    public InvalidQueryFilterValueException(String message) {
        super(message);
        methodArgumentNotValidException = null;
    }

    public InvalidQueryFilterValueException(String message, Throwable cause) {
        super(message, cause);
        methodArgumentNotValidException = null;
    }

    public InvalidQueryFilterValueException(String message, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public InvalidQueryFilterValueException(String message, Throwable cause, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message, cause);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public InvalidQueryFilterValueException(MethodArgumentNotValidException methodArgumentNotValidException) {
        super(methodArgumentNotValidException.getMessage());
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public MethodArgumentNotValidException getMethodArgumentNotValidException() {
        return methodArgumentNotValidException;
    }
}
