package com.hpcl.inout.service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Parking;
import com.hpcl.inout.repository.ParkingRepository;

@Service
public class ParkingService {

	@Autowired
	private ParkingRepository parkingRepository;

	public List<Parking> addParkingSlots(List<Integer> slots) {
		System.out.println("=========================================================");
		System.out.println(parkingRepository.getLastParkingSlot());
		long lastParkingSlot = parkingRepository.getLastParkingSlot() != null ? parkingRepository.getLastParkingSlot() : 0;
		AtomicInteger slotCounter = new AtomicInteger((int)lastParkingSlot);
		List<Parking> parking = slots.stream().map(intValue -> new Parking().setParkingSlotForParking((long)slotCounter.incrementAndGet())).toList();
		System.out.println(parking);
		return this.parkingRepository.saveAll(parking);
	}

	public List<Parking> getAllParkingSlots() {
		return this.parkingRepository.findAll();
	}

	public Long getParkingEmptySlots() {
		return this.parkingRepository.getParkingEmptySlots();
	}

	public Long getParkingFilledSlots() {
		return this.parkingRepository.getParkingFilledSlots();
	}

	public Parking updateParkingSlot(long id, Parking parking) {
		Parking existingParkingSlot = this.parkingRepository.findById(id).get();
		if(existingParkingSlot != null) {
			existingParkingSlot.setFullName(parking.getFullName());
			existingParkingSlot.setAadharNumber(parking.getAadharNumber());
			existingParkingSlot.setVehicalNumber(parking.getVehicalNumber());
			existingParkingSlot.setVehicalType(parking.getVehicalType());
			existingParkingSlot.setParkingDate(parking.getParkingDate());
			existingParkingSlot.setTimeIn(Time.valueOf(LocalTime.now()));
			return this.parkingRepository.save(existingParkingSlot);
		}
		return null;
	}

	public boolean parkOutSlot(long id) {
		Parking existingParkingSlot = this.parkingRepository.findById(id).get();
		if(existingParkingSlot != null) {
			existingParkingSlot.setFullName(null);
			existingParkingSlot.setAadharNumber(null);
			existingParkingSlot.setVehicalNumber(null);
			existingParkingSlot.setVehicalType(null);
			existingParkingSlot.setParkingDate(null);
			existingParkingSlot.setTimeIn(null);
			return this.parkingRepository.save(existingParkingSlot) != null ? true : false;
		}
		return false;
	}
	

}
