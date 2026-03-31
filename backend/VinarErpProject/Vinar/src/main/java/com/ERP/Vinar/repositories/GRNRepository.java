package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.GRNEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GRNRepository extends JpaRepository<GRNEntity, Long> {
	List<GRNEntity> findByPoId(Long poId);

}
