package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.UniqueIdDetails;

public interface UniqueIdDetailsRepository extends JpaRepository<UniqueIdDetails, Long> {
  UniqueIdDetails findByUniqueId(String paramString);
}
