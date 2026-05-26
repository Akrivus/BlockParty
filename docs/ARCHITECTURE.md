# Block Party Architecture

This document is a developer orientation for the current Block Party codebase. It describes the active NeoForge 21.4 / Minecraft 1.21.4 implementation and calls out old Forge 1.19.4 behavior only when it matters for compatibility or parity.

## What The Mod Is

Block Party is a NeoForge Minecraft mod that personifies blocks as small NPC companions called Moes. The active build targets Minecraft `1.21.4`, NeoForge `21.4.102-beta`, Java 21, and moddev Gradle tooling. In gameplay terms, the player can use `CustomSpawnEggItem` on a block tagged as `block_party:spawns_moes`; the block is destroyed, a `Moe` entity appears nearby, and the Moe keeps the original block's identity, block entity NBT, texture, scale, traits, sounds, owner, and database record.

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
2. It checks whether the clicked block state is in `CustomTags.Blocks.SPAWNS_MOES`, backed by `src/main/resources/data/block_party/tags/block/spawns_moes.json`.
3. It creates `CustomEntities.MOE`, registered in `src/main/java/block_party/registry/CustomEntities.java` as `EntityType<Moe>`.
4. It moves the Moe to the adjacent face and calls `moe.setBlockState(state)`.
5. If the original block has a block entity, it copies `level.getBlockEntity(pos).getPersistentData()` into `moe.setTileEntityData(...)`.
6. It assigns the player, inserts the SQLite-backed NPC row, stores the generated database ID on the Moe, adds the entity, destroys the source block without drops, and consumes the spawn egg in survival only.

The DB claim path is centered on `Moe`, `NPC`, and `BlockPartyDB`, with `Recordable` retained as old-baseline context:

- `block_party.db.Recordable.claim(Player)` is the old Forge baseline for assigning player UUIDs and inserting rows.
- the active `Moe` shell owns DB identity directly and stores the generated row ID as `DatabaseID`.
- `block_party.db.records.NPC` remains the row model for owner, identity, profile, hiding, and position columns.

There is another spawn-like path in `src/main/java/block_party/blocks/entity/ShrineTabletBlockEntity.java`, where shrine/tablet behavior can participate in Moe creation and row/state updates. Treat the normal spawn egg path as the primary supported lifecycle.

### Normal Behavior

The old Forge source used a `BlockPartyNPC` plus `Layer1`-through-`Layer7` inheritance stack. The active NeoForge build has normalized the live entity around `block_party.entities.Moe`, a `PathfinderMob` shell with migrated state for:

- movement/combat attributes, navigation category, home position, teleport helpers, and sound hooks
- source block identity, visible alias block state, scale, block-derived flags, and hide-as-block behavior
- player ownership and following state
- row-backed profile stats and traits such as `BloodType`, `Dere`, `Zodiac`, `Emotion`, `Gender`, name, food, stress, loyalty, affection, and age
- SQLite row identity and explicit row updates
- 36-slot inventory and chest menu behavior
- scene manager, dialogue state, client animation state, and interaction triggers

The current per-tick behavior is intentionally narrower than the old layer stack:

- server side: preserves identity, hidden-state, home/follow flags, inventory/slouch, scene/dialogue state, combat attributes, and row-backed profile fields
- client side: extracts the active state into `MoeRenderState` for model, layer, nameplate, scale, and animation rendering

Player interactions are converted into scene triggers on the active Moe surface:

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

`SendDialogue` constructs a `Dialogue` from JSON, opens it for the owning player with `DialogueOpenPayload`, and waits until a response arrives. `DialogueScreen` sends `DialogueRespondPayload`; the server loads the NPC row, finds the server entity, and records the selected response on the active Moe dialogue state.

### Hiding And Retreat

Hiding is a two-entity/block lifecycle:

1. `block_party.scene.actions.Hide` calls `moe.hide(HideUntil)`.
2. `Moe.hide(HideUntil)` writes the Moe's current identity into the NPC row, marks `Hiding = 1`, stores the hidden position, creates a `MoeInHiding` shell, and adds the hiding spot to `HidingSpots`.
3. The Moe's original `BlockState` is placed back into the world and the visible Moe is discarded.
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

`BlockPartyDB` stores lightweight saved-data lists such as claimed names and player-to-NPC IDs, while actual records live in `blockparty.db` using SQLite. The active server bootstrap initializes SQLite and creates the minimal NPC, shrine, garden, location, and sapling tables needed by the current NeoForge surface.

