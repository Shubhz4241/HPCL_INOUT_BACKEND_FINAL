package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Contractorworkman;

public interface ContractorworkmanRepository extends JpaRepository<Contractorworkman, Long> {
  Contractorworkman findByUniqueId(String paramString);
}
