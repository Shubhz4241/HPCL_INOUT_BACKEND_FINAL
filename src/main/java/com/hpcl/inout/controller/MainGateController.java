package com.hpcl.inout.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.License;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.User;
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
import com.hpcl.inout.service.PackedService;
import com.hpcl.inout.service.ProjectService;
import com.hpcl.inout.service.SecService;
import com.hpcl.inout.service.TatService;
import com.hpcl.inout.service.TransportorService;
import com.hpcl.inout.service.VisitorService;
import com.hpcl.inout.service.WorkmanService;

@RestController
@RequestMapping("/mainGate")
public class MainGateController {
  @Autowired
  private OfficerService officerService;
  
  @Autowired
  private ContractorService contractorService;
  
  @Autowired
  private EmployeeService employeeService;
  
  @Autowired
  private GatService gatService;
  
  @Autowired
  private TatService tatService;
  
  @Autowired
  private FegService fegService;
  
  @Autowired
  private SecService secService;
  
  @Autowired
  private ContractorworkmanService contractorWorkmanService;
  
  @Autowired
  private MathadiService mathadiService;
  
  @Autowired
  private PackedService packedService;
  
  @Autowired
  private BulkService bulkService;
  
  @Autowired
  private TransportorService transportorService;
  
  @Autowired
  private WorkmanService workmanService;
  
  @Autowired
  private AmcService amcService;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private VisitorService visitorService;
  
  @Autowired
  private ProjectService projectService;
  
  @Autowired
  private LicenseGateService licensegateService;
  
  @Autowired
  private UserRepository userRepository;
  
//  private void addUsernameAndRoleToModel(Model model, Principal principal) {
//    String username = principal.getName();
//    User user = this.userRepository.findByUserName(username);
//    if (user != null) {
//      String role = user.getRole();
//      model.addAttribute("username", username);
//      model.addAttribute("userRole", role);
//    } 
//  }
//  
//  private void addLicenseInfoToModel(Model model) {
//    User admin = (User)this.userRepository.getReferenceById(Integer.valueOf(1));
//    License license = admin.getLicense();
//    long remainingdays = ChronoUnit.DAYS.between(LocalDate.now(), license.getExpirydate());
//    if (remainingdays == 0L) {
//      model.addAttribute("remainingdays", Boolean.valueOf(false));
//    } else if (remainingdays == 1L) {
//      model.addAttribute("onedayremain", Boolean.valueOf(true));
//    } else {
//      model.addAttribute("remainingdays", Long.valueOf(remainingdays));
//    } 
//  }
  
//  @GetMapping({"/mainGate"})
//  public String dshbord(Model model) {
//    Long totalInscanRecordCount = this.inscanService.getCountOfInscanDetailsForCurrentDay();
//    model.addAttribute("totalInscanRecordCount", totalInscanRecordCount);
//    Long totalInscanRecordCountForOperation = this.inscanService.countByEntryDateTimeBetweenForOperation();
//    model.addAttribute("totalInscanRecordCountForOperation", totalInscanRecordCountForOperation);
//    Long totalInscanRecordCountForProject = this.inscanService.countByEntryDateTimeBetweenForProject();
//    model.addAttribute("totalInscanRecordCountForProject", totalInscanRecordCountForProject);
//    Long totalInscanRecordCountForVisitor = this.inscanService.countByEntryDateTimeBetweenForVisitor();
//    model.addAttribute("totalInscanRecordCountForVisitor", totalInscanRecordCountForVisitor);
//    Long totalLicenseGateRecordCountForOperation = this.LicensegateService.countByEntryDateTimeBetweenForOperationLicenseGate();
//    model.addAttribute("totalLicenseGateRecordCountForOperation", totalLicenseGateRecordCountForOperation);
//    Long totalLicenseGateRecordCount = this.LicensegateService.getCountOfLicensegateDetailsForCurrentDay();
//    model.addAttribute("totalLicenseGateRecordCount", totalLicenseGateRecordCount);
//    Long totalLicenseGateRecordCountForProject = this.LicensegateService.countByEntryDateTimeBetweenForProjectLicensegate();
//    model.addAttribute("totalLicenseGateRecordCountForProject", totalLicenseGateRecordCountForProject);
//    Long totalLicenseGateRecordCountForVisitor = this.LicensegateService.countByEntryDateTimeBetweenForVisitorLicensegate();
//    model.addAttribute("totalLicenseGateRecordCountForVisitor", totalLicenseGateRecordCountForVisitor);
//    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
//    addLicenseInfoToModel(model);
//    return "mainGate";
//  }
  
//  @GetMapping({"/maingateOperation"})
//  public String getInfoMoreI(Model model) {
//    List<Inscan> inscanDetailsForOperation = this.inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();
//    model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
//    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
//    addLicenseInfoToModel(model);
//    return "maingateOperation";
//  }
//  
//  @GetMapping({"/maingateDriver"})
//  public String getMaingateDriver(Model model) {
//    List<Inscan> operatorTotalDetails = this.inscanService.getAllInscanDetailsForCurrentDay();
//    model.addAttribute("operatorTotalDetails", operatorTotalDetails);
//    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
//    addLicenseInfoToModel(model);
//    return "maingateDriver";
//  }
//  
//  @GetMapping({"/maingeteProject"})
//  public String getMaingateProject(Model model) {
//    List<Inscan> inscanDetailsForProject = this.inscanService.findByEntryDateTimeBetweenOrderByDetailsForProject();
//    model.addAttribute("inscanDetailsForProject", inscanDetailsForProject);
//    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
//    addLicenseInfoToModel(model);
//    return "maingeteProject";
//  }
//  
//  @GetMapping({"/maingeteVisitor"})
//  public String getMaingateVisitor(Model model) {
//    List<Inscan> inscanDetailsForVisitor = this.inscanService.findByEntryDateTimeBetweenOrderByDetailsForVisitor();
//    model.addAttribute("inscanDetailsForVisitor", inscanDetailsForVisitor);
//    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
//    addLicenseInfoToModel(model);
//    return "maingateVisitor";
//  }
//  
//  @GetMapping({"/totalOperation"})
//  public String getTotalOperation(Model model) {
//    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
//    addLicenseInfoToModel(model);
//    return "totalOperation";
//  }//
  
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
  
