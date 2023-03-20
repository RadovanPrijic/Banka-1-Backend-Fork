package org.banka1.exchangeservice.controllers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.banka1.exchangeservice.services.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;

@CrossOrigin
@RestController
@RequestMapping("/api/exchanges")
@AllArgsConstructor
public class ExchangeController {


    private ExchangeService exchangeService;

    //Ukoliko hocete da testirate preko postmana a baca vam unauthorized zakomentarisite security dependencije u pom.xml
    @GetMapping(value = "/get-all",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExchanges(@RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {

        System.out.println("page size");
        return ResponseEntity.ok(exchangeService.getExchanges(page, size));
    }
}
