package com.hpcl.inout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hpcl.inout.entity.PasswordReset;
import com.hpcl.inout.service.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:5173")  // Consider making this dynamic or specific for production environments
public class DashboardController {

    @Autowired
    private OperationService operationService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private InscanService inscanService;

    @Autowired
    private LicenseGateService licenseGateService;
    
    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private VisitorService visitorService;

    // Get Dashboard Counts
    @GetMapping("/counts")
    public ResponseEntity<Map<String, Long>> getDashboardCounts() {
        Map<String, Long> counts = new HashMap<>();

        try {
            // Inscan related counts
            counts.put("totalInscanRecordCount", inscanService.getCountOfInscanDetailsForCurrentDay());
            counts.put("totalInscanRecordCountForOperation", inscanService.countByEntryDateTimeBetweenForOperation());
            counts.put("totalInscanRecordCountForProject", inscanService.countByEntryDateTimeBetweenForProject());
            counts.put("totalInscanRecordCountForVisitor", inscanService.countByEntryDateTimeBetweenForVisitor());

            // License gate related counts
            counts.put("totalLicenseGateRecordCount", licenseGateService.getCountOfLicensegateDetailsForCurrentDay());
            counts.put("totalLicenseGateRecordCountForOperation", licenseGateService.countByEntryDateTimeBetweenForOperationLicenseGate());
            counts.put("totalLicenseGateRecordCountForProject", licenseGateService.countByEntryDateTimeBetweenForProjectLicensegate());
            counts.put("totalLicenseGateRecordCountForVisitor", licenseGateService.countByEntryDateTimeBetweenForVisitorLicensegate());

         // Driver gate related counts
//            counts.put("totalDriverGateRecordCount", driverGateService.getCountOfDrivergateDetailsForCurrentDay());
           
            // Operation related counts
            counts.put("totalOperationRecordCount", operationService.getOperationTotalRecordCount());

            // Driver and Project counts
            counts.put("totalDriverRecordCount", driverService.getDriverTotalRecordCount());
            counts.put("totalProjectRecordCount", projectService.getProjectTotalRecordCount());

            // Visitor count
            counts.put("totalVisitorRecordCount", (long) visitorService.countVisitorsWithFullNameNotNull());

            // Calculate de-license operation count (Main Gate Operation Count - License Gate Operation Count)
            long totalOperationForMainGate = inscanService.countByEntryDateTimeBetweenForOperation();
            long totalOperationForLicenseGate = licenseGateService.countByEntryDateTimeBetweenForOperationLicenseGate();

            long deLicenseOperationCount = totalOperationForMainGate - totalOperationForLicenseGate;
            counts.put("deLicenseOperationCount", deLicenseOperationCount);

            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            // Logging the exception (if needed)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<>());
        }
    }

    // Reset Operation
    @PostMapping("/resetCount")
    public ResponseEntity<Map<String, String>> resetForm(@RequestBody Map<String, String> payload) {
        PasswordReset passwordReset = this.passwordResetService.getPasswordResetEntity();
        Map<String, String> response = new HashMap<>();

        // Get the 'reset' parameter from the request body
        String reset = payload.get("reset");

        if (reset != null && reset.equals(passwordReset.getResetPassword())) {
            try { 
                // Perform the update operations
                this.inscanService.updateNullExitDateTime();
                this.licenseGateService.updateNullExiteTime();
                
                response.put("message", "Reset values match!");
                return ResponseEntity.ok(response); // Return a success response
            } catch (Exception e) {
                response.put("message", "An error occurred while resetting.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            response.put("message", "Reset values do not match!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // Return an error response
        }
    }
    
    
 // ✅ Perform password reset
 	@PostMapping("/setPassword")
 	public ResponseEntity<?> resetPassword(@RequestBody PasswordReset resetEntity) {
 		passwordResetService.resetPassword(resetEntity, resetEntity.getResetPassword());
 		return ResponseEntity.ok("Password set successfully.");
 	}
}
