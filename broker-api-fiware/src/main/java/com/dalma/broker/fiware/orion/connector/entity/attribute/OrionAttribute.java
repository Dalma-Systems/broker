package com.dalma.broker.fiware.orion.connector.entity.attribute;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrionAttribute<T extends Serializable> extends OrionBaseAttribute<T> implements Serializable {

    private static final long serialVersionUID = 8636972584470062658L;

    private OrionAttributeMetadata metadata;
}
