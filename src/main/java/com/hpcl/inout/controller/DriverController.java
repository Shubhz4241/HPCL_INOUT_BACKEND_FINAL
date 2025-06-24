package com.hpcl.inout.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hpcl.inout.entity.Bulk;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.License;
import com.hpcl.inout.entity.Packed;
import com.hpcl.inout.entity.Transportor;
import com.hpcl.inout.entity.UniqueIdDetails;
import com.hpcl.inout.entity.User;
import com.hpcl.inout.repository.UserRepository;
import com.hpcl.inout.service.BulkService;
import com.hpcl.inout.service.InscanService;
import com.hpcl.inout.service.OfficerService;
import com.hpcl.inout.service.PackedService;
import com.hpcl.inout.service.TransportorService;
import com.hpcl.inout.service.UniqueIdDetailsService;

@RestController
@RequestMapping("drivers")
@CrossOrigin(origins = "http://localhost:5173")
public class DriverController {
	@Autowired
	private PackedService packedService;

	@Autowired
	private BulkService bulkService;

	@Autowired
	private TransportorService transportorService;

	//  @Autowired
	//  private WorkmanService workmanService;

	//	@Autowired
	//  private AmcService amcService;
	//  
	//  @Autowired
	//  private VisitorService visitorService;

	@Autowired
	private UniqueIdDetailsService uniqueIdDetailsService;

	@Autowired
	private OfficerService officerService;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private UserRepository userRepository;

	//  @Autowired
	//  private VisitorTokenIdRepository visitorTokenRepository;
	//  
	//  @Autowired
	//  private VisitorTokenIdService visitorTokenIdService;

	private void addUsernameAndRoleToResponse(Map<String, Object> response, Authentication authentication) {
		Object principal = authentication.getPrincipal();

		if (principal instanceof UserDetails userDetails) {
			String username = userDetails.getUsername();
			User user = this.userRepository.findByUserName(username);

			if (user != null) {
				response.put("username", username);
				response.put("userRole", user.getRole());
			}
		} else {
			response.put("username", "Unknown");
			response.put("userRole", "Unknown");
		}
	}

