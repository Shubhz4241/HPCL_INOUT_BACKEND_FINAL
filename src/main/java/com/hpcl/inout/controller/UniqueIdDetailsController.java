package com.hpcl.inout.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hpcl.inout.entity.UniqueIdDetails;
import com.hpcl.inout.service.UniqueIdDetailsService;

@RestController
@RequestMapping({"/uniqueId-details"})
public class UniqueIdDetailsController {
  @Autowired
  private UniqueIdDetailsService uniqueIdDetailsService;
  
  @GetMapping({"/{uniqueId}"})
  public ResponseEntity<UniqueIdDetails> getUniqueIdDetailsByUniqueId(@PathVariable String uniqueId) {
    try {
    	UniqueIdDetails uniqueIdDetails = this.uniqueIdDetailsService.getUniqueIdDetailsByUniqueId(uniqueId);
      if (uniqueIdDetails != null)
        return ResponseEntity.ok(uniqueIdDetails); 
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).build();
    } 
  }
}
