package com.hpcl.inout.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hpcl.inout.entity.Parking;
import com.hpcl.inout.service.ParkingService;

@RestController
@RequestMapping("/parking")
@CrossOrigin(origins = "http://localhost:5173")
public class ParkingController {
	
	@Autowired
	private ParkingService parkingService;
	

	@PostMapping("/addParkingSlots")
	public List<Parking> addParkingSlots(@RequestBody List<Integer> slots) {
		List<Parking> newParkings = new ArrayList<>();

		for (Integer id : slots) {
			Parking parking = new Parking();
			newParkings.add(parking);
		}
		return parkingService.addParkingSlots(slots);
	}
	
	@GetMapping("/allParkingSlots")
	public ResponseEntity<Map<String, Object>> getAllParkingSlots(){
		List<Parking> parkingDetails = parkingService.getAllParkingSlots();
		Map<String, Object> response = new HashMap<>();
		response.put("parkingDetails", parkingDetails);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/getParkingEmptySlots")
	public ResponseEntity<Map<String, Object>> getParkingEmptySlots(){
		Long parkingEmptySlots = parkingService.getParkingEmptySlots();
		Map<String, Object> response = new HashMap<>();
		response.put("parkingEmptySlots", parkingEmptySlots);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/getParkingFilledSlots")
	public ResponseEntity<Map<String, Object>> getParkingFilledSlots(){
		Long parkingFilledSlots = parkingService.getParkingFilledSlots();
		Map<String, Object> response = new HashMap<>();
		response.put("parkingFilledSlots", parkingFilledSlots);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/updateParkingSlot/{id}")
	public ResponseEntity<Map<String, Object>> updateParkingSlot(@PathVariable long id, @RequestBody Parking parking){
		Parking parkingSlot = parkingService.updateParkingSlot(id, parking);
		Map<String, Object> response = new HashMap<>();
		response.put("parkingSlot", parkingSlot);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/parkOutSlot/{id}")
	public ResponseEntity<Map<String, Object>> parkOutSlot(@PathVariable long id){
		boolean dbStatus = parkingService.parkOutSlot(id);
		Map<String, Object> response = new HashMap<>();
		response.put("parkSatus", dbStatus ? "removed" : "false"); 
		return ResponseEntity.ok(response);
	}
	
	
}
