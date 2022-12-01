package com.max.autoLookup;

import com.max.autoLookup.model.CarDetailsModel;
import com.max.autoLookup.model.SearchResults;
import com.max.autoLookup.repository.CarDetailsRepository;
import com.max.autoLookup.repository.SearchResultsRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.max.autoLookup.Constants.BASE_URL;

@Service
@RequiredArgsConstructor
public class CarDetailsPageParser {

    private final SearchResultsRepository searchResultsRepository;
    private final CarDetailsRepository carDetailsRepository;

    public int parsePage(){

        List<SearchResults> toParse = searchResultsRepository.findAll().stream()
                .filter(record -> "NEW".equals(record.getStatus()))
                .toList();

        try {
            for (SearchResults searchResults : toParse) {
                parseAd(searchResults);
            }
        } catch (IOException e) {
            return 500;
        }
        //TODO: exceptions in lambda
        return 200;
    }

    private void parseAd(SearchResults ad) throws IOException {
        String url = BASE_URL + ad.getLink();
        Document targetPage;
        org.jsoup.Connection connection = Jsoup.connect(url);
        targetPage = connection.get();
        CarDetailsModel carDetails = new CarDetailsModel();

        carDetails.setName(ad.getName());
        carDetails.setAdURL(url);

        targetPage.getElementsByClass("description-wrapper").stream()
                .map(Element::text)
                .filter(StringUtils::hasText)
                .findFirst()
                .map(text -> text.substring(0,600))
                .ifPresent(carDetails::setDescription);

        targetPage.getElementsByClass("regularPriceColor").stream()
                .map(Element::text)
                .filter(StringUtils::hasText)
                .findFirst()
                .ifPresent(carDetails::setPrice);


        Elements grid = targetPage.getElementsByClass("divider");
        carDetails.setBrand(parseGrid(grid,"Marka"));
        carDetails.setModel(parseGrid(grid,"Model"));
        carDetails.setModelYear(parseGrid(grid,"Godište"));
        carDetails.setOdometer(parseGrid(grid,"Kilometraža"));
        carDetails.setEngineType(parseGrid(grid,"Gorivo"));
        carDetails.setEngineVolume(parseGrid(grid,"Kubikaža"));
        carDetails.setEnginePower(parseGrid(grid,"Snaga motora"));
        carDetails.setAdNumber(parseGrid(grid,"Broj oglasa:"));
        carDetails.setRegistration(parseGrid(grid,"Registrovan do"));

        carDetails.setId(String.valueOf(carDetails.hashCode()));


        carDetailsRepository.saveAndFlush(carDetails);
        ad.setStatus("PROCESSED");
        searchResultsRepository.save(ad);
    }
    private void parseGridBulk(Elements grid, List<String> dataParams){
        dataParams.forEach(param -> {
            String result = parseGrid(grid, param);
            //TODO:map params Srb to Eng to set it here
        });
    }
    private String parseGrid(Elements grid, String reqirement){
        AtomicReference<String> result = new AtomicReference<>();
        result.set("");
        grid.stream()
                .filter(element -> element.childNodes().size() == 3)
                .map(element -> element.children().get(0))
                .filter(element -> reqirement.equals(element.child(0).text()))
                .findFirst()
                .ifPresent(element -> result.set(element.child(1).text()));

        return result.get().equals("") ? reqirement : result.get();
    }
}
