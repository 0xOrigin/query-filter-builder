package io.github._0xorigin.queryfilterbuilder.exceptions;

import io.github._0xorigin.queryfilterbuilder.base.dtos.ApiErrorResponse;
import io.github._0xorigin.queryfilterbuilder.base.enums.MessageKey;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class QueryFilterBuilderExceptionHandler {

    private final LocalizationService localizationService;

    protected QueryFilterBuilderExceptionHandler(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @ExceptionHandler(InvalidQueryParameterException.class)
    protected ResponseEntity<?> handleException(InvalidQueryParameterException exception, WebRequest request) {
        Map<String, List<String>> groupedErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                    FieldError::getField,
                    Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        ApiErrorResponse errorResponse = new ApiErrorResponse(
            localizationService.getMessage(MessageKey.INVALID_QUERY_PARAMETER_EXCEPTION_MESSAGE.getCode()),
            getRequestPath(request),
            groupedErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(QueryBuilderConfigurationException.class)
    protected ResponseEntity<?> handleException(QueryBuilderConfigurationException exception, WebRequest request) {
        Map<String, List<String>> groupedErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                    FieldError::getField,
                    Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        ApiErrorResponse errorResponse = new ApiErrorResponse(
            localizationService.getMessage(MessageKey.QUERY_BUILDER_CONFIGURATION_EXCEPTION_MESSAGE.getCode()),
            getRequestPath(request),
            groupedErrors
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
