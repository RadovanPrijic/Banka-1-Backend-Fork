package org.banka1.bankservice.bootstrap;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.bankservice.domains.entities.user.*;
import org.banka1.bankservice.repositories.CompanyRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.banka1.bankservice.services.CurrencyExchangeService;
import org.banka1.bankservice.services.CurrencyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
//@Profile("local")
@Profile("!test_it")
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CurrencyService currencyService;
    private final CurrencyExchangeService currencyExchangeService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

//        // CURRENCY DATA
//        List<CurrencyCsvBean> currencyCsvBeanList = getCurrencies();
//        currencyService.persistCurrencies(currencyCsvBeanList);
//        System.out.println("Currency Data Loaded!");

        currencyExchangeService.loadForex();
        System.out.println("Forexes loaded");

//        BankUser employee1 = BankUser.builder()
//                .firstName("Zoran")
//                .lastName("Stosic")
//                .birthDate(LocalDate.of(1981, 1, 29))
//                .gender(Gender.MALE)
//                .email("admin@admin.com")
//                .phoneNumber("0622495678")
//                .homeAddress("Bulevar Kralja Aleksandra 52")
//                .password(passwordEncoder.encode("admin"))
//                .position(Position.SYSTEM_ADMIN)
//                .department(Department.IT)
//                .roles(List.of("ROLE_EMPLOYEE"))
//                .build();
//
//        BankUser employee2 = BankUser.builder()
//                .firstName("Luka")
//                .lastName("Lukacevic")
//                .birthDate(LocalDate.of(1983, 6, 1))
//                .gender(Gender.MALE)
//                .email("luka.lukacevic@useremail.com")
//                .phoneNumber("0651452580")
//                .homeAddress("Bulevar Despota Stefana 37")
//                .password(passwordEncoder.encode("lukalukacevic"))
//                .position(Position.MANAGER)
//                .department(Department.FINANCE)
//                .roles(List.of("ROLE_EMPLOYEE"))
//                .build();
//
//        BankUser client1 = BankUser.builder()
//                .firstName("Marko")
//                .lastName("Markovic")
//                .birthDate(LocalDate.of(1990, 10, 5))
//                .gender(Gender.MALE)
//                .email("marko.markovic@useremail.com")
//                .phoneNumber("0651678989")
//                .homeAddress("Njegoseva 25")
//                .password(passwordEncoder.encode("markomarkovic"))
//                .roles(List.of("ROLE_CLIENT"))
//                .build();
//
//        BankUser client2 = BankUser.builder()
//                .firstName("Petar")
//                .lastName("Petrovic")
//                .birthDate(LocalDate.of(1986, 3, 17))
//                .gender(Gender.MALE)
//                .email("petar.petrovic@useremail.com")
//                .phoneNumber("0651224390")
//                .homeAddress("Kralja Milana 34")
//                .password(passwordEncoder.encode("petarpetrovic"))
//                .roles(List.of("ROLE_CLIENT"))
//                .build();
//
//        BankUser client3 = BankUser.builder()
//                .firstName("Jovana")
//                .lastName("Jovanovic")
//                .birthDate(LocalDate.of(1988, 9, 11))
//                .gender(Gender.FEMALE)
//                .email("jovana.jovanovic@useremail.com")
//                .phoneNumber("0633456751")
//                .homeAddress("Budimska 12")
//                .password(passwordEncoder.encode("jovanajovanovic"))
//                .roles(List.of("ROLE_CLIENT"))
//                .build();
//
//        Company company1 = Company.builder()
//                .companyName("Monsanto")
//                .phoneNumber("0621586732")
//                .faxNumber("6347642835")
//                .vatIdNumber(342243)
//                .identificationNumber(343242)
//                .activityCode(121)
//                .registryNumber(233)
//                .build();

//        userRepository.save(employee1);
////        userRepository.save(employee2);
//        userRepository.save(client1);
//        userRepository.save(client2);
//        userRepository.save(client3);
//        companyRepository.save(company1);

//        System.out.println("Data loaded");
    }

    public List<CurrencyCsvBean> getCurrencies() throws IOException {
        FileReader fileReader;
        try {
            fileReader = new FileReader(ResourceUtils.getFile("bank-service/csv-files/currencies.csv"));
        } catch (Exception e) {
            fileReader = new FileReader(ResourceUtils.getFile("classpath:csv/currencies.csv"));
        }

        return new CsvToBeanBuilder<CurrencyCsvBean>(fileReader)
                .withType(CurrencyCsvBean.class)
                .withSkipLines(1)
                .build()
                .parse();
    }
}
