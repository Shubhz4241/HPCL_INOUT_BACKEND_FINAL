package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Gat;

public interface GatRepository extends JpaRepository<Gat, Long> {
  Gat findByUniqueId(String paramString);
}
