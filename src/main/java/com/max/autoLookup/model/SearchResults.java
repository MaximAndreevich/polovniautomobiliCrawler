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
    Long adId;
    String name;
    String city;
    Long price;
    String link;
    @EqualsAndHashCode.Exclude
    String Status;
    @EqualsAndHashCode.Exclude
    String createTime;
    @EqualsAndHashCode.Exclude
    String lastUpdateTime;

}
