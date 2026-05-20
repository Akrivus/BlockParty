# Block Party Architecture

This document is a developer orientation for the current Block Party codebase. It is based on static reading only. Where behavior is unclear, unfinished, or contradicted by code, this document flags that uncertainty instead of filling in intent.

## What The Mod Is

Block Party is a Forge 1.19.4 Minecraft mod that personifies blocks as small NPC companions called Moes. In gameplay terms, the player can use `CustomSpawnEggItem` on a block tagged as `block_party:spawns_moes`; the block is destroyed, a `Moe` entity appears nearby, and the Moe keeps the original block's identity, block entity NBT, texture, scale, traits, sounds, owner, and database record.

The README describes Moes as affectionate block people who can bring food, pull pranks, perform chores, and adventure with players. In the code currently present, the strongest implemented gameplay loop is:

- spawn a Moe from a tagged block with `block_party.items.CustomSpawnEggItem`
- assign block-derived traits, sounds, name, player ownership, and SQLite-backed persistence
- interact through data-driven scenes and `DialogueScreen`
- hide by becoming the original block again while a hidden marker entity tracks the database ID
- re-spawn from hiding when the block is exposed, broken, moved, or timed out
- inspect/call NPC records with yearbook/cell phone controller screens

I did not find concrete chores, prank, or adventure AI implementations beyond data fields, sounds, `isFollowing`, empty update hooks, and README intent.

## Entity And NPC Lifecycle

### Spawn From A Block

The concrete block-to-Moe path lives in `src/main/java/block_party/items/CustomSpawnEggItem.java`.

1. `CustomSpawnEggItem.useOn(UseOnContext)` runs only server-side.
2. It checks whether the clicked block state is in `CustomTags.Blocks.SPAWNS_MOES`, backed by `src/main/resources/data/block_party/tags/blocks/spawns_moes.json`.
3. It creates `CustomEntities.MOE`, registered in `src/main/java/block_party/registry/CustomEntities.java` as `EntityType<Moe>`.
4. It moves the Moe to the adjacent face, sets a preliminary database ID to `pos.asLong()`, and calls `moe.setBlockState(state)`.
5. If the original block has a block entity, it copies `level.getBlockEntity(pos).getPersistentData()` into `moe.setTileEntityData(...)`.
6. It randomizes `Dere`, weighs `BloodType`, assigns the player, calls `moe.claim(player)`, adds the entity, destroys the source block without drops, and consumes the spawn egg.

The DB claim path is split across `Recordable`, `Layer5`, `NPC`, and `BlockPartyDB`:

- `block_party.db.Recordable.claim(Player)` assigns player UUID and inserts a row if needed.
- `block_party.entities.abstraction.Layer5.claim(Player)` only calls `Recordable.super.claim` if `syncWithDB` is true.
- `block_party.entities.Moe` enables DB sync in its constructor with `doSyncWithDatabase(true)`.
- `block_party.db.records.NPC.sync(BlockPartyNPC)` copies entity fields into SQL columns.

There is another spawn-like path in `src/main/java/block_party/blocks/entity/ShrineTabletBlockEntity.java`, where a shrine/tablet effect creates a `Moe`, sets its block state to `Blocks.BELL`, claims it, and spawns it. I did not fully verify shrine gameplay from placement through use, so treat that as a secondary lifecycle path.

### Normal Behavior

Most Moe behavior is inherited from `BlockPartyNPC`, which extends the stacked abstraction classes in `src/main/java/block_party/entities/abstraction/`.

- `Layer1`: pathfinding malus setup, home position, teleport helper, sound hooks, distance persistence.
- `Layer2`: block state identity, visible alias block state, scale, corporeal/ethereal state, block-derived navigation changes, hide-as-block behavior.
- `Layer3`: player ownership and following state.
- `Layer4`: synchronized profile stats and traits such as `BloodType`, `Dere`, `Zodiac`, `Emotion`, `Gender`, name, food, stress, loyalty, affection, age.
- `Layer5`: SQLite row sync.
- `Layer6`: 36-slot inventory and chest menu behavior.
- `Layer7`: scene manager, dialogue state, client animation, interaction triggers, and placeholder needs/action/sleep update methods.

