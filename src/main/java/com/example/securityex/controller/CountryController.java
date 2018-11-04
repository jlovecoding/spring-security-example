package com.example.securityex.controller;

import com.example.securityex.exception.BadRequestException;
import com.example.securityex.model.Country;
import com.example.securityex.repository.CountryRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/countries")
public class CountryController {

    private final CountryRepository countryRepository;

    public CountryController(final CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public List<Country> getCountries() {
        return countryRepository.findAll();
    }

    @PostMapping
    @Secured("ROLE_CREATE_COUNTRIES")
    public Country createCountry(@RequestBody final Country country) {
        if (countryRepository.findById(country.getCountryName()).isPresent()) {
            throw new BadRequestException(format("Country %s already exists", country.getCountryName()));
        }

        return countryRepository.save(country);
    }

}
