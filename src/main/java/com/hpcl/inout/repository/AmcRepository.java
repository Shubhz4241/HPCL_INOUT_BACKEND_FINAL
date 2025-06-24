package com.hpcl.inout.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Amc;

public interface AmcRepository extends JpaRepository<Amc, Long> 
{
  Amc findByUniqueId(String uniqueId);
}

