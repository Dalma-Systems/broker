package com.dalma.broker.fiware.orion.reader;

import com.dalma.broker.fiware.orion.connector.OrionConnector;
import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.dalma.common.util.Constant.EQUAL;
import static com.dalma.common.util.Constant.GREATER_EQUAL;
import static com.dalma.common.util.Constant.LESS_EQUAL;
import static com.dalma.common.util.Constant.SEMICOLON;

public abstract class DefaultOrionReader<T, O extends OrionEntity> implements OrionReader<T, O> {

    protected OrionConnector<O> orionConnector;

    public DefaultOrionReader(OrionConnectorConfiguration config, Class<O> clazz) {
        super();
        this.orionConnector = new OrionConnector<O>(config, clazz) {
        };
    }

    @Override
    public T readObject(String id) {
        O orionEntity = orionConnector.readEntity(id);
        return mapOrionEntityToEntity(orionEntity);
    }

    @Override
    public O readOrionEntity(String id) {
        return orionConnector.readEntity(id);
    }

    @Override
    public List<T> readObjectList(String type) {
        List<O> orionEntityList = readOrionEntityList(type);
        List<T> result = new ArrayList<>();

        for (O orionEntity : orionEntityList) {
            result.add(mapOrionEntityToEntity(orionEntity));
        }
        return result;
    }

    @Override
    public List<O> readOrionEntityList(String type) {
        return orionConnector.readEntityList(type);
    }

    @Override
    public List<T> readObjectQueryListRelationship(String refField, String refId, String relatedType) {
        List<O> orionEntityList = readOrionEntityQueryListRelationship(refField, refId, relatedType);
        List<T> result = new ArrayList<>();

        for (O orionEntity : orionEntityList) {
            result.add(mapOrionEntityToEntity(orionEntity));
        }
        return result;
    }

    @Override
    public List<O> readOrionEntityQueryListRelationship(String refField, String refId, String relatedType) {
        return orionConnector.readEntityQueryList(relatedType,
                new StringBuilder(refField).append(EQUAL).append(EQUAL).append(refId).toString());
    }

    @Override
    public List<O> readOrionEntityQueryListRelationship(List<String> refField, List<String> refId, String relatedType) {
    	if (refField == null || refId == null || refField.size() != refId.size()) {
    		return Collections.emptyList();
    	}
    	
    	StringBuilder query = new StringBuilder();
    	int numberFields = refField.size();
    	for (int index = 0; index < numberFields; index++) {
    		query.append(refField.get(index)).append(EQUAL).append(EQUAL).append(refId.get(index)).append(SEMICOLON);
		}
    	
        return orionConnector.readEntityQueryList(relatedType, query.substring(0, query.length() - 1));
    }
    
    @Override
    public List<T> readObjectQueryListBetween(String refField, String start, String end, String relatedType) {
        List<O> orionEntityList = readOrionEntityQueryListBetween(refField, start, end, relatedType);
        List<T> result = new ArrayList<>();

        for (O orionEntity : orionEntityList) {
            result.add(mapOrionEntityToEntity(orionEntity));
        }
        return result;
    }

    @Override
    public List<O> readOrionEntityQueryListBetween(String refField, String start, String end, String relatedType) {
        return orionConnector.readEntityQueryList(relatedType, URLEncoder.encode(
                new StringBuilder(refField).append(GREATER_EQUAL).append(start).append(SEMICOLON).append(refField)
                        .append(LESS_EQUAL).append(end).toString(), StandardCharsets.UTF_8));
    }
    
    @Override
    public O readOrionSubscription(String id) {
        return orionConnector.readSubscription(id);
    }

    @Override
	public List<O> readOrionSubscriptionList() {
        return orionConnector.readSubscriptionList();
	}
}
