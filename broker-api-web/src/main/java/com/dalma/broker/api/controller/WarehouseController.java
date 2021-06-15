package com.dalma.broker.api.controller;

import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseInputDto;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.Warehouse;
import com.dalma.broker.service.WarehouseMaterialService;
import com.dalma.broker.service.WarehouseService;
import com.dalma.broker.service.exception.warehouse.WarehouseUpdateNotSupportedException;
import com.dalma.broker.service.publisher.WarehousePublisher;
import com.dalma.broker.service.reader.WarehouseReader;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.warehouse.BrokerWarehouseSummaryOutputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialOutputDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.BY_ID;
import static com.dalma.broker.contract.Paths.EXTERNAL;
import static com.dalma.broker.contract.Paths.MATERIAL;
import static com.dalma.broker.contract.Paths.WAREHOUSE;

@Validated
@RestController
@RequestMapping(BASE_PATH + WAREHOUSE)
public class WarehouseController extends
        BaseOrionCrudController<BrokerWarehouseInputDto, BrokerWarehouseOutputDto, BrokerWarehouseSummaryOutputDto, Warehouse, WarehousePublisher, WarehouseReader, WarehouseService> {
    private final WarehouseMaterialService warehouseMaterialService;
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService, WarehouseMaterialService warehouseMaterialService) {
        super(warehouseService);
        this.warehouseMaterialService = warehouseMaterialService;
        this.warehouseService = warehouseService;
    }

    @Override
    @ApiOperation(value = "Updates the content of a specific warehouse in Orion", notes = "It is updated only the parameters received. It is "
    		+ "not supported update the materials of the warehouse.")
    public void update(@PathVariable(Constant.ID) String id, @RequestBody BrokerWarehouseInputDto input) {
        if (input.getMaterials() != null) {
            throw new WarehouseUpdateNotSupportedException();
        }
        super.update(id, input);
    }

    @PostMapping(BY_ID + MATERIAL)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Creates a new material inside a warehouse")
    public BrokerWarehouseMaterialOutputDto create(@RequestBody BrokerWarehouseMaterialInputDto input) {
        return warehouseMaterialService.createMaterialInWarehouse(input);
    }

    @GetMapping(EXTERNAL + BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a specific warehouse from Orion by the id used in SAP")
    public BrokerWarehouseSummaryOutputDto getByExternalId(@PathVariable(Constant.ID) String id) {
        return warehouseService.getByExternalId(id);
    }
    
    @Override
    @ApiOperation(value = "Creates a new warehouse in Orion")
    public BrokerWarehouseOutputDto create(@RequestBody BrokerWarehouseInputDto input) {
        return super.create(input);
    }

    @Override
    @ApiOperation(value = "Get all warehouses from Orion")
    public List<BrokerWarehouseSummaryOutputDto> getAll() {
        return super.getAll();
    }

    @Override
    @ApiOperation(value = "Get a specific warehouse from Orion")
    public BrokerWarehouseSummaryOutputDto get(@PathVariable(Constant.ID) String id) {
        return super.get(id);
    }

    @Override
    @ApiOperation(value = "Delete a specific warehouse from Orion")
    public void delete(@PathVariable(Constant.ID) String id) {
    	super.delete(id);
    }
}
