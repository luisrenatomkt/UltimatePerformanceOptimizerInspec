package com.upo.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public final class UPOConfig {
    // UI settings: all OFF by default. They only control visuals.
    public static boolean enableGUI;
    public static boolean enablePerformanceHUD;
    public static boolean enableNotifications;

    // Engine settings: independent of the UI settings above.
    public static boolean enableOptimizations;
    public static boolean safeMode;
    public static int metricsIntervalMs;

    private UPOConfig() {}

    public static void load(File f) {
        Configuration c = new Configuration(f);
        try {
            c.load();
            String ui = "ui";
            c.setCategoryComment(ui, "Visual options only. Disabling them never affects optimization.");
            enableGUI = c.getBoolean("enableGUI", ui, false, "Allow the /upo command to open the settings screen.");
            enablePerformanceHUD = c.getBoolean("enablePerformanceHUD", ui, false, "Draw a performance HUD on screen.");
            enableNotifications = c.getBoolean("enableNotifications", ui, false, "Show on-screen notices (log is always used).");

            String eng = "engine";
            enableOptimizations = c.getBoolean("enableOptimizations", eng, true, "Master switch for optimization modules.");
            safeMode = c.getBoolean("safeMode", eng, false, "Monitoring only; no optimization module is started.");
            metricsIntervalMs = c.getInt("metricsIntervalMs", eng, 1000, 250, 10000, "Metrics sampling interval.");
        } finally {
            if (c.hasChanged()) c.save();
        }
    }
}
