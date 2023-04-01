package org.banka1.exchangeservice.domains.dtos.forex;

public enum TimeSeriesForexEnum {

    FIVE_MIN("5min"),
    HOUR("60min"),
    DAILY("FX_DAILY"),
    WEEKLY("FX_WEEKLY"),
    MONTHLY("FX_MONTHLY");

    private String value;

    TimeSeriesForexEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
