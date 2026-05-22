# Semantic Drift Notes

These differences from the frozen Forge 1.19.4 baseline are intentional or currently accepted for the NeoForge 1.21.4 port PR.

- Spawn egg creative-mode use preserves the stack. Forge shrank the stack after successful spawn; Slice 1.1 explicitly restored survival consumption plus creative preservation and covers it with GameTests.
- Piston reveal checks both the piston event position and the block in front of the piston. Forge checked only the event position; the broader check preserves the player-facing disturbance contract.
- Cell Phone cross-dimension calls fail safely. Forge used dimension teleport hooks that are not restored in the current port surface. Slice 6.4 restored same-dimension Forge yaw-based arrival placement and kept cross-dimension transfer deferred.
- Far-call GameTests use in-template distant positions instead of the old 48-block fixture to avoid neighboring GameTest space while preserving forced-chunk behavior.
- Shrine tablet side effects are restored server-side: gate-pattern-gated claims, shrine-list rebroadcasts, visual lightning, ambient sound, and a bell-based Moe spawned five blocks below the tablet. Client shrine-location storage/rendering remains deferred.
- Shimenawa block entities now create/update/delete hidden `NPCs` rows and owner-list entries. The active port treats shimenawa as Moe hiding spots at the block position, using persistent block-entity strings for available profile fields.
- Trait tags are supported, but several frozen bundled Forge tag JSON categories are empty. Tests cover populated baseline tags plus parser fallback behavior.
- Shrine list filtering intentionally matches Forge: include rows owned by the requester OR rows in the requester's current dimension.
- Absent response text is represented as an empty string in NeoForge dialogue payloads. Rich translated fallback text remains deferred.
- Moe texture metadata is accepted from both `moes/textures` and `textures/moe` because the frozen shipped resources use the latter path for bundled metadata.
- Moe texture metadata with unknown or invalid block-state properties now fails closed. The frozen parser silently ignored those entries, which could make a malformed override match more broadly than intended.
- Recipes, loot tables, tags, worldgen fields, and item definitions are migrated only in generated NeoForge output. Frozen source resources remain unchanged.
- Creative tab restoration is treated as PR-review readiness. The frozen Forge code only had a commented tab shell; the active NeoForge tab now uses restored `ISortableItem`-style metadata through a local `SortableItem` interface.
- Weapon, armor, and music-disc server gameplay is active as of Slice 6.5. Record discs are represented with 1.21.4 jukebox song components/data instead of the removed old `RecordItem` constructor shape. Samurai armor client models/helmet overlays remain deferred, and exact armor durability is constrained by the modern armor-material multiplier surface.
- `ShimenawaBlock.neighborChanged` destroys only when support is missing. The frozen Forge source appears inverted and destroys when `canSurvive` is true; the NeoForge port keeps the player-facing support contract intact.
- The observed UI blur remains non-blocking because text and layout are usable. Lantern, hanging-scroll, and potted-plant classes/shapes are now restored, but final model/texture visual parity is still tracked for PR/manual review or post-port polish.
