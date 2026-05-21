# Audit Triage

This triage is based on the current workspace after the latest staged audit/regression changes. It uses the existing architecture, behavior contract, technical debt, testing strategy, port risk map, and regression plan as inputs.

Verification performed during this sweep:

- `gradlew compileJava`: passed.
- `gradlew regressionTest`: passed, 10 lightweight suites.
- `gradlew runGameTestServer`: passed, 7 required GameTests.

Status labels:

- **Fixed and covered**: code appears changed and has regression or GameTest coverage.
- **Fixed, needs broader coverage**: code appears changed, but important behavior is still only partially covered.
- **Open**: no current fix found.
- **Uncertain**: code intent or runtime behavior is not clear from static reading and available tests.

## 1. Must Fix Before Any Release

### Scene JSON actions and observations silently missing

- Current status: **Fixed and covered** in the latest staged changes. `ISceneAction.parseArray` now delegates through `SceneActions.build(ResourceLocation)`, `ISceneObservation.parseArray` delegates through `SceneFilters.build(ResourceLocation)`, and `SceneJsonParsingRegressionTest` covers bundled `test_dialogue.json`, `test_hide.json`, nested `send_dialogue`, `end`, `hide`, `never`, and emotion filters.
- Affected files/classes: `src/main/java/block_party/scene/ISceneAction.java`, `src/main/java/block_party/scene/ISceneObservation.java`, `src/main/java/block_party/registry/SceneActions.java`, `src/main/java/block_party/registry/SceneFilters.java`, `src/main/java/block_party/registry/SceneCodecRegistries.java`, `src/main/java/block_party/registry/resources/Scenes.java`, `src/test/java/block_party/regression/SceneJsonParsingRegressionTest.java`.
- Player-facing risk: dialogue scenes, hide scenes, and response chains stop working or appear to do nothing.
- Migration risk: high. Scene action/filter registries are custom Forge registries and version-sensitive.
- Recommended next prompt/task: "Review the new scene codec registry implementation for Forge registry correctness and add coverage for action/filter IDs used by every bundled scene resource."

### Registry lookup for blocks and sounds

- Current status: **Fixed and covered for block and sound lookups**. `JsonUtils.getAs` now resolves `BuiltInRegistries.BLOCK` and `BuiltInRegistries.SOUND_EVENT`, and `JsonUtilsRegistryLookupRegressionTest` covers valid block, valid sound, invalid IDs, `BlockAliases`, `MoeSounds`, and `MoeTextures` fixtures.
- Affected files/classes: `src/main/java/block_party/utils/JsonUtils.java`, `src/main/java/block_party/registry/resources/BlockAliases.java`, `src/main/java/block_party/registry/resources/MoeSounds.java`, `src/main/java/block_party/registry/resources/MoeTextures.java`, `src/test/java/block_party/regression/JsonUtilsRegistryLookupRegressionTest.java`.
- Player-facing risk: block aliases, Moe sound overrides, Moe texture resources, dialogue sounds, and resource-driven filters fail to load or log errors.
- Migration risk: high. Registry lookup APIs and built-in registry access change across Minecraft/NeoForge versions.
- Recommended next prompt/task: "Extend JsonUtils registry support or tests for item, entity type, particle, mob effect, and biome lookups used by scene observations and future resources."

### Loaded database rows treated as dirty

- Current status: **Fixed and covered in regression tests**. `Column.setFromSet` now sets values cleanly, and `PersistenceRegressionTest.testLoadedRowNoopUpdateDoesNotTreatLoadedColumnsAsDirty` covers loaded no-op updates and later dirty mutations.
- Affected files/classes: `src/main/java/block_party/db/sql/Column.java`, `src/main/java/block_party/db/sql/Row.java`, `src/test/java/block_party/regression/PersistenceRegressionTest.java`.
- Player-facing risk: unnecessary broad SQLite updates can overwrite data unexpectedly or create hard-to-debug persistence churn.
- Migration risk: medium. Persistence bugs become harder to separate from loader/storage API changes during a port.
- Recommended next prompt/task: "Add an in-world GameTest or integration check that hide/reveal updates only intended NPC row fields after loading from SQLite."

