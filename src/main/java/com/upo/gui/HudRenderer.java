package com.upo.gui;

import com.upo.intelligence.PerformanceMonitor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Registered only when enablePerformanceHUD=true. Off by default. */
final class HudRenderer {
    private final PerformanceMonitor monitor;
    HudRenderer(PerformanceMonitor m) { this.monitor = m; }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Text e) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(monitor.summary(), 4, 4, 0xFFFFFF);
    }
}
