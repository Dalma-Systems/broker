package com.dalma.broker.api.controller;

import com.dalma.broker.contract.dto.subscription.BrokerSubscriptionFibrewNotificationInputDto;
import com.dalma.broker.contract.dto.subscription.BrokerSubscriptionLatteNotificationInputDto;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrder;
import com.dalma.broker.service.WorkOrderService;
import com.dalma.broker.service.exception.workorder.WorkOrderUpdateNotSupportedException;
import com.dalma.broker.service.notification.IntegrationNotificationHandler;
import com.dalma.broker.service.publisher.WorkOrderPublisher;
import com.dalma.broker.service.reader.WorkOrderReader;
import com.dalma.common.util.Constant;
import com.dalma.common.workorder.enums.WorkOrderStatus;
import com.dalma.contract.dto.work.order.BrokerWorkOrderInputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderIntegrateOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderMaterialsSummaryOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderOutputDto;
import com.dalma.contract.dto.work.order.BrokerWorkOrderSummaryOutputDto;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dalma.broker.contract.Paths.APPLICATION_FIBREW;
import static com.dalma.broker.contract.Paths.APPLICATION_LATTE;
import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.BY_ID;
import static com.dalma.broker.contract.Paths.EXTERNAL;
import static com.dalma.broker.contract.Paths.FILTER;
import static com.dalma.broker.contract.Paths.INTEGRATE;
import static com.dalma.broker.contract.Paths.MATERIAL;
import static com.dalma.broker.contract.Paths.NOTIFICATION;
import static com.dalma.broker.contract.Paths.WORKORDER;
import static com.dalma.common.util.Constant.END;
import static com.dalma.common.util.Constant.START;

@Validated
@RestController
@RequestMapping(BASE_PATH + WORKORDER)
public class WorkOrderController extends
        BaseOrionCrudController<BrokerWorkOrderInputDto, BrokerWorkOrderOutputDto, BrokerWorkOrderSummaryOutputDto, WorkOrder, WorkOrderPublisher, WorkOrderReader, WorkOrderService> {
    private final WorkOrderService workorderService;
    private final IntegrationNotificationHandler integrationNotificationHandler;

    public WorkOrderController(WorkOrderService workorderService, IntegrationNotificationHandler integrationNotificationHandler) {
        super(workorderService);
        this.workorderService = workorderService;
        this.integrationNotificationHandler = integrationNotificationHandler;
    }

    @Override
    @ApiOperation(value = "Creates a new work order in Orion", notes = "If the order does not have a status, it is assumed the status \"scheduled\"")
    public BrokerWorkOrderOutputDto create(@RequestBody BrokerWorkOrderInputDto input) {
        if (Strings.isEmpty(input.getStatus())) {
            input.setStatus(WorkOrderStatus.SCHEDULED.getStatus());
        }
        return super.create(input);
    }

    @Override
    @ApiOperation(value = "Updates the content of a specific work order in Orion", notes = "It is not supported update the materials of the work order.")
    public void update(@PathVariable(Constant.ID) String id, @RequestBody BrokerWorkOrderInputDto input) {
        if (input.getMaterials() != null) {
            throw new WorkOrderUpdateNotSupportedException();
        }
        super.update(id, input);
    }
    
    @Override
    @ApiOperation(value = "Get all work orders from Orion")
    public List<BrokerWorkOrderSummaryOutputDto> getAll() {
        return super.getAll();
    }

    @Override
    @ApiOperation(value = "Get a specific work order from Orion")
    public BrokerWorkOrderSummaryOutputDto get(@PathVariable(Constant.ID) String id) {
        return super.get(id);
    }

    @Override
    @ApiOperation(value = "Delete a specific work order from Orion")
    public void delete(@PathVariable(Constant.ID) String id) {
    	super.delete(id);
    }

    @PostMapping(INTEGRATE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Integrates work orders in Orion", notes = "This is the endpoint called by CoFFEE during SAP integration to insert all orders in Orion.")
    public List<BrokerWorkOrderIntegrateOutputDto> integrate(@RequestBody List<BrokerWorkOrderInputDto> input) {
        return workorderService.integrate(input);
    }
    
    @GetMapping(FILTER)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get filtered work orders from Orion", notes = "Get all the work orders that have the scheduled date bewteen the start and end hour received")
    public List<BrokerWorkOrderSummaryOutputDto> getAll(@RequestParam(START) String start, @RequestParam(END) String end) {
        return workorderService.getAll(start, end);
    }

    @GetMapping(EXTERNAL + BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a specific work order from Orion by SAP order id and by scheduled date")
    public BrokerWorkOrderSummaryOutputDto getByExternalId(@PathVariable(Constant.ID) String id, @RequestParam(value = "scheduledAt", required = true) String scheduledAt) {
        return workorderService.getByExternalId(id, scheduledAt);
    }
    
    @GetMapping(BY_ID + MATERIAL)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all materials of a specific work order from Orion")
    public List<BrokerWorkOrderMaterialsSummaryOutputDto> getMaterialsById(@PathVariable(Constant.ID) String id) {
        return workorderService.getMaterialsById(id);
    }
    
    @PostMapping(NOTIFICATION + INTEGRATE + APPLICATION_LATTE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Subscription callback to notify LATTE about new orders integrated")
    public void notificationIntegrationLatte(@RequestBody BrokerSubscriptionLatteNotificationInputDto notificationInputDto) {
		integrationNotificationHandler.notifyIntegrationLatte(notificationInputDto);
    }
    
    @PostMapping(NOTIFICATION + INTEGRATE + APPLICATION_FIBREW)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Subscription callback to notify Fi-BREW about new orders integrated")
    public void notificationIntegrationFibrew(@RequestBody BrokerSubscriptionFibrewNotificationInputDto notificationInputDto) {
		integrationNotificationHandler.notifyIntegrationFibrew(notificationInputDto);
    }
}
