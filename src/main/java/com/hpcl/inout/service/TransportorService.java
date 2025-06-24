package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.Transportor;
import com.hpcl.inout.repository.TransportorRepository;

@Service
public class TransportorService {
  @Autowired
  private TransportorRepository transportorRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  public List<Transportor> addTransportor(List<Integer> transportorData) {
    List<Transportor> transportor = transportorData.stream().map(intValue -> new Transportor()).toList();
    return this.transportorRepository.saveAll(transportor);
  }
  
  public List<Transportor> getAllTransportorDetails() {
    return this.transportorRepository.findAll();
  }
  
  public Optional<Transportor> getTransportorById(Long id) {
    return this.transportorRepository.findById(id);
  }
  
  public Transportor addTransportor(Transportor transportor) {
    Transportor existingTransportor = this.transportorRepository.findByUniqueId(transportor.getUniqueId());
    if (existingTransportor != null)
      throw new IllegalArgumentException("An Transportor with the same UniqueId already exists"); 
    return (Transportor)this.transportorRepository.save(transportor);
  }
  
  public Transportor updateTransportor(Long id, Transportor updatedTransportor) {
    Optional<Transportor> existingTransportor = this.transportorRepository.findById(id);
    if (existingTransportor.isPresent()) {
      Transportor transportorToUpdate = existingTransportor.get();
      Transportor existingTransportorWithNewUniqueId = this.transportorRepository.findByUniqueId(updatedTransportor.getUniqueId());
      if (existingTransportorWithNewUniqueId != null && !existingTransportorWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("An Transportor with the same UniqueId already exists"); 
      transportorToUpdate.setUniqueId(updatedTransportor.getUniqueId());
      transportorToUpdate.setFullName(updatedTransportor.getFullName());
      transportorToUpdate.setMobileNumber(updatedTransportor.getMobileNumber());
      transportorToUpdate.setAddress(updatedTransportor.getAddress());
      transportorToUpdate.setTruckNumber(updatedTransportor.getTruckNumber());
      transportorToUpdate.setFirmName(updatedTransportor.getFirmName());
      transportorToUpdate.setQr(updatedTransportor.getQr()+"/"+updatedTransportor.getId());
      return (Transportor)this.transportorRepository.save(transportorToUpdate);
    } 
    throw new IllegalArgumentException("Transportor not found");
  }
  
  public Transportor deleteTransportorDetails(Long id) {
    Optional<Transportor> existingTransportor = this.transportorRepository.findById(id);
    if (existingTransportor.isPresent()) {
      Transportor transportorToUpdate = existingTransportor.get();
      transportorToUpdate.setUniqueId(null);
      transportorToUpdate.setFullName(null);
      transportorToUpdate.setMobileNumber(null);
      transportorToUpdate.setAddress(null);
      return (Transportor)this.transportorRepository.save(transportorToUpdate);
    } 
    throw new IllegalArgumentException("Transportor not found");
  }
  
  public String processAndSaveDetails(Long transportorId) {
    if (this.transportorRepository == null || this.inscanService == null)
      throw new IllegalStateException("Transportor repository or Inscan service not initialized"); 
    Optional<Transportor> optionalTransportor = this.transportorRepository.findById(transportorId);
    if (optionalTransportor.isPresent()) {
      Transportor transportor = optionalTransportor.get();
      StringBuilder detailsBuilder = (new StringBuilder("TR/HPNSK/")).append(transportor.getId());
      String department = "Driver";
      String sub_department = "TR";
      String details = "TR/HPNSK/";
      Long ofcid = transportor.getId();
      String name = transportor.getFullName();
      String uniqueId = transportor.getUniqueId();
      String mobile = transportor.getMobileNumber();
      String address = transportor.getAddress();
      String contractor="null";
      String firmName="null";
      
      
      Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
      if (inscan == null ) {
        String str = "Y";
        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, str,contractor,firmName);
        return "In";
      } 
   // Check License gate status
      Licensegate licensegate = this.licenseGateService.findByUniqueId(uniqueId);
      boolean isLicenseGateIn = (licensegate == null || licensegate.getExitDateTime() != null);
      
      if (!isLicenseGateIn) {
          return "Please exit from License gate";
      }
      
      // Handle scan in/out logic
      if (inscan.getExitDateTime() != null) {
          this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, "N", contractor, firmName);
          return "Scan In";
      } 
      
      this.inscanService.updateDetailsToInscan(inscan);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Transportor not found");
  }
  
  public Transportor getDetailsByUniqueId(String uniqueId) {
    return this.transportorRepository.findByUniqueId(uniqueId);
  }
  
  public String getFullName(Long entityId) {
    Transportor transportor = this.transportorRepository.findById(entityId).orElse(null);
    if (transportor != null) {
      String fullName = transportor.getFullName();
      return fullName;
    } 
    return "Unknown Transportor";
  }
  
  public String processAndSaveLicenseGateDetails(Long transportorId) {
    if (this.transportorRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Transportor repository or LicenseGateService service not initialized"); 
    Optional<Transportor> optionalTransportor = this.transportorRepository.findById(transportorId);
    if (optionalTransportor.isPresent()) {
      Transportor transportor = optionalTransportor.get();
      StringBuilder detailsBuilder = (new StringBuilder("TR/HPNSK/")).append(transportor.getId());
      String department = "Operation";
      String sub_department = "TR";
      String details = detailsBuilder.toString();
      Long ofcId = transportor.getId();
      String name = transportor.getFullName();
      String uniqueId = transportor.getUniqueId();
      String mobile = transportor.getMobileNumber();
      String address = transportor.getAddress();
      String contractor="null";
      String firmName="null";
      
      
      Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, transportorId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, transportorId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Transportor not found");
  }
}
