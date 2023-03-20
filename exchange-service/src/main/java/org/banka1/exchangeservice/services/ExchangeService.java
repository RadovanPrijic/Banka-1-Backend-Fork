package org.banka1.exchangeservice.services;

import org.banka1.exchangeservice.domains.entities.Exchange;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ExchangeService {

    @Autowired
    private ExchangeRepository exchangeRepository;



    public List<Exchange> findAll(){
        return exchangeRepository.findAll();
    }

   public Exchange findById(Long id){
        return exchangeRepository.findByExcId(id);
   }

    public List<Exchange> findByNameLike(String name){
        return exchangeRepository.findByExcNameLike(name);
    }

    public Exchange findByAcronym(String acronym){
        return exchangeRepository.findByExcAcronym(acronym);
    }
    public Exchange findByIdMicCode(String micCode){
        return exchangeRepository.findByExcMicCode(micCode);
    }
}
