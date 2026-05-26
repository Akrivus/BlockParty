# Technical Debt Register

This document lists known and suspected issues found by static reading. It is a triage document, not a fix plan. Do not change behavior until the current behavior is covered by parity tests.

Risk scale:

- `Version-move risk`: how likely this is to break or complicate future Minecraft/NeoForge updates.
- `Gameplay importance`: how visible or damaging this is to player-facing behavior.
- `Order`: suggested order of attack after baseline tests exist.

## Recently Resolved

### Active NeoForge texture metadata and hiding spot dirty churn

- Status: Resolved in NeoForge Slice 6.6.
- Previous evidence: malformed Moe texture metadata could silently ignore unknown/invalid block-state properties, making an override broader than intended; `HidingSpots` marked SavedData dirty for no-op removes and unchanged puts.
- Resolution: `MoeTextureReloadListener` now fails those malformed property entries closed, and `HidingSpots` dirties persistence only when entries actually change.
- Compatibility note: the old Forge SQL builder and Markov action debt remain listed below as frozen-reference issues, but those classes are not active in the normalized NeoForge source.

### Recursive combat overrides

- Status: Resolved in the active NeoForge line.
- Previous evidence: `Layer1.doHurtTarget(Entity)` and `Layer7.doHurtTarget(Entity)` called back into their own overrides, causing stack overflows when combat occurred.
- Resolution: both overrides now delegate to `super.doHurtTarget(target)` before applying their layer-specific behavior.
- Compatibility note: keep combat behavior in parity coverage, but this is no longer an open dangerous-bug prerequisite.

### Hidden Moe save/load ignored saved hide condition

- Status: Resolved in the active NeoForge line.
- Previous evidence: `MoeInHiding.readAdditionalSaveData` called `HideUntil.EXPOSED.fromValue("HideUntil")` instead of reading the NBT value.
- Resolution: hidden Moe loading now restores `HideUntil` from the saved `HideUntil` NBT string.
- Compatibility note: keep timed hide save/load in parity coverage so the fixed behavior survives future persistence changes.

## Dangerous Bugs

### Moe texture lookup likely uses the wrong map key

- Evidence: `MoeTextures.map` is keyed by `Block`, but `MoeTextures.get` calls `map.getOrDefault(state, ...)` with a `BlockState`.
- Likely cause: block-state pattern matching was added after the outer map was keyed by block.
- Version-move risk: Medium. Rendering APIs will move, and wrong fallback behavior may be mistaken for port breakage.
- Gameplay importance: High. Block-origin identity is mostly visual.
- Suggested order: 1. Screenshot baseline texture behavior, then fix with renderer/resource tests.

### Dirty-row update can generate invalid SQL

- Evidence: `Row.update()` builds `SET` clauses from dirty columns; if none are dirty, `getColumnSetters` substrings an empty string.
- Likely cause: row updates assume every call follows a mutation.
- Version-move risk: Medium. Persistence behavior may be exercised more often during future API changes.
- Gameplay importance: High if it crashes on save/sync; otherwise latent.
- Suggested order: 4. Add DB update tests around no-op updates, then guard or avoid no-op SQL.

### Hidden spot lookup can unbox null

- Evidence: `HidingSpots.get(ServerLevel, BlockPos)` returns primitive `long` from `spot.list.get(pos)`.
- Likely cause: callers normally check `isNormalBlock` first, so null was not handled locally.
- Version-move risk: Medium. Event order changes can make this fragile.
- Gameplay importance: High. Hidden Moe reveal should never crash the server.
- Suggested order: 5. Cover hidden-block event paths before changing event subscriptions.

### Player ownership checks can null-crash

- Evidence: `YearbookItem.interactLivingEntity` calls `npc.getPlayer().equals(player)` without null guard.
- Likely cause: code assumes owner is online/resolvable.
- Version-move risk: Low to medium.
- Gameplay importance: Medium. Affects Yearbook use on NPCs with missing/offline owner state.
- Suggested order: 6. Test Yearbook interaction with owner online/offline before fixing.

## Version-Sensitive Active Surfaces

### NeoForge custom payload surface

- Evidence: `CustomMessenger` registers typed payload records through `RegisterPayloadHandlersEvent`, `PayloadRegistrar`, and `PacketDistributor`.
- Likely cause: current NeoForge 1.21.4 networking implementation.
- Version-move risk: High. NeoForge networking APIs and payload registration patterns can differ across target versions.
- Gameplay importance: High. Dialogue, Yearbook, Cell Phone, shrine sync, NPC lookup, and teleport depend on packets.
- Suggested order: 7. Keep payload IDs/codecs covered before changing networking or moving versions.

