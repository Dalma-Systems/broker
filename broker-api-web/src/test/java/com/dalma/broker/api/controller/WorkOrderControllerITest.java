package com.dalma.broker.api.controller;

import com.dalma.broker.api.OrionTest;
import com.dalma.broker.contract.dto.station.work.BrokerWorkStationInputDto;
import com.dalma.broker.contract.dto.station.work.BrokerWorkStationOutputDto;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseInputDto;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrder;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderInputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderIntegrateOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderSummaryOutputDto;
import com.dalma.contract.dto.work.order.item.BrokerWorkOrderItemInputDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.EXTERNAL;
import static com.dalma.broker.contract.Paths.FILTER;
import static com.dalma.broker.contract.Paths.INTEGRATE;
import static com.dalma.broker.contract.Paths.MATERIAL;
import static com.dalma.broker.contract.Paths.STATION;
import static com.dalma.broker.contract.Paths.WAREHOUSE;
import static com.dalma.broker.contract.Paths.WORKORDER;
import static com.dalma.broker.contract.Paths.WORK_STATION;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkOrderControllerITest extends OrionTest {

    private static final String WORK_ORDER_CONTROLLER_PATH = BASE_PATH + WORKORDER;
    private static final String WAREHOUSE_CONTROLLER_PATH = BASE_PATH + WAREHOUSE;
    private static final String WORK_STATION_CONTROLLER_PATH = BASE_PATH + STATION + WORK_STATION;
    private static final String WAREHOUSE_MATERIAL_CONTROLLER_PATH = BASE_PATH + WAREHOUSE + MATERIAL;
    private String workOrderId;
    private String warehouseId;
    private String stationId;
    private String materialId;
    private final String workOrderErpId = "1234567890";

    @Test
    @Order(1)
    public void testCheckZeroWorkOrderInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + OrionConnector.QUERY_TYPE + EntityType.WORK_ORDER.getType();
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<WorkOrder> workOrders = objectMapper.readValue(response.getBody(),
                    new TypeReference<List<WorkOrder>>() {
                    });
            assertTrue(workOrders.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testCheckZeroWorkOrdersInApi() {
        ResponseEntity<Object> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH,
                HttpMethod.GET, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        String body = response.getBody().toString();
        try {
            List<BrokerWorkOrderSummaryOutputDto> workOrders = objectMapper.readValue(body,
                    new TypeReference<List<BrokerWorkOrderSummaryOutputDto>>() {
                    });
            assertTrue(workOrders.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testAddWarehouseAndMaterial() {
        BrokerWarehouseInputDto input = createWarehouseRequest();
        HttpEntity<BrokerWarehouseInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerWarehouseOutputDto> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH, HttpMethod.POST, request,
                BrokerWarehouseOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        warehouseId = response.getBody().getOrionId();

        BrokerWarehouseMaterialInputDto input2 = createWarehouseMaterialRequest();
        HttpEntity<BrokerWarehouseMaterialInputDto> request2 = new HttpEntity<>(input2);
        ResponseEntity<BrokerWarehouseMaterialOutputDto> response2 = restTemplate.exchange(
                HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + warehouseId + MATERIAL,
                HttpMethod.POST, request2, BrokerWarehouseMaterialOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response2.getStatusCodeValue());
        assertNotNull(response2.getBody().getOrionId());
        materialId = response2.getBody().getOrionId();
    }

    @Test
    @Order(1)
    public void testAddStation() {
        BrokerWorkStationInputDto input = createStationRequest(WorkStationControllerITest.erpId);
        HttpEntity<BrokerWorkStationInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerWorkStationOutputDto> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH, HttpMethod.POST, request,
                BrokerWorkStationOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        stationId = response.getBody().getOrionId();
    }

    @Test
    @Order(2)
    public void testAddWorkOrder() {
        BrokerWorkOrderInputDto input = createWorkOrderRequest();
        HttpEntity<BrokerWorkOrderInputDto> request = new HttpEntity<>(input);
        ResponseEntity<BrokerWorkOrderOutputDto> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH, HttpMethod.POST, request,
                BrokerWorkOrderOutputDto.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody().getOrionId());
        workOrderId = response.getBody().getOrionId();
    }

    @Test
    @Order(3)
    public void testCheckWorkOrderInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + workOrderId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            WorkOrder workOrder = objectMapper.readValue(response.getBody(), WorkOrder.class);
            assertNotNull(workOrder);
            BrokerWorkOrderInputDto workOrderCreated = createWorkOrderRequest();
            assertEquals(Constant.ID + workOrderErpId, workOrder.getErpId().getValue());
            assertEquals(workOrderCreated.getScheduledAt(), workOrder.getScheduledAt().getValue());
            assertEquals(workOrderCreated.getStatus(), workOrder.getStatus().getValue());
            assertEquals(warehouseId, workOrder.getRefWarehouse().getValue());
            assertEquals(stationId, workOrder.getRefWorkstation().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckWorkOrdersInApi() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<BrokerWorkOrderSummaryOutputDto> workOrders = objectMapper.readValue(response.getBody(),
                    new TypeReference<List<BrokerWorkOrderSummaryOutputDto>>() {
                    });
            assertEquals(1, workOrders.size());
            BrokerWorkOrderInputDto workOrderCreated = createWorkOrderRequest();
            assertEquals(workOrderErpId, workOrders.get(0).getErpId().getValue());
            assertEquals(workOrderCreated.getScheduledAt(), workOrders.get(0).getScheduledAt().getValue());
            assertEquals(workOrderCreated.getStatus(), workOrders.get(0).getStatus().getValue());
            assertEquals(warehouseId, workOrders.get(0).getWarehouseId().getValue());
            assertEquals(stationId, workOrders.get(0).getWorkstationId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testCheckWorkOrderInApi() {
        ResponseEntity<String> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + workOrderId, HttpMethod.GET, null,
                String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWorkOrderSummaryOutputDto workOrder = objectMapper.readValue(response.getBody(),
                    BrokerWorkOrderSummaryOutputDto.class);
            assertNotNull(workOrder);
            BrokerWorkOrderInputDto workOrderCreated = createWorkOrderRequest();
            assertEquals(workOrderErpId, workOrder.getErpId().getValue());
            assertEquals(workOrderCreated.getScheduledAt(), workOrder.getScheduledAt().getValue());
            assertEquals(workOrderCreated.getStatus(), workOrder.getStatus().getValue());
            assertEquals(warehouseId, workOrder.getWarehouseId().getValue());
            assertEquals(stationId, workOrder.getWorkstationId().getValue());
            assertEquals(1, workOrder.getMaterials().size());
            assertEquals(materialId, workOrder.getMaterials().get(0).getMaterialId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testRetrieveNonexistentWorkOrderInApi() {
        ResponseEntity<String> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY, HttpMethod.GET, null,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateWorkOrder() {
        BrokerWorkOrderInputDto input = createUpdateWorkOrderRequest();
        HttpEntity<BrokerWorkOrderInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + workOrderId, HttpMethod.PATCH,
                request, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testUpdateNonexistentWorkOrder() {
        BrokerWorkOrderInputDto input = createUpdateWorkOrderRequest();
        HttpEntity<BrokerWorkOrderInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY, HttpMethod.PATCH,
                request, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(4)
    public void testInvalidUpdateWorkOrder() {
        BrokerWorkOrderInputDto input = createUpdateWorkOrderRequest();
        input.setMaterials(Collections.emptyList());
        HttpEntity<BrokerWorkOrderInputDto> request = new HttpEntity<>(input);
        ResponseEntity<Object> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY, HttpMethod.PATCH,
                request, Object.class);
        assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(5)
    public void testCheckUpdatedWorkOrderInOrion() {
        String address = orion + OrionConnector.ENTITIES_PATH + Constant.SLASH + workOrderId;
        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            WorkOrder workOrder = objectMapper.readValue(response.getBody(), WorkOrder.class);
            assertNotNull(workOrder);
            BrokerWorkOrderInputDto workOrderCreated = createWorkOrderRequest();
            BrokerWorkOrderInputDto workOrderUpdated = createUpdateWorkOrderRequest();
            workOrderCreated = mergeRequests(workOrderCreated, workOrderUpdated);
            assertEquals(Constant.ID + workOrderErpId, workOrder.getErpId().getValue());
            assertEquals(workOrderCreated.getScheduledAt(), workOrder.getScheduledAt().getValue());
            assertEquals(workOrderCreated.getStatus(), workOrder.getStatus().getValue());
            assertEquals(warehouseId, workOrder.getRefWarehouse().getValue());
            assertEquals(stationId, workOrder.getRefWorkstation().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testCheckUpdatedWorkOrderInApi() {
        ResponseEntity<String> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + workOrderId, HttpMethod.GET, null,
                String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWorkOrderSummaryOutputDto workOrder = objectMapper.readValue(response.getBody(),
                    BrokerWorkOrderSummaryOutputDto.class);
            assertNotNull(workOrder);
            BrokerWorkOrderInputDto workOrderCreated = createWorkOrderRequest();
            BrokerWorkOrderInputDto workOrderUpdated = createUpdateWorkOrderRequest();
            workOrderCreated = mergeRequests(workOrderCreated, workOrderUpdated);
            assertEquals(workOrderErpId, workOrder.getErpId().getValue());
            assertEquals(workOrderCreated.getScheduledAt(), workOrder.getScheduledAt().getValue());
            assertEquals(workOrderCreated.getStatus(), workOrder.getStatus().getValue());
            assertEquals(warehouseId, workOrder.getWarehouseId().getValue());
            assertEquals(stationId, workOrder.getWorkstationId().getValue());
            assertEquals(1, workOrder.getMaterials().size());
            assertEquals(materialId, workOrder.getMaterials().get(0).getMaterialId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testRetrieveWorkOrderByExternalId() {
        BrokerWorkOrderInputDto workOrderCreated = createWorkOrderRequest();
        BrokerWorkOrderInputDto workOrderUpdated = createUpdateWorkOrderRequest();
        workOrderCreated = mergeRequests(workOrderCreated, workOrderUpdated);
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH
                + EXTERNAL + Constant.SLASH + workOrderErpId + Constant.QUERY + "scheduledAt" + Constant.EQUAL + 
                workOrderCreated.getScheduledAt(), HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            BrokerWorkOrderSummaryOutputDto workOrder = objectMapper.readValue(response.getBody(),
                    BrokerWorkOrderSummaryOutputDto.class);
            assertNotNull(workOrder);
            assertEquals(workOrderErpId, workOrder.getErpId().getValue());
            assertEquals(workOrderCreated.getScheduledAt(), workOrder.getScheduledAt().getValue());
            assertEquals(workOrderCreated.getStatus(), workOrder.getStatus().getValue());
            assertEquals(warehouseId, workOrder.getWarehouseId().getValue());
            assertEquals(stationId, workOrder.getWorkstationId().getValue());
            assertEquals(1, workOrder.getMaterials().size());
            assertEquals(materialId, workOrder.getMaterials().get(0).getMaterialId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testRetrieveWorkOrderByUnknownExternalId() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH
                + Constant.SLASH + EXTERNAL + Constant.SLASH + FAKE_ENTITY, HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(5)
    public void testRetrieveWorkOrderFilteredInvalidDates() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH
                        + Constant.SLASH + FILTER + Constant.QUERY + Constant.START + Constant.EQUAL + "2000-09-24T16:20:00"
                        + Constant.AND + Constant.END + Constant.EQUAL + "2000-09-24T17:20:00", HttpMethod.GET, null,
                String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<WorkOrder> workOrders = objectMapper.readValue(response.getBody(),
                    new TypeReference<List<WorkOrder>>() {
                    });
            assertTrue(workOrders.isEmpty());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testRetrieveWorkOrderFilteredValidDates() {
        ResponseEntity<String> response = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH
                        + Constant.SLASH + FILTER + Constant.QUERY + Constant.START + Constant.EQUAL + "2020-09-25T16:20:00"
                        + Constant.AND + Constant.END + Constant.EQUAL + "2020-09-25T16:30:00", HttpMethod.GET, null,
                String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        try {
            List<BrokerWorkOrderSummaryOutputDto> workOrders = objectMapper.readValue(response.getBody(),
                    new TypeReference<List<BrokerWorkOrderSummaryOutputDto>>() {
                    });
            assertEquals(1, workOrders.size());
            BrokerWorkOrderInputDto workOrderCreated = createWorkOrderRequest();
            BrokerWorkOrderInputDto workOrderUpdated = createUpdateWorkOrderRequest();
            workOrderCreated = mergeRequests(workOrderCreated, workOrderUpdated);
            assertEquals(workOrderErpId, workOrders.get(0).getErpId().getValue());
            assertEquals(workOrderCreated.getScheduledAt(), workOrders.get(0).getScheduledAt().getValue());
            assertEquals(workOrderCreated.getStatus(), workOrders.get(0).getStatus().getValue());
            assertEquals(warehouseId, workOrders.get(0).getWarehouseId().getValue());
            assertEquals(stationId, workOrders.get(0).getWorkstationId().getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(6)
    public void testIntegrateWorkOrder() throws JsonProcessingException {
    	List<BrokerWorkOrderInputDto> input = new ArrayList<>(1);
        BrokerWorkOrderInputDto orderInput = createWorkOrderRequest();
        String newErp = orderInput.getErpId() + 1;
        orderInput.setErpId(newErp);
        input.add(orderInput);

        // Integrate new order
        HttpEntity<List<BrokerWorkOrderInputDto>> request = new HttpEntity<>(input);
        ResponseEntity<String> responseObj = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + INTEGRATE, HttpMethod.POST, request,
                String.class);
        assertEquals(HttpStatus.OK.value(), responseObj.getStatusCodeValue());
        List<BrokerWorkOrderIntegrateOutputDto> responseList = objectMapper.readValue(responseObj.getBody(), new TypeReference<List<BrokerWorkOrderIntegrateOutputDto>>() {
        });
        BrokerWorkOrderIntegrateOutputDto response = responseList.get(0);
        assertNotNull(response.getOrionId());

        // Get all orders, now there are 2
        ResponseEntity<String> response2 = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH,
                HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK.value(), response2.getStatusCodeValue());
        try {
            List<BrokerWorkOrderSummaryOutputDto> workOrders = objectMapper.readValue(response2.getBody(),
                    new TypeReference<List<BrokerWorkOrderSummaryOutputDto>>() {
                    });
            assertEquals(2, workOrders.size());

            String newWorkOrderId = null;
            // Confirm that the new order is present
            boolean workOrderFound = false;
            for (BrokerWorkOrderSummaryOutputDto order : workOrders) {
                if (newErp.equals(order.getErpId().getValue())) {
                    workOrderFound = true;
                    assertEquals(orderInput.getScheduledAt(), order.getScheduledAt().getValue());
                    assertEquals(orderInput.getStatus(), order.getStatus().getValue());
                    assertEquals(orderInput.getWarehouseId(), order.getWarehouseId().getValue());
                    assertEquals(orderInput.getWorkingStationId(), order.getWorkstationId().getValue());
                    assertEquals(orderInput.getErpId(), order.getErpId().getValue());
                    newWorkOrderId = order.getId();
                    break;
                }
            }
            assertNotNull(newWorkOrderId);
            assertTrue(workOrderFound);

            // Integrate again the the new order
            orderInput.getMaterials().get(0).setQuantity(2.0);
            input.set(0, orderInput);
            HttpEntity<List<BrokerWorkOrderInputDto>> request3 = new HttpEntity<>(input);
            ResponseEntity<String> response3 = restTemplate.exchange(
                    HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + INTEGRATE, HttpMethod.POST, request3,
                    String.class);
            assertEquals(HttpStatus.OK.value(), response3.getStatusCodeValue());
            responseList = objectMapper.readValue(response3.getBody(), new TypeReference<List<BrokerWorkOrderIntegrateOutputDto>>() {
            });
            assertNotNull(responseList.get(0).getOrionId());

            // Get all orders
            ResponseEntity<String> response4 = restTemplate.exchange(HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH,
                    HttpMethod.GET, null, String.class);
            assertEquals(HttpStatus.OK.value(), response4.getStatusCodeValue());
            List<BrokerWorkOrderSummaryOutputDto> workOrders2 = objectMapper.readValue(response4.getBody(),
                    new TypeReference<List<BrokerWorkOrderSummaryOutputDto>>() {
                    });

            // Ensure that the update worked because the orders returned are the same before update
            assertEquals(workOrders.size(), workOrders2.size());
            int sizeExpected = 2;
            assertEquals(sizeExpected, workOrders2.size());
            for (int i = 0; i < sizeExpected; i++) {
                assertEquals(workOrders.get(i).getId(), workOrders2.get(i).getId());
                assertEquals(workOrders.get(i).getErpId().getValue(), workOrders2.get(i).getErpId().getValue());
                assertEquals(workOrders.get(i).getScheduledAt().getValue(), workOrders2.get(i).getScheduledAt().getValue());
                assertEquals(workOrders.get(i).getStatus().getValue(), workOrders2.get(i).getStatus().getValue());
                assertEquals(workOrders.get(i).getWarehouseId().getValue(), workOrders2.get(i).getWarehouseId().getValue());
                assertEquals(workOrders.get(i).getWorkstationId().getValue(), workOrders2.get(i).getWorkstationId().getValue());
            }

            // Delete new order
            ResponseEntity<Object> response5 = restTemplate.exchange(
                    HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + newWorkOrderId, HttpMethod.DELETE,
                    null, Object.class);
            assertEquals(HttpStatus.OK.value(), response5.getStatusCodeValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(7)
    public void testDeleteWorkOrder() {
        ResponseEntity<Object> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + workOrderId, HttpMethod.DELETE,
                null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void testDeleteNonexistentWorkOrder() {
        ResponseEntity<Object> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_ORDER_CONTROLLER_PATH + Constant.SLASH + FAKE_ENTITY, HttpMethod.DELETE,
                null, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @Order(8)
    public void testCheckZeroWorkOrdersAfterDeleteInOrion() {
        testCheckZeroWorkOrderInOrion();
    }

    @Test
    @Order(8)
    public void testCheckZeroWorkOrdersAfterDeleteInApi() {
        testCheckZeroWorkOrdersInApi();
    }

    @Test
    @Order(8)
    public void testDeleteWarehouseAndStationAndMaterials() {
        ResponseEntity<Object> response = restTemplate.exchange(
                HTTP_LOCALHOST + port + WAREHOUSE_MATERIAL_CONTROLLER_PATH + Constant.SLASH + materialId,
                HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        ResponseEntity<Object> response2 = restTemplate.exchange(
                HTTP_LOCALHOST + port + WAREHOUSE_CONTROLLER_PATH + Constant.SLASH + warehouseId, HttpMethod.DELETE,
                null, Object.class);
        assertEquals(HttpStatus.OK.value(), response2.getStatusCodeValue());

        ResponseEntity<Object> response3 = restTemplate.exchange(
                HTTP_LOCALHOST + port + WORK_STATION_CONTROLLER_PATH + Constant.SLASH + stationId, HttpMethod.DELETE,
                null, Object.class);
        assertEquals(HttpStatus.OK.value(), response3.getStatusCodeValue());
    }

    private BrokerWorkOrderInputDto createWorkOrderRequest() {
        BrokerWorkOrderInputDto input = new BrokerWorkOrderInputDto();
        input.setErpId(workOrderErpId);
        BrokerWorkOrderItemInputDto material = new BrokerWorkOrderItemInputDto();
        material.setId(materialId);
        material.setQuantity(1.0);
        input.setMaterials(List.of(material));
        input.setScheduledAt("2020-09-25T16:20:00.000Z");
        input.setWarehouseId(warehouseId);
        input.setWorkingStationId(stationId);
        input.setStatus("scheduled");
        return input;
    }

    private BrokerWorkOrderInputDto createUpdateWorkOrderRequest() {
        BrokerWorkOrderInputDto input = new BrokerWorkOrderInputDto();
        input.setScheduledAt("2020-09-25T16:21:00.000Z");
        return input;
    }

    private BrokerWorkOrderInputDto mergeRequests(BrokerWorkOrderInputDto workOrderCreated,
                                                  BrokerWorkOrderInputDto workOrderUpdated) {
        if (workOrderUpdated.getErpId() != null) {
            workOrderCreated.setErpId(workOrderUpdated.getErpId());
        }
        if (workOrderUpdated.getMaterials() != null) {
            workOrderCreated.setMaterials(workOrderUpdated.getMaterials());
        }
        if (workOrderUpdated.getScheduledAt() != null) {
            workOrderCreated.setScheduledAt(workOrderUpdated.getScheduledAt());
        }
        if (workOrderUpdated.getWarehouseId() != null) {
            workOrderCreated.setWarehouseId(workOrderUpdated.getWarehouseId());
        }
        if (workOrderUpdated.getWorkingStationId() != null) {
            workOrderCreated.setWorkingStationId(workOrderUpdated.getWorkingStationId());
        }
        if (workOrderUpdated.getStatus() != null) {
            workOrderCreated.setStatus(workOrderUpdated.getStatus());
        }
        return workOrderCreated;
    }
}
