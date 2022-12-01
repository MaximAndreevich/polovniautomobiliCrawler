package com.max.autoLookup;

import com.max.autoLookup.model.SearchResults;
import com.max.autoLookup.repository.SearchResultsRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.max.autoLookup.Constants.BASE_URL;

@Service
@RequiredArgsConstructor
public class SearchResultsPageParser {


    private final SearchResultsRepository repo;


    public static final String HARDCODED_SEARCH_CRITERIA = BASE_URL + "/auto-oglasi/pretraga?brand=alfa-romeo&model%5B%5D=brera&price_to=&year_from=&year_to=&showOldNew=all&submit_1=&without_price=1";


    public int exec() {
        Document targetPage;
        org.jsoup.Connection connection = Jsoup.connect(HARDCODED_SEARCH_CRITERIA);
        try {
            targetPage = connection.get();
        } catch (IOException e) {
            return 500;
        }
        //TODO: handle 500 with more information
        //TODO: save search result Elements for debug from file

        List<SearchResults> linksToParse = new ArrayList<>();
        Elements adData = targetPage.body().getElementsByClass("ordinaryClassified");
        adData
                .forEach(elements -> {
                    String adLink = elements.getElementsByTag("a").get(0).attr("href");
                    adLink = StringUtils.trimTrailingCharacter(adLink, '?');
                    String adTitle = elements.getElementsByTag("H2").text();
                    getAdNumber(adLink);
                    String adCity = elements.getElementsByClass("city").text();
                    String adPrice = elements.getElementsByClass("price").text();
                    linksToParse.add(SearchResults.builder()
                            .id(getAdNumber(adLink))
                            .name(adTitle.strip())
                            .price(adPrice)
                            .link(adLink)
                            .city(adCity)
                            .Status("NEW").build());
                });
        try {
            saveChanges(linksToParse);
            return 200;
        } catch (SQLException e) {
            return 500;
        }

        //TODO: table with search results, flag to skip
        //TODO: mode to process  prepared records

    }

    private void saveChanges(List<SearchResults> linksToParse) throws SQLException {
        if (!CollectionUtils.isEmpty(linksToParse)) {
            System.out.println("do we have ?");
            repo.saveAll(linksToParse);
        }
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
            result = UUID.randomUUID().toString();
        }
        return result;
    }

}