package com.max.autoLookup;

import com.max.autoLookup.model.CarListing;
import com.max.autoLookup.model.PriceHistory;
import com.max.autoLookup.repository.PriceHistoryRepository;
import com.max.autoLookup.service.SearchResultsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExistingListingPriceUpdater {
    private final SearchResultsService searchResultsService;

    private final PriceHistoryRepository priceHistoryRepository;

    public void checkForPriceUpdate(Long uniqueAdNumber, Double actualPrice){
        CarListing existingListing = searchResultsService.findListingByUniqueId(uniqueAdNumber);
        if (Objects.isNull(existingListing)) {
            log.error("can't find listing");
            return;
        }
        PriceHistory oldPriceHistory;
        try {
            oldPriceHistory = searchResultsService.getActualPriceHistory(existingListing);
        }catch (SQLException e){
            log.error("An error occurred with price. Check logic");
            return;
        }

        // priceHistoryRepository.findLatestByCarListingId(adNumber);

        if (actualPrice.compareTo(oldPriceHistory.getPrice()) != 0) {
            Date now = Date.from(Instant.now());
            oldPriceHistory.setChangeDate(now);
            PriceHistory newPriceHistory = PriceHistory.builder()
                    .carListing(existingListing)
                    .createDate(now)
                    .price(actualPrice)
                    .oldPrice(oldPriceHistory.getPrice())
                    .build();

            priceHistoryRepository.save(oldPriceHistory);
            priceHistoryRepository.save(newPriceHistory);
        }
    }
}
