package com.dalma.broker.fiware.orion.connector.entity.station;

import com.dalma.broker.fiware.orion.connector.entity.BaseOrionEntity;
import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionLocationAttribute;
import com.dalma.broker.fiware.orion.connector.entity.common.LocationPoint;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class Station extends BaseOrionEntity implements OrionEntity, Serializable {

    private static final long serialVersionUID = 5355120025047816565L;

    private OrionLocationAttribute<LocationPoint> location;
    private OrionAttribute<String> name;
    private OrionAttribute<String> status;
}
