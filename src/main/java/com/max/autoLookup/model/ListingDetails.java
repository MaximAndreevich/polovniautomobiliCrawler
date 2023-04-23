package com.max.autoLookup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String registration;

    @Column(length = 800)
    String description;

    @ManyToMany
    @JoinTable(
            name = "car_details_options",
            joinColumns = @JoinColumn(name = "car_details_id"),
            inverseJoinColumns = @JoinColumn(name = "car_option_id")
    )
    private Set<Options> options;


    @OneToMany(mappedBy = "listingDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListingPhoto> photos;
}
