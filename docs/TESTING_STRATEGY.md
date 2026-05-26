# Testing Strategy

This project needs tests that protect behavior before bug fixes or feature work. The goal is not to test Minecraft itself; it is to capture Block Party's current contracts around Moes, block identity, dialogue, hiding, persistence, and companion tools.

Use three layers:

1. Pure JUnit tests for logic that can run without a Minecraft world.
2. GameTests for in-world behavior that needs blocks, entities, ticks, events, saved data, or server-side gameplay.
3. Manual golden-world checks for rendering, UI, sound, screenshots, and complete interaction flows.

Do not use infrastructure work as the moment to decide what behavior should change. First capture the behavior, then fix or improve.

## Layer 1: Pure JUnit Tests

Pure JUnit tests should cover deterministic logic and serialization code that does not require a loaded Minecraft world. These tests should be fast and run in normal Gradle test tasks once a test source set exists.

Good candidates:

- enum parsing and fallback behavior in `src/main/java/block_party/scene/Response.java`
- trigger parsing and priority in `src/main/java/block_party/scene/SceneTrigger.java`
- trait parsing, compatibility, and values in `src/main/java/block_party/scene/traits/*.java`
- hide-condition parsing and timer predicates in `src/main/java/block_party/entities/goals/HideUntil.java`, with test doubles where needed
- NBT-like serialization logic that can be isolated from live registries, such as `DimBlockPos.write/read` in `src/main/java/block_party/db/DimBlockPos.java`
- scene variable containers under `src/main/java/block_party/scene/data/`
- markdown/substitution helpers in `src/main/java/block_party/utils/Markdown.java`, if they can be exercised with a small fake NPC or extracted later
- SQL row/column dirty tracking in `src/main/java/block_party/db/sql/Column.java` and `Row.java`, once tests can construct fake tables/rows safely

Avoid pure JUnit for behavior that needs a real registry, `Level`, `ServerLevel`, entity type, resource manager, client renderer, or packet context. Those belong in GameTests or manual checks.

## Layer 2: GameTests

GameTests should cover in-world server behavior. They should run with a real test level, real registries, real blocks, real entities, and ticks.

Run the initial GameTest suite separately from regression tests:

- `gradlew runGameTestServer`

Historical Java regression tests target the old Forge API and are currently disabled in Gradle. Re-enable or rewrite them only when they cover active NeoForge code.

Good candidates:

- Moe spawn from `CustomSpawnEggItem`
- valid and invalid block-origin behavior
- block-state preservation
- trait assignment from block tags
- hiding and reveal from `Moe`, `MoeInHiding`, `HidingSpots`, and `HideUntil`
- persistence across entity NBT and saved data where GameTest can simulate reload or save/load boundaries
- database creation and row sync through `BlockPartyDB`, `NPC`, and `Recordable`
- Yearbook/Cell Phone server-side packet effects where UI is not required
- shrine/garden/location record creation/deletion
- resolved dangerous-bug behavior such as non-recursive combat and `HideUntil` restore, plus remaining baselines such as no-op DB update and hidden spot null handling

GameTests should favor observable assertions: block exists or is removed, entity exists or is removed, database row contains expected values, owner UUID matches, hidden spot exists, NPC list contains an ID.

## Layer 3: Manual Golden-World Checklist

Manual testing should use a small saved world that contains known blocks, known Moes, hidden Moes, shrine/garden/location blocks, and a populated `blockparty.db`.

Manual checks should cover things automated tests will not catch well yet:

- Moe rendering, textures, scale, glow, emotion layers, held items, and name/health labels
- `DialogueScreen` layout, text reveal, response buttons, sounds, and close behavior
- `YearbookScreen` and `CellPhoneScreen` layout and full interaction flow
- speech, step, hurt, death, cat-feature, and phone sounds
- particle and block render-layer behavior
- seasonal texture behavior around Christmas/Halloween helpers
- full play loops: spawn, talk, hide, reveal, save, reload, call by phone, inspect in Yearbook

The golden world should be captured before risky bug fixes and before future Minecraft/NeoForge version moves. Keep screenshots and brief notes for expected results.

## Major Systems

### Block And Item Registration

Files:

- `src/main/java/block_party/BlockParty.java`
- `src/main/java/block_party/registry/CustomBlocks.java`
- `src/main/java/block_party/registry/CustomItems.java`
- `src/main/java/block_party/registry/CustomEntities.java`
- `src/main/java/block_party/items/MoeBlockItem.java`
- `src/main/java/block_party/items/CustomSpawnEggItem.java`

