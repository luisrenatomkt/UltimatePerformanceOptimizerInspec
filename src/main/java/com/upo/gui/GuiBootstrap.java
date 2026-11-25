package com.upo.gui;

import com.upo.config.UPOConfig;
import com.upo.core.UPOLog;
import com.upo.engine.ModuleManager;
import com.upo.intelligence.PerformanceMonitor;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

/**
 * Optional visual layer. With default config (all false) this method registers NOTHING:
 * no event handlers, no commands, no draw calls. Engine and monitoring are unaffected either way
 * because they never depend on this package.
 */
public final class GuiBootstrap {
    private GuiBootstrap() {}

    public static void init(PerformanceMonitor monitor, ModuleManager modules) {
        if (UPOConfig.enableGUI) {
            ClientCommandHandler.instance.registerCommand(new UPOCommand(monitor, modules));
            UPOLog.info("GUI command /upo registered (enableGUI=true).");
        }
        if (UPOConfig.enablePerformanceHUD) {
            MinecraftForge.EVENT_BUS.register(new HudRenderer(monitor));
            UPOLog.info("Performance HUD registered (enablePerformanceHUD=true).");
        }
    }
}