### Hidden-spot missing lookup crash

- Current status: **Fixed and GameTested**. `HidingSpots.get` now returns `OptionalLong`, missing hidden spots no-op, and `MoeHideGameTests.missingHiddenSpotSpawnAndRevealEventsNoOp` covers direct spawn lookup, break start/end, piston pre-event, and falling block join event.
- Affected files/classes: `src/main/java/block_party/entities/data/HidingSpots.java`, `src/main/java/block_party/gametest/MoeHideGameTests.java`.
- Player-facing risk: ordinary block interaction near non-hidden blocks could crash or cancel world behavior.
- Migration risk: medium. Event hook signatures and SavedData access change during porting.
- Recommended next prompt/task: "Add save/reload GameTests for real hidden spots, including reveal after block break, piston, falling block, and timer expiry."

### Combat recursion in entity layers

- Current status: **Fixed earlier, not directly GameTested in this suite**. `Layer1.doHurtTarget` and `Layer7.doHurtTarget` delegate to `super.doHurtTarget(target)`.
- Affected files/classes: `src/main/java/block_party/entities/abstraction/Layer1.java`, `src/main/java/block_party/entities/abstraction/Layer7.java`.
- Player-facing risk: Moe combat can stack overflow or crash if recursion regresses.
- Migration risk: low to medium. Entity combat APIs may shift, and override chains are easy to break.
- Recommended next prompt/task: "Add a GameTest where a Moe attacks a dummy target and assert no recursion crash plus attack scene/sound side effects."

### HideUntil save/load restoration

- Current status: **Fixed earlier, partially covered**. `MoeInHiding.readAdditionalSaveData` reads `compound.getString("HideUntil")`; `HideRegressionTest` covers enum parsing, and GameTests cover normal timed reveal. Full entity save/load of a hidden Moe is still not covered.
- Affected files/classes: `src/main/java/block_party/entities/MoeInHiding.java`, `src/main/java/block_party/entities/goals/HideUntil.java`, `src/test/java/block_party/regression/HideRegressionTest.java`, `src/main/java/block_party/gametest/MoeHideGameTests.java`.
- Player-facing risk: hidden Moes reveal at the wrong time or never reveal after world reload.
- Migration risk: high. Entity NBT and SavedData behavior are port-sensitive.
- Recommended next prompt/task: "Add a GameTest that creates a MoeInHiding, serializes/deserializes it or restarts a golden world, and verifies HideUntil and ticksHidden survive."

### Moe texture lookup keyed by block state instead of block

- Current status: **Fixed and covered**. `MoeTextures.getTextureFor` uses `visibleState.getBlock()` and regression tests cover matching override, mismatch fallback, and missing bucket fallback.
- Affected files/classes: `src/main/java/block_party/registry/resources/MoeTextures.java`, `src/test/java/block_party/regression/MoeTexturesRegressionTest.java`.
- Player-facing risk: Moes render with wrong or missing block-person texture.
- Migration risk: medium. Resource reload and model/render layers are port-sensitive.
- Recommended next prompt/task: "Add manual golden-world checks for visible Moe textures, transparent blocks, and model layers."

### Tracked generated logs

- Current status: **Partially fixed**. `.gitignore` now ignores `*.log`, but `logs/debug.log` and `logs/latest.log` are still tracked/dirty after GameTests.
- Affected files/classes: `.gitignore`, `logs/debug.log`, `logs/latest.log`.
- Player-facing risk: none directly.
- Migration risk: low, but noisy commits make audit and release diffs harder to trust.
- Recommended next prompt/task: "Remove tracked log files from version control without deleting local logs, then verify GameTest runs leave the working tree clean."

## 2. Must Fix Before NeoForge Port

### Custom scene registries and fallback codec map