Unit test:

- Sort-order behavior for `ISortableItem` implementations if exposed without live registries.
- Pure helper methods such as `BlockParty.source(...)`, `isChristmas()`, and `isHalloween()` only if the calendar dependency can be controlled or wrapped later.

GameTest:

- All registered Moe-spawnable blocks can be used with `CustomSpawnEggItem`.
- Invalid blocks fail with no entity and no block removal.
- Successful spawn consumes the source block, creates one `Moe`, assigns owner, assigns a DB record, and shrinks the item stack.
- Spawn preserves source block state and block entity persistent data where applicable.

Manual:

- Creative/inventory discoverability and active creative-tab sort order.
- Visible block item models and cutout/cutout-mipped render behavior for decorative blocks.

Capture before fixes:

- Exact valid/invalid spawn behavior.
- Current creative-tab item discoverability and sort order.
- Current render behavior for transparent blocks registered through `BlockPartyClientEvents`.

### Moe Entity Lifecycle

Files:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/MoeInHiding.java`
- `src/main/java/block_party/entities/goals/HideUntil.java`
- `src/main/java/block_party/entities/data/HidingSpots.java`

Unit test:

- `HideUntil.fromValue(...)` fallback behavior.
- `HideUntil.ONE_SECOND_PASSES` threshold semantics, with a test double or extracted predicate.
- `SceneTrigger` priority values that drive lifecycle scene replacement.

GameTest:

- Spawned Moe persists near player and does not despawn from distance.
- `Moe.setBlockState` changes visible block identity, scale, fire/pathing flags, and navigation category where observable.
- Death of a corporeal Moe triggers current hide/retreat behavior.
- `Moe.hide(...)` creates a hiding marker and restores the source block.
- `MoeInHiding.spawn()` restores the same NPC record and removes the hiding block.
- `HidingSpots` reveal behavior for left-click start, block break, piston pre-event, falling-block conversion, timer expiry, and air exposure.
- Combat coverage for active `Moe` attack behavior preserving the resolved non-recursive vanilla delegation.

Manual:

- Name/health label visibility and distance cutoff.
- Physical feel of scale, step sounds, animation, and held/head/special layers.
- Full hide/reveal loop with player perception.

Capture before remaining fixes:

- Current timed-hide behavior across save/load, especially the resolved `HideUntil` restore path.
- Current combat behavior after the `doHurtTarget` recursion fix.
- Current hidden-spot behavior in multiple dimensions.

### Personality And Profile Generation

Files:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/scene/traits/Gender.java`
- `src/main/java/block_party/scene/traits/BloodType.java`
- `src/main/java/block_party/scene/traits/Dere.java`
- `src/main/java/block_party/scene/traits/Zodiac.java`
- `src/main/java/block_party/scene/traits/Emotion.java`
- `src/main/java/block_party/registry/resources/Names.java`
- `src/main/java/block_party/registry/CustomTags.java`

Unit test:

- Trait enum `fromValue`, `getValue`, and fallback behavior.
- `BloodType.isCompatible(...)` and weighted boundaries if random source can be controlled.
- `Gender` pronoun/honorific keys, if translation lookup is isolated or asserted as keys later.

GameTest:

- Blocks tagged for male, female, nonbinary, blood type, dere, and zodiac produce the expected Moe traits.
- Name generation assigns a name from the correct loaded list and avoids reuse where current behavior supports uniqueness.
- Empty name-list behavior is captured if a controlled data pack can create that state.

Manual:

- Profile fields appear correctly in `YearbookScreen`.
- Dialogue substitutions display the expected given name, family name, and pronouns.
- Cat features, wings, glow, and festive textures are visually/audibly recognizable.

Capture before fixes/version moves:

- The current winner among constructor defaults, block tags, randomization, and `finalizeSpawn`.
- Current behavior when names run out.

### Dialogue And Scene System

Files:

