package com.upo.intelligence;

import com.upo.core.UPOLog;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

/**
 * Classifies memory pressure from two reliable JVM signals:
 *  - live ratio: heap pool occupancy measured right AFTER the last collection (MemoryPoolMXBean.getCollectionUsage),
 *    which is far more meaningful than instantaneous heap usage;
 *  - GC time share: fraction of the sampling interval spent in garbage collection.
 * Escalation is immediate; de-escalation requires several consecutive calmer samples (hysteresis).
 * Consumed later by the Dynamic Performance Controller. It does not call System.gc() or touch game data.
 */
public final class MemoryPressureAnalyzer {
    public enum Level { NORMAL, ELEVATED, HIGH }

    private static final double LIVE_ELEVATED = 0.70, LIVE_HIGH = 0.85;
    private static final double GC_ELEVATED = 0.04, GC_HIGH = 0.10;
    private static final int CALM_SAMPLES_TO_DROP = 5;

    private final List<MemoryPoolMXBean> pools = new ArrayList<MemoryPoolMXBean>();
    private Level level = Level.NORMAL;
    private int calm;
    private double lastLive, lastGcShare;

    public MemoryPressureAnalyzer() {
        for (MemoryPoolMXBean p : ManagementFactory.getMemoryPoolMXBeans()) {
            if (p.getType() == MemoryType.HEAP && p.isValid()) pools.add(p);
        }
    }

    private double liveRatio() {
        double best = 0;
        for (MemoryPoolMXBean p : pools) {
            MemoryUsage u = p.getCollectionUsage(); // null if the pool is not collected by a GC
            if (u == null || u.getMax() <= 0) continue;
            best = Math.max(best, (double) u.getUsed() / u.getMax());
        }
        return best;
    }

    public Level update(long gcTimeMsDelta, long intervalMs) {
        lastGcShare = intervalMs > 0 ? gcTimeMsDelta / (double) intervalMs : 0;
        lastLive = liveRatio();
        Level target = (lastLive > LIVE_HIGH || lastGcShare > GC_HIGH) ? Level.HIGH
                : (lastLive > LIVE_ELEVATED || lastGcShare > GC_ELEVATED) ? Level.ELEVATED : Level.NORMAL;

        if (target.ordinal() > level.ordinal()) {
            change(target);
        } else if (target.ordinal() < level.ordinal()) {
            if (++calm >= CALM_SAMPLES_TO_DROP) change(target);
        } else {
            calm = 0;
        }
        return level;
    }

    private void change(Level to) {
        UPOLog.info(String.format("Memory pressure %s -> %s (live after GC %.0f%%, GC time share %.1f%%)",
                level, to, lastLive * 100, lastGcShare * 100));
        level = to;
        calm = 0;
    }

    public Level level() { return level; }
}
