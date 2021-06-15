package com.dalma.broker.service;

import com.dalma.broker.contract.dto.station.work.BrokerWorkStationInputDto;
import com.dalma.broker.contract.dto.station.work.BrokerWorkStationOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.station.work.WorkStation;
import com.dalma.broker.fiware.orion.connector.entity.station.work.field.WorkStationField;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrder;
import com.dalma.broker.service.exception.workstation.WorkStationNotFoundException;
import com.dalma.broker.service.publisher.WorkStationPublisher;
import com.dalma.broker.service.reader.WorkOrderReader;
import com.dalma.broker.service.reader.WorkStationReader;
import com.dalma.common.entity.EntityType;
import com.dalma.common.util.Constant;
import com.dalma.contract.dto.station.work.BrokerWorkStationSummaryOutputDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkStationService extends
        BaseOrionCrudService<BrokerWorkStationInputDto, BrokerWorkStationOutputDto, BrokerWorkStationSummaryOutputDto, WorkStation, WorkStationPublisher, WorkStationReader> {
    private final WorkStationReader reader;
    private final WorkOrderReader workorderReader;

    public WorkStationService(WorkStationPublisher publisher, WorkStationReader reader,
                              WorkOrderReader workorderReader) {
        super(publisher, reader);
        this.reader = reader;
        this.workorderReader = workorderReader;
    }

    @Override
    protected String getType() {
        return EntityType.WORK_STATION.getType();
    }

    @Override
    protected BrokerWorkStationOutputDto mapOrionEntityToEntity(WorkStation orionEntity) {
    	BrokerWorkStationOutputDto output = new BrokerWorkStationOutputDto();
    	output.setOrionId(orionEntity.getId());
        return output;
    }

    @Override
    public BrokerWorkStationSummaryOutputDto get(String id) {
        BrokerWorkStationSummaryOutputDto orionEntity = reader.readObject(id);

        List<WorkOrder> workorders = workorderReader.readOrionEntityQueryListRelationship(WorkStationField.RELATIONSHIP_WORKSTATION.getField(),
                orionEntity.getId(), EntityType.WORK_ORDER.getType());
        if (!workorders.isEmpty()) {
            Optional<WorkOrder> workOrderMostRecent = workorders.stream()
                    .sorted((o1, o2) -> o2.getDateCreated().getValue().compareTo(o1.getDateCreated().getValue()))
                    .findFirst();
            if (workOrderMostRecent.isPresent()) {
                orionEntity.setWorkorderId(workOrderMostRecent.get().getId());
            }
        }

        return orionEntity;
    }

	public BrokerWorkStationSummaryOutputDto getByExternalId(String id) {
        List<WorkStation> workstations = reader.readOrionEntityQueryListRelationship(WorkStationField.ERP_EXTERNAL_ID.getField(),
                new StringBuilder(Constant.ID).append(id).toString(), EntityType.WORK_STATION.getType());
        if (workstations == null || workstations.isEmpty()) {
        	throw new WorkStationNotFoundException();
        }
        return reader.mapOrionEntityToEntity(workstations.get(0));
	}
}