	private void addLicenseInfoToModel(Map<String, Object> response) {
		User admin = this.userRepository.getReferenceById(1); // Assuming admin has ID 1
		License license = admin.getLicense();

		long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), license.getExpirydate());

		if (remainingDays == 0L) {
			response.put("remainingdays", false);
		} else if (remainingDays == 1L) {
			response.put("onedayremain", true);
		} else {
			response.put("remainingdays", remainingDays);
		}
	}

	//	// Genarate QR COde
	//	@GetMapping("/driver/generateQRCode")
	//	@CrossOrigin(origins = "http://localhost:5173")
	//	public ResponseEntity<byte[]> generateQRCode(@RequestParam("data") String data) {
	//		int width = 300;
	//		int height = 300;
	//		String format = "png";
	//		Map<EncodeHintType, Object> hints = new HashMap<>();
	//		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	//		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
	//
	//		try {
	//			QRCodeWriter qrCodeWriter = new QRCodeWriter();
	//			BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
	//			int qrCodeWidth = bitMatrix.getWidth();
	//			int qrCodeHeight = bitMatrix.getHeight();
	//			BufferedImage qrCodeImage = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_RGB);
	//			for (int x = 0; x < qrCodeWidth; x++) {
	//				for (int y = 0; y < qrCodeHeight; y++) {
	//					qrCodeImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
	//				}
	//			}
	//
	//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//			ImageIO.write(qrCodeImage, format, baos);
	//			byte[] imageBytes = baos.toByteArray();
	//
	//			HttpHeaders headers = new HttpHeaders();
	//			headers.setContentType(MediaType.IMAGE_PNG);
	//
	//			return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	//		} catch (WriterException | java.io.IOException e) {
	//			e.printStackTrace();
	//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	//		}
	//	}


	/*-------------------------------- Driver Packed -----------------------------------------------------------------------------------------*/

	@PostMapping("/submitPacked")
	public List<Packed> createOfficers(@RequestBody List<Integer> packedIds) {
		List<Packed> newOfficers = new ArrayList<>();

		for (Integer id : packedIds) {
			Packed packed = new Packed();
			newOfficers.add(packed);
		}
		return packedService.addPacked(packedIds);
	}

	@GetMapping("/packed/inplant/{id}")
	public ResponseEntity<Map<String, Boolean>> checkPackedInPlantStatus(@PathVariable Long id) {
	    Map<String, Boolean> response = new HashMap<>();
	    String status = inscanService.getMainGateSatus(id, "PT"); // "PT" for Packed entity type
	    boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
	    response.put("isInPlant", isInPlant);
	    return ResponseEntity.ok(response);
	}

	@GetMapping("/packed")
	public Map<String, Object> getPackedDetails() {
		Map<String, Object> response = new HashMap<>();

		List<Packed> packedDetails = packedService.getAllPackedDetails();
		List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();

		response.put("packedDetails", packedDetails);
		response.put("inscanDetailsForOperation", inscanDetailsForOperation);

		return response;
	}


	@PostMapping("/savePacked")
	public ResponseEntity<?> savePacked(@RequestBody Packed packed) {
	    Map<String, String> response = new HashMap<>();

	    try {
	        // CREATE NEW PACKED
	        if (packed.getId() == null) {
	            Packed createdPacked = packedService.addPacked(packed);

	            if (createdPacked != null) {
	                response.put("message", "Packed saved successfully.");
	                return ResponseEntity.ok(response);
	            } else {
	                response.put("error", "A Packed entry with the same token number already exists.");
	                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	            }
	        }

	        // UPDATE EXISTING PACKED
	        Optional<Packed> packedOldOpt = packedService.getPackedById(packed.getId());

	        if (!packedOldOpt.isPresent()) {
	            response.put("error", "Packed entry not found.");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }

	        Packed packedOld = packedOldOpt.get();

	        // CASE A: Unique ID being added for the first time
	        if (packedOld.getUniqueId() == null || packedOld.getUniqueId().isEmpty()) {
	            UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(packed.getUniqueId());
	            if (existing != null) {
	                response.put("error", "UniqueId already exists.");
	                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	            }

	            UniqueIdDetails newUnique = new UniqueIdDetails();
	            newUnique.setUniqueId(packed.getUniqueId());
	            newUnique.setEntity("Packed " + packed.getId());
	            newUnique.setFullName(packed.getFullName());
	            newUnique.setMobileNumber(packed.getMobileNumber());
	            newUnique.setAddress(packed.getAddress());
	            uniqueIdDetailsService.saveUniqueIdDetails(newUnique);
	        }

	        // CASE B: Unique ID changed
	        else if (!packed.getUniqueId().equalsIgnoreCase(packedOld.getUniqueId())) {
	            UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(packed.getUniqueId());
	            if (existing != null) {
	                response.put("error", "UniqueId already used by someone else.");
	                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	            }

	            UniqueIdDetails oldUnique = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(packedOld.getUniqueId());
	            if (oldUnique != null) {
	                uniqueIdDetailsService.deleteUniqueIdDetails(oldUnique.getId());
	            }

	            UniqueIdDetails newUnique = new UniqueIdDetails();
	            newUnique.setUniqueId(packed.getUniqueId());
	            newUnique.setEntity("Packed " + packed.getId());
	            newUnique.setFullName(packed.getFullName());
	            newUnique.setMobileNumber(packed.getMobileNumber());
	            newUnique.setAddress(packed.getAddress());
	            uniqueIdDetailsService.saveUniqueIdDetails(newUnique);
	        }

	        // CASE C: Unique ID same — just update normally
	        Packed updated = packedService.updatePacked(packed.getId(), packed);
	        if (updated != null) {
	            response.put("message", "Packed updated successfully.");
	            return ResponseEntity.ok(response);
	        } else {
	            response.put("error", "Failed to update the packed entry.");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }

	    } catch (IllegalArgumentException e) {
	        response.put("error", "Error: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}


	
	
	
	
	@GetMapping("/deletePacked/{id}")
	public ResponseEntity<?> deletePackedDetails(@PathVariable("id") Long packedId) {
		try {
			Optional<Packed> packedOpt = this.packedService.getPackedById(packedId);
			if (packedOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Packed entry with ID " + packedId + " not found.");
			}

			String uniqueId = packedOpt.get().getUniqueId();
			Packed updatedPacked = this.packedService.deletePackedDetails(packedId);

			if (updatedPacked != null) {
				System.out.println("Unique Id to delete: " + uniqueId);
				UniqueIdDetails uniqueIdDetails = this.uniqueIdDetailsService
						.getUniqueIdDetailsByUniqueId(uniqueId);

				if (uniqueIdDetails != null) {
					this.uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetails.getId());
				}

				return ResponseEntity.ok("Packed details deleted successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Failed to delete packed details.");
			}

		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Error: " + e.getMessage());
		}
	}























	/*-------------------------------- Driver Bulk -----------------------------------------------------------------------------------------*/

	@PostMapping("/submitBulk")
	public List<Bulk> createBulk(@RequestBody List<Integer> bulkIds) {
		List<Bulk> newBulks = new ArrayList<>();

		for (Integer id : bulkIds) {
			Bulk bulk = new Bulk();
			newBulks.add(bulk);
		}
		return bulkService.addBulk(bulkIds);
	}

	@GetMapping("/bulk/inplant/{id}")
	public ResponseEntity<Map<String, Boolean>> checkBulkInPlantStatus(@PathVariable Long id) {
	    Map<String, Boolean> response = new HashMap<>();
	    String status = inscanService.getMainGateSatus(id, "BK"); // "BK" for Bulk entity type
	    boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
	    response.put("isInPlant", isInPlant);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/bulk")
	public Map<String, Object> getBulkDetails() {
		Map<String, Object> response = new HashMap<>();

		List<Bulk> bulkDetails = bulkService.getAllBulkDetails();
		List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();

		response.put("bulkDetails", bulkDetails);
		response.put("inscanDetailsForOperation", inscanDetailsForOperation);

		return response;
	}

	@PostMapping("/saveBulk")
	public ResponseEntity<?> saveBulk(@RequestBody Bulk bulk) {
		Map<String, String> response = new HashMap<>();
		System.out.println("Received Bulk Data -> ID: " + bulk.getId() +
				", FullName: " + bulk.getFullName() +
				", Address: " + bulk.getAddress() +
				", UniqueId: " + bulk.getUniqueId());

		try {
			// CASE 1: CREATE
			if (bulk.getId() == null) {
				System.out.println("Creating new Bulk...");
				Bulk createdBulk = bulkService.addBulk(bulk);

				if (createdBulk != null) {
					response.put("message", "Bulk saved successfully.");
					return ResponseEntity.ok(response);
				} else {
					response.put("error", "A Bulk entry with the same token number already exists.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
				}
			}
			// CASE 2: UPDATE
			else {
				System.out.println("Updating Bulk...");
				Optional<Bulk> bulkOldOpt = bulkService.getBulkById(bulk.getId());
				System.out.println("Fetched Existing Bulk: " + bulkOldOpt);

				if (!bulkOldOpt.isPresent()) {
					response.put("error", "Bulk entry not found.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
				}

				Bulk bulkOld = bulkOldOpt.get();
				System.out.println("Old Bulk: " + bulkOld);
				System.out.println("New UniqueId: " + bulk.getUniqueId());

				// CASE A: Adding Unique ID for the first time
				if (bulkOld.getUniqueId() == null || bulkOld.getUniqueId().isEmpty()) {
					UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(bulk.getUniqueId());

					if (existing != null) {
						response.put("error", "UniqueId already exists.");
						return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
					}

					UniqueIdDetails newUnique = new UniqueIdDetails();
					newUnique.setUniqueId(bulk.getUniqueId());
					newUnique.setEntity("Bulk " + bulk.getId());
					newUnique.setFullName(bulk.getFullName());
					newUnique.setMobileNumber(bulk.getMobileNumber());
					newUnique.setAddress(bulk.getAddress());
					uniqueIdDetailsService.saveUniqueIdDetails(newUnique);

					Bulk updated = bulkService.updateBulk(bulk.getId(), bulk);
					if (updated != null) {
						response.put("message", "Bulk updated successfully.");
						return ResponseEntity.ok(response);
					} else {
						response.put("error", "Failed to update the bulk entry.");
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
					}
				}

				// CASE B: Unique ID is being changed
				else if (!bulk.getUniqueId().equalsIgnoreCase(bulkOld.getUniqueId())) {
					UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(bulk.getUniqueId());

					if (existing != null) {
						response.put("error", "UniqueId already used by someone else.");
						return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
					}

					UniqueIdDetails oldUnique = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(bulkOld.getUniqueId());
					if (oldUnique != null) {
						uniqueIdDetailsService.deleteUniqueIdDetails(oldUnique.getId());
					}

					UniqueIdDetails newUnique = new UniqueIdDetails();
					newUnique.setUniqueId(bulk.getUniqueId());
					newUnique.setEntity("Bulk " + bulk.getId());
					newUnique.setFullName(bulk.getFullName());
					newUnique.setMobileNumber(bulk.getMobileNumber());
					newUnique.setAddress(bulk.getAddress());
					uniqueIdDetailsService.saveUniqueIdDetails(newUnique);

					Bulk updated = bulkService.updateBulk(bulk.getId(), bulk);
					if (updated != null) {
						response.put("message", "Bulk updated successfully.");
						return ResponseEntity.ok(response);
					} else {
						response.put("error", "Failed to update the bulk entry.");
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
					}
				}

				// CASE C: Unique ID remains same
				else {
					Bulk updated = bulkService.updateBulk(bulk.getId(), bulk);
					if (updated != null) {
						response.put("message", "Bulk updated successfully.");
						return ResponseEntity.ok(response);
					} else {
						response.put("error", "Failed to update the bulk entry.");
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			response.put("error", "Error: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@GetMapping("/deleteBulk/{id}")
	public ResponseEntity<?> deleteBulkDetails(@PathVariable("id") Long bulkId) {
		try {
			Optional<Bulk> bulkOpt = this.bulkService.getBulkById(bulkId);
			if (bulkOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Bulk entry with ID " + bulkId + " not found.");
			}

			String uniqueId = bulkOpt.get().getUniqueId();
			Bulk updatedBulk = this.bulkService.deleteBulkDetails(bulkId);

			if (updatedBulk != null) {
				System.out.println("Unique Id to delete: " + uniqueId);
				UniqueIdDetails uniqueIdDetails = this.uniqueIdDetailsService
						.getUniqueIdDetailsByUniqueId(uniqueId);

				if (uniqueIdDetails != null) {
					this.uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetails.getId());
				}

				return ResponseEntity.ok("Bulk details deleted successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Failed to delete bulk details.");
			}

		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Error: " + e.getMessage());
		}
	}
















	/*-------------------------------- Driver Transportor -----------------------------------------------------------------------------------------*/

	@PostMapping("/submitTransportor")
	public List<Transportor> createTransportors(@RequestBody List<Integer> transportorIds) {
	    List<Transportor> newTransportors = new ArrayList<>();

	    for (Integer id : transportorIds) {
	        Transportor transportor = new Transportor();
	        // Assume you may want to populate additional fields here
	        newTransportors.add(transportor);
	    }

	    return transportorService.addTransportor(transportorIds);
	}
	
	@GetMapping("/transportor/inplant/{id}")
	public ResponseEntity<Map<String, Boolean>> checkTransportorInPlantStatus(@PathVariable Long id) {
	    Map<String, Boolean> response = new HashMap<>();
	    String status = inscanService.getMainGateSatus(id, "TR"); // "TR" for Transportor entity type
	    boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
	    response.put("isInPlant", isInPlant);
	    return ResponseEntity.ok(response);
	}

	@GetMapping("/transportor")
	public Map<String, Object> getTransportorDetails() {
	    Map<String, Object> response = new HashMap<>();

	    List<Transportor> transportorDetails = transportorService.getAllTransportorDetails();
	    List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();

	    // Add details to the response map
	    response.put("transportorDetails", transportorDetails);
	    response.put("inscanDetailsForOperation", inscanDetailsForOperation);

	    return response; // Return the response map
	}


	@PostMapping("/saveTransportor")
	public ResponseEntity<?> saveTransportor(@RequestBody Transportor transportor) {
		Map<String, String> response = new HashMap<>();
		System.out.println("Received Transportor Data -> ID: " + transportor.getId() +
				", FullName: " + transportor.getFullName() +
				", Address: " + transportor.getAddress() +
				", UniqueId: " + transportor.getUniqueId()+
		", Truck No"+transportor.getTruckNumber());

		try {
			if (transportor.getId() == null) {
				System.out.println("Creating new Transportor...");
				Transportor createdTransportor = transportorService.addTransportor(transportor);

				if (createdTransportor != null) {
					response.put("message", "Transportor saved successfully.");
					return ResponseEntity.ok(response);
				} else {
					response.put("error", "A Transportor entry with the same token number already exists.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
				}
			} else {
				System.out.println("Updating Transportor...");
				System.out.println("firm NAME"+ transportor.getFirmName());
				Optional<Transportor> transportorOldOpt = transportorService.getTransportorById(transportor.getId());
				System.out.println("Fetched Existing Transportor: " + transportorOldOpt);

				if (!transportorOldOpt.isPresent()) {
					response.put("error", "Transportor entry not found.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
				}

				Transportor transportorOld = transportorOldOpt.get();
				System.out.println("Old Transportor: " + transportorOld);
				System.out.println("New UniqueId: " + transportor.getUniqueId());

				if (transportorOld.getUniqueId() == null || transportorOld.getUniqueId().isEmpty()) {
					UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(transportor.getUniqueId());

					if (existing != null) {
						response.put("error", "UniqueId already exists.");
						return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
					}

					UniqueIdDetails newUnique = new UniqueIdDetails();
					newUnique.setUniqueId(transportor.getUniqueId());
					newUnique.setEntity("Transportor " + transportor.getId());
					newUnique.setFullName(transportor.getFullName());
					newUnique.setMobileNumber(transportor.getMobileNumber());
					newUnique.setAddress(transportor.getAddress());
					
					uniqueIdDetailsService.saveUniqueIdDetails(newUnique);

					Transportor updated = transportorService.updateTransportor(transportor.getId(), transportor);
					if (updated != null) {
						response.put("message", "Transportor updated successfully.");
						return ResponseEntity.ok(response);
					} else {
						response.put("error", "Failed to update the transportor entry.");
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
					}
				} else if (!transportor.getUniqueId().equalsIgnoreCase(transportorOld.getUniqueId())) {
					UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(transportor.getUniqueId());

					if (existing != null) {
						response.put("error", "UniqueId already used by someone else.");
						return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
					}

					UniqueIdDetails oldUnique = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(transportorOld.getUniqueId());
					if (oldUnique != null) {
						uniqueIdDetailsService.deleteUniqueIdDetails(oldUnique.getId());
					}

					UniqueIdDetails newUnique = new UniqueIdDetails();
					newUnique.setUniqueId(transportor.getUniqueId());
					newUnique.setEntity("Transportor " + transportor.getId());
					newUnique.setFullName(transportor.getFullName());
					newUnique.setMobileNumber(transportor.getMobileNumber());
					newUnique.setAddress(transportor.getAddress());
					uniqueIdDetailsService.saveUniqueIdDetails(newUnique);

					Transportor updated = transportorService.updateTransportor(transportor.getId(), transportor);
					if (updated != null) {
						response.put("message", "Transportor updated successfully.");
						return ResponseEntity.ok(response);
					} else {
						response.put("error", "Failed to update the transportor entry.");
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
					}
				} else {
					Transportor updated = transportorService.updateTransportor(transportor.getId(), transportor);
					if (updated != null) {
						response.put("message", "Transportor updated successfully.");
						return ResponseEntity.ok(response);
					} else {
						response.put("error", "Failed to update the transportor entry.");
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			response.put("error", "Error: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@GetMapping("/deleteTransportor/{id}")
	public ResponseEntity<?> deleteTransportorDetails(@PathVariable("id") Long transportorId) {
		try {
			Optional<Transportor> transportorOpt = this.transportorService.getTransportorById(transportorId);
			if (transportorOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Transportor entry with ID " + transportorId + " not found.");
			}

			String uniqueId = transportorOpt.get().getUniqueId();
			Transportor updatedTransportor = this.transportorService.deleteTransportorDetails(transportorId);

			if (updatedTransportor != null) {
				System.out.println("Unique Id to delete: " + uniqueId);
				UniqueIdDetails uniqueIdDetails = this.uniqueIdDetailsService
						.getUniqueIdDetailsByUniqueId(uniqueId);

				if (uniqueIdDetails != null) {
					this.uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetails.getId());
				}

				return ResponseEntity.ok("Transportor details deleted successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Failed to delete transportor details.");
			}

		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Error: " + e.getMessage());
		}
	}
}