	@GetMapping("/maingateOperation")
    public ResponseEntity<Map<String, Object>> getInfoMoreI(Authentication authentication) {
        List<Inscan> inscanDetailsForOperation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();
        Map<String, Object> response = new HashMap<>();
        response.put("inscanDetailsForOperation", inscanDetailsForOperation);
        addUsernameAndRoleToResponse(response, authentication);
        addLicenseInfoToModel(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/maingateDriver")
    public ResponseEntity<Map<String, Object>> getMaingateDriver(Authentication authentication) {
        List<Inscan> operatorTotalDetails = inscanService.getAllInscanDetailsForCurrentDay();
        Map<String, Object> response = new HashMap<>();
        response.put("operatorTotalDetails", operatorTotalDetails);
        addUsernameAndRoleToResponse(response, authentication);
        addLicenseInfoToModel(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/maingateProject")
    public ResponseEntity<Map<String, Object>> getMaingateProject(Authentication authentication) {
        List<Inscan> inscanDetailsForProject = inscanService.findByEntryDateTimeBetweenOrderByDetailsForProject();
        Map<String, Object> response = new HashMap<>();
        response.put("inscanDetailsForProject", inscanDetailsForProject);
        addUsernameAndRoleToResponse(response, authentication);
        addLicenseInfoToModel(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/maingateVisitor")
    public ResponseEntity<Map<String, Object>> getMaingateVisitor(Authentication authentication) {
        List<Inscan> inscanDetailsForVisitor = inscanService.findByEntryDateTimeBetweenOrderByDetailsForVisitor();
        Map<String, Object> response = new HashMap<>();
        response.put("inscanDetailsForVisitor", inscanDetailsForVisitor);
        addUsernameAndRoleToResponse(response, authentication);
        addLicenseInfoToModel(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/totalInscan")
    public ResponseEntity<Map<String, Object>> getTotalInscan(Authentication authentication) {
        // Get all full Inscan details for each department
        List<Inscan> visitor = inscanService.findByEntryDateTimeBetweenOrderByDetailsForVisitor();
        List<Inscan> operation = inscanService.findByEntryDateTimeBetweenOrderByDetailsForOperation();
        List<Inscan> driver = inscanService.getAllInscanDetailsForCurrentDay(); // assuming this is for Driver
        List<Inscan> project = inscanService.findByEntryDateTimeBetweenOrderByDetailsForProject();

        // Combine all into one list
        List<Inscan> all = new ArrayList<>();
        all.addAll(visitor);
        all.addAll(operation);
        all.addAll(driver);
        all.addAll(project);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("allInscanDetails", all);
        addUsernameAndRoleToResponse(response, authentication);
        addLicenseInfoToModel(response);

        return ResponseEntity.ok(response);
    }

  
//  @GetMapping({"/licenseGate"})
//  public String dshbord1(Model model) {
//    Long totalInscanRecordCount = this.inscanService.getCountOfInscanDetailsForCurrentDay();
//    model.addAttribute("totalInscanRecordCount", totalInscanRecordCount);
//    Long totalInscanRecordCountForOperation = this.inscanService.countByEntryDateTimeBetweenForOperation();
//    model.addAttribute("totalInscanRecordCountForOperation", totalInscanRecordCountForOperation);
//    Long totalInscanRecordCountForProject = this.inscanService.countByEntryDateTimeBetweenForProject();
//    model.addAttribute("totalInscanRecordCountForProject", totalInscanRecordCountForProject);
//    Long totalInscanRecordCountForVisitor = this.inscanService.countByEntryDateTimeBetweenForVisitor();
//    model.addAttribute("totalInscanRecordCountForVisitor", totalInscanRecordCountForVisitor);
//    Long totalLicenseGateRecordCountForOperation = this.LicensegateService.countByEntryDateTimeBetweenForOperationLicenseGate();
//    model.addAttribute("totalLicenseGateRecordCountForOperation", totalLicenseGateRecordCountForOperation);
//    Long totalLicenseGateRecordCount = this.LicensegateService.getCountOfLicensegateDetailsForCurrentDay();
//    model.addAttribute("totalLicenseGateRecordCount", totalLicenseGateRecordCount);
//    Long totalLicenseGateRecordCountForProject = this.LicensegateService.countByEntryDateTimeBetweenForProjectLicensegate();
//    model.addAttribute("totalLicenseGateRecordCountForProject", totalLicenseGateRecordCountForProject);
//    Long totalLicenseGateRecordCountForVisitor = this.LicensegateService.countByEntryDateTimeBetweenForVisitorLicensegate();
//    model.addAttribute("totalLicenseGateRecordCountForVisitor", totalLicenseGateRecordCountForVisitor);
//    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
//    addLicenseInfoToModel(model);
//    return "licenseGate";
//  }
  
//  @PostMapping({"/maingatein"})
//  public String processForm(@RequestParam String inputValue, RedirectAttributes redirectAttributes) {
//    System.out.println("Input value: " + inputValue);
//    String inoutFlag = "";
//    String[] parts = inputValue.split("/");
//    if (parts.length == 3) {
//      String entityType = parts[0];
//      String category = parts[1];
//      String idStr = parts[2];
//      if ("HPNSK".equals(category))
//        try {
//          boolean restrictstatus;
//          Long entityId = Long.valueOf(Long.parseLong(idStr));
//          String fullName = null;
//          switch (entityType) {
//            case "OFC":
//              fullName = this.officerService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.officerService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "EMP":
//              fullName = this.emplloyeeService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.emplloyeeService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "GAT":
//              fullName = this.gatService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.gatService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "TAT":
//              fullName = this.tatService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.tatService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "FEG":
//              fullName = this.fegService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.fegService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "SEC":
//              fullName = this.secService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.secService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "CON":
//              fullName = this.contractorService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.contractorService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "CONW":
//              fullName = this.contractorWorkmanService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.contractorWorkmanService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//              
//              
//            case "MT":
//                fullName = this.mathadiService.getFullName(entityId);
//                if (fullName == null || fullName.trim().isEmpty()) {
//                  redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                  return "redirect:/mainGate";
//                } 
//                inoutFlag = this.mathadiService.processAndSaveDetails(entityId);
//                redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//                System.out.println("inoutFlag " + inoutFlag);
//                return "redirect:/mainGate";
//              
//              
//            case "PT":
//              fullName = this.packedService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.packedService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "BK":
//              fullName = this.bulkService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.bulkService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "TR":
//              fullName = this.transportorService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.transportorService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "PW":
//              fullName = this.workmanService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.workmanService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "AMC":
//              fullName = this.amcService.getFullName(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              inoutFlag = this.amcService.processAndSaveDetails(entityId);
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//            case "VS":
//              fullName = this.visitorService.getFullName(entityId);
//              restrictstatus = this.visitorService.restrictornot(entityId);
//              if (fullName == null || fullName.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid data for " + entityType + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              if (!restrictstatus) {
//                inoutFlag = this.visitorService.processAndSaveDetails(entityId);
//              } else {
//                redirectAttributes.addFlashAttribute("UnknownEntityType", "User is restricted." + fullName + " with ID " + entityId);
//                return "redirect:/mainGate";
//              } 
//              redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
//              System.out.println("inoutFlag " + inoutFlag);
//              return "redirect:/mainGate";
//          } 
//          System.out.println("Unknown entity type: " + entityType);
//          redirectAttributes.addFlashAttribute("UnknownEntityType", "UnknownEntityType" + inputValue);
//          return "redirect:/mainGate";
//        } catch (NumberFormatException e) {
//          System.out.println("Invalid ID format: " + idStr);
//        }  
//    } 
//    redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid input format" + inputValue);
//    return "redirect:/mainGate";
//  }
//  

  @PostMapping("/maingatescanin")
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
            case "OFC":
              fullName = this.officerService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("OFC", entityId);
              inoutFlag = this.officerService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "EMP":
              fullName = this.employeeService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("EMP", entityId);
              inoutFlag = this.employeeService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "GAT":
              fullName = this.gatService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("GAT", entityId);
              inoutFlag = this.gatService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "TAT":
              fullName = this.tatService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("TAT", entityId);
              inoutFlag = this.tatService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "FEG":
              fullName = this.fegService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("FEG", entityId);
              inoutFlag = this.fegService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "SEC":
              fullName = this.secService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("SEC", entityId);
              inoutFlag = this.secService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "CON":
              fullName = this.contractorService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("CON", entityId);
              inoutFlag = this.contractorService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "CONW":
              fullName = this.contractorWorkmanService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("CONW", entityId);
              inoutFlag = this.contractorWorkmanService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "MT":
              fullName = this.mathadiService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("MT", entityId);
              inoutFlag = this.mathadiService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "PT":
              fullName = this.packedService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("PT", entityId);
              inoutFlag = this.packedService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "BK":
              fullName = this.bulkService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("BK", entityId);
              inoutFlag = this.bulkService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "TR":
              fullName = this.transportorService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("TR", entityId);
              inoutFlag = this.transportorService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "PW":
              fullName = this.workmanService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("PW", entityId);
              inoutFlag = this.workmanService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "AMC":
              fullName = this.amcService.getFullName(entityId);
              if (isInvalid(fullName)) return invalid("AMC", entityId);
              inoutFlag = this.amcService.processAndSaveDetails(entityId);
              return success(inoutFlag, fullName);

            case "VS":
              fullName = this.visitorService.getFullName(entityId);
              boolean restrictstatus = this.visitorService.restrictornot(entityId);
              if (isInvalid(fullName)) return invalid("VS", entityId);
              if (restrictstatus)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is restricted: " + fullName + " with ID " + entityId);
              inoutFlag = this.visitorService.processAndSaveDetails(entityId);
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
  
  @PostMapping("/licensegatescanin")
  public ResponseEntity<?> licenseGateIn(@RequestParam String inputValue) {
      System.out.println("Input value: " + inputValue);
      String inoutFlag = "";
      String[] parts = inputValue.split("/");
      
      if (parts.length == 3) {
          String entityType = parts[0];
          String category = parts[1];
          String idStr = parts[2];
          System.out.println(inputValue);
          if ("HPNSK".equals(category)) {
              try {
            	  System.out.println("Input value: " + inputValue);
                  
                  Long entityId = Long.valueOf(Long.parseLong(idStr));
                  String fullName = null;
                  String status = this.inscanService.getMainGateSatus(entityId, entityType);
                  System.out.println("Type"+entityType);
                  System.out.println("Entity ID"+entityId);
                  System.out.println("Status"+status);
                  if (status != null && status.equalsIgnoreCase("N")) {
                      boolean restrictstatus;

                      switch (entityType) {
                          case "OFC":
                              fullName = this.officerService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.officerService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "EMP":
                              fullName = this.employeeService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.employeeService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "GAT":
                              fullName = this.gatService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.gatService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "TAT":
                              fullName = this.tatService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.tatService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "FEG":
                              fullName = this.fegService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.fegService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "SEC":
                              fullName = this.secService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.secService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "CON":
                              fullName = this.contractorService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.contractorService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "CONW":
                              fullName = this.contractorWorkmanService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.contractorWorkmanService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "MT":
                              fullName = this.mathadiService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.mathadiService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "PT":
                        	  System.out.println("PKD");
                              fullName = this.packedService.getFullName(entityId);
                              System.out.println(fullName);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.packedService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "BK":
                              fullName = this.bulkService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.bulkService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "TR":
                              fullName = this.transportorService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.transportorService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "PW":
                              fullName = this.workmanService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.workmanService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "AMC":
                              fullName = this.amcService.getFullName(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              inoutFlag = this.amcService.processAndSaveLicenseGateDetails(entityId);
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          case "VS":
                              fullName = this.visitorService.getFullName(entityId);
                              System.out.println("Full Name:"+fullName);
                              restrictstatus = this.visitorService.restrictornot(entityId);
                              if (fullName == null || fullName.trim().isEmpty()) {
                                  return ResponseEntity.badRequest().body("Invalid data for " + entityType + " with ID " + entityId);
                              }
                              if (!restrictstatus) {
                                  inoutFlag = this.visitorService.processAndSaveLicenseGateDetails(entityId);
                              } else {
                                  return ResponseEntity.badRequest().body("User is restricted: " + fullName + " with ID " + entityId);
                              }
                              return ResponseEntity.ok("Scan successful: " + inoutFlag + " " + fullName);

                          default:
                              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unknown entity type: " + entityType);
                      }
                  }
                  return ResponseEntity.badRequest().body("Please enter main gate");

              } catch (NumberFormatException e) {
                  return ResponseEntity.badRequest().body("Invalid ID format: " + idStr);
              }
          }
          return ResponseEntity.badRequest().body("Invalid input format: " + inputValue);
      }
	return ResponseEntity.badRequest().body("Invalid input format: " + inputValue);
  }
  
  @GetMapping("/licensegateOperation")
  public ResponseEntity<Map<String, Object>> getInfoMoreL(Authentication authentication) {
      List<Licensegate> licensegateDetailsForOperation = licensegateService.findByEntryDateTimeBetweenOrderByDetailsForOperationLicenseGate();
      Map<String, Object> response = new HashMap<>();
      response.put("licensegateDetailsForOperation", licensegateDetailsForOperation);
      addUsernameAndRoleToResponse(response, authentication);
      addLicenseInfoToModel(response);
      return ResponseEntity.ok(response);
  }

  @GetMapping("/licensegateDriver")
  public ResponseEntity<Map<String, Object>> getLicensegateDriver(Authentication authentication) {
      List<Licensegate> operatorTotalDetails = licensegateService.getAllLicensegateDetailsForCurrentDay();
      Map<String, Object> response = new HashMap<>();
      response.put("operatorTotalDetails", operatorTotalDetails);
      addUsernameAndRoleToResponse(response, authentication);
      addLicenseInfoToModel(response);
      return ResponseEntity.ok(response);
  }

  @GetMapping("/licensegateProject")
  public ResponseEntity<Map<String, Object>> getLicesegateProject(Authentication authentication) {
      List<Licensegate> licensegateDetailsForProject = licensegateService.findByEntryDateTimeBetweenOrderByDetailsForProjectLicensegate();
      Map<String, Object> response = new HashMap<>();
      response.put("licensegateDetailsForProject", licensegateDetailsForProject);
      addUsernameAndRoleToResponse(response, authentication);
      addLicenseInfoToModel(response);
      return ResponseEntity.ok(response);
  }

  @GetMapping("/licensegateVisitor")
  public ResponseEntity<Map<String, Object>> getLicensegateVisitor(Authentication authentication) {
      List<Licensegate> licensegateDetailsForVisitor = licensegateService.findByEntryDateTimeBetweenOrderByDetailsForVisitorLicensegate();
      Map<String, Object> response = new HashMap<>();
      response.put("licensegateDetailsForVisitor", licensegateDetailsForVisitor);
      addUsernameAndRoleToResponse(response, authentication);
      addLicenseInfoToModel(response);
      return ResponseEntity.ok(response);
  }

  @GetMapping("/totalLicensegate")
  public ResponseEntity<Map<String, Object>> getTotalLicensegate(Authentication authentication) {
      // Get all full Inscan details for each department
      List<Licensegate> visitor = licensegateService.findByEntryDateTimeBetweenOrderByDetailsForVisitorLicensegate();
      List<Licensegate> operation = licensegateService.findByEntryDateTimeBetweenOrderByDetailsForOperationLicenseGate();
      List<Licensegate> driver = licensegateService.getAllLicensegateDetailsForCurrentDay(); // assuming this is for Driver
      List<Licensegate> project = licensegateService.findByEntryDateTimeBetweenOrderByDetailsForProjectLicensegate();

      // Combine all into one list
      List<Licensegate> all = new ArrayList<>();
      all.addAll(visitor);
      all.addAll(operation);
      all.addAll(driver);
      all.addAll(project);

      // Prepare response
      Map<String, Object> response = new HashMap<>();
      response.put("allInscanDetails", all);
      addUsernameAndRoleToResponse(response, authentication);
      addLicenseInfoToModel(response);

      return ResponseEntity.ok(response);
  }
//  @GetMapping("/totalOperationLicesegate")
//  public ResponseEntity<Map<String, Object>> getTotalOperationL(Authentication authentication) {
//      Map<String, Object> response = new HashMap<>();
//      addUsernameAndRoleToResponse(response, authentication);
//      addLicenseInfoToModel(response);
//      return ResponseEntity.ok(response);
//  }


  
  @GetMapping({"/restrictVisitor"})
  public ResponseEntity<String> restrictUser(@RequestParam("visitorId") long visitorId) {
    boolean success = this.visitorService.restrictUser(Long.valueOf(visitorId));
    if (success)
      return ResponseEntity.ok("User restricted successfully."); 
    return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to restrict user.");
  }
  
  @GetMapping({"/unrestrictVisitor"})
  public ResponseEntity<String> unrestrictUser(@RequestParam("visitorId") long visitorId) {
    boolean success = this.visitorService.unrestrictUser(Long.valueOf(visitorId));
    if (success)
      return ResponseEntity.ok("User unrestricted successfully."); 
    return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unrestrict user.");
  }
}
