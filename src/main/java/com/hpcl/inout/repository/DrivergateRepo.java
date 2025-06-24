package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Driver;
import com.hpcl.inout.entity.drivergate;

public interface DrivergateRepo extends JpaRepository<drivergate, Long>{

	drivergate findTopByQrOrderByIdDesc(String qrcode);
	
	
	long countByStatus(String status);
	
}
