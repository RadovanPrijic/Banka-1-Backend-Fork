package org.banka1.exchangeservice.domains.dtos.stock;

public enum TimeSeriesStockEnum {
    FIVE_MIN("5min"),
    HOUR("60min"),
    DAILY("TIME_SERIES_DAILY"),
    WEEKLY("TIME_SERIES_WEEKLY"),
    MONTHLY("TIME_SERIES_MONTHLY");

    private String value;

    TimeSeriesStockEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
