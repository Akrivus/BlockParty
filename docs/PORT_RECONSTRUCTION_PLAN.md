# NeoForge Port Reconstruction Plan

This plan maps the remaining NeoForge 1.21.4 spike work into vertical slices that can restore Forge 1.19.4 behavior incrementally. It uses the current spike surface in `src/neoforgeSpike` as the integration branch and the Forge 1.19.4 implementation in `src/main/java` as the behavior baseline.

The recommended rhythm for every slice is:

1. Add or port the narrow tests first when practical.
2. Reintroduce the smallest production surface needed for that behavior in `src/neoforgeSpike`.
3. Keep newly active code server-only unless the slice explicitly owns client behavior.
4. Update `docs/NEOFORGE_PORT_NOTES.md` and any flow docs touched by the slice.
5. Keep `compileJava` and `runGameTestServer` passing.

## Phase 1: Server Gameplay Parity

### Slice 1.1: Spawn From Block Parity

Status: Implemented in the NeoForge spike for the active thin Moe shell.

Player-facing behavior restored:

- Using `moe_spawn_egg` on a valid tagged block consumes the source block, spawns a Moe, preserves block identity, assigns owner, creates the NPC row, and respects invalid-block failure behavior.
- Survival spawn egg use consumes the item; creative spawn egg use preserves it. This is deliberate semantic drift from the frozen Forge 1.19.4 item source, which unconditionally shrinks the stack after a successful spawn.
- Persistent block-entity data is captured from `BlockEntity#getPersistentData()` into the visible Moe's `TileEntity` NBT when the source block has a block entity.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/items/CustomSpawnEggItem.java`
- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/BlockPartyNPC.java`
- `src/main/java/block_party/entities/abstraction/Layer2.java`
- `src/main/java/block_party/entities/abstraction/Layer3.java`
- `src/main/java/block_party/entities/abstraction/Layer4.java`
- `src/main/java/block_party/entities/abstraction/Layer5.java`
- `src/main/java/block_party/db/Recordable.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/registry/CustomTags.java`

NeoForge spike files involved:

- `src/main/java/block_party/items/CustomSpawnEggItem.java`
- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/registry/CustomTags.java`
- `src/main/java/block_party/gametest/MoeLifecycleGameTests.java`

Tests required before/after:

- Before: preserve current valid/invalid spawn GameTests.
- Done: block removal, item consumption mode, owner UUID, SQLite row, `NPCsByPlayer`, source `BlockState`, and block entity persistent data coverage.
- Done: invalid blocks do not change the world, consume items, spawn Moes, or insert rows.

Explicitly out of scope:

- Trait generation beyond fields owned by later profile slices.
- Rendering, spawn particles, sounds, dialogue triggers, and Cell Phone UI.
- Full Forge `Layer1`-`Layer7` behavior.

Recommended Codex prompt:

```text
Focus only on NeoForge spike spawn-from-block parity. Restore Forge 1.19.4 `CustomSpawnEggItem` behavior around valid tags, source block removal, item consumption rules, owner assignment, source block state, block entity persistent data capture, and NPC row creation. Do not port rendering, dialogue, AI, networking, or full profile traits. Update spike notes and keep all GameTests passing.
```

### Slice 1.2: Hide And Reveal Event Parity

Status: Implemented in the NeoForge spike for the active thin Moe/MoeInHiding shells.

Player-facing behavior restored:

- Moe hides as its original block, records `HidingSpots`, and returns when exposed.
- Timed reveal, break-start, break-complete, piston, and falling-block reveal hooks match Forge behavior where feasible.
- Persistent block-entity data captured by Slice 1.1 is restored to the hidden block and copied back to the revealed Moe.
- Semantic drift: NeoForge piston reveal checks the piston position and the block in front of the piston, while the frozen Forge 1.19.4 source checked the event position only.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/MoeInHiding.java`
- `src/main/java/block_party/entities/abstraction/Layer2.java`
- `src/main/java/block_party/entities/data/HidingSpots.java`
- `src/main/java/block_party/entities/goals/HideUntil.java`
- `src/main/java/block_party/scene/actions/Hide.java`
- `src/main/java/block_party/db/records/NPC.java`

NeoForge spike files involved:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/MoeInHiding.java`
- `src/main/java/block_party/entities/data/HidingSpots.java`
- `src/main/java/block_party/entities/goals/HideUntil.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/gametest/MoeLifecycleGameTests.java`

Tests required before/after:

- Before: current manual reveal, missing hidden spot, and hide record tests.
- Done: timed reveal, block break start/end, piston movement, falling block interaction, hidden NBT save/load, same-row identity restoration, and block-entity persistent data hide/reveal round-trip.
- Deferred: multi-dimension hidden spot coverage remains a later persistence/world-scope test because `HidingSpots` is still per-level and keyed by `BlockPos`.

Explicitly out of scope:

- Dialogue scene execution that triggers hide.
- Client rendering of hidden markers.
- Full block entity data restore if Slice 1.1 has not enabled it yet.

Recommended Codex prompt:

```text
Focus only on hide/reveal event parity in the NeoForge spike. Port `HidingSpots` event hooks, timed `HideUntil` reveal, and block disturbance reveal behavior using the thin Moe/MoeInHiding shells. Preserve database ID and HidingSpots semantics. Do not port dialogue, rendering, AI, or client UI. Add GameTests for each reveal path and update docs.
```

### Slice 1.3: Cell Phone Server Call And Chunk Loading Parity

Status: Implemented in the NeoForge spike for same-dimension loaded Moe shells and forced chunk cleanup.

Player-facing behavior restored:

- Calling a known owned visible Moe from the server flow can find a distant loaded Moe from the row position, move it near the requester, and set following mode.
- Forced chunk lifecycle is cleaned up after success/failure once a row-backed lookup queues a chunk.
- Missing-live-entity rows still fail safely after cleanup; full unloaded entity recovery remains blocked on broader entity persistence/chunk-load behavior.
- Cross-dimension calls fail safely until the NeoForge entity dimension-change/teleporter shell is intentionally ported.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/world/CellPhone.java`
- `src/main/java/block_party/world/chunk/ForcedChunk.java`
- `src/main/java/block_party/messages/CNPCTeleport.java`
- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/abstraction/Layer1.java`
- `src/main/java/block_party/entities/abstraction/Layer3.java`
- `src/main/java/block_party/db/records/NPC.java`

NeoForge spike files involved:

- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/gametest/CellPhoneServiceGameTests.java`

Tests required before/after:

- Before: current owner/non-owner/missing/dead/corrupt/hidden/unloaded call service tests.
- Done: same-dimension far call, forced chunk cleanup, failed lookup cleanup, final position near requester, and `following=true`.
- Deferred: cross-dimension call parity, because Forge `ITeleporter` behavior is not part of the active thin NeoForge entity shell.

Explicitly out of scope:

- Cell Phone screen.
- Client packet button wiring.
- Follow pathfinding or AI goals after teleport.

Recommended Codex prompt:

```text
Focus only on server-side Cell Phone call parity in the NeoForge spike. Extend `BlockPartyDB.callOwnedNpc` or a narrow server service to handle Forge-like distant/unloaded Moe lookup with minimal forced chunk handling and cleanup. Keep ownership and row checks authoritative. Do not add UI, rendering, dialogue, or follow AI. Add cleanup GameTests and update docs.
```

### Slice 1.4: Shrine, Garden, Location, And Block Entity Server Parity

Status: Implemented in the NeoForge spike for server-side data block shells and row/query persistence. Phase 1 is closed for the current thin server gameplay surface.

Player-facing behavior restored:

- Decorative/data blocks with block entities preserve owner/location row data server-side.
- Garden lantern, hanging scroll, paper lantern, sakura sapling, shimenawa, and shrine tablet block entity registry IDs are active. Wind chimes are intentionally disabled because the model asset is missing.
- Shrine, garden, location, and sapling SQLite tables are created; claim/update/delete behavior is active for the data-block tables.
- Shrine list queries are available server-side with Forge owner-or-dimension filtering.

Still stubbed in this slice:

- Shrine tablet lightning, sound, Moe creation, and `SShrineList` packet broadcast are deferred to later entity/network/client slices.
- Shimenawa is registered as a block entity shell, but its Forge NPC-row-from-persistent-data path is deferred until the fuller NPC/profile record port.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/blocks/**`
- `src/main/java/block_party/blocks/entity/**`
- `src/main/java/block_party/registry/CustomBlockEntities.java`
- `src/main/java/block_party/db/records/Shrine.java`
- `src/main/java/block_party/db/records/Garden.java`
- `src/main/java/block_party/db/records/Location.java`
- `src/main/java/block_party/db/records/Sapling.java`
- `src/main/java/block_party/db/ShrineLocations.java`
- `src/main/java/block_party/messages/SShrineList.java`

