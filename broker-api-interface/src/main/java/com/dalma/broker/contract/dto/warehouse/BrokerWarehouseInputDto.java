package com.dalma.broker.contract.dto.warehouse;

import com.dalma.contract.dto.base.BaseLocationInputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BrokerWarehouseInputDto extends BaseLocationInputDto {
    @NotBlank
    private String name;

    @NotBlank
    private String status;

    @NotBlank
    private String erpId;
    
    @NotBlank
    private List<BrokerWarehouseMaterialInputDto> materials;
}
