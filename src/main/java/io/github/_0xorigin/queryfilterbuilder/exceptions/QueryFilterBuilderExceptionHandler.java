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

/**
 * An abstract base class for handling exceptions thrown by the Query Filter Builder.
 * To use this, create a concrete class that extends this class and annotate it with {@code @RestControllerAdvice}.
 * It provides handlers for {@link InvalidQueryParameterException} and {@link QueryBuilderConfigurationException},
 * creating a standardized {@link ApiErrorResponse}.
 */
public abstract class QueryFilterBuilderExceptionHandler {

    private final LocalizationService localizationService;

    /**
     * Constructs the exception handler with a localization service.
     *
     * @param localizationService The service for retrieving localized error messages.
     */
    protected QueryFilterBuilderExceptionHandler(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    /**
     * Handles {@link InvalidQueryParameterException}s, returning a 400 Bad Request response.
     * The response body contains a structured error message with details of all validation failures.
     *
     * @param exception The exception that was thrown.
     * @param request   The current web request.
     * @return A {@link ResponseEntity} with a 400 status and an {@link ApiErrorResponse} body.
     */
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

    /**
     * Handles {@link QueryBuilderConfigurationException}s, returning a 500 Internal Server Error response.
     * The response body contains a structured error message with details of all validation failures.
     *
     * @param exception The exception that was thrown.
     * @param request   The current web request.
     * @return A {@link ResponseEntity} with a 500 status and an {@link ApiErrorResponse} body.
     */
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

    /**
     * Extracts the request path from the given web request.
     *
     * @param request The current web request.
     * @return The request path.
     */
    protected String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * Returns the localization service.
     *
     * @return The localization service.
     */
    protected LocalizationService getLocalizationService() {
        return localizationService;
    }
}
