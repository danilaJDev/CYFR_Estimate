package com.example.estimateapp.repository;

import com.example.estimateapp.model.EstimateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateItemRepository extends JpaRepository<EstimateItem, Long> {
}
