package com.dalma.broker.fiware.orion.connector;

import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.common.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Slf4j
public abstract class OrionConnector<T extends OrionEntity> {

    private static final String AND_LIMIT = "&limit=";
	public static final String ATTRS = "/attrs";
    private static final String HEADER_SUBSCRIPTION_ID = "Location";
    private static final String ERROR_MSG = "Could not execute HTTP request";
    public static final String QUERY_TYPE = "?type=";
    private static final String LOG_FIND_ENTITY = "Find entity: {}";
    private static final String HEADER_FIWARE_SERVICE = "Fiware-Service";
    private static final String QUERY = "?";
    private static final String SLASH = "/";
    private static final String API_VERSION = "/v2";
    public static final String ENTITIES_PATH = API_VERSION + "/entities";
    public static final String SUBSCRIPTIONS_PATH = API_VERSION + "/subscriptions";
    private static final String RELATIONSHIPS_PATH = API_VERSION + "/op/update";
    private static final String QUERY_ENTITY_TYPES = API_VERSION + "/types?options=values";
    private static final String KEY_VALUES = "options=keyValues";
    private static final String COUNT_HEADER = "Fiware-Total-Count";

    private OrionConnectorConfiguration config;
    private Type t;
    private URI orionAddr;
    private Gson gson;

    public OrionConnector(OrionConnectorConfiguration config, Class<T> clazz) {
        this.config = config;
        this.t = clazz;
        this.init();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
    }

    private void init() {
        try {
            this.orionAddr = new URIBuilder().setScheme(this.config.getOrionScheme())
                    .setHost(this.config.getOrionHost()).setPort(this.config.getOrionPort()).build();
        } catch (URISyntaxException e) {
            throw new OrionConnectorException("Could not build URI to make a request to Orion", e);
        }
    }

    public List<String> getEntityTypes() {
        String uri = this.orionAddr.toString() + QUERY_ENTITY_TYPES;
        return sendGetRequestStringToOrion(null, uri, HttpStatus.SC_OK);
    }

    public void createNewEntity(T orionEntity) {
        createEntity(orionEntity, false);
    }

    public void createNewEntity(T orionEntity, boolean keyValuesMode) {
        createEntity(orionEntity, keyValuesMode);
    }

    public void createEntity(T orionEntity, boolean keyValuesMode) {
        String uri = this.orionAddr.toString() + ENTITIES_PATH;
        if (keyValuesMode) {
            uri += QUERY + KEY_VALUES;
        }
        sendPostRequestVoidToOrion(orionEntity, uri, HttpStatus.SC_CREATED);
    }

    public T createNewSubscription(T orionEntity) {
        return createSubscription(orionEntity, false);
    }

    public T createNewSubscription(T orionEntity, boolean keyValuesMode) {
        return createSubscription(orionEntity, keyValuesMode);
    }

    public T createSubscription(T orionEntity, boolean keyValuesMode) {
        String uri = this.orionAddr.toString() + SUBSCRIPTIONS_PATH;
        if (keyValuesMode) {
            uri += QUERY + KEY_VALUES;
        }
        HttpResponse httpResponse = sendPostRequestToOrion(gson.toJson(orionEntity), uri);
        checkResponse(httpResponse, HttpStatus.SC_CREATED);
        Header[] header = httpResponse.getHeaders(HEADER_SUBSCRIPTION_ID);
        orionEntity.setId(header[0].getValue().replace(SUBSCRIPTIONS_PATH + Constant.SLASH, Strings.EMPTY));
        return orionEntity;
    }

    public int getEntityCount(String type) {
        log.debug(LOG_FIND_ENTITY, type);
        String uri = this.orionAddr.toString() + ENTITIES_PATH + QUERY_TYPE + type + "&limit=1&options=count";
        return sendGetRequestCountToOrion(null, uri, HttpStatus.SC_OK, true);
    }

    public List<T> readEntityList(String type) {
        log.debug(LOG_FIND_ENTITY, type);
        List<T> entities = new ArrayList<>();
        int count = getEntityCount(type);
        int offset = 0;
        do {
            entities.addAll(readEntityList(type, config.getQueryLimit(), offset));
            offset += config.getQueryLimit();
        } while (offset < count);
        return entities;
    }

    public List<T> readEntityList(String type, int limit, int offset) {
        log.debug("Find entity with offset: {}", offset);
        String uri = this.orionAddr.toString() + ENTITIES_PATH + QUERY_TYPE + type + "&offset=" + offset + AND_LIMIT
                + limit;
        return sendGetRequestGenericEntityToOrion(null, uri, HttpStatus.SC_OK);
    }

    public List<T> readEntityQueryList(String type, String query) {
        log.debug(LOG_FIND_ENTITY, type);
        List<T> entities = new ArrayList<>();
        int count = getEntityCount(type);
        int offset = 0;
        do {
            entities.addAll(readEntityQueryList(type, query, config.getQueryLimit(), offset));
            offset += config.getQueryLimit();
        } while (offset < count);
        return entities;
    }

