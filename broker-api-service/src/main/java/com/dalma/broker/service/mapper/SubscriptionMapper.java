package com.dalma.broker.service.mapper;

import com.dalma.broker.fiware.orion.connector.entity.subscription.Subscription;
import com.dalma.broker.fiware.orion.connector.entity.subscription.SubscriptionCondition;
import com.dalma.broker.fiware.orion.connector.entity.subscription.SubscriptionEntity;
import com.dalma.broker.fiware.orion.connector.entity.subscription.SubscriptionNotification;
import com.dalma.broker.fiware.orion.connector.entity.subscription.SubscriptionNotificationUrl;
import com.dalma.broker.fiware.orion.connector.entity.subscription.SubscriptionSubject;
import com.dalma.contract.dto.subscription.BrokerSubscriptionEntityInputDto;
import com.dalma.contract.dto.subscription.BrokerSubscriptionEntityOutputDto;
import com.dalma.contract.dto.subscription.BrokerSubscriptionInputDto;
import com.dalma.contract.dto.subscription.BrokerSubscriptionSummaryOutputDto;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class SubscriptionMapper extends OrionBaseMapper {
    private SubscriptionMapper() {
    }

    @Component
    public static class SubscriptionInputDtoToSubscription
            extends PropertyMap<BrokerSubscriptionInputDto, Subscription> {

        private static final Converter<BrokerSubscriptionInputDto, SubscriptionSubject> subjectToSubscriptionSubject = context -> {
            if (context.getSource() == null) {
                return null;
            }

            SubscriptionSubject subject = new SubscriptionSubject();

            List<SubscriptionEntity> entities = new LinkedList<>();
            for (BrokerSubscriptionEntityInputDto entity : context.getSource().getEntities()) {
                SubscriptionEntity subEntity = new SubscriptionEntity();
                subEntity.setId(entity.getId());
                subEntity.setIdPattern(entity.getIdPattern());
                subEntity.setType(entity.getType());
                entities.add(subEntity);
            }
            subject.setEntities(entities);

            SubscriptionCondition condition = new SubscriptionCondition();
            condition.setAttrs(context.getSource().getAttributes());
            subject.setCondition(condition);

            return subject;
        };

        private static final Converter<BrokerSubscriptionInputDto, SubscriptionNotification> notificationToSubscriptionNotification = context -> {
            if (context.getSource() == null) {
                return null;
            }

            SubscriptionNotification notification = new SubscriptionNotification();

            SubscriptionNotificationUrl subUrl = new SubscriptionNotificationUrl();
            subUrl.setUrl(context.getSource().getNotificationUrl());
            notification.setHttp(subUrl);

            notification.setAttrs(context.getSource().getNotificationAttributes());

            return notification;
        };

        @Override
        protected void configure() {
            using(subjectToSubscriptionSubject).map(source).setSubject(null);
            using(notificationToSubscriptionNotification).map(source).setNotification(null);
            map(source.getExpirationDate(), destination.getExpires());
        }
    }
    
    
    @Component
    public static class SubscriptionToBrokerSubscriptionSummaryOutputDto
            extends PropertyMap<Subscription, BrokerSubscriptionSummaryOutputDto> {

    	private static final Converter<SubscriptionSubject, List<BrokerSubscriptionEntityOutputDto>> subjectToEntities = context -> {
            if (context.getSource() == null) {
                return null;
            }

            List<BrokerSubscriptionEntityOutputDto> entities = new ArrayList<>(context.getSource().getEntities().size());
            
            context.getSource().getEntities().forEach(entity -> {
            	BrokerSubscriptionEntityOutputDto brokerEntity = new BrokerSubscriptionEntityOutputDto();
            	brokerEntity.setId(entity.getId());
            	brokerEntity.setIdPattern(entity.getIdPattern());
            	brokerEntity.setType(entity.getType());
            	entities.add(brokerEntity);
            });
            
            return entities;
        };
        
        private static final Converter<SubscriptionCondition, List<String>> conditionAttrToAttr = context -> {
            if (context.getSource() == null) {
                return null;
            }

            return context.getSource().getAttrs();
        };
        
		@Override
		protected void configure() {
            using(subjectToEntities).map(source.getSubject()).setEntities(null);
            using(conditionAttrToAttr).map(source.getSubject().getCondition()).setAttributes(null);
            map(source.getNotification().getHttp().getUrl(), destination.getNotificationUrl());
		}
    }
}
