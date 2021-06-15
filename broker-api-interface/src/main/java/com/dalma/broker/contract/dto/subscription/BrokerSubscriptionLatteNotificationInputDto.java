package com.dalma.broker.contract.dto.subscription;

import com.dalma.contract.dto.base.BaseOrionAttributeInputDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BrokerSubscriptionLatteNotificationInputDto {
    @NotBlank
	private String subscriptionId;

    @NotBlank
    private List<BrokerSubscriptionLatteNotificationDataInputDto> data;
	
    @Getter
    @Setter
    public static class BrokerSubscriptionLatteNotificationDataInputDto {

    	@NotBlank
	    private String id;

	    @NotBlank
	    private BaseOrionAttributeInputDto<String> scheduledAt;

	    private BaseOrionAttributeInputDto<ArrayList<String>> scheduleIds;
	}
}
