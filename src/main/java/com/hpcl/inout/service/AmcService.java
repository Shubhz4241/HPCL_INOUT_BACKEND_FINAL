package com.hpcl.inout.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Amc;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.repository.AmcRepository;


@Service
public class AmcService {
	@Autowired
	private AmcRepository amcRepository;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private LicenseGateService licenseGateService;

	public List<Amc> addAmc(List<Integer> amcData) {
		List<Amc> amc = amcData.stream().map(intValue -> new Amc()).toList();
		return this.amcRepository.saveAll(amc);
	}

	public List<Amc> getAmcDetails() {
		return this.amcRepository.findAll();
	}

	public Optional<Amc> getAmcById(Long id) {
		return this.amcRepository.findById(id);
	}

	public Amc addAmc(Amc amc) {
		Amc existingAmc = this.amcRepository.findByUniqueId(amc.getUniqueId());
		if (existingAmc != null)
			throw new IllegalArgumentException("An Amc with the same Unique ID already exists"); 
		return this.amcRepository.save(amc);
	}

	public Amc updateAmc(Long id, Amc updatedAmc) {
		Optional<Amc> existingAmc = this.amcRepository.findById(id);
		if (existingAmc.isPresent()) {
			Amc amcToUpdate = existingAmc.get();
			Amc existingAmcWithNewUniqueId = this.amcRepository.findByUniqueId(updatedAmc.getUniqueId());
			if (existingAmcWithNewUniqueId != null && !existingAmcWithNewUniqueId.getId().equals(id))
				throw new IllegalArgumentException("An AMC with the same Unique ID already exists");
			amcToUpdate.setUniqueId(updatedAmc.getUniqueId());
			amcToUpdate.setFullName(updatedAmc.getFullName());
			amcToUpdate.setMobileNumber(updatedAmc.getMobileNumber());
			amcToUpdate.setAddress(updatedAmc.getAddress());
			amcToUpdate.setFirmName(updatedAmc.getFirmName());
			return this.amcRepository.save(amcToUpdate);
		}
		throw new IllegalArgumentException("AMC not found");
	}

	public Amc deleteAmcDetails(Long id) {
		Optional<Amc> existingAmc = this.amcRepository.findById(id);
		if (existingAmc.isPresent()) {
			Amc amcToUpdate = existingAmc.get();
			amcToUpdate.setUniqueId(null);
			amcToUpdate.setFullName(null);
			amcToUpdate.setMobileNumber(null);
			amcToUpdate.setAddress(null);
			amcToUpdate.setFirmName(null);
			return this.amcRepository.save(amcToUpdate);
		} 
		throw new IllegalArgumentException("AMC not found");
	}

	public String processAndSaveDetails(Long amcId) {
		if (this.amcRepository == null || this.inscanService == null)
			throw new IllegalStateException("AMC repository or Inscan service not initialized"); 
		Optional<Amc> optionalAmc = this.amcRepository.findById(amcId);
		if (optionalAmc.isPresent()) {
			Amc amc = optionalAmc.get();
			StringBuilder detailsBuilder = new StringBuilder("AMC/HPNSK/").append(amc.getId());
			String department = "Project";
			String sub_department = "AMC";
			String details = "AMC/HPNSK/";
			Long ofcid = amc.getId();
			String name = amc.getFullName();
			String uniqueId = amc.getUniqueId();
			String mobile = amc.getMobileNumber();
			String address = amc.getAddress();
			String contractor = null;
			String firmName = null;
			Inscan inscan = this.inscanService.findByUniqueId(uniqueId);
			if (inscan == null) {
//				String str = "Y";
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
		throw new IllegalArgumentException("AMC not found");
	}

	public Amc getDetailsByUniqueId(String uniqueId) {
		return this.amcRepository.findByUniqueId(uniqueId);
	}

	public String getFullName(Long entityId) {
		Amc amc = this.amcRepository.findById(entityId).orElse(null);
		if (amc != null) {
			return amc.getFullName();
		} 
		return "Unknown Amc";
	}

	public String processAndSaveLicenseGateDetails(Long amcId) {
		if (this.amcRepository == null || this.licenseGateService == null)
			throw new IllegalStateException("AMC repository or LicenseGateService not initialized"); 
		Optional<Amc> optionalAMC = this.amcRepository.findById(amcId);
		if (optionalAMC.isPresent()) {
			Amc amc = optionalAMC.get();
			StringBuilder detailsBuilder = new StringBuilder("AMC/HPNSK/").append(amc.getId());
			String department = "Operation";
			String sub_department = "AMC";
			String details = detailsBuilder.toString();
			Long ofcId = amc.getId();
			String name = amc.getFullName();
			String uniqueId = amc.getUniqueId();
			String mobile = amc.getMobileNumber();
			String address = amc.getAddress();
			String contractor = null;
			String firmName = null;
			Licensegate licenseGate = this.licenseGateService.findByUniqueId(uniqueId);
			if (licenseGate == null || licenseGate.getExitDateTime() != null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, uniqueId, mobile, address, amcId, department, sub_department, contractor, firmName);
				return "Scan In";
			}
			this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
			return "Scan Out";
		} 
		throw new IllegalArgumentException("AMC not found");
	}
}