- Current status: **Fixed enough for current tests, migration risk remains**. The latest changes introduce `SceneCodecRegistries` and move scene action/filter deferred registers into `SceneActions` and `SceneFilters`. This restores parsing, but the port must decide whether custom Forge registries are still needed or whether the codec map becomes the stable source of truth.
- Affected files/classes: `src/main/java/block_party/BlockParty.java`, `src/main/java/block_party/registry/SceneActions.java`, `src/main/java/block_party/registry/SceneFilters.java`, `src/main/java/block_party/registry/SceneCodecRegistries.java`.
- Player-facing risk: data-driven dialogue and behavior hooks disappear after loader migration.
- Migration risk: high. Custom registry creation and deferred register APIs differ in NeoForge targets.
- Recommended next prompt/task: "Design the NeoForge scene action/filter registration strategy and add tests that fail if any registered scene ID cannot be built before resources load."

### Networking packet registration and payload contracts

- Current status: **Payload round trips covered; runtime flows still under-tested**. `NetworkRegressionTest` covers many encode/decode contracts, but not actual client/server packet handling under a live server.
- Affected files/classes: `src/main/java/block_party/registry/CustomMessenger.java`, `src/main/java/block_party/messages/*.java`, `src/test/java/block_party/regression/NetworkRegressionTest.java`.
- Player-facing risk: Dialogue, Yearbook, Cell Phone, shrine list, NPC request, removal, and teleport flows fail.
- Migration risk: very high. NeoForge custom payload APIs differ from Forge `SimpleChannel`.
- Recommended next prompt/task: "Add GameTests or server-side integration tests for CDialogueRespond, CNPCRequest, CNPCRemove, CNPCTeleport, and CRemovePage handlers."

### SavedData, SQLite, and world storage lifecycle

- Current status: **Partially covered; still must stabilize before port**. SQL generation and row dirty state have regression coverage. `BlockPartyDB`, `HidingSpots`, and `SceneVariables` still depend on Forge/Minecraft 1.19.4 SavedData and level events.
- Affected files/classes: `src/main/java/block_party/db/BlockPartyDB.java`, `src/main/java/block_party/entities/data/HidingSpots.java`, `src/main/java/block_party/scene/SceneVariables.java`, `src/main/java/block_party/db/sql/*.java`.
- Player-facing risk: lost known Moes, names, hidden spots, counters/cookies, shrines, gardens, and NPC records.
- Migration risk: high. SavedData factories, level load/unload events, and server storage APIs are port-sensitive.
- Recommended next prompt/task: "Add world reload tests or a manual golden-world checklist for visible Moe, hidden Moe, known NPC list, shrine list, and scene variables."

### Cell Phone teleport and forced chunk behavior

- Current status: **Open**. Payload shape is covered, but live `CNPCTeleport`, `NPC.findEntity`, `ForcedChunk`, cross-dimension teleport, and `Moe.onTeleport` following behavior are not GameTested.
- Affected files/classes: `src/main/java/block_party/messages/CNPCTeleport.java`, `src/main/java/block_party/db/records/NPC.java`, `src/main/java/block_party/world/chunk/ForcedChunk.java`, `src/main/java/block_party/entities/abstraction/Layer1.java`, `src/main/java/block_party/entities/Moe.java`, `src/main/java/block_party/world/CellPhone.java`.
- Player-facing risk: Cell Phone calls fail to find, teleport, or set Moes to following mode; chunks may remain forced.
- Migration risk: high. Chunk tickets and teleport APIs are version-sensitive.
- Recommended next prompt/task: "Create GameTests for Cell Phone call success, missing Moe failure, forced chunk release, and post-teleport following flag."

### Client UI screens and runtime screen opening

