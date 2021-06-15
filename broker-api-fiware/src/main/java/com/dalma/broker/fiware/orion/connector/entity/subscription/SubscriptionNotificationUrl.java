package com.dalma.broker.fiware.orion.connector.entity.subscription;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubscriptionNotificationUrl implements Serializable {
    private static final long serialVersionUID = 4037129761327855781L;

    private String url;
}
