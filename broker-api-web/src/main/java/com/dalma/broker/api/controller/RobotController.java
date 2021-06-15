package com.dalma.broker.api.controller;

import com.dalma.broker.fiware.orion.connector.entity.robot.Robot;
import com.dalma.broker.service.RobotService;
import com.dalma.broker.service.publisher.RobotPublisher;
import com.dalma.broker.service.reader.RobotReader;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotSummaryOutputDto;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationInputDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.NOTIFICATION;
import static com.dalma.broker.contract.Paths.ROBOT;
import static com.dalma.broker.contract.Paths.STATUS;

@Validated
@RestController
@RequestMapping(BASE_PATH + ROBOT)
public class RobotController extends
        BaseOrionCrudController<BrokerRobotInputDto, BrokerRobotOutputDto, BrokerRobotSummaryOutputDto, Robot, RobotPublisher, RobotReader, RobotService> {

    private final RobotService robotService;

    public RobotController(RobotService robotService) {
        super(robotService);
        this.robotService = robotService;
    }

    @PostMapping(NOTIFICATION + STATUS)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Subscription callback for robot atributes", notes = "This endpoint is used in API Orion callbacks for all wanted "
    		+ "attributes: \"battery\", \"status\", \"location\" and \"heartbeat\"")
    public void notificationStatus(@RequestBody BrokerRobotNotificationInputDto status) {
        robotService.notificationStatus(status);
    }
    
    @Override
    @ApiOperation(value = "Creates a new robot in Orion")
    public BrokerRobotOutputDto create(@RequestBody BrokerRobotInputDto input) {
        return super.create(input);
    }

    @Override
    @ApiOperation(value = "Get all robots from Orion")
    public List<BrokerRobotSummaryOutputDto> getAll() {
        return super.getAll();
    }

    @Override
    @ApiOperation(value = "Get a specific robot from Orion")
    public BrokerRobotSummaryOutputDto get(@PathVariable(Constant.ID) String id) {
        return super.get(id);
    }

    @Override
    @ApiOperation(value = "Delete a specific robot from Orion")
    public void delete(@PathVariable(Constant.ID) String id) {
    	super.delete(id);
    }

    @Override
    @ApiOperation(value = "Updates the content of a specific robot in Orion", notes = "It is updated only the parameters received. If it "
    		+ "is to change something related to position it is necessary to send latitude, longitude and angle.")
    public void update(@PathVariable(Constant.ID) String id, @RequestBody BrokerRobotInputDto input) {
    	super.update(id, input);
    }
}
