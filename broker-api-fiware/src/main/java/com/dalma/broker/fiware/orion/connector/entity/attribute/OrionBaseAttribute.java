package com.dalma.broker.fiware.orion.connector.entity.attribute;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrionBaseAttribute<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -1179289180783501527L;

    private String type;
    private T value;
}
