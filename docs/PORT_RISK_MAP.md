# Port Risk Map

This map focuses on systems that are likely to change during a Forge 1.19.4 to NeoForge/Minecraft-version port. It names the current implementation classes, the likely NeoForge replacement area to investigate for the chosen target version, the player-facing behavior at risk, the tests/checklists needed before porting, and a suggested migration order.

Because NeoForge APIs vary by target Minecraft version, treat the replacement areas below as investigation targets, not final imports. Lock the exact target version before changing code.

## Suggested Migration Order

1. Build toolchain, dependency shading, mod metadata, mappings, and run configs.
2. Core registries and event bus lifecycle hooks needed for mod load.
3. Resource reload listeners and data-pack loading.
4. Entity types, attributes, synced data, NBT, and spawn packets.
5. SavedData/world storage and SQLite persistence.
6. Networking packets.
7. Chunk forcing and teleport/cell-phone behavior.
8. Client rendering, model layers, render layers, and particles.
9. UI/screens and full client interaction flows.
10. Golden-world parity pass before bug fixes or feature work.

## build.gradle, Toolchain, And Shading

Current Forge 1.19.4 implementation classes/files:

- `build.gradle`
- `gradle.properties`
- `src/main/resources/META-INF/mods.toml`
- `src/main/resources/META-INF/accesstransformer.cfg`
- ForgeGradle `5.1.+`
- `net.minecraftforge:forge:1.19.4-45.2.0`
- Shadow plugin `com.github.johnrengelman.shadow` `7.0.0`
- SQLite dependency `org.xerial:sqlite-jdbc:3.40.1.0`
- relocation rule `relocate 'org.sqlite', 'block_party.org.sqlite'`

Likely NeoForge replacement area:

- NeoForge Gradle plugin for the target Minecraft version.
- NeoForge dependency coordinates and run configuration DSL.
- Target-version mappings setup.
- Modern resource/datagen source-set configuration.
- Reobfuscation or production-jar task changes.
- Shadow or equivalent dependency-bundling configuration for SQLite.
- Access transformer location/syntax compatibility, or the target-version equivalent.

Player-facing behavior at risk:

- The mod may not load at all.
- SQLite may not be present at runtime, breaking all persistent NPC records.
- Client/server runs may miss generated resources.
- Access transformer failure can break code paths that currently rely on widened access.
- Registry names or mod metadata changes can break existing worlds and resource packs.

Tests/checklist needed before porting:

- Current `gradlew build` result and logs.
- Current client/server launch result.
- Confirm `blockparty.db` can be created in a test world.
- Confirm the shaded jar contains the relocated SQLite classes.
- Save current `mods.toml` mod ID, version behavior, and resource paths.
- Record generated resource behavior from the current `data` run if used.

Suggested migration order:

- Do this first. Nothing else can be tested reliably until the target build loads a blank world with the mod present.

## Event Bus Hooks

Current Forge 1.19.4 implementation classes/files:

