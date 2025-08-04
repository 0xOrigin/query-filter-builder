package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class LocalizationConfig {

    @Bean(name = "queryFilterBuilderMessageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(true);
        messageSource.setUseCodeAsDefaultMessage(false);
        return messageSource;
    }

    @Bean
    public LocalizationService localizationService(@Qualifier("queryFilterBuilderMessageSource") MessageSource messageSource) {
        return new LocalizationService(messageSource);
    }
}
