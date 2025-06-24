package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Gat;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.repository.GatRepository;

@Service
public class GatService {
  @Autowired
  private GatRepository gatRepository;
  
  private final InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  @Autowired
  public GatService(InscanService inscanService) {
    this.inscanService = inscanService;
  }
  
  public List<Gat> addGat(List<Integer> gatData) {
    List<Gat> gats = gatData.stream().map(intValue -> new Gat()).toList();
    return this.gatRepository.saveAll(gats);
  }
  
  public List<Gat> getAllGatDetails() {
    return this.gatRepository.findAll();
  }
  
  public Optional<Gat> getGatById(Long id) {
    return this.gatRepository.findById(id);
  }
  
  public Gat addGat(Gat gat) {
    Gat existingGat = this.gatRepository.findByUniqueId(gat.getUniqueId());
    if (existingGat != null)
      throw new IllegalArgumentException("An Gat with the same UniqueId already exists"); 
    return (Gat)this.gatRepository.save(gat);
  }
  
  public Gat updateGat(Long id, Gat updatedgat) {
    Optional<Gat> existingGat = this.gatRepository.findById(id);
    if (existingGat.isPresent()) {
      Gat gatToUpdate = existingGat.get();
      Gat existingGatWithNewUniqueId = this.gatRepository.findByUniqueId(updatedgat.getUniqueId());
      if (existingGatWithNewUniqueId != null && !existingGatWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("An officer with the same UniqueId already exists"); 
      gatToUpdate.setUniqueId(updatedgat.getUniqueId());
      gatToUpdate.setFullName(updatedgat.getFullName());
      gatToUpdate.setMobileNumber(updatedgat.getMobileNumber());
      gatToUpdate.setAddress(updatedgat.getAddress());
      return (Gat)this.gatRepository.save(gatToUpdate);
    } 
    throw new IllegalArgumentException("Officer not found");
  }
  
  public Gat deleteGatDetails(Long id) {
    Optional<Gat> existingGat = this.gatRepository.findById(id);
    if (existingGat.isPresent()) {
      Gat gatToUpdate = existingGat.get();
      gatToUpdate.setUniqueId(null);
      gatToUpdate.setFullName(null);
      gatToUpdate.setMobileNumber(null);
      gatToUpdate.setAddress(null);
      return (Gat)this.gatRepository.save(gatToUpdate);
    } 
    throw new IllegalArgumentException("Bulk not found");
  }
  
  public String processAndSaveDetails(Long gatId) {
    if (this.gatRepository == null || this.inscanService == null)
      throw new IllegalStateException("Gat repository or Inscan service not initialized"); 
    Optional<Gat> optionalGat = this.gatRepository.findById(gatId);
    if (optionalGat.isPresent()) {
      Gat gat = optionalGat.get();
      StringBuilder detailsBuilder = (new StringBuilder("GAT/HPNSK/")).append(gat.getId());
      String department = "Operation";
      String sub_department = "GAT";
      String details = "GAT/HPNSK/";
      Long ofcid = gat.getId();
      String name = gat.getFullName();
      String uniqueId = gat.getUniqueId();
      String mobile = gat.getMobileNumber();
      String address = gat.getAddress();
      String contractor=null;
      String firmName=null;
      
      Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
      if (inscan == null) {
//        String str = "Y";
    	  String str = "N";
        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, str,contractor,firmName);
        return "Scan In";
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
    throw new IllegalArgumentException("Gat not found");
  }
  
  public Gat getDetailsByUniqueId(String uniqueId) {
    return this.gatRepository.findByUniqueId(uniqueId);
  }
  
  public String getFullName(Long entityId) {
    Gat gat = this.gatRepository.findById(entityId).orElse(null);
    if (gat != null) {
      String fullName = gat.getFullName();
      return fullName;
    } 
    return "Unknown Gat";
  }
  
  public String processAndSaveLicenseGateDetails(Long gatId) {
    if (this.gatRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("GAT repository or LicenseGateService service not initialized"); 
    Optional<Gat> optionalGat = this.gatRepository.findById(gatId);
    if (optionalGat.isPresent()) {
      Gat gat = optionalGat.get();
      StringBuilder detailsBuilder = (new StringBuilder("GAT/HPNSK/")).append(gat.getId());
      String department = "Operation";
      String sub_department = "GAT";
      String details = detailsBuilder.toString();
      Long ofcId = gat.getId();
      String name = gat.getFullName();
      String uniqueId = gat.getUniqueId();
      String mobile = gat.getMobileNumber();
      String address = gat.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licensegate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licensegate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, gatId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licensegate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, gatId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licensegate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("GAT not found");
  }
}
