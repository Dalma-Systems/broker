package com.dalma.broker.api.controller;

import com.dalma.broker.contract.dto.warehouse.material.BrokerWarehouseMaterialSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterial;
import com.dalma.broker.service.WarehouseMaterialService;
import com.dalma.broker.service.publisher.WarehouseMaterialPublisher;
import com.dalma.broker.service.reader.WarehouseMaterialReader;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialOutputDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.MATERIAL;
import static com.dalma.broker.contract.Paths.WAREHOUSE;

@Validated
@RestController
@RequestMapping(BASE_PATH + WAREHOUSE + MATERIAL)
public class WarehouseMaterialController extends
        BaseOrionCrudController<BrokerWarehouseMaterialInputDto, BrokerWarehouseMaterialOutputDto, BrokerWarehouseMaterialSummaryOutputDto, WarehouseMaterial, WarehouseMaterialPublisher, WarehouseMaterialReader, WarehouseMaterialService> {

    public WarehouseMaterialController(WarehouseMaterialService warehouseService) {
        super(warehouseService);
    }
    
    @Override
    @ApiOperation(value = "Creates a new material in Orion")
    public BrokerWarehouseMaterialOutputDto create(@RequestBody BrokerWarehouseMaterialInputDto input) {
        return super.create(input);
    }

    @Override
    @ApiOperation(value = "Get all materials from Orion")
    public List<BrokerWarehouseMaterialSummaryOutputDto> getAll() {
        return super.getAll();
    }

    @Override
    @ApiOperation(value = "Get a specific material from Orion")
    public BrokerWarehouseMaterialSummaryOutputDto get(@PathVariable(Constant.ID) String id) {
        return super.get(id);
    }

    @Override
    @ApiOperation(value = "Delete a specific material from Orion")
    public void delete(@PathVariable(Constant.ID) String id) {
    	super.delete(id);
    }

    @Override
    @ApiOperation(value = "Updates a specific material in Orion", notes = "It is updated only the parameters received.")
    public void update(@PathVariable(Constant.ID) String id, @RequestBody BrokerWarehouseMaterialInputDto input) {
    	super.update(id, input);
    }
}
