package com.hpcl.inout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hpcl.inout.dto.QrRequest;
import com.hpcl.inout.repository.DrivergateRepo;
import com.hpcl.inout.repository.InscanRepository;
import com.hpcl.inout.service.BulkService;
import com.hpcl.inout.service.DrivergateService;
import com.hpcl.inout.service.InscanService;
import com.hpcl.inout.service.LicenseGateService;
import com.hpcl.inout.service.LicenseService;
import com.hpcl.inout.service.PackedService;
import com.hpcl.inout.service.TransportorService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/driverGate")
@CrossOrigin(origins = "http://localhost:5173")
public class DriverGateController {
	
	@Autowired
	private PackedService packedService;
	
	@Autowired
	private BulkService bulkService;
	
	@Autowired
	private TransportorService transportorService;
	
	@Autowired
	private DrivergateService drivergateService;
	
	@Autowired
	private DrivergateRepo drivergateRepo;
	
	@Autowired
	private InscanRepository inscanRepository;
	
	@Autowired
	private LicenseService licenseService;
	
	@Autowired
	private InscanService inscanService;
	
	@Autowired
	private LicenseGateService licenseGateService;
	
	@PostMapping("/drivergatescanin")
	public ResponseEntity<String> processForm(@RequestParam String inputValue) {
	    System.out.println("Input value: " + inputValue);
	    String inoutFlag = "";
	    String[] parts = inputValue.split("/");

	    if (parts.length == 3) {
	      String entityType = parts[0];
	      String category = parts[1];
	      String idStr = parts[2];

	      if ("HPNSK".equals(category)) {
	        try {
	          Long entityId = Long.parseLong(idStr);
	          String fullName = null;

	          switch (entityType) {
	            case "PT":
	              fullName = this.packedService.getFullName(entityId);
	              if (isInvalid(fullName)) return invalid("PT", entityId);
	              inoutFlag = this.packedService.processAndSaveDetails(entityId);
	              return success(inoutFlag, fullName);

	            case "BK":
	              fullName = this.bulkService.getFullName(entityId);
	              System.out.println(fullName);
	              if (isInvalid(fullName)) return invalid("BK", entityId);
	              inoutFlag = this.bulkService.processAndSaveDetails(entityId);
	              return success(inoutFlag, fullName);

	            case "TR":
	              fullName = this.transportorService.getFullName(entityId);
	              if (isInvalid(fullName)) return invalid("TR", entityId);
	              inoutFlag = this.transportorService.processAndSaveDetails(entityId);
	              return success(inoutFlag, fullName);
	          }

	          System.out.println("Unknown entity type: " + entityType);
	          return ResponseEntity.badRequest().body("Unknown EntityType: " + inputValue);

	        } catch (NumberFormatException e) {
	          System.out.println("Invalid ID format: " + idStr);
	          return ResponseEntity.badRequest().body("Invalid ID format: " + idStr);
	        }
	      }
	    }

	    return ResponseEntity.badRequest().body("Invalid input format: " + inputValue);
	  }

	  private boolean isInvalid(String name) {
	    return name == null || name.trim().isEmpty();
	  }

	  private ResponseEntity<String> invalid(String type, Long id) {
	    return ResponseEntity.badRequest().body("Invalid data for " + type + " with ID " + id);
	  }

	  private ResponseEntity<String> success(String flag, String name) {
	    System.out.println("inoutFlag " + flag);
	    return ResponseEntity.ok(flag + " " + name);
	  }
	  
	  
	  // Fixed REST API endpoint for scanning
	  @PostMapping("/scan")
	  public ResponseEntity<String> scanIn(@RequestParam("inputValue") String inputValue) {
		  System.out.println(inputValue);
	      try {
//	          System.out.println("Scanning QR: " + inputValue);
	          
	          QrRequest qrRequest = new QrRequest();
	          qrRequest.setQr(inputValue);
	          String result = drivergateService.scanIn(qrRequest);
	          
//	          System.out.println("Scan result: " + result);
	          
	          if (result != null && result.contains("Success")) {
	              return ResponseEntity.ok(result);
	          } else {
	              // Return proper error message instead of generic one
	              String errorMessage = (result != null && !result.trim().isEmpty()) 
	                  ? result 
	                  : "Failed to process QR code: " + inputValue;
	              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
	          }
	          
	      } catch (Exception e) {
	          System.err.println("Error processing scan: " + e.getMessage());
	          e.printStackTrace();
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	              .body("Error processing scan: " + e.getMessage());
	      }
	  }
	  
