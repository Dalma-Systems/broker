package com.dalma.broker.service.notification;

import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationBatteryData;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationHeartbeatData;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationInputDto;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationLocationData;
import com.dalma.contract.dto.robot.notification.BrokerRobotNotificationStatusData;
import com.dalma.contract.dto.robot.notification.RobotNotificationBatteryDto;
import com.dalma.contract.dto.robot.notification.RobotNotificationHeartbeatDto;
import com.dalma.contract.dto.robot.notification.RobotNotificationLocationDto;
import com.dalma.contract.dto.robot.notification.RobotNotificationStatusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RobotNotificationHandler {

	private final LatteBridge latteBridge;
	private final ObjectMapper objectMapper;

	public RobotNotificationHandler(LatteBridge latteBridge, ObjectMapper objectMapper) {
		this.latteBridge = latteBridge;
		this.objectMapper = objectMapper;
	}

	public void handleNotification(BrokerRobotNotificationInputDto status) {
		try {
			if (status.getData().get(0).getBattery() != null) {
				handleBatteryChange(status.getData().get(0).getId(), status.getData().get(0).getBattery());
			}
			if (status.getData().get(0).getStatus() != null) {
				handleStatusChange(status.getData().get(0).getId(), status.getData().get(0).getStatus());
			}
			if (status.getData().get(0).getLocation() != null) {
				handleLocationChange(status.getData().get(0).getId(), status.getData().get(0).getLocation());
			}
			if (status.getData().get(0).getHeartbeat() != null) {
				handleHeartbeatChange(status.getData().get(0).getId(), status.getData().get(0).getHeartbeat());
			}
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void handleHeartbeatChange(String id, BrokerRobotNotificationHeartbeatData heartbeat) throws JsonProcessingException {
		RobotNotificationHeartbeatDto request = new RobotNotificationHeartbeatDto();
		request.setValue(heartbeat.getValue());
		latteBridge.postToLatte(objectMapper.writeValueAsString(request), LatteApiPath.ROBOT_HEARTBEAT_NOTIFICATION, id);
	}

	private void handleLocationChange(String id, BrokerRobotNotificationLocationData location)
			throws JsonProcessingException {
		log.debug("Received notification id {} about position change to ({},{})", id,
				location.getValue().getCoordinates()[1], location.getValue().getCoordinates()[0]);
		RobotNotificationLocationDto request = new RobotNotificationLocationDto(location.getValue().getCoordinates()[0],
				location.getValue().getCoordinates()[1]);
		latteBridge.postToLatte(objectMapper.writeValueAsString(request), LatteApiPath.ROBOT_LOCATION_NOTIFICATION, id);
	}

	private void handleStatusChange(String id, BrokerRobotNotificationStatusData status)
			throws JsonProcessingException {
		log.info("Received notification id {} about status change to {}", id, status.getValue());
		RobotNotificationStatusDto request = new RobotNotificationStatusDto();
		request.setValue(status.getValue());
		request.setContextId(status.getMetadata() != null && status.getMetadata().getContext() != null ? status.getMetadata().getContext().getValue() : null);
		latteBridge.postToLatte(objectMapper.writeValueAsString(request), LatteApiPath.ROBOT_STATUS_NOTIFICATION, id);
	}

	private void handleBatteryChange(String id, BrokerRobotNotificationBatteryData battery)
			throws JsonProcessingException {
		log.debug("Received notification id {} about battery change to {}", id, battery.getValue());
		RobotNotificationBatteryDto request = new RobotNotificationBatteryDto(battery.getValue());
		latteBridge.postToLatte(objectMapper.writeValueAsString(request), LatteApiPath.ROBOT_BATTERY_NOTIFICATION, id);
	}
}