NeoForge spike files involved:

- `src/main/java/block_party/registry/CustomBlocks.java`
- `src/main/java/block_party/registry/CustomItems.java`
- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/db/records/NPC.java`
- new spike block entity registry and block entity shells.

Tests required before/after:

- Before: registry tests must keep current IDs stable.
- Done: block entity registry IDs, block placement shell creation, block entity NBT round-trip, SQLite table creation for shrine/garden/location/sapling, row insert/delete for claimed data blocks, locative condition/priority rows, and shrine list query behavior.
- Deferred: shrine tablet Moe creation because that Forge path depends on real Moe profile/entity side effects and shrine packet broadcasts outside this slice.

Explicitly out of scope:

- Client block entity renderers or special visual effects.
- Worldgen.
- Shrine list client UI until networking/client phases.

Recommended Codex prompt:

```text
Focus only on server-side block entity and location-record parity in the NeoForge spike. Reintroduce the minimum `CustomBlockEntities`, data block entities, and Shrine/Garden/Location/Sapling SQLite scaffolding needed for placement and persistence tests. Do not port client rendering, UI, worldgen, or shrine packets beyond server query methods. Preserve registry IDs and update docs.
```

## Phase 2: Entity/Profile Parity

### Slice 2.1: Full Moe Synched Data And NBT

Status: Implemented in the NeoForge spike as data-only Layer2-Layer5 parity for the thin Moe shell.

Player-facing behavior restored:

- Moe identity survives save/load with Forge-visible fields: owner, DB ID, actual block state, explicit visible block state, following, name, gender, blood type, dere, zodiac, emotion, profile/stat scalars, scale, corporeal state, home, and last-seen time.
- The minimal `NPCs` SQLite row can create/migrate/read/write those same fields, and row data can hydrate a Moe shell.
- Regular field setters do not implicitly open SQLite connections or write rows; row writes stay explicit through the DB service boundary.

Still stubbed in this slice:

- Block alias calculation from resources/tags is not active; visible block state is an explicit data field until the resource/profile slices own alias behavior.
- Real trait enums/resource-driven trait assignment are deferred to Slice 2.2.
- Inventory/menu persistence, movement/combat hooks, and following AI remain deferred to Slice 2.3. Slice 2.3 restored the non-client inventory NBT, movement attributes, combat delegation, and following state shell; real menus and follow AI remain deferred.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/BlockPartyNPC.java`
- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/abstraction/Layer1.java`
- `src/main/java/block_party/entities/abstraction/Layer2.java`
- `src/main/java/block_party/entities/abstraction/Layer3.java`
- `src/main/java/block_party/entities/abstraction/Layer4.java`
- `src/main/java/block_party/entities/abstraction/Layer5.java`
- `src/main/java/block_party/entities/abstraction/Layer6.java`
- `src/main/java/block_party/entities/abstraction/Layer7.java`

NeoForge spike files involved:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/MoeInHiding.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/gametest/EntityDataGameTests.java`

Tests required before/after:

- Before: current data shell spawn and NBT round-trips.
- Done: Layer2-Layer5 field round-trip, golden legacy NBT key presence, SQLite row create/migrate/update/apply coverage, and a setter/no-SQL-write guard.

Explicitly out of scope:

- AI pathfinding, dialogue scene execution, rendering, UI, and network packets.

Recommended Codex prompt:

```text
Focus only on restoring full Moe/MoeInHiding synced data and NBT parity in the NeoForge spike. Port Forge Layer2-Layer5 state fields conservatively into the spike shell without enabling AI, dialogue, rendering, UI, or packets. Preserve legacy NBT keys and DB IDs. Add field-level GameTests and update docs.
```

### Slice 2.2: Profile Generation And Trait Tags

Status: Implemented in the NeoForge spike for resource-loaded names, Forge trait tag constants, block-tag profile assignment, and string trait fallback parsing.

Player-facing behavior restored:

- Moes can receive non-default names from `data/*/moes/names` through a real server reload listener, and claimed names are recorded in `BlockPartyDB`.
- Forge block trait tag IDs are available in the spike for pronouns, blood type, dere, zodiac, wings, glow, cat features, festive textures, and volume ignoring.
- Source block profile assignment applies populated baseline tags such as male pronouns, wings, glow, cat features, and volume ignoring.
- Blood type, dere, zodiac, gender, and emotion string parsing preserves Forge fallback defaults for invalid values.

Baseline resource note:

- The current Forge 1.19.4 bundled blood type, dere, and zodiac tag JSON files are present but mostly empty. The spike supports those tag IDs and assignment branches, while GameTests cover non-empty representative tags from the frozen resources plus parse fallback behavior.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/abstraction/Layer4.java`
- `src/main/java/block_party/scene/traits/*.java`
- `src/main/java/block_party/scene/ITrait.java`
- `src/main/java/block_party/registry/resources/Names.java`
- `src/main/java/block_party/registry/CustomTags.java`
- `src/main/java/block_party/items/CustomSpawnEggItem.java`

NeoForge spike files involved:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/registry/CustomResources.java`
- `src/main/java/block_party/registry/CustomTags.java`
- `src/main/java/block_party/registry/resources/CountingJsonReloadListener.java`
- `src/main/java/block_party/registry/resources/MoeNamesReloadListener.java`

Tests required before/after:

- Before: resource parse and spawn identity tests.
- Done: representative tag membership tests, block-derived profile flag/gender tests, name assignment/claim tests, trait parse fallback tests, and prior Slice 2.1 SQLite/NBT persistence of profile fields.

Explicitly out of scope:

- Rendering layers that visually consume traits.
- Dialogue filters/actions that read traits.
- Chores/pranks/adventuring stats updates.

Recommended Codex prompt:

```text
Focus only on Moe profile generation parity in the NeoForge spike. Port trait enums, name loading/use, block trait tags, and source-block profile assignment. Preserve Forge 1.19.4 values and fallback parsing. Do not port rendering, dialogue, AI, or UI. Add tests for representative tag-derived traits and persistence.
```

### Slice 2.3: Movement, Combat, Inventory, And Follow Shells

Status: Implemented in the NeoForge spike as non-client movement/combat/inventory/follow shell parity.

Player-facing behavior restored:

- Moe is now backed by a `PathfinderMob` shell with Forge baseline attributes for health, attack damage/speed, movement speed, flying speed, and follow range.
- Moe preserves Forge-style home helpers, non-despawn behavior, non-recursive combat delegation, 36-slot inventory NBT, inventory-derived slouch recalculation, and following flag semantics.
- Server-side Cell Phone calls still move a loaded visible owned Moe near the requester and set `following=true`.

Semantic drift / API note:

- Minecraft 1.21.4 `LivingEntity#getScale()` is final, so the spike keeps the persisted Forge `Scale` NBT/SQLite field but exposes it internally as `getMoeScale()` / `setMoeScale()`.
- The spike registers Moe as a creature mob for attribute support. Real goals/pathfinding remain intentionally absent beyond vanilla mob foundations and Forge-compatible path malus/home shell data.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/abstraction/Layer1.java`
- `src/main/java/block_party/entities/abstraction/Layer3.java`
- `src/main/java/block_party/entities/abstraction/Layer6.java`
- `src/main/java/block_party/entities/abstraction/Layer7.java`
- `src/main/java/block_party/utils/CustomDamageSource.java`

NeoForge spike files involved:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/registry/CustomEntities.java`
- `src/main/java/block_party/gametest/EntityDataGameTests.java`
- `src/main/java/block_party/gametest/CellPhoneServiceGameTests.java`

Tests required before/after:

- Before: following flag tests.
- Done: attribute registration smoke test, combat super-call regression, home/helper movement test, inventory NBT round-trip, inventory slouch recalculation test, and existing call service following coverage.

Explicitly out of scope:

- Real follow AI/pathfinding.
- Chores, pranks, sleep, hunger, stress, and action update implementations.
- Client inventory screens unless explicitly needed later.

Recommended Codex prompt:

```text
Focus only on non-client Moe movement/combat/inventory/follow state shells in the NeoForge spike. Port attributes, safe combat hooks, home/teleport helpers, and inventory NBT foundations from Forge without adding full AI/pathfinding or UI. Keep Cell Phone service tests passing and add targeted regressions.
```

### Slice 2.4: Entity Physical/Profile Closeout

Status: Implemented in the NeoForge spike as Phase 2 closeout parity for server-side physical/profile data.

Player-facing behavior restored:

- Source block assignment now resolves visible block aliases from `data/*/moes/aliases`, preserving the actual/source block separately from the visible/alias block.
- Moe scale is recalculated from the source block occlusion volume, with Forge's empty/tiny-shape fallback and `moe/ignores_volume` override.
- New Moe shells randomize default blood type with Forge's weighted `AB/B/A/O` distribution before block tags may override it.
- Name assignment now chooses randomly from unclaimed names rather than always taking the first unclaimed entry.
- Layer7 timer NBT fields are preserved: `TimeUntilHungry`, `TimeUntilLonely`, `TimeUntilStress`, and `TimeSinceSleep`.

Semantic drift / API note:

- Volume calculation uses Minecraft 1.21.4's no-arg `BlockState#getOcclusionShape()` API. This preserves the Forge intent for registered spike blocks, but some future custom block shapes may need revisiting when their real block classes return.
- `BlockAliasesReloadListener` fails closed for malformed alias entries while the existing counting listener still catches bundled JSON parse failures.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/abstraction/Layer2.java`
- `src/main/java/block_party/entities/abstraction/Layer4.java`
- `src/main/java/block_party/entities/abstraction/Layer7.java`
- `src/main/java/block_party/registry/resources/BlockAliases.java`
- `src/main/java/block_party/scene/traits/BloodType.java`
- `src/main/java/block_party/scene/traits/Gender.java`

NeoForge spike files involved:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/registry/CustomResources.java`
- `src/main/java/block_party/registry/resources/BlockAliasesReloadListener.java`
- `src/main/java/block_party/gametest/EntityDataGameTests.java`
- `src/main/java/block_party/gametest/ResourceGameTests.java`

Tests required before/after:

- Done: block alias reload listener smoke test, visible alias/actual source block test, volume scale test, empty-shape fallback scale test, and Layer7 timer NBT round-trip/key coverage.

Explicitly out of scope:

- Client rendering/model scale consumption.
- Real custom block shape parity beyond currently registered spike blocks.
- Dialogue, scene triggers, animation, sounds, and client/UI behavior.

## Phase 3: Networking Parity

### Slice 3.1: NPC List, Detail, Remove, And Call Payloads

Status: Implemented in the NeoForge spike for typed server-authoritative controller payloads.

Player-facing behavior restored:

