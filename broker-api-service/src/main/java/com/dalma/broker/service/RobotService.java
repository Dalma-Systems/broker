package com.dalma.broker.service;

import com.dalma.broker.fiware.orion.connector.OrionConnectorException;
import com.dalma.broker.fiware.orion.connector.entity.robot.Robot;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStation;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterial;
import com.dalma.broker.service.exception.robot.RobotAlreadyExistsException;
import com.dalma.broker.service.notification.RobotNotificationHandler;
import com.dalma.broker.service.publisher.RobotPublisher;
import com.dalma.broker.service.reader.IdleStationReader;
import com.dalma.broker.service.reader.RobotReader;
import com.dalma.broker.service.reader.WarehouseMaterialReader;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.base.BaseOrionAttributeOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotSummaryOutputDto;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationInputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderInputDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RobotService extends
        BaseOrionCrudService<BrokerRobotInputDto, BrokerRobotOutputDto, BrokerRobotSummaryOutputDto, Robot, RobotPublisher, RobotReader> {
    private static final String REF_ROBOT = "refRobot";
    private final RobotPublisher publisher;
    private final RobotReader reader;
    private final RobotNotificationHandler notificationHandler;
    private final WarehouseMaterialReader materialReader;
    private final IdleStationReader idleStationReader;
    private final WorkOrderService workOrderService;
    private final ObjectMapper objectMapper;

    public RobotService(RobotPublisher publisher, RobotReader reader, RobotNotificationHandler notificationHandler,
                        WarehouseMaterialReader materialReader, IdleStationReader idleStationReader,
                        WorkOrderService workOrderService, ObjectMapper objectMapper) {
        super(publisher, reader);
        this.reader = reader;
        this.publisher = publisher;
        this.notificationHandler = notificationHandler;
        this.materialReader = materialReader;
        this.idleStationReader = idleStationReader;
        this.workOrderService = workOrderService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected String getType() {
        return EntityType.ROBOT.getType();
    }

    @Override
    protected BrokerRobotOutputDto mapOrionEntityToEntity(Robot orionEntity) {
        return new BrokerRobotOutputDto(orionEntity.getId(), orionEntity.getEntityType());
    }

    @Override
    public BrokerRobotOutputDto create(BrokerRobotInputDto entity) {
    	try {
    		return super.create(entity);
    	} catch (OrionConnectorException e) {
    		try {
    			JsonNode orionError = objectMapper.readTree(e.getMessage().split(new StringBuilder(Constant.COLON).append(Constant.COLON).toString())[1]);
    			if (RobotAlreadyExistsException.ERROR_MESSAGE.equals(orionError.get(RobotAlreadyExistsException.ERROR_FIELD).asText())) {
    				throw new RobotAlreadyExistsException(entity.getMacAddress());
    			}
    		} catch (RobotAlreadyExistsException e1) {
    			throw e1;
    		} catch (Exception e2) {
    			// Nothing to do
			}
    		throw e;
		}
    }
    
    @Override
    public List<BrokerRobotSummaryOutputDto> getAll() {
        List<BrokerRobotSummaryOutputDto> robots = super.getAll();
        robots.stream().forEach(this::retrieveIdleStation);
        return robots;
    }

    @Override
    public BrokerRobotSummaryOutputDto get(String id) {
        BrokerRobotSummaryOutputDto orionEntity = reader.readObject(id);

        List<WarehouseMaterial> workorders = materialReader.readOrionEntityQueryListRelationship(REF_ROBOT,
                orionEntity.getId(), EntityType.WAREHOUSE_MATERIAL.getType());
        if (!workorders.isEmpty()) {
            orionEntity.setMaterials(workorders.stream().map(WarehouseMaterial::getId).collect(Collectors.toList()));
        }

        retrieveIdleStation(orionEntity);

        return orionEntity;
    }

    private void retrieveIdleStation(BrokerRobotSummaryOutputDto orionEntity) {
        List<IdleStation> idleStations = idleStationReader.readOrionEntityQueryListRelationship(REF_ROBOT,
                orionEntity.getId(), EntityType.IDLE_STATION.getType());
        if (!idleStations.isEmpty()) {
            BaseOrionAttributeOutputDto<String> idleStation = new BaseOrionAttributeOutputDto<>();
            idleStation.setValue(idleStations.get(0).getId());
            orionEntity.setIdleStation(idleStation);
        }
    }

    @Override
    public void update(String id, BrokerRobotInputDto entity) {
        super.update(id, entity);

        // Associate destination (warehouse / workstation / idlestation) and materials
        if (!Strings.isEmpty(entity.getDestination()) || entity.getPayload() != null && !entity.getPayload().isEmpty()
                || !Strings.isEmpty(entity.getWorkOrderId())) {
            List<String> relatedIds = new LinkedList<>();

            if (!Strings.isEmpty(entity.getDestination())) {
                relatedIds.add(entity.getDestination());
            } else {
                relatedIds.add(Strings.EMPTY);
            }

            if (!Strings.isEmpty(entity.getWorkOrderId())) {
                relatedIds.add(entity.getWorkOrderId());
            } else {
                relatedIds.add(Strings.EMPTY);
            }

            if (entity.getPayload() != null && !entity.getPayload().isEmpty()) {
                relatedIds.addAll(entity.getPayload());
            }

            publisher.append(reader.readOrionEntity(id), relatedIds, entity.getContextId());
        }

        if (!Strings.isEmpty(entity.getAction())) {
            updateWorkOrderAction(id, entity);
        }
    }

    private void updateWorkOrderAction(String id, BrokerRobotInputDto entity) {
        BrokerRobotSummaryOutputDto robot = get(id);
        if (robot.getWorkOrder() != null && !Strings.isEmpty(robot.getWorkOrder().getValue())) {
            BrokerWorkOrderInputDto orderInput = new BrokerWorkOrderInputDto();
            orderInput.setAction(entity.getAction());
            workOrderService.update(robot.getWorkOrder().getValue(), orderInput);
        }
    }

    public void notificationStatus(BrokerRobotNotificationInputDto status) {
        notificationHandler.handleNotification(status);
    }
}
