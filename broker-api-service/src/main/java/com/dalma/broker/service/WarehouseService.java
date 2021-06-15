package com.dalma.broker.service;

import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseInputDto;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.Warehouse;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.field.WarehouseField;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterial;
import com.dalma.broker.service.exception.warehouse.WarehouseNotFoundException;
import com.dalma.broker.service.publisher.WarehousePublisher;
import com.dalma.broker.service.reader.WarehouseMaterialReader;
import com.dalma.broker.service.reader.WarehouseReader;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.warehouse.BrokerWarehouseSummaryOutputDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService extends
        BaseOrionCrudService<BrokerWarehouseInputDto, BrokerWarehouseOutputDto, BrokerWarehouseSummaryOutputDto, Warehouse, WarehousePublisher, WarehouseReader> {

    private final WarehousePublisher publisher;
    private final WarehouseReader reader;
    private final WarehouseMaterialService materialService;
    private final WarehouseMaterialReader materialReader;

    public WarehouseService(WarehousePublisher publisher, WarehouseReader reader,
                            WarehouseMaterialService materialService, WarehouseMaterialReader materialReader) {
        super(publisher, reader);
        this.publisher = publisher;
        this.reader = reader;
        this.materialService = materialService;
        this.materialReader = materialReader;
    }

    @Override
    protected String getType() {
        return EntityType.WAREHOUSE.getType();
    }

    @Override
    protected BrokerWarehouseOutputDto mapOrionEntityToEntity(Warehouse orionEntity) {
        return new BrokerWarehouseOutputDto(orionEntity.getId());
    }

    @Override
    public BrokerWarehouseOutputDto create(BrokerWarehouseInputDto entity) {
        Warehouse orionEntity = publisher.create(entity);
        if (entity.getMaterials() != null) {
            List<String> ids = entity.getMaterials().stream()
                    .map(material -> materialService.create(material).getOrionId()).collect(Collectors.toList());
            publisher.append(orionEntity, ids);
        }
        return mapOrionEntityToEntity(orionEntity);
    }

    @Override
    public BrokerWarehouseSummaryOutputDto get(String id) {
        BrokerWarehouseSummaryOutputDto orionEntity = reader.readObject(id);

        List<WarehouseMaterial> workorders = materialReader.readOrionEntityQueryListRelationship(WarehouseField.RELATIONSHIP_WAREHOUSE.getField(),
                orionEntity.getId(), EntityType.WAREHOUSE_MATERIAL.getType());
        if (!workorders.isEmpty()) {
            orionEntity.setMaterials(workorders.stream().map(WarehouseMaterial::getId).collect(Collectors.toList()));
        }

        return orionEntity;
    }

	public BrokerWarehouseSummaryOutputDto getByExternalId(String id) {
        List<Warehouse> warehouses = reader.readOrionEntityQueryListRelationship(WarehouseField.ERP_EXTERNAL_ID.getField(),
                new StringBuilder(Constant.ID).append(id).toString(), EntityType.WAREHOUSE.getType());
        if (warehouses == null || warehouses.isEmpty()) {
        	throw new WarehouseNotFoundException();
        }
        return reader.mapOrionEntityToEntity(warehouses.get(0));
	}
}
