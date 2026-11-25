package com.upo.engine;

import com.upo.compatibility.CompatibilityInfo;

public interface OptimizationModule {
    String id();
    /** Return false (with a logged reason) if the module cannot be applied safely. */
    boolean start(CompatibilityInfo compat) throws Exception;
    void stop();
}
