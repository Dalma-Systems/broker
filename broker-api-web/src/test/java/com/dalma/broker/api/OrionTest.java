package com.dalma.broker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public abstract class OrionTest extends OrionUtilsTest {

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected ObjectMapper objectMapper;

    protected static final String FAKE_ENTITY = "FAKE_ENTITY";
    private static final Integer orionPort = 1027;
    protected static String orion;

    @SuppressWarnings("rawtypes")
    @ClassRule
    public static DockerComposeContainer compose = new DockerComposeContainer(
            new File("src/test/resources/docker-compose.yml"));

    @BeforeAll
    private void setup() throws InterruptedException {
        compose.start();
        orion = new StringBuilder("http://").append(compose.getServiceHost("orion", orionPort)).append(":").append(orionPort).toString();
        log.info(new StringBuilder("Connecting to orion at ").append(orion).toString());
        waitUntilOrionStarts();
    }

    private void waitUntilOrionStarts() throws InterruptedException {
        int safeGuard = 10;
        String versionUrl = new StringBuilder(orion).append("/version").toString();
        while (safeGuard > 0) {
            try {
                ResponseEntity<String> responseVersion = restTemplate.getForEntity(versionUrl, String.class);
                assertEquals(HttpStatus.OK.value(), responseVersion.getStatusCodeValue());
                log.info("Running Orion version: " + responseVersion.getBody().split("\"version\" : \"")[1].split("\"")[0]);
                return;
            } catch (ResourceAccessException e) {
                safeGuard -= 1;
                Thread.sleep(1000); // NOSONAR
            }
        }
        throw new RuntimeException("Waitted 10 seconds for Orion to start. Unable to check Orion version");
    }
}
