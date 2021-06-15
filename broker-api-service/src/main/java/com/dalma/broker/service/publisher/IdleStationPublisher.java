package com.dalma.broker.service.publisher;

import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationInputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStation;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStationOrionPublisher;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStationRelationship;
import com.dalma.broker.service.mapper.OrionBaseMapper;
import com.dalma.common.entity.EntityType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IdleStationPublisher extends IdleStationOrionPublisher<BrokerIdleStationInputDto> {

	private final ModelMapper modelMapper;

	public IdleStationPublisher(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public IdleStation mapEntityToOrionEntity(BrokerIdleStationInputDto entity) {
		return modelMapper.map(entity, IdleStation.class);
	}

	@Override
	protected IdleStation mapEntityIdAndDate(IdleStation mapEntityToOrionEntity) {
		mapEntityToOrionEntity.setId(
				mapEntityToOrionEntity.getId().replace(OrionBaseMapper.ORION_TYPE_MACRO, EntityType.IDLE_STATION.getType()));
		return mapEntityToOrionEntity;
	}

	@Override
	public OrionRelationship<? extends OrionRelationshipEntity> mapEntityToOrionRelationshipEntity(
			IdleStation orionEntity, List<String> relatedIds, String contextId) {
		OrionRelationship<OrionRelationshipEntity> relationship = new OrionRelationship<>();
		
		IdleStationRelationship robot = new IdleStationRelationship();
		robot.setRefRobot(OrionBaseMapper.createOrionRelationship(relatedIds.get(0)));
		robot.setId(orionEntity.getId());
		robot.setType(EntityType.IDLE_STATION.getType());
		relationship.setEntities(List.of(robot));
		
		return relationship;
	}
}
