package io.github._0xorigin.queryfilterbuilder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class QueryBuilderConfigurationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5807320529587725676L;
    private final MethodArgumentNotValidException methodArgumentNotValidException;

    public QueryBuilderConfigurationException(String message, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public QueryBuilderConfigurationException(String message, Throwable cause, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message, cause);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public QueryBuilderConfigurationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        super(methodArgumentNotValidException.getLocalizedMessage());
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public BindingResult getBindingResult() {
        return methodArgumentNotValidException.getBindingResult();
    }
}
