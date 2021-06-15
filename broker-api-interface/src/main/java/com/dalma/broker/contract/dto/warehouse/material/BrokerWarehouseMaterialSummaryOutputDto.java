package com.dalma.broker.contract.dto.warehouse.material;

import com.dalma.broker.contract.dto.base.BaseSummaryOutputDto;
import com.dalma.contract.dto.base.BaseOrionAttributeOutputDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BrokerWarehouseMaterialSummaryOutputDto extends BaseSummaryOutputDto {
    @NotBlank
    private String id;

    @NotBlank
    private BaseOrionAttributeOutputDto<String> type;

    @NotBlank
    private BaseOrionAttributeOutputDto<String> batch;

    @NotBlank
    private BaseOrionAttributeOutputDto<Double> quantity;

    @NotBlank
    private BaseOrionAttributeOutputDto<String> unit;

    @NotBlank
    private BaseOrionAttributeOutputDto<String> erpId;

    @NotBlank
    private BaseOrionAttributeOutputDto<String> dateCreated;

    @NotBlank
    private BaseOrionAttributeOutputDto<String> dateModified;

    private String warehouseId;
}
