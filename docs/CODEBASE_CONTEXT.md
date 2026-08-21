# Codebase Context

This is a quick orientation snapshot for the post-port Block Party codebase. It is meant to help future feature work start from current NeoForge 1.21.4 reality instead of old Forge 1.19 assumptions.

## Active Target

- Minecraft `1.21.4`
- NeoForge `21.4.102-beta`
- Java 21
- Gradle plugin `net.neoforged.moddev` `1.0.11`
- Mod id `block_party`
- Project version `26.6`

`src/main/resources/META-INF/neoforge.mods.toml` is the active mod metadata. `src/main/resources/pack.mcmeta` declares Minecraft 1.21.4 pack compatibility with resource format `46` and data format `61` through `supported_formats`.

## Java Ownership Map

Approximate source count by top-level package:

- `entities`: 44 files. Moe runtime state, AI goals, movement, social behavior, environmental memory, chores, preferences, inventory, and hidden-state helpers.
- `scene`: 42 files. Scene model, triggers, observations, variable stores, and scene actions.
- `client`: 36 files. Screens, renderer/model state, particles, skybox, and client payload bridge.
- `blocks`: 23 files. Decorative blocks and data block entities for shrine, shimenawa, lanterns, saplings, and locative blocks.
- `registry`: 22 files. NeoForge deferred registration plus reload listeners for scenes, names, aliases, textures, sounds, social affinities, and item preferences.
- `gametest`: 17 files. Active server-side regression suite.
- `world`: 15 files. Cell phone service, attention tracking, awakening opportunities, progression gates, and structure helpers.
- `items`: 15 files. Spawn egg, controllers, letters, food, music discs, samurai equipment, and sortable creative-tab metadata.
- `db`: 14 files. SavedData, SQLite bootstrap, typed record helpers, rows/tables, shrine locations, and voicemail playback/storage.
- `network`: 12 files. NeoForge custom payload records and transport glue.
- `utils`: 2 files. Small NBT and Markdown helpers.
- `commands`: 1 file. Debug command registration.

## Resource Ownership Map

Approximate `data/block_party` content count by top-level folder:

- `tags`: 62 files.
- `recipe`: 44 files.
- `loot_table`: 36 files.
- `scenes`: 32 files.
- `moes`: 26 files for aliases, names, sounds, textures, social affinity, and item preference data.
- `worldgen`: 8 files.
- `jukebox_song`: 2 files.
- `structure`: 1 file.

Resource reload listeners are registered from `block_party.registry.CustomResources`. The active authoring docs are `SCENE_DATAPACK_SCHEMA.md`, `SCENE_ARC_AUTHORING.md`, and `SCENE_OBSERVATION_ARCHITECTURE.md`.

## Active Verification

The active automated suite is 247 required NeoForge GameTests under `src/main/java/block_party/gametest`.

Largest GameTest files by test count:

- `SceneGameTests.java`: 40
- `MoeMovementGameTests.java`: 26
- `MoeLifecycleGameTests.java`: 24
- `EntityDataGameTests.java`: 20
- `ResourceGameTests.java`: 16
- `MoeSocialGameTests.java`: 15
- `NetworkPayloadGameTests.java`: 15
- `BlockEntityGameTests.java`: 14
- `RegistryGameTests.java`: 13
- `PlayerMovementGameTests.java`: 12
- `DecorativeContentGameTests.java`: 12
- `CellPhoneServiceGameTests.java`: 11

The historical regression tests under `src/test/java/block_party/regression` are not part of the active Gradle test source set because they target old Forge APIs. Use `phase1Compliance`, `localCi`, and `fullCi` as the local verification entry points described in `README.md` and `TESTING_STRATEGY.md`.

## Current Feature Baseline

Strong active surfaces:

- Arrival lifecycle distinguishes cardinal visitors from corporeal residents. Cardinal Moes poof out of the active world when they sleep and require another encounter trigger to return. Corporeal arrivals search near their trigger for a safe block matching their result type, emerge by vacating that block, and retain it as their home/hiding position. Arrival data can tune this search with `home_search_radius` (default `16`) and `home_search_vertical_radius` (default `4`).
- Arrival block selectors support exact blocks, tags, and `{"any":true}`. Exact placement/support combinations outrank tag matches, which outrank generic `any` rules. Dirt, coarse dirt, rooted dirt, grass block, podzol, and mycelium now use the corporeal stack-plus-place-anything-on-top pattern. Coal, copper, iron, gold, redstone, lapis, diamond, and emerald use their collected drops plus a torch placed on ordinary stone, producing one canonical cardinal identity per ore family.
- Hidden corporeal Moes are indexed by `HidingSpots` and can wake automatically when a familiar player approaches. The server performs staggered player-centered scans, reveals at most one Moe per player scan, and calculates wake radius continuously as the greater of relationship trust and loyalty divided by `5`, capped at `20` blocks. Players without a relationship still require deliberate block interaction. Player and Moe cooldowns prevent clustered reveals and immediate wake loops.

- Spawn a Moe from a `block_party:spawns_moes` block with `CustomSpawnEggItem`.
- Preserve block identity, visible block aliases, block entity persistent data, owner, profile fields, SQLite row identity, and hidden-state transitions.
- Use data-driven scenes, filters, actions, dialogue payloads, and scoped scene variables.
- Use Yearbook and Cell Phone controller screens backed by typed payloads and server-side row ownership checks.
- Hide/reveal through `Moe`, `MoeInHiding`, and `HidingSpots`.
- Store row-backed data through SQLite plus world `SavedData`.
- Load Moe names, aliases, texture metadata, sound metadata, social affinities, and item preferences from resources.
- Use restored server-side data block behavior for shrine, shimenawa, garden lantern, location, and sapling rows.
- Use server-side shrine tablet effects and shimenawa hidden NPC row creation.
- Cover decorative blocks, worldgen resource availability, particles, samurai server behavior, attention records, social behavior, movement/routine primitives, and awakening opportunities through GameTests.

Known follow-up surfaces before broader content expansion:

- Real follow AI/pathfinding beyond current service and movement primitives.
- Chores, pranks, and richer companion loops.
- Full Forge scene action/filter parity where the backing state is still missing.
- Explicit SQLite schema/version migration story for public save compatibility.
- Client visual screenshot validation for renderer, UI, particles, skybox, armor, and decorative block parity.