    public List<T> readEntityQueryList(String type, String query, int limit, int offset) {
        log.debug("Find entity with offset: {}", offset);
        String uri = this.orionAddr.toString() + ENTITIES_PATH + QUERY_TYPE + type + "&offset=" + offset + AND_LIMIT
                + limit + "&q=" + query;
        return sendGetRequestGenericEntityToOrion(null, uri, HttpStatus.SC_OK);
    }

    public void updateEntity(T orionEntity, String id) {
        updateEntity(orionEntity, id, false);
    }

    public void updateEntity(T orionEntity, String id, boolean keyValuesMode) {
        // prepare uri
        String uri = this.orionAddr.toString() + ENTITIES_PATH + SLASH + id + ATTRS + QUERY_TYPE + orionEntity.getType()
                + "&option=append";
        if (keyValuesMode) {
            uri += "&options=keyValues";
        }
        // prepare entity - only parameters to change
        orionEntity.setId(null);
        orionEntity.setType(null);
        sendPostRequestVoidToOrion(orionEntity, uri, HttpStatus.SC_NO_CONTENT);
    }

    public T readEntity(String id) {
        log.debug(LOG_FIND_ENTITY, id);
        String uri = this.orionAddr.toString() + ENTITIES_PATH + SLASH + id;
        List<T> lstgoe = sendGetRequestGenericEntityToOrion(null, uri, HttpStatus.SC_OK);
        return lstgoe.size() == 1 ? lstgoe.get(0) : null;
    }

    public void deleteOneEntity(String id) {
        log.debug(LOG_FIND_ENTITY, id);
        String uri = this.orionAddr.toString() + ENTITIES_PATH + SLASH + id;
        sendDeleteRequestGenericEntityToOrion(uri, HttpStatus.SC_NO_CONTENT);
    }

    public void deleteSubscription(String id) {
        log.debug("Find subscription: {}", id);
        String uri = this.orionAddr.toString() + SUBSCRIPTIONS_PATH + SLASH + id;
        sendDeleteRequestGenericEntityToOrion(uri, HttpStatus.SC_NO_CONTENT);
    }

    private List<String> sendGetRequestStringToOrion(T ctxElement, String uri, int expectedCode) {
        HttpResponse httpResponse = sendGetRequestToOrion(ctxElement, uri);
        checkResponse(httpResponse, expectedCode);
        return getStringFromResponse(httpResponse);
    }

    private List<T> sendGetRequestGenericEntityToOrion(T ctxElement, String uri, int expectedCode) {
        HttpResponse httpResponse = sendGetRequestToOrion(ctxElement, uri);
        checkResponse(httpResponse, expectedCode);
        return getOrionObjFromResponse(httpResponse);
    }

    private void sendPostRequestVoidToOrion(T ctxElement, String uri, int expectedCode) {
        HttpResponse httpResponse = sendPostRequestToOrion(gson.toJson(ctxElement), uri);
        checkResponse(httpResponse, expectedCode);
    }

    private int sendGetRequestCountToOrion(T ctxElement, String uri, int expectedCode, boolean count) {
        HttpResponse httpResponse = sendGetRequestToOrion(ctxElement, uri);
        checkResponse(httpResponse, expectedCode);
        if (count) {
            return getCount(httpResponse);
        }
        return -1;
    }

    private void sendDeleteRequestGenericEntityToOrion(String uri, int expectedCode) {
        HttpResponse httpResponse = sendDeleteRequestToOrion(uri);
        checkResponse(httpResponse, expectedCode);
    }