- Yearbook/Cell Phone server requests can list owned NPCs, load details, remove/de-list entries, and call a Moe through typed NeoForge payloads.
- Clientbound controller-open payloads can carry Cell Phone/Yearbook owned-ID lists, selected Yearbook ID, and hand context, but client screen opening remains intentionally disabled.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/registry/CustomMessenger.java`
- `src/main/java/block_party/messages/CNPCRequest.java`
- `src/main/java/block_party/messages/CNPCRemove.java`
- `src/main/java/block_party/messages/CNPCTeleport.java`
- `src/main/java/block_party/messages/SNPCList.java`
- `src/main/java/block_party/messages/SNPCResponse.java`
- `src/main/java/block_party/messages/SOpenCellPhone.java`
- `src/main/java/block_party/messages/SOpenYearbook.java`
- `src/main/java/block_party/messages/SOpenController.java`

NeoForge spike files involved:

- `src/main/java/block_party/network/CustomMessenger.java`
- `src/main/java/block_party/network/payload/**`
- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/gametest/NetworkPayloadGameTests.java`
- `src/main/java/block_party/gametest/CellPhoneServiceGameTests.java`

Tests required before/after:

- Before: current payload codec and handler service tests.
- Done: call request/response payload codec tests, controller-open payload codec tests, server response tests for `callOwnedNpc`, failure payload shape tests, and owned-list controller payload tests.

Explicitly out of scope:

- Actual client screens.
- Dialogue packets.
- Shrine packets.
- Client-side state rendering.

Recommended Codex prompt:

```text
Focus only on completing NPC controller payload parity in the NeoForge spike. Add typed payloads for Cell Phone call and any missing Yearbook/Cell Phone open/detail/remove flows, routing all authority through `BlockPartyDB` services. Do not add screens, rendering, dialogue, or shrine packets. Add codec and handler tests.
```

### Slice 3.2: Dialogue Networking

Status: Implemented in the NeoForge spike for typed dialogue payload contracts and server-side response/close state.

Player-facing behavior restored:

- Dialogue open payloads can carry row-backed NPC detail plus Forge-shaped dialogue data to the client.
- Dialogue responses return to the server through a typed payload and update a live, owned Moe's minimal dialogue response state.
- Dialogue close requests return to the server through a typed payload and clear a live, owned Moe's minimal dialogue state.

Semantic drift / API note:

- NeoForge 1.21.4 does not allow the same custom payload ID to be registered in both directions. The active spike registers `block_party:dialogue_close` as the serverbound close request because server state clearing is the tested behavior in this slice. Client-side screen closing remains deferred to the DialogueScreen/client UI slice.
- Dialogue payloads preserve Forge NBT field names for dialogue data, but they use the spike's minimal `Dialogue`, `Speaker`, and `Response` records until full scene actions/filters are ported.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/messages/SOpenDialogue.java`
- `src/main/java/block_party/messages/SCloseDialogue.java`
- `src/main/java/block_party/messages/CDialogueRespond.java`
- `src/main/java/block_party/messages/CDialogueClose.java`
- `src/main/java/block_party/messages/CNPCQuery.java`
- `src/main/java/block_party/scene/Dialogue.java`
- `src/main/java/block_party/scene/Response.java`
- `src/main/java/block_party/scene/Speaker.java`
- `src/main/java/block_party/scene/actions/SendDialogue.java`
- `src/main/java/block_party/scene/actions/SendResponse.java`

NeoForge spike files involved:

- `src/main/java/block_party/network/CustomMessenger.java`
- new dialogue payload files under `src/main/java/block_party/network/payload`
- future scene/dialogue spike files under `src/main/java/block_party/scene`

Tests required before/after:

- Before: pure dialogue NBT/codec contract tests from Forge baseline if available.
- Done: payload encode/decode, server handler rejects non-owned/missing NPCs, response sets server-side dialogue state, and close clears server-side dialogue state.

Explicitly out of scope:

- Rendering the dialogue screen.
- Full scene action/filter port if not already active.
- Markov behavior changes.

Recommended Codex prompt:

```text
Focus only on NeoForge dialogue packet parity. Add typed payloads for opening, closing, and responding to dialogue, preserving Forge payload fields and server authority. Wire handlers to the minimal scene/dialogue state already present or narrow stubs. Do not port DialogueScreen rendering or unrelated controller packets. Add codec and server-handler tests.
```

### Slice 3.3: Shrine And Location Sync Packets

Status: Implemented in the NeoForge spike for typed shrine-list request/response payloads and Forge owner-or-dimension filtering.

Player-facing behavior restored:

- Server can send shrine/location lists to clients under the same owner/dimension rules as Forge.
- The `block_party:shrine_list` response carries only shrine `BlockPos` values, matching the frozen Forge `SShrineList` packet shape.
- Shrine list selection now follows Forge parity: a row is included when the requesting player owns it or when the row is in the requested dimension.

Still stubbed in this slice:

- Client-side shrine location storage is still a no-op until the client/UI/rendering phase owns it.
- Shrine tablet visual/audio/Moe-spawn side effects remain deferred; this slice only restores network sync and the server query rule.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/messages/SShrineList.java`
- `src/main/java/block_party/db/records/Shrine.java`
- `src/main/java/block_party/db/ShrineLocations.java`
- `src/main/java/block_party/client/ShrineLocation.java`
- `src/main/java/block_party/blocks/entity/ShrineTabletBlockEntity.java`

NeoForge spike files involved:

- `src/main/java/block_party/network/CustomMessenger.java`
- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/network/payload/ShrineListRequestPayload.java`
- `src/main/java/block_party/network/payload/ShrineListPayload.java`
- `src/main/java/block_party/gametest/NetworkPayloadGameTests.java`
- `src/main/java/block_party/gametest/BlockEntityGameTests.java`

Tests required before/after:

- Before: server-side shrine/location row tests from Phase 1.4.
- Done: payload codec, player-login send hook, request service response, Forge owner-or-dimension filtering, and empty-list behavior.
- Deferred: client-side storage/rendering.

Explicitly out of scope:

- Japan skybox/client rendering.
- Shrine block visual effects and sounds unless Phase 1.4 explicitly enabled them.

Recommended Codex prompt:

```text
Focus only on shrine/location sync packet parity in the NeoForge spike. Add typed payloads and server handlers for the Forge `SShrineList` behavior using the ported shrine/location records. Do not add client rendering, skybox effects, or screens. Add codec and filtering tests.
```

Phase 3 closeout scope check:

- In scope and implemented: NPC list/detail/remove/call payloads, controller-open data shape, dialogue open/respond/close contracts, and shrine-list sync.
- Remaining packet references from the frozen Forge branch are now either represented by these typed payloads or intentionally owned by later phases. `CRemovePage` is represented in Phase 4.3 by the active `NpcRemoveRequestPayload` de-list flow; ripped-page item creation remains deferred with fuller Yearbook item behavior. Client-side dialogue close behavior is active through the DialogueScreen serverbound close payload. Shrine tablet row-change broadcasts can be wired when shrine tablet side effects return.

## Phase 4: Client UI/Rendering Parity

### Slice 4.1: Renderer And Model Layer Parity

Status: Implemented in the NeoForge spike as a first client render path for Moe and MoeInHiding.

Player-facing behavior restored:

- Moe renderer registration is active on the client distribution for `block_party:moe`.
- Moe uses the legacy `block_party:moe` model layer ID and a NeoForge/Minecraft 1.21.4 render-state-backed model adapted from the Forge Moe model geometry.
- Moe render state carries visible block texture fallback, scale, slouch, health/nameplate data, wing/cat/glow flags, and emote/glow/special layer inputs.
- Moe texture fallback resolves to `textures/moe/<visible_block>.png`, preserving the Forge fallback path convention.
- MoeInHiding has a registered non-rendering placeholder renderer so hidden markers do not crash client rendering before their visual presentation is intentionally restored.

Semantic drift / API note:

- Minecraft 1.21.4 uses render-state extraction (`LivingEntityRenderState`/`HumanoidRenderState`) instead of the Forge 1.19.4 entity-direct renderer callbacks. The NeoForge spike copies Moe fields into `MoeRenderState` before rendering.
- Full Forge animation objects, armor models, and screenshot-verified render-layer polish remain deferred. Moe texture override reload hooks return in Slice 4.4; the active layer path restores the data flow and fallback rendering surface without porting unrelated client systems.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/client/BlockPartyRenderers.java`
- `src/main/java/block_party/client/renderers/MoeRenderer.java`
- `src/main/java/block_party/client/renderers/MoeInHidingRenderer.java`
- `src/main/java/block_party/client/model/MoeModel.java`
- `src/main/java/block_party/client/renderers/layers/*.java`
- `src/main/java/block_party/client/renderers/layers/special/*.java`
- `src/main/java/block_party/client/animation/**`
- `src/main/java/block_party/registry/resources/MoeTextures.java`

NeoForge spike files involved:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/MoeInHiding.java`
- `src/main/java/block_party/registry/CustomEntities.java`
- `src/main/java/block_party/client/BlockPartyRenderers.java`
- `src/main/java/block_party/client/model/MoeModel.java`
- `src/main/java/block_party/client/renderers/MoeRenderer.java`
- `src/main/java/block_party/client/renderers/MoeInHidingRenderer.java`
- `src/main/java/block_party/client/renderers/layers/**`
- `src/main/java/block_party/client/renderers/state/MoeRenderState.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`

Tests required before/after:

- Before: entity/profile fields that renderers consume are stable.
- Done: compile/client launch smoke, dedicated-server GameTest smoke, texture fallback check, model layer/entity renderer registration wiring.
- Deferred: screenshot matrix for representative Moe block states, texture override reload listener parity, full animation and armor render paths.

Explicitly out of scope:

- Screens and controller UI.
- New animations beyond Forge parity.
- Fixing pre-existing texture lookup bugs unless separately baselined.

Recommended Codex prompt:

```text
Focus only on Moe renderer/model layer parity in the NeoForge spike. Port renderer registration, MoeModel, MoeRenderer, MoeInHidingRenderer, and render layers against the current entity data surface. Do not add screens, packets, or new rendering features. Verify with client launch/screenshots and document any known texture parity gaps.
```

### Slice 4.2: Dialogue Screen Parity

Player-facing behavior restored:

- Dialogue UI opens, shows speaker preview/text/responses, sends responses, plays UI sounds, and closes correctly.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/client/screens/DialogueScreen.java`
- `src/main/java/block_party/client/screens/AbstractScreen.java`
- `src/main/java/block_party/client/screens/state/DialogueViewModel.java`
- `src/main/java/block_party/client/screens/widget/RespondIconButton.java`
- `src/main/java/block_party/client/screens/widget/RespondTextButton.java`
- `src/main/java/block_party/messages/SOpenDialogue.java`
- `src/main/java/block_party/messages/CDialogueRespond.java`
- `src/main/java/block_party/messages/CDialogueClose.java`

NeoForge spike files involved:

- dialogue payloads from Phase 3.2
- `src/main/java/block_party/client/screens/DialogueScreen.java`
- `src/main/java/block_party/client/ClientPayloadHandler.java`
- `src/main/java/block_party/network/CustomMessenger.java`
- `build.gradle`

Tests required before/after:

- Before: dialogue payload and server response tests.
- Done: `compileJava`, dedicated-server GameTest smoke, and client-launch smoke through resource load/integrated-world entry.
- Remaining: manual in-game dialogue open/response/close, keyboard/ESC behavior, text reveal behavior, and screenshot checks at common sizes.

Active NeoForge behavior:

- `block_party:dialogue_open` now opens a client-only `DialogueScreen` through `ClientPayloadHandler`.
- The screen renders the legacy dialogue panel texture, wraps dialogue text, shows a Moe preview built from the row-backed NPC detail payload, exposes tooltip/text response modes, and sends `DialogueRespondPayload` or `DialogueClosePayload` back to the server.
- The spike resource sync now copies only the legacy `assets/block_party/textures/gui/dialogue.png` needed by this screen while the rest of the GUI texture folder remains out of scope.

Semantic drift/API notes:

- The Forge screen/view-model/widget classes were collapsed into one narrow NeoForge screen rather than ported as a broader screen framework. This preserves the player-facing dialogue surface without introducing Yearbook/Cell Phone abstractions early.
- NeoForge 1.21.4 still registers `block_party:dialogue_close` only serverbound; close behavior is client screen -> server close payload, not a bidirectional same-ID packet.
- Sounds are resolved only when their legacy `SoundEvent` IDs are present in the active spike registry/resource surface.

Explicitly out of scope:

- Yearbook and Cell Phone screens.
- Scene authoring changes.
- Renderer refactors outside what the screen preview needs.

Recommended Codex prompt:

```text
Focus only on DialogueScreen parity in the NeoForge spike. Port the Forge dialogue screen, response widgets, and view model against the typed dialogue payloads. Do not port Yearbook, Cell Phone, shrine UI, or unrelated rendering. Verify manual dialogue flow and update docs.
```

### Slice 4.3: Yearbook And Cell Phone Screen Parity

Status: Implemented in the NeoForge spike as first-pass client controller screens and item open behavior.

Player-facing behavior restored:

- Yearbook and Cell Phone open, show known NPCs, request detail rows, remove/de-list entries, and call a Moe from the Cell Phone.
- `block_party:yearbook` opens a Yearbook controller payload on use, and opens to an owned Moe when used on that Moe.
- `block_party:cell_phone` opens a Cell Phone controller payload on use.
- Client controller payloads now open screens, request row details, route detail/call/list responses to the active screen, and keep authority on the existing server payload/services.
- The Yearbook page item keeps its legacy one-stack behavior and tooltip name extraction from legacy custom `NPC` data when present.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/items/YearbookItem.java`
- `src/main/java/block_party/items/CellPhoneItem.java`
- `src/main/java/block_party/items/YearbookPageItem.java`
- `src/main/java/block_party/client/screens/ControllerScreen.java`
- `src/main/java/block_party/client/screens/YearbookScreen.java`
- `src/main/java/block_party/client/screens/CellPhoneScreen.java`
- `src/main/java/block_party/client/screens/state/ControllerViewModel.java`
- `src/main/java/block_party/client/screens/state/YearbookViewModel.java`
- `src/main/java/block_party/client/screens/state/CellPhoneViewModel.java`
- `src/main/java/block_party/messages/SOpenYearbook.java`
- `src/main/java/block_party/messages/SOpenCellPhone.java`
- `src/main/java/block_party/messages/SNPCList.java`
- `src/main/java/block_party/messages/SNPCResponse.java`
- `src/main/java/block_party/messages/CNPCRequest.java`
- `src/main/java/block_party/messages/CNPCRemove.java`
- `src/main/java/block_party/messages/CNPCTeleport.java`

NeoForge spike files involved:

- `src/main/java/block_party/network/CustomMessenger.java`
- `src/main/java/block_party/network/payload/**`
- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/items/YearbookItem.java`
- `src/main/java/block_party/items/CellPhoneItem.java`
- `src/main/java/block_party/items/YearbookPageItem.java`
- `src/main/java/block_party/client/ClientPayloadHandler.java`
- `src/main/java/block_party/client/screens/ControllerScreen.java`
- `src/main/java/block_party/client/screens/YearbookScreen.java`
- `src/main/java/block_party/client/screens/CellPhoneScreen.java`
- `src/main/java/block_party/registry/CustomItems.java`
- `build.gradle`

Tests required before/after:

- Before: list/detail/remove/call payload tests and server service tests.
- Done: `compileJava`, dedicated-server GameTest smoke, item/payload compile coverage, and existing controller-open/list/detail/remove/call service GameTests.
- Remaining: manual Yearbook open with zero/one/many NPCs, owned/unowned access behavior, remove flow, Cell Phone call flow, and screenshot checks.

Semantic drift/API notes:

- Minecraft 1.21.4 item `use` returns `InteractionResult`, not Forge 1.19.4 `InteractionResultHolder`.
- The active `NpcDetailPayload` is still intentionally thin. It does not carry full Forge dead/estranged relationship data or complete stat columns to the client, so the Yearbook first pass renders available row-backed identity/profile/block/hiding fields and allows explicit de-listing instead of restricting removal only to dead/estranged pages.
- Cell Phone contact filtering uses active row detail safety (`found` and not hidden) and keeps cross-dimension/unloaded failures server-authoritative through `NpcCallPayload`.

Explicitly out of scope:

- Dialogue UI.
- Follow pathfinding after call.
- Full profile stat editing unless Forge UI already exposes it.

Recommended Codex prompt:

```text
Focus only on Yearbook and Cell Phone screen parity in the NeoForge spike. Port controller view models/screens and item open behavior against the existing typed NPC payloads and call service. Do not port dialogue, rendering refactors, or follow AI. Add manual verification notes and keep server tests passing.
```

### Slice 4.4: Particles, Sounds, And Client Resource Hooks

Status: Implemented in the NeoForge spike for first-pass particle providers, client Moe texture reload hooks, and Moe sound override reload hooks.

Player-facing behavior restored:

- Custom particles render, sound overrides play, UI sounds work, and client reload listeners populate visual/audio maps.
- `firefly`, `ginkgo`, `sakura`, and `white_sakura` particle providers register on the NeoForge client mod bus.
- `MoeTextures` now supports client reload override data in addition to the Forge fallback path convention restored in Slice 4.1.
- The legacy `moes/sounds` reload path is active and copies the `minecraft` namespace sound override data used by the frozen Forge branch.
- The Cell Phone button subtitle key is restored for the active UI sound path.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/client/particle/*.java`
- `src/main/java/block_party/registry/CustomParticles.java`
- `src/main/java/block_party/registry/CustomSounds.java`
- `src/main/java/block_party/registry/resources/MoeSounds.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/registry/resources/BlockAliases.java`
- `src/main/resources/assets/block_party/sounds.json`
- `src/main/resources/assets/block_party/particles/**`

NeoForge spike files involved:

- `src/main/java/block_party/registry/CustomParticles.java`
- `src/main/java/block_party/registry/CustomSounds.java`
- `src/main/java/block_party/registry/CustomResources.java`
- `src/main/java/block_party/client/BlockPartyClientEvents.java`
- `src/main/java/block_party/client/particle/**`
- `src/main/java/block_party/registry/resources/MoeTextureReloadListener.java`
- `src/main/java/block_party/registry/resources/MoeSoundsReloadListener.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/gametest/ResourceGameTests.java`
- `build.gradle`

Tests required before/after:

- Before: registry/resource parse smoke tests.
- Done: `compileJava`, dedicated-server GameTest smoke, Moe sound override reload assertion, and client launch smoke through resource reload/particle atlas/integrated-world entry.
- Remaining: in-world visual particle spawn checks, manual sound matrix, and screenshot/audio capture for representative client effects.

Semantic drift/API notes:

- NeoForge 1.21.4 uses `AddClientReloadListenersEvent` rather than the Forge 1.19.4 `RegisterClientReloadListenersEvent`.
- The frozen Forge `data/minecraft/moes/sounds/bell.json` uses `block_party:moe/bell/step`; the spike resolves that to the registered `block_party:moe.bell.step` sound event so the existing content can drive the active registry IDs.
- Existing content warnings for `block_party:silence`, generic `block_party:moe.step`, and the invalid audio path `sounds/moe/yes6 .ogg` remain documented resource debt rather than behavior changed in this slice.

Explicitly out of scope:

- Dialogue screen layout.
- New sound events.
- Fixing audio balance/content.

Recommended Codex prompt:

```text
Focus only on client particles, sound overrides, texture aliases, and client resource hooks in the NeoForge spike. Port provider registration and reload listeners needed by existing render/UI behavior. Do not port screens or gameplay systems. Verify client launch and a manual sound/particle checklist.
```

## Phase 5: Resource/Content Parity

### Slice 5.1: Real Scene Resource Loading And Execution

Player-facing behavior restored:

- Data-driven scenes load from `data/*/scenes`, right-click/left-click triggers work, filters run, and actions such as dialogue, hide, cookies, counters, and end execute.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/registry/resources/Scenes.java`
- `src/main/java/block_party/scene/**`
- `src/main/java/block_party/scene/actions/**`
- `src/main/java/block_party/scene/observations/**`
- `src/main/java/block_party/registry/SceneActions.java`
- `src/main/java/block_party/registry/SceneFilters.java`
- `src/main/java/block_party/registry/SceneCodecRegistries.java`
- `src/main/resources/data/block_party/scenes/**`

