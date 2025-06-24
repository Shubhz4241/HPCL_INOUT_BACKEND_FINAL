package com.hpcl.inout.Handler;

import com.hpcl.inout.dto.QrRequest;

public interface ScanHandler {
    String getType(); // e.g. "PT", "BK", "TP"
    String handle(QrRequest qrId);

}