NPC SQL data lives in `block_party.db.records.NPC`. It stores owner UUID, name, block state, traits, health, food/stress-like stats, last seen, home, shrine reference, dead flag, and hiding flag. Active load/detail services push row values into `Moe` shells and controller payloads.

The old broad synced-data DB-write hook is no longer the preferred mental model for the active shell. Current row writes are explicit around spawn, hide, reveal, list/detail/call/remove services, profile/stat persistence, and block-entity row claims.

### Removal

There are several removal concepts:

- normal entity removal when a Moe hides: `Moe.hide` places the block and discards the visible entity.
- hidden marker removal: `MoeInHiding.kill()` removes its entry from `HidingSpots` then calls `super.kill()`.
- death while corporeal: the active Moe death/dead-row behavior is narrower than the old layer stack and should be verified through GameTests before expanding.
- hidden block destroyed while air: `MoeInHiding.hurt` marks the DB row `DEAD` and kills the marker.
- UI removal: `NpcRemoveRequestPayload` de-lists only owned/allowed NPC records according to the server service rules.

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

The mod uses NeoForge `DeferredRegister` and `DeferredHolder`. It targets NeoForge `21.4.102-beta` for Minecraft `1.21.4` with Java 21 in `build.gradle`. Active metadata lives in `src/main/resources/META-INF/neoforge.mods.toml`.

### Moe And Entity Classes

Active classes:

- `block_party.entities.Moe`: concrete block-person NPC, row-backed identity/profile state, hide override, inventory, sounds, and scene/dialogue state.
- `block_party.entities.MoeInHiding`: invisible/marker entity attached to a block while a Moe is hiding.
- `block_party.entities.MoeSpawner`: helper/service for spawn and call-style restoration/teleport work.

Frozen-reference classes such as `BlockPartyNPC`, `Senpai`, and `Layer1`-through-`Layer7` are no longer active in `src/main/java`. Compatibility notes still refer to them when describing old Forge behavior that the NeoForge shell intentionally preserves.

### Personality And Profile Generation

Profile state is mostly on `Moe` and the row-backed `NPC` record.

Traits and profile enums:

- `block_party.scene.traits.Gender`
- `block_party.scene.traits.BloodType`
- `block_party.scene.traits.Dere`
- `block_party.scene.traits.Zodiac`
- `block_party.scene.traits.Emotion`

Data sources:

- names: `block_party.registry.resources.Names`, loading `data/*/moes/names`
- block trait tags: `block_party.registry.CustomTags` and JSON under `src/main/resources/data/block_party/tags/block/moe/...`

Generation flow:

- `CustomSpawnEggItem` sets block state first, assigns owner/row identity, and lets the active Moe/profile path derive row-backed defaults.
- block tags and social-affinity resources drive visible traits, names, and profile defaults where current code has restored them.

Risk note: name, trait, and social-affinity generation still cross resource loading, spawn-time defaults, row persistence, and old baseline expectations. Keep this covered with focused tests before consolidating.

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
- `block_party.network.payload.DialogueOpenPayload`
- `block_party.network.payload.DialogueRespondPayload`
- `block_party.network.payload.DialogueClosePayload`

Scenes are JSON resources under `data/*/scenes`. Each has a trigger, filters, and actions. Filters must all pass. Matching scenes are shuffled and one fulfilled scene is selected.

Supported registered actions include dialogue, response, health/food/loyalty/stress mutation, cookie/counter mutation, hide, and end. `Markov` exists but is not registered in `SceneActions`, so I did not find a JSON entry point for it.

### Networking Packets

Network setup:

- `block_party.network.CustomMessenger`
- `block_party.network.payload.*`

Server-bound custom payload registrations in `CustomMessenger.registerPayloads`:

- `NpcRemoveRequestPayload`
- `NpcCallRequestPayload`
- `DialogueRespondPayload`
- `DialogueClosePayload`
- `ShrineListRequestPayload`

Client-bound custom payload registrations:

- `NpcCallPayload`
- `ControllerOpenPayload`
- `DialogueOpenPayload`
- `ShrineListPayload`

NeoForge 1.21.4 custom payloads are registered through `RegisterPayloadHandlersEvent` and `PayloadRegistrar`. `DialogueClosePayload` is serverbound because the active transport does not allow the same payload ID to be registered in both directions.

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
- NeoForge jar-in-jar includes `org.xerial:sqlite-jdbc:[3.40.1.0,3.41)`

