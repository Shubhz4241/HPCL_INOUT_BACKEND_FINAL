package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Sec;

public interface SecRepository extends JpaRepository<Sec, Long> {
  Sec findByUniqueId(String paramString);
}
