# NeoForge Spike Notes

Phase 5.4 replaced the spike handoff document with `docs/NEOFORGE_PORT_NOTES.md`.

This file is kept as a compatibility pointer for older reconstruction prompts and review checklists.

Phase 6 planning now lives in `docs/PORT_RECONSTRUCTION_PLAN.md` under "Gameplay Parity Closeout".
Slice 6.1 started that phase by wiring Moe runtime sound hooks to the active `moes/sounds` reload data and restoring source-block fire-immunity/path malus behavior.
Slice 6.2 restored the Forge corporeal-death retreat path: server-side corporeal Moe death runs vanilla death handling and then hides the Moe as its source block with the existing row-backed hiding lifecycle. Ethereal death remains ordinary death and does not create a hiding spot.
Slice 6.3 restored Forge-shaped scene trigger routing for owner right/left interactions, non-owner hurt, successful Moe attacks, and random/stare server AI hooks. The random/stare hook is factored so GameTests can exercise it deterministically without adding new invisible scene resources.
Slice 6.4 restored source-block navigation category changes and Forge Cell Phone same-dimension arrival placement. Winged Moes now use flying navigation, grounded Moes use ground navigation, and successful calls place Moes at the yaw-based Forge offset before setting `following=true`. Cross-dimension calls remain a safe failure.
Slice 6.5 replaced the remaining simple item-family placeholders with server-observable Forge behavior for letters, music discs, samurai weapons, and samurai armor. Music discs now use 1.21.4 jukebox song components, letters read the legacy `IsClosed` custom data key, katana/bokken parry behavior is event-wired, and samurai armor XP-repair/arrow-damage hooks are active. Client samurai armor model and overlay parity remains deferred.
Slice 6.6 closed the active tested tech-debt surface before release hardening: malformed Moe texture metadata with unknown/invalid block-state properties now fails closed, and `HidingSpots` only marks SavedData dirty when entries actually change. Frozen Forge SQL builder and Markov action debt remain reference-only because those classes are not active in the normalized NeoForge source.
Slice 6.7 closed Phase 6 for automated server-side verification. `compileJava` and `runGameTestServer` pass with all 115 required GameTests. The remaining gate before merge is manual client/golden-world review for rendering, screens, audio playback, long-lived persistence, and the accepted deferred visual polish.
