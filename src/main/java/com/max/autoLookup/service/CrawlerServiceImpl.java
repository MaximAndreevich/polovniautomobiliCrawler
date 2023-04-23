package com.max.autoLookup.service;

import com.max.autoLookup.CarDetailsPageParser;
import com.max.autoLookup.SearchPageParser;
import lombok.RequiredArgsConstructor;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.apache.http.HttpVersion.HTTP_1_1;

@RestController
@RequestMapping("/cardb")
@RequiredArgsConstructor
public class CrawlerServiceImpl implements ICrawlerService{

    private final SearchPageParser searchPageParser;
    private final CarDetailsPageParser carDetailsPageParser;

    @Override
     public BasicHttpResponse parseNewAds(String link){
        int code = searchPageParser.processSearchPage(link);
        return new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, code, httpCodeToPhrase(code)));
    }

    @Override
    public BasicHttpResponse processRecords(){
        System.out.println("ooooups");
        int code = carDetailsPageParser.parsePage();
        return new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, code, httpCodeToPhrase(code)));
    }

    private String httpCodeToPhrase(int code){
        HttpStatus status = HttpStatus.resolve(code);
        String statusPhrase =  String.valueOf(code);
        if(Objects.nonNull(status)){
            statusPhrase = status.getReasonPhrase();
        }
        return statusPhrase;
    }
}
