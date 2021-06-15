package com.dalma.broker.fiware.orion.connector.entity.work.order.field;

import lombok.Getter;

@Getter
public enum WorkOrderField {
    REF_WORK_ORDER("refWorkorder"),
    SCHEDULED_AT("scheduledAt"),
    ERP_EXTERNAL_ID("erpId"),
    ;

    private String field;

    private WorkOrderField(String field) {
        this.field = field;
    }
}
