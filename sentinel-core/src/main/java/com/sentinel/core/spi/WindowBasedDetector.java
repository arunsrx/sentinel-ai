package com.sentinel.core.spi;

import java.util.List;

import com.sentinel.core.model.AgentEvent;
import com.sentinel.core.model.DetectorResult;

public non-sealed interface WindowBasedDetector extends Detector {
    DetectorResult analyzeWindow(String agentId, List<AgentEvent> eventsWindow);
}
