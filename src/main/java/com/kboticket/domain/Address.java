package com.kboticket.domain;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {}

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
