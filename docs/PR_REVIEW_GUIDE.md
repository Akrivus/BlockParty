# NeoForge Port PR Review Guide

Use this guide to review the normalized NeoForge 1.21.4 port before merging and moving to post-port feature work.

Phase 6 automated closeout is complete as of Slice 6.7: `compileJava` and `runGameTestServer` pass with all 115 required GameTests. This guide is now the manual pre-merge review checklist.

## Build Checks

- Run `.\gradlew.bat compileJava --no-daemon`.
- Run `.\gradlew.bat runGameTestServer --no-daemon`.
- Confirm the GameTest summary reports all required tests passing.

## Manual Client Smoke

- Launch a client with `.\gradlew.bat runClient --no-daemon`.
- Create or open a disposable world.
- Confirm the `Block Party` creative tab appears.
- Confirm key creative-tab icons render: `moe_spawn_egg`, `cell_phone`, `yearbook`, representative wood/decorative blocks, saplings, and data blocks.
- Confirm block-backed item names resolve in inventory/tooltips instead of showing raw `item.block_party.*` keys, especially wood blocks, potted saplings, and wisteria vines.
- Confirm the creative tab is review-friendly near the front: spawn egg, Yearbook, Letter, Calligraphy Brush, shrine/garden/writing data blocks, shimenawa, saplings, wood sets, hanging scrolls, then paper lanterns.
- Confirm food/review items behave with the restored properties: cupcake/onigiri/bento are edible, calligraphy brush is one-stack and damageable, yearbook pages are one-stack, music discs are playable discs, letters keep open/closed state, and samurai weapons/armor have their restored server behavior.
- Confirm right-clicking with a katana or bokken raises the sword in a blocking/parry stance, and melee hits while raised are deflected back into the attacker.
- Place a block tagged by `block_party:spawns_moes`, take `moe_spawn_egg` from the tab, and spawn a Moe without commands.
- Confirm the spawned Moe renders, has a visible name/health label when expected, and can hide/reveal through the restored scene/lifecycle paths.
- Confirm Moe winged variants flap while airborne, crouching adjusts the arm pose, riding/passenger pose does not explode visually, and attack swings animate the active arm.
- Confirm dialogue speaker animations apply, especially bundled `wave`, and Yearbook previews still render correctly with the restored `YEARBOOK` animation key.
- Near a claimed shrine, confirm the JapanRenderer effect appears: Fuji overlay direction changes with nearest shrine bearing, fog tints toward the legacy Japan palette, and fireflies can appear over `block_party:spawns_fireflies` blocks during the active time window.
- Place/claim a supported shimenawa and confirm it appears as an owned hidden NPC entry in controller flows once the relevant UI list refreshes.
- Place/claim a complete shrine gate tablet and confirm the server-side activation is visible: lightning flash, ambient sound, shrine-spawned bell Moe below the tablet, and refreshed shrine list behavior.
- Open Cell Phone and Yearbook from the creative tab and confirm the UI is usable.
- Place transparent decorative blocks and confirm adjacent blocks render through cutout surfaces where expected.
- Check ginkgo leaves for falling leaf particles and wisteria leaves/vines for vine placement/growth behavior.
- Equip samurai armor in a client smoke world and verify the Samurai model/texture renders instead of the vanilla chainmail model/texture.

## Known Visual Follow-Ups

- Lantern, hanging-scroll, shoji, shrine/shimenawa, writing-table, and potted-sapling block classes/shapes are restored and GameTested; final model/texture visual parity still needs an in-client smoke. Wind chimes are intentionally disabled because the model asset is missing.
- Ginkgo leaf particle behavior and wisteria leaf/vine growth behavior are restored and GameTested; final particle appearance still needs in-client smoke.
- Moe wing, crouch, passenger, and attack-pose animation math is restored on the NeoForge render-state model path, but full legacy animation classes remain disabled.
- Samurai armor model layer definitions and NeoForge client item extensions are restored. Final first-person helmet overlay parity still needs client smoke.
- Hanging scroll model rendering was reported broken before this pass and should be rechecked after the restored block class/shape registration.
- Potted plants now use `FlowerPotBlock` behavior; recheck their model/texture mapping in client.
- UI blur has been observed, but text and layout are currently usable.

These should not block source normalization unless they hide gameplay-critical behavior. File PR comments or post-port polish issues if they remain after the final client smoke.

## Merge Readiness

- Automated server-side blocker status: clear after Slice 6.7.
- Manual blocker status: depends on this checklist. Treat crashes, invisible core items/entities, unusable Yearbook/Cell Phone/Dialogue screens, broken spawn/hide/reveal/call flows, or persistence loss after restart as merge blockers.
- Non-blocking polish if gameplay remains usable: UI blur, minor model mismatch, missing client-only armor overlay polish, and documented cross-dimension Cell Phone failure state.

## Source Layout Review

- Active NeoForge Java source is under `src/main/java`.
- Frozen Forge 1.19.4 Java reference source is under `src/forge1194/java`.
- Frozen source resources remain under `src/main/resources`; NeoForge target-format copies are generated by `syncNeoForgeSpikeResources`.
- NeoForge metadata/test structure resources remain under `src/neoforgeSpike/resources` until the resource normalization follow-up.

## Drift Review

Review `docs/SEMANTIC_DRIFT_NOTES.md` for accepted behavioral differences from Forge 1.19.4. New drift should be added there before merge.
