package com.dalma.broker.fiware.orion.connector.entity.attribute;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrionLocationAttribute<T extends Serializable> extends OrionBaseAttribute<T> implements Serializable {

    private static final long serialVersionUID = 7573833164273625004L;

    private OrionLocationAttributeMetadata metadata;
}
