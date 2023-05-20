package org.banka1.exchangeservice.domains.dtos.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionChainDto {
    private List<ResultDto> result;
    private String error;
}