- `src/main/java/block_party/scene/SceneManager.java`
- `src/main/java/block_party/scene/Scene.java`
- `src/main/java/block_party/scene/Dialogue.java`
- `src/main/java/block_party/scene/Response.java`
- `src/main/java/block_party/scene/Speaker.java`
- `src/main/java/block_party/scene/ISceneAction.java`
- `src/main/java/block_party/scene/ISceneObservation.java`
- `src/main/java/block_party/scene/actions/*.java`
- `src/main/java/block_party/scene/observations/*.java`
- `src/main/java/block_party/registry/resources/Scenes.java`
- `src/main/resources/data/block_party/scenes/test_dialogue.json`
- `src/main/resources/data/block_party/scenes/test_hide.json`

Unit test:

- `Response.fromValue(...)` and translation key generation.
- `Speaker` NBT/JSON defaults where constructible without client/world state.
- `Dialogue.write/read` round trip for text, tooltip flag, speaker, sound ID, and responses if sound registry lookup can be controlled.
- `DoCounter`/`DoCookie` operation parsing and mutation using `Counters` and `Cookies`.
- `Markov` current weighted behavior before deciding whether to fix/register it.

GameTest:

- Right-click owner interaction triggers `test_dialogue.json`.
- Left-click owner interaction triggers `test_hide.json`.
- Scene filters gate scenes correctly.
- Response selection advances the scene server-side.
- Higher-priority triggers interrupt or defer exactly as current `SceneManager` rules dictate.
- `SceneVariables` player/NPC counters and cookies persist where current behavior supports it.

Manual:

- `DialogueScreen` visual layout, speaker staging, text reveal timing, response button positions, tooltip/text modes, sound playback, and close behavior.
- Chained dialogue feels the same before and after port.

Capture before fixes/version moves:

- Scene priority/interruption behavior.
- Current JSON namespace remapping via `Scenes.own(...)`.
- Current behavior for malformed or missing scene fields.

### Networking Packets

Files:

- `src/main/java/block_party/network/CustomMessenger.java`
- `src/main/java/block_party/network/ClientPayloadBridge.java`
- `src/main/java/block_party/network/payload/DialogueRespondPayload.java`
- `src/main/java/block_party/network/payload/DialogueClosePayload.java`
- `src/main/java/block_party/network/payload/DialogueOpenPayload.java`
- `src/main/java/block_party/network/payload/ControllerOpenPayload.java`
- `src/main/java/block_party/network/payload/NpcCallRequestPayload.java`
- `src/main/java/block_party/network/payload/NpcCallPayload.java`
- `src/main/java/block_party/network/payload/NpcDetailPayload.java`
- `src/main/java/block_party/network/payload/NpcRemoveRequestPayload.java`
- `src/main/java/block_party/network/payload/ShrineListRequestPayload.java`
- `src/main/java/block_party/network/payload/ShrineListPayload.java`

Unit test:

- Packet encode/decode round trips for payload-only classes where `FriendlyByteBuf` can be constructed in tests.
- `Dialogue` and `NPC` NBT payload round trips used by packets.
- Direction naming should not be "fixed" without a test that preserves effective behavior.

GameTest:

- `DialogueRespondPayload` changes the correct server-side Moe response.
- `NpcDetailPayload` returns the requested NPC data for owned records.
- `NpcRemoveRequestPayload` removes only allowed NPC records from the player's visible list.
- `NpcCallRequestPayload`/`NpcCallPayload` find a listed Moe and teleport it near the player where current behavior succeeds.
- `ShrineListPayload` contents reflect current shrine visibility rules.

Manual:

- Actual screen opening for `DialogueOpenPayload` and `ControllerOpenPayload` in Yearbook/Cell Phone modes.
- Failure behavior when a phone target cannot be found.
- Packet-driven UI does not open when the player no longer holds the expected item.

Capture before fixes/version moves:

- Payload IDs/order from `CustomMessenger.registerPayloads(...)` if old clients/worlds are expected to interoperate during future version moves.
- Direction and ID behavior for the NeoForge custom payload registrations.

### Persistence And Database

Files:

- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/db/Recordable.java`
- `src/main/java/block_party/db/DimBlockPos.java`
- `src/main/java/block_party/db/sql/Table.java`
- `src/main/java/block_party/db/sql/Row.java`
- `src/main/java/block_party/db/sql/Column.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/db/records/Shrine.java`
- `src/main/java/block_party/db/records/Garden.java`
- `src/main/java/block_party/db/records/Location.java`
- `src/main/java/block_party/db/records/Sapling.java`

Unit test:

- `DimBlockPos` NBT round trip for dimension and coordinates.
- `Column` default values, dirty tracking, and equality/reference behavior as currently implemented.
- `Row.write/read` NBT round trips with fake schemas.
- No-op `Row.update()` behavior should be captured before fixing invalid SQL generation.

GameTest:

- World load creates expected SQLite tables.
- Spawned Moe creates and updates an `NPC` row.
- `NPC.load(...)` restores owner, name, block state, traits, stats, home, and DB ID.
- Player NPC list in `BlockPartyDB` persists across save/load.
- Claimed names persist.
- `Shrine`, `Garden`, `Location`, and `Sapling` records are created/deleted when their block entities are claimed/destroyed.
- Verify whether `Saplings.create(level)` absence is observable.

Manual:

- Inspect `blockparty.db` after golden-world setup.
- Confirm old-world DB file survives a port without data loss.
- Test real server stop/start around hidden Moes and known NPC lists.

Capture before fixes/version moves:

- Current DB schema exactly.
- Current behavior for no-op updates and dirty tracking.
- Current record lifetime for dead/removed NPCs.

### Client Rendering, Models, And UI

Files:

- `src/main/java/block_party/client/BlockPartyRenderers.java`
- `src/main/java/block_party/client/renderers/MoeRenderer.java`
- `src/main/java/block_party/client/renderers/MoeInHidingRenderer.java`
- `src/main/java/block_party/client/model/MoeModel.java`
- `src/main/java/block_party/client/renderers/layers/EmoteLayer.java`
- `src/main/java/block_party/client/renderers/layers/GlowLayer.java`
- `src/main/java/block_party/client/renderers/layers/SpecialLayer.java`
- `src/main/java/block_party/client/screens/DialogueScreen.java`
- `src/main/java/block_party/client/screens/YearbookScreen.java`
- `src/main/java/block_party/client/screens/CellPhoneScreen.java`
- `src/main/java/block_party/client/skybox/JapanRenderer.java`

Unit test:

- Very little should be pure unit tested until rendering/UI logic is separated from Minecraft client classes.
- Pure enum or layout constants can be tested later if extracted.

GameTest:

- Server-side preconditions for screens: player has NPC list, phone target exists, shrine list is populated.
- Entity state needed for render layers: glow flag, emotion, held item, block alias, scale.

Manual:

- Screenshot Moes for representative blocks: normal cube, slab/stair, glowing block, winged block, cat-feature block, festive block, block alias, barrel special overlay.
- Verify `MoeTextures` fallback and override behavior visually.
- Verify `MoeRenderer.renderNameTag` health/name display.
- Verify `DialogueScreen`, `YearbookScreen`, `CellPhoneScreen`, response buttons, and tooltips.
- Verify `JapanRenderer` shrine-dependent skybox effect.
- Verify all relevant sounds by ear.

Capture before fixes/version moves:

- Screenshots for texture lookup bug baseline.
- Current transparent/cutout block rendering through `BlockPartyClientEvents.registerRenderTypes`.
- Current UI layout at common window sizes.

### Resource Loading And Data Packs

Files:

- `src/main/java/block_party/registry/CustomResources.java`
- `src/main/java/block_party/registry/resources/Scenes.java`
- `src/main/java/block_party/registry/resources/Names.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/registry/resources/MoeSounds.java`
- `src/main/java/block_party/registry/resources/BlockAliases.java`
- resources under `src/main/resources/data/block_party/`
- resources under `src/main/resources/data/minecraft/`

Unit test:

- JSON parsing helpers only if registry lookups can be faked.
- `Scenes.own(...)` namespace remapping.
- `MoeTextures.BlockStatePattern.matches(...)` if made accessible or covered through package-private tests.

GameTest:

- Reload resources and verify names, scenes, aliases, sounds, and texture mappings are available.
- Verify block tags affect spawnability and traits.
- Verify malformed resources fail/log according to current behavior if a test data pack can isolate them.

Manual:

- Data-pack override smoke test with one custom scene, one custom texture override, and one custom name list.
- Resource reload in a running client/server.

Capture before fixes/version moves:

- Current behavior for `minecraft` namespace resources remapped to `block_party`.
- Current fallback behavior for missing texture/sound/name entries.

### Favorite Locations, Shrines, Gardens, And Saplings

Files:

- `src/main/java/block_party/blocks/entity/AbstractDataBlockEntity.java`
- `src/main/java/block_party/blocks/entity/LocativeBlockEntity.java`
- `src/main/java/block_party/blocks/entity/GardenLanternBlockEntity.java`
- `src/main/java/block_party/blocks/entity/ShrineTabletBlockEntity.java`
- `src/main/java/block_party/blocks/entity/SakuraSaplingBlockEntity.java`
- `src/main/java/block_party/db/records/Shrine.java`
- `src/main/java/block_party/db/records/Garden.java`
- `src/main/java/block_party/db/records/Location.java`
- `src/main/java/block_party/db/records/Sapling.java`
- `src/main/java/block_party/messages/SShrineList.java`

Unit test:

- Record NBT round trips for `Shrine`, `Garden`, `Location`, and `Sapling` if fake rows can be built.
- `Shrine.getClosest(...)` ordering with fake `DimBlockPos` rows, if construction can avoid live DB.

GameTest:

- Claiming data block entities creates player-owned rows.
- Destroying claimed blocks deletes rows where current behavior supports it.
- Shrine tablet update spawns the current bell Moe behavior.
- `SShrineList` includes the expected shrine positions for owner/dimension rules.
- Sapling record creation works or captures current failure from missing schema creation.

Manual:

- Shrine-related skybox/location experience.
- Full shrine tablet effect: lightning visual, ambient sound, spawned Moe.
- Garden/favorite-location gameplay if any is visible in current build.

Capture before fixes/version moves:

- Whether favorite-location return/respawn is currently observable.
- Exact shrine list visibility rule, including owner and dimension behavior.

### Chores, Pranks, Adventuring, And Needs

Files:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/world/CellPhone.java`
- `src/main/java/block_party/entities/MoeSpawner.java`

