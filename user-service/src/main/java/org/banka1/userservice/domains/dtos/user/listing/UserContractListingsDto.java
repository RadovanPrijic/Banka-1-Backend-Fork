package org.banka1.userservice.domains.dtos.user.listing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContractListingsDto {

    private String contractId;
    private Long userId;
    private Double sellPrice;
//    private List<StockData> sellStocks;
//    private List<StockData> buyStocks;
    private List<StockData> stocks;

    @Data
    public static class StockData {
        private String symbol;
        private int quantity;
        private TransactionType transactionType;
    }

    public enum TransactionType {
        BUY, SELL
    }

}
