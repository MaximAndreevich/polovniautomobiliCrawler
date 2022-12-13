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

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SEARCH_RESULTS")
public class SearchResults {
    @Id
    @Column(updatable = false)
    @EqualsAndHashCode.Exclude
    Long id;
    @Column(name = "AD_ID")
    Long adId;
    @Column(name = "NAME")
    String name;
    @Column(name = "CITY")
    String city;
    @Column(name = "PRICE")
    Long price;
    @Column(name = "LINK")
    String link;
    @EqualsAndHashCode.Exclude
    @Column(name = "STATUS")
    String Status;
    @EqualsAndHashCode.Exclude
    String createTime;
    @EqualsAndHashCode.Exclude
    String lastUpdateTime;

}
