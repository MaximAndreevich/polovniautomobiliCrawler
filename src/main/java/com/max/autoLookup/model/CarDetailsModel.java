package com.max.autoLookup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CarDetailsModel {
    @Id
    String id;
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
    String description;
    String adURL;
}
