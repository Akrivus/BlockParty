# NeoForge Port Reconstruction Plan

This plan maps the remaining NeoForge 1.21.4 spike work into vertical slices that can restore Forge 1.19.4 behavior incrementally. It uses the current spike surface in `src/neoforgeSpike` as the integration branch and the Forge 1.19.4 implementation in `src/main/java` as the behavior baseline.

The recommended rhythm for every slice is:

1. Add or port the narrow tests first when practical.
2. Reintroduce the smallest production surface needed for that behavior in `src/neoforgeSpike`.
3. Keep newly active code server-only unless the slice explicitly owns client behavior.
4. Update `docs/NEOFORGE_SPIKE_NOTES.md` and any flow docs touched by the slice.
5. Keep `compileJava` and `runGameTestServer` passing.

## Phase 1: Server Gameplay Parity

### Slice 1.1: Spawn From Block Parity

Player-facing behavior restored:

- Using `moe_spawn_egg` on a valid tagged block consumes the source block, spawns a Moe, preserves block identity, assigns owner, creates the NPC row, and respects invalid-block failure behavior.
- Survival/creative item consumption should match Forge 1.19.4 once player inventory behavior is intentionally enabled.

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

- `src/neoforgeSpike/java/block_party/items/CustomSpawnEggItem.java`
- `src/neoforgeSpike/java/block_party/entities/Moe.java`
- `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java`
- `src/neoforgeSpike/java/block_party/db/records/NPC.java`
- `src/neoforgeSpike/java/block_party/registry/CustomTags.java`
- `src/neoforgeSpike/java/block_party/gametest/MoeLifecycleGameTests.java`

Tests required before/after:

- Before: preserve current valid/invalid spawn GameTests.
- After: add block removal, item consumption mode, owner UUID, SQLite row, `NPCsByPlayer`, source `BlockState`, and block entity persistent data coverage.
- After: add a regression that invalid blocks do not change the world or insert rows.

Explicitly out of scope:

- Trait generation beyond fields owned by later profile slices.
- Rendering, spawn particles, sounds, dialogue triggers, and Cell Phone UI.
- Full Forge `Layer1`-`Layer7` behavior.

Recommended Codex prompt:

```text
Focus only on NeoForge spike spawn-from-block parity. Restore Forge 1.19.4 `CustomSpawnEggItem` behavior around valid tags, source block removal, item consumption rules, owner assignment, source block state, block entity persistent data capture, and NPC row creation. Do not port rendering, dialogue, AI, networking, or full profile traits. Update spike notes and keep all GameTests passing.
```

### Slice 1.2: Hide And Reveal Event Parity

Player-facing behavior restored:

