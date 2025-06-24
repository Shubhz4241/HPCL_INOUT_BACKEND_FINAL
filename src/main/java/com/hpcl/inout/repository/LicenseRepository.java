package com.hpcl.inout.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import com.hpcl.inout.entity.License;

public interface LicenseRepository extends JpaRepository<License, Integer> {
  License findBylicensekey(@RequestParam("licensekey") String paramString);
  
  boolean existsByLicensekey(String paramString);
}
