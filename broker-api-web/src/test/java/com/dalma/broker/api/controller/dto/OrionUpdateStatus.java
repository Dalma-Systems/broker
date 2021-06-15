package com.dalma.broker.api.controller.dto;

import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrionUpdateStatus {
    private OrionAttribute<String> status;
}
