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

- scene reload listener: `block_party.registry.resources.ScenesReloadListener`
- scene manager: `block_party.scene.SceneManager`
- triggers: `block_party.scene.SceneTrigger`
- actions: `block_party.scene.SceneAction` and `block_party.scene.actions.*`
- filters: `block_party.scene.SceneObservation`, `SceneObservations`, and `SceneObservationFactories`
- action/filter registries: `block_party.registry.SceneActions`, `block_party.registry.SceneFilters`

The current resource scenes under `src/main/resources/data/block_party/scenes/` include smoke-test content plus active content-forward scenes:

- `test_dialogue.json`: right-click dialogue chain
- `test_hide.json`: left-click hide action
- attention/place-memory scenes such as `oak_forest_attention_*.json`

`SendDialogueAction` constructs a `Dialogue` from JSON, opens it for the owning player with `DialogueOpenPayload`, and waits until a response arrives. `DialogueScreen` sends `DialogueRespondPayload`; the server loads the NPC row, finds the server entity, and records the selected response on the active Moe dialogue state.

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

Compatibility note: `MoeInHiding.readAdditionalSaveData` restores `HideUntil` from the saved NBT string. Timed hide save/load should remain covered when persistence code changes.

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

- `CustomBlocks.BLOCKS`, `CustomItems.ITEMS`, `CustomEntities.ENTITIES`, `CustomBlockEntities.BLOCK_ENTITIES`, `CustomParticles.PARTICLES`, `CustomSounds.SOUNDS`, and `CustomWorldGen` feature keys
- custom registry builders for scene actions and filters: `SceneActions.ACTIONS`, `SceneFilters.FILTERS`
- custom payload registration: `CustomMessenger.registerPayloads`

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
- `block_party.registry.resources.ScenesReloadListener`
- `block_party.scene.actions.SendDialogueAction`
- `block_party.scene.actions.SendResponseAction`
- `block_party.client.screens.DialogueScreen`
- `block_party.network.payload.DialogueOpenPayload`
- `block_party.network.payload.DialogueRespondPayload`
- `block_party.network.payload.DialogueClosePayload`

Scenes are JSON resources under `data/*/scenes`. Each has a trigger, filters, and actions. Filters must all pass. Matching scenes are shuffled and one fulfilled scene is selected.

Parser-supported actions include dialogue, response, health/food/loyalty/stress mutation, cookie/counter mutation, hide, voicemail, follow-session, anchor/routine, sleep-at-home, inventory/item transfer, wait/dismiss, and end. Keep `SceneActions`, `ScenesReloadListener`, and `SCENE_DATAPACK_SCHEMA.md` synchronized when this list changes.

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

Risk note: SQL strings are still assembled manually in several places, but table names and shared columns are now treated as compatibility constants. Keep dynamic values in prepared-statement bindings, and run `phase1Compliance` after DB edits to catch raw table-name literal regressions.

### Chores, Pranks, And Adventuring

The README claims Moes bring food, pull pranks, perform chores, and tag along on adventures. In code, I found only supporting scaffolding:

- follow state: `Moe.isFollowing`, `Moe.setFollowing`
- cell-phone teleport/call: `NpcCallRequestPayload`, `NpcCallPayload`, `CellPhone`, `MoeSpawner`, and Minecraft 1.21.4 `TeleportTransition`
- stat fields: food, fullness, stress, relaxation, loyalty, affection, age, personality traits, home, and visible block state on `Moe`/`NPC`
- old hunger/loneliness/stress/action/sleep update hooks are not active gameplay loops in the current normalized shell
- sounds such as `MOE_FEED`, `MOE_GRIEF`, `MOE_FOLLOW`, `MOE_EAT`, `MOE_SLEEPING`
- 36-slot inventory/menu support on `Moe`

I did not find concrete AI goals for following, chores, pranks, gifting food, combat adventuring, or sleep. Treat those as unfinished or planned unless there are files outside the scanned `src/main/java/block_party` tree.

## Architecture Boundaries

