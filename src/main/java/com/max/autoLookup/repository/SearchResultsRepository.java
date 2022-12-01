package com.max.autoLookup.repository;

import com.max.autoLookup.model.SearchResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchResultsRepository extends JpaRepository<SearchResults,String> {

}