The main per-tick behavior is in `Layer7.aiStep()`:

- client side: ticks the current `AbstractAnimation`
- server side: ticks `SceneManager`, then calls `updateHungerState`, `updateLonelyState`, `updateStressState`, `updateActionState`, and `updateSleepState`

Those five update methods are empty in `Layer7`, so hunger/loneliness/stress/action/sleep systems are currently scaffolding, not implemented behavior.

Additional server AI triggers are in `Layer7.customServerAiStep()`:

- random 1-in-20 tick chance triggers `SceneTrigger.RANDOM_TICK`
- if the owner is looking at the Moe, triggers `SceneTrigger.STARE`

Player interactions are converted into scene triggers in `Layer7`:

- owner right-click: `RIGHT_CLICK`
- owner shift-right-click: `SHIFT_RIGHT_CLICK`
- owner left-click damage attempt: `LEFT_CLICK`
- owner shift-left-click: `SHIFT_LEFT_CLICK`
- non-owner damage passes through to normal damage with block hardness scaling and may trigger `HURT`

### Dialogue And Interaction

Scene and dialogue behavior is data-driven:

- scene reload listener: `block_party.registry.resources.Scenes`
- scene manager: `block_party.scene.SceneManager`
- triggers: `block_party.scene.SceneTrigger`
- actions: `block_party.scene.ISceneAction` and `block_party.scene.actions.*`
- filters: `block_party.scene.ISceneObservation` and `block_party.scene.observations.*`
- action/filter registries: `block_party.registry.SceneActions`, `block_party.registry.SceneFilters`

The current resource scenes under `src/main/resources/data/block_party/scenes/` are test content only:

- `test_dialogue.json`: right-click dialogue chain
- `test_hide.json`: left-click hide action

`SendDialogue` constructs a `Dialogue` from JSON, opens it for the owning player with `SOpenDialogue`, and waits until a response arrives. `DialogueScreen.RespondButton` sends `CDialogueRespond`; the server loads the NPC row through `CNPCQuery`, finds the server entity, and calls `BlockPartyNPC.setResponse`.

### Hiding And Retreat

Hiding is a two-entity/block lifecycle:

1. `block_party.scene.actions.Hide` calls `npc.hide(HideUntil)`.
2. `Moe.hide(HideUntil)` marks the NPC database row's `HIDING` column true, creates a `MoeInHiding` ghost, attaches it to the current block position, and adds the hiding spot to `HidingSpots`.
3. If the ghost spawns successfully, `Layer2.hide(HideUntil)` places the original `actualBlockState` back into the world and removes the Moe with `RemovalReason.DISCARDED`.
4. `MoeInHiding.tick()` stays fixed at the attached block position, increments `ticksHidden`, and calls `spawn()` when `HideUntil.isOver(...)` returns true.
5. `MoeInHiding.spawn()` creates a new `Moe`, triggers `SceneTrigger.HIDING_SPOT_DISCOVERED`, loads the DB row into it, restores the block state and block entity persistent data, destroys the block, adds the Moe entity, and kills the ghost marker if spawning succeeded.

Hidden markers are tracked in `block_party.entities.data.HidingSpots`, a `SavedData` object keyed by block position to database ID. It listens for:

- `PlayerInteractEvent.LeftClickBlock`
- `BlockEvent.BreakEvent`
- `PistonEvent.Pre`
- `EntityJoinLevelEvent` for `FallingBlockEntity`

When those events touch a hidden block, `HidingSpots.spawn(...)` tries to reveal the Moe.

Resolved note: `MoeInHiding.readAdditionalSaveData` now restores `HideUntil` from the saved NBT string. Timed hide save/load should still be kept in parity coverage during persistence API migration.

### Saving And Loading

There are three persistence layers:

- vanilla entity NBT, via each `Layer*.addAdditionalSaveData/readAdditionalSaveData`
- world `SavedData`, via `BlockPartyDB` and `HidingSpots`
- SQLite, via `block_party.db.sql.Table`, `Row`, `Column`, and record schemas in `block_party.db.records`

`BlockPartyDB` stores lightweight saved-data lists such as claimed names and player-to-NPC IDs, while actual records live in `blockparty.db` using SQLite. `BlockPartyDB.onWorldLoad` initializes SQLite and creates tables for shrines, locations, gardens, and NPCs. `Saplings` exists as a schema field but I did not see `Saplings.create(level)` called in `onWorldLoad`.

NPC SQL data lives in `block_party.db.records.NPC`. It stores owner UUID, name, block state, traits, health, food/stress-like stats, last seen, home, shrine reference, dead flag, and hiding flag. `NPC.load(BlockPartyNPC)` pushes those values back into a new or loaded entity.

`Layer5.onSyncedDataUpdated` updates the DB row whenever synced data changes and `hasRow()` is true. This is broad and convenient, but risky because synced-data churn can become database churn.

### Removal

There are several removal concepts:

- normal entity removal when a Moe hides: `Layer2.hide` places the block and discards the entity.
- hidden marker removal: `MoeInHiding.kill()` removes its entry from `HidingSpots` then calls `super.kill()`.
- death while corporeal: `Layer2.die` calls `hide(HideUntil.EXPOSED)` after normal death.
- hidden block destroyed while air: `MoeInHiding.hurt` marks the DB row `DEAD` and kills the marker.
- UI removal: `CNPCRemove` removes an NPC ID from the player's list only if `NPC.isDeadOrEstrangedFrom(player)`.

I did not find a complete hard-delete flow for NPC database rows during normal gameplay. `Row.delete()` exists, but I did not see it used in the NPC removal path.

## Major Systems

### Block, Entity, Item, And Resource Registration

Entry point: `src/main/java/block_party/BlockParty.java`.

Important registries:

- `BlockParty.BLOCKS`, `ITEMS`, `ENTITIES`, `BLOCK_ENTITIES`, `PARTICLES`, `SOUNDS`, `WORLDGEN_FEATURES`
- custom registry builders for scene actions and filters: `BlockParty.ACTIONS`, `BlockParty.FILTERS`
- packet channel: `BlockParty.MESSENGER`

Registration classes:

- `block_party.registry.CustomBlocks`
- `block_party.registry.CustomItems`
- `block_party.registry.CustomEntities`
- `block_party.registry.CustomBlockEntities`
- `block_party.registry.CustomParticles`
- `block_party.registry.CustomSounds`
- `block_party.registry.CustomWorldGen`
- `block_party.registry.CustomResources`
- `block_party.client.BlockPartyRenderers`

The mod uses Forge `DeferredRegister` and `RegistryObject`. It targets Forge `1.19.4-45.2.0` with official mappings and Java 17 in `build.gradle`.

### Moe And Entity Classes

Concrete classes:

- `block_party.entities.BlockPartyNPC`: base NPC class, name display, honorific, block tag name.
- `block_party.entities.Moe`: concrete block-person NPC, DB sync enabled, hide override, sounds.
- `block_party.entities.MoeInHiding`: invisible/marker entity attached to a block while a Moe is hiding.
- `block_party.entities.Senpai`: currently just extends `BlockPartyNPC`; I did not find it registered in `CustomEntities`.

Layer classes:

- `Layer1` through `Layer7` under `src/main/java/block_party/entities/abstraction/`

### Personality And Profile Generation

Profile state is mostly in `Layer4`.

Traits and profile enums:

- `block_party.scene.traits.Gender`
- `block_party.scene.traits.BloodType`
- `block_party.scene.traits.Dere`
- `block_party.scene.traits.Zodiac`
- `block_party.scene.traits.Emotion`

Data sources:

- names: `block_party.registry.resources.Names`, loading `data/*/moes/names`
- block trait tags: `block_party.registry.CustomTags` and JSON under `src/main/resources/data/block_party/tags/blocks/moe/...`

