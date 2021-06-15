package com.dalma.broker.base.modelmapper;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

public class BrightModelMapper extends ModelMapper {

    private Collection<MapperPreProcessor> processors = Collections.emptySet();

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        return super.map(preProcess(source), destinationType);
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType, String typeMapName) {
        return super.map(preProcess(source), destinationType, typeMapName);
    }

    @Override
    public void map(Object source, Object destination) {
        super.map(preProcess(source), destination);
    }

    @Override
    public void map(Object source, Object destination, String typeMapName) {
        super.map(preProcess(source), destination, typeMapName);
    }

    @Override
    public <D> D map(Object source, Type destinationType) {
        return super.map(preProcess(source), destinationType);
    }

    @Override
    public <D> D map(Object source, Type destinationType, String typeMapName) {
        return super.map(preProcess(source), destinationType, typeMapName);
    }

    public void setProcessors(Collection<MapperPreProcessor> processors) {
        this.processors = processors;
    }

    private Object preProcess(Object object) {
        var result = object;

        for (var processor : processors) {
            result = processor.process(result);
        }

        return result;
    }
}
