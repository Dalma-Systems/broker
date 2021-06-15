package com.dalma.broker.api.controller;

import com.dalma.broker.api.OrionTest;
import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.entity.robot.Robot;
import com.dalma.broker.service.notification.LatteApiPath;
import com.dalma.broker.service.notification.LatteBridge;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotSummaryOutputDto;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationBatteryData;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationData;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationInputDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.NOTIFICATION;
import static com.dalma.broker.contract.Paths.ROBOT;
import static com.dalma.broker.contract.Paths.STATUS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RobotControllerITest extends OrionTest {

    private static final String ROBOT_CONTROLLER_PATH = BASE_PATH + ROBOT;
    private String robotId;

    @MockBean
    private LatteBridge latteBridge;

    @BeforeAll
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Order(1)
    public void testCheckZeroRobotsInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + OrionConnector.QUERY_TYPE + EntityType.ROBOT.getType();
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<Robot> robots = objectMapper.readValue(response.getBody(), new TypeReference<List<Robot>>() {
            });
            assertTrue(robots.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testCheckZeroRobotsInApi() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH,
                HttpMethod.GET, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        String body = response.getBody().toString();
        try {
            List<BrokerRobotSummaryOutputDto> robots = objectMapper.readValue(body, new TypeReference<List<BrokerRobotSummaryOutputDto>>() {
            });
            assertTrue(robots.isEmpty());
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
    public void testCheckRobotInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + robotId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            Robot robot = objectMapper.readValue(response.getBody(), Robot.class);
            assertNotNull(robot);
            BrokerRobotInputDto robotCreated = createRobotRequest();
            assertEquals(robotCreated.getBattery(), robot.getBattery().getValue());
            assertEquals(robotCreated.getLatitude(), robot.getLocation().getValue().getCoordinates()[1]);
            assertEquals(robotCreated.getLongitude(), robot.getLocation().getValue().getCoordinates()[0]);
            assertEquals(robotCreated.getName(), robot.getName().getValue());
            assertEquals(robotCreated.getStatus(), robot.getStatus().getValue());
            assertEquals(robotCreated.getVersion(), robot.getVersion().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckRobotsInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<BrokerRobotSummaryOutputDto> robots = objectMapper.readValue(response.getBody(), new TypeReference<List<BrokerRobotSummaryOutputDto>>() {
            });
            assertEquals(1, robots.size());
            BrokerRobotInputDto robotCreated = createRobotRequest();
            assertEquals(robotCreated.getBattery(), robots.get(0).getBattery().getValue());
            assertEquals(robotCreated.getLatitude(), robots.get(0).getCoordinates().getValue()[1]);
            assertEquals(robotCreated.getLongitude(), robots.get(0).getCoordinates().getValue()[0]);
            assertEquals(robotCreated.getName(), robots.get(0).getName().getValue());
            assertEquals(robotCreated.getStatus(), robots.get(0).getStatus().getValue());
            assertEquals(robotCreated.getVersion(), robots.get(0).getVersion().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckRobotInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + robotId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerRobotSummaryOutputDto robot = objectMapper.readValue(response.getBody(), BrokerRobotSummaryOutputDto.class);
            assertNotNull(robot);
            BrokerRobotInputDto robotCreated = createRobotRequest();
            assertEquals(robotCreated.getBattery(), robot.getBattery().getValue());
            assertEquals(robotCreated.getLatitude(), robot.getCoordinates().getValue()[1]);
            assertEquals(robotCreated.getLongitude(), robot.getCoordinates().getValue()[0]);
            assertEquals(robotCreated.getName(), robot.getName().getValue());
            assertEquals(robotCreated.getStatus(), robot.getStatus().getValue());
            assertEquals(robotCreated.getVersion(), robot.getVersion().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testRetrieveNonexistentRobotInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateRobot() {
        BrokerRobotInputDto input = createUpdateRobotRequest();
        HttpEntity<BrokerRobotInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + robotId,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateNonexistentRobot() {
        BrokerRobotInputDto input = createUpdateRobotRequest();
        HttpEntity<BrokerRobotInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(5)
    public void testCheckUpdatedRobotInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + robotId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            Robot robot = objectMapper.readValue(response.getBody(), Robot.class);
            assertNotNull(robot);
            BrokerRobotInputDto robotCreated = createRobotRequest();
            BrokerRobotInputDto robotUpdated = createUpdateRobotRequest();
            robotCreated = mergeRequests(robotCreated, robotUpdated);
            assertEquals(robotCreated.getBattery(), robot.getBattery().getValue());
            assertEquals(robotCreated.getLatitude(), robot.getLocation().getValue().getCoordinates()[1]);
            assertEquals(robotCreated.getLongitude(), robot.getLocation().getValue().getCoordinates()[0]);
            assertEquals(robotCreated.getName(), robot.getName().getValue());
            assertEquals(robotCreated.getStatus(), robot.getStatus().getValue());
            assertEquals(robotCreated.getVersion(), robot.getVersion().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testCheckUpdatedRobotInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + robotId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerRobotSummaryOutputDto robot = objectMapper.readValue(response.getBody(), BrokerRobotSummaryOutputDto.class);
            assertNotNull(robot);
            BrokerRobotInputDto robotCreated = createRobotRequest();
            BrokerRobotInputDto robotUpdated = createUpdateRobotRequest();
            robotCreated = mergeRequests(robotCreated, robotUpdated);
            assertEquals(robotCreated.getBattery(), robot.getBattery().getValue());
            assertEquals(robotCreated.getLatitude(), robot.getCoordinates().getValue()[1]);
            assertEquals(robotCreated.getLongitude(), robot.getCoordinates().getValue()[0]);
            assertEquals(robotCreated.getName(), robot.getName().getValue());
            assertEquals(robotCreated.getStatus(), robot.getStatus().getValue());
            assertEquals(robotCreated.getVersion(), robot.getVersion().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(6)
    public void testNotificationStatus() {
        Mockito.when(latteBridge.postToLatte(Mockito.anyString(), Mockito.any(LatteApiPath.class), Mockito.anyString())).thenReturn(Strings.EMPTY);

        BrokerRobotNotificationInputDto input = createNotificationRequest();
        HttpEntity<BrokerRobotNotificationInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerRobotOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + NOTIFICATION + STATUS,
                HttpMethod.POST, request, BrokerRobotOutputDto.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteRobot() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + robotId,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteNonexistentRobot() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(8)
    public void testCheckZeroRobotsAfterDeleteInOrion() {
        testCheckZeroRobotsInOrion();
    }

    @Test
    @Order(8)
    public void testCheckZeroRobotsAfterDeleteInApi() {
        testCheckZeroRobotsInApi();
    }

    private BrokerRobotNotificationInputDto createNotificationRequest() {
        BrokerRobotNotificationInputDto input = new BrokerRobotNotificationInputDto();
        List<BrokerRobotNotificationData> data = new ArrayList<>(1);
        BrokerRobotNotificationData notificationData = new BrokerRobotNotificationData();
        notificationData.setId(robotId);
        BrokerRobotNotificationBatteryData batteryData = new BrokerRobotNotificationBatteryData();
        batteryData.setValue(100);
        notificationData.setBattery(batteryData);
        data.add(notificationData);
        input.setData(data);
        return input;
    }
}