Generation flow:

- `Moe` constructor initially sets a unique gender-based given name.
- `Layer4.finalizeSpawn` also sets a unique name and weighted blood type.
- `CustomSpawnEggItem` sets block state first, then randomizes `Dere`, weighs `BloodType`, assigns player, and claims DB row.
- `Layer4.setAdditionalBlockStateData` derives gender, blood type, dere, and zodiac from block tags.

Risk note: name assignment appears duplicated between `Moe` constructor and `Layer4.finalizeSpawn`. Static reading cannot prove which path wins in all spawn cases.

### Dialogue And Conversation System

Core files:

- `block_party.scene.SceneManager`
- `block_party.scene.Scene`
- `block_party.scene.Dialogue`
- `block_party.scene.Response`
- `block_party.scene.Speaker`
- `block_party.scene.SceneTrigger`
- `block_party.registry.resources.Scenes`
- `block_party.scene.actions.SendDialogue`
- `block_party.scene.actions.SendResponse`
- `block_party.client.screens.DialogueScreen`
- `block_party.messages.SOpenDialogue`
- `block_party.messages.CDialogueRespond`
- `block_party.messages.CDialogueClose`

Scenes are JSON resources under `data/*/scenes`. Each has a trigger, filters, and actions. Filters must all pass. Matching scenes are shuffled and one fulfilled scene is selected.

Supported registered actions include dialogue, response, health/food/loyalty/stress mutation, cookie/counter mutation, hide, and end. `Markov` exists but is not registered in `SceneActions`, so I did not find a JSON entry point for it.

### Networking Packets

Network setup:

- `block_party.registry.CustomMessenger`
- `block_party.messages.AbstractMessage`

Server-bound packet registrations in `CustomMessenger.registerServer`:

- `CDialogueClose`
- `CDialogueRespond`
- `CNPCRemove`
- `CNPCRequest`
- `CNPCTeleport`
- `CRemovePage`

Client-bound packet registrations in `CustomMessenger.registerClient`:

- `SCloseDialogue`
- `SNPCList`
- `SNPCResponse`
- `SOpenCellPhone`
- `SOpenDialogue`
- `SOpenYearbook`
- `SShrineList`

Files present but not registered in the current messenger setup:

- `CNPCQuery` is abstract base behavior for server-bound NPC lookup.
- `SOpenController` is base behavior for controller screens.

Potential issue: the code uses `AbstractMessage.Server` for client-bound messages and `AbstractMessage.Client` for server-bound messages, which is semantically confusing even if it works through overridden handlers.

### Client Rendering And UI

Renderer entry:

- `block_party.client.BlockPartyRenderers`

Moe rendering:

- `block_party.client.renderers.MoeRenderer`
- `block_party.client.model.MoeModel`
- `block_party.client.renderers.layers.EmoteLayer`
- `block_party.client.renderers.layers.GlowLayer`
- `block_party.client.renderers.layers.SpecialLayer`
- `block_party.client.renderers.layers.special.BarrelOverlay`

Hidden marker rendering:

- `block_party.client.renderers.MoeInHidingRenderer`

Animations:

- `block_party.client.animation.Animation`
- `block_party.client.animation.AbstractAnimation`
- `block_party.client.animation.state.DefaultAnimation`
- `WaveAnimation`
- `YearbookAnimation`

Screens:

- `block_party.client.screens.DialogueScreen`
- `ControllerScreen`
- `CellPhoneScreen`
- `YearbookScreen`
- `AbstractScreen`
- widgets under `block_party.client.screens.widget`

Resource-driven rendering:

- `block_party.registry.resources.MoeTextures` maps block states to Moe textures, with fallback paths like `textures/moe/<block>.png`.
- `block_party.registry.resources.BlockAliases` maps blocks to alternate visible Moe block states.
- `block_party.registry.resources.MoeSounds` maps block-specific Moe sounds, with defaults from `CustomSounds`.

### Database And Persistence

Core DB files:

