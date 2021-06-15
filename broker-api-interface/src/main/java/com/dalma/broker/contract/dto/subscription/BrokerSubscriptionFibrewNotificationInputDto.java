package com.dalma.broker.contract.dto.subscription;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import java.util.List;

@Getter
@Setter
public class BrokerSubscriptionFibrewNotificationInputDto {
    @NotBlank
	private String subscriptionId;

    @NotBlank
    private List<BrokerSubscriptionFibrewNotificationDataInputDto> data;
	
    @Getter
    @Setter
	public static class BrokerSubscriptionFibrewNotificationDataInputDto {

	    @NotBlank
	    private String id;
	}
}
