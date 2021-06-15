package com.dalma.broker.fiware.orion.connector.entity.subscription;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubscriptionEntity implements Serializable {
    private static final long serialVersionUID = -1709957133019900444L;

    private String id;
    private String idPattern;
    private String type;
}
