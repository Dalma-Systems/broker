package com.dalma.broker.api.controller;

import com.dalma.broker.api.OrionTest;
import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationInputDto;
import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationOutputDto;
import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStation;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotSummaryOutputDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.IDLE_STATION;
import static com.dalma.broker.contract.Paths.ROBOT;
import static com.dalma.broker.contract.Paths.STATION;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdleStationControllerITest extends OrionTest {

    private static final String ROBOT_CONTROLLER_PATH = BASE_PATH + ROBOT;
    private static final String IDLE_STATION_CONTROLLER_PATH = BASE_PATH + STATION + IDLE_STATION;
    private String stationId;

    @Test
    @Order(1)
    public void testCheckZeroIdleStationsInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + OrionConnector.QUERY_TYPE + EntityType.IDLE_STATION.getType();
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<IdleStation> stations = objectMapper.readValue(response.getBody(), new TypeReference<List<IdleStation>>() {
            });
            assertTrue(stations.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testCheckZeroIdleStationsInApi() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH,
                HttpMethod.GET, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        String body = response.getBody().toString();
        try {
            List<BrokerIdleStationSummaryOutputDto> stations = objectMapper.readValue(body, new TypeReference<List<BrokerIdleStationSummaryOutputDto>>() {
            });
            assertTrue(stations.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testAddStation() {
        BrokerIdleStationInputDto input = createStationRequest();
        HttpEntity<BrokerIdleStationInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerIdleStationOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH,
                HttpMethod.POST, request, BrokerIdleStationOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        stationId = response.getBody().getOrionId();
    }

    @Test
    @Order(3)
    public void testCheckStationInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + stationId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            IdleStation station = objectMapper.readValue(response.getBody(), IdleStation.class);
            assertNotNull(station);
            BrokerIdleStationInputDto stationCreated = createStationRequest();
            assertEquals(stationCreated.getLatitude(), station.getLocation().getValue().getCoordinates()[1]);
            assertEquals(stationCreated.getLongitude(), station.getLocation().getValue().getCoordinates()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getLocation().getMetadata().getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckStationsInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<BrokerIdleStationSummaryOutputDto> stations = objectMapper.readValue(response.getBody(), new TypeReference<List<BrokerIdleStationSummaryOutputDto>>() {
            });
            assertEquals(1, stations.size());
            BrokerIdleStationInputDto stationCreated = createStationRequest();
            assertEquals(stationCreated.getLatitude(), stations.get(0).getCoordinates().getValue()[1]);
            assertEquals(stationCreated.getLongitude(), stations.get(0).getCoordinates().getValue()[0]);
            assertEquals(stationCreated.getName(), stations.get(0).getName().getValue());
            assertEquals(stationCreated.getStatus(), stations.get(0).getStatus().getValue());
            assertEquals(stationCreated.getAngle(), stations.get(0).getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckStationInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerIdleStationSummaryOutputDto station = objectMapper.readValue(response.getBody(), BrokerIdleStationSummaryOutputDto.class);
            assertNotNull(station);
            BrokerIdleStationInputDto stationCreated = createStationRequest();
            assertEquals(stationCreated.getLatitude(), station.getCoordinates().getValue()[1]);
            assertEquals(stationCreated.getLongitude(), station.getCoordinates().getValue()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testRetrieveNonexistentStationInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateStation() {
        BrokerIdleStationInputDto input = createUpdateStationRequest();
        HttpEntity<BrokerIdleStationInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateNonexistentStation() {
        BrokerIdleStationInputDto input = createUpdateStationRequest();
        HttpEntity<BrokerIdleStationInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(5)
    public void testCheckUpdatedStationInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + stationId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            IdleStation station = objectMapper.readValue(response.getBody(), IdleStation.class);
            assertNotNull(station);
            BrokerIdleStationInputDto stationCreated = createStationRequest();
            BrokerIdleStationInputDto stationUpdated = createUpdateStationRequest();
            stationCreated = mergeRequests(stationCreated, stationUpdated);
            assertEquals(stationCreated.getLatitude(), station.getLocation().getValue().getCoordinates()[1]);
            assertEquals(stationCreated.getLongitude(), station.getLocation().getValue().getCoordinates()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getLocation().getMetadata().getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testCheckUpdatedStationInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerIdleStationSummaryOutputDto station = objectMapper.readValue(response.getBody(), BrokerIdleStationSummaryOutputDto.class);
            assertNotNull(station);
            BrokerIdleStationInputDto stationCreated = createStationRequest();
            BrokerIdleStationInputDto stationUpdated = createUpdateStationRequest();
            stationCreated = mergeRequests(stationCreated, stationUpdated);
            assertEquals(stationCreated.getLatitude(), station.getCoordinates().getValue()[1]);
            assertEquals(stationCreated.getLongitude(), station.getCoordinates().getValue()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(6)
    public void testAddStationToRobotRelationAndViceVersaRelation() {
        // Add robot
        BrokerRobotInputDto input = createRobotRequest();
        HttpEntity<BrokerRobotInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerRobotOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH,
                HttpMethod.POST, request, BrokerRobotOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        assertEquals(EntityType.ROBOT.getType(), response.getBody().getType());

        // Update robot with station as relation
        BrokerRobotInputDto input2 = createUpdateRobotRequest();
        input2.setDestination(stationId);
        HttpEntity<BrokerRobotInputDto> request2 = new HttpEntity<>(input2);
        ResponseEntity<Object> response2 = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + response.getBody().getOrionId(),
                HttpMethod.PATCH, request2, Object.class);
        assertEquals(HttpStatus.OK.value(), response2.getStatusCodeValue());

        // Check station in robot details
        ResponseEntity<String> response3 = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + response.getBody().getOrionId(),
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response3.getStatusCodeValue());
        try {
            BrokerRobotSummaryOutputDto robot = objectMapper.readValue(response3.getBody(), BrokerRobotSummaryOutputDto.class);
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
            assertEquals(stationId, robot.getDestination().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // Update station with robot relation
        BrokerIdleStationInputDto input4 = createUpdateStationRequest();
        input4.setRobotId(response.getBody().getOrionId());
        HttpEntity<BrokerIdleStationInputDto> request4 = new HttpEntity<>(input4);
        ResponseEntity<Object> response4 = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.PATCH, request4, Object.class);
        assertEquals(HttpStatus.OK.value(), response4.getStatusCodeValue());

        // Check robot in station details
        ResponseEntity<String> response5 = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response5.getStatusCodeValue());
        try {
            BrokerIdleStationSummaryOutputDto station = objectMapper.readValue(response5.getBody(), BrokerIdleStationSummaryOutputDto.class);
            assertNotNull(station);
            BrokerIdleStationInputDto stationCreated = createStationRequest();
            BrokerIdleStationInputDto stationUpdated = createUpdateStationRequest();
            stationCreated = mergeRequests(stationCreated, stationUpdated);
            assertEquals(stationCreated.getLatitude(), station.getCoordinates().getValue()[1]);
            assertEquals(stationCreated.getLongitude(), station.getCoordinates().getValue()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getAngle().getValue());
            assertEquals(response.getBody().getOrionId(), station.getRobotId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // Delete robot
        ResponseEntity<Object> response6 = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + response.getBody().getOrionId(),
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response6.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteStation() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteNonexistentWarehouse() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + IDLE_STATION_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(8)
    public void testCheckZeroStationsAfterDeleteInOrion() {
        testCheckZeroIdleStationsInOrion();
    }

    @Test
    @Order(8)
    public void testCheckZeroStationsAfterDeleteInApi() {
        testCheckZeroIdleStationsInApi();
    }

    private BrokerIdleStationInputDto createStationRequest() {
        BrokerIdleStationInputDto input = new BrokerIdleStationInputDto();
        input.setLatitude(41.165013593);
        input.setLongitude(-8.606473804);
        input.setName("idle station from tests");
        input.setStatus("idle");
        input.setAngle(1.870796310);
        return input;
    }

    private BrokerIdleStationInputDto createUpdateStationRequest() {
        BrokerIdleStationInputDto input = new BrokerIdleStationInputDto();
        input.setStatus("out_of_service");
        input.setName("idle station2 from tests");
        return input;
    }

    private BrokerIdleStationInputDto mergeRequests(BrokerIdleStationInputDto stationCreated, BrokerIdleStationInputDto stationUpdated) {
        if (stationUpdated.getAngle() != null) {
            stationCreated.setAngle(stationUpdated.getAngle());
        }
        if (stationUpdated.getLatitude() != null) {
            stationCreated.setLatitude(stationUpdated.getLatitude());
        }
        if (stationUpdated.getLongitude() != null) {
            stationCreated.setLongitude(stationUpdated.getLongitude());
        }
        if (stationUpdated.getName() != null) {
            stationCreated.setName(stationUpdated.getName());
        }
        if (stationUpdated.getStatus() != null) {
            stationCreated.setStatus(stationUpdated.getStatus());
        }
        return stationCreated;
    }
}
