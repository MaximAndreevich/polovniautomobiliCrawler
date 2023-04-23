package com.max.autoLookup.repository;

import com.max.autoLookup.model.ListingPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingPhotoRepository extends JpaRepository<ListingPhoto, Long> {
}
