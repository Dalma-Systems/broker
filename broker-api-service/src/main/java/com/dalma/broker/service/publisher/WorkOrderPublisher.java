package com.dalma.broker.service.publisher;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrder;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrderOrionPublisher;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrderWarehouseRelationship;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrderWorkStationRelationship;
import com.dalma.broker.service.mapper.OrionBaseMapper;
import com.dalma.broker.service.reader.WarehouseReader;
import com.dalma.broker.service.reader.WorkStationReader;
import com.dalma.common.entity.EntityType;
import com.dalma.contract.dto.work.order.BrokerWorkOrderInputDto;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class WorkOrderPublisher extends WorkOrderOrionPublisher<BrokerWorkOrderInputDto> {

    private final ModelMapper modelMapper;
    private final WorkStationReader workstationReader;
    private final WarehouseReader warehouseReader;

    public WorkOrderPublisher(OrionConnectorConfiguration config, ModelMapper modelMapper,
                              WorkStationReader workstationReader, WarehouseReader warehouseReader) {
        super(config);
        this.modelMapper = modelMapper;
        this.workstationReader = workstationReader;
        this.warehouseReader = warehouseReader;
    }

    @Override
    public WorkOrder mapEntityToOrionEntity(BrokerWorkOrderInputDto entity) {
        return modelMapper.map(entity, WorkOrder.class);
    }

    @Override
    protected WorkOrder mapEntityIdAndDate(WorkOrder mapEntityToOrionEntity) {
        mapEntityToOrionEntity
                .setId(mapEntityToOrionEntity.getId().replace(OrionBaseMapper.ORION_TYPE_MACRO, EntityType.WORK_ORDER.getType()));
        return mapEntityToOrionEntity;
    }

    @Override
    public OrionRelationship<OrionRelationshipEntity> mapEntityToOrionRelationshipEntity(WorkOrder orionEntity,
                                                                                         List<String> relatedIds, 
                                                                                         String contextId) {
        OrionRelationship<OrionRelationshipEntity> relationship = new OrionRelationship<>();

        List<OrionRelationshipEntity> entities = new LinkedList<>();
        if (!Strings.isEmpty(relatedIds.get(0))) {
            WorkOrderWorkStationRelationship workstation = modelMapper
                    .map(workstationReader.readOrionEntity(relatedIds.get(0)), WorkOrderWorkStationRelationship.class);
            workstation.setId(orionEntity.getId());
            workstation.setType(EntityType.WORK_ORDER.getType());
            entities.add(workstation);
        }

        if (!Strings.isEmpty(relatedIds.get(1))) {
            WorkOrderWarehouseRelationship warehouse = modelMapper.map(warehouseReader.readOrionEntity(relatedIds.get(1)),
                    WorkOrderWarehouseRelationship.class);
            warehouse.setId(orionEntity.getId());
            warehouse.setType(EntityType.WORK_ORDER.getType());
            entities.add(warehouse);
        }

        relationship.setEntities(entities);
        return relationship;
    }
}
