package com.dalma.broker.fiware.orion.connector.entity.subscription;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SubscriptionCondition implements Serializable {

    private static final long serialVersionUID = 2754642012171110784L;

    private List<String> attrs;
}