    private HttpResponse sendPostRequestToOrion(String jsonEntity, String uri) {
        Request req = Request.Post(uri).addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON.getMimeType())
                .socketTimeout(5000);
        return callOrion(jsonEntity, uri, req);
    }

    private HttpResponse callOrion(String jsonEntity, String uri, Request req) {
        if (jsonEntity != null) {
            log.debug("Send request to Orion {}: {}", uri, jsonEntity);
            req.bodyString(jsonEntity, APPLICATION_JSON).connectTimeout(5000);
        }
        if (this.config.getFiwareService() != null && !Strings.EMPTY.equalsIgnoreCase(this.config.getFiwareService())) {
            req.addHeader(HEADER_FIWARE_SERVICE, this.config.getFiwareService());
        }
        Response response;
        try {
            response = req.execute();
        } catch (IOException e) {
            throw new OrionConnectorException(ERROR_MSG, e);
        }
        return getHttpResponse(response);
    }

    private HttpResponse sendGetRequestToOrion(T ctxElement, String uri) {
        Request req = Request.Get(uri).addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON.getMimeType())
                .socketTimeout(5000);
        if (ctxElement != null) {
            return callOrion(gson.toJson(ctxElement), uri, req);
        }
        return callOrion(null, uri, req);
    }

    private HttpResponse sendDeleteRequestToOrion(String uri) {
        Request req = Request.Delete(uri).socketTimeout(5000);
        if (this.config.getFiwareService() != null && !Strings.EMPTY.equalsIgnoreCase(this.config.getFiwareService())) {
            req.addHeader(HEADER_FIWARE_SERVICE, this.config.getFiwareService());
        }
        Response response;
        try {
            response = req.execute();
        } catch (IOException e) {
            throw new OrionConnectorException(ERROR_MSG, e);
        }
        return getHttpResponse(response);
    }

    private HttpResponse getHttpResponse(Response response) {
        HttpResponse httpResponse;
        try {
            httpResponse = response.returnResponse();
        } catch (IOException e) {
            throw new OrionConnectorException("Could not obtain HTTP response", e);
        }
        return httpResponse;
    }

    private void checkResponse(HttpResponse httpResponse, int expected) {
        if (httpResponse.getStatusLine().getStatusCode() != expected) {
            String msg = null;
            try {
                InputStream source = httpResponse.getEntity().getContent();
                Reader reader = new InputStreamReader(source);
                msg = IOUtils.toString(reader);
            } catch (IllegalStateException | IOException e) {
                msg = "No message returned";
                throw new OrionConnectorException("Unknown error on CheckResponse::" + msg, e);
            }
            throw new OrionConnectorException(
                    "Failed with HTTP error code : " + httpResponse.getStatusLine().getStatusCode() + "::" + msg, null);
        }
    }

    private List<String> getStringFromResponse(HttpResponse httpResponse) {
        InputStream source;
        List<String> list = null;
        try {
            source = httpResponse.getEntity().getContent();
        } catch (IllegalStateException | IOException e) {
            throw new OrionConnectorException("Could not obtain entity content from HTTP response", e);
        }
        String ctxResp = null;
        try (Reader reader = new InputStreamReader(source)) {
            ctxResp = IOUtils.toString(reader);
            list = gson.fromJson(ctxResp, new TypeToken<List<String>>() {
            }.getType());
        } catch (IOException e) {
            log.warn("Could not close input stream from HttpResponse.", e);
        }
        return list;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<T> getOrionObjFromResponse(HttpResponse httpResponse) {
        InputStream source;
        List<T> ctxResp = new ArrayList<>();
        try {
            source = httpResponse.getEntity().getContent();
        } catch (IllegalStateException | IOException e) {
            throw new OrionConnectorException("Could not obtain entity content from HTTP response", e);
        }
        try (Reader reader = new InputStreamReader(source)) {
            String targetString = IOUtils.toString(reader);
            if (targetString.startsWith("{")) {
                T objResp = null;
                objResp = gson.fromJson(targetString, t);
                ctxResp.add(objResp);
            } else if (targetString.startsWith("[")) {
                List<LinkedTreeMap> items = gson.fromJson(targetString, List.class);
                for (LinkedTreeMap it : items) {
                    T objResp = gson.fromJson(gson.toJson(it), t);
                    ctxResp.add(objResp);
                }
            }
        } catch (IOException e) {
            log.warn("Could not close input stream from HttpResponse.", e);
        }
        return ctxResp;
    }

    private int getCount(HttpResponse httpResponse) {
        Header h = httpResponse.getFirstHeader(COUNT_HEADER);
        return Integer.parseInt(h.getValue());
    }

    public void append(OrionRelationship<? extends OrionRelationshipEntity> orionEntityRelationship) {
        String uri = this.orionAddr.toString() + RELATIONSHIPS_PATH;
        HttpResponse httpResponse = sendPostRequestToOrion(gson.toJson(orionEntityRelationship), uri);
        checkResponse(httpResponse, HttpStatus.SC_NO_CONTENT);
    }

	public List<T> readSubscriptionList() {
		List<T> entities = new ArrayList<>();
        int count = getSubscriptionCount();
        int offset = 0;
        do {
            entities.addAll(readSubscriptionList(config.getQueryLimit(), offset));
            offset += config.getQueryLimit();
        } while (offset < count);
        return entities;
	}
	
    public int getSubscriptionCount() {
        String uri = this.orionAddr.toString() + SUBSCRIPTIONS_PATH + QUERY + "limit=1&options=count";
        return sendGetRequestCountToOrion(null, uri, HttpStatus.SC_OK, true);
    }
    
    public List<T> readSubscriptionList(int limit, int offset) {
        log.debug("Find subscriptions with offset: {}", offset);
        String uri = this.orionAddr.toString() + SUBSCRIPTIONS_PATH + QUERY + "offset=" + offset + AND_LIMIT
                + limit;
        return sendGetRequestGenericEntityToOrion(null, uri, HttpStatus.SC_OK);
    }

	public T readSubscription(String id) {
        log.debug("Find subscription: {}", id);
        String uri = this.orionAddr.toString() + SUBSCRIPTIONS_PATH + SLASH + id;
        List<T> lstgoe = sendGetRequestGenericEntityToOrion(null, uri, HttpStatus.SC_OK);
        return lstgoe.size() == 1 ? lstgoe.get(0) : null;
	}
}
