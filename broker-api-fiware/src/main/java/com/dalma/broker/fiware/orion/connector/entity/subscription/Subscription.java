package com.dalma.broker.fiware.orion.connector.entity.subscription;

import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Subscription implements Serializable, OrionEntity {
    private static final long serialVersionUID = 5613453245717925996L;

    private String id;
    private String description;
    private SubscriptionSubject subject;
    private SubscriptionNotification notification;
    private String expires;
    private Integer throttling;
    private String status;

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setType(String type) {
        // Subscription does not have any type associated
    }
}
