package com.max.autoLookup.repository;

import com.max.autoLookup.model.PriceArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public interface PriceArchiveRepository  extends JpaRepository<PriceArchive,Long> {

    default List<PriceArchive> findAllByAdUUID(List<Long> adUUID){
        List<PriceArchive> result = new ArrayList<>();
        adUUID.forEach(id -> {
            try {
                List<PriceArchive> request = getAllByAdUUID(id);
                if(request.size()>=1){
                    request.get(0).getAdUUID();
                    result.addAll(request);
                }else {throw new EntityNotFoundException();}
            } catch (PersistenceException e) {
            }
        });
        return result;
    }

    List<PriceArchive> getAllByAdUUID(Long adId);
}
