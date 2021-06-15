package com.dalma.broker.base.config;

import com.dalma.broker.base.modelmapper.BrightModelMapper;
import com.dalma.broker.base.modelmapper.MapperPreProcessor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * Configuration class for ModelMapper. All the customizations to the default
 * config should be registered here to have a central place to check how is the
 * ModelMapper configured.
 */
@Configuration
public class ModelMapperConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Create a new instance of @see org.modelmapper.ModelMapper and add all the
     * Mappings found in the project.
     *
     * @return a new instance of @see org.modelmapper.ModelMapper
     */
    @SuppressWarnings("rawtypes")
    @Bean
    public ModelMapper createModelMapper() {
        BrightModelMapper modelMapper = new BrightModelMapper();

        // Adding all PropertyMaps
        Collection<PropertyMap> mappings = findMappings();
        mappings.forEach(modelMapper::addMappings);

        // Add all model Converters
        Collection<Converter> converters = findConverters();
        converters.forEach(modelMapper::addConverter);

        // Add mapping source pre processors
        Collection<MapperPreProcessor> processors = findProcessors();
        modelMapper.setProcessors(processors);

        return modelMapper;
    }

    @SuppressWarnings("rawtypes")
    private Collection<PropertyMap> findMappings() {
        return applicationContext.getBeansOfType(PropertyMap.class).values();
    }

    @SuppressWarnings("rawtypes")
    private Collection<Converter> findConverters() {
        return applicationContext.getBeansOfType(Converter.class).values();
    }

    private Collection<MapperPreProcessor> findProcessors() {
        return applicationContext.getBeansOfType(MapperPreProcessor.class).values();
    }

}
