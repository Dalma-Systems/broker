package com.dalma.broker.api.controller;

import com.dalma.broker.api.OrionTest;
import com.dalma.broker.contract.dto.station.work.BrokerWorkStationInputDto;
import com.dalma.broker.contract.dto.station.work.BrokerWorkStationOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.entity.station.work.WorkStation;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotSummaryOutputDto;
import com.dalma.contract.dto.station.work.BrokerWorkStationSummaryOutputDto;
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
import static com.dalma.broker.contract.Paths.EXTERNAL;
import static com.dalma.broker.contract.Paths.ROBOT;
import static com.dalma.broker.contract.Paths.STATION;
import static com.dalma.broker.contract.Paths.WORK_STATION;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkStationControllerITest extends OrionTest {

    private static final String ROBOT_CONTROLLER_PATH = BASE_PATH + ROBOT;
    private static final String WORK_STATION_CONTROLLER_PATH = BASE_PATH + STATION + WORK_STATION;
    private String stationId;
    protected static final String erpId = "0161";

    @Test
    @Order(1)
    public void testCheckZeroWorkStationsInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + OrionConnector.QUERY_TYPE + EntityType.WORK_STATION.getType();
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<WorkStation> stations = objectMapper.readValue(response.getBody(), new TypeReference<List<WorkStation>>() {
            });
            assertTrue(stations.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testCheckZeroWorkStationsInApi() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH,
                HttpMethod.GET, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        String body = response.getBody().toString();
        try {
            List<BrokerWorkStationSummaryOutputDto> stations = objectMapper.readValue(body, new TypeReference<List<BrokerWorkStationSummaryOutputDto>>() {
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
        BrokerWorkStationInputDto input = createStationRequest(erpId);
        HttpEntity<BrokerWorkStationInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerWorkStationOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH,
                HttpMethod.POST, request, BrokerWorkStationOutputDto.class);
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
            WorkStation station = objectMapper.readValue(response.getBody(), WorkStation.class);
            assertNotNull(station);
            BrokerWorkStationInputDto stationCreated = createStationRequest(erpId);
            assertEquals(stationCreated.getLatitude(), station.getLocation().getValue().getCoordinates()[1]);
            assertEquals(stationCreated.getLongitude(), station.getLocation().getValue().getCoordinates()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getLocation().getMetadata().getAngle().getValue());
            assertEquals(Constant.ID + erpId, station.getErpId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckStationsInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<BrokerWorkStationSummaryOutputDto> stations = objectMapper.readValue(response.getBody(), new TypeReference<List<BrokerWorkStationSummaryOutputDto>>() {
            });
            assertEquals(1, stations.size());
            BrokerWorkStationInputDto stationCreated = createStationRequest(erpId);
            assertEquals(stationCreated.getLatitude(), stations.get(0).getCoordinates().getValue()[1]);
            assertEquals(stationCreated.getLongitude(), stations.get(0).getCoordinates().getValue()[0]);
            assertEquals(stationCreated.getName(), stations.get(0).getName().getValue());
            assertEquals(stationCreated.getStatus(), stations.get(0).getStatus().getValue());
            assertEquals(stationCreated.getAngle(), stations.get(0).getAngle().getValue());
            assertEquals(erpId, stations.get(0).getErpId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckStationInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWorkStationSummaryOutputDto station = objectMapper.readValue(response.getBody(), BrokerWorkStationSummaryOutputDto.class);
            assertNotNull(station);
            BrokerWorkStationInputDto stationCreated = createStationRequest(erpId);
            assertEquals(stationCreated.getLatitude(), station.getCoordinates().getValue()[1]);
            assertEquals(stationCreated.getLongitude(), station.getCoordinates().getValue()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getAngle().getValue());
            assertEquals(erpId, station.getErpId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testRetrieveNonexistentStationInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateStation() {
        BrokerWorkStationInputDto input = createUpdateStationRequest();
        HttpEntity<BrokerWorkStationInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateNonexistentStation() {
        BrokerWorkStationInputDto input = createUpdateStationRequest();
        HttpEntity<BrokerWorkStationInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
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
            WorkStation station = objectMapper.readValue(response.getBody(), WorkStation.class);
            assertNotNull(station);
            BrokerWorkStationInputDto stationCreated = createStationRequest(erpId);
            BrokerWorkStationInputDto stationUpdated = createUpdateStationRequest();
            stationCreated = mergeRequests(stationCreated, stationUpdated);
            assertEquals(stationCreated.getLatitude(), station.getLocation().getValue().getCoordinates()[1]);
            assertEquals(stationCreated.getLongitude(), station.getLocation().getValue().getCoordinates()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getLocation().getMetadata().getAngle().getValue());
            assertEquals(Constant.ID + erpId, station.getErpId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testCheckUpdatedStationInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWorkStationSummaryOutputDto station = objectMapper.readValue(response.getBody(), BrokerWorkStationSummaryOutputDto.class);
            assertNotNull(station);
            BrokerWorkStationInputDto stationCreated = createStationRequest(erpId);
            BrokerWorkStationInputDto stationUpdated = createUpdateStationRequest();
            stationCreated = mergeRequests(stationCreated, stationUpdated);
            assertEquals(stationCreated.getLatitude(), station.getCoordinates().getValue()[1]);
            assertEquals(stationCreated.getLongitude(), station.getCoordinates().getValue()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getAngle().getValue());
            assertEquals(erpId, station.getErpId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testRetrieveStationByExternalId() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + EXTERNAL + Constant.SLASH + erpId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWorkStationSummaryOutputDto station = objectMapper.readValue(response.getBody(), BrokerWorkStationSummaryOutputDto.class);
            assertNotNull(station);
            BrokerWorkStationInputDto stationCreated = createStationRequest(erpId);
            BrokerWorkStationInputDto stationUpdated = createUpdateStationRequest();
            stationCreated = mergeRequests(stationCreated, stationUpdated);
            assertEquals(stationCreated.getLatitude(), station.getCoordinates().getValue()[1]);
            assertEquals(stationCreated.getLongitude(), station.getCoordinates().getValue()[0]);
            assertEquals(stationCreated.getName(), station.getName().getValue());
            assertEquals(stationCreated.getStatus(), station.getStatus().getValue());
            assertEquals(stationCreated.getAngle(), station.getAngle().getValue());
            assertEquals(erpId, station.getErpId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testRetrieveWarehouseByNonexistentExternalId() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + EXTERNAL + FAKE_ENTITY,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(6)
    public void testAddStationToRobotRelation() {
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

        // Delete robot
        ResponseEntity<Object> response4 = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + response.getBody().getOrionId(),
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response4.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteStation() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + stationId,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteNonexistentWarehouse() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(8)
    public void testCheckZeroStationsAfterDeleteInOrion() {
        testCheckZeroWorkStationsInOrion();
    }

    @Test
    @Order(8)
    public void testCheckZeroStationsAfterDeleteInApi() {
        testCheckZeroWorkStationsInApi();
    }

    private BrokerWorkStationInputDto createUpdateStationRequest() {
        BrokerWorkStationInputDto input = new BrokerWorkStationInputDto();
        input.setStatus("out_of_service");
        input.setName("work station2 from tests");
        return input;
    }

    private BrokerWorkStationInputDto mergeRequests(BrokerWorkStationInputDto stationCreated, BrokerWorkStationInputDto stationUpdated) {
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
