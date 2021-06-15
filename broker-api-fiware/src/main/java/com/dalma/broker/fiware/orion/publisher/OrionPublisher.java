package com.dalma.broker.fiware.orion.publisher;

import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;

import java.util.List;

public interface OrionPublisher<T, O extends OrionEntity> {

    public O create(T entity);

    public O create(O orionEntity);

    public O subscribe(T entity);

    public O subscribe(O orionEntity);

    public void update(T entity, String id);

    public void update(O orionEntity, String id);

    public void delete(T entity);

    public void delete(O orionEntity);

    public void delete(String id);

    public void deleteSubscription(String id);

    public void append(O orionEntity, String id);

    public void append(O orionEntity, List<String> ids);
    
    public void append(O orionEntity, List<String> ids, String contextId);

    public O mapEntityToOrionEntity(T entity);

    public OrionRelationship<? extends OrionRelationshipEntity> mapEntityToOrionRelationshipEntity(O orionEntity,
                                                                                                   List<String> relatedIds,
                                                                                                   String contextId);
}
