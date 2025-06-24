package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Bulk;


public interface BulkRepository extends JpaRepository<Bulk, Long> {
  Bulk findByUniqueId(String paramString);
  Bulk findTopByQrOrderByIdDesc(String qrcode);
}  