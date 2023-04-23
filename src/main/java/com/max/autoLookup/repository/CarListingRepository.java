package com.max.autoLookup.repository;

import com.max.autoLookup.model.CarListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarListingRepository extends JpaRepository<CarListing, Long> {
    Optional<CarListing> findByUniqueNumber(Long uniqueNumber);
}