- Current status: **Pure view models covered; runtime UI still manual**. `ViewModelRegressionTest` covers extracted state logic, and packet payload tests cover screen-open packet data. Actual `DialogueScreen`, `YearbookScreen`, and `CellPhoneScreen` rendering/opening remain manual.
- Affected files/classes: `src/main/java/block_party/client/screens/DialogueScreen.java`, `src/main/java/block_party/client/screens/YearbookScreen.java`, `src/main/java/block_party/client/screens/CellPhoneScreen.java`, `src/main/java/block_party/client/screens/state/*.java`, `src/main/java/block_party/messages/SOpenDialogue.java`, `src/main/java/block_party/messages/SOpenYearbook.java`, `src/main/java/block_party/messages/SOpenCellPhone.java`.
- Player-facing risk: companion UI cannot open, navigate, display NPCs, or play dialogue sounds.
- Migration risk: high. Screen, pose stack, widget, and client event APIs shift across versions.
- Recommended next prompt/task: "Create a manual golden-world UI checklist and capture screenshots for Dialogue, Yearbook, Cell Phone, and controller navigation before porting."

### Rendering, model layers, particles, and client-only event hooks

- Current status: **Open/manual**. Client registration has been moved behind `BlockPartyClientEvents`; renderers and particles are not covered by automated tests.
- Affected files/classes: `src/main/java/block_party/client/BlockPartyClientEvents.java`, `src/main/java/block_party/client/BlockPartyRenderers.java`, `src/main/java/block_party/client/renderers/*.java`, `src/main/java/block_party/client/model/*.java`, `src/main/java/block_party/client/particle/*.java`, `src/main/java/block_party/registry/CustomParticles.java`.
- Player-facing risk: invisible Moes, missing particles, broken model layers, broken item property overrides.
- Migration risk: high. NeoForge client setup, model layers, particle providers, and render type APIs differ by target.
- Recommended next prompt/task: "Run a client golden-world pass and document screenshots for Moe rendering, hidden markers, emote/glow/special layers, particles, and Letter item property."

### Build, toolchain, shading, and run configs

- Current status: **Open for port**. Current Forge 1.19.4 build works; NeoForge build migration is not started.
- Affected files/classes: `build.gradle`, `gradle.properties`, `settings.gradle` if introduced later, `src/main/resources/META-INF/mods.toml`, `src/main/resources/META-INF/accesstransformer.cfg`.
- Player-facing risk: mod fails to build, load, include SQLite, or expose resources.
- Migration risk: very high. This is the foundation for all other port work.
- Recommended next prompt/task: "Create a NeoForge build migration plan with target Minecraft version, dependency coordinates, SQLite shading strategy, and run task parity."

## 3. Should Test Before Port

### Spawn lifecycle and ownership

- Current status: **GameTested for basic valid/invalid spawn**. More edge cases remain.
- Affected files/classes: `src/main/java/block_party/items/CustomSpawnEggItem.java`, `src/main/java/block_party/entities/Moe.java`, `src/main/java/block_party/entities/abstraction/Layer2.java`, `src/main/java/block_party/db/records/NPC.java`, `src/main/java/block_party/gametest/MoeSpawnGameTests.java`.
- Player-facing risk: spawn egg consumes the wrong block, fails to preserve block identity, or claims ownership incorrectly.
- Migration risk: medium to high. Tags, entity creation, DB row creation, and block state serialization are all port-sensitive.
- Recommended next prompt/task: "Expand MoeSpawnGameTests for block entity data, non-east faces, occupied spawn positions, owner list persistence, and DB row contents."

### Full hide/reveal persistence paths

- Current status: **GameTested for basic hide, timed reveal, disturbance reveal, and missing no-op**. Save/reload and multi-dimension behavior remain untested.
- Affected files/classes: `src/main/java/block_party/entities/Moe.java`, `src/main/java/block_party/entities/MoeInHiding.java`, `src/main/java/block_party/entities/data/HidingSpots.java`, `src/main/java/block_party/entities/goals/HideUntil.java`, `src/main/java/block_party/gametest/MoeHideGameTests.java`.
- Player-facing risk: hidden Moes disappear, duplicate, reveal wrong, or leave blocks behind.
- Migration risk: high. Entity NBT, SavedData, block events, and dimension storage are all port-sensitive.
- Recommended next prompt/task: "Add save/load or golden-world tests for hidden Moes across chunk unload, server restart, and dimension-specific hidden spots."

### Dialogue interaction flow

