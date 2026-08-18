package com.sentinel.core.spi;

import com.sentinel.core.model.AgentEvent;
import com.sentinel.core.model.DetectorResult;

public non-sealed interface EventBasedDetector extends Detector {
    DetectorResult analyze(AgentEvent event);
}