NeoForge spike files involved:

- `src/main/java/block_party/registry/SceneActions.java`
- `src/main/java/block_party/registry/SceneFilters.java`
- `src/main/java/block_party/registry/CustomResources.java`
- `src/main/java/block_party/registry/resources/ScenesReloadListener.java`
- `src/main/java/block_party/scene/Scene.java`
- `src/main/java/block_party/scene/SceneManager.java`
- `src/main/java/block_party/scene/SceneTrigger.java`
- `src/main/java/block_party/scene/actions/**`
- `src/main/java/block_party/scene/SceneVariables.java`
- `src/main/java/block_party/scene/data/**`
- `src/main/java/block_party/registry/resources/CountingJsonReloadListener.java`

Tests required before/after:

- Before: inert JSON parse tests.
- After: scene parse fixtures, trigger priority tests, right-click dialogue scene, left-click hide scene, cookie/counter variable mutations, and failure behavior for malformed scenes.

Current NeoForge status:

- Implemented in Slice 5.1. `ScenesReloadListener` parses bundled `data/*/scenes` resources into trigger-indexed scenes while `CountingJsonReloadListener` remains as a syntax/count smoke check. `Moe` owns a Forge-shaped `SceneManager`, advances it on server ticks, and triggers right-click, shift-right-click, left-click, shift-left-click, hurt, and attack scene priorities.
- Active actions: `send_dialogue`, `send_response`, `hide`, `cookie`, `counter`, and `end`. Active filters: `always`, `never`, `has_cookie`, and a minimal numeric `counter` comparison filter. Other registered legacy action/filter IDs remain registry-compatible stubs until their owning behavior is selected.
- GameTests now cover bundled right-click dialogue, nested response action execution, left-click hide through the existing hide lifecycle, trigger priority rejection for lower-priority scenes, cookie/counter mutation through `SceneVariables`, loaded scene counts, and malformed scene root failure.
- Semantic note: Forge response text could fall back to translated text or be absent; the current NeoForge `Dialogue` record snapshots responses with `Map.copyOf`, so absent response text is represented as an empty string to avoid nullable payload entries. Player-facing icons and action behavior are preserved; richer translated fallback text remains deferred.