- Moe hides as its original block, records `HidingSpots`, and returns when exposed.
- Timed reveal, break-start, break-complete, piston, and falling-block reveal hooks match Forge behavior where feasible.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/MoeInHiding.java`
- `src/main/java/block_party/entities/abstraction/Layer2.java`
- `src/main/java/block_party/entities/data/HidingSpots.java`
- `src/main/java/block_party/entities/goals/HideUntil.java`
- `src/main/java/block_party/scene/actions/Hide.java`
- `src/main/java/block_party/db/records/NPC.java`

NeoForge spike files involved:

- `src/neoforgeSpike/java/block_party/entities/Moe.java`
- `src/neoforgeSpike/java/block_party/entities/MoeInHiding.java`
- `src/neoforgeSpike/java/block_party/entities/data/HidingSpots.java`
- `src/neoforgeSpike/java/block_party/entities/goals/HideUntil.java`
- `src/neoforgeSpike/java/block_party/db/records/NPC.java`
- `src/neoforgeSpike/java/block_party/gametest/MoeLifecycleGameTests.java`

Tests required before/after:

- Before: current manual reveal, missing hidden spot, and hide record tests.
- After: timed reveal, block break start/end, piston movement, falling block interaction, hidden NBT save/load, and same-row identity restoration.
- After: multi-dimension hidden spot coverage if `HidingSpots` remains per-level and keyed by `BlockPos`.

Explicitly out of scope:

- Dialogue scene execution that triggers hide.
- Client rendering of hidden markers.
- Full block entity data restore if Slice 1.1 has not enabled it yet.

Recommended Codex prompt:

```text
Focus only on hide/reveal event parity in the NeoForge spike. Port `HidingSpots` event hooks, timed `HideUntil` reveal, and block disturbance reveal behavior using the thin Moe/MoeInHiding shells. Preserve database ID and HidingSpots semantics. Do not port dialogue, rendering, AI, or client UI. Add GameTests for each reveal path and update docs.
```

### Slice 1.3: Cell Phone Server Call And Chunk Loading Parity

Player-facing behavior restored:

- Calling a known owned Moe from the Cell Phone server flow can find distant or unloaded Moes, move them near the player, and set following mode.
- Forced chunk or ticket lifecycle is cleaned up after success/failure.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/world/CellPhone.java`
- `src/main/java/block_party/world/chunk/ForcedChunk.java`
- `src/main/java/block_party/messages/CNPCTeleport.java`
- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/abstraction/Layer1.java`
- `src/main/java/block_party/entities/abstraction/Layer3.java`
- `src/main/java/block_party/db/records/NPC.java`

NeoForge spike files involved:

- `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java`
- `src/neoforgeSpike/java/block_party/entities/Moe.java`
- `src/neoforgeSpike/java/block_party/gametest/CellPhoneServiceGameTests.java`

Tests required before/after:

- Before: current owner/non-owner/missing/dead/corrupt/hidden/unloaded call service tests.
- After: same-dimension far call, cross-dimension call if preserved, forced chunk cleanup, failed lookup cleanup, final position near requester, and `following=true`.

Explicitly out of scope:

- Cell Phone screen.
- Client packet button wiring.
- Follow pathfinding or AI goals after teleport.

Recommended Codex prompt:

```text
Focus only on server-side Cell Phone call parity in the NeoForge spike. Extend `BlockPartyDB.callOwnedNpc` or a narrow server service to handle Forge-like distant/unloaded Moe lookup with minimal forced chunk handling and cleanup. Keep ownership and row checks authoritative. Do not add UI, rendering, dialogue, or follow AI. Add cleanup GameTests and update docs.
```

### Slice 1.4: Shrine, Garden, Location, And Block Entity Server Parity

Player-facing behavior restored:

- Decorative/data blocks with block entities keep owner/location data.
- Shrine tablet behavior, shrine lists, garden/location records, and locative blocks are available server-side.

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

- `src/neoforgeSpike/java/block_party/registry/CustomBlocks.java`
- `src/neoforgeSpike/java/block_party/registry/CustomItems.java`
- `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java`
- `src/neoforgeSpike/java/block_party/db/records/NPC.java`
- new spike block entity registry and block entity shells.

Tests required before/after:

- Before: registry tests must keep current IDs stable.
- After: block placement, block entity NBT round-trip, SQLite table creation for shrine/garden/location/sapling, and shrine list query behavior.
- After: manual or GameTest coverage for shrine tablet Moe creation if that path remains active.

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

Player-facing behavior restored:

- Moe identity survives save/load with Forge-visible fields: owner, DB ID, block state, visible/alias block state, following, profile stats, name, emotion, scale, corporeal state, home, and inventory-facing data where applicable.

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

- `src/neoforgeSpike/java/block_party/entities/Moe.java`
- `src/neoforgeSpike/java/block_party/entities/MoeInHiding.java`
- `src/neoforgeSpike/java/block_party/db/records/NPC.java`
- `src/neoforgeSpike/java/block_party/gametest/EntityDataGameTests.java`

Tests required before/after:

- Before: current data shell spawn and NBT round-trips.
- After: one test group per restored layer field category, plus golden NBT compatibility checks for legacy key names.
- After: verify `onSyncedDataUpdated` or replacement DB-sync behavior does not create unsafe write storms.

Explicitly out of scope:

- AI pathfinding, dialogue scene execution, rendering, UI, and network packets.

Recommended Codex prompt:

```text
Focus only on restoring full Moe/MoeInHiding synced data and NBT parity in the NeoForge spike. Port Forge Layer2-Layer5 state fields conservatively into the spike shell without enabling AI, dialogue, rendering, UI, or packets. Preserve legacy NBT keys and DB IDs. Add field-level GameTests and update docs.
```

### Slice 2.2: Profile Generation And Trait Tags

Player-facing behavior restored:

- Moes receive names, gender, blood type, dere, zodiac, emotion, scale/category traits, and block-derived behavior flags from resources/tags.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/abstraction/Layer4.java`
- `src/main/java/block_party/scene/traits/*.java`
- `src/main/java/block_party/scene/ITrait.java`
- `src/main/java/block_party/registry/resources/Names.java`
- `src/main/java/block_party/registry/CustomTags.java`
- `src/main/java/block_party/items/CustomSpawnEggItem.java`

