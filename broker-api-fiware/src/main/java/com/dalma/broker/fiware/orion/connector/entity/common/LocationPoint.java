package com.dalma.broker.fiware.orion.connector.entity.common;

import com.dalma.broker.fiware.orion.connector.entity.OrionEntityType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LocationPoint implements Serializable {
    private static final long serialVersionUID = 3036247007175252333L;

    private String type = OrionEntityType.POINT.getType();
    private Double[] coordinates;
}