### NeoForge event bus and lifecycle wiring

- Evidence: `BlockParty`, `CustomResources`, `CustomEntities`, `BlockPartyDB`, `CellPhone`, `TsukumogamiSpawns`, and `HidingSpots` depend on NeoForge mod/game event buses.
- Likely cause: normal NeoForge architecture.
- Version-move risk: High. Load, reload, entity attributes, level events, and player events are all API-sensitive.
- Gameplay importance: High. Spawn, persistence, hiding, and resources depend on these hooks.
- Suggested order: 8. Change event wiring in small slices with smoke tests after each.

### SavedData and world storage APIs

- Evidence: `BlockPartyDB` and `HidingSpots` use `SavedData`, `DimensionDataStorage`, and server-level data storage directly.
- Likely cause: world-scoped persistence is built against current Minecraft saved-data APIs.
- Version-move risk: High.
- Gameplay importance: High. Losing DB lists or hidden spots breaks companion persistence.
- Suggested order: 9. Change after DB schema baseline and hidden-Moe save tests exist.

### Client rendering and screen APIs

- Evidence: `BlockPartyRenderers`, `MoeRenderer`, `MoeRenderState`, layers, `DialogueScreen`, `YearbookScreen`, `CellPhoneScreen`, Samurai armor client extensions, and JapanRenderer use modern Minecraft client APIs.
- Likely cause: normal Minecraft client API churn.
- Version-move risk: High.
- Gameplay importance: High. Moe identity and dialogue are heavily visual.
- Suggested order: 10. Screenshot-test after entity/data model changes.

### SQLite shading and JDBC availability

- Evidence: `build.gradle` shadows and relocates `org.sqlite`; `BlockPartyDB` loads `org.sqlite.JDBC`.
- Likely cause: bundling SQLite driver inside the mod jar.
- Version-move risk: Medium to high. Build tooling, module/classloader behavior, or jar-in-jar rules may change.
- Gameplay importance: High. NPC persistence depends on SQLite.
- Suggested order: 11. Verify DB connection in a minimal world before gameplay testing.

### Chunk forcing and teleport APIs

- Evidence: `CellPhone` and `MoeSpawner` use chunk tickets/lookup plus Minecraft 1.21.4 `TeleportTransition`.
- Likely cause: Cell Phone behavior depends on finding and moving Moes across loaded and cross-dimension contexts.
- Version-move risk: Medium to high.
- Gameplay importance: Medium to high. Cell Phone behavior depends on finding and moving Moes.
- Suggested order: 12. Change after basic NPC DB lookup works.

### Resource reload listener APIs

- Evidence: `Scenes`, `Names`, `MoeTextures`, `MoeSounds`, and `BlockAliases` extend `SimpleJsonResourceReloadListener`.
- Likely cause: data-driven design on current Minecraft resource APIs.
- Version-move risk: Medium.
- Gameplay importance: High. Names, dialogue, textures, and sounds depend on reload.
- Suggested order: 13. Keep early enough that spawn/dialogue tests use real resources.

## Unfinished Systems

### Chores, pranks, adventuring, and following AI

- Evidence: README describes these features; code has stats, sounds, inventory, and `isFollowing`, but no complete AI loop found.
- Likely cause: feature scaffolding exists after project hiatus, but behavior was not implemented.
- Version-move risk: Low if preserved as-is; high if mixed into unrelated version work.
- Gameplay importance: High for final mod vision, low for current parity if not observable.
- Suggested order: 20. Defer until after current baseline parity and bug fixes.

### Hunger, loneliness, stress, action, and sleep updates are empty

- Evidence: the old layer-stack need/update hooks are not active gameplay in the normalized NeoForge `Moe` shell.
- Likely cause: placeholders for companion simulation were preserved as design intent, not restored behavior.
- Version-move risk: Low unless signatures/API calls change.
- Gameplay importance: Medium now; high for future behavior.
- Suggested order: 21. Preserve fields during future version work; implement later with tests.

### Scene library is only test content

- Evidence: only `test_dialogue.json` and `test_hide.json` exist under `data/block_party/scenes`.
- Likely cause: scene engine built before final content.
- Version-move risk: Low.
- Gameplay importance: Medium. Dialogue system works, but content is minimal.
- Suggested order: 22. Add content after data-pack compatibility is stable.

### Markov action is unregistered and probably incorrect

- Evidence: `scene.actions.Markov` exists but is not registered in `SceneActions`; `chain` stores entries by probability rather than cumulative total.
- Likely cause: experimental action not wired into data-driven system.
- Version-move risk: Low if unused; medium if port assumes it is supported.
- Gameplay importance: Low currently.
- Suggested order: 23. Decide whether to delete, register, or rewrite after scene tests.

