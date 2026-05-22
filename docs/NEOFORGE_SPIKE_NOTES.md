# NeoForge 1.21.4 Build Spike Notes

This branch is a build/load spike only. It intentionally compiles a minimal NeoForge source path from `src/neoforgeSpike` and leaves the Forge 1.19.4 production code in place for later porting.

## Active Spike Surface

- `build.gradle`: switched from ForgeGradle to NeoForge ModDevGradle for NeoForge `21.4.102-beta`, Java 21, and minimal client/server/GameTest/data runs under `run-neoforge-spike`.
- `settings.gradle`: added NeoForge and Gradle plugin repositories for ModDevGradle.
- `gradle/wrapper/gradle-wrapper.properties`: moved the wrapper to Gradle 8.8 for ModDevGradle compatibility.
- `src/neoforgeSpike/java/block_party/BlockParty.java`: minimal `@Mod("block_party")` entrypoint preserving mod id and package name.
- `src/neoforgeSpike/java/block_party/gametest/SpikeGameTests.java`: one empty required GameTest using the existing `block_party:empty` template name.
- `src/neoforgeSpike/java/block_party/gametest/RegistryGameTests.java`: representative NeoForge registry ID smoke test for block, item, sound, particle, entity type, scene action, and scene filter registration.
- `src/neoforgeSpike/java/block_party/gametest/ResourceGameTests.java`: representative resource smoke tests for converted tags and bundled JSON reload parsing.
- `src/neoforgeSpike/java/block_party/gametest/PersistenceGameTests.java`: persistence smoke tests for SavedData serialization, `DimensionDataStorage` caching, SQLite open/close, `HidingSpots`, and `SceneVariables`.
- `src/neoforgeSpike/java/block_party/gametest/EntityDataGameTests.java`: entity data smoke tests for spawning `moe`, stable entity IDs, and Moe/MoeInHiding save/load round-trips.
- `src/neoforgeSpike/java/block_party/gametest/MoeLifecycleGameTests.java`: minimal lifecycle smoke tests for tagged block spawn, invalid spawn no-op, hide, HidingSpots recording, manual reveal, and missing hidden spot no-op behavior.
- `src/neoforgeSpike/java/block_party/gametest/NpcServiceGameTests.java`: server-side companion record service tests for owner lists, owned row access, de-listing, safe rejection, and hide/reveal list stability.
- `src/neoforgeSpike/java/block_party/gametest/NetworkPayloadGameTests.java`: NeoForge custom payload codec and server-handler tests for NPC list/detail/remove scaffolding.
- `src/neoforgeSpike/java/block_party/gametest/CellPhoneServiceGameTests.java`: server-side Cell Phone call service tests for visible owned Moe teleport, ownership rejection, safe failures, hidden rejection, unloaded rejection, and following flag behavior.
- `src/neoforgeSpike/java/block_party/network/CustomMessenger.java` and `src/neoforgeSpike/java/block_party/network/payload/**`: typed NeoForge custom payload registration for NPC list/detail/remove requests and list/detail responses.
- `src/neoforgeSpike/java/block_party/entities/Moe.java`: thin NeoForge entity shell preserving database ID, owner UUID, block state, following flag, and lightweight profile strings.
- `src/neoforgeSpike/java/block_party/entities/MoeInHiding.java`: thin NeoForge entity shell preserving database ID, attached block position, `HideUntil`, and `ticksHidden`.
- `src/neoforgeSpike/java/block_party/items/CustomSpawnEggItem.java`: minimal `moe_spawn_egg` behavior for blocks tagged `block_party:spawns_moes`.
- `src/neoforgeSpike/java/block_party/registry/CustomResources.java`: minimal server reload listener wiring for inert bundled JSON parsing.
- `src/neoforgeSpike/java/block_party/registry/resources/CountingJsonReloadListener.java`: narrow parser/counting reload listener for `moes/aliases`, `moes/names`, and `scenes`; it validates JSON syntax without constructing real scene/entity behavior.
- `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java`: NeoForge `SavedData.Factory` scaffold preserving `BlockParty_DB`, `Names`, `NPCsByPlayer`, and the world-local `blockparty.db` SQLite path.
- `src/neoforgeSpike/java/block_party/entities/data/HidingSpots.java`: NeoForge `SavedData.Factory` scaffold preserving `BlockParty_HidingSpots`, `BlockPos`, and `DatabaseID` storage.
- `src/neoforgeSpike/java/block_party/db/records/NPC.java`: minimal SQLite-backed `NPCs` row adapter for Moe identity, owner UUID, block state, hiding state, hidden position, and lightweight profile fields.
- `src/neoforgeSpike/java/block_party/scene/SceneVariables.java` and `src/neoforgeSpike/java/block_party/scene/data/**`: NeoForge `SavedData.Factory` scaffold preserving `BlockParty_SceneVariables`, cookies, counters, locations, and targets NBT shape without scene execution.
- `src/neoforgeSpike/resources/META-INF/neoforge.mods.toml`: minimal NeoForge mod metadata preserving mod id, display name, package-facing identity, and Minecraft/NeoForge dependency ranges.
- `src/neoforgeSpike/resources/data/block_party/structure/empty.nbt`: copied empty GameTest structure template for the NeoForge 1.21.4 resource path.
- `syncNeoForgeSpikeResources` in `build.gradle`: copies safe legacy assets and data into `build/generated/neoforgeSpikeResources`, and converts legacy `tags/blocks` / `tags/items` to 1.21.4 `tags/block` / `tags/item`.

