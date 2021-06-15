package com.dalma.broker.service;

import com.dalma.broker.contract.dto.warehouse.material.BrokerWarehouseMaterialSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.OrionEntityType;
import com.dalma.broker.fiware.orion.connector.entity.common.OrionDateTime;
import com.dalma.broker.fiware.orion.connector.entity.robot.Robot;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrder;
import com.dalma.broker.fiware.orion.connector.entity.work.order.field.WorkOrderField;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItem;
import com.dalma.broker.service.error.WorkOrderExceptionError;
import com.dalma.broker.service.exception.workorder.WorkOrderNotFoundException;
import com.dalma.broker.service.publisher.WorkOrderPublisher;
import com.dalma.broker.service.reader.RobotReader;
import com.dalma.broker.service.reader.WorkOrderItemReader;
import com.dalma.broker.service.reader.WorkOrderReader;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.common.workorder.enums.WorkOrderStatus;
import com.dalma.contract.dto.base.BaseOrionAttributeOutputDto;
import com.dalma.contract.dto.station.work.BrokerWorkStationSummaryOutputDto;
import com.dalma.contract.dto.warehouse.BrokerWarehouseSummaryOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderInputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderIntegrateOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderMaterialsSummaryOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderSummaryOutputDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkOrderService extends
        BaseOrionCrudService<BrokerWorkOrderInputDto, BrokerWorkOrderOutputDto, BrokerWorkOrderSummaryOutputDto, WorkOrder, WorkOrderPublisher, WorkOrderReader> {

	private static final String LOG_MSG_INTEGRATED_AT = "Integrated {} at {}";
	private final DateFormat dateFormat = new SimpleDateFormat(Constant.ORION_DATE_FORMAT);
    private final WorkOrderPublisher publisher;
    private final WorkOrderReader reader;
    private final WorkOrderItemService workorderItemService;
    private final WorkOrderItemReader workorderItemReader;
    private final RobotReader robotReader;
    private final ModelMapper modelMapper;
    private final WarehouseMaterialService materialService;
    private final WarehouseService warehouseService;
    private final WorkStationService workStationService;

    public WorkOrderService(WorkOrderPublisher publisher, WorkOrderReader reader, //NOSONAR
                            WorkOrderItemService workorderItemService, ModelMapper modelMapper, WorkOrderItemReader workorderItemReader,
                            RobotReader robotReader, WarehouseMaterialService materialService, WarehouseService warehouseService, 
                            WorkStationService workStationService) {
        super(publisher, reader);
        this.publisher = publisher;
        this.reader = reader;
        this.workorderItemService = workorderItemService;
        this.modelMapper = modelMapper;
        this.workorderItemReader = workorderItemReader;
        this.robotReader = robotReader;
        this.materialService = materialService;
        this.warehouseService = warehouseService;
        this.workStationService = workStationService;
    }

    @Override
    protected String getType() {
        return EntityType.WORK_ORDER.getType();
    }

    @Override
    protected BrokerWorkOrderOutputDto mapOrionEntityToEntity(WorkOrder orionEntity) {
        return new BrokerWorkOrderOutputDto(orionEntity.getId());
    }

    @Override
    public BrokerWorkOrderOutputDto create(BrokerWorkOrderInputDto entity) {
        WorkOrder orionEntity = publisher.create(entity);

        // Associate workstation and warehouse
        publisher.append(orionEntity, List.of(entity.getWorkingStationId(), entity.getWarehouseId()));

        // Associate materials - to identify the materials with
        // the respective quantity instead of create association
        // of order - materials create a bridge table (many to
        // many association)
        entity.getMaterials().stream().forEach(material -> workorderItemService.create(material, orionEntity.getId()));
        return mapOrionEntityToEntity(orionEntity);
    }

    @Override
    public BrokerWorkOrderSummaryOutputDto get(String id) {
        BrokerWorkOrderSummaryOutputDto orionEntity = reader.readObject(id);
        orionEntity.setMaterials(retriveOrderMaterials(orionEntity.getId()));
        orionEntity.setRobotId(retrieveWorkOrderRobot(id));
        return orionEntity;
    }

    @Override
    public void update(String id, BrokerWorkOrderInputDto entity) {
        super.update(id, entity);
        if (!Strings.isEmpty(entity.getWarehouseId()) || !Strings.isEmpty(entity.getWorkingStationId())) {
            // Associate workstation and warehouse
            WorkOrder orionEntity = reader.readOrionEntity(id);
            List<String> relationships = new LinkedList<>();
            if (!Strings.isEmpty(entity.getWorkingStationId())) {
                relationships.add(entity.getWorkingStationId());
            } else {
                relationships.add(Strings.EMPTY);
            }
            if (!Strings.isEmpty(entity.getWarehouseId())) {
                relationships.add(entity.getWarehouseId());
            } else {
                relationships.add(Strings.EMPTY);
            }
            publisher.append(orionEntity, relationships);
        }
    }

    public List<BrokerWorkOrderSummaryOutputDto> getAll(String start, String end) {
        List<WorkOrder> orders = reader.readOrionEntityQueryListBetween(WorkOrderField.SCHEDULED_AT.getField(), start,
                end, EntityType.WORK_ORDER.getType());
        
        Map<String, BaseOrionAttributeOutputDto<String>> workstations = workStationService.getAll().stream().collect(Collectors.toMap(BrokerWorkStationSummaryOutputDto::getId, BrokerWorkStationSummaryOutputDto::getName));
        Map<String, BaseOrionAttributeOutputDto<String>> warehouses = warehouseService.getAll().stream().collect(Collectors.toMap(BrokerWarehouseSummaryOutputDto::getId, BrokerWarehouseSummaryOutputDto::getName));
        
        List<BrokerWorkOrderSummaryOutputDto> output = new ArrayList<>(orders.size());
        for (WorkOrder order : orders) {
            if (order.getRefWarehouse() != null && order.getRefWorkstation() != null) {
                BrokerWorkOrderSummaryOutputDto orderOutput = modelMapper.map(order, BrokerWorkOrderSummaryOutputDto.class);
                orderOutput.setWarehouse(warehouses.get(order.getRefWarehouse().getValue()));
                orderOutput.setWorkstation(workstations.get(order.getRefWorkstation().getValue()));
                output.add(orderOutput);
            } else if (order.getRefWarehouse() == null) {            	
            	log.warn("Ignoring corrupted work order {} - it is missing warehouse", order.getId());
            }
            else {
            	log.warn("Ignoring corrupted work order {} - it is missing workstation", order.getId());
            }
        }

        return output;
    }

    private BaseOrionAttributeOutputDto<String> retrieveWorkOrderRobot(String id) {
        List<Robot> robots = robotReader.readOrionEntityQueryListRelationship("refWorkOrder", id, EntityType.ROBOT.getType());
        if (!robots.isEmpty()) {
            BaseOrionAttributeOutputDto<String> robot = new BaseOrionAttributeOutputDto<>();
            robot.setValue(robots.get(0).getId());
            return robot;
        }
        return null;
    }

    private List<BrokerWorkOrderMaterialsSummaryOutputDto> retriveOrderMaterials(String id) {
        List<WorkOrderItem> workorderItems = workorderItemReader.readOrionEntityQueryListRelationship(
                WorkOrderField.REF_WORK_ORDER.getField(), id, EntityType.WORK_ORDER_ITEM.getType());
        if (!workorderItems.isEmpty()) {
            List<BrokerWorkOrderMaterialsSummaryOutputDto> output = new ArrayList<>(workorderItems.size());
            for (WorkOrderItem workOrderItem : workorderItems) {
                BrokerWorkOrderMaterialsSummaryOutputDto material = modelMapper.map(workOrderItem, BrokerWorkOrderMaterialsSummaryOutputDto.class);
                BrokerWarehouseMaterialSummaryOutputDto materialBroker = materialService.get(material.getMaterialId());
                material.setType(materialBroker.getType());
                material.setUnit(materialBroker.getUnit());
                material.setBatch(materialBroker.getBatch());
                material.setErpId(materialBroker.getErpId());
                output.add(material);
            }
            return output;
        }
        return Collections.emptyList();
    }

    /**
     * Method that creates an work order if the order does not exists already (the
     * check is based on erp order id). If the order already exists, it is performed
     * an update (if the order did not started yet)
     *
     * @param input
     * @return
     */
    public List<BrokerWorkOrderIntegrateOutputDto> integrate(List<BrokerWorkOrderInputDto> orders) {
    	
    	List<BrokerWorkOrderIntegrateOutputDto> output = new ArrayList<>(orders.size());
    	
    	orders.stream().forEach(input -> {
    		log.info("Integrating {} at {}", input.getErpId(), Instant.now().getEpochSecond());
            if (Strings.isEmpty(input.getStatus())) {
                input.setStatus(WorkOrderStatus.SCHEDULED.getStatus());
            }
            
            BrokerWorkOrderIntegrateOutputDto result = new BrokerWorkOrderIntegrateOutputDto();
	        if (Strings.isEmpty(input.getErpId())) {
	            result.setOrionId(create(input).getOrionId());
	            result.setSuccess(Boolean.TRUE.booleanValue());
	            output.add(result);
	    		log.info(LOG_MSG_INTEGRATED_AT, input.getErpId(), Instant.now().getEpochSecond());
	            return;
	        }
	        
	        List<WorkOrder> workOrder = retrieveByExternalIdAndScheduledDate(input.getErpId(), input.getScheduledAt());
	        if (workOrder == null || workOrder.isEmpty()) {
	            result.setOrionId(create(input).getOrionId());
	            result.setSuccess(Boolean.TRUE.booleanValue());
	            output.add(result);
	    		log.info(LOG_MSG_INTEGRATED_AT, input.getErpId(), Instant.now().getEpochSecond());
	            return;
	        }
	        
	        if (workOrder.get(0).getStatus() != null && !WorkOrderStatus.SCHEDULED
	                .equals(WorkOrderStatus.getWorkOrderStatus(workOrder.get(0).getStatus().getValue()))) {
	        	result.setOrionId(workOrder.get(0).getErpId().getValue());
	        	result.setSuccess(Boolean.FALSE.booleanValue());
	            output.add(result);
	        	log.error(WorkOrderExceptionError.UPDATE_NOT_ALLOWED.message(workOrder.get(0).getStatus().getValue()));
	        	return;
	        }
	        
	        update(workOrder.get(0).getId(), input);
	        result.setOrionId(workOrder.get(0).getId());
            result.setSuccess(Boolean.TRUE.booleanValue());
            output.add(result);
    		log.info(LOG_MSG_INTEGRATED_AT, input.getErpId(), Instant.now().getEpochSecond());
    	});
    	
    	log.info("Finished integrating at {}", Instant.now().getEpochSecond());
    	forceIntegrationNotification(output);
    	log.info("Forced integration notification at {}", Instant.now().getEpochSecond());
    	
        return output;
    }
    
	private void forceIntegrationNotification(List<BrokerWorkOrderIntegrateOutputDto> orders) {
		new Thread(() -> 
			orders.stream().forEach(order -> {
				if (!order.isSuccess()) {
					return;
				}
		        WorkOrder orionEntity = reader.readOrionEntity(order.getOrionId());
		        
		        OrionDateTime integratedAt = new OrionDateTime();
		        integratedAt.setType(OrionEntityType.DATE_TIME.getType());
		        integratedAt.setValue(dateFormat.format(new Date()));
				orionEntity.setIntegratedAt(integratedAt);
		        
				publisher.update(orionEntity, order.getOrionId());
			})
		).start();
	}

    private List<WorkOrder> retrieveByExternalIdAndScheduledDate(String erpId, String scheduledAt) {
        return reader.readOrionEntityQueryListRelationship(List.of(WorkOrderField.ERP_EXTERNAL_ID.getField(), WorkOrderField.SCHEDULED_AT.getField()),
                List.of(new StringBuilder(Constant.ID).append(erpId).toString(), scheduledAt), EntityType.WORK_ORDER.getType());
	}

	public BrokerWorkOrderSummaryOutputDto getByExternalId(String id, String scheduledAt) {
        List<WorkOrder> workOrders = retrieveByExternalIdAndScheduledDate(id, scheduledAt);
        if (workOrders == null || workOrders.isEmpty()) {
            throw new WorkOrderNotFoundException();
        }
        return get(workOrders.get(0).getId());
    }

	public List<BrokerWorkOrderMaterialsSummaryOutputDto> getMaterialsById(String id) {
        return get(id).getMaterials();
	}
}
