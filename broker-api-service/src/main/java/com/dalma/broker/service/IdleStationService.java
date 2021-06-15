package com.dalma.broker.service;

import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationInputDto;
import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationOutputDto;
import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStation;
import com.dalma.broker.service.publisher.IdleStationPublisher;
import com.dalma.broker.service.reader.IdleStationReader;
import com.dalma.common.entity.EntityType;
import org.springframework.stereotype.Service;

@Service
public class IdleStationService extends
        BaseOrionCrudService<BrokerIdleStationInputDto, BrokerIdleStationOutputDto, BrokerIdleStationSummaryOutputDto, IdleStation, IdleStationPublisher, IdleStationReader> {
	private final IdleStationPublisher publisher;	
	private final IdleStationReader reader;
	
    public IdleStationService(IdleStationPublisher publisher, IdleStationReader reader) {
        super(publisher, reader);
        this.publisher = publisher;
        this.reader = reader;
    }

    @Override
    protected String getType() {
        return EntityType.IDLE_STATION.getType();
    }

    @Override
    public void update(String id, BrokerIdleStationInputDto entity) {
        super.update(id, entity);

        // Associate robot
        if (entity.getRobotId() != null) {
        	publisher.append(reader.readOrionEntity(id), entity.getRobotId());
        }
    }
    
    @Override
    protected BrokerIdleStationOutputDto mapOrionEntityToEntity(IdleStation orionEntity) {
    	BrokerIdleStationOutputDto output = new BrokerIdleStationOutputDto();
    	output.setOrionId(orionEntity.getId());
    	return output;
    }
}
