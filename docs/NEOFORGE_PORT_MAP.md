# NeoForge Port Map

This audit is scoped to Forge-specific API usage that is likely to matter during a Forge 1.19.4 to NeoForge port. It intentionally does not cover build tooling, content data, gameplay bugs, or unrelated Forge APIs.

NeoForge APIs vary by Minecraft target version. The replacements below are migration targets to verify after choosing the exact target version, not final import lists. In particular, networking, SavedData, reload listener, and GameTest APIs changed across NeoForge 1.20.6, 1.21.4, 1.21.5, and later.

Reference docs checked while writing this map:

- NeoForge events: https://docs.neoforged.net/docs/1.21.4/concepts/events/
- NeoForge registries: https://docs.neoforged.net/docs/1.21.4/concepts/registries/
- NeoForge payload networking: https://docs.neoforged.net/docs/1.21.4/networking/payload/
- NeoForge particles: https://docs.neoforged.net/docs/1.21.5/resources/client/particles/
- NeoForge entity renderers: https://docs.neoforged.net/docs/1.21.5/entities/renderer/
- NeoForge SavedData: https://docs.neoforged.net/docs/1.21.4/datastorage/saveddata/
- NeoForge GameTests: https://docs.neoforged.net/docs/1.21.4/misc/gametest/

## Networking Registration And Packet Handlers

Current Forge 1.19.4 implementation:

- `src/main/java/block_party/BlockParty.java` owns `public static final SimpleChannel MESSENGER = CustomMessenger.create()`.
- `src/main/java/block_party/registry/CustomMessenger.java` creates a `NetworkRegistry.ChannelBuilder` `SimpleChannel`, registers packets during `FMLCommonSetupEvent` and `FMLClientSetupEvent`, and sends via `SimpleChannel#sendTo` / `sendToServer`.
- `src/main/java/block_party/messages/AbstractMessage.java` routes packets through `NetworkEvent.Context`, `NetworkDirection`, `context.enqueueWork`, `context.getSender`, and client-side `Minecraft.getInstance()`.
- Packet payloads are manual `FriendlyByteBuf` encoders/decoders. Clientbound: `SCloseDialogue`, `SNPCList`, `SNPCResponse`, `SOpenCellPhone`, `SOpenDialogue`, `SOpenYearbook`, `SShrineList`. Serverbound: `CDialogueClose`, `CDialogueRespond`, `CNPCRemove`, `CNPCRequest`, `CNPCTeleport`, `CRemovePage`.

Likely NeoForge replacement APIs:

- Replace `SimpleChannel` with NeoForge custom payload registration through `RegisterPayloadHandlersEvent`, `PayloadRegistrar`, `CustomPacketPayload`, and target-version codec APIs such as `StreamCodec`.
- For 1.21.4-style APIs, register serverbound/clientbound payloads with the play-phase `PayloadRegistrar` methods and handle with `IPayloadContext`.
- For newer NeoForge versions, verify whether clientbound handlers must use `RegisterClientPayloadHandlersEvent`; current docs describe a separate client payload handler event to avoid loading client-only code on both physical sides.
- Replace `NetworkDirection` branching with separate typed handlers per direction.
- Replace static `SimpleChannel` sends with target-version NeoForge packet distribution helpers, or with player/server connection send methods for registered `CustomPacketPayload`s.

Migration difficulty: High.

Existing regression/GameTests protecting behavior:

- `NetworkRegressionTest` covers byte-level payload round trips, invalid/truncated payload failure behavior, `SOpenDialogue` sound/voice registry ID preservation, `SNPCResponse`, NPC query packet IDs, shrine lists, and Yearbook/Cell Phone open payloads.
- `ViewModelRegressionTest` covers dialogue response payload creation from UI state.
- `CellPhoneGameTests` directly exercises the server-side `CNPCTeleport` handler logic for success, dead/missing NPC safety, following flag, and forced chunk release.

Missing parity tests before migration:

- Live client-to-server packet dispatch for `CDialogueRespond`, `CDialogueClose`, `CNPCRequest`, `CNPCRemove`, and `CRemovePage` using the real network path or an integration harness.
- Live server-to-client packet dispatch for all screen-opening packets and `SShrineList`.
- A client/server protocol compatibility test that catches packet ID/order drift or renamed payload IDs.
- Negative tests for missing player sender on serverbound payloads and client-only handler isolation on dedicated server startup.

## Client Screen Opening