- `block_party.db.BlockPartyDB`
- `block_party.db.Recordable`
- `block_party.db.DimBlockPos`
- `block_party.db.sql.Table`
- `block_party.db.sql.Row`
- `block_party.db.sql.Column`

Record schemas:

- `block_party.db.records.NPC`
- `Location`
- `Garden`
- `Shrine`
- `Sapling`

SQLite dependency:

- `build.gradle` integrates `org.xerial:sqlite-jdbc:3.40.1.0`
- `shadowJar` relocates `org.sqlite` to `block_party.org.sqlite`

`BlockPartyDB` opens the DB at the world path `blockparty.db`, creates tables on world load, and closes tracked connections on world unload.

Risk note: SQL strings are assembled manually in several places. Some values use prepared-statement columns, but table names and selected IDs are interpolated into SQL strings. Static reading suggests the ID sources are internal numeric values, so exploitability may be low, but porting should preserve behavior while tightening safety later.

### Chores, Pranks, And Adventuring

The README claims Moes bring food, pull pranks, perform chores, and tag along on adventures. In code, I found only supporting scaffolding:

- follow state: `Layer3.FOLLOWING`, `Layer3.isFollowing`, `Layer3.setFollowing`
- cell-phone teleport/call: `CNPCTeleport`, `CellPhone`, `ForcedChunk`, `Moe.onTeleport`
- stat fields: food, exhaustion, saturation, stress, relaxation, loyalty, affection, age in `Layer4`
- empty update hooks: `Layer7.updateHungerState`, `updateLonelyState`, `updateStressState`, `updateActionState`, `updateSleepState`
- sounds such as `MOE_FEED`, `MOE_GRIEF`, `MOE_FOLLOW`, `MOE_EAT`, `MOE_SLEEPING`
- inventory/menu support in `Layer6`

I did not find concrete AI goals for following, chores, pranks, gifting food, combat adventuring, or sleep. Treat those as unfinished or planned unless there are files outside the scanned `src/main/java/block_party` tree.

## Stable, Unfinished, Duplicated, And Risky Areas

### Appears Stable Or Coherent

- Forge registration structure in `BlockParty`, `CustomBlocks`, `CustomItems`, `CustomEntities`, and related registries is conventional for Forge 1.19.x.
- The layered entity design makes responsibilities discoverable despite being deep.
- Block-to-Moe spawn and hide/reveal lifecycle is implemented end-to-end.
- Resource reload listeners for scenes, names, textures, sounds, and aliases are clear.
- Client rendering has a defined model/layer/screen organization.
- SQLite row abstraction is consistently used by NPC and block-record classes.

### Unfinished Or Placeholder

- Creative tab code in `BlockParty` is commented out with `TODO: Refactor creative tabs`.
- `CustomBlocks.registerRenderTypes` is entirely commented out.
- `Layer7.updateHungerState`, `updateLonelyState`, `updateStressState`, `updateActionState`, and `updateSleepState` are empty.
- Chores/pranks/adventuring/follow AI are not implemented in the scanned code.
- `Senpai` exists but is not registered as an entity.
- `SceneActions.Markov` exists but is not registered.
- `BlockPartyDB.Saplings` exists but `Saplings.create(level)` is not called in `onWorldLoad`.
- `SNPCList.handle` is empty.
- `DialogueScreen.renderTooltips` contains a commented `???`.

### Duplicated Or Confusing

- Name/blood generation happens in multiple places: `BlockPartyNPC` constructor, `Moe` constructor, `Layer4.finalizeSpawn`, and `CustomSpawnEggItem`.
- `Layer1.doHurtTarget` and `Layer7.doHurtTarget` previously called back into their own overrides and could stack overflow. They now delegate to `super.doHurtTarget(target)` before applying layer-specific behavior.
- `AbstractMessage.Client` and `AbstractMessage.Server` names appear reversed relative to packet direction use.
- `Moe.getFamilyName` derives a block translation key, while `Layer4.getFamilyName` returns `"Minashigo"`; this is probably intentional override behavior, but worth preserving in tests.

### Risky Or Bug-Prone

