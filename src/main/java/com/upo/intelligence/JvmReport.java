package com.upo.intelligence;

import com.upo.core.UPOLog;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * Logs the JVM facts that most affect GC behaviour. A mod cannot change JVM flags after startup,
 * so this only reports and flags clear problems; it never claims an optimization.
 */
public final class JvmReport {
    private JvmReport() {}

    public static void log() {
        RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
        long maxMb = Runtime.getRuntime().maxMemory() >> 20;
        StringBuilder gcs = new StringBuilder();
        for (GarbageCollectorMXBean b : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (gcs.length() > 0) gcs.append(", ");
            gcs.append(b.getName());
        }
        UPOLog.info("JVM: " + rt.getVmName() + " " + System.getProperty("java.version")
                + " | arch " + System.getProperty("os.arch") + " | cpus " + Runtime.getRuntime().availableProcessors()
                + " | max heap " + maxMb + " MB | collectors: " + gcs);
        UPOLog.info("JVM args: " + rt.getInputArguments());

        if ("32".equals(System.getProperty("sun.arch.data.model"))) {
            UPOLog.warn("32-bit JVM: heap is limited to ~1.5 GB; a 64-bit Java 8 is strongly recommended.");
        }
        if (maxMb < 2000) {
            UPOLog.warn("Max heap is " + maxMb + " MB. Under ~2 GB the game (especially with resource packs) "
                    + "tends to collect very frequently; consider raising -Xmx in the launcher.");
        }
    }
}
