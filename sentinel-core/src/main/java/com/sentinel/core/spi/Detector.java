package com.sentinel.core.spi;

import com.sentinel.core.model.ThreatCategory;

public sealed interface Detector permits EventBasedDetector, WindowBasedDetector {
    public String name();

    public ThreatCategory category();
}
