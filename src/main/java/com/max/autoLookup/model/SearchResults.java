package com.max.autoLookup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResults {
    @Id
    String id;
    String name;
    String city;
    String price;
    String Status;
    String link;

}
