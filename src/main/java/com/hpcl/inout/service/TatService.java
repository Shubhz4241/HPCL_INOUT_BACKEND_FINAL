package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.Tat;
import com.hpcl.inout.repository.TatRepository;

@Service
public class TatService {
  @Autowired
  private TatRepository tatRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  public List<Tat> addTat(List<Integer> tatData) {
    List<Tat> tat = tatData.stream().map(intValue -> new Tat()).toList();
    return this.tatRepository.saveAll(tat);
  }
  
  public List<Tat> getAllTatDetails() {
    return this.tatRepository.findAll();
  }
  
  public Optional<Tat> getTatById(Long id) {
    return this.tatRepository.findById(id);
  }
  
  public Tat addTat(Tat tat) {
    Tat existingTat = this.tatRepository.findByUniqueId(tat.getUniqueId());
    if (existingTat != null)
      throw new IllegalArgumentException("An Tat with the same UniqueIdr already exists"); 
    return (Tat)this.tatRepository.save(tat);
  }
  
  public Tat updateTat(Long id, Tat updatedTat) {
    Optional<Tat> existingTat = this.tatRepository.findById(id);
    if (existingTat.isPresent()) {
      Tat tatToUpdate = existingTat.get();
      Tat existingTatWithNewUniqueId = this.tatRepository.findByUniqueId(updatedTat.getUniqueId());
      if (existingTatWithNewUniqueId != null && !existingTatWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("An Tat with the same Aadhar number already exists"); 
      tatToUpdate.setUniqueId(updatedTat.getUniqueId());
      tatToUpdate.setFullName(updatedTat.getFullName());
      tatToUpdate.setMobileNumber(updatedTat.getMobileNumber());
      tatToUpdate.setAddress(updatedTat.getAddress());
      return (Tat)this.tatRepository.save(tatToUpdate);
    } 
    throw new IllegalArgumentException("Tat not found");
  }
  
  public Tat deleteTatDetails(Long id) {
    Optional<Tat> existingTat = this.tatRepository.findById(id);
    if (existingTat.isPresent()) {
      Tat tatToUpdate = existingTat.get();
      tatToUpdate.setUniqueId(null);
      tatToUpdate.setFullName(null);
      tatToUpdate.setMobileNumber(null);
      tatToUpdate.setAddress(null);
      return (Tat)this.tatRepository.save(tatToUpdate);
    } 
    throw new IllegalArgumentException("Tat not found");
  }
  
  public String processAndSaveDetails(Long tatId) {
    if (this.tatRepository == null || this.inscanService == null)
      throw new IllegalStateException("Tat repository or Inscan service not initialized"); 
    Optional<Tat> optionalTat = this.tatRepository.findById(tatId);
    if (optionalTat.isPresent()) {
      Tat tat = optionalTat.get();
      StringBuilder detailsBuilder = (new StringBuilder("TAT/HPNSK/")).append(tat.getId());
      String department = "Operation";
      String sub_department = "TAT";
      String details = "TAT/HPNSK/";
      Long ofcid = tat.getId();
      String name = tat.getFullName();
      String uniqueId = tat.getUniqueId();
      String mobile = tat.getMobileNumber();
      String address = tat.getAddress();
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
    throw new IllegalArgumentException("Tat not found");
  }
  
  public Tat getDetailsByUniqueId(String uniqueId) {
    return this.tatRepository.findByUniqueId(uniqueId);
  }
  
  public String getFullName(Long entityId) {
    Tat tat = this.tatRepository.findById(entityId).orElse(null);
    if (tat != null) {
      String fullName = tat.getFullName();
      return fullName;
    } 
    return "Unknown Tat";
  }
  
  public String processAndSaveLicenseGateDetails(Long tatId) {
    if (this.tatRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Tat repository or LicenseGateService service not initialized"); 
    Optional<Tat> optionalTat = this.tatRepository.findById(tatId);
    if (optionalTat.isPresent()) {
      Tat tat = optionalTat.get();
      StringBuilder detailsBuilder = (new StringBuilder("TAT/HPNSK/")).append(tat.getId());
      String department = "Operation";
      String sub_department = "TAT";
      String details = detailsBuilder.toString();
      Long ofcId = tat.getId();
      String name = tat.getFullName();
      String uniqueId = tat.getUniqueId();
      String mobile = tat.getMobileNumber();
      String address = tat.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, tatId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, tatId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("TAT not found");
  }
}
