package com.hpcl.inout.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Workman;

public interface WorkmanRepository extends JpaRepository<Workman, Long> 
{
	Workman findByUniqueId(String uniqueId);
}

