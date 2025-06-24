package com.hpcl.inout.controller;

import com.hpcl.inout.dto.VisitorRequest;
import com.hpcl.inout.entity.UniqueIdDetails;
import com.hpcl.inout.entity.Visitor;
import com.hpcl.inout.entity.VisitorTokenId;
import com.hpcl.inout.repository.VisitorRepository;
import com.hpcl.inout.repository.VisitorTokenIdRepository;
import com.hpcl.inout.service.UniqueIdDetailsService;
import com.hpcl.inout.service.VisitorService;
import com.hpcl.inout.service.VisitorTokenIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/visitors")
public class VisitorController {

	private static final Logger logger = LoggerFactory.getLogger(VisitorController.class);
	private static final String IMAGE_FOLDER_PATH = "src/main/resources/static/img/";

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private VisitorTokenIdRepository visitorTokenIdRepository;

	@Autowired
	private VisitorTokenIdService visitorTokenIdService;

	@Autowired
	private UniqueIdDetailsService uniqueIdDetailsService;
	
	@Autowired
	private VisitorRepository visitorRepository;

	@PostMapping("/addVisitors")
	public ResponseEntity<?> createVisitors(@RequestBody List<Integer> visitorIds) {
		List<Visitor> newVisitors = visitorService.addVisitor(visitorIds);
		return ResponseEntity.ok(newVisitors);
	}

