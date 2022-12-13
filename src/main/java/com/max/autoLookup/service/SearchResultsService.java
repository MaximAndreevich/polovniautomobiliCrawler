package com.max.autoLookup.service;

import com.max.autoLookup.model.PriceArchive;
import com.max.autoLookup.model.SearchResults;
import com.max.autoLookup.repository.CarDetailsRepository;
import com.max.autoLookup.repository.PriceArchiveRepository;
import com.max.autoLookup.util.SearchStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.autoLookup.repository.SearchResultsRepository;
import org.springframework.util.CollectionUtils;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchResultsService {

    private final SearchResultsRepository searchResultsRepository;
    private final PriceArchiveRepository priceArchiveRepository;

    public void saveSearchResponse(List<SearchResults> results, List<PriceArchive> prices){
        //all search results ad Id
        List<Long> adIdList = results.stream().map(SearchResults::getAdId).map(e-> Long.parseLong(e.toString())).toList();
        //existing records appeared in search
        List<SearchResults> existingAdsAppearedInSearch = searchResultsRepository.findAllByAdIds(adIdList);
        List<Long> existingUUIDList = existingAdsAppearedInSearch.stream().map(SearchResults::getId).toList();

        List<PriceArchive> existingPrices = priceArchiveRepository.findAllByAdUUID(existingUUIDList);

        //Update details and prices for existing ads
        if(!CollectionUtils.isEmpty(existingAdsAppearedInSearch) && !CollectionUtils.isEmpty(existingPrices)){
            saveUpdatedCarDetails(existingAdsAppearedInSearch,results, existingPrices, prices);
        }

        //Remove existing ads from result list
        List<Long> existindAdIds = existingAdsAppearedInSearch.stream().map(SearchResults::getAdId).toList();
        results = results.stream().filter(e -> !existindAdIds.contains(e.getAdId())).collect(Collectors.toList());
        prices = prices.stream().filter(e -> !existingUUIDList.contains(e.getAdUUID())).collect(Collectors.toList());
        //add rest of results as NEW to DB
        try {
            saveNewRecords(results,prices);

        }catch (PersistenceException e){
            System.out.println("exception during saving new data");
        }

    }

    private void saveUpdatedCarDetails(List<SearchResults> existingResults,List<SearchResults> newResults, List<PriceArchive> existingPrices, List<PriceArchive> newPrices){
        //compare prices, save new, update links

        existingResults.forEach(ad -> {
            SearchResults newSearchResult = newResults.stream().filter(r-> ad.getAdId().equals(r.getAdId())).findFirst().orElse(null);
            if(Objects.isNull(newSearchResult)){
                return;
            }
            newSearchResult.setPrice(ad.getPrice());
            PriceArchive newPrice = newPrices.stream()
                    .filter(price-> price.getId().equals(newSearchResult.getId()))
                    .findFirst()
                    .orElse(new PriceArchive());
            PriceArchive oldPrice = priceArchiveRepository.getReferenceById(ad.getId());
            //TODO: currently we are checking only price updates. Need to check name as well (links?)
            if(oldPrice.getPrice().equals(newPrice.getPrice())){
                oldPrice.setLastCheckedTime(newPrice.getCreatedTime());
                priceArchiveRepository.save(oldPrice);
            }else {
                SearchResults oldAd = searchResultsRepository.getReferenceById(ad.getId());
                oldAd.setPrice(newPrice.getId());
                oldAd.setStatus(SearchStatusCode.REPROCESSED);
                oldAd.setLastUpdateTime(ad.getCreateTime());
                oldAd.setName(ad.getName());
                newPrice.setAdUUID(ad.getId());
                priceArchiveRepository.save(newPrice);
                searchResultsRepository.save(oldAd);
            }
        });

    }

    private void saveNewRecords(List<SearchResults> results, List<PriceArchive> prices) {
        if(CollectionUtils.isEmpty(results)){
            return;
        }
        results.forEach(result -> {
            result.setPrice(result.getId());
            priceArchiveRepository.saveAndFlush(prices.stream().filter(p ->p.getId().equals(result.getId())).findFirst().orElseThrow(PersistenceException::new));
            searchResultsRepository.saveAndFlush(result);
        });
    }

    public void saveCarDetails(){

    }

}
