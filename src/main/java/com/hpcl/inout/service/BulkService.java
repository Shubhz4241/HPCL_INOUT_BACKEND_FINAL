package com.hpcl.inout.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Bulk;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.repository.BulkRepository;

@Service
public class BulkService {
  @Autowired
  private BulkRepository bulkRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  
//  public List<Bulk> addBulk(List<Integer> bulkData) {
//    List<Bulk> bulk = bulkData.stream().map(intValue -> new Bulk()).toList();
//    return this.bulkRepository.saveAll(bulk);
//  }
  public List<Bulk> addBulk(List<Integer> bulkData) {

      if (bulkData.stream().anyMatch(i -> i <= 0)) {
          throw new IllegalArgumentException("Bulk data cannot contain negative or zero values");
      }

      List<Bulk> bulkList = new ArrayList<>();

      for (Integer ignored : bulkData) {
          Bulk blank = new Bulk();                 //  ➟ first save: DB assigns ID
          Bulk saved = bulkRepository.save(blank);

          saved.setQr("BK/HPNSK/" + saved.getId()); // ➟ add QR that includes the ID
          bulkList.add(bulkRepository.save(saved)); // ➟ second save: persist QR
      }

      return bulkList;
  }
  
  public List<Bulk> getAllBulkDetails() {
    return this.bulkRepository.findAll();
  }
  
  public Optional<Bulk> getBulkById(Long id) {
    return this.bulkRepository.findById(id);
  }
  
//  public Bulk addBulk(Bulk bulk) {
//    Bulk existingBulk = this.bulkRepository.findByUniqueId(bulk.getUniqueId());
//    if (existingBulk != null)
//      throw new IllegalArgumentException("An Bulk with the same Unique ID already exists"); 
//    return (Bulk)this.bulkRepository.save(bulk);
//  }
// 
  
  public Bulk addBulk(Bulk bulk) {

      Bulk existing = bulkRepository.findByUniqueId(bulk.getUniqueId());
      if (existing != null) {
          throw new IllegalArgumentException("A Bulk with the same Unique ID already exists");
      }

      Bulk saved = bulkRepository.save(bulk);              // first save -> get ID
      saved.setQr("BK/HPNSK/" + saved.getId());            // build QR with ID
      return bulkRepository.save(saved);                   // second save -> persist QR
  }
  
  public Bulk updateBulk(Long id, Bulk updatedBulk) {
    Optional<Bulk> existingBulk = this.bulkRepository.findById(id);
    if (existingBulk.isPresent()) {
      Bulk bulkToUpdate = existingBulk.get();
      Bulk existingBulkWithNewUniqueId = this.bulkRepository.findByUniqueId(updatedBulk.getUniqueId());
      if (existingBulkWithNewUniqueId != null && !existingBulkWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("An Bulk with the same UniqueId already exists"); 
      bulkToUpdate.setUniqueId(updatedBulk.getUniqueId());
      bulkToUpdate.setFullName(updatedBulk.getFullName());
      bulkToUpdate.setMobileNumber(updatedBulk.getMobileNumber());
      bulkToUpdate.setAddress(updatedBulk.getAddress());
      bulkToUpdate.setFirmName(updatedBulk.getFirmName());
      bulkToUpdate.setTruckNumber(updatedBulk.getTruckNumber());
      return (Bulk)this.bulkRepository.save(bulkToUpdate);
    } 
    throw new IllegalArgumentException("Bulk not found");
  }
  
  public Bulk deleteBulkDetails(Long id) {
    Optional<Bulk> existingBulk = this.bulkRepository.findById(id);
    if (existingBulk.isPresent()) {
      Bulk bulkToUpdate = existingBulk.get();
      bulkToUpdate.setUniqueId(null);
      bulkToUpdate.setFullName(null);
      bulkToUpdate.setMobileNumber(null);
      bulkToUpdate.setAddress(null);
      bulkToUpdate.setFirmName(null);
      return (Bulk)this.bulkRepository.save(bulkToUpdate);
    } 
    throw new IllegalArgumentException("Bulk not found");
  }
  
  public String processAndSaveDetails(Long bulkId) {
      if (this.bulkRepository == null || this.inscanService == null) {
          throw new IllegalStateException("Bulk repository or Inscan service not initialized");
      }
      
      Optional<Bulk> optionalBulk = this.bulkRepository.findById(bulkId);
      if (optionalBulk.isEmpty()) {
          throw new IllegalArgumentException("Bulk not found");
      }
      
      Bulk bulk = optionalBulk.get();
      
      // Ensure QR code is set correctly
      String expectedQr = "BK/HPNSK/" + bulk.getId();
      if (bulk.getQr() == null || !bulk.getQr().equals(expectedQr)) {
          bulk.setQr(expectedQr);
          this.bulkRepository.save(bulk);
      }
      
      // Prepare details for processing
      String department = "Driver";
      String sub_department = "BK";
      String details = bulk.getQr(); // Use the QR code as details
      Long ofcid = bulk.getId();
      String name = bulk.getFullName();
      String uniqueId = bulk.getUniqueId();
      String mobile = bulk.getMobileNumber();
      String address = bulk.getAddress();
      String contractor = null;
      String firmName = null;
      
      // Check existing Inscan record
      Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
      if (inscan == null) {
          this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, "N", contractor, firmName);
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
  
  
  public Bulk getDetailsByUniqueId(String uniqueId) {
    return this.bulkRepository.findByUniqueId(uniqueId);
  }
  
  public String getFullName(Long entityId) {
    Bulk bulk = this.bulkRepository.findById(entityId).orElse(null);
    if (bulk != null) {
      String fullName = bulk.getFullName();
      return fullName;
    } 
    return "Unknown Bulk";
  }
  
  public String processAndSaveLicenseGateDetails(Long bulkId) {
    if (this.bulkRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Bulk repository or LicenseGateService service not initialized"); 
    Optional<Bulk> optionalBulk = this.bulkRepository.findById(bulkId);
    if (optionalBulk.isPresent()) {
      Bulk bulk = optionalBulk.get();
      StringBuilder detailsBuilder = (new StringBuilder("BK/HPNSK/")).append(bulk.getId());
      String department = "Operation";
      String sub_department = "BK";
      String details = detailsBuilder.toString();
      Long ofcId = bulk.getId();
      String name = bulk.getFullName();
      String uniqueId = bulk.getUniqueId();
      String mobile = bulk.getMobileNumber();
      String address = bulk.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, bulkId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, bulkId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Bulk not found");
  }
}
