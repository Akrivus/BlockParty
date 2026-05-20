# Technical Debt Register

This document lists known and suspected issues found by static reading. It is a triage document, not a fix plan. Do not change behavior until the current behavior is covered by parity tests.

Risk scale:

- `Migration risk`: how likely this is to break or complicate a Forge-to-NeoForge/Minecraft-version port.
- `Gameplay importance`: how visible or damaging this is to player-facing behavior.
- `Order`: suggested order of attack after baseline tests exist.

## Recently Resolved

### Recursive combat overrides

- Status: Resolved before NeoForge migration work.
- Previous evidence: `Layer1.doHurtTarget(Entity)` and `Layer7.doHurtTarget(Entity)` called back into their own overrides, causing stack overflows when combat occurred.
- Resolution: both overrides now delegate to `super.doHurtTarget(target)` before applying their layer-specific behavior.
- Migration note: keep combat behavior in parity coverage, but this is no longer an open dangerous-bug prerequisite.

### Hidden Moe save/load ignored saved hide condition

- Status: Resolved before NeoForge migration work.
- Previous evidence: `MoeInHiding.readAdditionalSaveData` called `HideUntil.EXPOSED.fromValue("HideUntil")` instead of reading the NBT value.
- Resolution: hidden Moe loading now restores `HideUntil` from the saved `HideUntil` NBT string.
- Migration note: keep timed hide save/load in parity coverage so the fixed behavior survives the persistence API port.

## Dangerous Bugs

### Moe texture lookup likely uses the wrong map key

- Evidence: `MoeTextures.map` is keyed by `Block`, but `MoeTextures.get` calls `map.getOrDefault(state, ...)` with a `BlockState`.
- Likely cause: block-state pattern matching was added after the outer map was keyed by block.
- Migration risk: Medium. Rendering APIs will move, and wrong fallback behavior may be mistaken for port breakage.
- Gameplay importance: High. Block-origin identity is mostly visual.
- Suggested order: 1. Screenshot baseline texture behavior, then fix with renderer/resource tests.

### Dirty-row update can generate invalid SQL

- Evidence: `Row.update()` builds `SET` clauses from dirty columns; if none are dirty, `getColumnSetters` substrings an empty string.
- Likely cause: row updates assume every call follows a mutation.
- Migration risk: Medium. Persistence behavior may be exercised more often during API rewrites.
- Gameplay importance: High if it crashes on save/sync; otherwise latent.
- Suggested order: 4. Add DB update tests around no-op updates, then guard or avoid no-op SQL.

### Hidden spot lookup can unbox null

- Evidence: `HidingSpots.get(ServerLevel, BlockPos)` returns primitive `long` from `spot.list.get(pos)`.
- Likely cause: callers normally check `isNormalBlock` first, so null was not handled locally.
- Migration risk: Medium. Event order changes can make this fragile.
- Gameplay importance: High. Hidden Moe reveal should never crash the server.
- Suggested order: 5. Cover hidden-block event paths before changing event subscriptions.

### Player ownership checks can null-crash

- Evidence: `YearbookItem.interactLivingEntity` calls `npc.getPlayer().equals(player)` without null guard.
- Likely cause: code assumes owner is online/resolvable.
- Migration risk: Low to medium.
- Gameplay importance: Medium. Affects Yearbook use on NPCs with missing/offline owner state.
- Suggested order: 6. Test Yearbook interaction with owner online/offline before fixing.

## Migration Blockers

### Forge-specific networking stack

- Evidence: `CustomMessenger` uses Forge `SimpleChannel`, `NetworkRegistry`, `NetworkDirection`, and `NetworkEvent.Context`.
- Likely cause: normal Forge 1.19.4 implementation.
- Migration risk: High. NeoForge networking APIs and payload registration patterns differ across target versions.
- Gameplay importance: High. Dialogue, Yearbook, Cell Phone, shrine sync, NPC lookup, and teleport depend on packets.
- Suggested order: 7. Port after registry load works, before UI parity.

### Forge event bus and lifecycle wiring

