package io.github._0xorigin.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;

public class InvalidFilterConfigurationException extends RuntimeException {

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
