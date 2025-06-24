package com.hpcl.inout.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.Handler.ScanHandler;
import com.hpcl.inout.dto.QrRequest;



@Service
public class DrivergateService {

	private final Map<String, ScanHandler> handlerMap = new HashMap<>();

    @Autowired
    public DrivergateService(List<ScanHandler> handlers) {
        for (ScanHandler handler : handlers) {
            handlerMap.put(handler.getType(), handler);
        }
    }
    

    public String scanIn(QrRequest qrRequest) {
        String[] parts = qrRequest.getQr().split("/");
        
        if (parts.length != 3) return "Invalid QR Code";

        String type = parts[0];  
        String qrId = parts[2];

//        System.out.println("service Qr" + type);
        
        ScanHandler handler = handlerMap.get(type);
        if (handler == null) {
            return "No handler found for type: " + type;
        }

        return handler.handle(qrRequest);  

    }

}