- Evidence: `BlockParty`, `CustomResources`, `CustomEntities`, `BlockPartyDB`, and `HidingSpots` depend on Forge mod/event bus classes.
- Likely cause: normal Forge architecture.
- Migration risk: High. Load, reload, entity attributes, level events, and player events are all API-sensitive.
- Gameplay importance: High. Spawn, persistence, hiding, and resources depend on these hooks.
- Suggested order: 8. Port event wiring in small slices with smoke tests after each.

### SavedData and world storage APIs

- Evidence: `BlockPartyDB` and `HidingSpots` use `SavedData`, `DimensionDataStorage`, and server-level data storage directly.
- Likely cause: world-scoped persistence is built against 1.19.4 APIs.
- Migration risk: High.
- Gameplay importance: High. Losing DB lists or hidden spots breaks companion persistence.
- Suggested order: 9. Port after DB schema baseline and hidden-Moe save tests exist.

### Client rendering and screen APIs

- Evidence: `BlockPartyRenderers`, `MoeRenderer`, layers, `DialogueScreen`, `YearbookScreen`, and `CellPhoneScreen` use 1.19.4 client APIs.
- Likely cause: normal Minecraft client API churn.
- Migration risk: High.
- Gameplay importance: High. Moe identity and dialogue are heavily visual.
- Suggested order: 10. Port after entity/data model compiles, then screenshot-test.

### SQLite shading and JDBC availability

- Evidence: `build.gradle` shadows and relocates `org.sqlite`; `BlockPartyDB` loads `org.sqlite.JDBC`.
- Likely cause: bundling SQLite driver inside the mod jar.
- Migration risk: Medium to high. Build tooling, module/classloader behavior, or relocation rules may change.
- Gameplay importance: High. NPC persistence depends on SQLite.
- Suggested order: 11. Verify DB connection in a minimal world before gameplay testing.

### Chunk forcing and teleport APIs

- Evidence: `ForcedChunk` calls `updateChunkForced`; `CellPhone` uses `ITeleporter`.
- Likely cause: current phone-call implementation relies on old Forge teleport/chunk APIs.
- Migration risk: Medium to high.
- Gameplay importance: Medium to high. Cell Phone behavior depends on finding and moving Moes.
- Suggested order: 12. Port after basic NPC DB lookup works.

### Resource reload listener APIs

- Evidence: `Scenes`, `Names`, `MoeTextures`, `MoeSounds`, and `BlockAliases` extend `SimpleJsonResourceReloadListener`.
- Likely cause: data-driven design on 1.19.4 APIs.
- Migration risk: Medium.
- Gameplay importance: High. Names, dialogue, textures, and sounds depend on reload.
- Suggested order: 13. Port early enough that spawn/dialogue tests use real resources.

## Unfinished Systems

### Chores, pranks, adventuring, and following AI

- Evidence: README describes these features; code has stats, sounds, inventory, and `isFollowing`, but no complete AI loop found.
- Likely cause: feature scaffolding exists after project hiatus, but behavior was not implemented.
- Migration risk: Low if preserved as-is; high if mixed into migration.
- Gameplay importance: High for final mod vision, low for current parity if not observable.
- Suggested order: 20. Defer until after port parity and bug fixes.

### Hunger, loneliness, stress, action, and sleep updates are empty

- Evidence: `Layer7.updateHungerState`, `updateLonelyState`, `updateStressState`, `updateActionState`, and `updateSleepState` are empty.
- Likely cause: placeholders for companion simulation.
- Migration risk: Low unless signatures/API calls change.
- Gameplay importance: Medium now; high for future behavior.
- Suggested order: 21. Preserve fields during port; implement later with tests.

### Scene library is only test content

- Evidence: only `test_dialogue.json` and `test_hide.json` exist under `data/block_party/scenes`.
- Likely cause: scene engine built before final content.
- Migration risk: Low.
- Gameplay importance: Medium. Dialogue system works, but content is minimal.
- Suggested order: 22. Add content after data-pack compatibility is stable.

### Markov action is unregistered and probably incorrect

- Evidence: `scene.actions.Markov` exists but is not registered in `SceneActions`; `chain` stores entries by probability rather than cumulative total.
- Likely cause: experimental action not wired into data-driven system.
- Migration risk: Low if unused; medium if port assumes it is supported.
- Gameplay importance: Low currently.
- Suggested order: 23. Decide whether to delete, register, or rewrite after scene tests.

