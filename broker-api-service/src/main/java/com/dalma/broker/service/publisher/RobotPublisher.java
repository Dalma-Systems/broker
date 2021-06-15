package com.dalma.broker.service.publisher;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.robot.Robot;
import com.dalma.broker.fiware.orion.connector.entity.robot.RobotDestinationRelationship;
import com.dalma.broker.fiware.orion.connector.entity.robot.RobotOrionPublisher;
import com.dalma.broker.fiware.orion.connector.entity.robot.RobotWarehouseMaterialRelationship;
import com.dalma.broker.fiware.orion.connector.entity.robot.RobotWorkOrderRelationship;
import com.dalma.broker.service.mapper.OrionBaseMapper;
import com.dalma.broker.service.reader.WarehouseReader;
import com.dalma.broker.service.reader.WorkStationReader;
import com.dalma.common.entity.EntityType;
import com.dalma.common.message.MessageContextType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class RobotPublisher extends RobotOrionPublisher<BrokerRobotInputDto> {

	private final ModelMapper modelMapper;
	private final WarehouseReader warehouseReader;
	private final WorkStationReader workstationReader;

	private static final String WAREHOUSE_PREFIX_ID = Constant.ORION_ID_PREFIX + EntityType.WAREHOUSE.getType();
	private static final String WORKSTATION_PREFIX_ID = Constant.ORION_ID_PREFIX + EntityType.WORK_STATION.getType();
	private static final String WORK_ORDER_PREFIX_ID = Constant.ORION_ID_PREFIX + EntityType.WORK_ORDER.getType();
	private static final String ACTION_CONTEXT_ID = new StringBuilder(MessageContextType.ACTION.getType()).append(Constant.HYPHEN).toString();
	
	public RobotPublisher(OrionConnectorConfiguration config, ModelMapper modelMapper, WarehouseReader warehouseReader,
			WorkStationReader workstationReader) {
		super(config);
		this.modelMapper = modelMapper;
		this.warehouseReader = warehouseReader;
		this.workstationReader = workstationReader;
	}

	@Override
	public Robot mapEntityToOrionEntity(BrokerRobotInputDto entity) {
		Robot robot = modelMapper.map(entity, Robot.class);
		if (Strings.isNotEmpty(entity.getAction()) && entity.getContextId() != null && entity.getContextId().startsWith(ACTION_CONTEXT_ID)) {
			robot.getAction().getMetadata().setContext(OrionBaseMapper.createOrionBaseAttribute(entity.getContextId()));
		} else if (Strings.isNotEmpty(entity.getStatus()) && entity.getContextId() != null && entity.getContextId().startsWith(ACTION_CONTEXT_ID)) {
			robot.getStatus().getMetadata().setContext(OrionBaseMapper.createOrionBaseAttribute(entity.getContextId()));
		}
		return robot;
	}

	@Override
	protected Robot mapEntityIdAndDate(Robot mapEntityToOrionEntity) {
		mapEntityToOrionEntity
				.setId(mapEntityToOrionEntity.getId().replace(OrionBaseMapper.ORION_TYPE_MACRO, EntityType.ROBOT.getType()));
		return mapEntityToOrionEntity;
	}

	@Override
	public OrionRelationship<OrionRelationshipEntity> mapEntityToOrionRelationshipEntity(Robot orionEntity,
			List<String> relatedIds, String contextId) {
		OrionRelationship<OrionRelationshipEntity> relationship = new OrionRelationship<>();
		List<OrionRelationshipEntity> relationshipEntities = new LinkedList<>();

		// Destination is in first position
		if (!Strings.EMPTY.equals(relatedIds.get(0))) {
			if (relatedIds.get(0).startsWith(WAREHOUSE_PREFIX_ID)) {
				RobotDestinationRelationship warehouse = modelMapper
						.map(warehouseReader.readOrionEntity(relatedIds.get(0)), RobotDestinationRelationship.class);
				warehouse.setId(orionEntity.getId());
				warehouse.setType(EntityType.ROBOT.getType());
				appendContextId(warehouse, contextId);
				relationshipEntities.add(warehouse);
			} else if (relatedIds.get(0).startsWith(WORKSTATION_PREFIX_ID)) {
				RobotDestinationRelationship workstation = modelMapper
						.map(workstationReader.readOrionEntity(relatedIds.get(0)), RobotDestinationRelationship.class);
				workstation.setId(orionEntity.getId());
				workstation.setType(EntityType.ROBOT.getType());
				appendContextId(workstation, contextId);
				relationshipEntities.add(workstation);
			} else {
				RobotDestinationRelationship positionZero = new RobotDestinationRelationship();
				positionZero.setRefDestination((OrionAttribute<String>) OrionBaseMapper.createOrionRelationship(relatedIds.get(0)));
				positionZero.setId(orionEntity.getId());
				positionZero.setType(EntityType.ROBOT.getType());
				appendContextId(positionZero, contextId);
				relationshipEntities.add(positionZero);
			}
		}
		relatedIds.remove(0);

		// Work order is now the first position
		if (!Strings.EMPTY.equals(relatedIds.get(0))) {
			RobotWorkOrderRelationship workOrder = new RobotWorkOrderRelationship();
			if (relatedIds.get(0).startsWith(WORK_ORDER_PREFIX_ID)) {
				workOrder.setRefWorkOrder((OrionAttribute<String>) OrionBaseMapper.createOrionRelationship(relatedIds.get(0)));
			} else {
				workOrder.setRefWorkOrder((OrionAttribute<String>) OrionBaseMapper.createOrionRelationship(Strings.EMPTY));
			}
			workOrder.setId(orionEntity.getId());
			workOrder.setType(EntityType.ROBOT.getType());
			relationshipEntities.add(workOrder);
		}
		relatedIds.remove(0);

		// Materials are in remaining positions
		RobotWarehouseMaterialRelationship material = new RobotWarehouseMaterialRelationship();
		material.setRefPayload(OrionBaseMapper.createOrionRelationshipArray(relatedIds.toArray(new String[0])));
		material.setId(orionEntity.getId());
		material.setType(EntityType.ROBOT.getType());
		relationshipEntities.add(material);

		relationship.setEntities(relationshipEntities);
		return relationship;
	}

	private void appendContextId(RobotDestinationRelationship relationship, String contextId) {
		if (contextId == null) {
			return;
		}
		relationship.getRefDestination().getMetadata().setContext(OrionBaseMapper.createOrionBaseAttribute(contextId));
	}
}
