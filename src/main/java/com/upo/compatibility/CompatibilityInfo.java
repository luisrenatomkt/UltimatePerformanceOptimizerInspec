package com.upo.compatibility;

/** Detects environment facts. Detection uses class lookup without initializing classes. */
public final class CompatibilityInfo {
    public final boolean optifine;

    private CompatibilityInfo(boolean optifine) { this.optifine = optifine; }

    public static CompatibilityInfo detect() {
        // Heuristic: these class names exist in OptiFine 1.8.9 builds. Needs verification on a real install.
        String[] candidates = { "optifine.OptiFineClassTransformer", "optifine.Installer", "Config" };
        boolean found = false;
        for (String n : candidates) {
            if (n.equals("Config")) continue; // too generic to trust on its own
            try {
                Class.forName(n, false, CompatibilityInfo.class.getClassLoader());
                found = true;
                break;
            } catch (Throwable ignored) { }
        }
        return new CompatibilityInfo(found);
    }
}