NeoForge spike files involved:

- `src/neoforgeSpike/java/block_party/entities/Moe.java`
- `src/neoforgeSpike/java/block_party/db/records/NPC.java`
- `src/neoforgeSpike/java/block_party/registry/CustomResources.java`
- `src/neoforgeSpike/java/block_party/registry/CustomTags.java`
- `src/neoforgeSpike/java/block_party/registry/resources/CountingJsonReloadListener.java`

Tests required before/after:

- Before: resource parse and spawn identity tests.
- After: tag-to-trait tests for representative blocks, name uniqueness/claim tests, trait parse fallback tests, and SQLite/NBT persistence of profile fields.

Explicitly out of scope:

- Rendering layers that visually consume traits.
- Dialogue filters/actions that read traits.
- Chores/pranks/adventuring stats updates.

Recommended Codex prompt:

```text
Focus only on Moe profile generation parity in the NeoForge spike. Port trait enums, name loading/use, block trait tags, and source-block profile assignment. Preserve Forge 1.19.4 values and fallback parsing. Do not port rendering, dialogue, AI, or UI. Add tests for representative tag-derived traits and persistence.
```

### Slice 2.3: Movement, Combat, Inventory, And Follow Shells

Player-facing behavior restored:

- Moe has Forge-like basic movement attributes, home/teleport helpers, combat hooks without recursion, inventory persistence/menu data foundations, and following state semantics.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/abstraction/Layer1.java`
- `src/main/java/block_party/entities/abstraction/Layer3.java`
- `src/main/java/block_party/entities/abstraction/Layer6.java`
- `src/main/java/block_party/entities/abstraction/Layer7.java`
- `src/main/java/block_party/utils/CustomDamageSource.java`

NeoForge spike files involved:

- `src/neoforgeSpike/java/block_party/entities/Moe.java`
- `src/neoforgeSpike/java/block_party/registry/CustomEntities.java`
- `src/neoforgeSpike/java/block_party/gametest/EntityDataGameTests.java`
- `src/neoforgeSpike/java/block_party/gametest/CellPhoneServiceGameTests.java`

Tests required before/after:

- Before: following flag tests.
- After: attribute registration smoke test, combat super-call regression, home/teleport helper test, inventory NBT round-trip, and call service still sets following.

Explicitly out of scope:

- Real follow AI/pathfinding.
- Chores, pranks, sleep, hunger, stress, and action update implementations.
- Client inventory screens unless explicitly needed later.

Recommended Codex prompt:

```text
Focus only on non-client Moe movement/combat/inventory/follow state shells in the NeoForge spike. Port attributes, safe combat hooks, home/teleport helpers, and inventory NBT foundations from Forge without adding full AI/pathfinding or UI. Keep Cell Phone service tests passing and add targeted regressions.
```

## Phase 3: Networking Parity

### Slice 3.1: NPC List, Detail, Remove, And Call Payloads

Player-facing behavior restored:

- Yearbook/Cell Phone server requests can list owned NPCs, load details, remove/de-list entries, and call a Moe through typed NeoForge payloads.

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

- `src/neoforgeSpike/java/block_party/network/CustomMessenger.java`
- `src/neoforgeSpike/java/block_party/network/payload/**`
- `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java`
- `src/neoforgeSpike/java/block_party/gametest/NetworkPayloadGameTests.java`
- `src/neoforgeSpike/java/block_party/gametest/CellPhoneServiceGameTests.java`

Tests required before/after:

- Before: current payload codec and handler service tests.
- After: add call request/response payload codec tests, packet handler tests for `callOwnedNpc`, and failure payload shape tests.

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

Player-facing behavior restored:

- Dialogue opens on the client, responses return to the server, close messages clear state, and server-side scene response selection matches Forge behavior.

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

- `src/neoforgeSpike/java/block_party/network/CustomMessenger.java`
- new dialogue payload files under `src/neoforgeSpike/java/block_party/network/payload`
- future scene/dialogue spike files under `src/neoforgeSpike/java/block_party/scene`

Tests required before/after:

- Before: pure dialogue NBT/codec contract tests from Forge baseline if available.
- After: payload encode/decode, server handler rejects non-owned/missing NPCs, response sets server-side dialogue state, and close clears state.

Explicitly out of scope:

- Rendering the dialogue screen.
- Full scene action/filter port if not already active.
- Markov behavior changes.

Recommended Codex prompt:

```text
Focus only on NeoForge dialogue packet parity. Add typed payloads for opening, closing, and responding to dialogue, preserving Forge payload fields and server authority. Wire handlers to the minimal scene/dialogue state already present or narrow stubs. Do not port DialogueScreen rendering or unrelated controller packets. Add codec and server-handler tests.
```

### Slice 3.3: Shrine And Location Sync Packets

Player-facing behavior restored:

- Server can send shrine/location lists to clients under the same owner/dimension rules as Forge.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/messages/SShrineList.java`
- `src/main/java/block_party/db/records/Shrine.java`
- `src/main/java/block_party/db/ShrineLocations.java`
- `src/main/java/block_party/client/ShrineLocation.java`
- `src/main/java/block_party/blocks/entity/ShrineTabletBlockEntity.java`

NeoForge spike files involved:

- `src/neoforgeSpike/java/block_party/network/CustomMessenger.java`
- `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java`
- future shrine/location record spike files.

Tests required before/after:

- Before: server-side shrine/location row tests from Phase 1.4.
- After: payload codec, login/request handler, owner/dimension filtering, and empty-list behavior.

Explicitly out of scope:

- Japan skybox/client rendering.
- Shrine block visual effects and sounds unless Phase 1.4 explicitly enabled them.

Recommended Codex prompt:

```text
Focus only on shrine/location sync packet parity in the NeoForge spike. Add typed payloads and server handlers for the Forge `SShrineList` behavior using the ported shrine/location records. Do not add client rendering, skybox effects, or screens. Add codec and filtering tests.
```

## Phase 4: Client UI/Rendering Parity

### Slice 4.1: Renderer And Model Layer Parity

Player-facing behavior restored:

- Moe and MoeInHiding render with the expected model, scale, name/health labels, texture fallback, emote/glow/special layers, and non-disruptive hidden marker presentation.

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

- `src/neoforgeSpike/java/block_party/entities/Moe.java`
- `src/neoforgeSpike/java/block_party/entities/MoeInHiding.java`
- `src/neoforgeSpike/java/block_party/registry/CustomEntities.java`
- future spike client renderer files.

Tests required before/after:

- Before: entity/profile fields that renderers consume are stable.
- After: client launch smoke, screenshot matrix for representative Moe block states, texture override/fallback checks, and model layer registration smoke.

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
- future spike client screen files.

Tests required before/after:

- Before: dialogue payload and server response tests.
- After: manual screen open/response/close, keyboard/ESC behavior, text reveal behavior, and screenshot checks at common sizes.

Explicitly out of scope:

- Yearbook and Cell Phone screens.
- Scene authoring changes.
- Renderer refactors outside what the screen preview needs.

Recommended Codex prompt:

```text
Focus only on DialogueScreen parity in the NeoForge spike. Port the Forge dialogue screen, response widgets, and view model against the typed dialogue payloads. Do not port Yearbook, Cell Phone, shrine UI, or unrelated rendering. Verify manual dialogue flow and update docs.
```

### Slice 4.3: Yearbook And Cell Phone Screen Parity

Player-facing behavior restored:

- Yearbook and Cell Phone open, show known NPCs, request detail rows, remove/de-list entries, and call a Moe from the Cell Phone.

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

- `src/neoforgeSpike/java/block_party/network/CustomMessenger.java`
- `src/neoforgeSpike/java/block_party/network/payload/**`
- `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java`
- future spike item and screen files.

Tests required before/after:

- Before: list/detail/remove/call payload tests and server service tests.
- After: manual Yearbook open with zero/one/many NPCs, owned/unowned access behavior, remove flow, Cell Phone call flow, and screenshot checks.

Explicitly out of scope:

- Dialogue UI.
- Follow pathfinding after call.
- Full profile stat editing unless Forge UI already exposes it.

Recommended Codex prompt:

```text
Focus only on Yearbook and Cell Phone screen parity in the NeoForge spike. Port controller view models/screens and item open behavior against the existing typed NPC payloads and call service. Do not port dialogue, rendering refactors, or follow AI. Add manual verification notes and keep server tests passing.
```

### Slice 4.4: Particles, Sounds, And Client Resource Hooks

Player-facing behavior restored:

- Custom particles render, sound overrides play, UI sounds work, and client reload listeners populate visual/audio maps.

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

- `src/neoforgeSpike/java/block_party/registry/CustomParticles.java`
- `src/neoforgeSpike/java/block_party/registry/CustomSounds.java`
- `src/neoforgeSpike/java/block_party/registry/CustomResources.java`
- future spike client particle/reload files.

Tests required before/after:

- Before: registry/resource parse smoke tests.
- After: client launch, particle provider registration smoke, manual sound matrix, and reload behavior for sounds/textures/aliases.

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

- `src/neoforgeSpike/java/block_party/registry/SceneActions.java`
- `src/neoforgeSpike/java/block_party/registry/SceneFilters.java`
- `src/neoforgeSpike/java/block_party/registry/CustomResources.java`
- `src/neoforgeSpike/java/block_party/scene/SceneVariables.java`
- `src/neoforgeSpike/java/block_party/scene/data/**`
- `src/neoforgeSpike/java/block_party/registry/resources/CountingJsonReloadListener.java`

Tests required before/after:

- Before: inert JSON parse tests.
- After: scene parse fixtures, trigger priority tests, right-click dialogue scene, left-click hide scene, cookie/counter variable mutations, and failure behavior for malformed scenes.

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

Forge 1.19.4 source files involved:

- `src/main/java/block_party/registry/resources/Names.java`
- `src/main/java/block_party/registry/resources/BlockAliases.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/registry/resources/MoeSounds.java`
- `src/main/java/block_party/utils/JsonUtils.java`
- `src/main/resources/data/*/moes/**`
- `src/main/resources/assets/*/textures/moe/**`

NeoForge spike files involved:

- `src/neoforgeSpike/java/block_party/registry/CustomResources.java`
- `src/neoforgeSpike/java/block_party/registry/resources/CountingJsonReloadListener.java`
- `src/neoforgeSpike/java/block_party/entities/Moe.java`

Tests required before/after:

- Before: inert data parse tests.
- After: name list load, alias map load, texture pattern matching, sound fallback/override, data-pack override smoke, and malformed resource behavior.

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

- `src/neoforgeSpike/java/block_party/registry/CustomBlocks.java`
- `src/neoforgeSpike/java/block_party/registry/CustomItems.java`
- `build.gradle` `syncNeoForgeSpikeResources`
- generated spike resources under `build/generated/neoforgeSpikeResources`

Tests required before/after:

- Before: registry/resource smoke tests.
- After: recipe load, loot table load, representative placement tests, sapling/grower behavior, worldgen data-pack load, and manual transparent block render check.

Explicitly out of scope:

- NPC behavior.
- Client screens.
- New biome/worldgen tuning.

Recommended Codex prompt:

```text
Focus only on resource/content parity for recipes, loot, worldgen, decorative blocks, and tree/vine behavior in the NeoForge spike. Preserve existing IDs and resource paths where practical. Do not port NPC dialogue, UI, or rendering beyond block render requirements. Add load and representative placement tests.
```

## Phase 6: Post-Port Unfinished Systems

### Slice 6.1: Chores, Pranks, Needs, And Adventure Behavior Baseline

Player-facing behavior restored or clarified:

- Distinguish implemented Forge behavior from README intent for chores, pranks, hunger/stress/sleep/action updates, gifts, adventuring, and follow behavior.
- Either preserve existing absence or implement new behavior deliberately after the port is stable.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/entities/abstraction/Layer3.java`
- `src/main/java/block_party/entities/abstraction/Layer4.java`
- `src/main/java/block_party/entities/abstraction/Layer6.java`
- `src/main/java/block_party/entities/abstraction/Layer7.java`
- `src/main/java/block_party/items/BentoBoxItem.java`
- `src/main/java/block_party/items/CupcakeItem.java`
- `src/main/java/block_party/items/OnigiriItem.java`
- `src/main/java/block_party/items/LetterItem.java`
- `src/main/java/block_party/items/MoeMusicItem.java`

NeoForge spike files involved:

- whichever entity/profile/inventory files are active after Phases 1-5.

Tests required before/after:

- Before: document current Forge absence/presence manually.
- After: for each implemented behavior, add a GameTest or manual golden-world note.

Explicitly out of scope:

- Loader/API migration. This is product work, not port work.
- UI redesign.

Recommended Codex prompt:

```text
Focus only on post-port unfinished companion systems. Audit the now-ported NeoForge behavior against Forge/README expectations for chores, pranks, needs, gifts, sleep, and adventuring. Do not mix this with API migration. Produce tests or a scoped implementation plan for one behavior at a time.
```

### Slice 6.2: Known Technical Debt Cleanup After Parity

Player-facing behavior restored or improved:

- Fix known risky behavior only after parity tests make regressions visible.

Forge 1.19.4 source files involved:

- `src/main/java/block_party/db/sql/Column.java`
- `src/main/java/block_party/db/sql/Row.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/entities/data/HidingSpots.java`
- `src/main/java/block_party/scene/SceneManager.java`
- `src/main/java/block_party/scene/actions/Markov.java`

NeoForge spike files involved:

- corresponding ported files after Phases 1-5.

Tests required before/after:

- Before: capture current behavior for dirty tracking, no-op SQL update, texture lookup fallback, hidden spot null/multi-dimension behavior, scene interruption, and Markov weighting.
- After: focused regression tests for each fixed behavior.

Explicitly out of scope:

- New content.
- Broad refactors without tests.
- Changing save formats without migration notes.

Recommended Codex prompt:

```text
Focus only on one known technical-debt fix after NeoForge parity is established. Start by adding a regression test that captures the current behavior, then make the narrow fix and update docs. Do not combine multiple debt fixes or add new features.
```

### Slice 6.3: Release Hardening And Golden-World Verification

Player-facing behavior restored:

- Existing worlds, DB rows, resources, screens, and core interactions survive the final port.

Forge 1.19.4 source files involved:

- all restored systems, plus `src/main/resources/**` and existing Forge GameTests/manual golden-world notes.

NeoForge spike files involved:

- the full NeoForge source set after reconstruction.

Tests required before/after:

- Full `compileJava`, `runGameTestServer`, client launch, server launch.
- Golden world: spawn, talk, hide, reveal, Yearbook, Cell Phone call, persistence stop/start, resources reload, representative renders, sounds, shrine/location checks.
- Compare SQLite schema and sample rows against baseline.

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
6. Treat Phase 6 as after-parity work. It should not be mixed with loader migration.

