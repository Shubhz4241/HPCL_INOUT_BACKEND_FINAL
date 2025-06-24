package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.Mathadi;
import com.hpcl.inout.repository.MathadiRepository;

@Service
public class MathadiService {
	

	  @Autowired
	  private MathadiRepository mathadiRepository;
	  
	  @Autowired
	  private InscanService inscanService;
	  
	  @Autowired
	  private LicenseGateService licenseGateService;
	  
	  public MathadiService(MathadiRepository mathadiRepository) {
	    this.mathadiRepository = mathadiRepository;
	  }
	  
	  public List<Mathadi> addMathadi(List<Integer> mathadiData) {
		    List<Mathadi> mathadi = mathadiData.stream().map(intValue -> new Mathadi()).toList();
		    return this.mathadiRepository.saveAll(mathadi);
		  }
		  
		  public List<Mathadi> getAllMathadiDetails() {
		    return this.mathadiRepository.findAll();
		  }
		  
		  public Optional<Mathadi> getMathadiById(Long id) {
		    return this.mathadiRepository.findById(id);
		  }
		  
		  public Mathadi addMathadi(Mathadi mathadi) {
			  Mathadi existingMathadi = this.mathadiRepository.findByUniqueId(mathadi.getUniqueId());
		    if (existingMathadi != null)
		      throw new IllegalArgumentException("An Mathadi with the same UniqueId already exists"); 
		    return (Mathadi)this.mathadiRepository.save(mathadi);
		  }
		   
	  public Mathadi updateMathadi(Long id, Mathadi updatedMathadi) {
		  System.out.println("Mmmmmmmmmmmmmmm");
	    Optional<Mathadi> existingMathadi = this.mathadiRepository.findById(id);
	    if (existingMathadi.isPresent()) {
	    	Mathadi mathadiToUpdate = existingMathadi.get();
	    	Mathadi existingMathadiWithNewUniqueId = this.mathadiRepository.findByUniqueId(updatedMathadi.getUniqueId());
	      if (existingMathadiWithNewUniqueId != null && !existingMathadiWithNewUniqueId.getId().equals(id))
	        throw new IllegalArgumentException("An Transportor with the same UniqueId already exists"); 
	      mathadiToUpdate.setUniqueId(updatedMathadi.getUniqueId());
	      mathadiToUpdate.setFullName(updatedMathadi.getFullName());
	      mathadiToUpdate.setMobileNumber(updatedMathadi.getMobileNumber());
	      mathadiToUpdate.setAddress(updatedMathadi.getAddress());
	      mathadiToUpdate.setFirmName(updatedMathadi.getFirmName());
	      mathadiToUpdate.setContractor(updatedMathadi.getContractor());
	      mathadiToUpdate.setEnumber (updatedMathadi.getEnumber());

	      return (Mathadi)this.mathadiRepository.save(mathadiToUpdate);
	    } 
	    throw new IllegalArgumentException("Mathadi not found");
	  }
	  
	  public Mathadi deleteMathadiDetails(Long id) {
	    Optional<Mathadi> existingMathadi = this.mathadiRepository.findById(id);
	    if (existingMathadi.isPresent()) {
	    	Mathadi mathadiToUpdate = existingMathadi.get();
	    	mathadiToUpdate.setUniqueId(null);
	    	mathadiToUpdate.setFullName(null);
	    	mathadiToUpdate.setMobileNumber(null);
	    	mathadiToUpdate.setAddress(null);
	    	mathadiToUpdate.setContractor(null);
	    	mathadiToUpdate.setEnumber(null);
	    	mathadiToUpdate.setFirmName(null);
	    	return (Mathadi)this.mathadiRepository.save(mathadiToUpdate);
	    } 
	    throw new IllegalArgumentException("Mathadi not found");
	  }
	  
	  
	  public String processAndSaveDetails(Long mathadiId) {
	    if (this.mathadiRepository == null || this.inscanService == null)
	      throw new IllegalStateException("Mathadi repository or Inscan service not initialized"); 
	    Optional<Mathadi> optionalMathadi = this.mathadiRepository.findById(mathadiId);
	    if (optionalMathadi.isPresent()) {
	    	Mathadi mathadi = optionalMathadi.get();
	      StringBuilder detailsBuilder = (new StringBuilder("MT/HPNSK/")).append(mathadi.getId());
	      String department = "Operation";
	      String sub_department = "MT";
	      String details = "MT/HPNSK/";
	      Long ofcid = mathadi.getId();
	      String name = mathadi.getFullName();
	      String uniqueId = mathadi.getUniqueId();
	      String mobile = mathadi.getMobileNumber();
	      String address = mathadi.getAddress();
	      String firmName=mathadi.getFirmName();
	      String contractor= mathadi.getContractor();
	    
	      
	      Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
	      if (inscan == null) {
//	        String str = "Y";
	    	  String str = "N";
	        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, str,firmName,contractor);
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
	        this.inscanService.saveDetailsToInscan(details, name, uniqueId, mobile, address, ofcid, department, sub_department, mainGateStatus,firmName,contractor);
	        return "Scan In";
	      } 
	      this.inscanService.updateDetailsToInscan(inscan);
	      return "Scan Out";
	    } 
	    return "Mathadi not found";
	  }
	  
	  public Mathadi getDetailsByUniqueId(String uniqueId) {
	    return this.mathadiRepository.findByUniqueId(uniqueId);
	  }
	  
	  public Mathadi getOperatorByUniqueId(String uniqueId) {
	    return this.mathadiRepository.findByUniqueId(uniqueId);
	  }
	  
	  public String getFullName(Long entityId) {
		  Mathadi mathadi = this.mathadiRepository.findById(entityId).orElse(null);
	    if (mathadi != null) {
	      String fullName = mathadi.getFullName();
	      return fullName;
	    } 
	    return "Unknown Mathadi";
	  }
	  
	  public String processAndSaveLicenseGateDetails(Long mathadiId) {
	    if (this.mathadiRepository == null || this.licenseGateService == null)
	      throw new IllegalStateException("Mathadi repository or LicenseGateService service not initialized"); 
	    Optional<Mathadi> optionalMathadi = this.mathadiRepository.findById(mathadiId);
	    if (optionalMathadi.isPresent()) {
	    	Mathadi mathadi = optionalMathadi.get();
	      StringBuilder detailsBuilder = (new StringBuilder("MT/HPNSK/")).append(mathadi.getId());
	      String department = "Operation";
	      String sub_department = "MT";
	      String details = detailsBuilder.toString();
	      Long ofcid = mathadi.getId();
	      String name = mathadi.getFullName();
	      String uniqueId = mathadi.getUniqueId();
	      String mobile = mathadi.getMobileNumber();
	      String address = mathadi.getAddress();
	      String firmName=mathadi.getFirmName();
	      String contractor= mathadi.getContractor();
	      Licensegate licensegate = this.licenseGateService.findByUniqueId(uniqueId);
	      if (licensegate == null) {
	        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, ofcid, department, sub_department,contractor,firmName);
	        return "IN";
	      } 
	      if (licensegate.getExitDateTime() != null) {
	        this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, ofcid, department, sub_department,contractor,firmName);
	        return "Scan In";
	      } 
	      this.licenseGateService.updateDetailsToLicenseGate(licensegate);
	      return "Scan Out";
	    } 
	    throw new IllegalArgumentException("Mathadi not found");
	  }
}



