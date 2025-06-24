package com.hpcl.inout.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Packed;

public interface PackedRepository extends JpaRepository<Packed, Long> {
  Packed findByUniqueId(String paramString);
  Packed findTopByQrOrderByIdDesc(String qrcode);
}
