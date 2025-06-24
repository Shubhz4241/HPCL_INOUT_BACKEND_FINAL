package com.hpcl.inout.Handler;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.dto.QrRequest;
import com.hpcl.inout.entity.Bulk;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.drivergate;
import com.hpcl.inout.repository.BulkRepository;
import com.hpcl.inout.repository.DrivergateRepo;
import com.hpcl.inout.repository.InscanRepository;
import com.hpcl.inout.repository.LicensegateRepository;


@Service
public class BulkScanHandler implements ScanHandler{


    @Autowired
    private DrivergateRepo drivergateRepo;

    @Autowired
    private BulkRepository bulkRepository;

    @Autowired
    private InscanRepository inscanRepository;

    @Autowired
    private LicensegateRepository licensegateRepository;

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		 return "BK";
	}

	@Override
	public String handle(QrRequest qrRequest) {
		 Bulk bulk = bulkRepository.findTopByQrOrderByIdDesc(qrRequest.getQr());
		 
		 System.out.println("Bulk Data"+bulk);

	        if (bulk == null) {
	            return "Driver Not Found (Bulk)";
	        }

	        drivergate lastEntry = drivergateRepo.findTopByQrOrderByIdDesc(qrRequest.getQr());
	        Inscan lastEntryInscan = inscanRepository.findTopByDetailsOrderByIdDesc(qrRequest.getQr());
	        Licensegate lastEntryInLicense = licensegateRepository.findTopByDetailsOrderByIdDesc(qrRequest.getQr());

	        if (lastEntry == null || lastEntry.getStatus() == null || lastEntry.getStatus().equalsIgnoreCase("Out")) {
	            drivergate inGate = new drivergate();
	            inGate.setName(bulk.getFullName());
	            inGate.setAadharNumber(bulk.getUniqueId());
	            inGate.setAddress(bulk.getAddress());
	            inGate.setQr(bulk.getQr());
	            inGate.setEntryDateTime(LocalDateTime.now());
	            inGate.setDepartment("BK");
	            inGate.setMobile(bulk.getMobileNumber());
	            inGate.setFirmName(bulk.getFirmName());
	            inGate.setTruckNumber(bulk.getTruckNumber());
	            inGate.setStatus("In");

	            Inscan inscan = new Inscan();
	            inscan.setName(bulk.getFullName());
	            inscan.setUniqueId(bulk.getUniqueId());
	            inscan.setAddress(bulk.getAddress());
	            inscan.setDetails(bulk.getQr());
	            inscan.setEntryDateTime(new Date());
	            inscan.setDepartment("Driver");
	            inscan.setSub_department("BK");
	            inscan.setMobile(bulk.getMobileNumber());
	            inscan.setFirmName(bulk.getFirmName());
	            inscan.setMainGateSatus("N");
	            inscan.setOfcid(bulk.getId());

	            inscanRepository.save(inscan);
	            drivergateRepo.save(inGate);

	            Licensegate licensegate = new Licensegate();
	            licensegate.setName(bulk.getFullName());
	            licensegate.setUniqueId(bulk.getUniqueId());
	            licensegate.setAddress(bulk.getAddress());
	            licensegate.setDetails(bulk.getQr());
	            licensegate.setEntryDateTime(new Date());
	            licensegate.setDepartment("Driver");
	            licensegate.setFirmName(bulk.getFirmName());
	            licensegate.setSub_department("BK");

	            licensegateRepository.save(licensegate);

	            return "Scanned In (Bulk - DriverGate)";
	        }

//	        if (lastEntry.getStatus().equalsIgnoreCase("In") ||
//	            (lastEntryInscan != null && "Y".equalsIgnoreCase(lastEntryInscan.getMainGateSatus())) ||
//	            (lastEntryInLicense != null && lastEntryInLicense.getExitDateTime() == null)) {
	//
//	            lastEntry.setExitDateTime(LocalDateTime.now());
//	            lastEntry.setStatus("Out");
//	            drivergateRepo.save(lastEntry);
	//
//	            if (lastEntryInscan != null) {
//	                lastEntryInscan.setExitDateTime(new Date());
//	                lastEntryInscan.setMainGateSatus("Y");
//	                inscanRepository.save(lastEntryInscan);
//	            }
	//
//	            if (lastEntryInLicense != null) {
//	                lastEntryInLicense.setExitDateTime(new Date());
//	                licensegateRepository.save(lastEntryInLicense);
//	            }
	//
//	            return "Scanned Out (Bulk)";
//	        }
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

	                return "Scanned Out (Bulk)";
	            } else {
	                return "Cannot Scan Out: Already In Plant";
	            }
	        }


	        return "Invalid scan state";
	    }

}
