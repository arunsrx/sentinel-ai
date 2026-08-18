package com.sentinel.core.spi;

import com.sentinel.core.model.DetectorResult;
import com.sentinel.core.port.StateStore;

public non-sealed interface WindowBasedDetector extends Detector {
    DetectorResult analyzeWindow(String agentId, StateStore eventsWindow);
}
