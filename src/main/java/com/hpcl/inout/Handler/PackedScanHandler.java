package com.hpcl.inout.Handler;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.dto.QrRequest;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.Packed;
import com.hpcl.inout.entity.drivergate;
import com.hpcl.inout.repository.DrivergateRepo;
import com.hpcl.inout.repository.InscanRepository;
import com.hpcl.inout.repository.LicensegateRepository;
import com.hpcl.inout.repository.PackedRepository;



@Service
public class PackedScanHandler implements ScanHandler {
	@Autowired
	private DrivergateRepo drivergateRepo;
	
	@Autowired
	private PackedRepository packedRepository;
	
	@Autowired
	private InscanRepository inscanRepository;
	
	@Autowired
	private LicensegateRepository licensegateRepository;

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "PT";
	}

	@Override
	public String handle(QrRequest qrRequest) {
		
		Packed packed = packedRepository.findTopByQrOrderByIdDesc(qrRequest.getQr());
        System.out.println("The driver Is "+qrRequest.getQr());
		if (packed == null) {
			return "Driver Not Found"; 
		}

		drivergate lastEntry = drivergateRepo.findTopByQrOrderByIdDesc(qrRequest.getQr());
		Inscan lastEntryInscan = inscanRepository.findTopByDetailsOrderByIdDesc(qrRequest.getQr());
		Licensegate lastEntryInLicense = licensegateRepository.findTopByDetailsOrderByIdDesc(qrRequest.getQr());
        
		if (lastEntry == null || lastEntry.getStatus() == null || lastEntry.getStatus().equalsIgnoreCase("Out")) {
			drivergate inGate = new drivergate();
			inGate.setName(packed.getFullName());
			inGate.setAadharNumber(packed.getUniqueId());
			inGate.setAddress(packed.getAddress());
			inGate.setQr(packed.getQr());
			inGate.setEntryDateTime(LocalDateTime.now());
			inGate.setDepartment("PT");
			inGate.setMobile(packed.getMobileNumber());
			inGate.setFirmName(packed.getFirmName());
			inGate.setTruckNumber(packed.getTruckNumber());
			inGate.setStatus("In");
            
			Inscan inscan = new Inscan();
			inscan.setName(packed.getFullName());
			inscan.setUniqueId(packed.getUniqueId());
			inscan.setAddress(packed.getAddress());
			inscan.setDetails(packed.getQr());
			inscan.setEntryDateTime(new Date());
			inscan.setDepartment("Driver");
			inscan.setSub_department("PT");
			inscan.setMobile(packed.getMobileNumber());
			inscan.setFirmName(packed.getFirmName());
			inscan.setMainGateSatus("N");
			inscan.setOfcid(packed.getId());
			
			inscanRepository.save(inscan);
			drivergateRepo.save(inGate);
            
			Licensegate licensegate = new Licensegate();
			licensegate.setName(packed.getFullName());
			licensegate.setUniqueId(packed.getUniqueId());
			licensegate.setAddress(packed.getAddress());
			licensegate.setDetails(packed.getQr());
			licensegate.setEntryDateTime(new Date());
			licensegate.setDepartment("Driver");
			licensegate.setFirmName(packed.getFirmName());
			licensegate.setSub_department("PT");
			
			licensegateRepository.save(licensegate);
                 
			return "Scanned In (DriverGate)";
		}

//		// Fixed null pointer check and condition logic
//		if (lastEntry.getStatus().equalsIgnoreCase("In") || 
//			(lastEntryInscan != null && "Y".equalsIgnoreCase(lastEntryInscan.getMainGateSatus())) || 
//			(lastEntryInLicense != null && lastEntryInLicense.getExitDateTime() == null)) {
//			
//			lastEntry.setExitDateTime(LocalDateTime.now());
//			lastEntry.setStatus("Out");
//			drivergateRepo.save(lastEntry);
//            
//			if (lastEntryInscan != null) {
//				lastEntryInscan.setExitDateTime(new Date());
//				lastEntryInscan.setMainGateSatus("Y");
//				inscanRepository.save(lastEntryInscan);
//			}
//            
//			if (lastEntryInLicense != null) {
//				lastEntryInLicense.setExitDateTime(new Date());
//				licensegateRepository.save(lastEntryInLicense);
//			}
//            
//			return "Scanned Out";
//		}
		
		if (lastEntry.getStatus().equalsIgnoreCase("In")) {

		    // Check License Gate is still In
		    boolean isInLicense = lastEntryInLicense != null && lastEntryInLicense.getExitDateTime() == null;

		    // Check Main Gate is still In
		    boolean isInMainGate = lastEntryInscan != null && "N".equalsIgnoreCase(lastEntryInscan.getMainGateSatus());

		    if (isInLicense && isInMainGate) {
		        lastEntry.setExitDateTime(LocalDateTime.now());
		        lastEntry.setStatus("Out");
		        drivergateRepo.save(lastEntry);

		        lastEntryInscan.setExitDateTime(new Date());
		        lastEntryInscan.setMainGateSatus("Y");
		        inscanRepository.save(lastEntryInscan);

		        lastEntryInLicense.setExitDateTime(new Date());
		        licensegateRepository.save(lastEntryInLicense);

		        return "Scanned Out (Packed)";
		    } else {
		        return "Cannot Scan Out:Already In Plant";
		    }
		}


		return "Invalid scan state";
	}

}
