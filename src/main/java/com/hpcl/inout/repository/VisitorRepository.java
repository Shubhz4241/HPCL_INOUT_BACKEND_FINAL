package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hpcl.inout.entity.Visitor;


public interface VisitorRepository extends JpaRepository<Visitor, Long> 
{
	Visitor findByUniqueId(String uniqueId);

	@Query(value = "SELECT COUNT(*) FROM visitor WHERE full_name IS NOT NULL", nativeQuery = true)
	long countVisitorsWithFullNameNotNull();
}