The codebase should stay organized around visible ownership boundaries. This matters more as content systems expand.

Package responsibilities:

- `blocks`: block classes and block entities. Block entities may claim/update SQLite rows, but should not own Moe behavior.
- `client`: renderers, models, screens, particles, client-only payload handling, and visual effects. Client code mirrors server state; it is not authoritative.
- `db`: SQLite records, row helpers, and world saved-data bridges. DB code should not decide gameplay outcomes beyond persistence queries and ownership checks.
- `entities`: live entity state and small domain helpers for Moe behavior.
- `entities.environment`: environmental observations and place memory.
- `entities.movement`: anchors, movement intents, and navigation helpers.
- `entities.preferences`: item preference and gift signal logic.
- `entities.social`: social target/signal/rule evaluation.
- `entities.chores`: chore-specific planning and execution helpers.
- `items`: item interaction entry points. Items should delegate service behavior rather than duplicating controller or entity logic.
- `network`: typed NeoForge payload records and transport glue. Payload handlers should validate authority and delegate to server services.
- `registry`: NeoForge registration and reload listener wiring.
- `scene`: data-driven behavior language: triggers, scene selection, observations, actions, dialogue, and scene-local variables.
- `world`: server-level services that are broader than one entity, such as Cell Phone lookup, attention, or world event aggregation.

Thin-class rule:

- Entity classes should expose state, lifecycle hooks, and orchestration points. Scanning, scoring, path selection, social math, item preference math, and persistence transforms should live in focused collaborators.
- Avoid new "just one more method" additions to `Moe` when the behavior has its own vocabulary.
- Keep package-private helpers near the package that owns the domain concept.
- Use imports for normal Java dependencies. Fully qualified class names should be rare and intentional, usually only to resolve a same-name collision.

DRY rule:

- Shared content behavior should become a scene action/filter or a domain service before it is copied into multiple item/entity methods.
- Scene-pack-facing concepts should be named once in `SceneActions`, `SceneFilters`, and `SCENE_DATAPACK_SCHEMA.md`.
- Registry IDs, payload IDs, table names, NBT keys, and resource paths are compatibility surfaces. Reuse constants or shared helpers when code needs the same value in more than one place. `phase1Compliance` enforces the current FQCN budget and raw SQL table-name guardrail; GameTests enforce the behavior-level contracts.

## Authoring Contracts

Author-facing content contracts live in `docs/SCENE_DATAPACK_SCHEMA.md`. That file is the source of truth for scene JSON, social affinities, names, aliases, block trait tags, and fail-closed behavior.

Important content-authoring invariants:

- Prefer explicit `block_party:*` IDs in authored content.
- Unknown filters fail closed, disabling the scene.
- Unknown object actions become `end`; this should stay documented and tested until a stricter author diagnostic replaces it.
- Cookies and counters are per Moe database ID, not global progression.
- Player/global progression state is not a first-class scene authoring surface yet.
- Java primitives added for content should expose a small scene action/filter when content authors are expected to use them.

For generated scene packs, Codex or another authoring tool should target the schema document, not Java implementation details. If a desired story beat cannot be expressed in that schema, add a small Java primitive and update the schema in the same slice.

## Maintenance Rules

- Keep `TESTING_STRATEGY.md` as the test strategy and coverage map. Do not recreate a standalone regression backlog.
- Keep `TECH_DEBT.md` limited to unresolved debt. Remove fixed items instead of carrying "resolved" sections forward.
- Keep `COMPATIBILITY_NOTES.md` for intentional differences from the old Forge baseline.
- Keep `ENTITY_STATE_FLOW.md` focused on runtime state authority and lifecycle flow.
- Keep `SCENE_DATAPACK_SCHEMA.md` author-facing. Avoid Java-only terminology there unless authors need it to make correct content.
- Run `./gradlew phase1Compliance` before cleanup-heavy changes. It enforces the current inline FQCN budget and rejects raw SQL table-name literals. Run `./gradlew installGitHooks` once per clone to enable the same guardrail as a pre-commit hook.
