package com.dalma.broker.service.mapper;

import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.robot.Robot;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.base.BaseOrionAttributeOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.robot.BrokerRobotSummaryOutputDto;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RobotMapper extends OrionBaseMapper {
    private RobotMapper() {
        // Mapping class
    }

    @Component
    public static class RobotInputDtoToRobot extends PropertyMap<BrokerRobotInputDto, Robot> {

    	private static final Converter<BrokerRobotInputDto, String> macToOrionId = context -> {
            if (context.getSource() != null && !Strings.isEmpty(context.getSource().getMacAddress())) {
            	return new StringBuilder(Constant.ORION_ID_PREFIX).append(ORION_TYPE_MACRO).append(Constant.COLON)
                        .append(context.getSource().getMacAddress().replace(Constant.COLON, Strings.EMPTY).toLowerCase()).toString();
            }
            
            return idToOrionId.convert(null);
        };
        
        @Override
        protected void configure() {
            using(macToOrionId).map(source).setId(null);
            using(coordinatesToOrionLocation).map(source).setLocation(null);
            using(stringToOrionString).map(source.getName()).setName(null);
            using(stringToOrionString).map(source.getStatus()).setStatus(null);
            using(integerToOrionInteger).map(source.getBattery()).setBattery(null);
            using(booleanToOrionBoolean).map(source.getAvailable()).setAvailable(null);
            using(stringToOrionString).map(source.getAction()).setAction(null);
            using(stringToOrionString).map(source.getVersion()).setVersion(null);
            using(stringToOrionString).map(source.getPendingDestination()).setPendingDestination(null);
            using(stringToOrionString).map(source.getConnectivity()).setConnectivity(null);
        }
    }

    @Component
    public static class RobotToRobotSummaryOutputDto extends PropertyMap<Robot, BrokerRobotSummaryOutputDto> {

    	private static final Converter<OrionAttribute<String>, BaseOrionAttributeOutputDto<String>> getContextId = context -> {
            if (context.getSource() == null || context.getSource().getMetadata() == null || context.getSource().getMetadata().getContext() == null) {
            	return null;
            }
            
            BaseOrionAttributeOutputDto<String> output = new BaseOrionAttributeOutputDto<>();
            output.setValue(context.getSource().getMetadata().getContext().getValue());
            return output;
        };

    	private static final Converter<OrionAttribute<String[]>, List<String>> strArrayToStrList = context -> {
			if (context.getSource() == null) {
				return null;
			}
			return Arrays.asList(context.getSource().getValue());
        };
        
        @Override
        protected void configure() {
        	skip(destination.getDestination());
            using(strAttrToAttrOutput).map(source.getName()).setName(null);
            using(strAttrToAttrOutput).map(source.getStatus()).setStatus(null);
            using(intAttrToAttrOutput).map(source.getBattery()).setBattery(null);
            using(geoCoordinatesToAttrOutput).map(source.getLocation()).setCoordinates(null);
            using(doubleAttrToAttrOutput).map(source.getLocation().getMetadata().getAngle()).setAngle(null);
            using(dateToDateOutput).map(source.getDateCreated()).setDateCreated(null);
            using(dateToDateOutput).map(source.getDateModified()).setDateModified(null);
            using(booleanAttrToAttrOutput).map(source.getAvailable()).setAvailable(null);
            using(strAttrToAttrOutput).map(source.getRefWorkOrder()).setWorkOrder(null);
            using(strAttrToAttrOutput).map(source.getPendingDestination()).setPendingDestination(null);
            using(strArrayToStrList).map(source.getRefPayload()).setMaterials(null);
            using(strAttrToAttrOutput).map(source.getAction()).setAction(null);
            using(strAttrToAttrOutput).map(source.getVersion()).setVersion(null);
            using(strAttrToAttrOutput).map(source.getConnectivity()).setConnectivity(null);
            using(dateToDateOutput).map(source.getHeartbeat()).setHeartbeat(null);
            using(getContextId).map(source.getAction()).setActionContextId(null);
            using(getContextId).map(source.getRefDestination()).setDestinationContextId(null);
            using(getContextId).map(source.getStatus()).setStatusContextId(null);
        }
    }
}