Current Forge 1.19.4 implementation:

- Screen opening is driven by clientbound packet handlers in `src/main/java/block_party/messages`.
- `SOpenDialogue#handle` calls `minecraft.setScreen(new DialogueScreen(...))`.
- `SOpenController#handle` calls `minecraft.setScreen(this.getScreen())`; subclasses `SOpenCellPhone` and `SOpenYearbook` create `CellPhoneScreen` and `YearbookScreen`.
- `SCloseDialogue#handle` closes an open dialogue via `minecraft.setScreen(null)`.
- Screen classes use vanilla client APIs plus Forge-only `@OnlyIn(Dist.CLIENT)` annotations.

Likely NeoForge replacement APIs:

- `Minecraft#setScreen` remains the likely vanilla client API for direct screen changes.
- Replace Forge annotations/imports with NeoForge equivalents where still needed, or avoid loader annotations by keeping client-only classes behind client-only packet handlers and client event wiring.
- Ensure clientbound payload handlers are registered in a client-only NeoForge handler event if required by the chosen target version.

Migration difficulty: Medium.

Existing regression/GameTests protecting behavior:

- `ViewModelRegressionTest` covers screen state logic for controller selection, Yearbook removal/close behavior, Cell Phone contact paging, and dialogue response/close state.
- `NetworkRegressionTest` covers payloads that carry screen state into the client.

Missing parity tests before migration:

- Manual or automated client smoke test that receiving `SOpenDialogue`, `SOpenCellPhone`, and `SOpenYearbook` actually displays the correct screen.
- Client close-flow parity test for `SCloseDialogue` and the Yearbook done/remove flows.
- Dedicated server startup check ensuring screen classes are not loaded by common networking registration.

## Renderer Registration

Current Forge 1.19.4 implementation:

- `src/main/java/block_party/client/BlockPartyRenderers.java` registers `EntityRenderersEvent.RegisterRenderers` on the mod bus.
- It registers `CustomEntities.MOE` to `MoeRenderer` and `CustomEntities.MOE_IN_HIDING` to `MoeInHidingRenderer`.
- `BlockPartyClientEvents.register` wires renderer registration from the client-only path in `BlockParty`.

Likely NeoForge replacement APIs:

- Replace imports with `net.neoforged.neoforge.client.event.EntityRenderersEvent`.
- Keep registration on the mod event bus, using the target-version event type `EntityRenderersEvent.RegisterRenderers`.
- Replace `RegistryObject#get` with `DeferredHolder#get` or whatever holder type is used after deferred-register migration.

Migration difficulty: Low to Medium.

Existing regression/GameTests protecting behavior:

- `RegistryGameTests` confirms the entity types are registered.
- `MoeSpawnGameTests`, `MoeHideGameTests`, and `CellPhoneGameTests` confirm server-side entity lifecycle and identity, but not rendering.

Missing parity tests before migration:

- Client screenshot/manual smoke test for visible Moe rendering, hidden-Moe marker rendering, and renderer layer stack.
- Dedicated server startup check to catch accidental client renderer classloading.

## Model Layer Registration

Current Forge 1.19.4 implementation:

- `BlockPartyRenderers` defines `ModelLayerLocation`s for `SAMURAI_INNER_ARMOR`, `SAMURAI_OUTER_ARMOR`, and `MOE`.
- It handles `EntityRenderersEvent.RegisterLayerDefinitions` and registers layer definitions from `SamuraiModel.create(...)` and `MoeModel.create(...)`.

Likely NeoForge replacement APIs:

- Replace imports with `net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions`.
- Keep model-layer registration on the client mod bus.
- Verify target-version model construction signatures, especially if Minecraft model builders or armor model APIs changed.

Migration difficulty: Medium.

Existing regression/GameTests protecting behavior:

- No direct regression or GameTest coverage for model layer registration.
- Server-side GameTests indirectly protect entity data that renderers consume, such as actual block state, scale, identity, and hidden attach position.

Missing parity tests before migration:

- Client startup smoke test that layer definitions register without missing-layer exceptions.
- Screenshot/manual check for Moe body proportions, armor inner/outer layers, and special render layers.

## Particle Registration

Current Forge 1.19.4 implementation:

