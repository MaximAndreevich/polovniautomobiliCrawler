package com.max.autoLookup;

import com.max.autoLookup.exception.PageParsingException;
import com.max.autoLookup.model.CarListing;
import com.max.autoLookup.model.PriceHistory;
import com.max.autoLookup.service.SearchResultsService;
import com.max.autoLookup.util.CommonUtils;
import com.max.autoLookup.util.SearchStatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchResultsPageParser {


    private final SearchResultsService searchResultsService;

    private final ExistingListingPriceUpdater existingListingPriceUpdater;

    public int exec(Document targetPage) {

        List<CarListing> linksToParse = new ArrayList<>();
        Elements adData = targetPage.body().getElementsByClass("ordinaryClassified");
        adData
                .forEach(elements -> {
                    String adLink = elements.getElementsByTag("a").get(0).attr("href");
                    Long adNumber;
                    try {
                        adNumber = CommonUtils.getUniqueAdNumberFromAdLink(adLink);
                    } catch (PageParsingException e) {
                        log.error("Error during getting ad number");
                        return;
                    }

                    Double adPrice = getAdPrice(elements);
                    if (searchResultsService.isListingExistInDb(adNumber)) {
                        existingListingPriceUpdater.checkForPriceUpdate(adNumber, adPrice);
                        return;
                    }
                    String previewImageLink = elements.getElementsByClass("lead").attr("data-src");
                    if (adLink.contains("?")) {
                        adLink = adLink.substring(0, adLink.indexOf("?"));
                    }
                    String adTitle = elements.getElementsByTag("H2").text();
                    String adCity = elements.getElementsByClass("city").text();
                    int imageCount = 0;
                    try {
                        imageCount = Integer.parseInt(elements.getElementsByClass("imageCount").text());
                    } catch (NumberFormatException e) {
                        log.error("Failed to get imageCount");
                    }
                    PriceHistory priceHistory = PriceHistory.builder()
                            .price(adPrice)
                            .createDate(Date.from(Instant.now()))
                            .build();
                    CarListing currentItem = CarListing.builder()
                            .uniqueNumber(adNumber)
                            .previewUrl(previewImageLink)
                            .imageCount(imageCount)
                            .title(adTitle.strip())
                            .url(adLink)
                            .priceHistory(List.of(priceHistory))
                            .city(adCity)
                            .listingDate(new Date())
                            .status(SearchStatusCode.NEW).build();

                    List<String> plates = processPlates(elements);
                    if (plates.size() == 8) {
                        //TODO: connect with Options an re write in lambda
                        currentItem.setModelYear(plates.get(0));
                        currentItem.setBodyType(plates.get(1));
                        currentItem.setOdometer(plates.get(2));
                        currentItem.setTransmissionType(plates.get(3));
                        currentItem.setEngineType(plates.get(4));
                        currentItem.setEngineVolume(plates.get(5));
                        currentItem.setEnginePower(plates.get(6));
                        currentItem.setDoorsNumber(plates.get(7));
                    }

                    linksToParse.add(currentItem);
                });
        try {
            saveChanges(linksToParse);
            return 200;
        } catch (SQLException e) {
            return 500;
        }

    }

    private Double getAdPrice(Element elements) {
        double result;
        try {
            String adPrice = elements.getElementsByClass("price").text();
            adPrice = adPrice.replaceAll("\\D", "");
            result = Double.parseDouble(adPrice);
        } catch (NumberFormatException e) {
            log.error("failed to parse the price");
            result = Double.parseDouble("0.00");
        }
        return result;
    }

    private List<String> processPlates(Element elements) {
        //there are 6 elements on each ad. it's always remains the same order
        List<String> plates = new ArrayList<>();

        Elements topSection = elements.getElementsByClass("top");
        Elements bottomSection = elements.getElementsByClass("bottom");
        try {
            if (topSection.size() != 3) {
                throw new PageParsingException("topSection incorrect size");
            }
            plates.add(topSection.get(0).text().replaceAll("\\D", "")); //year and bodyType
            plates.add(topSection.get(0).text().replaceAll("[^\\p{Alpha}]", ""));
            plates.add(topSection.get(1).text().replaceAll("\\D", "")); // odometer
            plates.add(topSection.get(2).text()); // transmission
            if (bottomSection.size() != 3) {
                throw new PageParsingException("bottomSection incorrect size");
            }
            elements.getElementsByClass("bottom");
            String forthPlate = bottomSection.get(0).text();
            plates.add(forthPlate.substring(0, forthPlate.indexOf("|")).strip()); //engine type and size
            plates.add(forthPlate.substring(forthPlate.indexOf("|") + 1).strip());

            plates.add(bottomSection.get(1).text());
            plates.add(bottomSection.get(2).text());

            if (plates.size() != 8) {
                throw new PageParsingException("incorrect result value");
            }
        } catch (PageParsingException | Exception e) {
            log.error("Search result parser didn't managed to get info from top plates" + e.getMessage());
        }
        return plates;
    }

    private void saveChanges(List<CarListing> ads) throws SQLException {
        searchResultsService.saveSearchResponse(ads);
    }

}