package com.dalma.broker.service.mapper;

import com.dalma.broker.contract.dto.base.BaseInputDto;
import com.dalma.broker.fiware.orion.connector.entity.OrionEntityType;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttributeMetadata;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionLocationAttribute;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionLocationAttributeMetadata;
import com.dalma.broker.fiware.orion.connector.entity.common.LocationPoint;
import com.dalma.broker.fiware.orion.connector.entity.common.OrionDateTime;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.base.BaseLocationInputDto;
import com.dalma.contract.dto.base.BaseOrionAttributeOutputDto;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.Converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public abstract class OrionBaseMapper {
    protected OrionBaseMapper() {
        // Base Mapper class
    }

    public static final String ORION_TYPE_MACRO = "<TYPE>";

    public static final Converter<? extends BaseLocationInputDto, OrionLocationAttribute<LocationPoint>> coordinatesToOrionLocation = context -> {
        if (context.getSource() == null || context.getSource().getLongitude() == null
                || context.getSource().getLatitude() == null) {
            return null;
        }

        OrionLocationAttribute<LocationPoint> location = new OrionLocationAttribute<>();
        location.setType(OrionEntityType.GEO_JSON.getType());
        LocationPoint locationPoint = new LocationPoint();
        Double[] coordinates = {context.getSource().getLongitude(), context.getSource().getLatitude()};
        locationPoint.setCoordinates(coordinates);
        location.setValue(locationPoint);
        location.setMetadata(retrieveLocationMetadata(context.getSource().getAngle()));
        return location;
    };

    public static final Converter<String, OrionAttribute<String>> stringToOrionString = context -> {
        if (context.getSource() == null) {
            return null;
        }

        OrionAttribute<String> orionStringAttribute = new OrionAttribute<>();
        orionStringAttribute.setType(OrionEntityType.TEXT.getType());
        orionStringAttribute.setValue(context.getSource());
        orionStringAttribute.setMetadata(retrieveMetadata());
        return orionStringAttribute;
    };

    public static final Converter<String, OrionAttribute<String>> stringToOrionIdString = context -> {
        if (context.getSource() == null) {
            return null;
        }

        OrionAttribute<String> orionStringAttribute = new OrionAttribute<>();
        orionStringAttribute.setType(OrionEntityType.TEXT.getType());
        orionStringAttribute.setValue(new StringBuilder(Constant.ID).append(context.getSource()).toString());
        orionStringAttribute.setMetadata(retrieveMetadata());
        return orionStringAttribute;
    };

    public static final Converter<Integer, OrionAttribute<Integer>> integerToOrionInteger = context -> {
        if (context.getSource() == null) {
            return null;
        }

        OrionAttribute<Integer> orionIntegerAttribute = new OrionAttribute<>();
        orionIntegerAttribute.setType(OrionEntityType.NUMBER.getType());
        orionIntegerAttribute.setValue(context.getSource());
        orionIntegerAttribute.setMetadata(retrieveMetadata());
        return orionIntegerAttribute;
    };

    public static final Converter<Double, OrionAttribute<Double>> doubleToOrionDouble = context -> {
        if (context.getSource() == null) {
            return null;
        }

        OrionAttribute<Double> orionIntegerAttribute = new OrionAttribute<>();
        orionIntegerAttribute.setType(OrionEntityType.NUMBER.getType());
        orionIntegerAttribute.setValue(context.getSource());
        orionIntegerAttribute.setMetadata(retrieveMetadata());
        return orionIntegerAttribute;
    };

    public static final Converter<Boolean, OrionAttribute<Boolean>> booleanToOrionBoolean = context -> {
        if (context.getSource() == null) {
            return null;
        }

        OrionAttribute<Boolean> orionIntegerAttribute = new OrionAttribute<>();
        orionIntegerAttribute.setType(OrionEntityType.BOOLEAN.getType());
        orionIntegerAttribute.setValue(context.getSource());
        orionIntegerAttribute.setMetadata(retrieveMetadata());
        return orionIntegerAttribute;
    };

    public static final Converter<Integer, OrionBaseAttribute<Integer>> integerToOrionBaseInteger = context -> {
        if (context.getSource() == null) {
            return null;
        }

        OrionBaseAttribute<Integer> orionIntegerAttribute = new OrionBaseAttribute<>();
        orionIntegerAttribute.setType(OrionEntityType.NUMBER.getType());
        orionIntegerAttribute.setValue(context.getSource());
        return orionIntegerAttribute;
    };

    public static final Converter<Double, OrionBaseAttribute<Double>> doubleToOrionBaseDouble = context -> {
        if (context.getSource() == null) {
            return null;
        }

        OrionBaseAttribute<Double> orionIntegerAttribute = new OrionBaseAttribute<>();
        orionIntegerAttribute.setType(OrionEntityType.NUMBER.getType());
        orionIntegerAttribute.setValue(context.getSource());
        return orionIntegerAttribute;
    };

    public static final Converter<OrionAttribute<Boolean>, BaseOrionAttributeOutputDto<Boolean>> booleanAttrToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<Boolean> attributeOutput = new BaseOrionAttributeOutputDto<>();
        if (context.getSource().getMetadata() != null && context.getSource().getMetadata().getDateModified() != null) {
            attributeOutput.setDateModified(context.getSource().getMetadata().getDateModified().getValue());
        }
        attributeOutput.setValue(context.getSource().getValue());
        return attributeOutput;
    };

    public static final Converter<BaseInputDto, String> idToOrionId = context -> new StringBuilder(Constant.ORION_ID_PREFIX).append(ORION_TYPE_MACRO).append(Constant.COLON)
                .append(UUID.randomUUID().toString().replace(Constant.HYPHEN, Strings.EMPTY)).toString();

    public static OrionAttributeMetadata retrieveMetadata() {
        OrionAttributeMetadata metadata = new OrionAttributeMetadata();
        metadata.setDateModified(retrieveActualDateModified());
        return metadata;
    }

    private static OrionLocationAttributeMetadata retrieveLocationMetadata(Double angle) {
        OrionLocationAttributeMetadata metadata = new OrionLocationAttributeMetadata();

        metadata.setDateModified(retrieveActualDateModified());

        OrionAttribute<Double> angleAttribute = new OrionAttribute<>();
        angleAttribute.setType(OrionEntityType.DOUBLE.getType());
        angleAttribute.setValue(angle);
        metadata.setAngle(angleAttribute);

        return metadata;
    }

    public static OrionDateTime retrieveActualDateModified() {
        OrionDateTime dateModified = new OrionDateTime();
        dateModified.setValue(new SimpleDateFormat(Constant.ORION_DATE_FORMAT).format(new Date()));
        return dateModified;
    }

    public static final Converter<OrionBaseAttribute<String>, BaseOrionAttributeOutputDto<String>> strBaseAttrToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<String> attributeOutput = new BaseOrionAttributeOutputDto<>();
        attributeOutput.setValue(context.getSource().getValue());
        return attributeOutput;
    };

    public static final Converter<OrionBaseAttribute<String>, String> strBaseAttrToStrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        return context.getSource().getValue();
    };

    public static final Converter<OrionAttribute<String>, BaseOrionAttributeOutputDto<String>> strAttrToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<String> attributeOutput = new BaseOrionAttributeOutputDto<>();
        if (context.getSource().getMetadata() != null && context.getSource().getMetadata().getDateModified() != null) {
            attributeOutput.setDateModified(context.getSource().getMetadata().getDateModified().getValue());
        }
        attributeOutput.setValue(context.getSource().getValue());
        return attributeOutput;
    };

    public static final Converter<OrionAttribute<String>, BaseOrionAttributeOutputDto<String>> strIdAttrToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<String> attributeOutput = new BaseOrionAttributeOutputDto<>();
        if (context.getSource().getMetadata() != null && context.getSource().getMetadata().getDateModified() != null) {
            attributeOutput.setDateModified(context.getSource().getMetadata().getDateModified().getValue());
        }
        attributeOutput.setValue(context.getSource().getValue().replaceFirst(Constant.ID, Strings.EMPTY));
        return attributeOutput;
    };

    public static final Converter<OrionAttribute<Integer>, BaseOrionAttributeOutputDto<Integer>> intAttrToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<Integer> attributeOutput = new BaseOrionAttributeOutputDto<>();
        if (context.getSource().getMetadata() != null && context.getSource().getMetadata().getDateModified() != null) {
            attributeOutput.setDateModified(context.getSource().getMetadata().getDateModified().getValue());
        }
        attributeOutput.setValue(context.getSource().getValue());
        return attributeOutput;
    };

    public static final Converter<OrionAttribute<Double>, BaseOrionAttributeOutputDto<Double>> doubleAttrToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<Double> attributeOutput = new BaseOrionAttributeOutputDto<>();
        if (context.getSource().getMetadata() != null && context.getSource().getMetadata().getDateModified() != null) {
            attributeOutput.setDateModified(context.getSource().getMetadata().getDateModified().getValue());
        }
        attributeOutput.setValue(context.getSource().getValue());
        return attributeOutput;
    };

    public static final Converter<OrionBaseAttribute<Integer>, BaseOrionAttributeOutputDto<Integer>> intBaseAttrToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<Integer> attributeOutput = new BaseOrionAttributeOutputDto<>();
        attributeOutput.setValue(context.getSource().getValue());
        return attributeOutput;
    };

    public static final Converter<OrionBaseAttribute<Double>, BaseOrionAttributeOutputDto<Double>> doubleBaseAttrToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<Double> attributeOutput = new BaseOrionAttributeOutputDto<>();
        attributeOutput.setValue(context.getSource().getValue());
        return attributeOutput;
    };

    public static final Converter<OrionDateTime, BaseOrionAttributeOutputDto<String>> dateToDateOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<String> coordinates = new BaseOrionAttributeOutputDto<>();
        coordinates.setValue(context.getSource().getValue());
        return coordinates;
    };

    public static final Converter<String, OrionDateTime> dateToOrionDate = context -> {
        if (context.getSource() == null) {
            return null;
        }

        OrionDateTime date = new OrionDateTime();
        date.setValue(context.getSource());
        date.setType(OrionEntityType.DATE_TIME.getType());
        return date;
    };

    public static final Converter<OrionLocationAttribute<LocationPoint>, BaseOrionAttributeOutputDto<Double[]>> geoCoordinatesToAttrOutput = context -> {
        if (context.getSource() == null) {
            return null;
        }

        BaseOrionAttributeOutputDto<Double[]> coordinates = new BaseOrionAttributeOutputDto<>();
        if (context.getSource().getMetadata() != null && context.getSource().getMetadata().getDateModified() != null) {
            coordinates.setDateModified(context.getSource().getMetadata().getDateModified().getValue());
        }
        coordinates.setValue(context.getSource().getValue().getCoordinates());
        return coordinates;
    };

    public static OrionBaseAttribute<String> createOrionRelationship(String id) {
        OrionAttribute<String> ref = new OrionAttribute<>();
        ref.setType(OrionEntityType.RELATIONSHIP.getType());
        ref.setValue(id);
        ref.setMetadata(retrieveMetadata());
        return ref;
    }

    public static OrionBaseAttribute<ArrayList<String>> createOrionRelationshipArray(String[] id) {
        OrionAttribute<ArrayList<String>> ref = new OrionAttribute<>();
        ref.setType(OrionEntityType.RELATIONSHIP.getType());
        ref.setValue(new ArrayList<>(Arrays.asList(id)));
        ref.setMetadata(retrieveMetadata());
        return ref;
    }

    public static OrionBaseAttribute<String> createOrionBaseAttribute(String value) {
        OrionBaseAttribute<String> orionIntegerAttribute = new OrionBaseAttribute<>();
        orionIntegerAttribute.setType(OrionEntityType.TEXT.getType());
        orionIntegerAttribute.setValue(value);
        return orionIntegerAttribute;
    }
}