- `src/main/java/block_party/registry/CustomParticles.java` registers `SimpleParticleType`s through Forge `DeferredRegister<ParticleType<?>>` and `RegistryObject`.
- `src/main/java/block_party/client/BlockPartyClientEvents.java` listens for `RegisterParticleProvidersEvent` and calls `Minecraft.getInstance().particleEngine.register(...)` for firefly, ginkgo, sakura, and white sakura factories.
- Particle JSON and textures live under `assets/block_party/particles` and `assets/block_party/textures/particle`.

Likely NeoForge replacement APIs:

- Keep particle types as registry entries through NeoForge `DeferredRegister`.
- Register providers on the client mod bus using `net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent`.
- Prefer target-version event helper methods, such as `registerSpriteSet` / equivalent, over direct `Minecraft.getInstance().particleEngine.register` if required by the chosen NeoForge version.

Migration difficulty: Medium.

Existing regression/GameTests protecting behavior:

- No direct particle provider tests.
- `RegistryGameTests` currently checks representative block/item/entity/block-entity registrations, but not particle registrations.

Missing parity tests before migration:

- Registry GameTest or startup assertion that all four particle types are registered under stable IDs.
- Client manual/screenshot smoke test that firefly, ginkgo, sakura, and white sakura particles spawn and use the expected textures.
- Dedicated server startup check for no client particle provider classloading.

## Entity Synced Data

Current Forge 1.19.4 implementation:

- Uses vanilla `SynchedEntityData`, `EntityDataAccessor`, and `EntityDataSerializers`.
- `Layer2` syncs actual/visible block state, scale, and corporeal flag.
- `Layer3` syncs owner UUID and following flag.
- `Layer4` syncs traits/profile/stats: blood type, dere, zodiac, emotion, gender, given name, food, exhaustion, saturation, stress, relaxation, loyalty, affection, slouch, and age.
- `Layer5` syncs database ID.
- `Layer7` syncs animation.
- `MoeInHiding` syncs database ID and attached block position.

Likely NeoForge replacement APIs:

- This is mostly a vanilla Minecraft API migration rather than a Forge-to-NeoForge API replacement.
- Verify target-version method signatures for `defineSynchedData`, `onSyncedDataUpdated`, serializers, and entity NBT methods.
- Consider whether any synced field should move to NeoForge data attachments only after preserving parity; current behavior depends on vanilla entity data propagation.

Migration difficulty: Medium to High, because the API may remain conceptually similar but many fields are behavior-critical.

Existing regression/GameTests protecting behavior:

- `MoeSpawnGameTests` covers spawned Moe block state and owner UUID.
- `MoeHideGameTests` covers hidden attach position, hidden database ID, hide/reveal identity, hidden-Moe NBT serialization, and actual block state preservation.
- `CellPhoneGameTests` covers following flag after teleport.
- `PersistenceRegressionTest` covers row/NBT representation for NPC identity, dimensions, stats, and traits, but not live entity sync to a client.

Missing parity tests before migration:

- Explicit GameTest or entity serialization test for all synced `Layer4` profile/stat fields.
- Save/load parity for visible Moe synced fields, not only hidden Moe.
- Client-observed sync test or manual check that changing scale, block state, emotion, and animation updates rendering without relog.

## SavedData Lifecycle

Current Forge 1.19.4 implementation:

- `BlockPartyDB` extends `SavedData`, stores names and NPC IDs by player, opens SQLite connections, and uses `LevelEvent.Load`, `LevelEvent.Unload`, and `PlayerEvent.PlayerLoggedInEvent`.
- `BlockPartyDB#get(Level)` stores data in the overworld `DimensionDataStorage` using `computeIfAbsent(BlockPartyDB::load, BlockPartyDB::new, KEY)`.
- `HidingSpots` extends `SavedData`, stores `BlockPos -> database ID` per `ServerLevel`, marks dirty on mutation, and is also driven by block/entity events.
- `SceneVariables` extends `SavedData`, stores scene cookies/counters/locations/targets in overworld data storage.

Likely NeoForge replacement APIs:

- For NeoForge 1.21.4-style targets, use `SavedData.Factory` with `DimensionDataStorage#computeIfAbsent(factory, key)` and update `save`/load methods for `HolderLookup.Provider`.
- For NeoForge 1.21.5+ targets, verify the newer `SavedDataType` API; docs note that `computeIfAbsent` / `get` take only the `SavedDataType`.
- Replace Forge event imports with NeoForge `LevelEvent`, player login event, and bus packages.
- Re-check world-path APIs for the SQLite file and lifecycle timing of load/unload events.

Migration difficulty: High.

Existing regression/GameTests protecting behavior:

