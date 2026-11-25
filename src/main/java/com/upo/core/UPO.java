package com.upo.core;

import com.upo.compatibility.CompatibilityInfo;
import com.upo.config.UPOConfig;
import com.upo.engine.ModuleManager;
import com.upo.gui.GuiBootstrap;
import com.upo.intelligence.JvmReport;
import com.upo.intelligence.PerformanceMonitor;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Entry point. Client-side only.
 * Order matters: config -> compatibility -> engine/monitoring (always) -> GUI (optional, last).
 * The GUI is bootstrapped last and nothing in engine/ or intelligence/ references the gui package.
 */
@Mod(modid = UPO.MODID, name = UPO.NAME, version = "1.0.0",
        clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]",
        acceptableRemoteVersions = "*")
public class UPO {
    public static final String MODID = "upo";
    public static final String NAME = "Ultimate Performance Optimizer";

    private static PerformanceMonitor monitor;
    private static ModuleManager modules;
    private static CompatibilityInfo compat;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        UPOConfig.load(e.getSuggestedConfigurationFile());
        compat = CompatibilityInfo.detect();
        UPOLog.info("OptiFine detected: " + compat.optifine);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        // Background work: independent of any GUI setting.
        JvmReport.log();
        monitor = new PerformanceMonitor(UPOConfig.metricsIntervalMs);
        FMLCommonHandler.instance().bus().register(monitor);

        modules = new ModuleManager();
        // No optimization modules are registered yet (see README: Phases 3+ not implemented).
        modules.initAll(compat);

        // Optional visual layer. Registers nothing unless the player enabled it in config.
        GuiBootstrap.init(monitor, modules);
    }

    public static PerformanceMonitor monitor() { return monitor; }
    public static ModuleManager modules() { return modules; }
}