`BlockPartyDB` opens the DB at the world path `blockparty.db`, creates tables on world load, and closes tracked connections on world unload.

Risk note: SQL strings are assembled manually in several places. Some values use prepared-statement columns, but table names and selected IDs are interpolated into SQL strings. Static reading suggests the ID sources are internal numeric values, so exploitability may be low, but future cleanup should preserve behavior while tightening safety later.

### Chores, Pranks, And Adventuring

The README claims Moes bring food, pull pranks, perform chores, and tag along on adventures. In code, I found only supporting scaffolding:

- follow state: `Moe.isFollowing`, `Moe.setFollowing`
- cell-phone teleport/call: `NpcCallRequestPayload`, `NpcCallPayload`, `CellPhone`, `MoeSpawner`, and Minecraft 1.21.4 `TeleportTransition`
- stat fields: food, fullness, stress, relaxation, loyalty, affection, age, personality traits, home, and visible block state on `Moe`/`NPC`
- old hunger/loneliness/stress/action/sleep update hooks are not active gameplay loops in the current normalized shell
- sounds such as `MOE_FEED`, `MOE_GRIEF`, `MOE_FOLLOW`, `MOE_EAT`, `MOE_SLEEPING`
- 36-slot inventory/menu support on `Moe`

I did not find concrete AI goals for following, chores, pranks, gifting food, combat adventuring, or sleep. Treat those as unfinished or planned unless there are files outside the scanned `src/main/java/block_party` tree.

## Stable, Unfinished, Duplicated, And Risky Areas

### Appears Stable Or Coherent

- NeoForge registration structure in `BlockParty`, `CustomBlocks`, `CustomItems`, `CustomEntities`, `CustomCreativeTabs`, and related registries is conventional for the current 1.21.4 target.
- The normalized `Moe` shell keeps the active entity/runtime surface easier to reason about than the frozen layered source.
- Block-to-Moe spawn and hide/reveal lifecycle is implemented end-to-end.
- Resource reload listeners for scenes, names, textures, sounds, and aliases are clear.
- Client rendering has a defined model/layer/screen organization.
- SQLite row abstraction is consistently used by NPC and block-record classes.

### Unfinished Or Placeholder

- Chores/pranks/adventuring/follow AI are not implemented in the scanned code.
- `Senpai` exists but is not registered as an entity.
- `SceneActions.Markov` exists but is not registered.
- full old Forge scene action/filter coverage, full Forge NPC row synchronization, and planned `BlockPartyNPC`/`Senpai` hierarchy restoration remain follow-ups.

### Duplicated Or Confusing

- Name/blood/profile generation still has multiple historical entry points between row data, tags, spawn, and old frozen source expectations.
- `Layer1.doHurtTarget` and `Layer7.doHurtTarget` previously called back into their own overrides and could stack overflow. The active NeoForge combat path delegates safely through vanilla mob combat and remains covered by GameTests.
- Some class names and compatibility notes still refer to old Forge packet/entity concepts. Keep those references only when they describe old-world parity.
- family-name behavior still needs explicit tests if the old block translation-key behavior is restored more fully.

### Risky Or Bug-Prone

- `MoeInHiding.readAdditionalSaveData` now restores the saved hide condition from NBT; keep this fixed behavior covered during save/load work.
- `HidingSpots` maps `BlockPos` only, even though some APIs receive dimensional positions. Because each `SavedData` instance is per `ServerLevel`, this may be acceptable, but it should be verified across dimensions.
- `HidingSpots.get(ServerLevel, BlockPos)` returns `spot.list.get(pos)` as primitive `long`; callers rely on `isNormalBlock` first to avoid unboxing null. The order is fragile.
- `SceneManager.trigger` only replaces the current scene if the incoming priority is greater than the current trigger. Equal-priority triggers are ignored until the active action clears.
- `SceneManager.setAction(null)` calls `onComplete` on the existing action. For `SendDialogue`, that can enqueue response follow-up behavior during interruption; verify whether that is intended.
- DB writes should stay explicit and covered by tests so row synchronization does not regress back into broad synced-data churn.
- `Column.set` reference/equality behavior and no-op `Row.update()` handling should remain covered by DB tests.
- Moe texture metadata was normalized in the active NeoForge path, but visual override/fallback screenshots are still useful release checks.
- `Markov.chain` stores entries by `probability` instead of cumulative `total`, which likely breaks weighted selection if it is ever used.

