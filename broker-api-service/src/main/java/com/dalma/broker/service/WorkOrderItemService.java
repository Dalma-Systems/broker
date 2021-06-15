package com.dalma.broker.service;

import com.dalma.broker.contract.dto.base.BaseOutputDto;
import com.dalma.broker.contract.dto.base.BaseSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItem;
import com.dalma.broker.service.publisher.WorkOrderItemPublisher;
import com.dalma.broker.service.reader.WorkOrderItemReader;
import com.dalma.common.entity.EntityType;
import com.dalma.contract.dto.work.order.item.BrokerWorkOrderItemInputDto;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderItemService extends
        BaseOrionCrudService<BrokerWorkOrderItemInputDto, BaseOutputDto, BaseSummaryOutputDto, WorkOrderItem, WorkOrderItemPublisher, WorkOrderItemReader> {
    private final WorkOrderItemPublisher publisher;

    public WorkOrderItemService(WorkOrderItemPublisher publisher, WorkOrderItemReader reader) {
        super(publisher, reader);
        this.publisher = publisher;
    }

    @Override
    protected String getType() {
        return EntityType.WORK_ORDER_ITEM.getType();
    }

    @Override
    protected BaseOutputDto mapOrionEntityToEntity(WorkOrderItem orionEntity) {
        return new BaseOutputDto() {
        };
    }

    public BaseOutputDto create(BrokerWorkOrderItemInputDto entity, String workorderId) {
        // Create work order item
        WorkOrderItem orionEntity = publisher.create(entity);

        // Associate with work order
        publisher.append(orionEntity, workorderId);

        return mapOrionEntityToEntity(orionEntity);
    }
}
