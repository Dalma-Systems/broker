package com.dalma.broker.contract.dto.subscription;

import com.dalma.broker.contract.dto.base.BaseOutputDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
public class BrokerSubscriptionOutputDto extends BaseOutputDto {
    @NonNull
    private String orionId;
}
