package com.dalma.broker.fiware.orion.reader;

import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;

import java.util.List;

public interface OrionReader<T, O extends OrionEntity> {
    public T readObject(String id);

    public O readOrionEntity(String id);

    public T mapOrionEntityToEntity(O type);

    public List<T> readObjectList(String type);

    public List<O> readOrionEntityList(String type);

    public List<T> readObjectQueryListRelationship(String refField, String refId, String relatedType);

    public List<O> readOrionEntityQueryListRelationship(String refField, String refId, String relatedType);

    public List<O> readOrionEntityQueryListRelationship(List<String> refField, List<String> refId, String relatedType);

    public List<T> readObjectQueryListBetween(String refField, String start, String end, String relatedType);

    public List<O> readOrionEntityQueryListBetween(String refField, String start, String end, String relatedType);
    
    public O readOrionSubscription(String id);

	public List<O> readOrionSubscriptionList();
}
