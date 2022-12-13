package com.max.autoLookup.service;

import org.apache.http.message.BasicHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("cardb")
@RequestMapping("/default")
public interface ICrawlerService {

    @GetMapping("/processRecords")
    BasicHttpResponse processRecords();

    @GetMapping("/parseNewAds")
    BasicHttpResponse   parseNewAds(String link);
}
