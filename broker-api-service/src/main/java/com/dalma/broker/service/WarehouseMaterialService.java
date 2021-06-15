package com.dalma.broker.service;

import com.dalma.broker.contract.dto.warehouse.material.BrokerWarehouseMaterialSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.Warehouse;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterial;
import com.dalma.broker.service.exception.warehouse.WarehouseNotFoundException;
import com.dalma.broker.service.publisher.WarehouseMaterialPublisher;
import com.dalma.broker.service.publisher.WarehousePublisher;
import com.dalma.broker.service.reader.WarehouseMaterialReader;
import com.dalma.broker.service.reader.WarehouseReader;
import com.dalma.common.entity.EntityType;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialOutputDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class WarehouseMaterialService extends
        BaseOrionCrudService<BrokerWarehouseMaterialInputDto, BrokerWarehouseMaterialOutputDto, BrokerWarehouseMaterialSummaryOutputDto, WarehouseMaterial, WarehouseMaterialPublisher, WarehouseMaterialReader> {
    private final WarehouseReader warehouseReader;
    private final WarehousePublisher warehousePublisher;

    public WarehouseMaterialService(WarehouseMaterialPublisher publisher, WarehouseMaterialReader reader,
                                    WarehouseReader warehouseReader, WarehousePublisher warehousePublisher) {
        super(publisher, reader);
        this.warehouseReader = warehouseReader;
        this.warehousePublisher = warehousePublisher;
    }

    public BrokerWarehouseMaterialOutputDto createMaterialInWarehouse(BrokerWarehouseMaterialInputDto entity) {
        String[] uriParts = ServletUriComponentsBuilder.fromCurrentRequest().toUriString().split("/");
        String warehouseId = uriParts[uriParts.length - 2];
        Warehouse warehouse = warehouseReader.readOrionEntity(warehouseId);
        if (warehouse == null) {
            throw new WarehouseNotFoundException();
        }

        BrokerWarehouseMaterialOutputDto output = super.create(entity);
        warehousePublisher.append(warehouse, output.getOrionId());

        return output;
    }

    @Override
    protected String getType() {
        return EntityType.WAREHOUSE_MATERIAL.getType();
    }

    @Override
    protected BrokerWarehouseMaterialOutputDto mapOrionEntityToEntity(WarehouseMaterial orionEntity) {
        return new BrokerWarehouseMaterialOutputDto(orionEntity.getId());
    }
}
