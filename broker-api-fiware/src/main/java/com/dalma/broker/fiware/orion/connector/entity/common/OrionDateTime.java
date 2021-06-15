package com.dalma.broker.fiware.orion.connector.entity.common;

import lombok.Setter;
import com.dalma.broker.fiware.orion.connector.entity.OrionEntityType;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Setter
public class OrionDateTime implements Serializable {

    private static final long serialVersionUID = -7499692827323248352L;

    private String type = OrionEntityType.DATE_TIME.getType();
    private String value;
}
