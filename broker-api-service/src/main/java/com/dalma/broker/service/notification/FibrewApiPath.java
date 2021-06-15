package com.dalma.broker.service.notification;

import lombok.Getter;

@Getter
public enum FibrewApiPath {
	WORK_ORDER_INTEGRAGE_NOTIFICATION("/api/fibrew/notification/workorder/integrate"),
	;

	private String path;

	private FibrewApiPath(String path) {
		this.path = path;
	}
}
