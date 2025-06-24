package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.UniqueIdDetails;
import com.hpcl.inout.repository.UniqueIdDetailsRepository;

@Service
public class UniqueIdDetailsService {
  @Autowired
  private UniqueIdDetailsRepository uniqueIdDetailsRepository;
  
  public UniqueIdDetails saveUniqueIdDetails(UniqueIdDetails uniqueIdDetails) {
    return (UniqueIdDetails)this.uniqueIdDetailsRepository.save(uniqueIdDetails);
  }
  
  public Optional<UniqueIdDetails> getUniqueIdDetailsById(Long id) {
	  return this.uniqueIdDetailsRepository.findById(id);
  }
  
  public List<UniqueIdDetails> getAllUniqueIdDetails() {
	  return this.uniqueIdDetailsRepository.findAll();
  }
  
  public void deleteUniqueIdDetails(Long id) {
    this.uniqueIdDetailsRepository.deleteById(id);
  }
  
  public UniqueIdDetails getUniqueIdDetailsByUniqueId(String uniqueId) {
    return this.uniqueIdDetailsRepository.findByUniqueId(uniqueId);
  }
}
