package com.dalma.broker.fiware.orion.connector.entity.attribute;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrionLocationAttributeMetadata extends OrionAttributeMetadata implements Serializable {
    private static final long serialVersionUID = 7283857086831368300L;

    private OrionAttribute<Double> angle;
}
