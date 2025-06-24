package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Contractor;


public interface ContractorRepository extends JpaRepository<Contractor, Long> {
  Contractor findByUniqueId(String paramString);
}
