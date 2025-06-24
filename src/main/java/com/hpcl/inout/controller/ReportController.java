package com.hpcl.inout.controller;

import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.server.RSocketServer.Transport;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hpcl.inout.entity.Amc;
import com.hpcl.inout.entity.Bulk;
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
import com.hpcl.inout.entity.Transportor;
import com.hpcl.inout.entity.User;
import com.hpcl.inout.entity.Visitor;
import com.hpcl.inout.entity.Workman;
import com.hpcl.inout.repository.UserRepository;
import com.hpcl.inout.service.AmcService;
import com.hpcl.inout.service.BulkService;
import com.hpcl.inout.service.ContractorService;
import com.hpcl.inout.service.ContractorworkmanService;
import com.hpcl.inout.service.EmployeeService;
import com.hpcl.inout.service.FegService;
import com.hpcl.inout.service.GatService;
import com.hpcl.inout.service.InscanService;
import com.hpcl.inout.service.LicenseGateService;
import com.hpcl.inout.service.MathadiService;
import com.hpcl.inout.service.OfficerService;
import com.hpcl.inout.service.SecService;
import com.hpcl.inout.service.TatService;
import com.hpcl.inout.service.TransportorService;
import com.hpcl.inout.service.VisitorService;
import com.hpcl.inout.service.WorkmanService;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {
	@Autowired
	private OfficerService officerService;

	@Autowired
	private LicenseGateService LicensegateService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InscanService inscanService;
	
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
	private BulkService bulkService;
	
	@Autowired
	private TransportorService transportService;
	
	@Autowired
	private WorkmanService workmanService;
	
	@Autowired
	private AmcService amcService;


	@Autowired
	private VisitorService visitorService;
	
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

//	@GetMapping({ "/report" })
//	public String report(Model model) {
//		List<String> officerDetails = this.officerService.getAllOfficerFullNames();
//		model.addAttribute("OfficerDetails", officerDetails);
//		List<Inscan> inscandetails = this.inscanService.getAllInscanDetails();
//		model.addAttribute("reportdetails", inscandetails);
//		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
//		addLicenseInfoToModel(model);
//		return "report";
//	}
	
	@GetMapping("/findNameBySubdept")
	public Map<String, Object> findNameBySubdept(@RequestParam String subdept) {
	    System.out.println("Subdepartment received: " + subdept);

	    Map<String, Object> response = new HashMap<>();

	    switch (subdept.toLowerCase()) {
	        case "ofc":
	        	System.out.println(subdept);
	            List<Officer> officerDetails = officerService.getAllOfficerDetails();
	            response.put("officerDetails", officerDetails);
	            break;
	        case "emp":
	            List<Employee> employeeDetails = employeeService.getAllEmployeeDetails();
	            response.put("employeeDetails", employeeDetails);
	            break;
	        case "con":
	            List<Contractor> contractorDetails = contractorService.getAllContractorDetails();
	            response.put("contractorDetails", contractorDetails);
	            break;
	        case "conw":
	            List<Contractorworkman> cwDetails = contractorworkmanService.getAllContractorworkmanDetails();
	            response.put("contractorWorkmen", cwDetails);
	            break;
	        case "mt":
	            List<Mathadi> mathadiDetails = mathadiService.getAllMathadiDetails();
	            response.put("mathadiDetails", mathadiDetails);
	            break;
	        case "gat":
	            List<Gat> gatDetails = gatService.getAllGatDetails();
	            response.put("gatDetails", gatDetails);
	            break;
	        case "tat":
	            List<Tat> tatDetails = tatService.getAllTatDetails();
	            response.put("tatDetails", tatDetails);
	            break;
	        case "feg":
	            List<Feg> fegDetails = fegService.getAllFegDetails();
	            response.put("fegDetails", fegDetails);
	            break;
	        case "sec":
	            List<Sec> secDetails = secService.getAllSecDetails();
	            response.put("secDetails", secDetails);
	            break;
	        case "bk":
	            List<Bulk> bulkDetails = bulkService.getAllBulkDetails();
	            response.put("bulkDriverDetails", bulkDetails);
	            break;
	        case "tr":
	            List<Transportor> transportDetails = transportService.getAllTransportorDetails();
	            response.put("transportDriverDetails", transportDetails);
	            break;
	        case "pw":
	            List<Workman> projectWorkmen = workmanService.getAllWorkmanDetails();
	            response.put("projectWorkmanDetails", projectWorkmen);
	            break;
	        case "amc":
	            List<Amc> amcDetails = amcService.getAmcDetails();
	            response.put("amcDetails", amcDetails);
	            break;
	        case "visitor":
	            List<Visitor> visitorDetails = visitorService.getAllVisitorDetails();
	            response.put("visitorDetails", visitorDetails);
	            break;
	        default:
	            response.put("message", "Invalid subdepartment: " + subdept);
	            break;
	    }

	    return response;
	}


//	@PostMapping("/generatereport")
//	public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, String> payload) {
//
//	    String fromDate = payload.get("fromdate");
//	    String toDate = payload.get("todate");
//	    String department = payload.get("department");
//	    String subDepartment = payload.get("subdept");
//	    String name = payload.get("name");
//	    String gate = payload.get("gate");
//
//	    System.out.println(
//	        fromDate + ", " + toDate + ", " + department + ", " + subDepartment + ", " + name + ", " + gate);
//
//	    Map<String, Object> response = new HashMap<>();
//
//	    List<?> reportData;
//	    if ("Main Gate".equalsIgnoreCase(gate)) {
//	    	System.out.println(gate);
//	        reportData = inscanService.generateReportDataMainGate(fromDate, toDate, department, subDepartment, !"All".equals(name) ? name : null);
//	    } else if ("License Gate".equalsIgnoreCase(gate)) {
//	        reportData = LicensegateService.generateReportDataLicenseGate(fromDate, toDate, department, subDepartment, !"All".equals(name) ? name : null);
//	    } else if ("".equalsIgnoreCase(gate) || "All".equalsIgnoreCase(gate)) {
//	        reportData = inscanService.generateReportDataMainGate(fromDate, toDate, department, subDepartment, !"All".equals(name) ? name : null);
//	    } else {
//	        throw new IllegalArgumentException("Invalid gate value: " + gate);
//	    }
//
//	    List<String> officerDetails = officerService.getAllOfficerFullNames();
//	    System.out.println(reportData);
//	    response.put("reportData", reportData);
//	    response.put("officerDetails", officerDetails);
//
//	    return ResponseEntity.ok(response);
//	}
	
	@PostMapping("/generatereport")
	public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, String> payload) {

	    String fromDate = payload.get("fromdate");
	    String toDate = payload.get("todate");
	    String department = payload.get("department");
	    String subDepartment = payload.get("subdept");
	    String name = payload.get("name");
	    String gate = payload.get("gate");

	    System.out.println("Request params: " + fromDate + ", " + toDate + ", " + department + ", " + subDepartment + ", " + name + ", " + gate);

	    Map<String, Object> response = new HashMap<>();
	    List<?> reportData;

	    try {
	        // Handle different gate conditions
	        if ("All".equals(department) && "All".equals(subDepartment) && "All".equals(name) && ("All".equals(gate) || gate == null || gate.isEmpty())) {
	            System.out.println("All condition triggered");
	            // For "All" case, pass null values to get all data
	            reportData = inscanService.generateReportDataMainGate(fromDate, toDate, null, null, null);
	            response.put("gateName", "All Gates");
	        }
	        else if ("Main gate".equalsIgnoreCase(gate)) {
	            System.out.println("Main Gate condition");
	            reportData = inscanService.generateReportDataMainGate(fromDate, toDate, 
	                !"All".equals(department) ? department : null, 
	                !"All".equals(subDepartment) ? subDepartment : null, 
	                !"All".equals(name) ? name : null);
	            response.put("gateName", "Main Gate");
	        } 
	        else if ("licensegate".equalsIgnoreCase(gate)) {
	            System.out.println("License Gate condition");
	            reportData = LicensegateService.generateReportDataLicenseGate(fromDate, toDate, 
	                !"All".equals(department) ? department : null, 
	                !"All".equals(subDepartment) ? subDepartment : null, 
	                !"All".equals(name) ? name : null);
	            response.put("gateName", "License Gate");
	        } 
	        else if ("all".equalsIgnoreCase(gate) || gate == null || gate.isEmpty()) {
	            System.out.println("All gates condition");
	            reportData = inscanService.generateReportDataMainGate(fromDate, toDate, 
	                !"All".equals(department) ? department : null, 
	                !"All".equals(subDepartment) ? subDepartment : null, 
	                !"All".equals(name) ? name : null);
	            response.put("gateName", "All Gates");
	        } 
	        else {
	            throw new IllegalArgumentException("Invalid gate value: " + gate);
	        }

	        List<String> officerDetails = officerService.getAllOfficerFullNames();
	        System.out.println("Final reportData size: " + (reportData != null ? reportData.size() : 0));
	        
	        response.put("reportData", reportData);
	        response.put("officerDetails", officerDetails);
	        
	        // Add authentication info
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null) {
	            addUsernameAndRoleToResponse(response, authentication);
	        }
	        
	        // Add license info to response
	        addLicenseInfoToResponse(response);

	        System.out.println("Response: " + response);
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        System.err.println("Error generating report: " + e.getMessage());
	        e.printStackTrace();
	        
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("error", "Failed to generate report: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	private void addLicenseInfoToResponse(Map<String, Object> response) {
	    User admin = this.userRepository.getReferenceById(1); // Assuming admin has ID 1
	    License license = admin.getLicense();

	    long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), license.getExpirydate());

	    System.out.println("The Remaining Data Is "+remainingDays);
	    
	    if (remainingDays == 0L) {
	        response.put("remainingdays", false);
	    } else if (remainingDays == 1L) {
	        response.put("onedayremain", true);
	    } else {
	        response.put("remainingdays", remainingDays);
	    }
	}

	@PostMapping("/nightreport")
    public ResponseEntity<?> generateNightReport(
            @RequestParam("date") String date,
            @RequestParam("fromtime") String fromTime,
            @RequestParam("totime") String toTime) {
        try {
            List<Map<String, Object>> nightReportData = inscanService.generateNightReportData(date, fromTime, toTime);
            return ResponseEntity.ok(nightReportData);
        } catch (ParseException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid date format! Please use yyyy-MM-dd HH:mm.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }	
}
