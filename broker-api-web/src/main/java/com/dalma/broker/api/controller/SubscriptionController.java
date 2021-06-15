package com.dalma.broker.api.controller;

import com.dalma.broker.contract.dto.subscription.BrokerSubscriptionOutputDto;
import com.dalma.broker.service.SubscriptionService;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.subscription.BrokerSubscriptionInputDto;
import com.dalma.contract.dto.subscription.BrokerSubscriptionSummaryOutputDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dalma.broker.contract.Paths.BASE_PATH;
import static com.dalma.broker.contract.Paths.BY_ID;
import static com.dalma.broker.contract.Paths.CONDITION;
import static com.dalma.broker.contract.Paths.SUBSCRIPTION;

@Validated
@RestController
@RequestMapping(BASE_PATH + SUBSCRIPTION)
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Creates a new subscription in Orion")
    public BrokerSubscriptionOutputDto createSubscription(@RequestBody BrokerSubscriptionInputDto subscription) {
        return subscriptionService.createSubscription(subscription);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all subscriptions from Orion")
    public List<BrokerSubscriptionSummaryOutputDto> getAll() {
    	return subscriptionService.getAllSubscriptions();
    }
    
    @GetMapping(BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a specific subscription from Orion")
    public BrokerSubscriptionSummaryOutputDto getById(@PathVariable(Constant.ID) String id) {
    	return subscriptionService.getSubscriptionById(id);
    }
    
    @DeleteMapping(BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete a specific subscription from Orion")
    public void delete(@PathVariable(Constant.ID) String id) {
        subscriptionService.deleteSubscription(id);
    }
    
    @GetMapping(CONDITION)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all subscriptions from Orion that have a specific condition attribute")
    public List<BrokerSubscriptionSummaryOutputDto> getByConditionAttributes(@RequestParam("attr") String attr) {
        return subscriptionService.getSubscriptionByConditionAttribute(attr);
    }
}
