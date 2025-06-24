package com.hpcl.inout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.hpcl.inout.entity.Amc;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Workman;
import com.hpcl.inout.service.AmcService;
import com.hpcl.inout.service.InscanService;
import com.hpcl.inout.service.UniqueIdDetailsService;
import com.hpcl.inout.service.WorkmanService;

import java.util.*;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController 
{

    @Autowired
    private AmcService amcService;
    
    @Autowired
    private WorkmanService workmanService;

    // POST: Create AMC
    @PostMapping("/submitAmc")
    public List<Amc> createAmc(@RequestBody List<Integer> amcIds) {
        List<Amc> newAmcs = new ArrayList<>();

        for (Integer id : amcIds) {
            Amc amc = new Amc();
            newAmcs.add(amc);
        }

        return amcService.addAmc(amcIds);
    }

    // GET: Fetch all AMC details
    @GetMapping("/amc")
    public Map<String, Object> getAmcDetails() {
        Map<String, Object> response = new HashMap<>();

        List<Amc> amcDetails = amcService.getAmcDetails();
        response.put("amcDetails", amcDetails);

        return response;
    }

    // POST: Save or update AMC
    @PostMapping("/saveAmc")
    public ResponseEntity<?> saveAmc(@RequestBody Amc amc) {
        Map<String, String> response = new HashMap<>();

        try {
            if (amc.getId() == null) {
                // Create new AMC
                Amc createdAmc = amcService.addAmc(amc);
                if (createdAmc != null) {
                    response.put("message", "AMC saved successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("error", "AMC with the same ID already exists.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            } else {
                // Update existing AMC
                Optional<Amc> amcOldOpt = amcService.getAmcById(amc.getId());
                if (!amcOldOpt.isPresent()) {
                    response.put("error", "AMC entry not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Amc amcOld = amcOldOpt.get();

                // Handle update logic based on AMC ID or other fields
                Amc updated = amcService.updateAmc(amc.getId(), amc);
                if (updated != null) {
                    response.put("message", "AMC updated successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("error", "Failed to update AMC.");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }
        } catch (IllegalArgumentException e) {
            response.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // DELETE: Delete AMC details
    @GetMapping("/deleteAmc/{id}")
    public ResponseEntity<?> deleteAmcDetails(@PathVariable("id") Long amcId) {
        try {
            Optional<Amc> amcOpt = amcService.getAmcById(amcId);
            if (amcOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("AMC entry with ID " + amcId + " not found.");
            }

            Amc deletedAmc = amcService.deleteAmcDetails(amcId);
            if (deletedAmc != null) {
                return ResponseEntity.ok("AMC details deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to delete AMC details.");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
    
    
    
    
   /*------------------------------------------------------Workman--------------------------------------------------------------*/ 
    
    @PostMapping("/submitWorkman")
    public List<Workman> createWorkman(@RequestBody List<Integer> newWorkmanIds) {
        List<Workman> newWorkmans = new ArrayList<>();

        for (Integer id : newWorkmanIds) {
            Workman workman = new Workman();
            newWorkmans.add(workman);
        }

        return workmanService.addWorkman(newWorkmanIds);
    }
    
    
    @GetMapping("/workman")
    public Map<String, Object> getWorkmanDetails() {
        Map<String, Object> response = new HashMap<>();

        List<Workman> workmanDetails = workmanService.getAllWorkmanDetails();
        response.put("workmanDetails", workmanDetails);

        return response;
    }
    
    
    @PostMapping("/saveWorkman")
    public ResponseEntity<?> saveWorkman(@RequestBody Workman workman) {
        Map<String, String> response = new HashMap<>();

        try {
            if (workman.getId() == null) {
                // Create new Workman
                Workman createdWorkman = workmanService.addWorkman(workman);
                if (createdWorkman != null) {
                    response.put("message", "Workman saved successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("error", "Workman with the same Aadhar number already exists.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            } else {
                // Update existing Workman
                Optional<Workman> oldWorkmanOpt = workmanService.getWorkmanById(workman.getId());
                if (!oldWorkmanOpt.isPresent()) {
                    response.put("error", "Workman entry not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Workman updated = workmanService.updateWorkman(workman.getId(), workman);
                if (updated != null) {
                    response.put("message", "Workman updated successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("error", "Failed to update Workman.");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }
        } catch (IllegalArgumentException e) {
            response.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    
    @GetMapping("/deleteWorkman/{id}")
    public ResponseEntity<?> deleteWorkmanDetails(@PathVariable("id") Long workmanId) {
        try {
            Optional<Workman> workmanOpt = workmanService.getWorkmanById(workmanId);
            if (workmanOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Workman entry with ID " + workmanId + " not found.");
            }

            Workman deletedWorkman = workmanService.deleteWorkmanDetails(workmanId);
            if (deletedWorkman != null) {
                return ResponseEntity.ok("Workman details deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to delete Workman details.");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }


   
    
}
