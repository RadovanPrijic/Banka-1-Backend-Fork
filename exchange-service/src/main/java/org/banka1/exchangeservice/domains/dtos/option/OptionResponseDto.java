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
public class OptionResponseDto {
    private Double expirationDate;
    private Boolean hasMiniOptions;
    private List<OptionTypeDto> calls;
    private List<OptionTypeDto> puts;
}
