package com.dalma.broker.api.controller;

import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;
import com.dalma.broker.service.BaseOrionCrudService;
import com.dalma.common.util.Constant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.dalma.broker.contract.Paths.BY_ID;

public abstract class BaseOrionCrudController<I, O, S, E extends OrionEntity, P extends DefaultOrionPublisher<I, E>, R extends DefaultOrionReader<S, E>, Z extends BaseOrionCrudService<I, O, S, E, P, R>> {
    private Z service;

    public BaseOrionCrudController(Z service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public O create(@RequestBody I input) {
        return service.create(input);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<S> getAll() {
        return service.getAll();
    }

    @GetMapping(BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public S get(@PathVariable(Constant.ID) String id) {
        return service.get(id);
    }

    @DeleteMapping(BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable(Constant.ID) String id) {
        service.delete(id);
    }

    @PatchMapping(BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable(Constant.ID) String id, @RequestBody I input) {
        service.update(id, input);
    }
}
