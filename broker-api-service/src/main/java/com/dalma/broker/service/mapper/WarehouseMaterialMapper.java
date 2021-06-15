package com.dalma.broker.service.mapper;

import com.dalma.broker.contract.dto.warehouse.material.BrokerWarehouseMaterialSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterial;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMaterialMapper extends OrionBaseMapper {
    private WarehouseMaterialMapper() {
    }

    @Component
    public static class WarehouseMaterialInputDtoToWarehouseMaterial
            extends PropertyMap<BrokerWarehouseMaterialInputDto, WarehouseMaterial> {

        @Override
        protected void configure() {
            using(idToOrionId).map(source).setId(null);
            using(stringToOrionString).map(source.getBatch()).setBatch(null);
            using(stringToOrionString).map(source.getMaterial()).setMType(null);
            using(doubleToOrionDouble).map(source.getQuantity()).setQuantity(null);
            using(stringToOrionString).map(source.getUnit()).setUnit(null);
            using(stringToOrionString).map(source.getErpId()).setErpId(null);
        }
    }

    @Component
    public static class WarehouseMaterialToWarehouseMaterialSummaryOutputDto
            extends PropertyMap<WarehouseMaterial, BrokerWarehouseMaterialSummaryOutputDto> {

        @Override
        protected void configure() {
            using(strAttrToAttrOutput).map(source.getBatch()).setBatch(null);
            using(strAttrToAttrOutput).map(source.getMType()).setType(null);
            using(doubleAttrToAttrOutput).map(source.getQuantity()).setQuantity(null);
            using(strAttrToAttrOutput).map(source.getUnit()).setUnit(null);
            using(strAttrToAttrOutput).map(source.getErpId()).setErpId(null);
            using(strBaseAttrToStrOutput).map(source.getRefWarehouse()).setWarehouseId(null);
            using(dateToDateOutput).map(source.getDateCreated()).setDateCreated(null);
            using(dateToDateOutput).map(source.getDateModified()).setDateModified(null);
        }
    }
}
