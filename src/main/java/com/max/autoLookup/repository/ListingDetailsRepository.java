package com.max.autoLookup.repository;

import com.max.autoLookup.model.ListingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingDetailsRepository extends JpaRepository<ListingDetails, Long> {

}
