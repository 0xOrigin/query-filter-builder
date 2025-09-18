package io.github._0xorigin.queryfilterbuilder.base.services;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * A service for retrieving localized messages from a {@link MessageSource}.
 * It simplifies the process by automatically using the current locale from {@link LocaleContextHolder}.
 */
public final class LocalizationService {

    private final MessageSource messageSource;

    /**
     * Constructs a new LocalizationService.
     *
     * @param messageSource The Spring MessageSource to be used for message retrieval.
     */
    public LocalizationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Retrieves a message for the given code, using the current locale.
     *
     * @param code The code of the message to retrieve (e.g., "error.not.found"). Must not be null.
     * @return The resolved message.
     */
    public String getMessage(@NonNull String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a message for the given code and arguments, using the current locale.
     *
     * @param code The code of the message to retrieve. Must not be null.
     * @param args An array of arguments to be filled into the message, or null if none.
     * @return The resolved message.
     */
    public String getMessage(@NonNull String code, @Nullable Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a message for the given code and arguments, with a default message, using the current locale.
     *
     * @param code           The code of the message to retrieve. Must not be null.
     * @param defaultMessage A default message to return if the lookup fails.
     * @param args           An array of arguments to be filled into the message, or null if none.
     * @return The resolved message, or the default message if the lookup fails.
     */
    public String getMessageWithDefault(@NonNull String code, @Nullable String defaultMessage, @Nullable Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }
}
