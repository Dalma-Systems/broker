package com.dalma.broker.service;

import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

import java.util.List;

public abstract class BaseOrionCrudService<I, O, S, E extends OrionEntity, P extends DefaultOrionPublisher<I, E>, R extends DefaultOrionReader<S, E>> {
    private P publisher;
    private R reader;

    public BaseOrionCrudService(P publisher, R reader) {
        this.publisher = publisher;
        this.reader = reader;
    }

    public O create(I entity) {
        E orionEntity = publisher.create(entity);
        return mapOrionEntityToEntity(orionEntity);
    }

    public List<S> getAll() {
        return reader.readObjectList(getType());
    }

    public S get(String id) {
        return reader.readObject(id);
    }

    public void update(String id, I entity) {
        publisher.update(entity, id);
    }

    public void delete(String id) {
        publisher.delete(id);
    }

    protected abstract String getType();

    protected abstract O mapOrionEntityToEntity(E orionEntity);
}