## Reintroduced Registries

These registries are active in the NeoForge spike. Entries preserve legacy registry IDs where practical, but most registered objects are inert vanilla placeholders until gameplay systems are ported back.

| Registry | Spike file | Status |
| --- | --- | --- |
| Blocks | `src/neoforgeSpike/java/block_party/registry/CustomBlocks.java` | Reintroduced as simple vanilla `Block` entries for legacy block IDs. Custom block behavior remains disabled. |
| Items | `src/neoforgeSpike/java/block_party/registry/CustomItems.java` | Reintroduced as simple `BlockItem` entries for active block IDs and plain `Item` entries for non-block legacy IDs. Custom item behavior remains disabled. |
| Block entities | none | Still disabled. The spike block registrations are plain blocks and do not require block entity types yet. Legacy IDs still to port: `garden_lantern`, `hanging_scroll`, `paper_lantern`, `sakura_sapling`, `shimenawa`, `shrine_tablet`, `wind_chimes`. |
| Entity types | `src/neoforgeSpike/java/block_party/registry/CustomEntities.java`, `src/neoforgeSpike/java/block_party/entities/Moe.java`, `src/neoforgeSpike/java/block_party/entities/MoeInHiding.java` | Reintroduced as `moe` and `moe_in_hiding` declarations backed by thin data-shell entities. They use `MobCategory.MISC` and plain `Entity` subclasses, so no attribute registration, AI, combat, dialogue, networking, or rendering hooks are active. |
| Sounds | `src/neoforgeSpike/java/block_party/registry/CustomSounds.java` | Reintroduced as variable-range `SoundEvent` entries preserving legacy sound IDs. Sound JSON/audio behavior is not validated in this spike. |
| Particles | `src/neoforgeSpike/java/block_party/registry/CustomParticles.java` | Reintroduced as `SimpleParticleType` entries preserving legacy particle IDs. Client particle providers remain disabled. |
| Scene actions | `src/neoforgeSpike/java/block_party/registry/SceneActions.java` | Reintroduced as a custom NeoForge registry containing inert builder records for legacy action IDs. Scene implementations remain disabled. |
| Scene filters | `src/neoforgeSpike/java/block_party/registry/SceneFilters.java` | Reintroduced as a custom NeoForge registry containing inert builder records for legacy filter IDs. Scene implementations remain disabled. |

## Reintroduced Persistence

These persistence foundations are active in the NeoForge spike. SQLite now owns minimal NPC row identity for the Moe spawn/hide/reveal lifecycle; other persistence systems remain shells.

