package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.Sec;
import com.hpcl.inout.repository.SecRepository;

@Service
public class SecService {
  @Autowired
  private SecRepository secRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  public List<Sec> addSec(List<Integer> secData) {
    List<Sec> secs = secData.stream().map(intValue -> new Sec()).toList();
    return this.secRepository.saveAll(secs);
  }
  
  public List<Sec> getAllSecDetails() {
    return this.secRepository.findAll();
  }
  
  public Optional<Sec> getSecById(Long id) {
    return this.secRepository.findById(id);
  }
  
  public Sec addSec(Sec sec) {
    Sec existingSec = this.secRepository.findByUniqueId(sec.getUniqueId());
    if (existingSec != null)
      throw new IllegalArgumentException("An sec with the same UniqueId already exists"); 
    return (Sec)this.secRepository.save(sec);
  }
  
  public Sec updateSec(Long id, Sec updatedSec) {
    Optional<Sec> existingSec = this.secRepository.findById(id);
    if (existingSec.isPresent()) {
      Sec secToUpdate = existingSec.get();
      Sec existingSecWithNewUniqueId = this.secRepository.findByUniqueId(updatedSec.getUniqueId());
      if (existingSecWithNewUniqueId != null && !existingSecWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("An Sec with the same UniqueId already exists"); 
      secToUpdate.setUniqueId(updatedSec.getUniqueId());
      secToUpdate.setFullName(updatedSec.getFullName());
      secToUpdate.setMobileNumber(updatedSec.getMobileNumber());
      secToUpdate.setAddress(updatedSec.getAddress());
      secToUpdate.setFirmName(updatedSec.getFirmName());
      return (Sec)this.secRepository.save(secToUpdate);
    } 
    throw new IllegalArgumentException("Sec not found");
  }
  
  public Sec deleteSecDetails(Long id) {
    Optional<Sec> existingSec = this.secRepository.findById(id);
    if (existingSec.isPresent()) {
      Sec secToUpdate = existingSec.get();
      secToUpdate.setUniqueId(null);
      secToUpdate.setFullName(null);
      secToUpdate.setMobileNumber(null);
      secToUpdate.setAddress(null);
      return (Sec)this.secRepository.save(secToUpdate);
    } 
    throw new IllegalArgumentException("Sec not found");
  }
  
  public String processAndSaveDetails(Long secId) {
    if (this.secRepository == null || this.inscanService == null)
      throw new IllegalStateException("Sec repository or Inscan service not initialized"); 
    Optional<Sec> optionalSec = this.secRepository.findById(secId);
    if (optionalSec.isPresent()) {
      Sec sec = optionalSec.get();
      StringBuilder detailsBuilder = (new StringBuilder("SEC/HPNSK/")).append(sec.getId());
      String department = "Operation";
      String sub_department = "SEC";
      String details = "SEC/HPNSK/";
      Long ofcid = sec.getId();
      String name = sec.getFullName();
      String uniqueId = sec.getUniqueId();
      String mobile = sec.getMobileNumber();
      String address = sec.getAddress();
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
    throw new IllegalArgumentException("Sec not found");
  }
  
  public Sec getDetailsByUniqueId(String uniqueId) {
    return this.secRepository.findByUniqueId(uniqueId);
  }
  
  public String getFullName(Long entityId) {
    Sec sec = this.secRepository.findById(entityId).orElse(null);
    if (sec != null) {
      String fullName = sec.getFullName();
      return fullName;
    } 
    return "Unknown Sec";
  }
  
  public String processAndSaveLicenseGateDetails(Long secId) {
    if (this.secRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Sec repository or LicenseGateService service not initialized"); 
    Optional<Sec> optionalSec = this.secRepository.findById(secId);
    if (optionalSec.isPresent()) {
      Sec sec = optionalSec.get();
      StringBuilder detailsBuilder = (new StringBuilder("SEC/HPNSK/")).append(sec.getId());
      String department = "Operation";
      String sub_department = "SEC";
      String details = detailsBuilder.toString();
      Long ofcId = sec.getId();
      String name = sec.getFullName();
      String uniqueId = sec.getUniqueId();
      String mobile = sec.getMobileNumber();
      String address = sec.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, secId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, secId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Sec not found");
  }
}
