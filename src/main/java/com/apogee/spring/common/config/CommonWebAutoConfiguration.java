package com.apogee.spring.common.config;

import com.apogee.spring.common.advice.GlobalExceptionHandler;
import com.apogee.spring.common.aspect.LoggerAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Auto-configuration class for common Spring utilities.
 * Automatically registers aspects and exception handlers for client applications.
 *
 * This configuration is enabled automatically when the package is added as a dependency
 * to a Spring Boot application.
 */
@AutoConfiguration
@EnableAspectJAutoProxy
public class CommonWebAutoConfiguration {

    /**
     * Registers the LoggerAspect as a bean for AOP logging.
     * Only created if not already defined by the client application.
     *
     * @return the LoggerAspect bean
     */
    @Bean
    @ConditionalOnMissingBean(LoggerAspect.class)
    public LoggerAspect loggerAspect() {
        return new LoggerAspect();
    }

    /**
     * Registers the GlobalExceptionHandler as a bean for centralized exception handling.
     * Only created if not already defined by the client application.
     *
     * @return the GlobalExceptionHandler bean
     */
    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}