### Senpai entity is not registered

- Evidence: `Senpai` extends `BlockPartyNPC`, but `CustomEntities` registers only `MOE` and `MOE_IN_HIDING`.
- Likely cause: planned NPC type never finished.
- Migration risk: Low.
- Gameplay importance: Low unless external data references it.
- Suggested order: 24. Defer; preserve class until intent is known.

### Sapling DB schema not created on world load

- Evidence: `BlockPartyDB.Saplings` exists; `onWorldLoad` creates Shrines, Locations, Gardens, and NPCs, but not Saplings.
- Likely cause: missed schema during persistence expansion.
- Migration risk: Medium if sapling block entities are exercised.
- Gameplay importance: Medium for sakura sapling persistence/worldgen.
- Suggested order: 14. Verify sapling gameplay before porting block entities.

### SNPCList base packet has no client handling

- Evidence: `SNPCList.handle` is empty; subclasses such as `SOpenController` carry actual behavior.
- Likely cause: base class exists for shared encoding, not standalone use.
- Migration risk: Low, but confusing.
- Gameplay importance: Low unless standalone packet is sent.
- Suggested order: 25. Leave alone during port; consider making abstract later.

### Render type registration is commented out

- Evidence: `CustomBlocks.registerRenderTypes` contains only commented `ItemBlockRenderTypes.setRenderLayer` calls.
- Likely cause: API changed or rendering setup was temporarily disabled.
- Migration risk: Medium. NeoForge/client rendering migration will revisit this area.
- Gameplay importance: Medium. Transparent/cutout blocks may render incorrectly.
- Suggested order: 15. Screenshot block rendering before and after client port.

### Creative tab is commented out

- Evidence: `BlockParty` has `TODO: Refactor creative tabs` around old creative-tab code.
- Likely cause: creative tab API changed in 1.19+.
- Migration risk: Medium.
- Gameplay importance: Medium. Item discoverability suffers, but core mechanics can still work.
- Suggested order: 26. Modernize after items register and gameplay parity works.

## Architectural Risks

### Seven-layer NPC inheritance stack

- Evidence: `BlockPartyNPC` inherits `Layer7`, which inherits six more behavior layers.
- Likely cause: incremental separation of NPC concerns.
- Migration risk: Medium. API changes must be threaded through a deep inheritance chain.
- Gameplay importance: High. Nearly every Moe behavior lives here.
- Suggested order: 16. Port without redesign first; refactor only after tests.

### Database sync from broad synced-data callback

- Evidence: `Layer5.onSyncedDataUpdated` updates the DB row whenever synced entity data changes and `hasRow()` is true.
- Likely cause: convenient persistence hook.
- Migration risk: Medium. Synched-data APIs and update frequency may change.
- Gameplay importance: High. Can affect performance, DB churn, and persistence correctness.
- Suggested order: 17. Instrument after port; replace with explicit saves later if needed.

### SQL builder is custom and partly manual

- Evidence: `Table`, `Row`, and `Column` generate SQL strings directly; some IDs and table names are interpolated.
- Likely cause: lightweight in-mod ORM.
- Migration risk: Medium.
- Gameplay importance: High. DB failures are world/companion failures.
- Suggested order: 18. Preserve schema first; add tests before changing query generation.

### Column dirty tracking uses reference comparison

- Evidence: `Column.set` checks `this.value != value`.
- Likely cause: simple object identity check.
- Migration risk: Medium. New APIs may produce new object instances for equivalent values.
- Gameplay importance: Medium to high. Can cause excess updates or missed logical equality.
- Suggested order: 19. Fix after DB no-op update behavior is tested.

### Personality generation is scattered

- Evidence: names and traits are assigned in `BlockPartyNPC`, `Moe`, `Layer4.finalizeSpawn`, `Layer4.setAdditionalBlockStateData`, and `CustomSpawnEggItem`.
- Likely cause: spawn paths grew organically.
- Migration risk: Medium.
- Gameplay importance: High. Moe identity must not drift during port.
- Suggested order: 27. Add personality golden tests first; consolidate later.

### Scene interruption semantics are implicit

