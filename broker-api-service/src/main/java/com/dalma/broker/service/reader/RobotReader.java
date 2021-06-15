package com.dalma.broker.service.reader;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.robot.Robot;
import com.dalma.broker.fiware.orion.connector.entity.robot.RobotOrionReader;
import com.dalma.contract.dto.base.BaseOrionAttributeOutputDto;
import com.dalma.contract.dto.robot.BrokerRobotSummaryOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RobotReader extends RobotOrionReader<BrokerRobotSummaryOutputDto> {

    private final ModelMapper modelMapper;

    public RobotReader(OrionConnectorConfiguration config, ModelMapper modelMapper) {
        super(config);
        this.modelMapper = modelMapper;
    }

    @Override
    public BrokerRobotSummaryOutputDto mapOrionEntityToEntity(Robot entity) {
    	BrokerRobotSummaryOutputDto robotOutput = modelMapper.map(entity, BrokerRobotSummaryOutputDto.class);
    	if (entity.getRefDestination() != null) {
	        BaseOrionAttributeOutputDto<String> destination = new BaseOrionAttributeOutputDto<>();
	        destination.setValue(entity.getRefDestination().getValue());
	        if (entity.getRefDestination().getMetadata() != null && entity.getRefDestination().getMetadata().getDateModified() != null) {
	        	destination.setDateModified(entity.getRefDestination().getMetadata().getDateModified().getValue());
	        }
	    	robotOutput.setDestination(destination);
    	}
    	return robotOutput;
    }
}