- `PersistenceRegressionTest` covers `DimBlockPos`, row/NBT round trips, generated SQLite SQL, no-op row update safety, and dirty row update behavior.
- `MoeHideGameTests.hiddenMoeSerializationReloadRevealRestoresIdentity` covers hidden-Moe NBT restore and reveal through persisted hiding spot state inside a running GameTest.
- `CellPhoneGameTests` covers DB-backed NPC lookup paths for teleport behavior.

Missing parity tests before migration:

- Full world save/reload or golden-world test for `BlockPartyDB` names and NPC IDs by player.
- Full world save/reload test for `SceneVariables`.
- Full world save/reload test for `HidingSpots` using actual `SavedData` disk persistence, not only entity NBT reconstruction.
- Load/unload test that SQLite connections are closed and a second world/session can reopen the DB cleanly.
- Player login integration test that `SShrineList` is sent after saved shrine data is available.

## Event Bus Usage

Current Forge 1.19.4 implementation:

- `BlockParty` gets the mod bus through `FMLJavaModLoadingContext.get().getModEventBus()`.
- It uses `MinecraftForge.EVENT_BUS` for common/game events and passes the mod bus to registries and client setup.
- Static subscribers use `@Mod.EventBusSubscriber` plus `@SubscribeEvent`, including `BlockPartyDB`, `HidingSpots`, `SamuraiArmorItem`, `SamuraiKatanaItem`, and `JapanRenderer`.
- Mod bus listeners include common/client setup, entity attributes, renderer/layer registration, particle providers, and custom registry setup.

Likely NeoForge replacement APIs:

- Use NeoForge's constructor-injected mod bus pattern where available: `public BlockParty(IEventBus modBus)`.
- Replace package imports with `net.neoforged.bus.api.IEventBus`, `net.neoforged.bus.api.SubscribeEvent`, `net.neoforged.fml.common.Mod`, and `net.neoforged.neoforge.common.NeoForge.EVENT_BUS`.
- Keep mod lifecycle and registry events on the mod bus; keep gameplay events on `NeoForge.EVENT_BUS`.
- Add `modid = BlockParty.ID` to event bus subscribers during migration to improve diagnostics.

Migration difficulty: High as a cross-cutting change.

Existing regression/GameTests protecting behavior:

- `MoeHideGameTests.missingHiddenSpotSpawnAndRevealEventsNoOp` directly calls the current Forge event handlers for left click, break, piston pre-event, and falling block join.
- `MoeHideGameTests.hiddenSpotDisturbanceRevealsMoe` protects reveal behavior behind those handlers.
- `CellPhoneGameTests` and `MoeSpawnGameTests` protect several event-adjacent behaviors, but not registration itself.

Missing parity tests before migration:

- Startup smoke test that all mod-bus listeners register on the correct side and dedicated server startup does not load client classes.
- In-world tests that trigger the real event bus for hide/reveal events rather than directly invoking handlers.
- Player login event integration test for shrine list sync.
- Client-only render/skybox event smoke test if keeping `JapanRenderer`.

## Deferred Registers

Current Forge 1.19.4 implementation:

- `BlockParty` owns Forge `DeferredRegister`s for block entities, blocks, entities, items, particles, features, sounds, scene actions, and scene filters.
- Registry classes use Forge `RegistryObject`.
- `SceneActions` and `SceneFilters` create custom Forge registries with `RegistryBuilder`, `IForgeRegistry`, and `DeferredRegister.create(new ResourceLocation(...), BlockParty.ID)`.
- `CustomEntities` also registers entity attributes through `EntityAttributeCreationEvent`.

Likely NeoForge replacement APIs:

- Replace Forge registries with `net.neoforged.neoforge.registries.DeferredRegister`.
- Replace `RegistryObject` with `DeferredHolder` or the target-version recommended holder/supplier type.
- Use `BuiltInRegistries` or NeoForge registry constants as appropriate for vanilla registries.
- Rebuild custom scene registries with NeoForge `RegistryBuilder`, `NewRegistryEvent`, or target-version custom registry guidance.
- Keep `RegisterEvent` in mind for any entries that do not fit `DeferredRegister`, though NeoForge docs still recommend `DeferredRegister`.

Migration difficulty: High, mostly because this touches almost every content class and custom scene registry.

Existing regression/GameTests protecting behavior:

