package org.banka1.exchangeservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.stock.TimeSeriesStockEnum;
import org.banka1.exchangeservice.services.StockService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/stocks")
@AllArgsConstructor
public class StockController {

    private final StockService stockService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllStocks(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String symbol) {
        return ResponseEntity.ok(stockService.getStocks(page, size, symbol));
    }

    @GetMapping(value = "/time-series",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStockTimeSeries(@RequestParam TimeSeriesStockEnum timeSeries, @RequestParam String symbol) {
        return ResponseEntity.ok(stockService.getStockTimeSeries(symbol, timeSeries));
    }
}