- Current status: **JSON parsing and payload contracts covered; live interaction not covered**.
- Affected files/classes: `src/main/java/block_party/scene/SceneManager.java`, `src/main/java/block_party/scene/Scene.java`, `src/main/java/block_party/scene/actions/SendDialogue.java`, `src/main/java/block_party/scene/actions/SendResponse.java`, `src/main/java/block_party/messages/CDialogueRespond.java`, `src/main/java/block_party/messages/SOpenDialogue.java`, `src/main/resources/data/block_party/scenes/*.json`.
- Player-facing risk: right-click dialogue fails, response selection does not advance, sounds/emotions do not apply.
- Migration risk: high. Combines resource reload, entity interaction, packet handling, and client UI.
- Recommended next prompt/task: "Add a GameTest or scripted manual checklist for right-clicking a Moe through all dialogue pages and closing the conversation."

### Yearbook ownership and offline-owner behavior

- Current status: **Open**. `YearbookItem.interactLivingEntity` still compares `player.equals(npc.getPlayer())`; behavior when `npc.getPlayer()` is null/offline should be tested before changing.
- Affected files/classes: `src/main/java/block_party/items/YearbookItem.java`, `src/main/java/block_party/entities/abstraction/Layer3.java`, `src/main/java/block_party/messages/SOpenYearbook.java`, `src/main/java/block_party/client/screens/YearbookScreen.java`.
- Player-facing risk: using a Yearbook on a Moe with an unavailable owner can fail or expose incorrect controls.
- Migration risk: medium. Player lookup and client/server ownership checks may change.
- Recommended next prompt/task: "Add GameTests for Yearbook use on owned Moe, unowned/offline-owner Moe, and other-player Moe before fixing null/ownership handling."

### Cell Phone and Yearbook live flows

- Current status: **Payloads and view models covered, live flows open**.
- Affected files/classes: `src/main/java/block_party/items/CellPhoneItem.java`, `src/main/java/block_party/items/YearbookItem.java`, `src/main/java/block_party/messages/CNPCRequest.java`, `src/main/java/block_party/messages/SNPCResponse.java`, `src/main/java/block_party/messages/CNPCTeleport.java`, `src/main/java/block_party/client/screens/CellPhoneScreen.java`, `src/main/java/block_party/client/screens/YearbookScreen.java`.
- Player-facing risk: companion management tools appear empty, fail to request records, fail to remove pages, or fail to call Moes.
- Migration risk: high. Networking, UI, DB, and teleport are all involved.
- Recommended next prompt/task: "Add server-side GameTests for CNPCRequest/SNPCResponse and CRemovePage, then manual UI checks for screen behavior."

### Personality/profile generation and resource-loaded names

- Current status: **Partially covered by trait and persistence tests**. Full spawn-time distribution and data-loaded names are not covered.
- Affected files/classes: `src/main/java/block_party/entities/abstraction/Layer2.java`, `src/main/java/block_party/entities/abstraction/Layer4.java`, `src/main/java/block_party/registry/resources/Names.java`, `src/main/java/block_party/db/records/NPC.java`, `src/main/resources/data/block_party/moes/names/*.json`.
- Player-facing risk: Moes lose names, traits, sounds, emotions, scale, or profile flavor.
- Migration risk: medium. Resource loading and registry-backed traits are port-sensitive.
- Recommended next prompt/task: "Add regression tests for generated NPC profile fields and GameTests for spawn-created names/traits loaded from data resources."

### Resource reload warning-free baseline

- Current status: **Improved**. The previous GameTest resource reload errors from null block/sound lookup were not present in the latest run. This should be made explicit in tests or checklist.
- Affected files/classes: `src/main/java/block_party/registry/resources/Scenes.java`, `Names.java`, `BlockAliases.java`, `MoeSounds.java`, `MoeTextures.java`, `src/main/resources/data/block_party/**`, `src/main/resources/data/minecraft/**`.
- Player-facing risk: missing names, textures, sounds, aliases, or scenes.
- Migration risk: high. Resource reload APIs change during port.
- Recommended next prompt/task: "Add a resource reload regression test or GameTest assertion that bundled resources load expected counts and no known parse errors."

