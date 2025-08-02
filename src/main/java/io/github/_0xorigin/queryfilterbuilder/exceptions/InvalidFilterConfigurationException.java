package io.github._0xorigin.queryfilterbuilder.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.Serial;

public class InvalidFilterConfigurationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5807320529587725676L;
    private final MethodArgumentNotValidException methodArgumentNotValidException;

    public InvalidFilterConfigurationException(String message) {
        super(message);
        methodArgumentNotValidException = null;
    }

    public InvalidFilterConfigurationException(String message, Throwable cause) {
        super(message, cause);
        methodArgumentNotValidException = null;
    }

    public InvalidFilterConfigurationException(String message, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public InvalidFilterConfigurationException(String message, Throwable cause, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message, cause);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public InvalidFilterConfigurationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        super(methodArgumentNotValidException.getMessage());
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public MethodArgumentNotValidException getMethodArgumentNotValidException() {
        return methodArgumentNotValidException;
    }
}
