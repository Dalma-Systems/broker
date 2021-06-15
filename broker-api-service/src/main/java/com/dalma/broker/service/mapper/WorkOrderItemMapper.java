package com.dalma.broker.service.mapper;

import com.dalma.broker.fiware.orion.connector.entity.OrionEntityType;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrder;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItem;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItemWorkorderRelationship;
import com.dalma.contract.dto.work.order.item.BrokerWorkOrderItemInputDto;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderItemMapper extends OrionBaseMapper {
    private WorkOrderItemMapper() {
    }

    @Component
    public static class WorkOrderItemInputDtoToWorkOrderItem
            extends PropertyMap<BrokerWorkOrderItemInputDto, WorkOrderItem> {

        private static final Converter<String, OrionBaseAttribute<String>> strToOrionBaseRelation = context -> {
            if (context.getSource() == null) {
                return null;
            }

            OrionBaseAttribute<String> orionBaseString = new OrionBaseAttribute<>();
            orionBaseString.setType(OrionEntityType.RELATIONSHIP.getType());
            orionBaseString.setValue(context.getSource());
            return orionBaseString;
        };

        @Override
        protected void configure() {
            using(idToOrionId).map(source).setId(null);
            using(doubleToOrionBaseDouble).map(source.getQuantity()).setQuantity(null);
            using(strToOrionBaseRelation).map(source.getId()).setRefMaterial(null);
        }
    }

    @Component
    public static class WorkOrderToWorkOrderItemWorkOrderRelationship
            extends PropertyMap<WorkOrder, WorkOrderItemWorkorderRelationship> {

        private final Converter<WorkOrder, OrionBaseAttribute<String>> entityToRelationshipEntity = context -> {
            if (context.getSource() == null) {
                return null;
            }

            return createOrionRelationship(context.getSource().getId());
        };

        @Override
        protected void configure() {
            using(entityToRelationshipEntity).map(source).setRefWorkorder(null);
        }
    }
}