| Area | Spike file | Status |
| --- | --- | --- |
| `BlockParty_DB` SavedData | `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java` | Reintroduced with NeoForge/Minecraft 1.21.4 `SavedData.Factory`, overworld `DimensionDataStorage` lookup, `Names`, and `NPCsByPlayer` serialization. It also owns world-local SQLite connection setup, owner-to-NPC ID lists, owned row access checks, and de-listing for the minimal `NPCs` table. |
| SQLite lifecycle | `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java`, `src/neoforgeSpike/java/block_party/BlockParty.java`, `build.gradle` | Reintroduced as a minimal `ServerStartedEvent` bootstrap and `ServerStoppedEvent` shutdown using xerial `sqlite-jdbc`. Assumption: server-started timing is late enough for `server.overworld()` and world paths, unlike the old Forge `LevelEvent.Load` path. The spike loads the JDBC driver explicitly and calls `Driver.connect` directly because NeoForge dev runs expose the dependency across a module classloader boundary where `DriverManager` discovery is unreliable. |
| `NPCs` SQLite table | `src/neoforgeSpike/java/block_party/db/records/NPC.java` | Reintroduced as a minimal table compatible with core Forge baseline names where possible: `DatabaseID`, `PosDim`, `PosX`, `PosY`, `PosZ`, `PlayerUUID`, `Dead`, `Name`, `BlockState`, and `Hiding`. Temporary spike columns: `Gender`, `HiddenPosDim`, `HiddenPosX`, `HiddenPosY`, `HiddenPosZ`. Full Forge trait/health/home/shrine columns are still disabled. |
| `BlockParty_HidingSpots` SavedData | `src/neoforgeSpike/java/block_party/entities/data/HidingSpots.java` | Reintroduced as ID/position storage for hidden Moe lookup. It remains the position-to-database-ID index; SQLite owns row identity and hiding fields. Block disturbance hooks remain disabled. |
| `BlockParty_SceneVariables` SavedData | `src/neoforgeSpike/java/block_party/scene/SceneVariables.java`, `src/neoforgeSpike/java/block_party/scene/data/**` | Reintroduced for cookie, counter, location, and target map serialization only. Scene execution, NPC/player live variable population, and entity target resolution remain disabled. |
| `DimBlockPos` NBT helper | `src/neoforgeSpike/java/block_party/db/DimBlockPos.java` | Reintroduced only for location variable round-trips using legacy `Coordinates`, `Dimension`, and `IsEmpty` keys. |

## Reintroduced Entity Data Shells

These entity structures are active only far enough to compile, register, spawn, and preserve identity/state data.

| Entity area | Spike file | Status |
| --- | --- | --- |
| `Moe` entity shell | `src/neoforgeSpike/java/block_party/entities/Moe.java` | Reintroduced as a plain NeoForge-compatible `Entity` with synced `DatabaseID`, `OwnerUUID`, `BlockState`, `Following`, `GivenName`, and `Gender`. NBT uses legacy `DatabaseID`, `BlockState`, and `Following` keys plus explicit `OwnerUUID`, `GivenName`, and `Gender` spike keys. |
| `MoeInHiding` entity shell | `src/neoforgeSpike/java/block_party/entities/MoeInHiding.java` | Reintroduced as a plain no-gravity/no-physics `Entity` with synced `DatabaseID`, `AttachPos`, and owner UUID, plus local `HideUntil` and `TicksHidden` NBT. It tracks its attached position on tick but does not run timed reveal logic, disturbance hooks, sounds, or scene triggers. |
| `HideUntil` enum | `src/neoforgeSpike/java/block_party/entities/goals/HideUntil.java` | Reintroduced only as serializable values `EXPOSED` and `ONE_SECOND_PASSES`. Timing conditions and hiding goal behavior remain disabled. |

## Reintroduced Spawn/Hide Lifecycle

This lifecycle is active only as a parity-oriented shell around the entity data and persistence scaffolding.

