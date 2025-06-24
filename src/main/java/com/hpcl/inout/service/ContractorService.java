package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Contractor;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.repository.ContractorRepository;

@Service
public class ContractorService {
  @Autowired
  private ContractorRepository contractorRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  public List<Contractor> addContractor(List<Integer> contractorData) {
    List<Contractor> employee = contractorData.stream().map(intValue -> new Contractor()).toList();
    return this.contractorRepository.saveAll(employee);
  }
  
  public List<Contractor> getAllContractorDetails() {
    return this.contractorRepository.findAll();
  }
  
  public Optional<Contractor> getContractorById(Long id) {
    return this.contractorRepository.findById(id);
  }
  
  public Contractor addContractor(Contractor contractor) {
    Contractor existingContractor = this.contractorRepository.findByUniqueId(contractor.getUniqueId());
    if (existingContractor != null)
      throw new IllegalArgumentException("An Contractor with the same UniqueId already exists"); 
    return (Contractor)this.contractorRepository.save(contractor);
  }
  
  public Contractor updateContractor(Long id, Contractor updatedContractor) {
    Optional<Contractor> existingContractor = this.contractorRepository.findById(id);
    if (existingContractor.isPresent()) {
      Contractor contractorToUpdate = existingContractor.get();
      Contractor existingContractorWithNewUniqueId = this.contractorRepository.findByUniqueId(updatedContractor.getUniqueId());
      if (existingContractorWithNewUniqueId != null && !existingContractorWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("An Contractor with the same UniqueId already exists"); 
      contractorToUpdate.setUniqueId(updatedContractor.getUniqueId());
      contractorToUpdate.setFullName(updatedContractor.getFullName());
      contractorToUpdate.setMobileNumber(updatedContractor.getMobileNumber());
      contractorToUpdate.setAddress(updatedContractor.getAddress());
      contractorToUpdate.setFirmName(updatedContractor.getFirmName());
      return (Contractor)this.contractorRepository.save(contractorToUpdate);
    } 
    throw new IllegalArgumentException("Contractor not found");
  }
  
  public Contractor deleteContractorDetails(Long id) {
    Optional<Contractor> existingContractor = this.contractorRepository.findById(id);
    if (existingContractor.isPresent()) {
      Contractor contractorToUpdate = existingContractor.get();
      contractorToUpdate.setUniqueId(null);
      contractorToUpdate.setFullName(null);
      contractorToUpdate.setMobileNumber(null);
      contractorToUpdate.setAddress(null);
      contractorToUpdate.setFirmName(null);
      return (Contractor)this.contractorRepository.save(contractorToUpdate);
    } 
    throw new IllegalArgumentException("Contractor not found");
  }
  
  public void processContractorInput(String inputValue) {}
  
  public String processAndSaveDetails(Long contractorId) {
    if (this.contractorRepository == null || this.inscanService == null)
      throw new IllegalStateException("Contractor repository or Inscan service not initialized"); 
    Optional<Contractor> optionalContractor = this.contractorRepository.findById(contractorId);
    if (optionalContractor.isPresent()) {
      Contractor contractors = optionalContractor.get();
      StringBuilder detailsBuilder = (new StringBuilder("CON/HPNSK/")).append(contractors.getId());
      String department = "Operation";
      String sub_department = "CON";
      String details = "CON/HPNSK/";
      Long ofcid = contractors.getId();
      String name = contractors.getFullName();
      String uniqueId = contractors.getUniqueId();
      String mobile = contractors.getMobileNumber();
      String address = contractors.getAddress();
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
    throw new IllegalArgumentException("Contractor not found");
  }
  
  public Contractor getDetailsByUniqueId(String uniqueId) {
    return this.contractorRepository.findByUniqueId(uniqueId);
  }
  
  public String getFullName(Long entityId) {
    Contractor con = this.contractorRepository.findById(entityId).orElse(null);
    if (con != null) {
      String fullName = con.getFullName();
      return fullName;
    } 
    return "Unknown Contractor";
  }
  
  public String processAndSaveLicenseGateDetails(Long contractorId) {
    if (this.contractorRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Contractor repository or LicenseGateService service not initialized"); 
    Optional<Contractor> optionalContractor = this.contractorRepository.findById(contractorId);
    if (optionalContractor.isPresent()) {
      Contractor contractors = optionalContractor.get();
      StringBuilder detailsBuilder = (new StringBuilder("CON/HPNSK/")).append(contractors.getId());
      String department = "Operation";
      String sub_department = "CON";
      String details = detailsBuilder.toString();
      Long ofcId = contractors.getId();
      String name = contractors.getFullName();
      String uniqueId = contractors.getUniqueId();
      String mobile = contractors.getMobileNumber();
      String address = contractors.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, contractorId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, contractorId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Contractor not found");
  }
}
