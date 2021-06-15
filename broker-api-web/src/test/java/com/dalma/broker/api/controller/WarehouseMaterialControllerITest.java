package com.dalma.broker.api.controller;

import com.dalma.broker.api.OrionTest;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseInputDto;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseOutputDto;
import com.dalma.broker.contract.dto.warehouse.material.BrokerWarehouseMaterialSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterial;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotOutputDto;
import com.dalma.contract.dto.warehouse.BrokerWarehouseSummaryOutputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialOutputDto;
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
import static com.dalma.broker.contract.Paths.MATERIAL;
import static com.dalma.broker.contract.Paths.ROBOT;
import static com.dalma.broker.contract.Paths.WAREHOUSE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WarehouseMaterialControllerITest extends OrionTest {

    private static final String WAREHOUSE_CONTROLLER_PATH = BASE_PATH + WAREHOUSE + Constant.SLASH;
    private static final String WAREHOUSE_MATERIAL_CONTROLLER_PATH = BASE_PATH + WAREHOUSE + MATERIAL;
    private static final String ROBOT_CONTROLLER_PATH = BASE_PATH + ROBOT;
    private String materialId;
    private String warehouseId;

    @Test
    @Order(1)
    public void testCheckZeroWarehouseMaterialsInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + OrionConnector.QUERY_TYPE + EntityType.WAREHOUSE_MATERIAL.getType();
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<WarehouseMaterial> materials = objectMapper.readValue(response.getBody(), new TypeReference<List<WarehouseMaterial>>() {
            });
            assertTrue(materials.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testCheckZeroWarehouseMaterialsInApi() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH,
                HttpMethod.GET, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        String body = response.getBody().toString();
        try {
            List<BrokerWarehouseMaterialSummaryOutputDto> materials = objectMapper.readValue(body, new TypeReference<List<BrokerWarehouseMaterialSummaryOutputDto>>() {
            });
            assertTrue(materials.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(1)
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
    @Order(2)
    public void testAddWarehouseMaterial() {
        BrokerWarehouseMaterialInputDto input = createWarehouseMaterialRequest();
        HttpEntity<BrokerWarehouseMaterialInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerWarehouseMaterialOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + warehouseId + MATERIAL,
                HttpMethod.POST, request, BrokerWarehouseMaterialOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        materialId = response.getBody().getOrionId();
    }

    @Test
    @Order(2)
    public void testAddWarehouseMaterialUnexistentWarehouse() {
        BrokerWarehouseMaterialInputDto input = createWarehouseMaterialRequest();
        HttpEntity<BrokerWarehouseMaterialInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerWarehouseMaterialOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + 0 + MATERIAL,
                HttpMethod.POST, request, BrokerWarehouseMaterialOutputDto.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(3)
    public void testCheckWarehouseMaterialInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + materialId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            WarehouseMaterial material = objectMapper.readValue(response.getBody(), WarehouseMaterial.class);
            assertNotNull(material);
            BrokerWarehouseMaterialInputDto materialCreated = createWarehouseMaterialRequest();
            assertEquals(materialCreated.getErpId(), material.getErpId().getValue());
            assertEquals(materialCreated.getBatch(), material.getBatch().getValue());
            assertEquals(materialCreated.getMaterial(), response.getBody().split("mType")[1].split("value")[1].split("\"")[2]);
            assertEquals(materialCreated.getQuantity(), material.getQuantity().getValue());
            assertEquals(materialCreated.getUnit(), material.getUnit().getValue());
            assertEquals(warehouseId, material.getRefWarehouse().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckWarehouseMaterialsInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<BrokerWarehouseMaterialSummaryOutputDto> materials = objectMapper.readValue(response.getBody(), new TypeReference<List<BrokerWarehouseMaterialSummaryOutputDto>>() {
            });
            assertEquals(1, materials.size());
            BrokerWarehouseMaterialInputDto materialCreated = createWarehouseMaterialRequest();
            assertEquals(materialCreated.getErpId(), materials.get(0).getErpId().getValue());
            assertEquals(materialCreated.getBatch(), materials.get(0).getBatch().getValue());
            assertEquals(materialCreated.getMaterial(), materials.get(0).getType().getValue());
            assertEquals(materialCreated.getQuantity(), materials.get(0).getQuantity().getValue());
            assertEquals(materialCreated.getUnit(), materials.get(0).getUnit().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckWarehouseMaterialInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH + Constant.SLASH + materialId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWarehouseMaterialSummaryOutputDto material = objectMapper.readValue(response.getBody(), BrokerWarehouseMaterialSummaryOutputDto.class);
            assertNotNull(material);
            BrokerWarehouseMaterialInputDto materialCreated = createWarehouseMaterialRequest();
            assertEquals(materialCreated.getErpId(), material.getErpId().getValue());
            assertEquals(materialCreated.getBatch(), material.getBatch().getValue());
            assertEquals(materialCreated.getMaterial(), material.getType().getValue());
            assertEquals(materialCreated.getQuantity(), material.getQuantity().getValue());
            assertEquals(materialCreated.getUnit(), material.getUnit().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckMaterialInWarehouseApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + warehouseId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWarehouseSummaryOutputDto warehouse = objectMapper.readValue(response.getBody(), BrokerWarehouseSummaryOutputDto.class);
            assertNotNull(warehouse);
            assertNotNull(warehouse.getMaterials());
            assertEquals(1, warehouse.getMaterials().size());
            assertEquals(materialId, warehouse.getMaterials().get(0));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testRetrieveNonexistentWarehouseMaterialInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateWarehouseMaterial() {
        BrokerWarehouseMaterialInputDto input = createUpdateWarehouseMaterialRequest();
        HttpEntity<BrokerWarehouseMaterialInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH + Constant.SLASH + materialId,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateNonexistentWarehouseMaterial() {
        BrokerWarehouseMaterialInputDto input = createUpdateWarehouseMaterialRequest();
        HttpEntity<BrokerWarehouseMaterialInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.PATCH, request, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(5)
    public void testCheckUpdatedWarehouseMaterialInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + materialId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            WarehouseMaterial material = objectMapper.readValue(response.getBody(), WarehouseMaterial.class);
            assertNotNull(material);
            BrokerWarehouseMaterialInputDto materialCreated = createWarehouseMaterialRequest();
            BrokerWarehouseMaterialInputDto materialUpdated = createUpdateWarehouseMaterialRequest();
            materialCreated = mergeRequests(materialCreated, materialUpdated);
            assertEquals(materialCreated.getErpId(), material.getErpId().getValue());
            assertEquals(materialCreated.getBatch(), material.getBatch().getValue());
            assertEquals(materialCreated.getMaterial(), response.getBody().split("mType")[1].split("value")[1].split("\"")[2]);
            assertEquals(materialCreated.getQuantity(), material.getQuantity().getValue());
            assertEquals(materialCreated.getUnit(), material.getUnit().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testCheckUpdatedWarehouseMaterialInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH + Constant.SLASH + materialId,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWarehouseMaterialSummaryOutputDto material = objectMapper.readValue(response.getBody(), BrokerWarehouseMaterialSummaryOutputDto.class);
            assertNotNull(material);
            BrokerWarehouseMaterialInputDto materialCreated = createWarehouseMaterialRequest();
            BrokerWarehouseMaterialInputDto materialUpdated = createUpdateWarehouseMaterialRequest();
            materialCreated = mergeRequests(materialCreated, materialUpdated);
            assertEquals(materialCreated.getErpId(), material.getErpId().getValue());
            assertEquals(materialCreated.getBatch(), material.getBatch().getValue());
            assertEquals(materialCreated.getMaterial(), material.getType().getValue());
            assertEquals(materialCreated.getQuantity(), material.getQuantity().getValue());
            assertEquals(materialCreated.getUnit(), material.getUnit().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(6)
    public void testAddmaterialToRobotRelation() {
        // Add robot
        BrokerRobotInputDto input = createRobotRequest();
        HttpEntity<BrokerRobotInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerRobotOutputDto> response = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH,
                HttpMethod.POST, request, BrokerRobotOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        assertEquals(EntityType.ROBOT.getType(), response.getBody().getType());

        // Update robot with material as payload relation
        BrokerRobotInputDto input2 = createUpdateRobotRequest();
        List<String> materials = List.of(materialId);
        input2.setDestination(warehouseId);
        input2.setPayload(materials);
        HttpEntity<BrokerRobotInputDto> request2 = new HttpEntity<>(input2);
        ResponseEntity<Object> response2 = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + response.getBody().getOrionId(),
                HttpMethod.PATCH, request2, Object.class);
        assertEquals(HttpStatus.OK.value(), response2.getStatusCodeValue());

        // Delete robot
        ResponseEntity<Object> response4 = restTemplate.exchange(HTTP_LOCALHOST + port + ROBOT_CONTROLLER_PATH + Constant.SLASH + response.getBody().getOrionId(),
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response4.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteWarehouseMaterial() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH + Constant.SLASH + materialId,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteNonexistentWarehouseMaterial() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(8)
    public void testCheckZeroWarehouseMaterialsAfterDeleteInOrion() {
        testCheckZeroWarehouseMaterialsInOrion();
    }

    @Test
    @Order(8)
    public void testCheckZeroWarehouseMaterialsAfterDeleteInApi() {
        testCheckZeroWarehouseMaterialsInApi();
    }

    private BrokerWarehouseMaterialInputDto createUpdateWarehouseMaterialRequest() {
        BrokerWarehouseMaterialInputDto input = new BrokerWarehouseMaterialInputDto();
        input.setQuantity(15.2);
        input.setMaterial("hood type2");
        return input;
    }

    private BrokerWarehouseMaterialInputDto mergeRequests(BrokerWarehouseMaterialInputDto warehouseMaterialCreated, BrokerWarehouseMaterialInputDto warehouseMaterialUpdated) {
        if (warehouseMaterialUpdated.getBatch() != null) {
            warehouseMaterialCreated.setBatch(warehouseMaterialUpdated.getBatch());
        }
        if (warehouseMaterialUpdated.getErpId() != null) {
            warehouseMaterialCreated.setErpId(warehouseMaterialUpdated.getErpId());
        }
        if (warehouseMaterialUpdated.getMaterial() != null) {
            warehouseMaterialCreated.setMaterial(warehouseMaterialUpdated.getMaterial());
        }
        if (warehouseMaterialUpdated.getQuantity() != null) {
            warehouseMaterialCreated.setQuantity(warehouseMaterialUpdated.getQuantity());
        }
        if (warehouseMaterialUpdated.getUnit() != null) {
            warehouseMaterialCreated.setUnit(warehouseMaterialUpdated.getUnit());
        }
        return warehouseMaterialCreated;
    }
}