### Favorite locations, shrines, gardens, and saplings

- Current status: **Open/uncertain**. `Shrines`, `Locations`, `Gardens`, and `NPCs` are created on world load; `Saplings` schema exists, but `Saplings.create(level)` was not found in `BlockPartyDB.onWorldLoad`.
- Affected files/classes: `src/main/java/block_party/db/BlockPartyDB.java`, `src/main/java/block_party/db/records/Shrine.java`, `Location.java`, `Garden.java`, `Sapling.java`, `src/main/java/block_party/blocks/entity/SakuraSaplingBlockEntity.java`.
- Player-facing risk: favorite locations or sapling-linked records may fail to persist or be discoverable.
- Migration risk: medium. DB schema initialization and block entity persistence need parity.
- Recommended next prompt/task: "Add tests to document current shrine/location/garden/sapling table creation and decide whether missing Saplings.create is a bug or unused content."

### Entity synced data and save/load coverage

- Current status: **Partially covered through spawn/hide tests**. Full synced fields are not covered.
- Affected files/classes: `src/main/java/block_party/entities/abstraction/Layer1.java` through `Layer7.java`, `src/main/java/block_party/entities/Moe.java`, `src/main/java/block_party/entities/MoeInHiding.java`.
- Player-facing risk: following state, visible block, emotion, animation, profile, health/food/stats, and hidden marker state can desync.
- Migration risk: high. Synched entity data serializers and IDs are port-sensitive.
- Recommended next prompt/task: "Add entity NBT/synced-data regression tests for all player-visible fields before any entity porting."

## 4. Safe To Defer

### Markov action registration and weighted selection

- Current status: **Open**. `Markov` exists but is not registered in `SceneCodecRegistries` or current `SceneActions`; weighted selection also looked suspicious in earlier audit notes.
- Affected files/classes: `src/main/java/block_party/scene/actions/Markov.java`, `src/main/java/block_party/registry/SceneCodecRegistries.java`, `src/main/java/block_party/registry/SceneActions.java`.
- Player-facing risk: low for current content unless a scene references `markov`.
- Migration risk: low if kept unused; medium if registered during port.
- Recommended next prompt/task: "Decide whether Markov is planned content; if yes, write tests for current behavior before registering or fixing it."

### Senpai entity

- Current status: **Open**. `Senpai` exists but is not registered in `CustomEntities`.
- Affected files/classes: `src/main/java/block_party/entities/Senpai.java`, `src/main/java/block_party/registry/CustomEntities.java`.
- Player-facing risk: low unless content expects Senpai to spawn.
- Migration risk: low if deferred as unused.
- Recommended next prompt/task: "Classify Senpai as planned content or remove/defer it from port scope; do not register it during the loader port without gameplay tests."

### Creative tab refactor

- Current status: **Open**. Old creative tab code is commented out in `BlockParty`.
- Affected files/classes: `src/main/java/block_party/BlockParty.java`, `src/main/java/block_party/registry/CustomItems.java`.
- Player-facing risk: medium for discoverability, low for core survival behavior.
- Migration risk: medium because creative tab APIs changed after 1.19.
- Recommended next prompt/task: "Defer until after core port unless release requires creative inventory polish; then add a manual checklist for item discoverability."

### Render type polish for decorative blocks

- Current status: **Open/manual**. Earlier audit noted render type concerns; no automated coverage exists.
- Affected files/classes: `src/main/java/block_party/client/BlockPartyRenderers.java`, decorative block/model/resource files under `src/main/resources/assets/block_party/**`.
- Player-facing risk: visual glitches, transparency issues, or missing polish.
- Migration risk: medium.
- Recommended next prompt/task: "Manual golden-world visual pass after core rendering is ported."

### Generated SQL/debug console noise

