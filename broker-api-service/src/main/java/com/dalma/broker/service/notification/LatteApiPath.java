package com.dalma.broker.service.notification;

public enum LatteApiPath {
	ROBOT_STATUS_NOTIFICATION(LatteApiPath.ROBOT_NOTIF_URL + LatteApiPath.ID_MACRO + "/status"),
	ROBOT_BATTERY_NOTIFICATION(LatteApiPath.ROBOT_NOTIF_URL + LatteApiPath.ID_MACRO + "/battery"),
	ROBOT_LOCATION_NOTIFICATION(LatteApiPath.ROBOT_NOTIF_URL + LatteApiPath.ID_MACRO + "/location"),
	ROBOT_HEARTBEAT_NOTIFICATION(LatteApiPath.ROBOT_NOTIF_URL + LatteApiPath.ID_MACRO + "/heartbeat"),
	WORK_ORDER_LATTE_INTEGRAGE_NOTIFICATION(LatteApiPath.LATTE_APPLICATION_NOTIFICATION + "/workorder/integrate"),
	;

	private static final String LATTE_APPLICATION_NOTIFICATION = "/api/latte/notification";
	private static final String ROBOT_NOTIF_URL = LATTE_APPLICATION_NOTIFICATION + "/robot/";
	private static final String ID_MACRO = "{id}";
	private String path;

	private LatteApiPath(String path) {
		this.path = path;
	}

	public String getPath(String id) {
		if (id != null) {
			return path.replace(ID_MACRO, id);
		}
		return path;
	}
}
