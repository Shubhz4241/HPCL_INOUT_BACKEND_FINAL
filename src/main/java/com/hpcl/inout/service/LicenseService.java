package com.hpcl.inout.service;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.License;
import com.hpcl.inout.entity.User;
import com.hpcl.inout.repository.LicenseRepository;
import com.hpcl.inout.repository.UserRepository;

@Service
public class LicenseService {
  @Autowired
  private LicenseRepository licenseRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  public String validateLicense(String licensekey) {
    License license = this.licenseRepository.findBylicensekey(licensekey);
    if (license != null) {
      if (license.getExpirydate().equals(LocalDate.now()) || license.getExpirydate().isBefore(LocalDate.now()))
        return "Expired"; 
      if (!license.getExpirydate().isBefore(LocalDate.now())) {
        User admin = (User)this.userRepository.getReferenceById(Integer.valueOf(1));
        if (admin.getLicense() != null) {
          admin.setLicense(null);
          license.setUser(null);
          this.userRepository.save(admin);
          this.licenseRepository.save(license);
        } 
        admin.setLicense(license);
        license.setUser(admin);
        license.setStartdate(LocalDate.now());
        this.userRepository.save(admin);
        this.licenseRepository.save(license);
        return "Validated";
      } 
    } else {
      return null;
    } 
    return "";
  }
}