### Senpai entity is not active

- Evidence: `CustomEntities` registers only `MOE` and `MOE_IN_HIDING`; the old Senpai/base-NPC hierarchy is not active in `src/main/java`.
- Likely cause: planned NPC type never finished.
- Version-move risk: Low.
- Gameplay importance: Low unless external data references it.
- Suggested order: 24. Defer; preserve class until intent is known.

### Sapling DB schema not created on world load

- Evidence: `BlockPartyDB.Saplings` exists; `onWorldLoad` creates Shrines, Locations, Gardens, and NPCs, but not Saplings.
- NeoForge note: the build intentionally creates the minimal `Saplings` table alongside `Shrines`, `GardenLanterns`, and `Locations` to keep the server-side block entity surface internally consistent. This is a documented compatibility choice from the old Forge baseline.
- Likely cause: missed schema during persistence expansion.
- Version-move risk: Medium if sapling block entities are exercised.
- Gameplay importance: Medium for sakura sapling persistence/worldgen.
- Suggested order: verify sapling gameplay before broad block-entity changes.

### Payload surfaces should stay explicitly documented

- Evidence: old Forge packet classes have been replaced by `block_party.network.payload.*`, but compatibility notes and tests still need to track payload IDs, directions, and codecs.
- Likely cause: networking was normalized during the NeoForge port.
- Version-move risk: Low, but confusing.
- Gameplay importance: Medium. Dialogue, Yearbook, Cell Phone, and shrine sync all depend on payload routing.
- Suggested order: 25. Keep payload tests and docs current before future networking changes.

### Client render type registration needs visual validation

- Evidence: `BlockPartyClientEvents.registerRenderTypes` now assigns cutout and cutout-mipped layers for representative decorative blocks.
- Likely cause: render setup was restored on the modern client event path.
- Version-move risk: Medium. NeoForge/client rendering changes will revisit this area.
- Gameplay importance: Medium. Transparent/cutout blocks may render incorrectly.
- Suggested order: 15. Screenshot block rendering before and after client/render changes.

### Creative tab ordering needs release checks

- Evidence: `CustomCreativeTabs` registers the active Block Party tab and GameTests review stack sorting.
- Likely cause: creative tabs were restored against the current NeoForge API.
- Version-move risk: Medium.
- Gameplay importance: Medium. Item discoverability and release review depend on stable ordering.
- Suggested order: 26. Keep GameTests and release review checks current as items are restored.

## Architectural Risks

### Frozen seven-layer NPC inheritance stack

- Evidence: the old `BlockPartyNPC`/`Layer1`-through-`Layer7` structure is no longer active in `src/main/java`; the active runtime is centered on `Moe`.
- Likely cause: NeoForge normalization moved live behavior out of the old layered hierarchy.
- Version-move risk: Low for active code, medium for compatibility work that restores old NPC abstractions.
- Gameplay importance: Medium. Planned `Senpai`/base-NPC restoration should not destabilize active Moe behavior.
- Suggested order: 16. Reintroduce shared hierarchy only with Senpai/base-NPC tests.

### Avoid broad synced-data DB writeback returning

- Evidence: old Forge `Layer5.onSyncedDataUpdated` did broad row writes; the active NeoForge shell uses more explicit row updates.
- Likely cause: convenient persistence hook in the frozen design.
- Version-move risk: Medium. Synched-data APIs and update frequency may change.
- Gameplay importance: High. Can affect performance, DB churn, and persistence correctness.
- Suggested order: 17. Keep explicit save/update paths covered and resist reintroducing broad callbacks without tests.

### SQL builder is custom and partly manual

- Evidence: `Table`, `Row`, and `Column` generate SQL strings directly; some IDs and table names are interpolated.
- Likely cause: lightweight in-mod ORM.
- Version-move risk: Medium.
- Gameplay importance: High. DB failures are world/companion failures.
- Suggested order: 18. Preserve schema first; add tests before changing query generation.

### Column dirty tracking uses reference comparison

- Evidence: `Column.set` checks `this.value != value`.
- Likely cause: simple object identity check.
- Version-move risk: Medium. New APIs may produce new object instances for equivalent values.
- Gameplay importance: Medium to high. Can cause excess updates or missed logical equality.
- Suggested order: 19. Fix after DB no-op update behavior is tested.

### Personality generation is scattered

- Evidence: names and traits cross `Moe`, `NPC`, block tags, social-affinity resources, and old baseline expectations.
- Likely cause: spawn paths grew organically.
- Version-move risk: Medium.
- Gameplay importance: High. Moe identity must not drift during future version work.
- Suggested order: 27. Add personality golden tests first; consolidate later.