Explicitly out of scope:

- Dialogue screen rendering unless Phase 4.2 is active.
- Markov registration/fixes unless intentionally selected.
- New scene content.

Recommended Codex prompt:

```text
Focus only on real scene resource loading and server-side scene execution in the NeoForge spike. Replace the inert scene counting listener with Forge-parity parsing for actions/filters/triggers and wire minimal entity triggers. Do not port client screens or new scene content. Add tests for `test_dialogue`, `test_hide`, cookies, and counters.
```

### Slice 5.2: Names, Aliases, Textures, And Sounds Data Parity

Player-facing behavior restored:

- Resource packs can customize Moe names, aliases, textures, and sounds as in Forge.
- Note: Phases 2.2 and 4.4 already restored the first active name/alias/texture/sound reload hooks. Slice 5.2 should close remaining data-pack override parity, malformed-resource behavior, and richer fixture coverage rather than re-port those hooks from scratch.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/registry/resources/Names.java`
- `src/main/java/block_party/registry/resources/BlockAliases.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/registry/resources/MoeSounds.java`
- `src/main/java/block_party/utils/JsonUtils.java`
- `src/main/resources/data/*/moes/**`
- `src/main/resources/assets/*/textures/moe/**`

NeoForge spike files involved:

- `src/main/java/block_party/registry/CustomResources.java`
- `src/main/java/block_party/registry/resources/CountingJsonReloadListener.java`
- `src/main/java/block_party/registry/resources/BlockAliasesReloadListener.java`
- `src/main/java/block_party/registry/resources/MoeNamesReloadListener.java`
- `src/main/java/block_party/registry/resources/MoeSoundsReloadListener.java`
- `src/main/java/block_party/registry/resources/MoeTextureReloadListener.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/entities/Moe.java`
- `build.gradle` `syncNeoForgeSpikeResources`

Tests required before/after:

- Before: inert data parse tests.
- After: name list load, alias map load, texture pattern matching, sound fallback/override, data-pack override smoke, and malformed resource behavior.

Current NeoForge status:

- Implemented in Slice 5.2. The generated spike resources now include the frozen `assets/minecraft/textures/moe/**` metadata fixtures in addition to `data/block_party/moes/**` and `data/minecraft/moes/**`.
- `MoeNamesReloadListener`, `BlockAliasesReloadListener`, `MoeSoundsReloadListener`, and `MoeTextureReloadListener` expose focused parser helpers used by GameTests. Optional malformed metadata fails closed in the active loaders.
- `MoeTextureReloadListener` accepts both the legacy data-driven path `moes/textures` and the bundled asset metadata path `textures/moe`, preserving the frozen branch's shipped texture metadata without enabling unrelated renderer work.
- GameTests cover bundled name loading, alias resolution, sound override resolution, texture pattern parsing/matching, synthetic override-shaped JSON for names/aliases/textures/sounds, and malformed optional metadata behavior.
- Remaining caveat: this slice verifies override-shaped parsing and bundled resource availability through GameTests. A live client resource-pack reload smoke remains useful once the renderer screenshot matrix is revisited.

Explicitly out of scope:

- Renderer visuals except to consume loaded texture IDs.
- Profile generation if Slice 2.2 has not enabled it.

Recommended Codex prompt:

```text
Focus only on names, aliases, Moe texture metadata, and Moe sound metadata resource parity in the NeoForge spike. Port the Forge reload listeners and JSON parsing behavior. Do not port rendering/UI beyond exposing loaded data to existing consumers. Add resource and data-pack override tests.
```

### Slice 5.3: Recipes, Loot, Worldgen, And Decorative Blocks

Player-facing behavior restored:

- Recipes, loot tables, worldgen, decorative block behavior, tree growers, vines/leaves, and transparent block rendering rules are available again.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/registry/CustomWorldGen.java`
- `src/main/java/block_party/blocks/**`
- `src/main/java/block_party/blocks/grower/**`
- `src/main/resources/data/block_party/recipes/**`
- `src/main/resources/data/block_party/loot_tables/**`
- `src/main/resources/data/block_party/worldgen/**`
- `src/main/resources/assets/block_party/blockstates/**`
- `src/main/resources/assets/block_party/models/**`

NeoForge spike files involved:

- `src/main/java/block_party/registry/CustomBlocks.java`
- `src/main/java/block_party/registry/CustomItems.java`
- `src/main/java/block_party/registry/CustomWorldGen.java`
- `src/main/java/block_party/blocks/SakuraBlossomsBlock.java`
- `src/main/java/block_party/blocks/DataSaplingBlock.java`
- `src/main/java/block_party/blocks/WisteriaVineBodyBlock.java`
- `src/main/java/block_party/blocks/WisteriaVineTipBlock.java`
- `build.gradle` `syncNeoForgeSpikeResources`
- generated spike resources under `build/generated/neoforgeSpikeResources`

Tests required before/after:

- Before: registry/resource smoke tests.
- After: recipe load, loot table load, representative placement tests, sapling/grower behavior, worldgen data-pack load, and manual transparent block render check.

Current NeoForge status:

- Implemented in Slice 5.3 for representative resource/content parity. The generated spike resources now copy recipes, loot tables, and worldgen data into Minecraft 1.21.4 resource paths (`recipe`, `loot_table`, and `worldgen`) and migrate the legacy JSON fields needed by the frozen Forge resources.
- Active decorative block behavior now includes pillar properties for logs/wood, leaves properties for ginkgo/wisteria leaves, `blooming` sakura blossom leaves, sapling `stage`/tree-grower keys for ginkgo/sakura/white sakura/wisteria, data-sapling behavior for row-backed sakura saplings, slab `type` for sakura slabs, door `half` for shoji screens, and non-solid support checks for wisteria vine body/tip blocks.
- GameTests cover migrated recipe loading, loot/worldgen resource availability, configured-feature registration, representative decorative state properties, and wisteria vine survival.
- Remaining caveat: full block-class parity for every decorative block shape/interaction and manual transparent render verification are still deferred. The generated-resource format migration is NeoForge-target-only; the frozen Forge resource files remain unchanged.

Explicitly out of scope:

- NPC behavior.
- Client screens.
- New biome/worldgen tuning.

Recommended Codex prompt:

```text
Focus only on resource/content parity for recipes, loot, worldgen, decorative blocks, and tree/vine behavior in the NeoForge spike. Preserve existing IDs and resource paths where practical. Do not port NPC dialogue, UI, or rendering beyond block render requirements. Add load and representative placement tests.
```

### Slice 5.3.5: Manual Review Ergonomics And Registry Discoverability

Goal:
Restore enough creative inventory discoverability that the NeoForge spike can be manually reviewed before PR shaping without command-only workflows.

Implemented:

- NeoForge creative mode tab registration under `block_party:block_party`.
- Tab title uses the existing `itemGroup.block_party` translation.
- Tab icon uses the existing `cupcake` item.
- Tab population uses the active `CustomItems.ENTRIES` registry order so current block items, `moe_spawn_egg`, Cell Phone, Yearbook, data blocks, saplings, decorative blocks, and simple item placeholders are visible for manual review.
- GameTests cover the creative tab registry ID plus review-critical entries: `moe_spawn_egg`, `cell_phone`, `yearbook`, `shrine_tablet`, `garden_lantern`, `sakura_sapling`, `sakura_log`, `sakura_blossoms`, and `wisteria_vines`.

Still out of scope:

- Final creative-tab sort order parity from the commented Forge helper.
- New items or new gameplay behavior.
- Manual client visual validation, which should happen before or during Phase 5.4 PR review checklist work.

### Slice 5.3.6: Client Asset And Render-Type Readiness

Goal:
Fix the client rendering blockers exposed by the creative tab before the Phase 5.4 normalization pass.

Implemented:

- Generated Minecraft 1.21.4 item definition files under `assets/block_party/items/*.json` from the copied legacy `assets/block_party/models/item/*.json` files. This keeps the frozen legacy item models/textures as source of truth while making item icons resolve in the NeoForge target.
- Restored client render-layer registration for transparent/cutout block IDs through the NeoForge client setup event.
- Mirrored the frozen Forge cutout/cutout-mipped intent for paper lanterns, hanging scrolls, garden lanterns, leaves, saplings, blossoms, shimenawa, shoji panels/screens, shrine tablets, and wisteria vines.
- Added GameTest coverage that representative generated item definitions, item models, item textures, blockstates, block models, and block textures are packaged on the mod classpath.

Still out of scope:

- The reported UI blur issue. Text and layout are usable, so this remains a non-blocking client polish/manual-review note.
- Full screenshot-based client validation. Before merging the normalized PR, manually confirm the creative tab icons render, transparent blocks no longer occlude adjacent blocks, and the UI blur is acceptable or filed separately.

### Phase 5.4: Spike Normalization And PR Shaping

Goal:
Convert the completed NeoForge spike into a proper PR branch for main before post-port feature work begins.

This phase happens after Phases 1-5 reach parity and before Phase 6 unfinished systems.

Objectives:
- Reduce diff size where practical.
- Move active code out of src/neoforgeSpike into normal source layout.
- Preserve original package/class names and file organization where reasonable.
- Reuse original Forge 1.19.4 class structure when NeoForge APIs allow it.
- Keep new service/helper classes only when they clearly isolate loader/API-specific behavior or reduce future port pain.
- Remove spike-only stubs, inert placeholders, generated safe subsets, and temporary scaffolding.
- Keep tests, docs, and migration notes.
- Preserve semantic parity over cosmetic cleanup.

Rules:
- Do not implement chores, pranks, Senpai, new dialogue, or new content.
- Do not redesign architecture for style.
- Do not collapse domain-specific code into generic abstractions.
- Do not rename public registry IDs, resource IDs, packet IDs, NBT keys, or DB columns unless explicitly documented.
- Prefer recognizable evolved code over pristine rewritten code.
- If code differs meaningfully from Forge 1.19.4, document it in docs/SEMANTIC_DRIFT_NOTES.md.
- If a spike-only class remains, justify why it belongs in the PR branch.

Deliverables:
- Normalized source tree.
- Removed or documented spike scaffolding.
- Updated docs/NEOFORGE_PORT_NOTES.md renamed or replaced with docs/NEOFORGE_PORT_NOTES.md.
- docs/SEMANTIC_DRIFT_NOTES.md.
- docs/PR_REVIEW_GUIDE.md.
- All tests passing.
- Manual golden-world checklist ready for review.

## Phase 6: Gameplay Parity Closeout

Phase 6 turns the normalized port into a mergeable gameplay PR. It is still a parity phase: restore behavior that exists in the frozen Forge 1.19.4 branch, or document why it remains deferred. Chores, pranks, adventure loops, and other README-level ideas are treated as future feature work unless the old branch contains concrete shipped behavior.

### Slice 6.1: Moe Sound And Block-Physical Hooks

Player-facing behavior restored:

- Moe hurt, attack, death, step, speak, and cat ambient sounds resolve through the legacy `moes/sounds` resource overrides.
- Moe attacks play the attack sound after successful delegated combat.
- Moe step movement plays the configured step sound.
- Moe block buffer, voice pitch, and source-block fire immunity remain derived from the persisted source block state.

Forge 1.19.4 source files involved:

- `src/forge1194/java/block_party/entities/Moe.java`
- `src/forge1194/java/block_party/entities/abstraction/Layer1.java`
- `src/forge1194/java/block_party/entities/abstraction/Layer2.java`
- `src/forge1194/java/block_party/registry/resources/MoeSounds.java`

Tests required:

- Sound hook resolution covers default Moe sounds and the bundled bell step override.
- Cat-feature blocks use the Moe meow ambient sound.
- Source block flammability drives `fireImmune()`.
- Existing delegated combat test remains green.

Out of scope:

- Dialogue UI audio playback.
- Full animation state restoration.
- AI goals, following, chores, pranks, and adventure behavior.

### Slice 6.2: Corporeal Death, Hide, And Inventory Drop Parity

Player-facing behavior restored:

- A corporeal Moe that dies retreats into its source block where the Forge branch did.
- Equipment drop chances remain suppressed.
- Moe inventory contents drop through the server-side death/drop path, preserving existing active inventory persistence.
- Implemented in active NeoForge code: `Moe.die(DamageSource)` delegates to vanilla death handling, then uses the existing row-backed `hide(HideUntil.EXPOSED)` path for corporeal Moes. Ethereal Moes do not create hiding spots.

Tests required:

- Corporeal death produces a hiding/block state path rather than losing the NPC row.
- Ethereal/non-corporeal death does not create a hiding spot.
- Inventory contents drop when the death/drop path runs.
- Active GameTests cover all three required cases.

Out of scope:

- Client death effects.
- New recovery UI.

### Slice 6.3: Interaction Scene And Dialogue Trigger Parity

Player-facing behavior restored:

- Right click, shift-right click, left click, shift-left click, hurt, attack, random tick, and stare/looked-at triggers match the old trigger semantics where active scene data exists.
- Scene sound payloads continue to carry resolved `SoundEvent` IDs.
- Implemented in active NeoForge code: owner main-hand right-click runs right/shift-right triggers, offhand passes, owner left-click/attack interactions run left/shift-left triggers without damage, non-owner damage is scaled by source-block buffer and triggers `HURT` only after successful damage, successful Moe attacks trigger `ATTACK`, and server AI hooks can trigger `RANDOM_TICK`/`STARE`.

Tests required:

- Scene trigger GameTests for every server-observable trigger.
- Regression coverage for trigger priority/interruption where Forge behavior is known.
- Active GameTests cover owner right-click, offhand pass-through, owner shift-left, non-owner hurt, successful attack, random/stare server hooks, existing trigger priority, right-click dialogue, and left-click hide.

Out of scope:

- New scene content.
- Client UI redesign.

### Slice 6.4: Follow, Navigation, And Cell Phone Arrival Parity

Player-facing behavior restored:

- Follow flag behavior, door/trapdoor path malus, winged navigation category, and Cell Phone arrival behavior match the old branch where observable.
- Cross-dimension call behavior is restored only after confirming the 1.21.4 equivalent API path.
- Implemented in active NeoForge code: source blocks tagged with wings now switch Moe to flying movement/navigation, non-winged source blocks switch back to ground navigation with door opening enabled, and Cell Phone calls place loaded same-dimension Moes at the Forge yaw-based arrival point `x - sin(yRot) * 1.44`, `z + cos(yRot) * 1.44` before setting `following=true`.
- Cross-dimension calls remain the documented safe failure for this PR because Forge used `ITeleporter` and the active NeoForge entity shell does not yet own dimension-transfer semantics.

Tests required:

- Follow flag survives save/load and call flows.
- Cell Phone service handles same-dimension and, if ported, cross-dimension calls.
- Winged/source-block navigation setup is covered by server assertions where possible.
- Active GameTests cover following save/load, following set after call, Forge yaw-based same-dimension arrival, forced chunk release, and winged vs grounded navigation category.

Out of scope:

- Inventing new follow AI.
- Chores, pranks, or adventure systems.

### Slice 6.5: Remaining Simple Item Families

Status: Implemented for server-observable item-family parity.

Player-facing behavior restored:

- `MoeMusicItem` replaces the placeholder music discs with one-stack rare jukebox-playable discs using preserved sound IDs and Forge durations through 1.21.4 jukebox song data.
- `LetterItem` replaces the placeholder letter and reads the legacy `IsClosed` custom data key for open/closed state.
- Samurai katana and wooden bokken replace placeholders with Forge durability, rarity, attack profile, parry-sword tag membership, and the incoming-damage parry hook.
- Samurai armor and masked samurai items replace placeholders with Forge defense/toughness/knockback/rarity shape plus server event hooks for XP repair and arrow damage reduction.
- Armor model/overlay work is deferred to a client/manual slice if not safely testable server-side.

Tests:

- Done: music disc item/component assertions.
- Done: letter `IsClosed` state assertions.
- Done: samurai weapon durability/rarity/tag assertions.
- Done: samurai armor rarity/tag helper coverage for XP repair and stacked arrow-damage halving.

Out of scope:

- New weapons or armor balancing.
- Client model redesign.
- Client samurai armor model/helmet overlay parity.

### Slice 6.6: Known Technical Debt Cleanup After Parity

Status: Implemented for the active NeoForge debt surface that is covered by automated tests.

Player-facing behavior restored or improved:

- Moe texture metadata now fails closed when an override names an unknown block-state property or an invalid property value, avoiding broad accidental texture matches from malformed metadata.
- `HidingSpots` now marks SavedData dirty only when entries actually change, reducing persistence churn without changing hide/reveal behavior.
- The old custom SQL `Column`/`Row`/`Table` debt and `Markov` action debt were reviewed as frozen Forge reference-only in the normalized NeoForge source; they are not active code in this slice.

Candidate files:

- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/entities/data/HidingSpots.java`

Tests:

- Done: malformed Moe texture metadata fails closed for unknown properties and invalid values.
- Done: `HidingSpots` no-op remove/unchanged put stay clean, while new put/existing remove mark data dirty.
- Existing coverage already guards texture fallback, missing hidden spot no-op, event reveal paths, and scene interruption priority.

Out of scope:

- Broad refactors without tests.
- Changing save formats without migration notes.
- Cross-dimension hidden-spot persistence tests and client visual verification; those remain release-hardening work.

### Slice 6.7: Release Hardening And Golden-World Verification

Status: Phase 6 closeout complete for automated server-side verification. Manual client/golden-world review remains the final pre-merge gate.

Player-facing behavior restored:

- Existing worlds, DB rows, resources, screens, and core interactions survive the final port.

Forge 1.19.4 source files involved:

- all restored systems, plus `src/main/resources/**` and existing Forge GameTests/manual golden-world notes.

NeoForge spike files involved:

- the full NeoForge source set after reconstruction.

Tests required before/after:

- Done: `compileJava`.
- Done: `runGameTestServer`; all 115 required GameTests passed.
- Covered by automated tests: registry/resource smoke, spawn/hide/reveal, profile/state persistence, SQLite row/table behavior, data block records, network payload contracts, dialogue/controller server contracts, same-dimension Cell Phone call service, scene triggers, sound hooks, item families, and active tech-debt cleanup.
- Still manual before merge: client launch, server launch outside GameTest, golden-world spawn/talk/hide/reveal, Yearbook, Cell Phone, persistence stop/start, representative renders, sounds, shrine/location checks, and SQLite sample-row inspection.

Release-blocker evaluation:

- No automated server-side release blockers found in Slice 6.7.
- Manual client/golden-world review is still required because rendering, screen visuals, audio playback, and long-lived world persistence cannot be fully validated by the current dedicated-server GameTests.
- Accepted deferred work remains documented in `docs/SEMANTIC_DRIFT_NOTES.md`: cross-dimension Cell Phone behavior, client shrine-location rendering, richer translated response labels, exact samurai armor client model/overlay parity, visual polish for reported UI blur/model issues, and future feature work beyond the frozen Forge branch.

### Pre-Merge Botanical Cleanup

Status: Implemented after Phase 6 automated closeout as a parity tightening pass.

Player-facing behavior restored:

- Ginkgo leaves use their Forge-style falling leaf particle hook.
- Wisteria leaves can create vine tips below themselves.
- Wisteria vine body and tip blocks use vanilla growing-plant body/head behavior again, including downward bonemeal growth and non-solid collision.

Tests:

- Done: ginkgo/wisteria concrete block class checks.
- Done: wisteria leaves place vine tips below.
- Done: wisteria vine tip bonemeal grows downward.
- Existing coverage continues to assert non-solid survival behavior.

Explicitly out of scope:

- New gameplay.
- Cosmetic redesign.
- Large schema redesign unless required for compatibility.

Recommended Codex prompt:

```text
Focus only on NeoForge release hardening. Run the full automated test suite and the golden-world checklist, compare DB/resource/screen/render behavior against the Forge baseline docs, and produce a release-blocker list. Do not implement new features or refactor unrelated systems.
```

## Suggested Execution Order

1. Finish Phase 1 server gameplay slices until spawn, hide/reveal, call, and server records are stable.
2. Restore Phase 2 entity/profile parity so server and client consumers share stable entity data.
3. Complete Phase 3 networking parity against server services before opening UI.
4. Reintroduce Phase 4 rendering and screens on top of stable payloads/entity data.
5. Fill Phase 5 resource/content parity, prioritizing scene/resources that unlock core gameplay.
6. Close remaining Forge gameplay parity through Phase 6 one slice at a time.
7. Move chores, pranks, adventure loops, and feature additions beyond the current Forge branch into later feature PRs.