- Current status: **Open**. Table creation and SQL updates print to output during GameTests.
- Affected files/classes: `src/main/java/block_party/db/sql/Table.java`, `src/main/java/block_party/db/sql/Row.java`.
- Player-facing risk: none in normal gameplay unless logs become excessive.
- Migration risk: low.
- Recommended next prompt/task: "Defer logging cleanup until after persistence behavior is locked by tests."

## 5. Design/Content Backlog, Not Technical Debt

### Chores

- Current status: **Backlog/unfinished design**. The README/behavior docs describe chores, but current code appears to expose mostly profile fields, sounds, and update hooks rather than concrete chore gameplay.
- Affected files/classes: `src/main/java/block_party/entities/abstraction/Layer7.java`, `src/main/java/block_party/scene/*`, future AI/goal classes not yet present.
- Player-facing risk: expectation mismatch if release messaging promises chores.
- Migration risk: low if treated as out of scope for port; high if implemented during port.
- Recommended next prompt/task: "Write a design spec for chore behavior after the NeoForge port is stable; keep port parity focused on existing behavior."

### Pranks

- Current status: **Backlog/unfinished design**. No confirmed prank AI loop was found; preserve sounds, scene hooks, traits, and future-facing data.
- Affected files/classes: `src/main/java/block_party/entities/abstraction/Layer7.java`, `src/main/java/block_party/registry/resources/MoeSounds.java`, `src/main/java/block_party/scene/*`.
- Player-facing risk: expectation mismatch if marketed as current gameplay.
- Migration risk: low if deferred.
- Recommended next prompt/task: "Create prank content requirements separately from the port, with tests for any world-changing behavior."

### Adventuring and following AI

- Current status: **Backlog/partial support**. There is a following synced flag and Cell Phone teleport support, but no complete visible follow/adventure AI loop was found.
- Affected files/classes: `src/main/java/block_party/entities/abstraction/Layer3.java`, `Layer7.java`, `src/main/java/block_party/entities/Moe.java`, `src/main/java/block_party/messages/CNPCTeleport.java`, `src/main/java/block_party/world/CellPhone.java`.
- Player-facing risk: players may expect richer companion behavior than currently implemented.
- Migration risk: medium if mixed into port; low if deferred.
- Recommended next prompt/task: "After teleport/follow parity is tested, write a separate adventuring AI design and implementation plan."

### Senpai and story/NPC expansion

- Current status: **Backlog/planned content unless proven otherwise**. `Senpai` is code scaffolding, not registered gameplay.
- Affected files/classes: `src/main/java/block_party/entities/Senpai.java`, future scenes/resources.
- Player-facing risk: none unless content references it.
- Migration risk: low if excluded from port.
- Recommended next prompt/task: "Decide narrative role and spawn rules for Senpai after the port; do not make it part of technical debt triage."

### New dialogue/content authoring

- Current status: **Backlog**. Current scenes appear test/demo-like. The technical need is preserving scene parsing; expanding content is separate.
- Affected files/classes: `src/main/resources/data/block_party/scenes/*.json`, `src/main/java/block_party/scene/*`.
- Player-facing risk: limited content depth, not a port blocker.
- Migration risk: low if deferred.
- Recommended next prompt/task: "Create a content roadmap for production dialogue after the scene parser and UI are stable."

## Suggested Order Of Attack

1. Finish release blockers that are already partially solved: remove tracked logs, broaden `JsonUtils` lookup support, and add scene ID coverage for every bundled resource.
2. Add missing pre-port GameTests: hidden save/reload, dialogue interaction, Yearbook ownership, Cell Phone teleport/forced chunk release, and entity synced-data persistence.
3. Stabilize port-sensitive foundations: build/toolchain/shading, custom scene registries, resource reload, SavedData/SQLite lifecycle, and networking.
4. Run the manual golden-world checklist for client rendering, UI, sounds, particles, and full spawn/talk/hide/reveal/call flows.
5. Start NeoForge migration only after the above release and parity tests pass on the Forge 1.19.4 baseline.
6. Treat chores, pranks, richer adventuring, Senpai, and new dialogue as post-port content work.