### Scene interruption semantics are implicit

- Evidence: `SceneManager.trigger` only accepts higher-priority triggers; `setAction(null)` calls `onComplete` on existing action.
- Likely cause: simple priority queue/state-machine implementation.
- Version-move risk: Low to medium.
- Gameplay importance: Medium. Dialogue/behavior timing may feel wrong if changed.
- Suggested order: 28. Document with tests before refactoring scene manager.

### Hidden spots are position keyed and dimension-sensitive by storage context

- Evidence: `HidingSpots` stores `Map<BlockPos, Long>`, while public APIs pass `DimBlockPos`.
- Likely cause: one `SavedData` instance per server level makes dimension implicit.
- Version-move risk: Medium. SavedData scoping may change during future version work.
- Gameplay importance: High. Hidden Moes must not reveal in the wrong place or vanish.
- Suggested order: 29. Test overworld/nether hidden positions before changing.

### Payload direction and ID contracts are easy to drift

- Evidence: NeoForge payload registration is split between `playToServer` and `playToClient`, with `DialogueClosePayload` intentionally serverbound.
- Likely cause: payload direction is encoded in registration rather than class inheritance.
- Version-move risk: Medium during networking changes.
- Gameplay importance: Medium.
- Suggested order: 30. Preserve payload IDs, directions, and codecs through tests before renaming or reorganizing payload classes.

### Resource namespace remapping may surprise data packs

- Evidence: `Scenes.own` remaps `minecraft` namespace resources into `block_party`.
- Likely cause: convenience for un-namespaced/default scene references.
- Version-move risk: Low to medium.
- Gameplay importance: Medium for external content compatibility.
- Suggested order: 31. Preserve during future version work; document for data-pack authors later.

## Safe Modernization Opportunities

These are lower-risk cleanup opportunities once parity tests pass. They should not be mixed with risky behavior changes.

### Add focused parity tests

- Likely cause addressed: most risks are untested behavior.
- Version-move risk: Low; reduces future risk.
- Gameplay importance: High.
- Suggested order: 0. Do this before fixing risky behavior.

### Keep payload names and services explicit

- Targets: `ControllerOpenPayload`, `NpcDetailPayload`, `NpcCallRequestPayload`, `DialogueOpenPayload`, `DialogueRespondPayload`, `ShrineListPayload`, and related server services.
- Version-move risk: Low after networking parity.
- Gameplay importance: Low.
- Suggested order: 32.

### Replace broad SQL string building with safer helpers

- Targets: `Table`, `Row`, `Column`.
- Version-move risk: Medium, but safe after DB tests.
- Gameplay importance: Medium.
- Suggested order: 33.

### Centralize profile generation

- Targets: name, gender, blood type, dere, zodiac, block tag application.
- Version-move risk: Medium.
- Gameplay importance: High.
- Suggested order: 34.

### Isolate loader/platform APIs behind small adapters

- Targets: networking, event registration, resource reload, chunk forcing, saved data.
- Version-move risk: Medium initially, beneficial after first port.
- Gameplay importance: Medium.
- Suggested order: 35.

### Convert commented render setup to modern client registration

- Targets: cutout/cutout-mipped render layers for decorative blocks.
- Version-move risk: Low to medium after client port.
- Gameplay importance: Medium.
- Suggested order: 36.

### Add diagnostics for resource loading

- Targets: scenes, names, textures, sounds, aliases.
- Version-move risk: Low.
- Gameplay importance: Medium.
- Suggested order: 37.

### Document save schema and add migration versioning

- Targets: SQLite tables, world saved data, hidden spots.
- Version-move risk: Medium.
- Gameplay importance: High for long-term worlds.
- Suggested order: 38.

### Decide fate of planned systems

- Targets: chores, pranks, adventuring, sleep, hunger, stress, Senpai, Markov.
- Version-move risk: Low if done after parity.
- Gameplay importance: High for product direction.
- Suggested order: 39.

## Suggested Overall Order

1. Add parity tests and a manual golden-world checklist for spawn, dialogue, hide/reveal, save/load, Yearbook, Cell Phone, textures, and DB.
2. Fix or explicitly baseline the remaining dangerous bugs that can crash core behavior.
3. Keep loader registration, resources, networking, entity APIs, rendering, and persistence stable during future version moves.
4. Verify the behavior contract against the active NeoForge build after each risky slice.
5. Address architectural DB/sync/profile risks.
6. Modernize low-risk names, comments, diagnostics, and render setup.
7. Implement unfinished systems only after current baseline coverage is stable.
