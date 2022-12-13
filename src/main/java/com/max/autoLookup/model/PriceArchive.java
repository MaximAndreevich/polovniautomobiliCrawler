package com.max.autoLookup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "PRICE_HISTORICAL_DATA")
public class PriceArchive {

    @Id
    @Column(updatable = false)
    @EqualsAndHashCode.Exclude
    private Long id;

    private Long adUUID;


    @Column(name = "PRICE")
    private String price;

    @Column(name = "CREATION_TIME")
    @EqualsAndHashCode.Exclude
    private String createdTime;
    @Column(name = "LAST_UPDATED")
    @EqualsAndHashCode.Exclude
    private String lastCheckedTime;
}
