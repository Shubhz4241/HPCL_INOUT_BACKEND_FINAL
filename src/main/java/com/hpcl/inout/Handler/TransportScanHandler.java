package com.hpcl.inout.Handler;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.dto.QrRequest;
import com.hpcl.inout.entity.Inscan;
import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.entity.Transportor;
import com.hpcl.inout.entity.drivergate;
import com.hpcl.inout.repository.DrivergateRepo;
import com.hpcl.inout.repository.InscanRepository;
import com.hpcl.inout.repository.LicensegateRepository;
import com.hpcl.inout.repository.TransportorRepository;


@Service
public class TransportScanHandler implements ScanHandler{
    @Autowired
    private DrivergateRepo drivergateRepo;

    @Autowired
    private TransportorRepository transporterRepository;

    @Autowired
    private InscanRepository inscanRepository;

    @Autowired
    private LicensegateRepository licensegateRepository;

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "TR";
	}

	@Override
	public String handle(QrRequest qrRequest) {
		 String qrId = qrRequest.getQr().split("/")[2];
         Transportor transport = transporterRepository.findTopByQrOrderByIdDesc(qrRequest.getQr());

        if (transport == null) {
            return "Transport Driver Not Found";
        }

        drivergate lastEntry = drivergateRepo.findTopByQrOrderByIdDesc(qrRequest.getQr());
        Inscan lastEntryInscan = inscanRepository.findTopByDetailsOrderByIdDesc(qrRequest.getQr());
        Licensegate lastEntryInLicense = licensegateRepository.findTopByDetailsOrderByIdDesc(qrRequest.getQr());

        if (lastEntry == null || lastEntry.getStatus() == null || lastEntry.getStatus().equalsIgnoreCase("Out")) {
            drivergate inGate = new drivergate();
            inGate.setName(transport.getFullName());
            inGate.setAadharNumber(transport.getUniqueId());
            inGate.setAddress(transport.getAddress());
            inGate.setQr(transport.getQr());
            inGate.setEntryDateTime(LocalDateTime.now());
            inGate.setDepartment("TR");
            inGate.setMobile(transport.getMobileNumber());
//            inGate.setFirmName(transport.getFirmName());
            inGate.setTruckNumber(transport.getTruckNumber());
            inGate.setStatus("In");

            Inscan inscan = new Inscan();
            inscan.setName(transport.getFullName());
            inscan.setUniqueId(transport.getUniqueId());
            inscan.setAddress(transport.getAddress());
            inscan.setDetails(transport.getQr());
            inscan.setEntryDateTime(new Date());
            inscan.setDepartment("Driver");
            inscan.setSub_department("TR");
            inscan.setMobile(transport.getMobileNumber());
            inscan.setMainGateSatus("N");
            inscan.setOfcid(transport.getId());

            Licensegate licensegate = new Licensegate();
            licensegate.setName(transport.getFullName());
            licensegate.setUniqueId(transport.getUniqueId());
            licensegate.setAddress(transport.getAddress());
            licensegate.setDetails(transport.getQr());
            licensegate.setEntryDateTime(new Date());
            licensegate.setDepartment("Driver");
//            licensegate.setFirmName(transport.getFirmName());
            licensegate.setSub_department("TR");

            inscanRepository.save(inscan);
            
            drivergateRepo.save(inGate);
            
            licensegateRepository.save(licensegate);

            return "Scanned In (Transport)";
        }
//
//        if (lastEntry.getStatus().equalsIgnoreCase("In")
//                || lastEntryInscan.getMainGateSatus().equalsIgnoreCase("N")
//                || lastEntryInLicense.getExitDateTime() == null) {
//
//            lastEntry.setExitDateTime(LocalDateTime.now());
//            lastEntry.setStatus("Out");
//            drivergateRepo.save(lastEntry);
//
//            lastEntryInscan.setExitDateTime(new Date());
//            lastEntryInscan.setMainGateSatus("Y");
//            inscanRepository.save(lastEntryInscan);
//
//            lastEntryInLicense.setExitDateTime(new Date());
//            licensegateRepository.save(lastEntryInLicense);
//
//            return "Scanned Out";
//        }

        if (lastEntry.getStatus().equalsIgnoreCase("In")) {

            boolean isInLicense = lastEntryInLicense != null && lastEntryInLicense.getExitDateTime() == null;
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

                return "Scanned Out (Transport)";
            } else {
                return "Cannot Scan Out: Already In Plant";
            }
        }

        
        return "Invalid scan state";
    }

}