| Lifecycle area | Spike file | Status |
| --- | --- | --- |
| Spawn egg use | `src/neoforgeSpike/java/block_party/items/CustomSpawnEggItem.java`, `src/neoforgeSpike/java/block_party/registry/CustomTags.java`, `src/neoforgeSpike/java/block_party/db/records/NPC.java` | Reintroduced for `block_party:moe_spawn_egg` against blocks in `block_party:spawns_moes`. Valid blocks create an `NPCs` row, assign the generated `DatabaseID` to the Moe shell, store source `BlockState`, and copy owner UUID when a player/UUID is available. Invalid blocks return `FAIL`/`null` without spawning or inserting a row. |
| Hide | `src/neoforgeSpike/java/block_party/entities/Moe.java`, `src/neoforgeSpike/java/block_party/db/records/NPC.java` | Reintroduced as `Moe.hide(HideUntil)`: requires an existing NPC row, updates that row with current Moe fields, marks `Hiding = 1`, stores hidden position columns, places the Moe source block, creates `MoeInHiding`, records `HidingSpots`, and discards the Moe shell. No AI goal, animation, sound, block entity, or scene trigger is performed. |
| Reveal | `src/neoforgeSpike/java/block_party/entities/MoeInHiding.java`, `src/neoforgeSpike/java/block_party/entities/data/HidingSpots.java`, `src/neoforgeSpike/java/block_party/db/records/NPC.java` | Reintroduced as manual reveal through `HidingSpots.reveal(ServerLevel, BlockPos)`. A matching hidden marker must also have an NPC row. Reveal restores a Moe shell from row-backed identity, marks `Hiding = 0`, destroys the hidden block, clears `HidingSpots`, and discards the hidden marker. Missing hidden spots or missing rows no-op. |
| SQLite row sync boundary | `src/neoforgeSpike/java/block_party/db/records/NPC.java` | Minimal row creation/loading/updating is active only for Moe identity fields. Full Forge NPC schema columns, DB-backed traits beyond `Gender`, health/food/stress stats, home/shrine references, player login sync, packet transport, and Cell Phone teleport behavior remain disabled. |
| Companion record service | `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java` | Reintroduced as server-side methods future packets can call: `listNpcIds(UUID)`, `loadOwnedNpc(UUID, long)`, `removeOwnedNpc(UUID, long)`, `callOwnedNpc(...)`, and safe row lookup. These reject missing, corrupt, dead, hidden where relevant, unloaded where relevant, and non-owned rows. De-listing removes only the SavedData owner-list entry; it does not delete the SQLite row. |
| Cell Phone call service | `src/neoforgeSpike/java/block_party/db/BlockPartyDB.java` | Reintroduced as server-side behavior only. A visible, loaded, owned Moe can be moved near the requester and marked `following=true`. Hidden Moe rows fail safely by design until reveal owns that transition. Unloaded rows fail safely; the spike does not use forced chunks or chunk ticket cleanup. |

## Reintroduced Networking Scaffold

This networking layer is active only for server-side companion record flows. It does not enable client UI, Cell Phone call/teleport packet behavior, dialogue, rendering, or old Forge `AbstractMessage` handling.

| Payload area | Spike file | Status |
| --- | --- | --- |
| Payload registration | `src/neoforgeSpike/java/block_party/network/CustomMessenger.java`, `src/neoforgeSpike/java/block_party/BlockParty.java` | Reintroduced through NeoForge `RegisterPayloadHandlersEvent` on the mod bus. The registrar is versioned as `1` and optional for this spike. |
| NPC list request/response | `src/neoforgeSpike/java/block_party/network/payload/NpcListRequestPayload.java`, `NpcListPayload.java` | Client request `block_party:npc_list_request` maps to `BlockPartyDB.listNpcIds(player UUID)` and replies with `block_party:npc_list`. The response contains only readable, live, owned SQLite row IDs. |
| NPC detail request/response | `src/neoforgeSpike/java/block_party/network/payload/NpcDetailRequestPayload.java`, `NpcDetailPayload.java` | Client request `block_party:npc_detail_request` maps to `BlockPartyDB.loadOwnedNpc(player UUID, DatabaseID)` and replies with `block_party:npc_detail`. Missing, corrupt, dead, and non-owned rows return `found=false`. |
| NPC remove request | `src/neoforgeSpike/java/block_party/network/payload/NpcRemoveRequestPayload.java`, `src/neoforgeSpike/java/block_party/network/CustomMessenger.java` | Client request `block_party:npc_remove_request` maps to `BlockPartyDB.removeOwnedNpc(player UUID, DatabaseID)` and replies with the refreshed owned ID list. The SQLite row is not deleted. |
| Client handling | `src/neoforgeSpike/java/block_party/network/CustomMessenger.java` | Client-bound list/detail handlers are no-ops by design. Screen state, Cell Phone UI, Yearbook UI, and dialogue packet behavior remain disabled. |

