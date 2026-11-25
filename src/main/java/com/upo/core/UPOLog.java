package com.upo.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** All diagnostics go to the log; nothing is shown on screen. */
public final class UPOLog {
    private static final Logger L = LogManager.getLogger("UPO");
    private UPOLog() {}
    public static void info(String m) { L.info(m); }
    public static void warn(String m) { L.warn(m); }
    public static void error(String m, Throwable t) { L.error(m, t); }
}
