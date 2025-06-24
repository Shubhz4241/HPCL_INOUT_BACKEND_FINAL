package com.hpcl.inout.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Feg;

public interface FegRepository extends JpaRepository<Feg, Long> {
  Feg findByUniqueId(String paramString);
}
