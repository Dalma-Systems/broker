package com.dalma.broker.api.controller;

import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationInputDto;
import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationOutputDto;
import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStation;
import com.dalma.broker.service.IdleStationService;
import com.dalma.broker.service.publisher.IdleStationPublisher;
import com.dalma.broker.service.reader.IdleStationReader;
import com.dalma.common.util.Constant;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.IDLE_STATION;
import static com.dalma.broker.contract.Paths.STATION;

@Validated
@RestController
@RequestMapping(BASE_PATH + STATION + IDLE_STATION)
public class IdleStationController extends
        BaseOrionCrudController<BrokerIdleStationInputDto, BrokerIdleStationOutputDto, BrokerIdleStationSummaryOutputDto, IdleStation, IdleStationPublisher, IdleStationReader, IdleStationService> {

    public IdleStationController(IdleStationService idleStationService) {
        super(idleStationService);
    }
    
    @Override
    @ApiOperation(value = "Creates a new idle station in Orion")
    public BrokerIdleStationOutputDto create(@RequestBody BrokerIdleStationInputDto input) {
        return super.create(input);
    }
    
    @Override
    @ApiOperation(value = "Get all idle stations from Orion")
    public List<BrokerIdleStationSummaryOutputDto> getAll() {
        return super.getAll();
    }
    
    @Override
    @ApiOperation(value = "Get a specific idle station from Orion")
    public BrokerIdleStationSummaryOutputDto get(@PathVariable(Constant.ID) String id) {
        return super.get(id);
    }

    @Override
    @ApiOperation(value = "Delete a specific idle station from Orion")
    public void delete(@PathVariable(Constant.ID) String id) {
        super.delete(id);
    }

    @Override
    @ApiOperation(value = "Updates the content of a specific idle station in Orion", notes = "It is updated only the parameters received. If it "
    		+ "is to change something related to position it is necessary to send latitude, longitude and angle.")
    public void update(@PathVariable(Constant.ID) String id, @RequestBody BrokerIdleStationInputDto input) {
        super.update(id, input);
    }
}