- `MoeInHiding.readAdditionalSaveData` now restores the saved hide condition from NBT; keep this fixed behavior covered during save/load migration.
- `HidingSpots` maps `BlockPos` only, even though `spawn(ServerLevel, DimBlockPos)` receives a dimensional position. Because each `SavedData` instance is per `ServerLevel` in `HidingSpots.get`, this may be acceptable, but it should be verified during migration.
- `HidingSpots.get(ServerLevel, BlockPos)` returns `spot.list.get(pos)` as primitive `long`; callers rely on `isNormalBlock` first to avoid unboxing null. The order is fragile.
- `SceneManager.trigger` only replaces the current scene if the incoming priority is greater than the current trigger. Equal-priority triggers are ignored until the active action clears.
- `SceneManager.setAction(null)` calls `onComplete` on the existing action. For `SendDialogue`, that can enqueue response follow-up behavior during interruption; verify whether that is intended.
- `Layer5.onSyncedDataUpdated` writes DB updates broadly and may do so often.
- `Column.set` uses reference comparison (`this.value != value`) rather than equality. For boxed, string, enum, block-state, or position values this can mark dirty unexpectedly or miss logical equality.
- `Row.update()` builds SQL from dirty columns and assumes at least one dirty column; if no columns are dirty, `getColumnSetters` would substring an empty string.
- `MoeTextures.get` appears to call `CustomResources.MOE_TEXTURES.map.getOrDefault(state, ...)` where the map key type is `Block`, not `BlockState`. This may cause fallback textures to be used more often than intended.
- `Markov.chain` stores entries by `probability` instead of cumulative `total`, which likely breaks weighted selection if it is ever used.

## Proposed Test Plan Before Porting

Start with behavior-preserving tests and manual smoke tests in the current Forge version before changing loader or Minecraft version.

1. Build and launch smoke test
   - Run Gradle build/client once on the current code.
   - Record any existing compile/runtime failures as baseline.
   - Verify `mods.toml`, assets, data packs, SQLite relocation, and resource reloads load without hard crashes.

2. Spawn lifecycle test
   - In a test world, place a block in `block_party:spawns_moes`.
   - Use `CustomSpawnEggItem`.
   - Verify source block is removed, `Moe` appears, owner UUID is set, block state is preserved, texture fallback works, name is assigned, and a row exists in `blockparty.db`.

3. Trait/profile test
   - Spawn Moes from blocks tagged for gender, blood type, dere, zodiac, wings, glow, cat features, and ignores-volume.
   - Verify `Layer4.setAdditionalBlockStateData`, `Layer2.setBlockState`, sound selection, scale, navigation type, and renderer layers.

4. Dialogue scene test
   - Use `test_dialogue.json` through right-click.
   - Verify `SceneManager.trigger`, `SOpenDialogue`, `DialogueScreen`, `CDialogueRespond`, chained responses, and `End` behavior.
   - Add a scene with filters/counters/cookies before porting if this system is intended to survive.

5. Hide/reveal test
   - Use `test_hide.json` through left-click.
   - Verify Moe becomes a block, `MoeInHiding` exists, `HidingSpots` saved data records the position, and the Moe returns after `ONE_SECOND_PASSES`.
   - Repeat for break-start, break-end, piston push, falling block, chunk unload/reload, and full server restart.

6. Persistence test
   - Spawn, rename/profile-change if possible, hide, save, quit, reload.
   - Verify DB row, player NPC list, `HidingSpots`, entity NBT, and block entity NBT restoration.
   - Specifically test `HideUntil` restoration so the resolved timed-hide restore behavior survives migration.

7. Controller UI test
   - Open `YearbookItem` and `CellPhoneItem`.
   - Verify `SOpenYearbook`, `SOpenCellPhone`, `CNPCRequest`, `SNPCResponse`, `CNPCTeleport`, `ForcedChunk`, and `Moe.onTeleport`.

