package com.hpcl.inout.service;

import com.hpcl.inout.entity.Visitor;
import com.hpcl.inout.entity.VisitorTokenId;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.UniqueIdDetails;
import com.hpcl.inout.repository.UniqueIdDetailsRepository;
import com.hpcl.inout.repository.VisitorRepository;
import com.hpcl.inout.repository.VisitorTokenIdRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {
  
  @Autowired
  private VisitorRepository visitorRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  @Autowired
  private VisitorTokenIdRepository visitorTokenRepository;
  
  @Autowired
  private VisitorTokenIdService visitorTokenIdService;
  
  @Autowired
  private UniqueIdDetailsRepository uniqueIdDetailsRepository;
  
  @Autowired
  private UniqueIdDetailsService uniqueIdDetailsService;
  
  
  private static final String IMAGE_FOLDER_PATH = "src/main/resources/static/img/";

  public List<Visitor> addVisitor(List<Integer> visitorData) {
    List<Visitor> visitor = visitorData.stream().map(intValue -> new Visitor()).toList();
    return this.visitorRepository.saveAll(visitor);
  }

  public List<Visitor> getAllVisitorDetails() {
    return this.visitorRepository.findAll();
  }

  public Optional<Visitor> getVisitorById(Long id) {
    return this.visitorRepository.findById(id);
  }

  public long countVisitorsWithFullNameNotNull() {
    return this.visitorRepository.countVisitorsWithFullNameNotNull();
  }

  public Visitor addVisitor(Visitor visitor) {
    Visitor existingVisitor = this.visitorRepository.findByUniqueId(visitor.getUniqueId());
    if (existingVisitor != null)
      throw new IllegalArgumentException("A visitor with the same Unique ID already exists");
    return this.visitorRepository.save(visitor);
  }

  public Visitor updateVisitor(Long id, Visitor updatedVisitor) {
    Optional<Visitor> existingVisitor = this.visitorRepository.findById(id);
    if (existingVisitor.isPresent()) {
      Visitor visitorToUpdate = existingVisitor.get();
      Visitor existingVisitorWithNewUniqueId = this.visitorRepository.findByUniqueId(updatedVisitor.getUniqueId());
      if (existingVisitorWithNewUniqueId != null && !existingVisitorWithNewUniqueId.getId().equals(id))
        throw new IllegalArgumentException("A visitor with the same Unique ID already exists");

      visitorToUpdate.setUniqueId(updatedVisitor.getUniqueId());
      visitorToUpdate.setFullName(updatedVisitor.getFullName());
      visitorToUpdate.setMobileNumber(updatedVisitor.getMobileNumber());
      visitorToUpdate.setAddress(updatedVisitor.getAddress());
      visitorToUpdate.setWhom(updatedVisitor.getWhom());
      visitorToUpdate.setPurpose(updatedVisitor.getPurpose());
      visitorToUpdate.setImageName(updatedVisitor.getImageName());
      visitorToUpdate.setVisitDate(LocalDateTime.now());
      visitorToUpdate.setRegular(updatedVisitor.isRegular());

      return this.visitorRepository.save(visitorToUpdate);
    }
    throw new IllegalArgumentException("Visitor not found");
  }

  
  // Soft delete visitor details
  public Visitor deleteVisitorDetails(Long id) {
      Visitor visitorToUpdate = visitorRepository.findById(id)
              .orElseThrow(() -> new IllegalArgumentException("Visitor not found"));

      // Soft delete logic - nullify all details except ID
      visitorToUpdate.setUniqueId(null);
      visitorToUpdate.setFullName(null);
      visitorToUpdate.setMobileNumber(null);
      visitorToUpdate.setAddress(null);
      visitorToUpdate.setWhom(null);
      visitorToUpdate.setPurpose(null);
      visitorToUpdate.setImageName(null);
      visitorToUpdate.setVisitDate(null);
      visitorToUpdate.setRegular(false);

      return visitorRepository.save(visitorToUpdate); // Save the updated visitor data
  }




  public String processAndSaveDetails(Long visitorId) {
    Optional<Visitor> optionalVisitor = this.visitorRepository.findById(visitorId);
    if (optionalVisitor.isPresent()) {
      Visitor visitor = optionalVisitor.get();
      StringBuilder detailsBuilder = new StringBuilder("VS/HPNSK/").append(visitor.getId());
      String department = "Visitor";
      String sub_department = "VS";
      String details = "VS/HPNSK/";
      Long ofcid = visitor.getId();
      String name = visitor.getFullName();
      String uniqueId = visitor.getUniqueId();
      String mobile = visitor.getMobileNumber();
      String address = visitor.getAddress();
      String contractor = null;
      String firmName = null;

      Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
      if (inscan == null) {
        String mainGateStatus = "N";
        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, mainGateStatus, contractor, firmName);
        return "In";
      }

      String mainGateStatus = "N";
      Licensegate licensegate = this.licenseGateService.findByUniqueId(uniqueId);
      boolean isLicenseGateIn = (licensegate == null || licensegate.getExitDateTime() != null);

      if (!isLicenseGateIn)
        return "Please exit from License gate";

      if (inscan.getExitDateTime() != null) {
        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, mainGateStatus, contractor, firmName);
        return "Scan In";
      }

      this.inscanService.updateDetailsToInscan(inscan);
      return "Scan Out";
    }
    return "Visitor not found";
  }

  public Visitor getDetailsByUniqueId(String uniqueId) {
    return this.visitorRepository.findByUniqueId(uniqueId);
  }

  public String getFullName(Long entityId) {
    return this.visitorRepository.findById(entityId)
            .map(Visitor::getFullName)
            .orElse("Unknown Visitor");
  }

  public String processAndSaveLicenseGateDetails(Long visitorId) {
    Optional<Visitor> optionalVisitor = this.visitorRepository.findById(visitorId);
    if (optionalVisitor.isPresent()) {
      Visitor visitor = optionalVisitor.get();
      StringBuilder detailsBuilder = new StringBuilder("VS/HPNSK/").append(visitor.getId());
      String department = "Visitor";
      String sub_department = "VS";
      String details = detailsBuilder.toString();
      Long ofcid = visitor.getId();
      String name = visitor.getFullName();
      String uniqueId = visitor.getUniqueId();
      String mobile = visitor.getMobileNumber();
      String address = visitor.getAddress();
      String contractor = null;
      String firmName = null;
      
      Licensegate licensegate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licensegate == null || licensegate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, ofcid, department, sub_department, contractor, firmName);
        return "IN";
      }

      this.licenseGateService.updateDetailsToLicenseGate(licensegate);
      return "Scan Out";
    }
    throw new IllegalArgumentException("Visitor not found");
  }

  public void storeVisitorDetailsIntoToken(Visitor visitor) {
    VisitorTokenId visitorToken = new VisitorTokenId();
    visitorToken.setCurrSrNo(visitorTokenIdService.generateNextSrNo());
    visitorToken.setVisitorId(visitor.getId());
    visitorToken.setFullName(visitor.getFullName());
    visitorToken.setMobileNumber(visitor.getMobileNumber());
    visitorToken.setAddress(visitor.getAddress());
    visitorToken.setWhom(visitor.getWhom());
    visitorToken.setPurpose(visitor.getPurpose());
    visitorToken.setUniqueId(visitor.getUniqueId());
    visitorToken.setDate(LocalDate.now());
    visitorToken.setImageName(visitor.getImageName());
    this.visitorTokenRepository.save(visitorToken);
  }

  public boolean restrictUser(Long id) {
    return this.visitorRepository.findById(id).map(visitor -> {
      visitor.setRestricted(true);
      this.visitorRepository.save(visitor);
      return true;
    }).orElse(false);
  }

  public boolean unrestrictUser(Long visitorId) {
    return this.visitorRepository.findById(visitorId).map(visitor -> {
      visitor.setRestricted(false);
      this.visitorRepository.save(visitor);
      return true;
    }).orElse(false);
  }

  public boolean restrictornot(Long entityId) {
    Visitor visitor = this.visitorRepository.findById(entityId).orElse(null);
    return visitor != null && visitor.isRestricted();
  }
  
}