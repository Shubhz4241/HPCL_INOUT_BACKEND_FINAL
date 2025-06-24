package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.*;
import com.hpcl.inout.repository.WorkmanRepository;


@Service
public class WorkmanService {

  @Autowired
  private WorkmanRepository workmanRepository;

  @Autowired
  private InscanService inscanService;

  @Autowired
  private LicenseGateService licenseGateService;

  public List<Workman> addWorkman(List<Integer> workmanData) {
    List<Workman> workmans = workmanData.stream().map(intValue -> new Workman()).toList();
    return this.workmanRepository.saveAll(workmans);
  }

  public List<Workman> getAllWorkmanDetails() {
    return this.workmanRepository.findAll();
  }

  public Optional<Workman> getWorkmanById(Long id) {
    return this.workmanRepository.findById(id);
  }

  public Workman addWorkman(Workman workman) {
    Workman existingWorkman = this.workmanRepository.findByUniqueId(workman.getUniqueId());
    if (existingWorkman != null)
      throw new IllegalArgumentException("A Workman with the same Unique ID already exists");
    return this.workmanRepository.save(workman);
  }

  public Workman updateWorkman(Long id, Workman updatedWorkman) {
    Optional<Workman> existingWorkman = this.workmanRepository.findById(id);
    if (existingWorkman.isPresent()) {
      Workman workmanToUpdate = existingWorkman.get();
      Workman existingWithSameUniqueId = this.workmanRepository.findByUniqueId(updatedWorkman.getUniqueId());
      if (existingWithSameUniqueId != null && !existingWithSameUniqueId.getId().equals(id))
        throw new IllegalArgumentException("A Workman with the same Unique ID already exists");

      workmanToUpdate.setUniqueId(updatedWorkman.getUniqueId());
      workmanToUpdate.setFullName(updatedWorkman.getFullName());
      workmanToUpdate.setMobileNumber(updatedWorkman.getMobileNumber());
      workmanToUpdate.setAddress(updatedWorkman.getAddress());
      workmanToUpdate.setFirmName(updatedWorkman.getFirmName());

      return this.workmanRepository.save(workmanToUpdate);
    }
    throw new IllegalArgumentException("Workman not found");
  }

  public Workman deleteWorkmanDetails(Long id) {
    Optional<Workman> existingWorkman = this.workmanRepository.findById(id);
    if (existingWorkman.isPresent()) {
      Workman workmanToUpdate = existingWorkman.get();
      workmanToUpdate.setUniqueId(null);
      workmanToUpdate.setFullName(null);
      workmanToUpdate.setMobileNumber(null);
      workmanToUpdate.setAddress(null);
      return this.workmanRepository.save(workmanToUpdate);
    }
    throw new IllegalArgumentException("Workman not found");
  }

  public String processAndSaveDetails(Long workmanId) {
    if (this.workmanRepository == null || this.inscanService == null)
      throw new IllegalStateException("Workman repository or Inscan service not initialized");

    Optional<Workman> optionalWorkman = this.workmanRepository.findById(workmanId);
    if (optionalWorkman.isPresent()) {
      Workman workman = optionalWorkman.get();

      String department = "Project";
      String sub_department = "PW";
      String details = "PW/HPNSK/";
      Long ofcid = workman.getId();
      String name = workman.getFullName();
      String uniqueId = workman.getUniqueId();
      String mobile = workman.getMobileNumber();
      String address = workman.getAddress();
      String contractor = null;
      String firmName = null;

      Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
      if (inscan == null) {
//        String str = "Y";
    	  String str = "N";
        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, str, contractor, firmName);
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
        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, mainGateStatus, contractor, firmName);
        return "Scan In";
      }

      this.inscanService.updateDetailsToInscan(inscan);
      return "Scan Out";
    }
    throw new IllegalArgumentException("Workman not found");
  }

  public Workman getDetailsByUniqueId(String uniqueId) {
    return this.workmanRepository.findByUniqueId(uniqueId);
  }

  public String getFullName(Long entityId) {
    Workman workman = this.workmanRepository.findById(entityId).orElse(null);
    if (workman != null) {
      return workman.getFullName();
    }
    return "Unknown Workman";
  }

  public String processAndSaveLicenseGateDetails(Long workmanId) {
    if (this.workmanRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Workman repository or LicenseGateService not initialized");

    Optional<Workman> optionalWorkman = this.workmanRepository.findById(workmanId);
    if (optionalWorkman.isPresent()) {
      Workman workman = optionalWorkman.get();

      String department = "Operation";
      String sub_department = "PW";
      String details = "PW/HPNSK/" + workman.getId();
      Long ofcId = workman.getId();
      String name = workman.getFullName();
      String uniqueId = workman.getUniqueId();
      String mobile = workman.getMobileNumber();
      String address = workman.getAddress();
      String contractor = null;
      String firmName = null;

      Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, workmanId, department, sub_department, contractor, firmName);
        return "In";
      }

      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, workmanId, department, sub_department, contractor, firmName);
        return "Scan In";
      }

      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    }
    throw new IllegalArgumentException("Workman not found");
  }
}
