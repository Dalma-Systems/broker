package com.dalma.broker.service.notification;

import com.dalma.broker.contract.dto.subscription.BrokerSubscriptionFibrewNotificationInputDto;
import com.dalma.broker.contract.dto.subscription.BrokerSubscriptionLatteNotificationInputDto;
import com.dalma.broker.service.WorkOrderService;
import com.dalma.broker.service.WorkStationService;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.work.order.BrokerWorkOrderSummaryOutputDto;
import com.dalma.contract.dto.work.order.notification.WorkOrderFibrewNotificationIntegration;
import com.dalma.contract.dto.work.order.notification.WorkOrderFibrewNotificationIntegration.WorkOrderFibrewNotificationMaterialIntegration;
import com.dalma.contract.dto.work.order.notification.WorkOrderLatteNotificationIntegration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class IntegrationNotificationHandler {
	private final DateFormat dateFormatter = new SimpleDateFormat(Constant.SCHEDULE_FORMAT);

	private final LatteBridge latteBridge;
	private final FibrewBridge fibrewBridge;
	private final ObjectMapper objectMapper;
	private final WorkOrderService workOrderService;
	private final WorkStationService workStationService;
	
	public IntegrationNotificationHandler(LatteBridge latteBridge, FibrewBridge fibrewBridge, ObjectMapper objectMapper, WorkOrderService workOrderService, WorkStationService workStationService) {
		this.latteBridge = latteBridge;
		this.fibrewBridge = fibrewBridge;
		this.objectMapper = objectMapper;
		this.workOrderService = workOrderService;
		this.workStationService = workStationService;
	}
	
	public void notifyIntegrationLatte(BrokerSubscriptionLatteNotificationInputDto notificationInputDto) {
		if (notificationInputDto.getData() == null) {
			return;
		}
		
		notificationInputDto.getData().stream().forEach(order -> {
			try {
				WorkOrderLatteNotificationIntegration integrationData = new WorkOrderLatteNotificationIntegration();
				integrationData.setId(order.getId());
				integrationData.setDate(dateFormatter.parse(order.getScheduledAt().getValue()));
				if (order.getScheduleIds() != null) {
					integrationData.setScheduleIds(order.getScheduleIds().getValue());
				}			
				latteBridge.postToLatte(objectMapper.writeValueAsString(integrationData), LatteApiPath.WORK_ORDER_LATTE_INTEGRAGE_NOTIFICATION, null);
			} catch (ParseException | JsonProcessingException e) {
				log.error(MessageFormat.format("Error notifying Latte about entity change {0}", order.getId()), e);
			}
		});
	}

	public void notifyIntegrationFibrew(BrokerSubscriptionFibrewNotificationInputDto notificationInputDto) {
		if (notificationInputDto.getData() == null) {
			return;
		}
		
		notificationInputDto.getData().stream().forEach(order -> {
			try {
				BrokerWorkOrderSummaryOutputDto brokerOrder = workOrderService.get(order.getId());
				log.info("Received to integrate in Fi-Brew order {} for {}", brokerOrder.getId(), brokerOrder.getScheduledAt() != null ? brokerOrder.getScheduledAt().getValue() : null);
				
				WorkOrderFibrewNotificationIntegration integrationData = new WorkOrderFibrewNotificationIntegration();
				integrationData.setErpId(brokerOrder.getErpId().getValue());
				integrationData.setDate(dateFormatter.parse(brokerOrder.getScheduledAt().getValue()));
				
				List<WorkOrderFibrewNotificationMaterialIntegration> integrationMaterials = new ArrayList<>(brokerOrder.getMaterials().size());
				brokerOrder.getMaterials().stream().forEach(orderMaterial -> {
					WorkOrderFibrewNotificationMaterialIntegration material = new WorkOrderFibrewNotificationMaterialIntegration();
					material.setMaterialId(orderMaterial.getErpId().getValue());
					material.setBatchNumber(orderMaterial.getBatch().getValue());
					material.setQuantity(BigDecimal.valueOf(orderMaterial.getQuantity().getValue()));
					integrationMaterials.add(material);
				});
				integrationData.setMaterials(integrationMaterials);
				
				log.info("Integrating in Fi-Brew order {} for {}", integrationData.getErpId(), integrationData.getDate());
				integrationData.setWorkstation(workStationService.get(brokerOrder.getWorkstationId().getValue()).getName().getValue());
				fibrewBridge.postToFibrew(objectMapper.writeValueAsString(integrationData), FibrewApiPath.WORK_ORDER_INTEGRAGE_NOTIFICATION);
			} catch (ParseException | JsonProcessingException e) {
				log.error(MessageFormat.format("Error notifying Fibrew about entity change {0}", order.getId()), e);
			}
		});
	}
}
