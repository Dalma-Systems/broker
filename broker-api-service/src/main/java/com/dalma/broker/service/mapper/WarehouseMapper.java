package com.dalma.broker.service.mapper;

import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseInputDto;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import com.dalma.broker.fiware.orion.connector.entity.robot.RobotDestinationRelationship;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.Warehouse;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterialRelationship;
import com.dalma.contract.dto.warehouse.BrokerWarehouseSummaryOutputDto;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper extends OrionBaseMapper {
    private WarehouseMapper() {
    }

    @Component
    public static class WarehouseInputDtoToWarehouse extends PropertyMap<BrokerWarehouseInputDto, Warehouse> {

        @Override
        protected void configure() {
            using(idToOrionId).map(source).setId(null);
            using(coordinatesToOrionLocation).map(source).setLocation(null);
            using(stringToOrionString).map(source.getName()).setName(null);
            using(stringToOrionString).map(source.getStatus()).setStatus(null);
            using(stringToOrionIdString).map(source.getErpId()).setErpId(null);
        }
    }

    @Component
    public static class WarehouseToWarehouseRelationship extends PropertyMap<Warehouse, WarehouseMaterialRelationship> {

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
    public static class WarehouseToWarehouseSummaryOutputDto
            extends PropertyMap<Warehouse, BrokerWarehouseSummaryOutputDto> {

        @Override
        protected void configure() {
            using(geoCoordinatesToAttrOutput).map(source.getLocation()).setCoordinates(null);
            using(doubleAttrToAttrOutput).map(source.getLocation().getMetadata().getAngle()).setAngle(null);
            using(strAttrToAttrOutput).map(source.getName()).setName(null);
            using(strAttrToAttrOutput).map(source.getStatus()).setStatus(null);
            using(strIdAttrToAttrOutput).map(source.getErpId()).setErpId(null);
            using(dateToDateOutput).map(source.getDateCreated()).setDateCreated(null);
            using(dateToDateOutput).map(source.getDateModified()).setDateModified(null);
        }
    }

    @Component
    public static class WarehouseToRobotDestinationRelationship
            extends PropertyMap<Warehouse, RobotDestinationRelationship> {

        private final Converter<Warehouse, OrionAttribute<String>> entityToRelationshipEntity = context -> {
            if (context.getSource() == null) {
                return null;
            }

            return (OrionAttribute<String>) createOrionRelationship(context.getSource().getId());
        };

        @Override
        protected void configure() {
            using(entityToRelationshipEntity).map(source).setRefDestination(null);
        }
    }
}
