package com.hpcl.inout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hpcl.inout.entity.Parking;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
	
	Parking findByParkingSlot(long parkingSlot);
	
	@Query(value = "SELECT p.parking_slot FROM Parking p ORDER BY p.id DESC LIMIT 1", nativeQuery = true)
	Long getLastParkingSlot();

	@Query("SELECT COUNT(p) FROM Parking p WHERE p.fullName IS NULL")
	Long getParkingEmptySlots();

	@Query("SELECT COUNT(p) FROM Parking p WHERE p.fullName != ''")
	Long getParkingFilledSlots();

}
