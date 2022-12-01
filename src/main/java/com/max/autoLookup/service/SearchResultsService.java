package com.max.autoLookup.service;

import com.max.autoLookup.model.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.autoLookup.repository.SearchResultsRepository;

import java.util.List;

@Service
public class SearchResultsService {
    @Autowired
    SearchResultsRepository repo;

    public void saveSearchResponse(List<SearchResults> results){
        repo.saveAll(results);

    }
}
