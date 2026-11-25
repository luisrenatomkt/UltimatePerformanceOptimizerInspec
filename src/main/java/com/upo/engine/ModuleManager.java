package com.upo.engine;

import com.upo.compatibility.CompatibilityInfo;
import com.upo.config.UPOConfig;
import com.upo.core.UPOLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Starts modules in isolation: a failing module is disabled, others keep running. */
public final class ModuleManager {
    private final List<OptimizationModule> registered = new ArrayList<OptimizationModule>();
    private final List<OptimizationModule> active = new ArrayList<OptimizationModule>();

    public void register(OptimizationModule m) { registered.add(m); }

    public void initAll(CompatibilityInfo compat) {
        if (UPOConfig.safeMode || !UPOConfig.enableOptimizations) {
            UPOLog.info("Optimizations not started (safeMode or disabled in config).");
            return;
        }
        for (OptimizationModule m : registered) {
            try {
                if (m.start(compat)) { active.add(m); UPOLog.info("Module started: " + m.id()); }
                else UPOLog.warn("Module not applicable, skipped: " + m.id());
            } catch (Throwable t) {
                UPOLog.error("Module failed and was disabled: " + m.id(), t);
                try { m.stop(); } catch (Throwable ignored) { }
            }
        }
    }

    public List<OptimizationModule> active() { return Collections.unmodifiableList(active); }
}
