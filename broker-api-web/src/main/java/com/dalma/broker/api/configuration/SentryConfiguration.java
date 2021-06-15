package com.dalma.broker.api.configuration;

import com.dalma.broker.base.error.exception.RestResponseException;
import io.sentry.DefaultSentryClientFactory;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.dsn.Dsn;
import io.sentry.spring.SentryExceptionResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class SentryConfiguration {
    private static final String EQUALS = "=";
    private static final String QUERY_SYMBOL = "?";
    private static final String IN_APP_FRAMES_OPTION = "stacktrace.app.packages";

    @Value("${sentry.dsn}")
    private String dsn;
    @Value("${sentry.release}")
    private String release;
    @Value("${sentry.environment}")
    private String env;
    @Value("${sentry.stacktrace.app-packages}")
    private String inAppFrames;

    @Bean
    public void setup() throws URISyntaxException {
        if (dsn == null || dsn.length() == 0) {
            return;
        }
        // Create DSN entity in URI format in order to put inAppFrames as DSN option
        String dsnPath = new StringBuilder(dsn).append(QUERY_SYMBOL).append(IN_APP_FRAMES_OPTION).append(EQUALS)
                .append(inAppFrames).toString();
        Dsn dsnEntity = new Dsn(new URI(dsnPath));
        DefaultSentryClientFactory clientFactory = new DefaultSentryClientFactory();
        SentryClient client = clientFactory.createSentryClient(dsnEntity);
        client.setEnvironment(env);
        client.setRelease(release);
        Sentry.setStoredClient(client);
    }

    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {

        return new SentryExceptionResolver() {

            @Override
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                                 Object handler, Exception ex) {
                Throwable rootCause = ex;

                while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
                    rootCause = rootCause.getCause();
                }

                if (!(ex instanceof RestResponseException)) {
                    super.resolveException(request, response, handler, ex);
                }

                return null;
            }

        };

    }

    @Bean
    public ServletContextInitializer sentryServletContextInitializer() {
        return new io.sentry.spring.SentryServletContextInitializer();
    }
}
