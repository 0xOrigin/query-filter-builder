package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidFilterConfigurationException;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidQueryFilterValueException;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order
public class QueryFilterBuilderExceptionHandler {

    private final MessageSource messageSource;

    public QueryFilterBuilderExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(InvalidQueryFilterValueException.class)
    public ResponseEntity<?> handleException(@NotNull InvalidQueryFilterValueException e, WebRequest request, Locale locale) {
        Map<String, List<String>> groupedErrors = e.getMethodArgumentNotValidException()
                .getBindingResult()
                .getAllErrors()
                .stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Query strings validation failed");
        response.put("errors", groupedErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidFilterConfigurationException.class)
    public ResponseEntity<?> handleException(@NotNull InvalidFilterConfigurationException e, WebRequest request, Locale locale) {
        Map<String, List<String>> groupedErrors = e.getMethodArgumentNotValidException()
                .getBindingResult()
                .getAllErrors()
                .stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Query strings configuration failed");
        response.put("errors", groupedErrors);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
