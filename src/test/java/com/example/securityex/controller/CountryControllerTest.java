package com.example.securityex.controller;

import com.example.securityex.model.Country;
import com.example.securityex.repository.CountryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(controllers = {CountryController.class})
@WebAppConfiguration
public class CountryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private CountryRepository countryRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldGetCountries() throws Exception {
        List<Country> countries = Stream.of("Spain", "Holand", "Portugal").map(Country::new)
                .collect(Collectors.toList());
        when(countryRepository.findAll()).thenReturn(countries);
        mockMvc.perform(get("/countries").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].countryName").value("Spain"))
                .andExpect(jsonPath("$.[1].countryName").value("Holand"))
                .andExpect(jsonPath("$.[2].countryName").value("Portugal"));
    }

    @Test
    public void shouldCreateCountry() throws Exception {
        when(countryRepository.save(any(Country.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));
        mockMvc.perform(post("/countries").accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                .content("{\"countryName\": \"Argentina\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryName").value("Argentina"));
    }

    @Test
    public void shouldFailIfCountryAlreadyExist() throws Exception {
        when(countryRepository.findById(anyString())).thenReturn(of(new Country("somecountry")));
        mockMvc.perform(post("/countries").accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                .content("{\"countryName\": \"existingCountry\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Country existingCountry already exists"));

    }
}