package com.upo.gui;

import com.upo.engine.ModuleManager;
import com.upo.intelligence.PerformanceMonitor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/** Player-invoked only. Prints current metrics to chat when the player types /upo. */
public final class UPOCommand extends CommandBase {
    private final PerformanceMonitor monitor;
    private final ModuleManager modules;

    UPOCommand(PerformanceMonitor monitor, ModuleManager modules) { this.monitor = monitor; this.modules = modules; }

    @Override public String getCommandName() { return "upo"; }
    @Override public String getCommandUsage(ICommandSender s) { return "/upo"; }
    @Override public int getRequiredPermissionLevel() { return 0; }
    @Override public boolean canCommandSenderUseCommand(ICommandSender s) { return true; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("[UPO] " + monitor.summary()));
        sender.addChatMessage(new ChatComponentText("[UPO] active modules: " + modules.active().size()));
    }
}
