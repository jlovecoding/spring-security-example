package com.example.securityex.repository;

import com.example.securityex.model.Country;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class CountryRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void shouldFindAll() {
        //Given
        Stream.of("Spain", "England").map(Country::new).forEach(testEntityManager::persistAndFlush);
        //When
        List<Country> countries = countryRepository.findAll();
        //Then
        assertThat(countries).hasSize(2);
        assertThat(countries.stream().map(Country::getCountryName).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("Spain", "England");
    }

    @Test
    public void shouldSave() {
        //Given
        Country newCountry = new Country("newcountry");
        //When
        Country saved = countryRepository.save(newCountry);
        //Then
        assertThat(saved.getCountryName()).isEqualTo("newcountry");
        assertThat(countryRepository.findById("newcountry")).isPresent();
        assertThat(countryRepository.findById("newcountry").get().getCountryName()).isEqualTo("newcountry");
    }

}