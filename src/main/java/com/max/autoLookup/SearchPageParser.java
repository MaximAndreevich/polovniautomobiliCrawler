package com.max.autoLookup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static com.max.autoLookup.Constants.BASE_URL;


@Service
@Slf4j
@RequiredArgsConstructor
public class SearchPageParser {

    public final SearchResultsPageParser searchResultsPageParser;

    private int textLengthBeforeAdsAmount = 38;
    private int adsOnOnePage = 25;
    private int localRun = 0;

    //TODO: add links constructor. Create extendable collections of year, engine type, models and brands

    public int processSearchPage(String searchPath) {
        Document searchPage = getPage(searchPath);
        welcomeMessage(searchPath);
        //https://www.polovniautomobili.com/auto-oglasi/pretraga?
        // brand=audi&price_to=&year_from=&year_to=&showOldNew=all&submit_1=&without_price=1
        // %5B0%5D = [0] %5B1%5D = [1] model%5B0%5D=80 model[n]]=model_name

        if (Objects.isNull(searchPage)) {
            log.error("Error during the page load");
            return 500;
        }
        if (isSearchPageErrored(searchPage)) {
            log.error("Looks like requested page does not exist: " + searchPath);
            return 502;
        }

        Integer pageAmount = getExpectedPageAmount(searchPage);
        if (Objects.isNull(pageAmount)) {
            log.error("An error occurred during the page preparation");
            return 500;
        }

        System.out.println("");
        if (localRun == 1 || pageAmount == 1) {
            log.info("Pages amount: " + pageAmount);
            searchResultsPageParser.exec(searchPage);
            log.info("Pages parsing completed");
            return 200;
        }
        log.info("Pages amount: " + pageAmount);
        for (int i = 1; i <= pageAmount; i++) {
            String nextPageURL = BASE_URL + searchPath + "&page=" + i;
            Document nextPage = getPage(nextPageURL);
            if (isSearchPageErrored(nextPage)) {
                System.out.println("Page preparing has been errored: " + nextPageURL + "\nProcessing has been stopped");
                break;
            }
            searchResultsPageParser.exec(nextPage);
            try {
                //to avoid captcha
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Pages parsing completed");
        return 200;
    }

    private void welcomeMessage(String searchPath) {
        log.info("\n\n\n");
        log.info("Started processing for:");
        if (searchPath.startsWith("testPages")) {
            log.info("test page from resources: " + searchPath);
            return;
        }
        try {
            int brandIndex = searchPath.indexOf("brand=") + 6;
            if (searchPath.indexOf("&", brandIndex) == -1) {
                log.info("Brand: " + searchPath.substring(brandIndex));
            } else {
                log.info("Brand: " + searchPath.substring(brandIndex, searchPath.indexOf("&", brandIndex)));
            }
            int modelIndex = searchPath.indexOf("model[]=") + 8;
            if (searchPath.indexOf("&", modelIndex) == -1) {
                log.info("model: " + searchPath.substring(modelIndex));
            } else {
                log.info("model: " + searchPath.substring(modelIndex, searchPath.indexOf("&", modelIndex)));
            }
        } catch (Exception e) {
            System.out.println("Just a minor problem occurred. code:78192");
        }

    }

    private Integer getExpectedPageAmount(Document searchPage) {
        Integer adsAmount = searchPage.body().getElementsByTag("small").stream()
                .map(Element::ownText)
                .filter(s -> s.startsWith("Prikazano od"))
                .map(e -> e.substring(e.indexOf("ukupno") + 7))
                .map(Integer::parseInt)
                .findFirst().orElse(0);
        if (adsAmount == 0) {
            return null;
        }
        if (adsAmount <= adsOnOnePage) {
            return 1;
        }
        return adsAmount % adsOnOnePage == 0 ? adsAmount / adsOnOnePage : adsAmount / adsOnOnePage + 1;
    }

    //path is already in /resources/
    private Document loadHTMLFromResources(String path) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Document result = null;
        try (InputStream is = classloader.getResourceAsStream(path)) {
            result = Jsoup.parse(is, "UTF-8", "");
        } catch (IOException e) {
            System.out.println("I'm sad. We have an error loading file" + "\n" + e.getMessage());
        }
        return result;
    }

    private Document getPage(String searchPath) {
        Document searchPage = null;
        if (searchPath.startsWith("testPages")) {
            searchPage = loadHTMLFromResources(searchPath);
            localRun = 1;
        } else {
            if (searchPath.startsWith("/auto-oglasi")) {
                searchPath = BASE_URL + searchPath;
            }
            try {
                searchPage = Jsoup.connect(searchPath).get();
            } catch (IOException e) {
                System.out.println("Couldn't load the page: " + searchPath + "\n" + e.getMessage());
            }
        }
        return searchPage;
    }

    private Boolean isSearchPageErrored(Document page) {
        String buttonThatOnlyAvailableInTheCorrectSearch = "klik na marku";
        Element node = page.body().getElementsByAttribute("data-label").stream()
                .filter(element -> element.attr("data-label").startsWith(buttonThatOnlyAvailableInTheCorrectSearch))
                .findFirst().orElse(null);
        return Objects.isNull(node);
    }
}
