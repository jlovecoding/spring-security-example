package com.example.securityex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "countries")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    @Id
    private String countryName;
}
