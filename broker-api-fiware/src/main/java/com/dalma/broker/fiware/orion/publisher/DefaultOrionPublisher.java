package com.dalma.broker.fiware.orion.publisher;

import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.BaseOrionEntity;
import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;

import java.util.List;

public abstract class DefaultOrionPublisher<T, O extends OrionEntity> implements OrionPublisher<T, O> {
    protected OrionConnector<O> orionConnector;

    public DefaultOrionPublisher(OrionConnectorConfiguration config, Class<O> clazz) {
        this.orionConnector = new OrionConnector<O>(config, clazz) {
        };
    }

    @Override
    public O create(T entity) {
        O orionEntity = mapEntityIdAndDate(mapEntityToOrionEntity(entity));
        ((BaseOrionEntity) orionEntity).setDateCreated(((BaseOrionEntity) orionEntity).getDateModified());
        return create(orionEntity);
    }

    @Override
    public O create(O orionEntity) {
        orionConnector.createNewEntity(orionEntity, false);
        return orionEntity;
    }

    @Override
    public O subscribe(T entity) {
        O orionEntity = mapEntityToOrionEntity(entity);
        return subscribe(orionEntity);
    }

    @Override
    public O subscribe(O orionEntity) {
        orionConnector.createNewSubscription(orionEntity, true);
        return orionEntity;
    }

    @Override
    public void update(T entity, String id) {
        O orionEntity = mapEntityToOrionEntity(entity);
        update(orionEntity, id);
    }

    @Override
    public void update(O orionEntity, String id) {
        orionConnector.updateEntity(orionEntity, id, false);
    }

    @Override
    public void delete(T entity) {
        OrionEntity entity2Delete = mapEntityToOrionEntity(entity);
        delete(entity2Delete.getId());
    }

    @Override
    public void delete(O orionEntity) {
        delete(orionEntity.getId());
    }

    @Override
    public void delete(String id) {
        orionConnector.deleteOneEntity(id);
    }

    @Override
    public void deleteSubscription(String id) {
        orionConnector.deleteSubscription(id);
    }

    @Override
    public void append(O orionEntity, String id) {
        append(orionEntity, List.of(id));
    }

    @Override
    public void append(O orionEntity, List<String> ids) {
    	append(orionEntity, ids, null);
    }
    
    @Override
    public void append(O orionEntity, List<String> ids, String contextId) {
        OrionRelationship<? extends OrionRelationshipEntity> orionEntityRelationship = mapEntityToOrionRelationshipEntity(
                orionEntity, ids, contextId);
        orionConnector.append(orionEntityRelationship);
    }

    protected abstract O mapEntityIdAndDate(O mapEntityToOrionEntity);
}
