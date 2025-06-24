package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Feg;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.repository.FegRepository;

@Service
public class FegService {
  @Autowired
  private FegRepository fegRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  public List<Feg> addFeg(List<Integer> fegData) {
    List<Feg> feg = fegData.stream().map(intValue -> new Feg()).toList();
    return this.fegRepository.saveAll(feg);
  }
  
  public List<Feg> getAllFegDetails() {
    return this.fegRepository.findAll();
  }
  
  public Optional<Feg> getFegById(Long id) {
    return this.fegRepository.findById(id);
  }
  
  public Feg addFeg(Feg feg) {
    Feg existingFeg = this.fegRepository.findByUniqueId(feg.getUniqueId());
    if (existingFeg != null)
      throw new IllegalArgumentException("An Feg with the same UniqueId already exists"); 
    return (Feg)this.fegRepository.save(feg);
  }
  
  public Feg updateFeg(Long id, Feg updatedFeg) {
    Optional<Feg> existingFeg = this.fegRepository.findById(id);
    if (existingFeg.isPresent()) {
      Feg fegToUpdate = existingFeg.get();
      Feg existingFegWithNewUniqueId = this.fegRepository.findByUniqueId(updatedFeg.getUniqueId());
      if (existingFegWithNewUniqueId != null && !existingFegWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("An Feg with the same UniqueId already exists"); 
      fegToUpdate.setUniqueId(updatedFeg.getUniqueId());
      fegToUpdate.setFullName(updatedFeg.getFullName());
      fegToUpdate.setMobileNumber(updatedFeg.getMobileNumber());
      fegToUpdate.setAddress(updatedFeg.getAddress());
      return (Feg)this.fegRepository.save(fegToUpdate);
    } 
    throw new IllegalArgumentException("Feg not found");
  }
  
  public Feg deleteFegDetails(Long id) {
    Optional<Feg> existingFeg = this.fegRepository.findById(id);
    if (existingFeg.isPresent()) {
      Feg fegToUpdate = existingFeg.get();
      fegToUpdate.setUniqueId(null);
      fegToUpdate.setFullName(null);
      fegToUpdate.setMobileNumber(null);
      fegToUpdate.setAddress(null);
      return (Feg)this.fegRepository.save(fegToUpdate);
    } 
    throw new IllegalArgumentException("Feg not found");
  }
  
  public String processAndSaveDetails(Long fegId) {
    if (this.fegRepository == null || this.inscanService == null)
      throw new IllegalStateException("Feg repository or Inscan service not initialized"); 
    Optional<Feg> optionalFeg = this.fegRepository.findById(fegId);
    if (optionalFeg.isPresent()) {
      Feg feg = optionalFeg.get();
      StringBuilder detailsBuilder = (new StringBuilder("FEG/HPNSK/")).append(feg.getId());
      String department = "Operation";
      String sub_department = "FEG";
      String details = "FEG/HPNSK/";
      Long ofcid = feg.getId();
      String name = feg.getFullName();
      String uniqueId = feg.getUniqueId();
      String mobile = feg.getMobileNumber();
      String address = feg.getAddress();
      String contractor=null;
      String firmName=null;
      
      Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
      if (inscan == null) {
//        String str = "Y";
    	  String str = "N";
        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, str,contractor,firmName);
        return "In";
      } 
      String mainGateStatus = "N";
      Licensegate licensegate = this.licenseGateService.findByUniqueId(uniqueId);
      boolean isLicenseGateIn = false;
      if (licensegate == null || (licensegate != null && licensegate.getExitDateTime() != null))
        isLicenseGateIn = true; 
      if (!isLicenseGateIn)
        return "Please exit from License gate"; 
      if (inscan.getExitDateTime() != null) {
        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, mainGateStatus,contractor,firmName);
        return "Scan In";
      } 
      this.inscanService.updateDetailsToInscan(inscan);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Feg not found");
  }
  
  public Feg getDetailsByUniqueId(String uniqueId) {
    return this.fegRepository.findByUniqueId(uniqueId);
  }
  
  public String getFullName(Long entityId) {
    Feg feg = this.fegRepository.findById(entityId).orElse(null);
    if (feg != null) {
      String fullName = feg.getFullName();
      return fullName;
    } 
    return "Unknown Feg";
  }
  
  public String processAndSaveLicenseGateDetails(Long fegId) {
    if (this.fegRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Feg repository or LicenseGateService service not initialized"); 
    Optional<Feg> optionalFeg = this.fegRepository.findById(fegId);
    if (optionalFeg.isPresent()) {
      Feg feg = optionalFeg.get();
      StringBuilder detailsBuilder = (new StringBuilder("FEG/HPNSK/")).append(feg.getId());
      String department = "Operation";
      String sub_department = "FEG";
      String details = detailsBuilder.toString();
      Long ofcId = feg.getId();
      String name = feg.getFullName();
      String uniqueId = feg.getUniqueId();
      String mobile = feg.getMobileNumber();
      String address = feg.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, fegId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, fegId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Feg not found");
  }
}
