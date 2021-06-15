package com.dalma.broker.api.configuration;

import com.bybright.logging.annotation.EnableRequestLogFilter;
import com.dalma.broker.base.config.ModelMapperConfiguration;
import com.dalma.broker.base.controller.RestErrorController;
import com.dalma.broker.base.controller.advice.ErrorHandlingControllerAdvice;
import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import({ModelMapperConfiguration.class, ErrorHandlingControllerAdvice.class, RestErrorController.class,
        SentryConfiguration.class, OrionConnectorConfiguration.class})
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableRequestLogFilter
public class ApplicationConfiguration {

}