- Evidence: `SceneManager.trigger` only accepts higher-priority triggers; `setAction(null)` calls `onComplete` on existing action.
- Likely cause: simple priority queue/state-machine implementation.
- Migration risk: Low to medium.
- Gameplay importance: Medium. Dialogue/behavior timing may feel wrong if changed.
- Suggested order: 28. Document with tests before refactoring scene manager.

### Hidden spots are position keyed and dimension-sensitive by storage context

- Evidence: `HidingSpots` stores `Map<BlockPos, Long>`, while public APIs pass `DimBlockPos`.
- Likely cause: one `SavedData` instance per server level makes dimension implicit.
- Migration risk: Medium. SavedData scoping may change during port.
- Gameplay importance: High. Hidden Moes must not reveal in the wrong place or vanish.
- Suggested order: 29. Test overworld/nether hidden positions before changing.

### Packet class naming is direction-confusing

- Evidence: client-bound messages extend `AbstractMessage.Server`; server-bound query base extends `AbstractMessage.Client`.
- Likely cause: names may refer to handler side rather than send direction.
- Migration risk: Medium during networking rewrite.
- Gameplay importance: Medium.
- Suggested order: 30. Preserve behavior during port; rename only after packet parity.

### Resource namespace remapping may surprise data packs

- Evidence: `Scenes.own` remaps `minecraft` namespace resources into `block_party`.
- Likely cause: convenience for un-namespaced/default scene references.
- Migration risk: Low to medium.
- Gameplay importance: Medium for external content compatibility.
- Suggested order: 31. Preserve during port; document for data-pack authors later.

## Safe Modernization Opportunities

These are lower-risk cleanup opportunities once parity tests pass. They should not be mixed with the initial port.

### Add focused parity tests

- Likely cause addressed: most risks are untested behavior.
- Migration risk: Low; reduces future risk.
- Gameplay importance: High.
- Suggested order: 0. Do this before fixing behavior.

### Make unfinished base packets/classes abstract or clearly named

- Targets: `SNPCList`, `SOpenController`, packet direction subclasses.
- Migration risk: Low after networking parity.
- Gameplay importance: Low.
- Suggested order: 32.

### Replace broad SQL string building with safer helpers

- Targets: `Table`, `Row`, `Column`.
- Migration risk: Medium, but safe after DB tests.
- Gameplay importance: Medium.
- Suggested order: 33.

### Centralize profile generation

- Targets: name, gender, blood type, dere, zodiac, block tag application.
- Migration risk: Medium.
- Gameplay importance: High.
- Suggested order: 34.

### Isolate loader/platform APIs behind small adapters

- Targets: networking, event registration, resource reload, chunk forcing, saved data.
- Migration risk: Medium initially, beneficial after first port.
- Gameplay importance: Medium.
- Suggested order: 35.

### Convert commented render setup to modern client registration

- Targets: cutout/cutout-mipped render layers for decorative blocks.
- Migration risk: Low to medium after client port.
- Gameplay importance: Medium.
- Suggested order: 36.

### Add diagnostics for resource loading

- Targets: scenes, names, textures, sounds, aliases.
- Migration risk: Low.
- Gameplay importance: Medium.
- Suggested order: 37.

### Document save schema and add migration versioning

- Targets: SQLite tables, world saved data, hidden spots.
- Migration risk: Medium.
- Gameplay importance: High for long-term worlds.
- Suggested order: 38.

### Decide fate of planned systems

- Targets: chores, pranks, adventuring, sleep, hunger, stress, Senpai, Markov.
- Migration risk: Low if done after parity.
- Gameplay importance: High for product direction.
- Suggested order: 39.

## Suggested Overall Order

1. Add parity tests and a manual golden-world checklist for spawn, dialogue, hide/reveal, save/load, Yearbook, Cell Phone, textures, and DB.
2. Fix or explicitly baseline the remaining dangerous bugs that can crash core behavior.
3. Port loader registration, resources, networking, entity APIs, rendering, and persistence with behavior unchanged.
4. Verify the behavior contract against the port.
5. Address architectural DB/sync/profile risks.
6. Modernize low-risk names, comments, diagnostics, and render setup.
7. Implement unfinished systems only after the port is stable.