## Reintroduced Resources

These resources are active in the NeoForge spike.

| Resource area | Source | Status |
| --- | --- | --- |
| Block tags | `src/main/resources/data/block_party/tags/blocks/**` copied to generated `data/block_party/tags/block/**` | Reintroduced through build-time path conversion for NeoForge/Minecraft 1.21.4. Representative `sakura_logs` membership is GameTested. |
| Item tags | `src/main/resources/data/block_party/tags/items/**` copied to generated `data/block_party/tags/item/**` | Reintroduced through build-time path conversion. Representative `sakura_logs` membership is GameTested. |
| Entity type tags | `src/neoforgeSpike/resources/data/block_party/tags/entity_type/spike_registered.json` | Added a narrow spike tag for registered placeholder entity type IDs. |
| Sound event tags | `src/neoforgeSpike/resources/data/block_party/tags/sound_event/spike_registered.json` | Added a narrow spike tag for registered sound IDs. |
| Particle type tags | `src/neoforgeSpike/resources/data/block_party/tags/particle_type/spike_registered.json` | Added a narrow spike tag for registered particle IDs. |
| Block/item assets | `src/main/resources/assets/block_party/blockstates/**`, `models/**`, `textures/block/**`, `textures/item/**`, and `lang/en_us.json` via generated resource copy | Bundled for registered inert blocks/items. Client rendering/UI remains disabled, so this is not yet screenshot-verified. |
| Sound assets | `src/main/resources/assets/block_party/sounds.json` and `sounds/**` via generated resource copy | Bundled with registered sound IDs. Audio playback is not tested in this spike. |
| Particle assets | `src/main/resources/assets/block_party/particles/**` and `textures/particle/**` via generated resource copy | Bundled with registered particle IDs. Client particle providers remain disabled. |
| Moe alias/name data | `src/main/resources/data/block_party/moes/**` via generated resource copy | Parsed by the inert counting reload listener to catch syntax/load errors without constructing entity behavior. |
| Scene data | `src/main/resources/data/block_party/scenes/**` via generated resource copy | Parsed by the inert counting reload listener to catch syntax/load errors. Scene actions/filters are ID-covered, but no scene execution is enabled. |

## Temporarily Disabled Systems

These are disabled by `build.gradle` source-set isolation, not deleted.

