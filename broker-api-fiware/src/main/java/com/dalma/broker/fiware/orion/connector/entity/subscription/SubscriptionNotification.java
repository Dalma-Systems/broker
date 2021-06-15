package com.dalma.broker.fiware.orion.connector.entity.subscription;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SubscriptionNotification implements Serializable {

    private static final long serialVersionUID = 7507775021621021744L;

    private SubscriptionNotificationUrl http;
    private List<String> attrs;
}
