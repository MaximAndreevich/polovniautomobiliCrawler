package com.max.autoLookup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long uniqueNumber;
    private String status;
    private String url;
    private String title;
    private String city;

    private String additionalPriceLabel;

    private String sellerType;
    // from the car card table
    private String modelYear;
    private String bodyType;
    private String odometer;
    private String transmissionType;
    private String engineType;
    private String engineVolume;
    private String enginePower;
    private Integer horsepower;
    private String doorsNumber;
    private String seatsNumber;
    // ---

    private String previewUrl;
    private Integer imageCount;

    private Date listingDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "car_listing_id")
    private List<PriceHistory> priceHistory;

    @ManyToOne
    @JoinColumn(name = "car_listing_id", insertable = false, updatable = false)
    private CarModel carModel;

}