	@GetMapping("/allVisitors")
	public ResponseEntity<Map<String, Object>> getVisitorDetails() {
		List<Visitor> visitorDetails = visitorService.getAllVisitorDetails();
		Map<String, Object> response = new HashMap<>();
		response.put("visitorDetails", visitorDetails);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveVisitor(@RequestBody VisitorRequest request) {
		try {
			Visitor visitor = request.getVisitor();
			String imgData = request.getImgData();

			if (visitor.getId() == null) {
				visitor.setImageName(saveImgAndGetName(imgData, visitor));
				visitor.setRegular(true);

				Visitor createdVisitor = visitorService.addVisitor(visitor);
				if (createdVisitor != null) {
					return ResponseEntity.ok("Visitor saved successfully.");
				} else {
					return ResponseEntity.badRequest().body("A Visitor with the same token number already exists.");
				}
			} else {
				Optional<Visitor> visitorOldOpt = visitorService.getVisitorById(visitor.getId());
				if (visitorOldOpt.isEmpty()) {
					return ResponseEntity.badRequest().body("Visitor not found.");
				}

				Visitor visitorOld = visitorOldOpt.get();

				if (visitorOld.getUniqueId() == null || visitorOld.getUniqueId().isEmpty()) {
					UniqueIdDetails existingUniqueId = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(visitor.getUniqueId());
					if (existingUniqueId != null) {
						return ResponseEntity.badRequest().body("Unique ID already exists.");
					} else {
						saveUniqueIdDetails(visitor);
						visitor.setImageName(saveImgAndGetName(imgData, visitor));
						visitor.setRegular(visitor.getUniqueId() != null && !visitor.getUniqueId().isEmpty());

						Visitor updatedVisitor = visitorService.updateVisitor(visitor.getId(), visitor);
						return updatedVisitor != null ?
								ResponseEntity.ok("Visitor saved successfully.") :
									ResponseEntity.badRequest().body("Failed to save the Visitor.");
					}
				} else if (!visitor.getUniqueId().equalsIgnoreCase(visitorOld.getUniqueId())) {
					UniqueIdDetails existingUniqueId = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(visitor.getUniqueId());
					if (existingUniqueId != null) {
						return ResponseEntity.badRequest().body("Unique ID already used by someone else.");
					} else {
						UniqueIdDetails oldUniqueId = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(visitorOld.getUniqueId());
						if (oldUniqueId != null) {
							uniqueIdDetailsService.deleteUniqueIdDetails(oldUniqueId.getId());
						}

						saveUniqueIdDetails(visitor);
						visitor.setImageName(saveImgAndGetName(imgData, visitor));
						visitor.setRegular(true);

						Visitor updatedVisitor = visitorService.updateVisitor(visitor.getId(), visitor);
						return updatedVisitor != null ?
								ResponseEntity.ok("Visitor saved successfully.") :
									ResponseEntity.badRequest().body("Failed to save the Visitor.");
					}
				} else {
				    visitor.setImageName(getUpdatedImageName(imgData, visitor, visitorOldOpt.get()));
				    Visitor updatedVisitor = visitorService.updateVisitor(visitor.getId(), visitor);
				    return updatedVisitor != null ?
				        ResponseEntity.ok("Visitor updated successfully.") :
				        ResponseEntity.badRequest().body("Failed to update the Visitor.");
				}
			}

		} catch (Exception e) {
			logger.error("Error occurred while saving visitor", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	private String getUpdatedImageName(String imgData, Visitor newVisitor, Visitor oldVisitor) {
	    if (StringUtils.hasText(imgData)) {
	        // Save new image and return name
	        return saveImgAndGetName(imgData, newVisitor);
	    } else {
	        // Return previously stored image name
	        return oldVisitor.getImageName();
	    }
	}

	private void saveUniqueIdDetails(Visitor visitor) {
		UniqueIdDetails details = new UniqueIdDetails();
		details.setUniqueId(visitor.getUniqueId());
		details.setEntity("Visitor " + visitor.getId());
		details.setFullName(visitor.getFullName());
		details.setMobileNumber(visitor.getMobileNumber());
		details.setAddress(visitor.getAddress());
		uniqueIdDetailsService.saveUniqueIdDetails(details);
	}

	private String saveImgAndGetName(String imgData, Visitor visitor) {
		String imageName = "";
		if (StringUtils.hasText(imgData)) {
			try {
				byte[] decodedImg = Base64.getDecoder().decode(imgData.split(",")[1]);
				String lastFourDigits = visitor.getUniqueId().substring(visitor.getUniqueId().length() - 4);
				imageName = visitor.getFullName().replaceAll("\\s+", "_") + "_" + lastFourDigits + ".jpg";

				Path uploadDir = Paths.get(IMAGE_FOLDER_PATH);
				if (!Files.exists(uploadDir)) {
					Files.createDirectories(uploadDir);
				}
				Path imagePath = uploadDir.resolve(imageName);
				Files.write(imagePath, decodedImg);

			} catch (Exception e) {
				logger.error("Error occurred while saving image", e);
			}
		}
		return imageName;
	}
	
	
	@GetMapping("/image/{id}")
	public ResponseEntity<byte[]> getVisitorImage(@PathVariable Long id) {
		Optional<Visitor> optionalVisitor = visitorRepository.findById(id);
		if (optionalVisitor.isPresent()) {
			Visitor visitor = optionalVisitor.get();
			String imageName = visitor.getImageName();

			if (imageName == null || imageName.trim().isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Path imagePath = Paths.get(IMAGE_FOLDER_PATH, imageName);
			if (Files.exists(imagePath)) {
				try {
					byte[] imageBytes = Files.readAllBytes(imagePath);
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.IMAGE_JPEG); // You could also check for PNG or others if needed
					return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
				} catch (IOException e) {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			}
		}
		return ResponseEntity.notFound().build();
	}
	

	@GetMapping("/selectedVisitor")
	public ResponseEntity<?> viewVisitor(@RequestParam("visitorId") long visitorId) {
		Optional<Visitor> visitorOptional = visitorService.getVisitorById(visitorId);
		if (visitorOptional.isPresent()) {
			Visitor visitor = visitorOptional.get();
			visitorService.storeVisitorDetailsIntoToken(visitor);
			VisitorTokenId lastVisitorToken = visitorTokenIdRepository.findFirstByOrderByIdDesc();

			Map<String, Object> response = new HashMap<>();
			response.put("selectedVisitor", visitor);
			response.put("lastVisitorToken", lastVisitorToken);

			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Visitor not found.");
		}
	}
	
	@GetMapping("/deleteVisitor/{id}")
    public ResponseEntity<?> deleteVisitorDetails(@PathVariable Long id) {
        try {
        	
            Optional<Visitor> visitorOpt = this.visitorService.getVisitorById(id);
            if (visitorOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Visitor with ID " + id + " not found.");
            }

            Visitor visitor = visitorOpt.get();
            String uniqueId = visitor.getUniqueId();

            // Get actual image name if available
            String imageName = visitor.getImageName(); // You can use this directly if stored in DB
            if (imageName == null || imageName.isBlank()) {
                // Fallback: generate image name using fullName and uniqueId
                String[] nameParts = visitor.getFullName().split(" ");
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";
                String last4Digits = uniqueId.length() >= 4 ? uniqueId.substring(uniqueId.length() - 4) : uniqueId;
                imageName = firstName + "_" + lastName + "_" + last4Digits;
            }

            // Try deleting the image with known extensions
            String[] extensions = {".jpg", ".jpeg", ".png"};
            boolean imageDeleted = false;

            for (String ext : extensions) {
                Path imagePath = Paths.get(IMAGE_FOLDER_PATH, imageName + ext);
                if (Files.exists(imagePath)) {
                    try {
                        Files.delete(imagePath);
                        System.out.println("Image file deleted: " + imagePath.toAbsolutePath());
                        imageDeleted = true;
                        break;
                    } catch (Exception e) {
                        System.out.println("Failed to delete image file: " + imagePath.toAbsolutePath());
                    }
                }
            }

            if (!imageDeleted) {
                System.out.println("Image not found for deletion: " + imageName);
            }

            // Delete Visitor from DB
            Visitor deletedVisitor = this.visitorService.deleteVisitorDetails(id);

            // Delete UniqueIdDetails
            UniqueIdDetails uniqueIdDetails = this.uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(uniqueId);
            if (uniqueIdDetails != null) {
                this.uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetails.getId());
                System.out.println("Unique ID Details Deleted for: " + uniqueId);
            }

            return ResponseEntity.ok("Visitor, image, and unique ID details deleted successfully.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected Error: " + e.getMessage());
        }
    }
	
	// Get current Sr. No
    @GetMapping("/manageSrNo")
    public ResponseEntity<Map<String, Object>> getCurrentSrNo() {
        Long currentSrNo = visitorTokenIdService.getCurrentSrNo();
        return ResponseEntity.ok(Map.of("currentSrNo", currentSrNo));
    }

 // Update Sr. No
    @PostMapping("/updateSrNo")
    public ResponseEntity<Map<String, Object>> updateSrNo(@RequestBody Map<String, Long> requestBody) {
        try {
            Long newSrNo = requestBody.get("newSrNo"); // Get the new Sr. No from the request body
            System.out.println(newSrNo);
            
            visitorTokenIdService.updateCurrentSrNo(newSrNo); // Call the service to update the Sr. No
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Serial number updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/searchGatePass")
    public ResponseEntity<?> searchGatePass(@RequestParam("id") Long id) {
        // Logic to search for the token using the ID
    	System.out.println(id);
    	VisitorTokenId visitorToken = visitorTokenIdService.findByTokenId(id);
        System.out.println("Visitor Details:"+visitorToken);
        if (visitorToken != null) {
            // Return token details in the response
            return ResponseEntity.ok(visitorToken);
        } else {
            // Return an error message in the response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("No gate pass found with the provided ID.");
        }
    }
    
    @GetMapping("/visitortokenid/image/{id}")
	public ResponseEntity<byte[]> getVisitorImageByTokenNo(@PathVariable Long id) {
		VisitorTokenId visitorTokenId = visitorTokenIdService.findById(id);
		if (visitorTokenId!=null) {
			String imageName = visitorTokenId.getImageName();

			if (imageName == null || imageName.trim().isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Path imagePath = Paths.get(IMAGE_FOLDER_PATH, imageName);
			if (Files.exists(imagePath)) {
				try {
					byte[] imageBytes = Files.readAllBytes(imagePath);
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.IMAGE_JPEG); // You could also check for PNG or others if needed
					return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
				} catch (IOException e) {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			}
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/restrictVisitor")
		public ResponseEntity<String> restrictVisitor(@RequestParam("visitorId") Long visitorId) {
		    boolean success = visitorService.restrictUser(visitorId);
		    if (success) {
		        return ResponseEntity.ok("Visitor restricted successfully.");
		    }
		    return ResponseEntity.badRequest().body("Failed to restrict visitor.");
		}


	@GetMapping("/unrestrictVisitor")
	public ResponseEntity<String> unrestrictVisitor(@RequestParam("visitorId") Long visitorId) {
	    boolean success = visitorService.unrestrictUser(visitorId);
	    if (success) {
	        return ResponseEntity.ok("Visitor unrestricted successfully.");
	    }
	    return ResponseEntity.badRequest().body("Failed to unrestrict visitor.");
	}
}
