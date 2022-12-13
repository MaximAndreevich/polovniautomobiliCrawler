package com.max.autoLookup.repository;

import com.max.autoLookup.model.SearchResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public interface SearchResultsRepository extends JpaRepository<SearchResults,Long> {

    List<SearchResults> findAllByAdId(Long adId);
    default List<SearchResults> findAllByAdIds(List<Long> idIdList){
        List<SearchResults> result=new ArrayList<>();
        idIdList.forEach(id -> {
            try {
                List<SearchResults> query= findAllByAdId(id);
                if(query.size() >= 1){
                    query.get(0).getId();
                    result.add(query.get(0));
                }
            }catch (PersistenceException e){
                System.out.println("exception here");
            }

        });
        return result;
    };
}
