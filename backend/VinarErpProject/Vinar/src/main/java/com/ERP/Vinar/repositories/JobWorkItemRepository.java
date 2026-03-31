package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.JobWorkItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobWorkItemRepository extends JpaRepository<JobWorkItem, Long> {
}
