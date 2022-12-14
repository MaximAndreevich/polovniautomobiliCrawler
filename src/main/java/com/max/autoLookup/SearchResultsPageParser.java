package com.max.autoLookup;

import com.max.autoLookup.model.PriceArchive;
import com.max.autoLookup.model.SearchResults;
import com.max.autoLookup.service.SearchResultsService;
import com.max.autoLookup.util.SearchStatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.max.autoLookup.Constants.BASE_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchResultsPageParser {


    private final SearchResultsService searchResultsService;


    public int exec(Document targetPage) {

        List<SearchResults> linksToParse = new ArrayList<>();
        List<PriceArchive> priceArchive = new ArrayList<>();
        Elements adData = targetPage.body().getElementsByClass("ordinaryClassified");
        adData
                .forEach(elements -> {
                    String adLink = elements.getElementsByTag("a").get(0).attr("href");
                    adLink = StringUtils.trimTrailingCharacter(adLink, '?');
                    String adTitle = elements.getElementsByTag("H2").text();
                    Long adNumber = Long.parseLong(getAdNumber(adLink));
                    Long adIdentifier = Math.abs(UUID.randomUUID().getMostSignificantBits());
                    String adCity = elements.getElementsByClass("city").text();
                    String adPrice = elements.getElementsByClass("price").text();
                    SearchResults currentItem = SearchResults.builder()
                            .id(adIdentifier)
                            .adId(adNumber)
                            .name(adTitle.strip())
                            .link(adLink)
                            .city(adCity)
                            .createTime(createTS())
                            .lastUpdateTime(createTS())
                            .Status(SearchStatusCode.NEW).build();
                    PriceArchive currentPrice = PriceArchive.builder()
                            .id(adIdentifier)
                            .adUUID(adIdentifier)
                            .price(adPrice)
                            .createdTime(createTS())
                            .lastCheckedTime(createTS())
                            .build();
                    linksToParse.add(currentItem);
                    priceArchive.add(currentPrice);
                });
        try {
            saveChanges(linksToParse, priceArchive);
            return 200;
        } catch (SQLException e) {
            return 500;
        }

    }

    private void saveChanges(List<SearchResults> ads, List<PriceArchive> prices) throws SQLException {
        searchResultsService.saveSearchResponse(ads, prices);
    }

    private String getAdNumber(String adLink) {
        String result = "";
        if (StringUtils.hasText(adLink)) {
            Pattern digitPattern = Pattern.compile("\\d{8}");
            Matcher match = digitPattern.matcher(adLink);
            if (match.find()) {
                result = match.group(0);
            } else {
                digitPattern = Pattern.compile("\\d{9}");
                match = digitPattern.matcher(adLink);
            }
            if (match.find()) {
                result = match.group(0);
            }
        }
        if (!StringUtils.hasText(result)) {
            log.error("can't parse ad ID. use random");
            result = UUID.randomUUID().toString();
        }
        return result;
    }

    private String createTS() {
        return new Timestamp(System.currentTimeMillis()).toString();
    }

}