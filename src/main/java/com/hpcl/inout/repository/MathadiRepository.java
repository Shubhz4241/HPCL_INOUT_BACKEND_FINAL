package com.hpcl.inout.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hpcl.inout.entity.Mathadi;


@Repository
public interface MathadiRepository extends JpaRepository<Mathadi, Long> {
	Mathadi findByUniqueId(String paramString);
	  
	  @Query("SELECT m.fullName FROM Mathadi m WHERE m.fullName IS NOT NULL")
	  List<String> findFullNames();
}

