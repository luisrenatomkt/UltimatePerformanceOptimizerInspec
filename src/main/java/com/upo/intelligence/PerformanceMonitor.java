package com.upo.intelligence;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.List;

/**
 * Lightweight metrics. Frame time comes from consecutive render-tick starts (one nanoTime call per frame).
 * JVM metrics are sampled at a configurable interval. GPU usage is NOT exposed: no reliable API in LWJGL2/OpenGL.
 * Written and read on the render thread only (the optional HUD/command also run there).
 */
public final class PerformanceMonitor {
    private static final int WINDOW = 600;
    private final long[] frames = new long[WINDOW];
    private int idx, count;
    private long lastStart;
    private final long intervalNs;
    private long lastSample;
    private long lastGcCount = -1, lastGcTimeMs;

    // Latest snapshot
    private volatile double avgFps, low1Fps, avgFrameMs, worstFrameMs;
    private volatile long heapUsedMb, heapMaxMb, gcCountDelta, gcTimeMsDelta;

    private final MemoryPressureAnalyzer pressure = new MemoryPressureAnalyzer();
    private final List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

    public PerformanceMonitor(int intervalMs) { this.intervalNs = intervalMs * 1_000_000L; }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;
        long now = System.nanoTime();
        if (lastStart != 0) {
            frames[idx] = now - lastStart;
            idx = (idx + 1) % WINDOW;
            if (count < WINDOW) count++;
        }
        lastStart = now;
        if (now - lastSample >= intervalNs) {
            long elapsedMs = lastSample == 0 ? intervalNs / 1_000_000L : (now - lastSample) / 1_000_000L;
            lastSample = now;
            sample(elapsedMs);
        }
    }

    private void sample(long elapsedMs) {
        if (count < 10) return;
        long[] copy = Arrays.copyOf(frames, count);
        Arrays.sort(copy);
        long sum = 0;
        for (long v : copy) sum += v;
        double avgNs = (double) sum / count;
        int n = Math.max(1, count / 100);
        long worstSum = 0;
        for (int i = count - n; i < count; i++) worstSum += copy[i];
        avgFrameMs = avgNs / 1e6;
        avgFps = 1e9 / avgNs;
        low1Fps = 1e9 / ((double) worstSum / n);
        worstFrameMs = copy[count - 1] / 1e6;

        MemoryUsage h = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        heapUsedMb = h.getUsed() >> 20;
        heapMaxMb = h.getMax() >> 20;

        long c = 0, t = 0;
        for (GarbageCollectorMXBean b : gcBeans) {
            if (b.getCollectionCount() > 0) c += b.getCollectionCount();
            if (b.getCollectionTime() > 0) t += b.getCollectionTime();
        }
        if (lastGcCount >= 0) { gcCountDelta = c - lastGcCount; gcTimeMsDelta = t - lastGcTimeMs; }
        pressure.update(gcTimeMsDelta, elapsedMs);
        lastGcCount = c; lastGcTimeMs = t;
    }

    public MemoryPressureAnalyzer.Level memoryPressure() { return pressure.level(); }

    public String summary() {
        return String.format("FPS avg %.0f | 1%% low %.0f | frame %.2f ms (worst %.1f) | heap %d/%d MB | GC +%d (%d ms) | mem pressure %s | GPU: unavailable",
                avgFps, low1Fps, avgFrameMs, worstFrameMs, heapUsedMb, heapMaxMb, gcCountDelta, gcTimeMsDelta, pressure.level());
    }
}
