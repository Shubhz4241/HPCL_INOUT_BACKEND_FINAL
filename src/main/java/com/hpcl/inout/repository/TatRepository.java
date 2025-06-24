package com.hpcl.inout.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Tat;

public interface TatRepository extends JpaRepository<Tat, Long> {
  Tat findByUniqueId(String paramString);
}
