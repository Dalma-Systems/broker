package com.dalma.broker.api.controller;

import com.dalma.broker.contract.dto.station.work.BrokerWorkStationInputDto;
import com.dalma.broker.contract.dto.station.work.BrokerWorkStationOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.station.work.WorkStation;
import com.dalma.broker.service.WorkStationService;
import com.dalma.broker.service.publisher.WorkStationPublisher;
import com.dalma.broker.service.reader.WorkStationReader;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.station.work.BrokerWorkStationSummaryOutputDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.BY_ID;
import static com.dalma.broker.contract.Paths.EXTERNAL;
import static com.dalma.broker.contract.Paths.STATION;
import static com.dalma.broker.contract.Paths.WORK_STATION;

@Validated
@RestController
@RequestMapping(BASE_PATH + STATION + WORK_STATION)
public class WorkStationController extends
        BaseOrionCrudController<BrokerWorkStationInputDto, BrokerWorkStationOutputDto, BrokerWorkStationSummaryOutputDto, WorkStation, WorkStationPublisher, WorkStationReader, WorkStationService> {
    private final WorkStationService workstationService;

    public WorkStationController(WorkStationService workstationService) {
        super(workstationService);
        this.workstationService = workstationService;
    }

    @GetMapping(EXTERNAL + BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a specific work station from Orion by SAP id")
    public BrokerWorkStationSummaryOutputDto getByExternalId(@PathVariable(Constant.ID) String id) {
        return workstationService.getByExternalId(id);
    }
    
    @Override
    @ApiOperation(value = "Creates a new work station in Orion")
    public BrokerWorkStationOutputDto create(@RequestBody BrokerWorkStationInputDto input) {
        return super.create(input);
    }

    @Override
    @ApiOperation(value = "Get all work stations from Orion")
    public List<BrokerWorkStationSummaryOutputDto> getAll() {
        return super.getAll();
    }

    @Override
    @ApiOperation(value = "Get a specific work station from Orion")
    public BrokerWorkStationSummaryOutputDto get(@PathVariable(Constant.ID) String id) {
        return super.get(id);
    }

    @Override
    @ApiOperation(value = "Delete a specific work station from Orion")
    public void delete(@PathVariable(Constant.ID) String id) {
    	super.delete(id);
    }

    @Override
    @ApiOperation(value = "Updates the content of a specific work station in Orion", notes = "It is updated only the parameters received. If it "
    		+ "is to change something related to position it is necessary to send latitude, longitude and angle.")
    public void update(@PathVariable(Constant.ID) String id, @RequestBody BrokerWorkStationInputDto input) {
        super.update(id, input);
    }
}