	  // REST API endpoint to get dashboard counts
	  @GetMapping("/counts")
	  public ResponseEntity<Map<String, Object>> getDashboardCounts() {
	      try {
	          Map<String, Object> counts = new HashMap<>();
	          
	          // Driver gate counts
	          long driverGateInCount = drivergateRepo.countByStatus("In");
	          counts.put("driverCount2", driverGateInCount);
	          counts.put("totalDriverGateRecordCount", driverGateInCount);
	          
	          // Main gate counts
	          long mainGateCount = inscanRepository.countByMainGateSatus("Y");
	          counts.put("mainGateCount", mainGateCount);
	          
	          // Inscan counts
	          counts.put("totalInscanRecordCountForOperation", 
	              inscanService.countByEntryDateTimeBetweenForOperation());
	          counts.put("totalInscanRecordCountForProject", 
	              inscanService.countByEntryDateTimeBetweenForProject());
	          counts.put("totalInscanRecordCountForVisitor", 
	              inscanService.countByEntryDateTimeBetweenForVisitor());
	          counts.put("driverCount", inscanService.getCountOfInscanDetailsForCurrentDay());
	          
	          // License gate counts
	          counts.put("totalLicenseGateRecordCountForOperation", 
	              licenseGateService.countByEntryDateTimeBetweenForOperationLicenseGate());
	          counts.put("totalLicenseGateRecordCount", 
	              licenseGateService.getCountOfLicensegateDetailsForCurrentDay());
	          counts.put("totalLicenseGateRecordCountForProject", 
	              licenseGateService.countByEntryDateTimeBetweenForProjectLicensegate());
	          counts.put("totalLicenseGateRecordCountForVisitor", 
	              licenseGateService.countByEntryDateTimeBetweenForVisitorLicensegate());
	          
	          return ResponseEntity.ok(counts);
	          
	      } catch (Exception e) {
	          System.err.println("Error fetching dashboard counts: " + e.getMessage());
	          e.printStackTrace();
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	              .body(Map.of("error", "Failed to fetch dashboard counts: " + e.getMessage()));
	      }
	  }
	  
	  // Keep the original Thymeleaf endpoint for backward compatibility
//	  @GetMapping("/driverGate")
//	  public String dashboard(org.springframework.ui.Model model) {
//	      long countByStatus = drivergateRepo.countByStatus("In");
//	      long countByMainGateSatus = inscanRepository.countByMainGateSatus("Y");
//	      
//	      model.addAttribute("driverCount2", countByStatus);
//	      model.addAttribute("totalInscanRecordCountForOperation", inscanService.countByEntryDateTimeBetweenForOperation());
//	      model.addAttribute("totalInscanRecordCountForProject", inscanService.countByEntryDateTimeBetweenForProject());
//	      model.addAttribute("totalInscanRecordCountForVisitor", inscanService.countByEntryDateTimeBetweenForVisitor());
//	      model.addAttribute("driverCount", inscanService.getCountOfInscanDetailsForCurrentDay());
//	      model.addAttribute("totalLicenseGateRecordCountForOperation", licenseGateService.countByEntryDateTimeBetweenForOperationLicenseGate());
//	      model.addAttribute("totalLicenseGateRecordCount", licenseGateService.getCountOfLicensegateDetailsForCurrentDay());
//	      model.addAttribute("totalLicenseGateRecordCountForProject", licenseGateService.countByEntryDateTimeBetweenForProjectLicensegate());
//	      model.addAttribute("totalLicenseGateRecordCountForVisitor", licenseGateService.countByEntryDateTimeBetweenForVisitorLicensegate());
//	      
//	      return "driverGate";
//	  }
}