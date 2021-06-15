package com.dalma.broker.service.publisher;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterial;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterialOrionPublisher;
import com.dalma.broker.service.mapper.OrionBaseMapper;
import com.dalma.common.entity.EntityType;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WarehouseMaterialPublisher extends WarehouseMaterialOrionPublisher<BrokerWarehouseMaterialInputDto> {

	private final ModelMapper modelMapper;

	public WarehouseMaterialPublisher(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public WarehouseMaterial mapEntityToOrionEntity(BrokerWarehouseMaterialInputDto entity) {
		return modelMapper.map(entity, WarehouseMaterial.class);
	}

	@Override
	public OrionRelationship<OrionRelationshipEntity> mapEntityToOrionRelationshipEntity(WarehouseMaterial orionEntity,
			List<String> relatedIds, String contextId) {
		return null;
	}

	@Override
	protected WarehouseMaterial mapEntityIdAndDate(WarehouseMaterial mapEntityToOrionEntity) {
		mapEntityToOrionEntity.setId(mapEntityToOrionEntity.getId().replace(OrionBaseMapper.ORION_TYPE_MACRO,
				EntityType.WAREHOUSE_MATERIAL.getType()));
		return mapEntityToOrionEntity;
	}
}
