package com.max.autoLookup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CAR_DETAILS")
public class CarDetailsModel {
    @Id
    Long id;
    String adNumber;
    String name;
    String brand;
    String model;
    String modelYear;
    String price;
    String registration;
    String city;
    String odometer;
    String engineType;
    String engineVolume;
    String enginePower;
    @Column(length = 600)
    String description;
    String adURL;
}
