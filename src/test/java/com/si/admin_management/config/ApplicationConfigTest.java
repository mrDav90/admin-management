package com.si.admin_management.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationConfigTest {
    @Test
    void messageSource_beanCreation_shouldReturnCorrectlyConfiguredInstance() {
        ApplicationConfig config = new ApplicationConfig();
        MessageSource messageSource = config.messageSource();

        assertThat(messageSource).isNotNull();

        assertThat(messageSource).isInstanceOf(ResourceBundleMessageSource.class);

    }
}
