package com.max.autoLookup.repository;

import com.max.autoLookup.model.DescriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescriptionHistoryRepository extends JpaRepository<DescriptionHistory, Long> {
}
