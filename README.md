# Ultimate Performance Optimizer (UPO) — Minecraft 1.8.9 Forge

## Status (honest)
**Implemented (written, NOT yet compiled or run — no Forge toolchain/network in the authoring sandbox):**
- Gradle/ForgeGradle 2.1 project, GitHub Actions workflow (Java 8, Gradle 2.14.1, artifact upload)
- Client-side-only mod entry, config, logging
- Performance monitor: frame time, avg FPS, 1% low, heap, GC count/time (JVM MXBeans). GPU usage: unavailable by design.
- Module manager with per-module isolation, Safe Mode, config master switch
- OptiFine detection (heuristic class lookup; needs verification on a real install)
- Optional GUI layer (see below)
- Phase 3a: `MemoryPressureAnalyzer` (post-GC live ratio + GC time share, with hysteresis) and `JvmReport` (logs heap/GC/JVM args, warns on 32-bit or <2 GB heap). These are diagnostics feeding the future controller, NOT optimizations.
- `inspect.yml`: manual workflow that exports decompiled Minecraft 1.8.9 sources for bytecode-accurate patching

**NOT implemented yet (no placeholders were created for them):**
Phases 3–8: CPU, Memory, Chunk, Entity, GPU, Rendering, Texture, Particle optimizers, Scheduler,
Dynamic Controller, ASM transformers. Each requires inspecting real 1.8.9/OptiFine bytecode and
before/after benchmarks; none is claimed.

## GUI is fully optional
`enableGUI`, `enablePerformanceHUD`, `enableNotifications` default to `false`.
All visual code lives in `com.upo.gui`; `engine/` and `intelligence/` never import it.
`GuiBootstrap.init` registers nothing when the flags are false. Engine switches (`enableOptimizations`,
`safeMode`) are separate settings in the `engine` config category.