| Class/file | Reason |
| --- | --- |
| `src/main/java/block_party/BlockParty.java` | Forge 1.19.4 entrypoint imports ForgeGradle-era Forge APIs, `SimpleChannel`, Forge deferred registers, and common/client event wiring. Replaced only for the spike by a TODO NeoForge stub. |
| `src/main/java/block_party/registry/CustomMessenger.java` and `src/main/java/block_party/messages/**` | Legacy Forge networking remains disabled. The spike has a new narrow NeoForge payload scaffold for NPC list/detail/remove only; old `SimpleChannel`, `NetworkRegistry`, `NetworkDirection`, `NetworkEvent.Context`, and broad message families are not ported. |
| `src/main/java/block_party/entities/**` | Legacy entity implementations remain disabled. Spike equivalents for `Moe`, `MoeInHiding`, `HideUntil`, and `HidingSpots` are active under `src/neoforgeSpike/java`, but only data shells plus minimal spawn/hide/manual reveal lifecycle are enabled. |
| `src/main/java/block_party/db/**`, `src/main/java/block_party/entities/data/HidingSpots.java`, and `src/main/java/block_party/scene/SceneVariables.java` | Legacy persistence implementations remain disabled. Spike equivalents are active under `src/neoforgeSpike/java`; only the minimal SQLite `NPCs` row identity path is gameplay-connected. |
| `src/main/java/block_party/client/**` | Rendering/UI port is explicitly out of scope. These classes depend on Forge client events, renderer/model layer registration, particle provider registration, screens, and client-only annotations. |
| `src/main/java/block_party/registry/CustomBlocks.java`, `CustomEntities.java`, `CustomItems.java`, `CustomParticles.java`, `CustomSounds.java`, `SceneActions.java`, and `SceneFilters.java` | Legacy Forge registry implementations remain disabled. Spike equivalents are active under `src/neoforgeSpike/java/block_party/registry`, but they use inert placeholders instead of gameplay classes. |
| `src/main/java/block_party/registry/CustomBlockEntities.java` | Still disabled because spike blocks are plain blocks and do not require block entities yet. |
| `src/main/java/block_party/registry/CustomWorldGen.java` | Still disabled because worldgen is gameplay-facing and out of scope for this registry-only spike. |
| `src/main/java/block_party/registry/CustomResources.java` and `src/main/java/block_party/registry/resources/**` | Legacy Forge resource reload listeners remain disabled. The spike equivalent under `src/neoforgeSpike/java/block_party/registry` wires only inert JSON counting listeners for `moes` and `scenes` data. |
| `src/main/resources/data/block_party/recipes/**` | Still disabled. Recipes are gameplay-facing and some outputs/ingredients need item behavior decisions before parity testing. |
| `src/main/resources/data/block_party/loot_tables/**` | Still disabled. Loot behavior depends on real blocks/items and old ID cleanup. |
| `src/main/resources/data/block_party/worldgen/**` | Still disabled. Worldgen is gameplay-facing and depends on unported feature registrations. |
| `src/main/resources/assets/block_party/textures/gui/**` | Still disabled in the generated spike copy because rendering/UI is out of scope. |
| `src/main/java/block_party/blocks/**`, `src/main/java/block_party/items/**`, `src/main/java/block_party/world/**`, `src/main/java/block_party/scene/**`, and `src/main/java/block_party/utils/**` | Gameplay code is not required for the minimal load/GameTest spike and still references old Forge/Minecraft APIs transitively. The spike-only `CustomSpawnEggItem` is a narrow exception for minimal Moe lifecycle coverage. |
| `src/main/java/block_party/gametest/**` | Existing GameTests target Forge 1.19.4 gameplay systems. A single NeoForge empty spike GameTest replaces them until systems are ported back. |
| `src/test/java/block_party/regression/**` | Legacy regression tests compile against the Forge 1.19.4 production classes and are disabled while the spike source set contains only the minimal loader stub. |
| `src/main/resources/**` | The full legacy resource root is not loaded directly. `syncNeoForgeSpikeResources` copies only safe subsets into generated resources, while recipes, loot tables, worldgen, GUI textures, and other gameplay-facing files remain disabled. |

## Follow-Up TODOs

- TODO: Replace inert block/item placeholders and entity data shells with real implementations incrementally after each gameplay system is ported.
- TODO: Replace `CountingJsonReloadListener` with real resource reload listeners once scene/entity behavior is ported.
- TODO: Reintroduce recipes, loot tables, worldgen, and GUI assets only when their owning gameplay systems are active.
- TODO: Expand the spike `NPCs` table toward the full Forge baseline schema, then reintroduce player login sync, hiding-spot disturbance handling, and scene variable population after their gameplay owners are ported.
- TODO: Reintroduce GameTests incrementally as each gameplay system is ported.
- TODO: Keep networking beyond the NPC list/detail/remove scaffold, Cell Phone client request/response wiring, real entity behavior, persistence gameplay integration, scene execution, and rendering/UI out of this spike branch until their owning systems are intentionally ported.
