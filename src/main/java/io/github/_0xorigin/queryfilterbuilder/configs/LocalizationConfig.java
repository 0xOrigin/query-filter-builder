package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Spring configuration for localization and message sources.
 */
@Configuration
public class LocalizationConfig {

    /**
     * Creates a {@link MessageSource} bean specifically for the query filter builder.
     * It is configured to read messages from a resource bundle located at {@code messages/messages}.
     *
     * @return A configured {@link ResourceBundleMessageSource} instance.
     */
    @Bean(name = "queryFilterBuilderMessageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(true);
        messageSource.setUseCodeAsDefaultMessage(false);
        return messageSource;
    }

    /**
     * Creates the {@link LocalizationService} bean.
     * This service acts as a convenient wrapper around the {@code MessageSource} to simplify message retrieval.
     *
     * @param messageSource The {@code queryFilterBuilderMessageSource} bean, injected by Spring.
     * @return A new {@link LocalizationService} instance.
     */
    @Bean
    public LocalizationService localizationService(@Qualifier("queryFilterBuilderMessageSource") MessageSource messageSource) {
        return new LocalizationService(messageSource);
    }
}