## Current Regression Plan

Start with behavior-preserving tests and manual smoke tests in the current NeoForge 1.21.4 build before changing risky behavior.

1. Build and launch smoke test
   - Run Gradle build/client once on the current code.
   - Record any existing compile/runtime failures as baseline.
   - Verify `neoforge.mods.toml`, assets, data packs, SQLite jar-in-jar loading, and resource reloads load without hard crashes.

2. Spawn lifecycle test
   - In a test world, place a block in `block_party:spawns_moes`.
   - Use `CustomSpawnEggItem`.
   - Verify source block is removed, `Moe` appears, owner UUID is set, block state is preserved, texture fallback works, name is assigned, and a row exists in `blockparty.db`.

3. Trait/profile test
   - Spawn Moes from blocks tagged for gender, blood type, dere, zodiac, wings, glow, cat features, and ignores-volume.
   - Verify active `Moe` block-state/profile derivation, sound selection, scale, navigation type, and renderer layers.

4. Dialogue scene test
   - Use `test_dialogue.json` through right-click.
   - Verify `SceneManager.trigger`, `DialogueOpenPayload`, `DialogueScreen`, `DialogueRespondPayload`, chained responses, and `End` behavior.
   - Add a scene with filters/counters/cookies before changing this system if it is intended to survive.

5. Hide/reveal test
   - Use `test_hide.json` through left-click.
   - Verify Moe becomes a block, `MoeInHiding` exists, `HidingSpots` saved data records the position, and the Moe returns after `ONE_SECOND_PASSES`.
   - Repeat for break-start, break-end, piston push, falling block, chunk unload/reload, and full server restart.

6. Persistence test
   - Spawn, rename/profile-change if possible, hide, save, quit, reload.
   - Verify DB row, player NPC list, `HidingSpots`, entity NBT, and block entity NBT restoration.
   - Specifically test `HideUntil` restoration so the resolved timed-hide restore behavior stays fixed.

7. Controller UI test
   - Open `YearbookItem` and `CellPhoneItem`.
   - Verify `ControllerOpenPayload`, `NpcDetailPayload`, `NpcCallRequestPayload`, `NpcCallPayload`, `CellPhone`, `MoeSpawner`, and `TeleportTransition` behavior.

8. Death/removal test
   - Kill or expose a hidden Moe and inspect `NPC.DEAD`, `NPC.HIDING`, player list removal, and row lifetime.
   - Verify expected behavior before deciding whether follow-up fixes should preserve or change it.

9. Resource reload/data-pack test
   - Reload names, aliases, textures, sounds, and scenes.
   - Verify malformed JSON failure behavior is documented.
   - Confirm `minecraft` namespace scene/resources are intentionally remapped by `Scenes.own`.

10. Regression harness
   - Run `.\gradlew.bat runGameTestServer` and keep manual smoke-test checklists/golden worlds for client-only coverage.

## Current Modernization Plan

Order follow-up work from safest behavior locks to broader feature restoration.

1. Keep current NeoForge baseline green
   - Run `.\gradlew.bat compileJava --no-daemon` and `.\gradlew.bat runGameTestServer --no-daemon`.
   - Save logs, generated DB schema, a small test world, and screenshots of Moe spawn/dialogue/hide before large behavior changes.

2. Preserve active API seams
   - Keep registry names, payload IDs, SQLite table/column names, resource paths, and item/entity IDs stable.
   - When Minecraft or NeoForge versions move again, update Gradle, metadata, generated resources, and GameTests first.

3. Expand parity tests around restored systems
   - Cover spawn, hide/reveal, profile traits, social affinities, Yearbook, Cell Phone, dialogue, shrine/location records, resources, and decorative blocks.
   - Treat old Forge behavior as a compatibility input, not as a reason to reintroduce old APIs.

4. Finish active client validation
   - Screenshot-test `MoeRenderer`, `MoeModel`, `MoeRenderState`, layers, `DialogueScreen`, `YearbookScreen`, `CellPhoneScreen`, Samurai armor, JapanRenderer, particles, and transparent blocks.

5. Address remaining architecture risks
   - Tighten SQL update helpers, hidden-spot edge cases, profile generation, data-pack diagnostics, and save schema versioning behind tests.

6. Restore planned systems last
   - Chores, pranks, adventuring, hunger/stress/sleep, richer following, Senpai, and the broader NPC hierarchy are new behavior on top of the stable NeoForge port.
