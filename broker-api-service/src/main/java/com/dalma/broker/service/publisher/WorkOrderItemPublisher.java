package com.dalma.broker.service.publisher;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItem;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItemOrionPublisher;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItemWorkorderRelationship;
import com.dalma.broker.service.mapper.OrionBaseMapper;
import com.dalma.broker.service.reader.WorkOrderReader;
import com.dalma.common.entity.EntityType;
import com.dalma.contract.dto.work.order.item.BrokerWorkOrderItemInputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkOrderItemPublisher extends WorkOrderItemOrionPublisher<BrokerWorkOrderItemInputDto> {

    private final ModelMapper modelMapper;
    private final WorkOrderReader workorderReader;

    public WorkOrderItemPublisher(OrionConnectorConfiguration config, ModelMapper modelMapper,
                                  WorkOrderReader workorderReader) {
        super(config);
        this.modelMapper = modelMapper;
        this.workorderReader = workorderReader;
    }

    @Override
    public WorkOrderItem mapEntityToOrionEntity(BrokerWorkOrderItemInputDto entity) {
        return modelMapper.map(entity, WorkOrderItem.class);
    }

    @Override
    protected WorkOrderItem mapEntityIdAndDate(WorkOrderItem mapEntityToOrionEntity) {
        mapEntityToOrionEntity.setId(
                mapEntityToOrionEntity.getId().replace(OrionBaseMapper.ORION_TYPE_MACRO, EntityType.WORK_ORDER_ITEM.getType()));
        return mapEntityToOrionEntity;
    }

    @Override
    public OrionRelationship<WorkOrderItemWorkorderRelationship> mapEntityToOrionRelationshipEntity(
            WorkOrderItem orionEntity, List<String> relatedIds, String contextId) {
        OrionRelationship<WorkOrderItemWorkorderRelationship> relationship = new OrionRelationship<>();
        List<WorkOrderItemWorkorderRelationship> workorders = new ArrayList<>(relatedIds.size());
        for (String relatedId : relatedIds) {
            WorkOrderItemWorkorderRelationship workorder = modelMapper.map(workorderReader.readOrionEntity(relatedId),
                    WorkOrderItemWorkorderRelationship.class);
            workorder.setId(orionEntity.getId());
            workorder.setType(EntityType.WORK_ORDER_ITEM.getType());
            workorders.add(workorder);
        }
        relationship.setEntities(workorders);
        return relationship;
    }
}
