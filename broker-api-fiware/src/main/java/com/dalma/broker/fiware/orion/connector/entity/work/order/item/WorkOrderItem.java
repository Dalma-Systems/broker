package com.dalma.broker.fiware.orion.connector.entity.work.order.item;

import com.dalma.broker.fiware.orion.connector.entity.BaseOrionEntity;
import com.dalma.broker.fiware.orion.connector.entity.OrionEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import com.dalma.common.entity.EntityType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WorkOrderItem extends BaseOrionEntity implements OrionEntity, Serializable {

    private static final long serialVersionUID = 603855168278670390L;

    private OrionBaseAttribute<String> refMaterial;
    private OrionBaseAttribute<Double> quantity;

    @Override
    public String getEntityType() {
        return EntityType.WORK_ORDER_ITEM.getType();
    }
}