8. Death/removal test
   - Kill or expose a hidden Moe and inspect `NPC.DEAD`, `NPC.HIDING`, player list removal, and row lifetime.
   - Verify expected behavior before deciding whether NeoForge migration should preserve or fix it.

9. Resource reload/data-pack test
   - Reload names, aliases, textures, sounds, and scenes.
   - Verify malformed JSON failure behavior is documented.
   - Confirm `minecraft` namespace scene/resources are intentionally remapped by `Scenes.own`.

10. Regression harness
   - Add minimal GameTest-style tests if staying on a Minecraft version that supports them cleanly.
   - Where GameTest is too expensive, preserve a manual smoke-test checklist and golden world save with known Moes, hidden Moes, and DB state.

## Proposed NeoForge Migration Plan

Order the migration from lowest-risk mechanical work to highest-risk behavioral changes.

1. Freeze baseline
   - Create a branch and run current build/client smoke tests.
   - Save logs, generated DB schema, a small test world, and screenshots of Moe spawn/dialogue/hide.

2. Update Gradle and metadata first
   - Replace ForgeGradle/Forge coordinates in `build.gradle` with the target NeoForge toolchain.
   - Update `mods.toml` or metadata format only as required by the target Minecraft/NeoForge version.
   - Keep package names and gameplay code unchanged.

3. Port registration wrappers
   - Start with `BlockParty`, `CustomBlocks`, `CustomItems`, `CustomEntities`, `CustomSounds`, `CustomParticles`, `CustomBlockEntities`.
   - Preserve registry names exactly.
   - Keep `CustomResources`, scene action/filter registry names, and packet IDs stable until the mod loads.

4. Port data/resource reload listeners
   - Move `CustomResources`, `Scenes`, `Names`, `MoeTextures`, `MoeSounds`, and `BlockAliases` to the new reload/event APIs.
   - Verify all paths under `data/*/moes`, `data/*/scenes`, and client texture resources still load.

5. Port networking
   - Replace `SimpleChannel` setup and packet registration in `CustomMessenger`.
   - Preserve packet payload fields and direction behavior first, even though class names are confusing.
   - Add logging around unregistered/unused packets only after parity is confirmed.

6. Port entity APIs
   - Update `EntityType`, attributes, synched data, spawn packets, dimensions, navigation, interaction, damage, and NBT method names.
   - Port the `Layer1`-`Layer7` stack without redesigning it.
   - Fix only compile/API breaks at this stage; preserve the already-fixed `doHurtTarget` super-call behavior.

7. Port client rendering
   - Update model layer registration, renderer registration, render layers, pose stack/model APIs, screens, buttons, and sound UI calls.
   - Verify `MoeRenderer`, `MoeModel`, `EmoteLayer`, `GlowLayer`, `SpecialLayer`, `DialogueScreen`, `YearbookScreen`, and `CellPhoneScreen`.

8. Port persistence and world hooks
   - Update `SavedData`, `DimensionDataStorage`, level load/unload events, player login events, and chunk forcing APIs.
   - Keep SQLite schema and `blockparty.db` filename stable.
   - Verify `BlockPartyDB`, `HidingSpots`, `ForcedChunk`, and `CellPhone`.

9. Port tags, data, worldgen, and block entities
   - Update tag locations if the target Minecraft version changed conventions.
   - Port `AbstractDataBlockEntity` and locative/shrine/garden block entities.
   - Port worldgen after core Moe lifecycle works, because worldgen is less central to NPC behavior.

10. Run parity tests
   - Execute the test plan above in the NeoForge branch.
   - Compare DB rows, NBT, visible textures, dialogue behavior, and hide/reveal behavior against the baseline.

11. Only then fix remaining known behavior bugs
   - After parity is measurable, fix remaining risks such as `MoeTextures` key mismatch, empty dirty SQL updates, hidden-spot edge cases, and other open issues.
   - Each fix should get a narrow regression test or manual before/after note.

12. Implement unfinished systems last
   - Chores, pranks, adventuring, hunger/stress/sleep, and richer following should come after the port is stable.
   - These are new behavior, not migration work, and should not be mixed with loader/API migration.
