package org.banka1.exchangeservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.services.ExchangeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/exchanges")
@AllArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;



}
