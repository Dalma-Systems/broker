package com.dalma.broker.fiware.orion.connector.entity.station.work;

import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.station.Station;
import com.dalma.common.entity.EntityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkStation extends Station {

    private static final long serialVersionUID = 4957359721858603683L;

    private OrionAttribute<String> erpId;

    @Override
    public String getEntityType() {
        return EntityType.WORK_STATION.getType();
    }
}