- `src/main/java/block_party/BlockParty.java`
- `src/main/java/block_party/registry/CustomMessenger.java`
- `src/main/java/block_party/registry/CustomBlockEntities.java`
- `src/main/java/block_party/registry/CustomBlocks.java`
- `src/main/java/block_party/registry/CustomEntities.java`
- `src/main/java/block_party/registry/CustomItems.java`
- `src/main/java/block_party/registry/CustomParticles.java`
- `src/main/java/block_party/registry/CustomSounds.java`
- `src/main/java/block_party/registry/CustomWorldGen.java`
- `src/main/java/block_party/registry/CustomResources.java`
- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/entities/data/HidingSpots.java`
- Forge mod bus via `FMLJavaModLoadingContext.get().getModEventBus()`
- Forge common bus via `MinecraftForge.EVENT_BUS`
- lifecycle events such as `FMLCommonSetupEvent`, `FMLClientSetupEvent`, `EntityAttributeCreationEvent`, `AddReloadListenerEvent`, `RegisterClientReloadListenersEvent`, `LevelEvent`, `PlayerEvent`, block/entity interaction events

Likely NeoForge replacement area:

- NeoForge mod lifecycle events and mod bus access for the selected version.
- NeoForge common event bus annotations/subscriptions.
- Target-version replacements for client setup, entity attribute registration, reload listener registration, level load/unload, player login, block events, piston events, and entity join-level events.

Player-facing behavior at risk:

- Blocks/items/entities may not register.
- Entity attributes may be missing, causing Moes to fail to spawn or behave incorrectly.
- Hiding reveal events may stop firing.
- Database setup/teardown may stop happening.
- Client renderers/resources may not register.
- Shrine sync on login may stop.

Tests/checklist needed before porting:

- Smoke launch with all registries present.
- GameTest spawn a Moe from a valid block.
- GameTest hide/reveal via break, piston, and falling block.
- GameTest player login sends/updates shrine list.
- GameTest database tables are created on world load and connections close on unload.

Suggested migration order:

- Port immediately after build setup. Keep each hook small and verify load/spawn after every few event migrations.

## Resource Reload Listeners

Current Forge 1.19.4 implementation classes/files:

- `src/main/java/block_party/registry/CustomResources.java`
- `src/main/java/block_party/registry/resources/Scenes.java`
- `src/main/java/block_party/registry/resources/Names.java`
- `src/main/java/block_party/registry/resources/MoeTextures.java`
- `src/main/java/block_party/registry/resources/MoeSounds.java`
- `src/main/java/block_party/registry/resources/BlockAliases.java`
- `src/main/java/block_party/utils/JsonUtils.java`
- `src/main/resources/data/block_party/scenes/test_dialogue.json`
- `src/main/resources/data/block_party/scenes/test_hide.json`
- `src/main/resources/data/block_party/moes/names/*.json`
- `src/main/resources/data/block_party/moes/aliases/*.json`
- `src/main/resources/data/minecraft/moes/sounds/*.json`
- `SimpleJsonResourceReloadListener`
- `AddReloadListenerEvent`
- `RegisterClientReloadListenersEvent`

Likely NeoForge replacement area:

- Target-version server resource reload listener registration.
- Target-version client reload listener registration.
- Updated JSON reload APIs if `SimpleJsonResourceReloadListener` signatures changed.
- Registry lookup access during reload, especially for blocks and sounds.

Player-facing behavior at risk:

- Moes may lose names.
- Dialogue scenes may not load, so right-click/left-click behavior appears dead.
- Block aliases may not apply, changing visible Moe identity.
- Texture overrides and sound overrides may not apply.
- Data packs may stop customizing Moes.

Tests/checklist needed before porting:

- Resource reload smoke test for scenes, names, textures, sounds, aliases.
- GameTest right-click `test_dialogue.json`.
- GameTest left-click `test_hide.json`.
- GameTest block tags and aliases affect a spawned Moe.
- Manual data-pack override with one custom name list, scene, texture, and sound.

Suggested migration order:

- Port before entity/dialogue parity tests, because spawn/profile/dialogue tests depend on loaded resources.

## Entity Synced Data

Current Forge 1.19.4 implementation classes/files:

- `src/main/java/block_party/entities/Moe.java`
- `src/main/java/block_party/entities/MoeInHiding.java`
- `src/main/java/block_party/entities/BlockPartyNPC.java`
- `src/main/java/block_party/entities/abstraction/Layer2.java`
- `src/main/java/block_party/entities/abstraction/Layer3.java`
- `src/main/java/block_party/entities/abstraction/Layer4.java`
- `src/main/java/block_party/entities/abstraction/Layer5.java`
- `src/main/java/block_party/entities/abstraction/Layer7.java`
- `SynchedEntityData`
- `EntityDataAccessor`
- `EntityDataSerializers`
- `defineSynchedData`
- `onSyncedDataUpdated`
- `ClientboundAddEntityPacket` for `MoeInHiding`

Likely NeoForge replacement area:

- Target-version entity data accessor registration and serializer APIs.
- Entity spawn packet APIs.
- Entity NBT/save method naming changes.
- Target-version entity dimensions, pose, navigation, attributes, and interaction methods.

Player-facing behavior at risk:

- Moe block identity, scale, emotion, name, owner, following state, stats, animation, corporeal state, and DB ID may desync.
- Client renderers may show wrong texture, scale, emotion, or animation.
- Database sync tied to `onSyncedDataUpdated` may stop or become too chatty.
- Hidden marker database ID and attach position may not sync to clients/server correctly.

Tests/checklist needed before porting:

- GameTest spawn Moe and assert owner, block state, visible block state, scale, profile fields, following flag, DB ID.
- GameTest hide and reveal with `MoeInHiding` attach position and database ID.
- GameTest save/load for all entity data fields that affect behavior.
- Manual render screenshot for scale, emotion, animation, name, health, glow, and block texture.

Suggested migration order:

- Port after resources and registries load, before networking and UI. Packets and screens depend on entity/record data being stable.

## SavedData And World Storage

Current Forge 1.19.4 implementation classes/files:

- `src/main/java/block_party/db/BlockPartyDB.java`
- `src/main/java/block_party/entities/data/HidingSpots.java`
- `src/main/java/block_party/db/DimBlockPos.java`
- `src/main/java/block_party/db/Recordable.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/db/records/Shrine.java`
- `src/main/java/block_party/db/records/Garden.java`
- `src/main/java/block_party/db/records/Location.java`
- `src/main/java/block_party/db/records/Sapling.java`
- `SavedData`
- `DimensionDataStorage`
- `ServerLevel.getDataStorage()`
- `Server.getWorldPath(new LevelResource("blockparty.db"))`
- `LevelEvent.Load`
- `LevelEvent.Unload`

Likely NeoForge replacement area:

- Target-version `SavedData` factory/compute APIs.
- Server/world data storage access.
- Level load/unload event replacements.
- Target-version world path APIs.
- Any changes around logical server level access.

Player-facing behavior at risk:

- Player known-NPC lists may disappear.
- Claimed names may be reused unexpectedly.
- Hidden Moes may reveal incorrectly or vanish.
- Shrine/garden/location records may be lost.
- Existing `blockparty.db` files may not be found.
- SQLite connections may leak or fail to close.

Tests/checklist needed before porting:

- Record current SQLite schema and sample rows.
- GameTest or manual save/reload for visible Moe, hidden Moe, player NPC list, claimed names, shrine list, garden/location records.
- Multi-dimension hidden Moe baseline because `HidingSpots` keys by `BlockPos` and relies on storage context.
- Manual server stop/start with existing `blockparty.db`.

Suggested migration order:

- Port after entity data, before networking and phone/yearbook behavior. Most companion tools depend on persisted `NPC` records.

## Networking

Current Forge 1.19.4 implementation classes/files:

- `src/main/java/block_party/registry/CustomMessenger.java`
- `src/main/java/block_party/messages/AbstractMessage.java`
- `src/main/java/block_party/messages/CDialogueClose.java`
- `src/main/java/block_party/messages/CDialogueRespond.java`
- `src/main/java/block_party/messages/CNPCQuery.java`
- `src/main/java/block_party/messages/CNPCRequest.java`
- `src/main/java/block_party/messages/CNPCRemove.java`
- `src/main/java/block_party/messages/CNPCTeleport.java`
- `src/main/java/block_party/messages/CRemovePage.java`
- `src/main/java/block_party/messages/SCloseDialogue.java`
- `src/main/java/block_party/messages/SNPCList.java`
- `src/main/java/block_party/messages/SNPCResponse.java`
- `src/main/java/block_party/messages/SOpenCellPhone.java`
- `src/main/java/block_party/messages/SOpenController.java`
- `src/main/java/block_party/messages/SOpenDialogue.java`
- `src/main/java/block_party/messages/SOpenYearbook.java`
- `src/main/java/block_party/messages/SShrineList.java`
- `SimpleChannel`
- `NetworkRegistry.ChannelBuilder`
- `NetworkDirection`
- `NetworkEvent.Context`
- `FriendlyByteBuf`

Likely NeoForge replacement area:

- NeoForge target-version custom payload registration.
- Clientbound/serverbound payload handlers.
- Payload codecs or stream codecs if required by target version.
- Updated packet distributor/send-to-player/send-to-server helpers.
- Thread/enqueue handling for client and server packet work.

Player-facing behavior at risk:

- Dialogue screens may not open.
- Dialogue responses may not reach the server.
- Yearbook and Cell Phone may not open or receive NPC data.
- Cell Phone teleport may fail.
- Shrine locations may not sync to the client.
- Yearbook page removal may fail.

Tests/checklist needed before porting:

- Packet encode/decode round trips for all packet payloads.
- GameTest `CDialogueRespond` sets the server-side response.
- GameTest `CNPCRequest` returns correct NPC data for an owned NPC.
- GameTest `CNPCTeleport` moves a Moe near the player.
- Manual UI open checks for `SOpenDialogue`, `SOpenYearbook`, and `SOpenCellPhone`.
- Record current packet registration order if compatibility with old clients/world tooling matters.

Suggested migration order:

- Port after persistence is working, before client UI. Keep packet payload data unchanged until behavior parity is proven.

## Chunk Forcing And Teleport

Current Forge 1.19.4 implementation classes/files:

- `src/main/java/block_party/world/CellPhone.java`
- `src/main/java/block_party/world/chunk/ForcedChunk.java`
- `src/main/java/block_party/messages/CNPCTeleport.java`
- `src/main/java/block_party/db/records/NPC.java`
- `src/main/java/block_party/entities/abstraction/Layer1.java`
- `src/main/java/block_party/entities/Moe.java`
- `ITeleporter`
- `PortalInfo`
- `Entity.changeDimension(...)`
- `ServerLevel.getChunk(...)`
- `ServerChunkCache.updateChunkForced(...)`

Likely NeoForge replacement area:

- Target-version entity teleport/change-dimension APIs.
- NeoForge teleporter/event hooks for cross-dimension movement.
- Target-version forced chunk APIs or ticketing APIs.
- Server chunk loading guarantees before entity lookup.

Player-facing behavior at risk:

- Cell Phone may fail to find distant Moes.
- Called Moes may not teleport near the player.
- Moes may appear in the wrong dimension or position.
- Forced chunks may remain loaded or unload too soon.
- `Moe.onTeleport` may not set following behavior.

Tests/checklist needed before porting:

- GameTest or manual test with a known Moe far from the player.
- Phone call from same dimension.
- Phone call from another dimension if current behavior supports it.
- Verify final position is near the player's facing offset.
- Verify `isFollowing` is true after successful phone call.
- Verify forced chunk is released after teleport attempt.

Suggested migration order:

- Port after networking and persistence, because phone behavior needs packets, DB rows, and server entity lookup.

## Rendering And Model Layers

Current Forge 1.19.4 implementation classes/files:

- `src/main/java/block_party/client/BlockPartyRenderers.java`
- `src/main/java/block_party/client/renderers/MoeRenderer.java`
- `src/main/java/block_party/client/renderers/MoeInHidingRenderer.java`
- `src/main/java/block_party/client/model/MoeModel.java`
- `src/main/java/block_party/client/model/SamuraiModel.java`
- `src/main/java/block_party/client/renderers/layers/EmoteLayer.java`
- `src/main/java/block_party/client/renderers/layers/GlowLayer.java`
- `src/main/java/block_party/client/renderers/layers/SpecialLayer.java`
- `src/main/java/block_party/client/renderers/layers/special/BarrelOverlay.java`
- `src/main/java/block_party/client/particle/*.java`
- `EntityRenderersEvent.RegisterRenderers`
- `EntityRenderersEvent.RegisterLayerDefinitions`
- `ModelLayerLocation`
- `MobRenderer`
- `PoseStack`
- `MultiBufferSource`
- `ItemBlockRenderTypes` calls currently commented in `CustomBlocks.registerRenderTypes`

Likely NeoForge replacement area:

- Target-version entity renderer registration events.
- Model layer definition events.
- RenderType/block render layer registration replacement.
- Particle provider registration events.
- Pose/model/render API changes.
- Armor/model layer setup changes.

Player-facing behavior at risk:

- Moes may render missing, invisible, untextured, or at wrong scale.
- Emotion eyes/faces may fail.
- Glow layer may fail.
- Special overlays such as barrel overlay may fail.
- Name/health labels may disappear.
- Transparent decorative blocks may render incorrectly.
- Particles may not spawn or may use wrong sprites.

Tests/checklist needed before porting:

- Manual screenshot set from `docs/TESTING_STRATEGY.md` golden world.
- Include normal cube, partial block, glow block, cat-feature block, block alias, festive block, and barrel overlay if available.
- Verify `MoeInHiding` remains intentionally non-disruptive visually.
- Verify name/health distance behavior.
- Verify cutout block rendering while `CustomBlocks.registerRenderTypes` is commented.

Suggested migration order:

- Port after server entity/data parity, before screen polish. Rendering needs stable entity state and resources.

## UI And Screens

Current Forge 1.19.4 implementation classes/files:

- `src/main/java/block_party/client/screens/AbstractScreen.java`
- `src/main/java/block_party/client/screens/DialogueScreen.java`
- `src/main/java/block_party/client/screens/ControllerScreen.java`
- `src/main/java/block_party/client/screens/YearbookScreen.java`
- `src/main/java/block_party/client/screens/CellPhoneScreen.java`
- `src/main/java/block_party/client/screens/widget/RespondIconButton.java`
- `src/main/java/block_party/client/screens/widget/RespondTextButton.java`
- `src/main/java/block_party/messages/SOpenDialogue.java`
- `src/main/java/block_party/messages/SOpenYearbook.java`
- `src/main/java/block_party/messages/SOpenCellPhone.java`
- `Minecraft.setScreen(...)`
- `Button`
- `Tooltip`
- `PoseStack`
- `GuiGraphics` likely replacement area in newer versions
- `SimpleSoundInstance.forUI(...)`

Likely NeoForge replacement area:

- Target-version Minecraft screen/render APIs.
- Button/widget constructor changes.
- Tooltip APIs.
- GUI rendering stack changes.
- Client sound manager APIs.
- Client entity preview rendering APIs.

Player-facing behavior at risk:

- Dialogue UI may not open, render, or close correctly.
- Response buttons may not send responses.
- Speaker preview may render wrong or not at all.
- Yearbook/Cell Phone screens may fail to request or display NPC data.
- UI may pause unexpectedly or fail to respect held-item checks.
- Text reveal and sound playback may change.

Tests/checklist needed before porting:

- Manual dialogue flow with `test_dialogue.json`.
- Manual response button click and keyboard controls.
- Manual `ESC` close behavior.
- Manual Yearbook open with no NPCs and with known NPCs.
- Manual Yearbook open on a player-owned Moe and someone else's Moe.
- Manual Cell Phone open, list navigation, request, and call.
- Screenshots at common window sizes.

Suggested migration order:

- Port after networking and rendering. Screens are the final integration point for packet, entity preview, sound, and UI APIs.

## Final Pre-Port Checklist

Before changing API surfaces, capture:

- Current build/client/server launch logs.
- Current DB schema and sample `blockparty.db`.
- Golden world with visible Moes, hidden Moes, shrine/garden/location records, and known NPC list.
- Screenshots of representative Moes and all screens.
- Manual notes for sounds and dialogue flow.
- GameTest or manual baseline for hide/reveal, phone teleport, Yearbook, and resource reload.
- Known bug baselines from `docs/TECH_DEBT.md`, especially resolved combat non-recursion and hide-condition save/load behavior, plus remaining texture lookup fallback, no-op DB update, and hidden spot null/multi-dimension behavior.

Only after this checklist is captured should migration code changes begin.
