package com.max.autoLookup;

import com.max.autoLookup.model.CarListing;
import com.max.autoLookup.repository.ListingDetailsRepository;
import com.max.autoLookup.repository.CarListingRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.max.autoLookup.Constants.BASE_URL;

@Service
@RequiredArgsConstructor
public class CarDetailsPageParser {

    private final CarListingRepository carListingRepository;
    private final ListingDetailsRepository listingDetailsRepository;

    public int parsePage(){

        List<CarListing> toParse = carListingRepository.findAll().stream()
                .filter(record -> "NEW".equals(record.getStatus()))
                .toList();

        try {
            for (CarListing carListing : toParse) {
                parseAd(carListing);
            }
        } catch (IOException e) {
            return 500;
        }
        //TODO: exceptions in lambda
        return 200;
    }

    private void parseAd(CarListing listing) throws IOException {
//        String url = BASE_URL + listing.getUrl();
//        Document targetPage;
//        org.jsoup.Connection connection = Jsoup.connect(url);
//        targetPage = connection.get();
//        ListingDetails listingDetails = new ListingDetails();
//
//        listingDetails.setName(listing.getName());
//        listingDetails.setAdURL(url);
//
//        targetPage.getElementsByClass("description-wrapper").stream()
//                .map(Element::text)
//                .filter(StringUtils::hasText)
//                .findFirst()
//                .map(text -> text.substring(0,600))
//                .ifPresent(listingDetails::setDescription);
//
//        targetPage.getElementsByClass("regularPriceColor").stream()
//                .map(Element::text)
//                .filter(StringUtils::hasText)
//                .findFirst()
//                .ifPresent(listingDetails::setPrice);
//
//
//        Elements grid = targetPage.getElementsByClass("divider");
//        listingDetails.setBrand(parseGrid(grid,"Marka"));
//        listingDetails.setModel(parseGrid(grid,"Model"));
//        listingDetails.setModelYear(parseGrid(grid,"Godište"));
//        listingDetails.setOdometer(parseGrid(grid,"Kilometraža"));
//        listingDetails.setEngineType(parseGrid(grid,"Gorivo"));
//        listingDetails.setEngineVolume(parseGrid(grid,"Kubikaža"));
//        listingDetails.setEnginePower(parseGrid(grid,"Snaga motora"));
//        listingDetails.setAdNumber(parseGrid(grid,"Broj oglasa:"));
//        listingDetails.setRegistration(parseGrid(grid,"Registrovan do"));
//
//        listingDetails.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
//
//
//        carDetailsRepository.saveAndFlush(listingDetails);
//        listing.setStatus("PROCESSED");
//        searchResultsRepository.save(listing);
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
