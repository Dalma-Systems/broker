package com.dalma.broker.fiware.orion.connector;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class OrionConnectorConfiguration {

    @Value("${fiware.orion.host}")
    private String orionHost;

    @Value("${fiware.orion.port}")
    private int orionPort;

    @Value("${fiware.orion.schema}")
    private String orionScheme;

    @Value("${fiware.orion.service}")
    private String fiwareService;

    @Value("${fiware.orion.query.limit}")
    private int queryLimit;
}