Unit test:

- None for current behavior beyond trait/stat accessors unless logic is added later.

GameTest:

- Phone call sets current follow state after teleport.
- Inventory persists across save/load.
- Health/combat attributes exist and current combat behavior is captured.
- Empty needs update hooks do not unexpectedly mutate hunger/stress/sleep fields.

Manual:

- Any discovered chore, prank, or adventuring behavior should be recorded in the golden-world checklist.
- Full Cell Phone adventure flow: call Moe from far away, inspect following state, observe what it does next.

Capture before fixes/version moves:

- That chores/pranks/adventuring are currently mostly scaffolding, not active gameplay.
- Current `isFollowing` behavior after `NpcCallRequestPayload`/`NpcCallPayload`.

## Baseline Before Fixes

Before fixing remaining known bugs from `docs/TECH_DEBT.md`, capture:

- Active `Moe` non-recursive combat result.
- `MoeInHiding` hide timer behavior across save/load with restored `HideUntil`.
- `MoeTextures.get` actual visual result for override and fallback textures.
- `Row.update()` no-dirty-column behavior.
- `HidingSpots` behavior for missing positions and multiple dimensions.
- `YearbookItem.interactLivingEntity` behavior when owner is offline or unresolved.
- `BlockPartyDB` current table schema, including missing `Saplings.create(level)` behavior.
- Current render-layer behavior for cutout blocks.

## Baseline Before Future Minecraft/NeoForge Version Moves

Before moving Minecraft/NeoForge versions again, prepare a golden world with:

- one valid spawned Moe from a simple block
- one Moe from a block with block-state properties
- one Moe with glow
- one Moe with cat features
- one winged Moe if tags/resources provide one
- one hidden Moe
- one timed hidden Moe
- one known NPC in Yearbook and Cell Phone
- one shrine tablet/gate record
- one garden/location record
- one sapling record if current gameplay can create it
- a populated `blockparty.db`

Record:

- screenshots of each representative Moe and UI screen
- exact dialogue flow from `test_dialogue.json`
- exact hide/reveal flow from `test_hide.json`
- DB schema and sample rows
- resource reload logs
- manual notes for sounds and visual effects

## Test Implementation Order

1. Add JUnit infrastructure and pure tests for enums, `DimBlockPos`, scene variables, and safe serialization.
2. Add DB unit tests around `Column`, `Row`, and no-op dirty update behavior.
3. Add GameTests for spawn, ownership, block origin, and DB row creation.
4. Add GameTests for dialogue trigger and response behavior.
5. Add GameTests for hide/reveal and hidden event paths.
6. Add GameTests for save/load persistence and phone/yearbook server behavior.
7. Build the manual golden-world checklist and screenshots.
8. Only then fix remaining dangerous bugs.
9. Re-run all tests before and after each risky behavior fix or future version-move slice.
