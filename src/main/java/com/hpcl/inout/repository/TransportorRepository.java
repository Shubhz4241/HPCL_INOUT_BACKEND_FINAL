package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Transportor;


public interface TransportorRepository extends JpaRepository<Transportor, Long> {
  Transportor findByUniqueId(String paramString);
  Transportor findTopByQrOrderByIdDesc(String qrcode);
}
