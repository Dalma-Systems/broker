package com.dalma.broker.service.mapper;

import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import com.dalma.broker.fiware.orion.connector.entity.station.work.WorkStation;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.Warehouse;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrder;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrderWarehouseRelationship;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrderWorkStationRelationship;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItem;
import com.dalma.contract.dto.work.order.BrokerWorkOrderInputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderMaterialsSummaryOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderSummaryOutputDto;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class WorkOrderMapper extends OrionBaseMapper {
    private WorkOrderMapper() {
    }

    @Component
    public static class WorkOrderInputDtoToWorkOrder extends PropertyMap<BrokerWorkOrderInputDto, WorkOrder> {

        private static final Converter<BrokerWorkOrderInputDto, OrionAttribute<ArrayList<String>>> stringListToOrionStringList = context -> {
            if (context.getSource() == null || context.getSource().getScheduleIds() == null || context.getSource().getScheduleIds().isEmpty()) {
                return null;
            }

            return (OrionAttribute<ArrayList<String>>) createOrionRelationshipArray(context.getSource().getScheduleIds().toArray(new String[context.getSource().getScheduleIds().size()]));
        };

        @Override
        protected void configure() {
            using(idToOrionId).map(source).setId(null);
            using(stringToOrionString).map(source.getStatus()).setStatus(null);
            using(stringToOrionString).map(source.getAction()).setAction(null);
            using(dateToOrionDate).map(source.getScheduledAt()).setScheduledAt(null);
            using(dateToOrionDate).map(source.getStartedAt()).setStartedAt(null);
            using(dateToOrionDate).map(source.getEndAt()).setEndedAt(null);
            using(stringToOrionIdString).map(source.getErpId()).setErpId(null);
            using(stringListToOrionStringList).map(source).setScheduleIds(null);
        }
    }

    @Component
    public static class WorkOrderToWorkOrderSummaryOutputDto
            extends PropertyMap<WorkOrder, BrokerWorkOrderSummaryOutputDto> {

        @Override
        protected void configure() {
            using(strAttrToAttrOutput).map(source.getStatus()).setStatus(null);
            using(strAttrToAttrOutput).map(source.getAction()).setAction(null);
            using(strIdAttrToAttrOutput).map(source.getErpId()).setErpId(null);
            using(dateToDateOutput).map(source.getDateCreated()).setDateCreated(null);
            using(dateToDateOutput).map(source.getDateModified()).setDateModified(null);
            using(dateToDateOutput).map(source.getScheduledAt()).setScheduledAt(null);
            using(dateToDateOutput).map(source.getStartedAt()).setStartedAt(null);
            using(dateToDateOutput).map(source.getEndedAt()).setEndedAt(null);
            using(strBaseAttrToAttrOutput).map(source.getRefWorkstation()).setWorkstationId(null);
            using(strBaseAttrToAttrOutput).map(source.getRefWarehouse()).setWarehouseId(null);
        }
    }

    @Component
    public static class WorkstationToWorkOrderWorkstationRelationship
            extends PropertyMap<WorkStation, WorkOrderWorkStationRelationship> {

        private final Converter<WorkStation, OrionBaseAttribute<String>> entityToRelationshipEntity = context -> {
            if (context.getSource() == null) {
                return null;
            }

            return createOrionRelationship(context.getSource().getId());
        };

        @Override
        protected void configure() {
            using(entityToRelationshipEntity).map(source).setRefWorkstation(null);
        }
    }

    @Component
    public static class WarehouseToWorkOrderWarehouseRelationship
            extends PropertyMap<Warehouse, WorkOrderWarehouseRelationship> {

        private final Converter<Warehouse, OrionBaseAttribute<String>> entityToRelationshipEntity = context -> {
            if (context.getSource() == null) {
                return null;
            }

            return createOrionRelationship(context.getSource().getId());
        };

        @Override
        protected void configure() {
            using(entityToRelationshipEntity).map(source).setRefWarehouse(null);
        }
    }

    @Component
    public static class WorkOrderItemToWorkOrderMaterialsSummaryOutputDto
            extends PropertyMap<WorkOrderItem, BrokerWorkOrderMaterialsSummaryOutputDto> {

        @Override
        protected void configure() {
            using(strBaseAttrToStrOutput).map(source.getRefMaterial()).setMaterialId(null);
            using(doubleBaseAttrToAttrOutput).map(source.getQuantity()).setQuantity(null);
        }
    }
}
