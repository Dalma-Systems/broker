package com.dalma.broker.fiware.orion.connector.entity;

import com.dalma.broker.fiware.orion.connector.entity.common.OrionDateTime;
import com.dalma.common.util.Constant;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public abstract class BaseOrionEntity {
    private String id;
    private String type = getEntityType();
    private OrionDateTime dateModified = initDate();
    private OrionDateTime dateCreated;

    public abstract String getEntityType();

    private OrionDateTime initDate() {
        OrionDateTime date = new OrionDateTime();
        date.setValue(new SimpleDateFormat(Constant.ORION_DATE_FORMAT).format(new Date()));
        return date;
    }
}
