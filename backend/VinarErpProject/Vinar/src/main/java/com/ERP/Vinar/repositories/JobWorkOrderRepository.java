package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.JobWorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobWorkOrderRepository extends JpaRepository<JobWorkOrder, Long> {
}
