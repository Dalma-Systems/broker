package com.dalma.broker.fiware.orion.connector.entity.attribute;

import com.dalma.broker.fiware.orion.connector.entity.common.OrionDateTime;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrionAttributeMetadata implements Serializable {
    private static final long serialVersionUID = -19587484696396230L;

    private OrionDateTime dateModified;
    private OrionBaseAttribute<String> context;
}
