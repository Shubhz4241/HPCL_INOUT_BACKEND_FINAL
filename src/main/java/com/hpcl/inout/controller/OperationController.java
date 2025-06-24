package com.hpcl.inout.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hpcl.inout.entity.Contractor;
import com.hpcl.inout.entity.Contractorworkman;
import com.hpcl.inout.entity.Employee;
import com.hpcl.inout.entity.Feg;
import com.hpcl.inout.entity.Gat;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.License;
import com.hpcl.inout.entity.Mathadi;
import com.hpcl.inout.entity.Officer;
import com.hpcl.inout.entity.Sec;
import com.hpcl.inout.entity.Tat;
import com.hpcl.inout.entity.UniqueIdDetails;
import com.hpcl.inout.entity.User;
import com.hpcl.inout.repository.UserRepository;
import com.hpcl.inout.service.ContractorService;
import com.hpcl.inout.service.ContractorworkmanService;
import com.hpcl.inout.service.DriverService;
import com.hpcl.inout.service.EmployeeService;
import com.hpcl.inout.service.FegService;
import com.hpcl.inout.service.GatService;
import com.hpcl.inout.service.InscanService;
import com.hpcl.inout.service.LicenseGateService;
import com.hpcl.inout.service.MathadiService;
import com.hpcl.inout.service.OfficerService;
import com.hpcl.inout.service.OperationService;
import com.hpcl.inout.service.ProjectService;
import com.hpcl.inout.service.SecService;
import com.hpcl.inout.service.TatService;
import com.hpcl.inout.service.UniqueIdDetailsService;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class OperationController {
	
	@Autowired
	private OfficerService officerService;
	
	@Autowired
	private InscanService inscanService;
	
	@Autowired
	private LicenseGateService licensegateService;
	
	@Autowired
	private UniqueIdDetailsService uniqueIdDetailsService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private ContractorService contractorService;
	
	@Autowired
	private ContractorworkmanService contractorworkmanService;
	
	@Autowired
	private MathadiService mathadiService;
	
	@Autowired
	private GatService gatService;

	@Autowired
	private TatService tatService;

	@Autowired
	private FegService fegService;

	@Autowired
	private SecService secService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OperationService operationService;
	
	@Autowired
	private DriverService driverService;
	
	@Autowired
	private ProjectService projectService;

	
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
	
	
	
	//Genarate QR COde
	@GetMapping("/generateQRCode")
	@CrossOrigin(origins = "http://localhost:5173") 
    public ResponseEntity<byte[]> generateQRCode(@RequestParam("data") String data) {
        int width = 300;
        int height = 300;
        String format = "png";
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
            int qrCodeWidth = bitMatrix.getWidth();
            int qrCodeHeight = bitMatrix.getHeight();
            BufferedImage qrCodeImage = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < qrCodeWidth; x++) {
                for (int y = 0; y < qrCodeHeight; y++) {
                    qrCodeImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, format, baos);
            byte[] imageBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (WriterException | java.io.IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PostMapping("/submitOperators")
    public List<Officer> createOfficers(@RequestBody List<Integer> officerIds) {
        List<Officer> newOfficers = new ArrayList<>();

        for (Integer id : officerIds) {
            Officer officer = new Officer();
            newOfficers.add(officer);
        }

        return officerService.addOfficer(officerIds);
    }
	
	@GetMapping("/officer")
    public Map<String, Object> getOperatorDetails() {
        Map<String, Object> response = new HashMap<>();

        List<Officer> operatorDetails = officerService.getAllOfficerDetails();
        List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();

        response.put("operatorDetails", operatorDetails);
        response.put("inscanDetailsForOperation", inscanDetailsForOperation);

        // Optional: Add user info if needed
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            response.put("username", authentication.getName());
//            response.put("roles", authentication.getAuthorities());
//        }

        return response;
    }
	
	@GetMapping("/officer/inplant/{id}")
	public ResponseEntity<Map<String, Boolean>> checkOfficerInPlantStatus(@PathVariable Long id) {
	    Map<String, Boolean> response = new HashMap<>();
	    String status = inscanService.getMainGateSatus(id, "OFC"); // "OFC" for Officer entity type
	    boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
	    response.put("isInPlant", isInPlant);
	    return ResponseEntity.ok(response);
	}
	
	@PostMapping("/save")
	public ResponseEntity<?> saveOperator(@RequestBody Officer officer) {
	    Map<String, String> response = new HashMap<>();
	    System.out.println("Received Officer Data -> ID: " + officer.getId() +
	                       ", FullName: " + officer.getFullName() +
	                       ", Address: " + officer.getAddress() +
	                       ", UniqueId: " + officer.getUniqueId());

	    try {
	        // ========== CASE 1: CREATE ==========
	        if (officer.getId() == null) {
	            System.out.println("Creating new Officer...");
	            Officer createdOfficer = officerService.addOfficer(officer);

	            if (createdOfficer != null) {
	                response.put("message", "Officer saved successfully.");
	                return ResponseEntity.ok(response);
	            } else {
	                response.put("error", "An Officer with the same token number already exists.");
	                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	            }
	        }

	        // ========== CASE 2: UPDATE ==========
	        else {
	            System.out.println("Updating Officer...");
	            Optional<Officer> officerOldOpt = officerService.getOfficerById(officer.getId());
	            System.out.println("Fetched Existing Officer: " + officerOldOpt);

	            if (!officerOldOpt.isPresent()) {
	                response.put("error", "Officer not found.");
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	            }

	            Officer officerOld = officerOldOpt.get();
	            System.out.println("Old Officer: " + officerOld);
	            System.out.println("New UniqueId: " + officer.getUniqueId());

	            // CASE A: Adding Unique ID for the first time
	            if (officerOld.getUniqueId() == null || officerOld.getUniqueId().isEmpty()) {
	                UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(officer.getUniqueId());

	                if (existing != null) {
	                    response.put("error", "UniqueId already exists.");
	                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	                }

	                UniqueIdDetails newUnique = new UniqueIdDetails();
	                newUnique.setUniqueId(officer.getUniqueId());
	                newUnique.setEntity("Officer " + officer.getId());
	                newUnique.setFullName(officer.getFullName());
	                newUnique.setMobileNumber(officer.getMobileNumber());
	                newUnique.setAddress(officer.getAddress());
	                uniqueIdDetailsService.saveUniqueIdDetails(newUnique);

	                Officer updated = officerService.updateOfficer(officer.getId(), officer);
	                if (updated != null) {
	                    response.put("message", "Officer updated successfully.");
	                    return ResponseEntity.ok(response);
	                } else {
	                    response.put("error", "Failed to update the officer.");
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	                }
	            }

	            // CASE B: Unique ID is being changed
	            else if (!officer.getUniqueId().equalsIgnoreCase(officerOld.getUniqueId())) {
	                UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(officer.getUniqueId());

	                if (existing != null) {
	                    response.put("error", "UniqueId already used by someone else.");
	                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	                }

	                UniqueIdDetails oldUnique = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(officerOld.getUniqueId());
	                if (oldUnique != null) {
	                    uniqueIdDetailsService.deleteUniqueIdDetails(oldUnique.getId());
	                }

	                UniqueIdDetails newUnique = new UniqueIdDetails();
	                newUnique.setUniqueId(officer.getUniqueId());
	                newUnique.setEntity("Officer " + officer.getId());
	                newUnique.setFullName(officer.getFullName());
	                newUnique.setMobileNumber(officer.getMobileNumber());
	                newUnique.setAddress(officer.getAddress());
	                uniqueIdDetailsService.saveUniqueIdDetails(newUnique);

	                Officer updated = officerService.updateOfficer(officer.getId(), officer);
	                if (updated != null) {
	                    response.put("message", "Officer updated successfully.");
	                    return ResponseEntity.ok(response);
	                } else {
	                    response.put("error", "Failed to update the officer.");
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	                }
	            }

	            // CASE C: Unique ID remains same
	            else {
	                Officer updated = officerService.updateOfficer(officer.getId(), officer);
	                if (updated != null) {
	                    response.put("message", "Officer updated successfully.");
	                    return ResponseEntity.ok(response);
	                } else {
	                    response.put("error", "Failed to update the officer.");
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	                }
	            }
	        }
	    } catch (IllegalArgumentException e) {
	        response.put("error", "Error: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}
	
	@GetMapping("/deleteOfficer/{id}")
	public ResponseEntity<?> deleteOfficerDetails(@PathVariable("id") Long productId) {
	    try {
	        Optional<Officer> ofccopy = this.officerService.getOfficerById(productId);
	        if (ofccopy.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("Officer with ID " + productId + " not found.");
	        }

	        String uniqueId = ofccopy.get().getUniqueId();
	        Officer updateofficer = this.officerService.deleteOfficerDetails(productId);

	        if (updateofficer != null) {
	            System.out.println("Unique Id to delete: " + uniqueId);
	            UniqueIdDetails uniqueIdDetails = this.uniqueIdDetailsService
	                    .getUniqueIdDetailsByUniqueId(uniqueId);

	            if (uniqueIdDetails != null) {
	                this.uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetails.getId());
	            }

	            return ResponseEntity.ok("Officer details deleted successfully.");
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Failed to delete Officer details.");
	        }

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Error: " + e.getMessage());
	    }
	}
	
	@PostMapping("/submitEmployee")
    public List<Employee> createEmployee(@RequestBody List<Integer> employeeIds) {
		List<Employee> newEmployee = new ArrayList<>();

        for (Integer id : employeeIds) {
            Employee employee = new Employee();
            newEmployee.add(employee);
        }

        return employeeService.addEmployee(employeeIds);
    }
	
	@GetMapping("/employee")
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployeeDetails();
        List<Inscan> inscans = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();
        
        Map<String, Object> response = new HashMap<>();
        response.put("employeeDetails", employees);
        response.put("inscanDetailsForOperation", inscans);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/employee/inplant/{id}")
	public ResponseEntity<Map<String, Boolean>> checkEmployeeInPlantStatus(@PathVariable Long id) {
	    Map<String, Boolean> response = new HashMap<>();
	    String status = inscanService.getMainGateSatus(id, "EMP"); // "EMP" for Employee entity type
	    boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
	    response.put("isInPlant", isInPlant);
	    return ResponseEntity.ok(response);
	}

	
	@GetMapping("/employee/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(employeeService.getEmployeeById(id),HttpStatus.OK);
    }
	
	@PostMapping("/saveEmployee")
    public ResponseEntity<?> saveOrUpdateEmployee(@RequestBody Employee employee) {
        try {
            if (employee.getId() == null) {
                // Create new
                Employee created = employeeService.addEmployee(employee);
                if (created != null)
                    return ResponseEntity.ok("Employee saved successfully.");
                else
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Employee with same token number already exists.");
            } else {
                // Update
            	System.out.println(employee.getFullName());
                Optional<Employee> empOldOpt = employeeService.getEmployeeById(employee.getId());
                if (empOldOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");

                Employee empOld = empOldOpt.get();

                if (empOld.getUniqueId() == null || empOld.getUniqueId().isEmpty()) {
                    UniqueIdDetails existing = uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(employee.getUniqueId());
                    if (existing != null)
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("Aadhar Already Exists.");

                    UniqueIdDetails newUniqueId = new UniqueIdDetails();
                    newUniqueId.setUniqueId(employee.getUniqueId());
                    newUniqueId.setEntity("Employee " + employee.getId());
                    newUniqueId.setFullName(employee.getFullName());
                    newUniqueId.setMobileNumber(employee.getMobileNumber());
                    newUniqueId.setAddress(employee.getAddress());
                    uniqueIdDetailsService.saveUniqueIdDetails(newUniqueId);
                } else if (!employee.getUniqueId().equalsIgnoreCase(empOld.getUniqueId())) {
                    UniqueIdDetails existing = uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(employee.getUniqueId());
                    if (existing != null)
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("Unique ID already used by someone else..Aadhar Already Exists.");

                    UniqueIdDetails oldUniqueId = uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(empOld.getUniqueId());
                    uniqueIdDetailsService.deleteUniqueIdDetails(oldUniqueId.getId());

                    UniqueIdDetails newUniqueId = new UniqueIdDetails();
                    newUniqueId.setUniqueId(employee.getUniqueId());
                    newUniqueId.setEntity("Employee " + employee.getId());
                    newUniqueId.setFullName(employee.getFullName());
                    newUniqueId.setMobileNumber(employee.getMobileNumber());
                    newUniqueId.setAddress(employee.getAddress());
                    uniqueIdDetailsService.saveUniqueIdDetails(newUniqueId);
                }

                Employee updated = employeeService.updateEmployee(employee.getId(), employee);
                if (updated != null)
                    return ResponseEntity.ok("Employee updated successfully.");
                else
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update the Employee.");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // 4. DELETE Employee
    @GetMapping("deleteEmployee/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        try {
            Optional<Employee> empOpt = employeeService.getEmployeeById(id);
            if (empOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");

            String uniqueId = empOpt.get().getUniqueId();
            Employee deleted = employeeService.deleteEmployeeDetails(id);
            if (deleted != null && uniqueId != null) {
                UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(uniqueId);
                if (uniqueIdDetails != null) {
                	uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetails.getId());
                }
                return ResponseEntity.ok("Employee deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete Employee.");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    
    //Contractor
    @PostMapping("/submitContractor")
    public List<Contractor> createContractor(@RequestBody List<Integer> contractorIds) {
		List<Contractor> newContractor = new ArrayList<>();

        for (Integer id : contractorIds) {
        	Contractor contractor = new Contractor();
            newContractor.add(contractor);
        }

        return contractorService.addContractor(contractorIds);
    }
    
    @GetMapping("/contractor/inplant/{id}")
    public ResponseEntity<Map<String, Boolean>> checkContractorInPlantStatus(@PathVariable Long id) {
        Map<String, Boolean> response = new HashMap<>();
        String status = inscanService.getMainGateSatus(id, "CON"); // "CON" for Contractor entity type
        boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
        response.put("isInPlant", isInPlant);
        return ResponseEntity.ok(response);
    }
    
 // 1. Get All Contractors
    @GetMapping("/contractor")
    public ResponseEntity<?> getAllContractors() {
        List<Contractor> contractorDetails = contractorService.getAllContractorDetails();
        List<Inscan> inscanDetails = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();

        Map<String, Object> response = new HashMap<>();
        response.put("contractors", contractorDetails);
        response.put("inscanDetails", inscanDetails);
        return ResponseEntity.ok(response);
    }

    // 2. Get Contractor by ID
    @GetMapping("/contractor/{id}")
    public ResponseEntity<?> getContractorDetails(@PathVariable Long id) {
        return new ResponseEntity<>(contractorService.getContractorById(id),HttpStatus.OK);
    }

    // 3. Save or Update Contractor
    @PostMapping("/saveContractor")
    public ResponseEntity<?> saveOrUpdateContractor(@RequestBody Contractor contractor) {
        try {
            if (contractor.getId() == null) {
                Contractor created = contractorService.addContractor(contractor);
                if (created != null)
                    return ResponseEntity.ok("Contractor saved successfully.");
                else
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Contractor with the same token number already exists.");
            } else {
                Optional<Contractor> existingOpt = contractorService.getContractorById(contractor.getId());
                if (existingOpt.isEmpty())
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contractor not found");

                Contractor existing = existingOpt.get();

                if (existing.getUniqueId() == null || existing.getUniqueId().isEmpty()) {
                	UniqueIdDetails existingUniqueId = uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(contractor.getUniqueId());
                    if (existingUniqueId != null)
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("UniqueId Already Exists.");
                    
                    UniqueIdDetails newUniqueId = createUniqueIdDetails(contractor, "Contractor");
                    uniqueIdDetailsService.saveUniqueIdDetails(newUniqueId);

                    Contractor updated = contractorService.updateContractor(contractor.getId(), contractor);
                    return ResponseEntity.ok("Contractor saved successfully.");
                } else if (!contractor.getUniqueId().equalsIgnoreCase(existing.getUniqueId())) {
                	UniqueIdDetails existingUniqueId = uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(contractor.getUniqueId());

                    if (existingUniqueId != null)
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("UniqueId already used by someone else.");

                    UniqueIdDetails toDelete = uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(existing.getUniqueId());
                    if (toDelete != null)
                        uniqueIdDetailsService.deleteUniqueIdDetails(toDelete.getId());

                    UniqueIdDetails newUniqueId = createUniqueIdDetails(contractor, "Contractor");
                    uniqueIdDetailsService.saveUniqueIdDetails(newUniqueId);

                    contractorService.updateContractor(contractor.getId(), contractor);
                    return ResponseEntity.ok("Contractor updated successfully.");
                } else {
                    contractorService.updateContractor(contractor.getId(), contractor);
                    return ResponseEntity.ok("Contractor updated successfully.");
                }
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // 4. Delete Contractor
    @GetMapping("deleteContractor/{id}")
    public ResponseEntity<?> deleteContractor(@PathVariable Long id) {
        try {
            Optional<Contractor> contractorOpt = contractorService.getContractorById(id);
            if (contractorOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contractor not found");

            String uniqueId = contractorOpt.get().getUniqueId();

            Contractor deleted = contractorService.deleteContractorDetails(id);
            if (deleted != null) {
                if (uniqueId != null) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(uniqueId);
                    if (uniqueIdDetails != null)
                        uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetails.getId());
                }
                return ResponseEntity.ok("Contractor deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to delete Contractor.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // ✅ Utility to create UniqueIdDetails
    private UniqueIdDetails createUniqueIdDetails(Contractor contractor, String entityPrefix) {
    	UniqueIdDetails uniqueIdDetails = new UniqueIdDetails();
    	uniqueIdDetails.setUniqueId(contractor.getUniqueId());
    	uniqueIdDetails.setEntity(entityPrefix + " " + contractor.getId());
    	uniqueIdDetails.setFullName(contractor.getFullName());
    	uniqueIdDetails.setMobileNumber(contractor.getMobileNumber());
    	uniqueIdDetails.setAddress(contractor.getAddress());
        return uniqueIdDetails;
    }
    
    //Contractor-Workman
    @PostMapping("/submitContractorWorkman")
    public List<Contractorworkman> createContractorWorkman(@RequestBody List<Integer> contractorWorkmanIds) {
		List<Contractorworkman> newContractorWorkman = new ArrayList<>();

        for (Integer id : contractorWorkmanIds) {
        	Contractorworkman contractorWorkman = new Contractorworkman();
            newContractorWorkman.add(contractorWorkman);
        }

        return contractorworkmanService.addContractorworkman(contractorWorkmanIds);
    }
    @GetMapping("/contractorWorkman/inplant/{id}")
    public ResponseEntity<Map<String, Boolean>> checkContractorWorkmanInPlantStatus(@PathVariable Long id) {
        Map<String, Boolean> response = new HashMap<>();
        String status = inscanService.getMainGateSatus(id, "CONW"); // "CONW" for ContractorWorkman entity type
        boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
        response.put("isInPlant", isInPlant);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/contractorWorkman")
    public ResponseEntity<Map<String, Object>> getAllContractorworkmans() {
        List<Contractorworkman> contractorworkmanDetails = contractorworkmanService.getAllContractorworkmanDetails();
        List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();

        Map<String, Object> response = new HashMap<>();
        response.put("contractorworkmanDetails", contractorworkmanDetails);
        response.put("inscanDetailsForOperation", inscanDetailsForOperation);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contractorWorkman/{id}")
    public ResponseEntity<?> getContractorworkmanById(@PathVariable Long id) {
    	return new ResponseEntity<>(contractorworkmanService.getContractorworkmanById(id),HttpStatus.OK);
    }

//    @GetMapping("/contractorWorkman")
//    public ResponseEntity<List<Contractorworkman>> getAllContractorWorkman() {
//        return ResponseEntity.ok(contractorworkmanService.getAllContractorworkmanDetails());
//    }

    @PostMapping("/saveContractorworkman")
    public ResponseEntity<String> contractorworkmansave(@RequestBody Contractorworkman contractorworkman) {
        try {
            if (contractorworkman.getId() == null) {
                Contractorworkman created = contractorworkmanService.addContractorworkman(contractorworkman);
                if (created != null) {
                    return ResponseEntity.ok("Contractorworkman saved successfully.");
                } else {
                    return ResponseEntity.badRequest().body("Contractorworkman with same token number already exists.");
                }
            } else {
                Optional<Contractorworkman> oldOpt = contractorworkmanService.getContractorworkmanById(contractorworkman.getId());
                if (oldOpt.isPresent()) {
                    Contractorworkman old = oldOpt.get();

                    if (old.getUniqueId() == null || old.getUniqueId().isEmpty()) {
                        UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(contractorworkman.getUniqueId());
                        if (existing != null) {
                            return ResponseEntity.badRequest().body("Aadhar Already Exists.");
                        } else {
                            UniqueIdDetails newDetails = new UniqueIdDetails();
                            newDetails.setUniqueId(contractorworkman.getUniqueId());
                            newDetails.setEntity("Contractorworkman " + contractorworkman.getId());
                            newDetails.setFullName(contractorworkman.getFullName());
                            newDetails.setMobileNumber(contractorworkman.getMobileNumber());
                            newDetails.setAddress(contractorworkman.getAddress());
                            uniqueIdDetailsService.saveUniqueIdDetails(newDetails);

                            contractorworkmanService.updateContractorworkman(contractorworkman.getId(), contractorworkman);
                            return ResponseEntity.ok("Contractorworkman saved successfully.");
                        }
                    } else if (!contractorworkman.getUniqueId().equalsIgnoreCase(old.getUniqueId())) {
                        UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(contractorworkman.getUniqueId());
                        if (existing != null) {
                            return ResponseEntity.badRequest().body("UniqueID already used by someone else.");
                        } else {
                            UniqueIdDetails oldDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(old.getUniqueId());
                            uniqueIdDetailsService.deleteUniqueIdDetails(oldDetails.getId());

                            UniqueIdDetails newDetails = new UniqueIdDetails();
                            newDetails.setUniqueId(contractorworkman.getUniqueId());
                            newDetails.setEntity("Contractorworkman " + contractorworkman.getId());
                            newDetails.setFullName(contractorworkman.getFullName());
                            newDetails.setMobileNumber(contractorworkman.getMobileNumber());
                            newDetails.setAddress(contractorworkman.getAddress());
                            uniqueIdDetailsService.saveUniqueIdDetails(newDetails);

                            contractorworkmanService.updateContractorworkman(contractorworkman.getId(), contractorworkman);
                            return ResponseEntity.ok("Contractorworkman saved successfully.");
                        }
                    } else {
                        contractorworkmanService.updateContractorworkman(contractorworkman.getId(), contractorworkman);
                        return ResponseEntity.ok("Contractorworkman updated successfully.");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred.");
    }

    @GetMapping("/deleteContractorworkman/{id}")
    public ResponseEntity<?> deleteContractorworkman(@PathVariable Long id) {
        try {
            Optional<Contractorworkman> conwOpt = contractorworkmanService.getContractorworkmanById(id);
            if (conwOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contractorworkman not found");

            Contractorworkman deleted = contractorworkmanService.deleteContractorworkmanDetails(id);
            if (deleted != null) {
                UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(deleted.getUniqueId());
                if (uniqueIdDetails != null) {
                    uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetails.getId());
                }
                return ResponseEntity.ok("Contractorworkman deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete Contractorworkman.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    
    @GetMapping("/mathadi/inplant/{id}")
    public ResponseEntity<Map<String, Boolean>> checkMathadiInPlantStatus(@PathVariable Long id) {
        Map<String, Boolean> response = new HashMap<>();
        String status = inscanService.getMainGateSatus(id, "MT"); // "MT" for Mathadi entity type
        boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
        response.put("isInPlant", isInPlant);
        return ResponseEntity.ok(response);
    }

    //Mathadis
    @PostMapping("/submitMathadi")
    public List<Mathadi> createMathadi(@RequestBody List<Integer> mathadiIds) {
		List<Mathadi> newMathadi = new ArrayList<>();

        for (Integer id : mathadiIds) {
        	Mathadi mathadi = new Mathadi();
            newMathadi.add(mathadi);
        }

        return mathadiService.addMathadi(mathadiIds);
    }
    
    
    
    @GetMapping("/mathadi")
    public ResponseEntity<Map<String, Object>> getMathadiDetails() {
        Map<String, Object> response = new HashMap<>();
        List<Mathadi> mathadiDetails = mathadiService.getAllMathadiDetails();
        List<Inscan> inscanDetails = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();

        response.put("mathadiDetails", mathadiDetails);
        response.put("inscanDetailsForOperation", inscanDetails);
        return ResponseEntity.ok(response);
    }

    // Get specific mathadi detail by ID for view/edit
    @GetMapping("/mathadi/{id}")
    public ResponseEntity<?> getMathadiDetailsById(@PathVariable("id") Long id) {
       return new ResponseEntity<>(mathadiService.getMathadiById(id),HttpStatus.OK);
    }

    // Get contractor + all mathadi + selected one with action
    @GetMapping("/contractorMathadiDetails/{id}")
    public ResponseEntity<?> getMathadiDetailsWithAction(
            @RequestParam("productId") Long productId,
            @RequestParam("action") String action) {
        
        Optional<Mathadi> mathadi = mathadiService.getMathadiById(productId);
        if (mathadi.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mathadi not found");

        Map<String, Object> response = new HashMap<>();
        response.put("mathadiDetails", mathadiService.getAllMathadiDetails());
        response.put("contractorDetails", contractorService.getAllContractorDetails());
        response.put("selectedMathadi", mathadi.get());
        response.put("action", action);
        return ResponseEntity.ok(response);
    }

    // Save or update mathadi
//    @PostMapping("/saveMathadi")
//    public ResponseEntity<?> saveMathadi(@RequestBody Mathadi mathadi) {
//        try {
//            if (mathadi.getId() == null) {
//                Mathadi createdMathadi = mathadiService.addMathadi(mathadi);
//                if (createdMathadi != null) return ResponseEntity.ok("Mathadi saved successfully.");
//                else return ResponseEntity.badRequest().body("Mathadi with same token number exists.");
//            } else {
//                Optional<Mathadi> existingOpt = mathadiService.getMathadiById(mathadi.getId());
//                if (existingOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mathadi not found.");
//                Mathadi old = existingOpt.get();
//
//                if (old.getUniqueId() == null || old.getUniqueId().isEmpty()) {
//                    UniqueIdDetails details = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(mathadi.getUniqueId());
//                    if (details != null) return ResponseEntity.badRequest().body("Unique ID Already Exists.");
//                } else if (!old.getUniqueId().equalsIgnoreCase(mathadi.getUniqueId())) {
//                    UniqueIdDetails existing = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(mathadi.getUniqueId());
//                    if (existing != null) return ResponseEntity.badRequest().body("Unique ID already used by someone else.");
//                    uniqueIdDetailsService.deleteUniqueIdDetails(old.getId());
//                }
//
//                UniqueIdDetails newDetails = new UniqueIdDetails();
//                newDetails.setUniqueId(mathadi.getUniqueId());
//                newDetails.setEntity("Mathadi " + mathadi.getId());
//                newDetails.setFullName(mathadi.getFullName());
//                newDetails.setMobileNumber(mathadi.getMobileNumber());
//                newDetails.setAddress(mathadi.getAddress());
//                uniqueIdDetailsService.saveUniqueIdDetails(newDetails);
//
//                Mathadi updated = mathadiService.updateMathadi(mathadi.getId(), mathadi);
//                return ResponseEntity.ok("Mathadi updated successfully.");
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }
    
    @PostMapping("/saveMathadi")
    public ResponseEntity<String> mathadiSave(@RequestBody Mathadi mathadi) {
        try {
            if (mathadi.getId() == null) {
                Mathadi createdMathadi = this.mathadiService.addMathadi(mathadi);
                if (createdMathadi != null) {
                    return ResponseEntity.ok("Mathadi saved successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("A Mathadi with the same token number already exists.");
                }
            } else {
                Optional<Mathadi> mathadi_old_opt = this.mathadiService.getMathadiById(mathadi.getId());
                Mathadi mathadi_old = null;
                if (mathadi_old_opt.isPresent())
                    mathadi_old = mathadi_old_opt.get();

                if (mathadi_old.getUniqueId() == null || "".equals(mathadi_old.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = this.uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(mathadi.getUniqueId());

                    if (uniqueIdDetails != null) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("Unique ID already exists.");
                    } else {
                        UniqueIdDetails newDetails = new UniqueIdDetails();
                        newDetails.setUniqueId(mathadi.getUniqueId());
                        newDetails.setEntity("Mathadi " + mathadi.getId());
                        newDetails.setFullName(mathadi.getFullName());
                        newDetails.setMobileNumber(mathadi.getMobileNumber());
                        newDetails.setAddress(mathadi.getAddress());
                        this.uniqueIdDetailsService.saveUniqueIdDetails(newDetails);

                        Mathadi updatedMathadi = this.mathadiService.updateMathadi(mathadi.getId(), mathadi);
                        if (updatedMathadi != null) {
                            return ResponseEntity.ok("Contractorworkman saved successfully.");
                        } else {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Failed to update the Contractorworkman.");
                        }
                    }
                } else if (!mathadi.getUniqueId().equalsIgnoreCase(mathadi_old.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = this.uniqueIdDetailsService
                            .getUniqueIdDetailsByUniqueId(mathadi.getUniqueId());

                    if (uniqueIdDetails != null) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("Unique ID already used by someone else.");
                    } else {
                        UniqueIdDetails deleteOld = this.uniqueIdDetailsService
                                .getUniqueIdDetailsByUniqueId(mathadi_old.getUniqueId());
                        this.uniqueIdDetailsService.deleteUniqueIdDetails(deleteOld.getId());

                        UniqueIdDetails newDetails = new UniqueIdDetails();
                        newDetails.setUniqueId(mathadi.getUniqueId());
                        newDetails.setEntity("Mathadi " + mathadi.getId());
                        newDetails.setFullName(mathadi.getFullName());
                        newDetails.setMobileNumber(mathadi.getMobileNumber());
                        newDetails.setAddress(mathadi.getAddress());
                        this.uniqueIdDetailsService.saveUniqueIdDetails(newDetails);

                        Mathadi updatedMathadi = this.mathadiService.updateMathadi(mathadi.getId(), mathadi);
                        if (updatedMathadi != null) {
                            return ResponseEntity.ok("Mathadi saved successfully.");
                        } else {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Failed to update the Mathadi.");
                        }
                    }
                } else {
                    Mathadi updatedMathadi = this.mathadiService.updateMathadi(mathadi.getId(), mathadi);
                    if (updatedMathadi != null) {
                        return ResponseEntity.ok("Mathadi updated successfully.");
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to update the Mathadi.");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }


    // Delete mathadi by ID
    @GetMapping("/deleteMathadi/{id}")
    public ResponseEntity<?> deleteMathadi(@PathVariable Long id) {
        try {
            Optional<Mathadi> mathadiOpt = mathadiService.getMathadiById(id);
            if (mathadiOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mathadi not found.");

            mathadiService.deleteMathadiDetails(id);
            return ResponseEntity.ok("Mathadi deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    
    /////////Gat
    @PostMapping("/submitGat")
    public List<Gat> createGat(@RequestBody List<Integer> gatIds) {
		List<Gat> newGat = new ArrayList<>();

        for (Integer id : gatIds) {
        	Gat gat = new Gat();
            newGat.add(gat);
        }

        return gatService.addGat(gatIds);
    }
    
    @GetMapping("/gat/inplant/{id}")
    public ResponseEntity<Map<String, Boolean>> checkGatInPlantStatus(@PathVariable Long id) {
        Map<String, Boolean> response = new HashMap<>();
        String status = inscanService.getMainGateSatus(id, "GAT"); // "GAT" for Graduate Apprentice Trainee entity type
        boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
        response.put("isInPlant", isInPlant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/gat")
    public ResponseEntity<?> getAllGatDetails() {
        List<Gat> gatDetails = gatService.getAllGatDetails();
        List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();
        
        Map<String, Object> response = new HashMap<>();
        response.put("gatDetails", gatDetails);
        response.put("inscanDetailsForOperation", inscanDetailsForOperation);
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/gat/{productId}")
    public ResponseEntity<?> getGatDetails(@PathVariable Long productId, @RequestParam String action) {
        Optional<Gat> gat = gatService.getGatById(productId);
        if (gat.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Gat not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("selectedProduct", gat.get());
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveGat")
    public ResponseEntity<?> saveGat(@RequestBody Gat gat) {
        try {
            Map<String, String> response = new HashMap<>();
            
            if (gat.getId() == null) {
                Gat createdGat = gatService.addGat(gat);
                if (createdGat != null) {
                    response.put("successMessage", "Gat saved successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("errorMessage", "A Gat with the same token number already exists.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            } else {
                Optional<Gat> gatOldOpt = gatService.getGatById(gat.getId());
                if (gatOldOpt.isEmpty()) {
                    response.put("errorMessage", "Gat not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                
                Gat gatOld = gatOldOpt.get();
                if (gatOld.getUniqueId() == null || "".equals(gatOld.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(gat.getUniqueId());
                    if (uniqueIdDetails != null) {
                        response.put("errorMessage", "Unique ID Already Exists.");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else {
                        UniqueIdDetails uniqueIdDetails2 = new UniqueIdDetails();
                        uniqueIdDetails2.setUniqueId(gat.getUniqueId());
                        uniqueIdDetails2.setEntity("Gat " + gat.getId());
                        uniqueIdDetails2.setFullName(gat.getFullName());
                        uniqueIdDetails2.setMobileNumber(gat.getMobileNumber());
                        uniqueIdDetails2.setAddress(gat.getAddress());
                        uniqueIdDetailsService.saveUniqueIdDetails(uniqueIdDetails2);
                        
                        Gat updatedGat = gatService.updateGat(gat.getId(), gat);
                        if (updatedGat != null) {
                            response.put("successMessage", "Gat saved successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("errorMessage", "Failed to update the Gat.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                        }
                    }
                } else if (!gat.getUniqueId().equalsIgnoreCase(gatOld.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(gat.getUniqueId());
                    if (uniqueIdDetails != null) {
                        response.put("errorMessage", "Unique ID already used by someone else.");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else {
                        UniqueIdDetails uniqueIdDetailsDelete = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(gatOld.getUniqueId());
                        uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetailsDelete.getId());
                        
                        UniqueIdDetails uniqueIdDetails2 = new UniqueIdDetails();
                        uniqueIdDetails2.setUniqueId(gat.getUniqueId());
                        uniqueIdDetails2.setEntity("Gat " + gat.getId());
                        uniqueIdDetails2.setFullName(gat.getFullName());
                        uniqueIdDetails2.setMobileNumber(gat.getMobileNumber());
                        uniqueIdDetails2.setAddress(gat.getAddress());
                        uniqueIdDetailsService.saveUniqueIdDetails(uniqueIdDetails2);
                        
                        Gat updatedGat = gatService.updateGat(gat.getId(), gat);
                        if (updatedGat != null) {
                            response.put("successMessage", "Gat saved successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("errorMessage", "Failed to update the Gat.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                        }
                    }
                } else {
                    Gat updatedGat = gatService.updateGat(gat.getId(), gat);
                    if (updatedGat != null) {
                        response.put("successMessage", "Gat updated successfully.");
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("errorMessage", "Failed to update the Gat.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/gat/selected/{productId}")
    public ResponseEntity<?> getSelectedGat(@PathVariable Long productId) {
        Optional<Gat> gat = gatService.getGatById(productId);
        if (gat.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Gat not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("selectedProduct", gat.get());
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deleteGat/{productId}")
    public ResponseEntity<?> deleteGatDetails(@PathVariable Long productId) {
        try {
            Map<String, String> response = new HashMap<>();
            Optional<Gat> gatCopy = gatService.getGatById(productId);
            String uniqueId = null;
            
            if (gatCopy.isPresent()) {
                uniqueId = gatCopy.get().getUniqueId();
            }
            
            Gat updateGat = gatService.deleteGatDetails(productId);
            if (updateGat != null) {
                UniqueIdDetails uniqueIdDetailsDelete = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(uniqueId);
                uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetailsDelete.getId());
                
                response.put("successMessage", "Gat details deleted successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("errorMessage", "Failed to delete Gat details.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //Tat
    
    @PostMapping("/submitTat")
    public List<Tat> createTat(@RequestBody List<Integer> tatIds) {
		List<Tat> newTat = new ArrayList<>();

        for (Integer id : tatIds) {
        	Tat tat = new Tat();
            newTat.add(tat);
        }

        return tatService.addTat(tatIds);
    }
    
    @GetMapping("/tat/inplant/{id}")
    public ResponseEntity<Map<String, Boolean>> checkTatInPlantStatus(@PathVariable Long id) {
        Map<String, Boolean> response = new HashMap<>();
        String status = inscanService.getMainGateSatus(id, "TAT"); // "TAT" for Trade Apprentice Trainee entity type
        boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
        response.put("isInPlant", isInPlant);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/tat")
    public ResponseEntity<?> getAllTatDetails() {
        List<Tat> tatDetails = tatService.getAllTatDetails();
        List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();
        
        Map<String, Object> response = new HashMap<>();
        response.put("tatDetails", tatDetails);
        response.put("inscanDetailsForOperation", inscanDetailsForOperation);
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tat/{productId}")
    public ResponseEntity<?> getTatDetails(@PathVariable Long productId, @RequestParam String action) {
        Optional<Tat> tat = tatService.getTatById(productId);
        if (tat.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tat not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("selectedProduct", tat.get());
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveTat")
    public ResponseEntity<?> saveTat(@RequestBody Tat tat) {
        try {
            Map<String, String> response = new HashMap<>();
            
            if (tat.getId() == null) {
                Tat createdTat = tatService.addTat(tat);
                if (createdTat != null) {
                    response.put("successMessage", "Tat saved successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("errorMessage", "A Tat with the same token number already exists.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            } else {
                Optional<Tat> tatOldOpt = tatService.getTatById(tat.getId());
                if (tatOldOpt.isEmpty()) {
                    response.put("errorMessage", "Tat not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                
                Tat tatOld = tatOldOpt.get();
                if (tatOld.getUniqueId() == null || "".equals(tatOld.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(tat.getUniqueId());
                    if (uniqueIdDetails != null) {
                        response.put("errorMessage", "Unique ID Already Exists.");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else {
                        UniqueIdDetails uniqueIdDetails2 = new UniqueIdDetails();
                        uniqueIdDetails2.setUniqueId(tat.getUniqueId());
                        uniqueIdDetails2.setEntity("Tat " + tat.getId());
                        uniqueIdDetails2.setFullName(tat.getFullName());
                        uniqueIdDetails2.setMobileNumber(tat.getMobileNumber());
                        uniqueIdDetails2.setAddress(tat.getAddress());
                        uniqueIdDetailsService.saveUniqueIdDetails(uniqueIdDetails2);
                        
                        Tat updatedTat = tatService.updateTat(tat.getId(), tat);
                        if (updatedTat != null) {
                            response.put("successMessage", "Tat saved successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("errorMessage", "Failed to update the Tat.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                        }
                    }
                } else if (!tat.getUniqueId().equalsIgnoreCase(tatOld.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(tat.getUniqueId());
                    if (uniqueIdDetails != null) {
                        response.put("errorMessage", "Unique ID already used by someone else.");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else {
                        UniqueIdDetails uniqueIdDetailsDelete = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(tatOld.getUniqueId());
                        uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetailsDelete.getId());
                        
                        UniqueIdDetails uniqueIdDetails2 = new UniqueIdDetails();
                        uniqueIdDetails2.setUniqueId(tat.getUniqueId());
                        uniqueIdDetails2.setEntity("Tat " + tat.getId());
                        uniqueIdDetails2.setFullName(tat.getFullName());
                        uniqueIdDetails2.setMobileNumber(tat.getMobileNumber());
                        uniqueIdDetails2.setAddress(tat.getAddress());
                        uniqueIdDetailsService.saveUniqueIdDetails(uniqueIdDetails2);
                        
                        Tat updatedTat = tatService.updateTat(tat.getId(), tat);
                        if (updatedTat != null) {
                            response.put("successMessage", "Tat saved successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("errorMessage", "Failed to update the Tat.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                        }
                    }
                } else {
                    Tat updatedTat = tatService.updateTat(tat.getId(), tat);
                    if (updatedTat != null) {
                        response.put("successMessage", "Tat updated successfully.");
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("errorMessage", "Failed to update the Tat.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/tat/selected/{productId}")
    public ResponseEntity<?> getSelectedTat(@PathVariable Long productId) {
        Optional<Tat> tat = tatService.getTatById(productId);
        if (tat.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tat not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("selectedProduct", tat.get());
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deleteTat/{productId}")
    public ResponseEntity<?> deleteTatDetails(@PathVariable Long productId) {
        try {
            Map<String, String> response = new HashMap<>();
            Optional<Tat> tatCopy = tatService.getTatById(productId);
            String uniqueId = null;
            
            if (tatCopy.isPresent()) {
                uniqueId = tatCopy.get().getUniqueId();
            }
            
            Tat updateTat = tatService.deleteTatDetails(productId);
            if (updateTat != null) {
                UniqueIdDetails uniqueIdDetailsDelete = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(uniqueId);
                uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetailsDelete.getId());
                
                response.put("successMessage", "Tat details deleted successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("errorMessage", "Failed to delete Tat details.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    //Feg
    
    @PostMapping("/submitFeg")
    public List<Feg> createFeg(@RequestBody List<Integer> fegIds) {
		List<Feg> newFeg = new ArrayList<>();

        for (Integer id : fegIds) {
        	Feg feg = new Feg();
            newFeg.add(feg);
        }

        return fegService.addFeg(fegIds);
    }
    
    @GetMapping("/feg/inplant/{id}")
    public ResponseEntity<Map<String, Boolean>> checkFegInPlantStatus(@PathVariable Long id) {
        Map<String, Boolean> response = new HashMap<>();
        String status = inscanService.getMainGateSatus(id, "FEG"); // "FEG" for Field Engineer entity type
        boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
        response.put("isInPlant", isInPlant);
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/feg")
    public ResponseEntity<?> getAllFegDetails() {
        List<Feg> fegDetails = fegService.getAllFegDetails();
        List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();
        
        Map<String, Object> response = new HashMap<>();
        response.put("fegDetails", fegDetails);
        response.put("inscanDetailsForOperation", inscanDetailsForOperation);
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feg/{productId}")
    public ResponseEntity<?> getFegDetails(@PathVariable Long productId, @RequestParam String action) {
        Optional<Feg> feg = fegService.getFegById(productId);
        if (feg.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feg not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("selectedProduct", feg.get());
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveFeg")
    public ResponseEntity<?> saveFeg(@RequestBody Feg feg) {
        try {
            Map<String, String> response = new HashMap<>();
            
            if (feg.getId() == null) {
                Feg createdFeg = fegService.addFeg(feg);
                if (createdFeg != null) {
                    response.put("successMessage", "Feg saved successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("errorMessage", "A Feg with the same token number already exists.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            } else {
                Optional<Feg> fegOldOpt = fegService.getFegById(feg.getId());
                if (fegOldOpt.isEmpty()) {
                    response.put("errorMessage", "Feg not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                
                Feg fegOld = fegOldOpt.get();
                if (fegOld.getUniqueId() == null || "".equals(fegOld.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(feg.getUniqueId());
                    if (uniqueIdDetails != null) {
                        response.put("errorMessage", "Unique ID Already Exists.");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else {
                        UniqueIdDetails uniqueIdDetails2 = new UniqueIdDetails();
                        uniqueIdDetails2.setUniqueId(feg.getUniqueId());
                        uniqueIdDetails2.setEntity("Feg " + feg.getId());
                        uniqueIdDetails2.setFullName(feg.getFullName());
                        uniqueIdDetails2.setMobileNumber(feg.getMobileNumber());
                        uniqueIdDetails2.setAddress(feg.getAddress());
                        uniqueIdDetailsService.saveUniqueIdDetails(uniqueIdDetails2);
                        
                        Feg updatedFeg = fegService.updateFeg(feg.getId(), feg);
                        if (updatedFeg != null) {
                            response.put("successMessage", "Feg saved successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("errorMessage", "Failed to update the Feg.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                        }
                    }
                } else if (!feg.getUniqueId().equalsIgnoreCase(fegOld.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(feg.getUniqueId());
                    if (uniqueIdDetails != null) {
                        response.put("errorMessage", "Unique ID already used by someone else.");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else {
                        UniqueIdDetails uniqueIdDetailsDelete = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(fegOld.getUniqueId());
                        uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetailsDelete.getId());
                        
                        UniqueIdDetails uniqueIdDetails2 = new UniqueIdDetails();
                        uniqueIdDetails2.setUniqueId(feg.getUniqueId());
                        uniqueIdDetails2.setEntity("Feg " + feg.getId());
                        uniqueIdDetails2.setFullName(feg.getFullName());
                        uniqueIdDetails2.setMobileNumber(feg.getMobileNumber());
                        uniqueIdDetails2.setAddress(feg.getAddress());
                        uniqueIdDetailsService.saveUniqueIdDetails(uniqueIdDetails2);
                        
                        Feg updatedFeg = fegService.updateFeg(feg.getId(), feg);
                        if (updatedFeg != null) {
                            response.put("successMessage", "Feg saved successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("errorMessage", "Failed to update the Feg.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                        }
                    }
                } else {
                    Feg updatedFeg = fegService.updateFeg(feg.getId(), feg);
                    if (updatedFeg != null) {
                        response.put("successMessage", "Feg updated successfully.");
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("errorMessage", "Failed to update the Feg.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/feg/selected/{productId}")
    public ResponseEntity<?> getSelectedFeg(@PathVariable Long productId) {
        Optional<Feg> feg = fegService.getFegById(productId);
        if (feg.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feg not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("selectedProduct", feg.get());
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deleteFeg/{productId}")
    public ResponseEntity<?> deleteFegDetails(@PathVariable Long productId) {
        try {
            Map<String, String> response = new HashMap<>();
            Optional<Feg> fegCopy = fegService.getFegById(productId);
            String uniqueId = null;
            
            if (fegCopy.isPresent()) {
                uniqueId = fegCopy.get().getUniqueId();
            }
            
            Feg updateFeg = fegService.deleteFegDetails(productId);
            if (updateFeg != null) {
                UniqueIdDetails uniqueIdDetailsDelete = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(uniqueId);
                uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetailsDelete.getId());
                
                response.put("successMessage", "Feg details deleted successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("errorMessage", "Failed to delete Feg details.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    
    //Sec
    
    @PostMapping("/submitSec")
    public List<Sec> createSec(@RequestBody List<Integer> secIds) {
		List<Sec> newSec = new ArrayList<>();

        for (Integer id : secIds) {
        	Sec sec = new Sec();
            newSec.add(sec);
        }

        return secService.addSec(secIds);
    }
    
    @GetMapping("/sec/inplant/{id}")
    public ResponseEntity<Map<String, Boolean>> checkSecInPlantStatus(@PathVariable Long id) {
        Map<String, Boolean> response = new HashMap<>();
        String status = inscanService.getMainGateSatus(id, "SEC"); // "SEC" for Security entity type
        boolean isInPlant = status != null && status.equalsIgnoreCase("N"); // "N" means they are inside (not exited)
        response.put("isInPlant", isInPlant);
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/sec")
    public ResponseEntity<?> getAllSecDetails() {
        List<Sec> secDetails = secService.getAllSecDetails();
        List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();
        
        Map<String, Object> response = new HashMap<>();
        response.put("secDetails", secDetails);
        response.put("inscanDetailsForOperation", inscanDetailsForOperation);
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sec/{productId}")
    public ResponseEntity<?> getSecDetails(@PathVariable Long productId, @RequestParam String action) {
        Optional<Sec> sec = secService.getSecById(productId);
        if (sec.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sec not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("selectedProduct", sec.get());
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveSec")
    public ResponseEntity<?> saveSec(@RequestBody Sec sec) {
        try {
            Map<String, String> response = new HashMap<>();
            
            if (sec.getId() == null) {
                Sec createdSec = secService.addSec(sec);
                if (createdSec != null) {
                    response.put("successMessage", "Sec saved successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("errorMessage", "A Sec with the same token number already exists.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            } else {
                Optional<Sec> secOldOpt = secService.getSecById(sec.getId());
                if (secOldOpt.isEmpty()) {
                    response.put("errorMessage", "Sec not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                
                Sec secOld = secOldOpt.get();
                if (secOld.getUniqueId() == null || "".equals(secOld.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(sec.getUniqueId());
                    if (uniqueIdDetails != null) {
                        response.put("errorMessage", "Unique ID Already Exists.");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else {
                        UniqueIdDetails uniqueIdDetails2 = new UniqueIdDetails();
                        uniqueIdDetails2.setUniqueId(sec.getUniqueId());
                        uniqueIdDetails2.setEntity("Sec " + sec.getId());
                        uniqueIdDetails2.setFullName(sec.getFullName());
                        uniqueIdDetails2.setMobileNumber(sec.getMobileNumber());
                        uniqueIdDetails2.setAddress(sec.getAddress());
                        uniqueIdDetailsService.saveUniqueIdDetails(uniqueIdDetails2);
                        
                        Sec updatedSec = secService.updateSec(sec.getId(), sec);
                        if (updatedSec != null) {
                            response.put("successMessage", "Sec saved successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("errorMessage", "Failed to update the Sec.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                        }
                    }
                } else if (!sec.getUniqueId().equalsIgnoreCase(secOld.getUniqueId())) {
                    UniqueIdDetails uniqueIdDetails = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(sec.getUniqueId());
                    if (uniqueIdDetails != null) {
                        response.put("errorMessage", "Unique ID already used by someone else.");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else {
                        UniqueIdDetails uniqueIdDetailsDelete = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(secOld.getUniqueId());
                        uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetailsDelete.getId());
                        
                        UniqueIdDetails uniqueIdDetails2 = new UniqueIdDetails();
                        uniqueIdDetails2.setUniqueId(sec.getUniqueId());
                        uniqueIdDetails2.setEntity("Sec " + sec.getId());
                        uniqueIdDetails2.setFullName(sec.getFullName());
                        uniqueIdDetails2.setMobileNumber(sec.getMobileNumber());
                        uniqueIdDetails2.setAddress(sec.getAddress());
                        uniqueIdDetailsService.saveUniqueIdDetails(uniqueIdDetails2);
                        
                        Sec updatedSec = secService.updateSec(sec.getId(), sec);
                        if (updatedSec != null) {
                            response.put("successMessage", "Sec saved successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("errorMessage", "Failed to update the Sec.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                        }
                    }
                } else {
                    Sec updatedSec = secService.updateSec(sec.getId(), sec);
                    if (updatedSec != null) {
                        response.put("successMessage", "Sec updated successfully.");
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("errorMessage", "Failed to update the Sec.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/sec/selected/{productId}")
    public ResponseEntity<?> getSelectedSec(@PathVariable Long productId) {
        Optional<Sec> sec = secService.getSecById(productId);
        if (sec.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sec not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("selectedProduct", sec.get());
        addLicenseInfoToModel(response);
        addUsernameAndRoleToResponse(response, SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deleteSec/{productId}")
    public ResponseEntity<?> deleteSecDetails(@PathVariable Long productId) {
        try {
            Map<String, String> response = new HashMap<>();
            Optional<Sec> secCopy = secService.getSecById(productId);
            String uniqueId = null;
            
            if (secCopy.isPresent()) {
                uniqueId = secCopy.get().getUniqueId();
            }
            
            Sec updateSec = secService.deleteSecDetails(productId);
            if (updateSec != null) {
                UniqueIdDetails uniqueIdDetailsDelete = uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(uniqueId);
                uniqueIdDetailsService.deleteUniqueIdDetails(uniqueIdDetailsDelete.getId());
                
                response.put("successMessage", "Sec details deleted successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("errorMessage", "Failed to delete Sec details.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