- `RegistryGameTests` checks representative block, item, entity, and block-entity registry IDs.
- `RegistryContractRegressionTest` protects stable vanilla sound IDs and scene sound/voice ID parsing.
- `SceneContractRegressionTest`, `SceneJsonParsingRegressionTest`, and `TraitRegressionTest` protect scene action/filter semantics and trait parsing around the custom registries.

Missing parity tests before migration:

- Expand registry GameTests to cover particles, sounds, worldgen features, scene actions, and scene filters.
- Custom registry test that every scene JSON action/filter ID resolves after registry freeze.
- Stable ID inventory for all public blocks/items/entities/sounds/particles to catch accidental renames.

## Resource Reload Listeners

Current Forge 1.19.4 implementation:

- `CustomResources` listens on the Forge common bus.
- Server reload listeners are registered through `AddReloadListenerEvent`: `BlockAliases`, `MoeSounds`, `Names`, and `Scenes`.
- Client reload listener is registered through `RegisterClientReloadListenersEvent`: `MoeTextures`.
- Resource classes extend vanilla `SimpleJsonResourceReloadListener`.

Likely NeoForge replacement APIs:

- For older NeoForge targets, replace imports with `net.neoforged.neoforge.event.AddReloadListenerEvent` and `net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent`.
- For newer 1.21.4+ targets, verify renamed events such as `AddServerReloadListenersEvent` and `AddClientReloadListenersEvent`.
- Update `SimpleJsonResourceReloadListener` constructors and `apply` signatures for target-version `HolderLookup.Provider` / registry access changes if present.
- Keep server data listeners and client texture listeners side-separated.

Migration difficulty: Medium to High.

Existing regression/GameTests protecting behavior:

- `SceneJsonParsingRegressionTest` covers bundled scene JSON parsing.
- `MoeTexturesRegressionTest` covers texture override selection logic.
- `RegistryContractRegressionTest` covers sound/voice ID behavior used by resource-loaded scenes.
- GameTests indirectly rely on bundled resources for spawn/hide behavior.

Missing parity tests before migration:

- Resource reload integration test that all bundled listeners load expected counts without parse errors.
- Data-pack override test for scenes, names, block aliases, Moe sounds, and Moe textures.
- Client-only reload smoke test for `MoeTextures` after resource pack reload.
- Server dedicated startup check that client reload listener is not registered on the server.

## GameTest Compatibility

Current Forge 1.19.4 implementation:

- `build.gradle` enables Forge GameTest run properties: `forge.enableGameTest`, `forge.gameTestServer`, and `forge.enabledGameTestNamespaces`.
- GameTest classes use vanilla `net.minecraft.gametest.framework.GameTest` / `GameTestHelper`.
- Forge annotations `net.minecraftforge.gametest.GameTestHolder` and `PrefixGameTestTemplate` register classes under `block_party`.
- GameTests also use Forge helpers/events such as `FakePlayerFactory`, `EntityJoinLevelEvent`, `PlayerInteractEvent`, `BlockEvent`, and `PistonEvent`.

Likely NeoForge replacement APIs:

- Replace run properties with `neoforge.enableGameTest` and `neoforge.enabledGameTestNamespaces`.
- Replace Forge annotations/imports with NeoForge GameTest equivalents where available, or register tests through `RegisterGameTestsEvent` on the mod bus.
- For newer NeoForge versions, verify the datapack/test-environment based GameTest system; docs for latest versions describe registered test functions, environments, and test instances.
- Replace Forge test helper/event imports with NeoForge packages.

Migration difficulty: Medium to High, depending on target version.

Existing regression/GameTests protecting behavior:

- `BlockPlacementGameTests` covers basic block placement.
- `RegistryGameTests` covers representative registry entries.
- `MoeSpawnGameTests` covers valid and invalid spawn egg behavior.
- `MoeHideGameTests` covers hide, timed reveal, disturbance reveal, hidden-Moe serialization/reload/reveal, and missing hidden spot no-op behavior across event handlers.
- `CellPhoneGameTests` covers phone teleport, dead/missing NPC safety, following flag, and forced chunk release.

Missing parity tests before migration:

- GameTest runner smoke test under NeoForge before porting gameplay internals, even if only one empty test runs.
- Dialogue interaction GameTest for right-click/open/respond/close server behavior.
- Yearbook server-side ownership/removal tests.
- Full SavedData disk save/reload GameTests or golden-world tests.
- Registry/resource reload GameTests for particles, sounds, scene actions, scene filters, names, and aliases.
