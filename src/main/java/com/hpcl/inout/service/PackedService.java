package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.Packed;
import com.hpcl.inout.repository.PackedRepository;

@Service
public class PackedService {
  @Autowired
  private PackedRepository packedRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  public List<Packed> addPacked(List<Integer> PackedData) {
    List<Packed> packed = PackedData.stream().map(intValue -> new Packed()).toList();
    return this.packedRepository.saveAll(packed);
  }
  
  public List<Packed> getAllPackedDetails() {
    return this.packedRepository.findAll();
  }
  
  public Optional<Packed> getPackedById(Long id) {
    return this.packedRepository.findById(id);
  }
  
  public Packed addPacked(Packed packed) {
    Packed existingPacked = this.packedRepository.findByUniqueId(packed.getUniqueId());
    if (existingPacked != null)
      throw new IllegalArgumentException("An Packed with the same UniqueId already exists"); 
    return (Packed)this.packedRepository.save(packed);
  }
  
  public Packed updatePacked(Long id, Packed updatedPacked) {
    Optional<Packed> existingPacked = this.packedRepository.findById(id);
    if (existingPacked.isPresent()) {
      Packed packedToUpdate = existingPacked.get();
      Packed existingPackedWithNewUniqueId = this.packedRepository.findByUniqueId(updatedPacked.getUniqueId());
      if (existingPackedWithNewUniqueId != null && !existingPackedWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("An Packed with the same UniqueId already exists"); 
      packedToUpdate.setUniqueId(updatedPacked.getUniqueId());
      packedToUpdate.setFullName(updatedPacked.getFullName());
      packedToUpdate.setMobileNumber(updatedPacked.getMobileNumber());
      packedToUpdate.setAddress(updatedPacked.getAddress());
      packedToUpdate.setFirmName(updatedPacked.getFirmName());
      packedToUpdate.setTruckNumber(updatedPacked.getTruckNumber());
      packedToUpdate.setQr(updatedPacked.getQr()+"/"+updatedPacked.getId());
      return (Packed)this.packedRepository.save(packedToUpdate);
    } 
    throw new IllegalArgumentException("Officer not found");
  }
  
  public Packed deletePackedDetails(Long id) {
    Optional<Packed> existingPacked = this.packedRepository.findById(id);
    if (existingPacked.isPresent()) {
      Packed packedToUpdate = existingPacked.get();
      packedToUpdate.setUniqueId(null);
      packedToUpdate.setFullName(null);
      packedToUpdate.setMobileNumber(null);
      packedToUpdate.setAddress(null);
      packedToUpdate.setFirmName(null);
      return (Packed)this.packedRepository.save(packedToUpdate);
    } 
    throw new IllegalArgumentException("Packed not found");
  }
  
  public String processAndSaveDetails(Long packedId) {
	    if (this.packedRepository == null || this.inscanService == null) {
	        throw new IllegalStateException("Packed repository or Inscan service not initialized");
	    }

	    Optional<Packed> optionalPacked = this.packedRepository.findById(packedId);
	    if (optionalPacked.isEmpty()) {
	        throw new IllegalArgumentException("Packed not found");
	    }

	    Packed packed = optionalPacked.get();

	   
	    String expectedQr = "PT/HPNSK/" + packed.getId();
	    if (packed.getQr() == null || !packed.getQr().equals(expectedQr)) {
	        packed.setQr(expectedQr);
	        this.packedRepository.save(packed); 
	    }

	   
	    String department = "Driver";
	    String sub_department = "PT";
	    String details = packed.getQr(); 
	    Long ofcid = packed.getId();
	    String name = packed.getFullName();
	    String uniqueId = packed.getUniqueId();
	    String mobile = packed.getMobileNumber();
	    String address = packed.getAddress();
	    String contractor = null;
	    String firmName = null;

	  
	    Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
	    if (inscan == null) {
	        this.inscanService.saveDetailsToInscan(
	            details, name, uniqueId, mobile, address, ofcid,
	            department, sub_department, "N", contractor, firmName // // updated 11/06/2025 Ritesh Shingote
	        );
	        return "In";
	    }


	    Licensegate licensegate = this.licenseGateService.findByUniqueId(uniqueId);
	    boolean isLicenseGateIn = (licensegate == null || licensegate.getExitDateTime() != null);

	    if (!isLicenseGateIn) {
	        return "Please exit from License gate";
	    }

	 
	    if (inscan.getExitDateTime() != null) {
	        this.inscanService.saveDetailsToInscan(
	            details, name, uniqueId, mobile, address, ofcid,
	            department, sub_department, "N", contractor, firmName
	        );
	        return "Scan In";
	    }

	    this.inscanService.updateDetailsToInscan(inscan);
	    return "Scan Out";
	}
  
  public Packed getDetailsByUniqueId(String uniqueId) {
    return this.packedRepository.findByUniqueId(uniqueId);
  }
  
  public String getFullName(Long entityId) {
    Packed packed = this.packedRepository.findById(entityId).orElse(null);
    if (packed != null) {
      String fullName = packed.getFullName();
      return fullName;
    } 
    return "Unknown Packed";
  }
  
  public String processAndSaveLicenseGateDetails(Long packedId) {
    if (this.packedRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Packed repository or LicenseGateService service not initialized"); 
    Optional<Packed> optionalPacked = this.packedRepository.findById(packedId);
    if (optionalPacked.isPresent()) {
      Packed packed = optionalPacked.get();
      StringBuilder detailsBuilder = (new StringBuilder("PT/HPNSK/")).append(packed.getId());
      String department = "Operation";
      String sub_department = "PT";
      String details = detailsBuilder.toString();
      Long ofcId = packed.getId();
      String name = packed.getFullName();
      String uniqueId = packed.getUniqueId();
      String mobile = packed.getMobileNumber();
      String address = packed.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, packedId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, packedId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Packed not found");
  }
}