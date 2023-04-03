package org.banka1.exchangeservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.forex.ForexFilterRequest;
import org.banka1.exchangeservice.domains.dtos.forex.TimeSeriesForexEnum;
import org.banka1.exchangeservice.services.ForexService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/forexes")
@AllArgsConstructor
public class ForexController {

    private final ForexService forexService;


    @PostMapping
    public ResponseEntity<?> getForexes(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size,
                                        @RequestBody ForexFilterRequest forexFilterRequest) {
        return ResponseEntity.ok(forexService.getForexes(page, size, forexFilterRequest));
    }

    @GetMapping
    public ResponseEntity<?> getForexByTimeSeries(@RequestParam String fromCurrency, @RequestParam String toCurrency, @RequestParam TimeSeriesForexEnum timeSeries) {
        return ResponseEntity.ok(forexService.getForexByTimeSeries(fromCurrency, toCurrency, timeSeries));
    }
}
