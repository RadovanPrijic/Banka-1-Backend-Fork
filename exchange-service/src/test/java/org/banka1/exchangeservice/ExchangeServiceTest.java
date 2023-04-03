package org.banka1.exchangeservice;

import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.exchangeservice.domains.dtos.exchange.ExchangeCSV;
import org.banka1.exchangeservice.domains.entities.Exchange;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.banka1.exchangeservice.services.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class ExchangeServiceTest {

    private ExchangeService exchangeService;
    private ExchangeRepository exchangeRepository;

    @BeforeEach
    void setUp(){
        this.exchangeRepository = mock(ExchangeRepository.class);
        this.exchangeService = new ExchangeService(this.exchangeRepository);
    }

    @Test
    void persistExchangesSuccessfully(){
        //given

        var exchange1 = new ExchangeCSV("Jakarta Futures Exchange (bursa Berjangka Jakarta)","BBJ","XBBJ","Indonesia","Indonesian Rupiah","Asia/Jakarta","09:00","17:30");
        var exchange2 = new ExchangeCSV("Asx - Trade24","SFE","XSFE","Australia","Australian Dollar","Australia/Melbourne","10:00","16:00");
        var exchange3 = new ExchangeCSV("Cboe Edga U.s. Equities Exchange Dark","EDGADARK","EDGD","United States","United States Dollar","America/New_York","09:30", "16:00");
        List<ExchangeCSV> csvBeanList = List.of(exchange1, exchange2, exchange3);

        //when
        exchangeService.persistExchanges(csvBeanList);

        verify(exchangeRepository, times(1)).saveAll((Mockito.anyCollection()));
        verifyNoMoreInteractions(exchangeRepository);
    }


    @Test
    void getExchangesSuccessfully(){

        //given

        //when
        when(exchangeRepository.findAll(PageRequest.of(0, 10))).thenReturn(getExchangesList());
        var result = exchangeService.getExchanges(0,10);
        //then
        assertEquals(6, result.size());
    }

    @Test
    void findByIdSuccessfully(){
        //given
        var exchange1 = new Exchange(2L,"Asx - Trade24","SFE","XSFE","Australia","Australian Dollar","Australia/Melbourne","10:00","16:00");
        when(exchangeRepository.findById(anyLong())).thenReturn(Optional.of(exchange1));
        //when
        var result = exchangeService.findById(anyLong());
        //then
        assertEquals(2L,result.getExcId());
        assertEquals("Asx - Trade24",result.getExcName());
        assertEquals("SFE",result.getExcAcronym());
        assertEquals("XSFE",result.getExcMicCode());
        assertEquals("Australia",result.getExcCountry());
        assertEquals("Australian Dollar",result.getExcCurrency());
        assertEquals("Australia/Melbourne",result.getExcTimeZone());
        assertEquals("10:00",result.getExcOpenTime());
        assertEquals("16:00",result.getExcCloseTime());


    }
    @Test
    void findByIdThrowsNotFoundException(){
        //given
        when(exchangeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThrows(NotFoundExceptions.class, () -> exchangeService.findById(1L));

        verify(exchangeRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(exchangeRepository);
    }

    @Test
    void findByNameLikeSuccessfully(){
        //given
        when(exchangeRepository.findByExcNameLike(anyString())).thenReturn(getExchangesListNameLike());

        //when
        var result = exchangeService.findByNameLike("C");
        //then
        assertEquals(2,result.size());

        assertEquals(3L,result.get(0).getExcId());
        assertEquals(4L,result.get(1).getExcId());
    }

    @Test
    void findByNameLikeThrowsNotFoundException(){
        //given
        when(exchangeRepository.findByExcNameLike(anyString())).thenReturn(new ArrayList<>());

        //then
        assertThrows(NotFoundExceptions.class, () -> exchangeService.findByNameLike(anyString()));

        verify(exchangeRepository, times(1)).findByExcNameLike(anyString());
        verifyNoMoreInteractions(exchangeRepository);
    }

    @Test
    void findByAcronymSuccessfully(){
        //given
        var exchange1 = new Exchange(2L,"Asx - Trade24","SFE","XSFE","Australia","Australian Dollar","Australia/Melbourne","10:00","16:00");
        when(exchangeRepository.findByExcAcronym(anyString())).thenReturn(exchange1);
        //when
        var result = exchangeService.findByAcronym(anyString());
        //then
        assertEquals(2L,result.getExcId());
        assertEquals("Asx - Trade24",result.getExcName());
        assertEquals("SFE",result.getExcAcronym());
        assertEquals("XSFE",result.getExcMicCode());
        assertEquals("Australia",result.getExcCountry());
        assertEquals("Australian Dollar",result.getExcCurrency());
        assertEquals("Australia/Melbourne",result.getExcTimeZone());
        assertEquals("10:00",result.getExcOpenTime());
        assertEquals("16:00",result.getExcCloseTime());


    }

    @Test
    void findByAcronymThrowsNotFoundException(){
        //given
        when(exchangeRepository.findByExcAcronym(anyString())).thenReturn(null);

        //then
        assertThrows(NotFoundExceptions.class, () -> exchangeService.findByAcronym(anyString()));

        verify(exchangeRepository, times(1)).findByExcAcronym(anyString());
        verifyNoMoreInteractions(exchangeRepository);
    }

    @Test
    void findByIdMicCodeSuccessfully(){
        //given
        var exchange1 = new Exchange(2L,"Asx - Trade24","SFE","XSFE","Australia","Australian Dollar","Australia/Melbourne","10:00","16:00");
        when(exchangeRepository.findByExcMicCode(anyString())).thenReturn(Optional.of(exchange1));
        //when
        var result = exchangeService.findByIdMicCode(anyString());
        //then
        assertEquals(2L,result.getExcId());
        assertEquals("Asx - Trade24",result.getExcName());
        assertEquals("SFE",result.getExcAcronym());
        assertEquals("XSFE",result.getExcMicCode());
        assertEquals("Australia",result.getExcCountry());
        assertEquals("Australian Dollar",result.getExcCurrency());
        assertEquals("Australia/Melbourne",result.getExcTimeZone());
        assertEquals("10:00",result.getExcOpenTime());
        assertEquals("16:00",result.getExcCloseTime());


    }
    @Test
    void findByIdMicCodeThrowsNotFoundException(){
        //given
        when(exchangeRepository.findByExcMicCode(anyString())).thenReturn(Optional.empty());

        //then
        assertThrows(NotFoundExceptions.class, () -> exchangeService.findByIdMicCode(anyString()));

        verify(exchangeRepository, times(1)).findByExcMicCode(anyString());
        verifyNoMoreInteractions(exchangeRepository);
    }


    private Page<Exchange> getExchangesList() {
        var exchange1 = new Exchange(1L,"Jakarta Futures Exchange (bursa Berjangka Jakarta)","BBJ","XBBJ","Indonesia","Indonesian Rupiah","Asia/Jakarta","09:00","17:30");
        var exchange2 = new Exchange(2L,"Asx - Trade24","SFE","XSFE","Australia","Australian Dollar","Australia/Melbourne","10:00","16:00");
        var exchange3 = new Exchange(3L,"Cboe Edga U.s. Equities Exchange Dark","EDGADARK","EDGD","United States","United States Dollar","America/New_York","09:30", "16:00");
        var exchange4 = new Exchange(4L,"Clear Street","CLST","CLST","United States","United States Dollar","America/New_York","09:30", "16:00");
        var exchange5 = new Exchange(5L,"Wall Street Access Nyc","WABR","WABR","United States","United States Dollar","America/New_York","09:30", "16:00");
        var exchange6 = new Exchange(6L,"Marex Spectron Europe Limited - Otf","MSEL OTF","MSEL","Ireland","Euro","Europe/Dublin","08:00", "16:30");
        return new PageImpl<>(List.of(exchange1,exchange2,exchange3,exchange4,exchange5,exchange6), PageRequest.of(0, 10), 2);
    }

    private List<Exchange> getExchangesListNameLike() {
        var exchange3 = new Exchange(3L,"Cboe Edga U.s. Equities Exchange Dark","EDGADARK","EDGD","United States","United States Dollar","America/New_York","09:30", "16:00");
        var exchange4 = new Exchange(4L,"Clear Street","CLST","CLST","United States","United States Dollar","America/New_York","09:30", "16:00");
         return List.of(exchange3,exchange4);
    }
}
