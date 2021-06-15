package com.dalma.broker.fiware.orion.connector.entity.subscription;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SubscriptionSubject implements Serializable {

    private static final long serialVersionUID = 1280785446225048668L;

    private List<SubscriptionEntity> entities;
    private SubscriptionCondition condition;
}
