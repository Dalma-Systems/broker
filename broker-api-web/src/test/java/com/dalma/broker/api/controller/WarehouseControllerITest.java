package com.dalma.broker.api.controller;

import com.dalma.broker.api.OrionTest;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseInputDto;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.Warehouse;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotSummaryOutputDto;
import com.dalma.contract.dto.warehouse.BrokerWarehouseSummaryOutputDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.EXTERNAL;
import static com.dalma.broker.contract.Paths.ROBOT;
import static com.dalma.broker.contract.Paths.WAREHOUSE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WarehouseControllerITest extends OrionTest {

    private static final String ROBOT_CONTROLLER_PATH = BASE_PATH + ROBOT;
    private static final String WAREHOUSE_CONTROLLER_PATH = BASE_PATH + WAREHOUSE;
    private String warehouseId;
    private final String erpId = "0075";

    @Test
    @Order(1)
    public void testCheckZeroWarehousesInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + OrionConnector.QUERY_TYPE + EntityType.WAREHOUSE.getType();
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<Warehouse> warehouses = objectMapper.readValue(response.getBody(), new TypeReference<List<Warehouse>>() {
            });
            assertTrue(warehouses.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testCheckZeroWarehousesInApi() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH,
                HttpMethod.GET, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        String body = response.getBody().toString();
        try {
            List<BrokerWarehouseSummaryOutputDto> warehouses = objectMapper.readValue(body, new TypeReference<List<BrokerWarehouseSummaryOutputDto>>() {
            });
            assertTrue(warehouses.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testAddWarehouse() {
        BrokerWarehouseInputDto input = createWarehouseRequest();
        HttpEntity<BrokerWarehouseInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerWarehouseOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH,
                HttpMethod.POST, request, BrokerWarehouseOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        warehouseId = response.getBody().getOrionId();
    }

    @Test
    @Order(3)
    public void testCheckWarehouseInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + warehouseId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            Warehouse warehouse = objectMapper.readValue(response.getBody(), Warehouse.class);
            assertNotNull(warehouse);
            BrokerWarehouseInputDto warehouseCreated = createWarehouseRequest();
            assertEquals(Constant.ID + warehouseCreated.getErpId(), warehouse.getErpId().getValue());
            assertEquals(warehouseCreated.getLatitude(), warehouse.getLocation().getValue().getCoordinates()[1]);
            assertEquals(warehouseCreated.getLongitude(), warehouse.getLocation().getValue().getCoordinates()[0]);
            assertEquals(warehouseCreated.getName(), warehouse.getName().getValue());
            assertEquals(warehouseCreated.getStatus(), warehouse.getStatus().getValue());
            assertEquals(warehouseCreated.getAngle(), warehouse.getLocation().getMetadata().getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckWarehousesInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<BrokerWarehouseSummaryOutputDto> warehouses = objectMapper.readValue(response.getBody(), new TypeReference<List<BrokerWarehouseSummaryOutputDto>>() {
            });
            assertEquals(1, warehouses.size());
            BrokerWarehouseInputDto warehouseCreated = createWarehouseRequest();
            assertEquals(warehouseCreated.getErpId(), warehouses.get(0).getErpId().getValue());
            assertEquals(warehouseCreated.getLatitude(), warehouses.get(0).getCoordinates().getValue()[1]);
            assertEquals(warehouseCreated.getLongitude(), warehouses.get(0).getCoordinates().getValue()[0]);
            assertEquals(warehouseCreated.getName(), warehouses.get(0).getName().getValue());
            assertEquals(warehouseCreated.getStatus(), warehouses.get(0).getStatus().getValue());
            assertEquals(warehouseCreated.getAngle(), warehouses.get(0).getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckWarehouseInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + warehouseId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWarehouseSummaryOutputDto warehouse = objectMapper.readValue(response.getBody(), BrokerWarehouseSummaryOutputDto.class);
            assertNotNull(warehouse);
            BrokerWarehouseInputDto warehouseCreated = createWarehouseRequest();
            assertEquals(warehouseCreated.getErpId(), warehouse.getErpId().getValue());
            assertEquals(warehouseCreated.getLatitude(), warehouse.getCoordinates().getValue()[1]);
            assertEquals(warehouseCreated.getLongitude(), warehouse.getCoordinates().getValue()[0]);
            assertEquals(warehouseCreated.getName(), warehouse.getName().getValue());
            assertEquals(warehouseCreated.getStatus(), warehouse.getStatus().getValue());
            assertEquals(warehouseCreated.getAngle(), warehouse.getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testRetrieveNonexistentWarehouseInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateWarehouse() {
        BrokerWarehouseInputDto input = createUpdateWarehouseRequest();
        HttpEntity<BrokerWarehouseInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + warehouseId,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateNonexistentWarehouse() {
        BrokerWarehouseInputDto input = createUpdateWarehouseRequest();
        HttpEntity<BrokerWarehouseInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateWarehouseMaterials() {
        BrokerWarehouseInputDto input = createUpdateWarehouseRequest();
        input.setMaterials(Collections.emptyList());
        HttpEntity<BrokerWarehouseInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + warehouseId,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(5)
    public void testCheckUpdatedWarehouseInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + warehouseId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            Warehouse warehouse = objectMapper.readValue(response.getBody(), Warehouse.class);
            assertNotNull(warehouse);
            BrokerWarehouseInputDto warehouseCreated = createWarehouseRequest();
            BrokerWarehouseInputDto warehouseUpdated = createUpdateWarehouseRequest();
            warehouseCreated = mergeRequests(warehouseCreated, warehouseUpdated);
            assertEquals(Constant.ID + warehouseCreated.getErpId(), warehouse.getErpId().getValue());
            assertEquals(warehouseCreated.getLatitude(), warehouse.getLocation().getValue().getCoordinates()[1]);
            assertEquals(warehouseCreated.getLongitude(), warehouse.getLocation().getValue().getCoordinates()[0]);
            assertEquals(warehouseCreated.getName(), warehouse.getName().getValue());
            assertEquals(warehouseCreated.getStatus(), warehouse.getStatus().getValue());
            assertEquals(warehouseCreated.getAngle(), warehouse.getLocation().getMetadata().getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testCheckUpdatedWarehouseInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + warehouseId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWarehouseSummaryOutputDto warehouse = objectMapper.readValue(response.getBody(), BrokerWarehouseSummaryOutputDto.class);
            assertNotNull(warehouse);
            BrokerWarehouseInputDto warehouseCreated = createWarehouseRequest();
            BrokerWarehouseInputDto warehouseUpdated = createUpdateWarehouseRequest();
            warehouseCreated = mergeRequests(warehouseCreated, warehouseUpdated);
            assertEquals(warehouseCreated.getErpId(), warehouse.getErpId().getValue());
            assertEquals(warehouseCreated.getLatitude(), warehouse.getCoordinates().getValue()[1]);
            assertEquals(warehouseCreated.getLongitude(), warehouse.getCoordinates().getValue()[0]);
            assertEquals(warehouseCreated.getName(), warehouse.getName().getValue());
            assertEquals(warehouseCreated.getStatus(), warehouse.getStatus().getValue());
            assertEquals(warehouseCreated.getAngle(), warehouse.getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testRetrieveWarehouseByExternalId() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + EXTERNAL + Constant.SLASH + erpId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWarehouseSummaryOutputDto warehouse = objectMapper.readValue(response.getBody(), BrokerWarehouseSummaryOutputDto.class);
            assertNotNull(warehouse);
            BrokerWarehouseInputDto warehouseCreated = createWarehouseRequest();
            BrokerWarehouseInputDto warehouseUpdated = createUpdateWarehouseRequest();
            warehouseCreated = mergeRequests(warehouseCreated, warehouseUpdated);
            assertEquals(warehouseCreated.getErpId(), warehouse.getErpId().getValue());
            assertEquals(warehouseCreated.getLatitude(), warehouse.getCoordinates().getValue()[1]);
            assertEquals(warehouseCreated.getLongitude(), warehouse.getCoordinates().getValue()[0]);
            assertEquals(warehouseCreated.getName(), warehouse.getName().getValue());
            assertEquals(warehouseCreated.getStatus(), warehouse.getStatus().getValue());
            assertEquals(warehouseCreated.getAngle(), warehouse.getAngle().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testRetrieveWarehouseByNonexistentExternalId() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + EXTERNAL + FAKE_ENTITY,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(6)
    public void testAddWarehouseToRobotRelation() {
        // Add robot
        BrokerRobotInputDto input = createRobotRequest();
        HttpEntity<BrokerRobotInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerRobotOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH,
                HttpMethod.POST, request, BrokerRobotOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        assertEquals(EntityType.ROBOT.getType(), response.getBody().getType());

        // Update robot with warehouse as relation
        BrokerRobotInputDto input2 = createUpdateRobotRequest();
        input2.setDestination(warehouseId);
        HttpEntity<BrokerRobotInputDto> request2 = new HttpEntity<>(input2);
        ResponseEntity<Object> response2 = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + response.getBody().getOrionId(),
                HttpMethod.PATCH, request2, Object.class);
        assertEquals(HttpStatus.OK.value(), response2.getStatusCodeValue());

        // Check warehouse in robot details
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
            assertEquals(warehouseId, robot.getDestination().getValue());
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
    public void testDeleteWarehouse() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + warehouseId,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteNonexistentWarehouse() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(8)
    public void testCheckZeroWarehousesAfterDeleteInOrion() {
        testCheckZeroWarehousesInOrion();
    }

    @Test
    @Order(8)
    public void testCheckZeroWarehousesAfterDeleteInApi() {
        testCheckZeroWarehousesInApi();
    }

    private BrokerWarehouseInputDto createUpdateWarehouseRequest() {
        BrokerWarehouseInputDto input = new BrokerWarehouseInputDto();
        input.setLatitude(40.70885);
        input.setLongitude(-8.4906426);
        input.setAngle(1.670796320);
        return input;
    }

    private BrokerWarehouseInputDto mergeRequests(BrokerWarehouseInputDto warehouseCreated, BrokerWarehouseInputDto warehouseUpdated) {
        if (warehouseUpdated.getErpId() != null) {
            warehouseCreated.setErpId(warehouseUpdated.getErpId());
        }
        if (warehouseUpdated.getLatitude() != null) {
            warehouseCreated.setLatitude(warehouseUpdated.getLatitude());
        }
        if (warehouseUpdated.getLongitude() != null) {
            warehouseCreated.setLongitude(warehouseUpdated.getLongitude());
        }
        if (warehouseUpdated.getName() != null) {
            warehouseCreated.setName(warehouseUpdated.getName());
        }
        if (warehouseUpdated.getStatus() != null) {
            warehouseCreated.setStatus(warehouseUpdated.getStatus());
        }
        if (warehouseUpdated.getAngle() != null) {
            warehouseCreated.setAngle(warehouseUpdated.getAngle());
        }
        return warehouseCreated;
    }
}
