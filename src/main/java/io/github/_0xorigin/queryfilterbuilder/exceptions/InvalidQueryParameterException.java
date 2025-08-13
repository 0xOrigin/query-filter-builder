package io.github._0xorigin.queryfilterbuilder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidQueryParameterException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2456821264780421595L;
    private final MethodArgumentNotValidException methodArgumentNotValidException;

    public InvalidQueryParameterException(String message, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public InvalidQueryParameterException(String message, Throwable cause, MethodArgumentNotValidException methodArgumentNotValidException) {
        super(message, cause);
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public InvalidQueryParameterException(MethodArgumentNotValidException methodArgumentNotValidException) {
        super(methodArgumentNotValidException.getLocalizedMessage());
        this.methodArgumentNotValidException = methodArgumentNotValidException;
    }

    public BindingResult getBindingResult() {
        return methodArgumentNotValidException.getBindingResult();
    }
}
