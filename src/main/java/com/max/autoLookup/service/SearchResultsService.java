package com.max.autoLookup.service;

import com.max.autoLookup.model.CarListing;
import com.max.autoLookup.model.PriceHistory;
import com.max.autoLookup.repository.CarListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SearchResultsService {

    private final CarListingRepository carListingRepository;

    public void saveSearchResponse(List<CarListing> results) {
        //all search results ad Id
        carListingRepository.saveAll(results);
        //existing records appeared in search
        //Update details and prices for existing ads

        //Remove existing ads from result list
        //add rest of results as NEW to DB
    }

    public PriceHistory getActualPriceHistory(CarListing carListing) throws SQLException {
        return carListing.getPriceHistory().stream()
                .filter(e -> Objects.isNull(e.getChangeDate()))
                .findFirst()
                .orElseThrow(SQLException::new);

    }

    public boolean isListingExistInDb(Long listingId){
        try {
            CarListing listing = carListingRepository.findByUniqueNumber(listingId).orElseThrow(() -> new RuntimeException("No record found"));
        }catch (RuntimeException e){
            return false;
        }
        return true;
    }

    public CarListing findListingByUniqueId(Long listingId){
        CarListing listing;
        try {
            listing = carListingRepository.findByUniqueNumber(listingId).orElseThrow(() -> new RuntimeException("No record found"));
        }catch (RuntimeException e){
            listing = null;
        }
        return listing;
    }

}
