package com.dalma.broker.service.publisher;

import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseInputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.Warehouse;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.WarehouseOrionPublisher;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterialRelationship;
import com.dalma.broker.service.mapper.OrionBaseMapper;
import com.dalma.common.entity.EntityType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WarehousePublisher extends WarehouseOrionPublisher<BrokerWarehouseInputDto> {

    private final ModelMapper modelMapper;

    public WarehousePublisher(OrionConnectorConfiguration config, ModelMapper modelMapper) {
        super(config);
        this.modelMapper = modelMapper;
    }

    @Override
    public Warehouse mapEntityToOrionEntity(BrokerWarehouseInputDto entity) {
        return modelMapper.map(entity, Warehouse.class);
    }

    @Override
    public OrionRelationship<WarehouseMaterialRelationship> mapEntityToOrionRelationshipEntity(Warehouse orionEntity,
                                                                                               List<String> relatedIds,
                                                                                               String contextId) {
        OrionRelationship<WarehouseMaterialRelationship> relationship = new OrionRelationship<>();
        List<WarehouseMaterialRelationship> materials = new ArrayList<>(relatedIds.size());
        for (String relatedId : relatedIds) {
            WarehouseMaterialRelationship material = modelMapper.map(orionEntity, WarehouseMaterialRelationship.class);
            material.setId(relatedId);
            material.setType(EntityType.WAREHOUSE_MATERIAL.getType());
            materials.add(material);
        }
        relationship.setEntities(materials);
        return relationship;
    }

    @Override
    protected Warehouse mapEntityIdAndDate(Warehouse mapEntityToOrionEntity) {
        mapEntityToOrionEntity
                .setId(mapEntityToOrionEntity.getId().replace(OrionBaseMapper.ORION_TYPE_MACRO, EntityType.WAREHOUSE.getType()));
        return mapEntityToOrionEntity;
    }
}
