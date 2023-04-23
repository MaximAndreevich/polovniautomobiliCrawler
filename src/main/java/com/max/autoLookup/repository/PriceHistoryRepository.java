package com.max.autoLookup.repository;

import com.max.autoLookup.model.CarListing;
import com.max.autoLookup.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    Optional<List<PriceHistory>> findPriceHistoriesByCarListing(CarListing carListing);

    @Query("SELECT ph FROM PriceHistory ph WHERE ph.carListing.uniqueNumber = :carListingId AND ph.changeDate IS NULL")
    PriceHistory findLatestByCarListingId(@Param("carListingId") Long carListingId);
}
