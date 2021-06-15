package com.dalma.broker.fiware.orion.connector.entity;

public interface OrionEntity {
    public String getId();

    public String getType();

    public void setId(String id);

    public void setType(String type);
}
