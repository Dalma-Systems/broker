package com.dalma.broker.api.controller;

import com.dalma.broker.api.OrionTest;
import com.dalma.broker.api.controller.dto.OrionUpdateStatus;
import com.dalma.broker.contract.dto.subscription.BrokerSubscriptionOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.entity.OrionEntityType;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.subscription.Subscription;
import com.dalma.broker.service.mapper.OrionBaseMapper;
import com.dalma.broker.service.notification.RobotNotificationHandler;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotOutputDto;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationInputDto;
import com.dalma.contract.dto.subscription.BrokerSubscriptionEntityInputDto;
import com.dalma.contract.dto.subscription.BrokerSubscriptionInputDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.ROBOT;
import static com.dalma.broker.contract.Paths.SUBSCRIPTION;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SubscriptionControllerITest extends OrionTest {

    private static final String SUBSCRIPTION_CONTROLLER_PATH = BASE_PATH + SUBSCRIPTION;
    private static final String ROBOT_CONTROLLER_PATH = BASE_PATH + ROBOT;
    private String robotId;
    private String subscriptionId;

    @MockBean
    private RobotNotificationHandler robotNotificationHandler;

    @Captor
    ArgumentCaptor<BrokerRobotNotificationInputDto> notificationCaptor;

    @BeforeAll
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Order(1)
    public void testCheckZeroSubscriptionsInOrion() {
        String address = orion + OrionConnector.SUBSCRIPTIONS_PATH;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<Subscription> subscriptions = objectMapper.readValue(response.getBody(), new TypeReference<List<Subscription>>() {
            });
            assertTrue(subscriptions.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testAddRobot() {
        BrokerRobotInputDto input = createRobotRequest();
        HttpEntity<BrokerRobotInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerRobotOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH,
                HttpMethod.POST, request, BrokerRobotOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        robotId = response.getBody().getOrionId();
        assertEquals(EntityType.ROBOT.getType(), response.getBody().getType());
    }

    @Test
    @Order(3)
    public void testAddSubscription() {
        BrokerSubscriptionInputDto input = createSubscriptionRequest();
        HttpEntity<BrokerSubscriptionInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerSubscriptionOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + SUBSCRIPTION_CONTROLLER_PATH,
                HttpMethod.POST, request, BrokerSubscriptionOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        subscriptionId = response.getBody().getOrionId();
    }

    @Test
    @Order(4)
    public void testCheckSubscriptionInOrion() {
        String address = orion + OrionConnector.SUBSCRIPTIONS_PATH + Constant.SLASH + subscriptionId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            Subscription subscription = objectMapper.readValue(response.getBody(), Subscription.class);
            assertNotNull(subscription);
            BrokerSubscriptionInputDto subscriptionCreated = createSubscriptionRequest();
            assertEquals(subscriptionId, subscription.getId());
            assertEquals(subscriptionCreated.getNotificationUrl(), subscription.getNotification().getHttp().getUrl());
            assertEquals(subscriptionCreated.getNotificationAttributes(), subscription.getNotification().getAttrs());
            assertEquals(1, subscription.getSubject().getEntities().size());
            assertEquals(robotId, subscription.getSubject().getEntities().get(0).getId());
            assertEquals(EntityType.ROBOT.getType(), subscription.getSubject().getEntities().get(0).getType());
            assertEquals(subscriptionCreated.getAttributes(), subscription.getSubject().getCondition().getAttrs());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testCheckOrionNotifiesApi() throws InterruptedException {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + robotId + OrionConnector.ATTRS;
        OrionUpdateStatus input = createOrionUpdateStatusRequest();
        ResponseEntity<Object> response = restTemplate.postForEntity(address, input, Object.class);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue());
        int safeGuard = 10;
        while (safeGuard > 0) {
            try {
                Mockito.verify(robotNotificationHandler).handleNotification(notificationCaptor.capture());
                break;
            } catch (WantedButNotInvoked e) {
                safeGuard -= 1;
                Thread.sleep(1000); // NOSONAR
            }
        }

        BrokerRobotNotificationInputDto valueCaptured = notificationCaptor.getValue();
        assertNotNull(valueCaptured);
        assertEquals(subscriptionId, valueCaptured.getSubscriptionId());
        assertNotNull(valueCaptured.getData());
        assertEquals(1, valueCaptured.getData().size());
        assertNotNull(valueCaptured.getData().get(0).getStatus());
        assertNull(valueCaptured.getData().get(0).getLocation());
        assertNull(valueCaptured.getData().get(0).getBattery());
        assertEquals(input.getStatus().getValue(), valueCaptured.getData().get(0).getStatus().getValue());
        assertEquals(input.getStatus().getMetadata().getContext().getValue(), valueCaptured.getData().get(0).getStatus().getMetadata().getContext().getValue());
    }

    @Test
    @Order(6)
    public void testDeleteSubscription() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + SUBSCRIPTION_CONTROLLER_PATH + Constant.SLASH + subscriptionId,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteNonexistentSubscription() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + SUBSCRIPTION_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(8)
    public void testDeleteRobot() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + robotId,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(9)
    public void testCheckZeroSubscriptionsAfterDeleteInOrion() {
        testCheckZeroSubscriptionsInOrion();
    }

    private BrokerSubscriptionInputDto createSubscriptionRequest() {
        BrokerSubscriptionInputDto request = new BrokerSubscriptionInputDto();

        BrokerSubscriptionEntityInputDto entity = new BrokerSubscriptionEntityInputDto();
        entity.setId(robotId);
        entity.setType(EntityType.ROBOT.getType());
        List<BrokerSubscriptionEntityInputDto> entities = List.of(entity);
        request.setEntities(entities);

        List<String> attributes = List.of("status");
        request.setAttributes(attributes);

        List<String> notificationAttr = new ArrayList<>(attributes.size() + 1);
        notificationAttr.addAll(attributes);
        notificationAttr.add(Constant.ID);
        request.setNotificationAttributes(notificationAttr);

        try {
            InetAddress ip = InetAddress.getLocalHost();
            request.setNotificationUrl("http://" + ip.getHostAddress() + ":" + port + "/api/broker/robot/notification/status");
        } catch (UnknownHostException e) {
            fail(e.getMessage());
        }

        return request;
    }

    private OrionUpdateStatus createOrionUpdateStatusRequest() {
        OrionUpdateStatus input = new OrionUpdateStatus();
        OrionAttribute<String> status = new OrionAttribute<>();
        status.setType(OrionEntityType.TEXT.getType());
        status.setValue("stopped");
        status.setMetadata(OrionBaseMapper.retrieveMetadata());
        status.getMetadata().setContext(OrionBaseMapper.createOrionBaseAttribute("contextId"));
        input.setStatus(status);
        return input;
    }
}
