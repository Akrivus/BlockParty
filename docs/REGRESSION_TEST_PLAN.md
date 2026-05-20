# Regression Test Plan

This plan turns the architecture, behavior contract, and technical debt notes into a concrete test backlog. It does not implement tests. The intent is to stop hotfix churn by capturing current behavior before remaining bug fixes and before a Forge-to-NeoForge migration.

Timing labels:

- `Before bug fixes`: write or manually baseline this before changing known risky behavior.
- `Before porting`: required before major API/version migration begins.
- `Before release`: can wait until after the first migration pass, but must exist before a public release.

Priority focus:

- spawn lifecycle
- hide/reveal
- persistence
- dialogue packets
- Yearbook/Cell Phone flows
- SQLite no-op update
- texture lookup
- resource loading

## 1. GameTests For Player-Visible Minecraft Behavior

### `spawn_moe_from_valid_block_creates_owned_record`

- System under test: spawn lifecycle.
- Behavior protected: using `CustomSpawnEggItem` on a block tagged as Moe-spawnable removes the block, spawns one `Moe`, assigns the player as owner, preserves the source block identity, and creates an NPC record.
- Why it matters for Forge/NeoForge migration: touches item use, block tags, entity creation, synced data, DB insertion, and world mutation. This is the core smoke test for the mod.
- Timing: Before porting.
- Likely classes/files: `src/main/java/block_party/items/CustomSpawnEggItem.java`, `src/main/java/block_party/entities/Moe.java`, `src/main/java/block_party/registry/CustomEntities.java`, `src/main/java/block_party/db/records/NPC.java`, `src/main/java/block_party/db/BlockPartyDB.java`, `src/main/resources/data/block_party/tags/blocks/spawns_moes.json`.

### `spawn_moe_from_invalid_block_fails_without_world_change`

- System under test: block-origin rules.
- Behavior protected: invalid blocks do not spawn Moes, do not remove the block, and return the current failure behavior.
- Why it matters for Forge/NeoForge migration: tag lookup and item interaction APIs are version-sensitive; accidental broad spawning would break the core block-person contract.
- Timing: Before porting.
- Likely classes/files: `CustomSpawnEggItem.java`, `CustomTags.java`, `spawns_moes.json`.

### `spawn_preserves_block_state_and_block_entity_data`

- System under test: block-origin data preservation.
- Behavior protected: a Moe retains the clicked block's actual block state and persistent block-entity data where current behavior supports it.
- Why it matters for Forge/NeoForge migration: block state IDs, block entity APIs, and NBT access often change across Minecraft versions.
- Timing: Before porting.
- Likely classes/files: `CustomSpawnEggItem.java`, `Layer2.java`, `MoeInHiding.java`, block entity classes under `src/main/java/block_party/blocks/entity/`.

### `spawn_applies_block_trait_tags_to_profile`

- System under test: personality/profile generation.
- Behavior protected: source block tags set visible profile traits such as gender, blood type, dere, zodiac, wings, glow, cat features, and ignores-volume behavior.
- Why it matters for Forge/NeoForge migration: tag paths and registries are fragile during porting; traits drive rendering, sounds, names, and later behavior.
- Timing: Before porting.
- Likely classes/files: `Layer4.java`, `Layer2.java`, `CustomTags.java`, trait JSON under `src/main/resources/data/block_party/tags/blocks/moe/`.

### `moe_name_claim_is_persisted_for_player`

- System under test: ownership and identity.
- Behavior protected: claimed Moes appear in the player's known NPC list and their generated names are reserved in saved data.
- Why it matters for Forge/NeoForge migration: combines `SavedData`, DB records, owner UUID, and resource-loaded names.
- Timing: Before porting.
- Likely classes/files: `Recordable.java`, `Layer5.java`, `Gender.java`, `Names.java`, `BlockPartyDB.java`, `NPC.java`.

### `right_click_owned_moe_starts_test_dialogue_scene`

- System under test: dialogue scene triggering.
- Behavior protected: owner right-click can trigger the loaded `test_dialogue.json` scene and place the Moe into a waiting-for-response dialogue state.
- Why it matters for Forge/NeoForge migration: verifies resource reload, scene filters/actions, interaction hooks, and server-side dialogue state before UI packets are involved.
- Timing: Before porting.
- Likely classes/files: `Layer7.java`, `SceneManager.java`, `Scenes.java`, `SendDialogue.java`, `src/main/resources/data/block_party/scenes/test_dialogue.json`.

### `dialogue_response_packet_advances_scene`

- System under test: dialogue packets.
- Behavior protected: `CDialogueRespond` finds the right NPC record/server entity and sets the selected `Response`, allowing the scene to advance.
- Why it matters for Forge/NeoForge migration: networking APIs are high-risk; this protects the server-side contract behind `DialogueScreen` buttons.
- Timing: Before porting.
- Likely classes/files: `CDialogueRespond.java`, `CNPCQuery.java`, `Layer7.java`, `SendDialogue.java`, `SendResponse.java`, `Response.java`.

### `left_click_owned_moe_hides_as_original_block`

- System under test: hide/retreat.
- Behavior protected: the test hide scene causes the Moe to disappear, restores its original block at the hiding position, records the hidden spot, and marks the NPC as hiding.
- Why it matters for Forge/NeoForge migration: covers scene action execution, entity removal, block placement, saved data, and DB update.
- Timing: Before bug fixes.
- Likely classes/files: `Layer7.java`, `Hide.java`, `Moe.java`, `Layer2.java`, `HidingSpots.java`, `NPC.java`, `test_hide.json`.

### `hidden_moe_reveals_after_timer`

- System under test: timed hide/reveal.
- Behavior protected: `HideUntil.ONE_SECOND_PASSES` reveals the hidden Moe after the current tick threshold, removes the block, and restores the same NPC record.
- Why it matters for Forge/NeoForge migration: timing, entity tick, saved data, and block mutation APIs are all migration-sensitive.
- Timing: Before bug fixes.
- Likely classes/files: `MoeInHiding.java`, `HideUntil.java`, `HidingSpots.java`, `NPC.java`, `Moe.java`.

### `hidden_moe_reveals_when_block_is_disturbed`

- System under test: hidden spot event hooks.
- Behavior protected: break start, break completion, piston pre-event, and falling-block conversion trigger reveal where current behavior supports it.
- Why it matters for Forge/NeoForge migration: event class names and ordering will change; missing one event creates hard-to-debug hidden-Moe loss.
- Timing: Before porting.
- Likely classes/files: `HidingSpots.java`, `MoeInHiding.java`, Forge event hook usage, `BlockEvent`, `PlayerInteractEvent`, `PistonEvent`, `EntityJoinLevelEvent`.

### `hidden_moe_reload_preserves_hide_until`

- System under test: hidden Moe persistence.
- Behavior protected: verifies the resolved behavior that hidden Moe save/load restores the saved `HideUntil` value, including timed hide modes.
- Why it matters for Forge/NeoForge migration: persistence APIs will change, and the fixed behavior should not regress during the port.
- Timing: Before porting.
- Likely classes/files: `MoeInHiding.java`, `HideUntil.java`, `HidingSpots.java`, `Layer2.java`, `NPC.java`.

### `visible_moe_save_load_restores_profile_block_and_owner`

- System under test: entity and DB persistence.
- Behavior protected: a saved/reloaded visible Moe keeps owner, DB ID, block state, visible block state, name, traits, stats, home, and inventory where current behavior supports it.
- Why it matters for Forge/NeoForge migration: protects `SynchedEntityData`, entity NBT, SQLite rows, and world saved data.
- Timing: Before porting.
- Likely classes/files: `Layer1.java` through `Layer7.java`, `BlockPartyDB.java`, `NPC.java`, `Column.java`, `Row.java`.

### `player_npc_list_survives_world_reload`

- System under test: player-to-NPC persistence.
- Behavior protected: the player's known NPC list still contains claimed Moe IDs after save/reload.
- Why it matters for Forge/NeoForge migration: Yearbook and Cell Phone are useless if `SavedData` migration loses this list.
- Timing: Before porting.
- Likely classes/files: `BlockPartyDB.java`, `Recordable.java`, `CellPhoneItem.java`, `YearbookItem.java`.

### `yearbook_request_returns_owned_npc_record`

- System under test: Yearbook server flow.
- Behavior protected: a Yearbook request for an owned NPC returns an `NPC` payload with the current profile data.
- Why it matters for Forge/NeoForge migration: protects the packet-to-DB-to-screen data path without depending on client rendering.
- Timing: Before porting.
- Likely classes/files: `YearbookItem.java`, `SOpenYearbook.java`, `CNPCRequest.java`, `SNPCResponse.java`, `NPC.java`, `ControllerScreen.java`.

### `yearbook_interact_with_unowned_moe_does_not_claim_or_open_private_record`

- System under test: ownership privacy.
- Behavior protected: using a Yearbook on another player's Moe does not claim it or expose private control.
- Why it matters for Forge/NeoForge migration: owner lookups and player references can change; this prevents accidental privacy regressions.
- Timing: Before release.
- Likely classes/files: `YearbookItem.java`, `Layer3.java`, `BlockPartyDB.java`, `NPC.java`.

### `cell_phone_call_teleports_moe_and_sets_following`

- System under test: Cell Phone flow.
- Behavior protected: calling a listed Moe finds it, teleports it near the player, and sets following mode after a successful call.
- Why it matters for Forge/NeoForge migration: combines packets, DB lookup, forced chunks, teleport APIs, dimension behavior, and entity synced data.
- Timing: Before porting.
- Likely classes/files: `CellPhoneItem.java`, `SOpenCellPhone.java`, `CNPCTeleport.java`, `CellPhone.java`, `ForcedChunk.java`, `NPC.java`, `Moe.java`, `Layer3.java`.

### `cell_phone_missing_target_failure_is_captured`

- System under test: Cell Phone failure path.
- Behavior protected: capture current behavior when the listed NPC cannot be found or loaded.
- Why it matters for Forge/NeoForge migration: prevents silent changes from "fails gracefully" to crash, or vice versa, during API rewrites.
- Timing: Before porting.
- Likely classes/files: `CNPCTeleport.java`, `NPC.java`, `ForcedChunk.java`, `CellPhone.java`.

### `shrine_list_sync_matches_owner_and_dimension_rule`

- System under test: shrine/location sync.
- Behavior protected: `SShrineList` includes the same shrine positions under the current owner/dimension rules.
- Why it matters for Forge/NeoForge migration: player login events, DB queries, and client-side location data are API-sensitive.
- Timing: Before release.
- Likely classes/files: `SShrineList.java`, `Shrine.java`, `BlockPartyDB.java`, `ShrineLocation.java`, `ShrineTabletBlockEntity.java`.

### `resource_reload_enables_dialogue_names_aliases_sounds_and_textures`

- System under test: resource loading.
- Behavior protected: server/client reload listeners populate scenes, names, aliases, sounds, and textures enough for spawned Moes and dialogue to behave.
- Why it matters for Forge/NeoForge migration: reload listener APIs are one of the highest-risk port areas.
- Timing: Before porting.
- Likely classes/files: `CustomResources.java`, `Scenes.java`, `Names.java`, `BlockAliases.java`, `MoeSounds.java`, `MoeTextures.java`, `JsonUtils.java`.

### `combat_attack_behavior_uses_super_without_recursion`

- System under test: combat/adventuring scaffold.
- Behavior protected: verifies Moe attacks route through `Layer1.doHurtTarget` and `Layer7.doHurtTarget` without recursive stack overflow.
- Why it matters for Forge/NeoForge migration: entity combat methods are API-sensitive, and the resolved super-call behavior should be preserved.
- Timing: Before porting.
- Likely classes/files: `Layer1.java`, `Layer7.java`, `Moe.java`, `CustomEntities.java`.

## 2. JUnit Tests For Pure Java, DB, Parsing, And Payload Contracts

### `hide_until_from_value_parses_known_values_and_falls_back`

- System under test: hide-condition parsing.
- Behavior protected: `HideUntil.fromValue` accepts known values and falls back consistently for unknown values.
- Why it matters for Forge/NeoForge migration: supports the hide/reveal bug fix and prevents future data-driven hide regressions.
- Timing: Before bug fixes.
- Likely classes/files: `src/main/java/block_party/entities/goals/HideUntil.java`.

### `scene_trigger_priorities_match_current_contract`

- System under test: scene priority.
- Behavior protected: trigger priorities stay stable for creation, interaction, hurt, attack, stare, random/every tick, and null.
- Why it matters for Forge/NeoForge migration: if scene interruption changes accidentally, dialogue/hide behavior will feel random.
- Timing: Before porting.
- Likely classes/files: `SceneTrigger.java`, `SceneManager.java`.

### `response_parse_and_translation_keys_are_stable`

- System under test: dialogue response IDs.
- Behavior protected: `Response.fromValue` and `getTranslationKey` keep existing data-pack and UI response identifiers stable.
- Why it matters for Forge/NeoForge migration: packet payloads and scene JSON use response enum names.
- Timing: Before porting.
- Likely classes/files: `Response.java`, `SendResponse.java`, `Dialogue.java`.

### `trait_enums_parse_fallback_and_values_are_stable`

- System under test: personality traits.
- Behavior protected: `Gender`, `BloodType`, `Dere`, `Zodiac`, and `Emotion` preserve values and fallback parsing.
- Why it matters for Forge/NeoForge migration: DB rows, NBT, scene filters, and data-pack tags encode these names.
- Timing: Before porting.
- Likely classes/files: `src/main/java/block_party/scene/traits/*.java`, `ITrait.java`, `Column.AsTrait`.

### `blood_type_compatibility_and_weighting_contract`

- System under test: blood-type profile logic.
- Behavior protected: compatibility relationships and weighted selection boundaries are captured.
- Why it matters for Forge/NeoForge migration: personality generation is scattered and easy to perturb.
- Timing: Before release.
- Likely classes/files: `BloodType.java`, `Layer4.java`, `CustomSpawnEggItem.java`.

### `dim_block_pos_nbt_round_trip_preserves_dimension_and_coordinates`

- System under test: position serialization.
- Behavior protected: `DimBlockPos` writes and reads dimension, coordinates, and empty state.
- Why it matters for Forge/NeoForge migration: DB columns, home positions, locations, and hidden spots rely on dimension-aware positions.
- Timing: Before porting.
- Likely classes/files: `DimBlockPos.java`, `Column.AsPosition`, `Row.java`.

### `scene_variables_round_trip_counters_cookies_locations_targets`

- System under test: scene state persistence.
- Behavior protected: counters, cookies, locations, and targets serialize current values correctly.
- Why it matters for Forge/NeoForge migration: future dialogue/chore/prank state depends on these containers.
- Timing: Before release.
- Likely classes/files: `SceneVariables.java`, `scene/data/AbstractVariables.java`, `Counters.java`, `Cookies.java`, `Locations.java`, `Targets.java`.

### `dialogue_nbt_round_trip_preserves_text_speaker_sound_and_responses`

- System under test: dialogue payload contract.
- Behavior protected: `Dialogue.write/read` preserves text, tooltip flag, speaker, sound, and response map.
- Why it matters for Forge/NeoForge migration: `SOpenDialogue` sends this over the network; codec changes must not alter payload semantics.
- Timing: Before porting.
- Likely classes/files: `Dialogue.java`, `Speaker.java`, `SOpenDialogue.java`, `Response.java`.

### `npc_payload_nbt_round_trip_preserves_profile_columns`

- System under test: NPC packet/DB payload.
- Behavior protected: an `NPC` written to NBT and reconstructed keeps the columns used by Yearbook, Cell Phone, and Dialogue.
- Why it matters for Forge/NeoForge migration: protects `SNPCResponse` and `SOpenDialogue` payloads before networking rewrite.
- Timing: Before porting.
- Likely classes/files: `NPC.java`, `Row.java`, `Column.java`, `SNPCResponse.java`, `SOpenDialogue.java`.

### `packet_encode_decode_round_trip_dialogue_response`

- System under test: dialogue packet payload.
- Behavior protected: `CDialogueRespond` preserves NPC ID and selected `Response`.
- Why it matters for Forge/NeoForge migration: payload codecs will change; response buttons must still select the same choice.
- Timing: Before porting.
- Likely classes/files: `CDialogueRespond.java`, `CNPCQuery.java`, `Response.java`, `AbstractMessage.java`.

### `packet_encode_decode_round_trip_controller_packets`

- System under test: Yearbook/Cell Phone packet payloads.
- Behavior protected: `SOpenYearbook`, `SOpenCellPhone`, `SNPCResponse`, and `CNPCRequest` preserve NPC IDs, selected ID, hand, and NBT payloads.
- Why it matters for Forge/NeoForge migration: protects controller screen opening and NPC list navigation.
- Timing: Before porting.
- Likely classes/files: `SOpenController.java`, `SOpenYearbook.java`, `SOpenCellPhone.java`, `SNPCResponse.java`, `CNPCRequest.java`.

### `column_dirty_tracking_current_reference_behavior_is_captured`

- System under test: DB dirty tracking.
- Behavior protected: captures current `Column.set` reference-comparison behavior before changing it.
- Why it matters for Forge/NeoForge migration: dirty tracking drives SQL update generation; changing it blindly can cause DB churn or missed saves.
- Timing: Before bug fixes.
- Likely classes/files: `Column.java`, `Row.java`, `Table.java`.

### `row_noop_update_current_behavior_is_captured`

- System under test: SQLite no-op update.
- Behavior protected: captures current behavior when `Row.update()` has no dirty columns, including any thrown exception.
- Why it matters for Forge/NeoForge migration: this is a known dangerous bug area and can cause hotfix churn after persistence APIs change.
- Timing: Before bug fixes.
- Likely classes/files: `Row.java`, `Table.java`, `Column.java`, `BlockPartyDB.java`.

### `column_position_read_write_matches_sql_column_order`

- System under test: DB position column mapping.
- Behavior protected: `Column.AsPosition` writes dimension, X, Y, Z in the order expected by `Row` index constants.
- Why it matters for Forge/NeoForge migration: position data backs home locations, NPC locations, shrine sorting, and phone lookup.
- Timing: Before porting.
- Likely classes/files: `Column.java`, `Row.java`, `DimBlockPos.java`, `NPC.java`, `Shrine.java`.

### `scenes_own_namespace_remap_is_stable`

- System under test: resource namespace behavior.
- Behavior protected: `Scenes.own` remaps `minecraft` namespace resource locations into `block_party` exactly as current code does.
- Why it matters for Forge/NeoForge migration: data-pack compatibility can silently break if resource IDs are normalized differently.
- Timing: Before porting.
- Likely classes/files: `Scenes.java`, `ISceneAction.java`, `ISceneObservation.java`.

### `block_state_pattern_matching_for_moe_textures`

- System under test: texture lookup.
- Behavior protected: block-state pattern matching accepts matching block/property pairs and rejects mismatches.
- Why it matters for Forge/NeoForge migration: texture lookup is already suspicious; this isolates the intended pattern logic before fixing outer-map keying.
- Timing: Before bug fixes.
- Likely classes/files: `MoeTextures.java`.

### `markov_current_behavior_is_captured_before_decision`

- System under test: unused scene action.
- Behavior protected: captures current `Markov.chain`/selection behavior before either registering, fixing, or deleting it.
- Why it matters for Forge/NeoForge migration: prevents an experimental class from becoming a hidden behavior change.
- Timing: Before release.
- Likely classes/files: `Markov.java`, `SceneActions.java`.

## 3. Manual Golden-World Checks For UI, Rendering, And Sounds

### `golden_world_spawn_talk_hide_reveal_loop`

- System under test: full Moe lifecycle.
- Behavior protected: player can spawn a Moe, see its identity, right-click dialogue, left-click hide, and reveal it again.
- Why it matters for Forge/NeoForge migration: this is the smallest full gameplay loop and catches integration failures automated tests may miss.
- Timing: Before porting.
- Likely classes/files: `CustomSpawnEggItem.java`, `MoeRenderer.java`, `DialogueScreen.java`, `MoeInHiding.java`, `HidingSpots.java`, `test_dialogue.json`, `test_hide.json`.

### `golden_world_visible_moe_render_matrix`

- System under test: rendering/model layers.
- Behavior protected: representative Moes render with correct texture, scale, emotion layer, glow layer, held item, special overlay, name, and health label.
- Why it matters for Forge/NeoForge migration: rendering/model layer APIs are high-risk and texture lookup has known debt.
- Timing: Before porting.
- Likely classes/files: `BlockPartyRenderers.java`, `MoeRenderer.java`, `MoeModel.java`, `EmoteLayer.java`, `GlowLayer.java`, `SpecialLayer.java`, `MoeTextures.java`.

### `golden_world_texture_lookup_override_and_fallback_screenshots`

- System under test: texture lookup.
- Behavior protected: current override and fallback texture behavior is captured visually, including any existing incorrect fallback caused by map-key mismatch.
- Why it matters for Forge/NeoForge migration: prevents confusing "port broke textures" with pre-existing lookup bugs.
- Timing: Before bug fixes.
- Likely classes/files: `MoeTextures.java`, resource files under `src/main/resources/assets/*/textures/moe/`, `data/*/moes/textures` if present.

### `golden_world_dialogue_screen_flow`

- System under test: UI/screens and dialogue packets.
- Behavior protected: `DialogueScreen` opens, renders speaker preview, reveals text, plays sound, shows response controls, advances on response, and closes correctly.
- Why it matters for Forge/NeoForge migration: GUI APIs, button APIs, sound APIs, and packet APIs will all change.
- Timing: Before porting.
- Likely classes/files: `DialogueScreen.java`, `RespondIconButton.java`, `RespondTextButton.java`, `SOpenDialogue.java`, `CDialogueRespond.java`, `Dialogue.java`.

### `golden_world_yearbook_screen_flow`

- System under test: Yearbook UI.
- Behavior protected: Yearbook opens with known NPCs, displays profile stats and entity preview, navigates entries, and behaves correctly when used on owned/unowned Moes.
- Why it matters for Forge/NeoForge migration: protects client screen rendering plus packet/DB payload integration.
- Timing: Before porting.
- Likely classes/files: `YearbookItem.java`, `YearbookScreen.java`, `SOpenYearbook.java`, `CNPCRequest.java`, `SNPCResponse.java`, `NPC.java`.

### `golden_world_cell_phone_screen_and_call_flow`

- System under test: Cell Phone UI and teleport.
- Behavior protected: Cell Phone opens the known NPC list, requests records, calls a Moe, and results in the current teleport/following behavior.
- Why it matters for Forge/NeoForge migration: touches UI, networking, forced chunk loading, teleport APIs, and synced follow state.
- Timing: Before porting.
- Likely classes/files: `CellPhoneItem.java`, `CellPhoneScreen.java`, `SOpenCellPhone.java`, `CNPCTeleport.java`, `CellPhone.java`, `ForcedChunk.java`, `Moe.java`.

### `golden_world_sound_matrix`

- System under test: sounds.
- Behavior protected: speech, hurt, attack, death, step, cat-feature ambient, phone/item, shrine, and dialogue UI sounds play as expected.
- Why it matters for Forge/NeoForge migration: sound event registration, resource reload, and UI sound APIs are port-sensitive.
- Timing: Before release.
- Likely classes/files: `CustomSounds.java`, `MoeSounds.java`, `Moe.java`, `DialogueScreen.java`, item classes under `src/main/java/block_party/items/`, sound resources under `src/main/resources/assets/block_party/sounds/`.

### `golden_world_resource_reload_and_data_pack_override`

- System under test: resource loading.
- Behavior protected: `/reload` or equivalent reload keeps scenes, names, aliases, sounds, and textures active; a tiny custom data pack can override one scene/name/texture/sound.
- Why it matters for Forge/NeoForge migration: reload listener APIs and resource IDs are major port risks.
- Timing: Before porting.
- Likely classes/files: `CustomResources.java`, `Scenes.java`, `Names.java`, `BlockAliases.java`, `MoeSounds.java`, `MoeTextures.java`.

### `golden_world_persistence_stop_start`

- System under test: full persistence.
- Behavior protected: after stopping and restarting, visible Moes, hidden Moes, known NPC list, names, DB rows, inventory, home fields, shrine/garden/location records, and block state identity still match baseline.
- Why it matters for Forge/NeoForge migration: the port must not lose companion identity or old worlds.
- Timing: Before porting.
- Likely classes/files: `BlockPartyDB.java`, `HidingSpots.java`, `NPC.java`, `Layer1.java` through `Layer7.java`, `AbstractDataBlockEntity.java`, `Shrine.java`, `Garden.java`, `Location.java`.

### `golden_world_shrine_garden_location_flow`

- System under test: favorite locations and shrine/garden/location records.
- Behavior protected: claimed location blocks remember player ownership, shrine list sync works, shrine visual/sound effect works, and any current return/respawn behavior is documented.
- Why it matters for Forge/NeoForge migration: location persistence is partially implemented and easy to regress silently.
- Timing: Before release.
- Likely classes/files: `AbstractDataBlockEntity.java`, `LocativeBlockEntity.java`, `GardenLanternBlockEntity.java`, `ShrineTabletBlockEntity.java`, `SShrineList.java`, `ShrineLocation.java`, `JapanRenderer.java`.

### `golden_world_transparent_block_rendering`

- System under test: block rendering.
- Behavior protected: current cutout/cutout-mipped behavior for lanterns, screens, saplings, vines, leaves, and other transparent/decorative blocks is captured.
- Why it matters for Forge/NeoForge migration: `CustomBlocks.registerRenderTypes` is commented out, so current rendering may already be odd; capture it before fixing.
- Timing: Before bug fixes.
- Likely classes/files: `CustomBlocks.java`, block model/state resources under `src/main/resources/assets/block_party/`, client renderer setup.

### `golden_world_chore_prank_adventure_absence_or_presence`

- System under test: unfinished companion systems.
- Behavior protected: documents whether chores, pranks, and adventuring are currently absent, partial, or discoverable through manual play.
- Why it matters for Forge/NeoForge migration: prevents planned features from being accidentally treated as migration regressions.
- Timing: Before release.
- Likely classes/files: `Layer3.java`, `Layer4.java`, `Layer6.java`, `Layer7.java`, `CellPhone.java`, `ForcedChunk.java`, README behavior expectations.

## Recommended Execution Order

1. Create the JUnit contract tests for parsing, payloads, and DB no-op behavior.
2. Create the core GameTests for spawn lifecycle, hide/reveal, persistence, and dialogue response packets.
3. Build the golden world and capture screenshots/logs/DB schema.
4. Lock in resolved dangerous-bug behavior: combat attacks do not recurse, and hidden Moe reload preserves `HideUntil`.
5. Baseline remaining known dangerous bugs: texture lookup, no-op SQL update, hidden spot null behavior.
6. Start NeoForge migration only after the pre-port GameTests and golden-world checks are usable.
7. Before release, add remaining manual and GameTest coverage for Yearbook, Cell Phone, resource overrides, shrine/location flows, sounds, and unfinished-system expectations.
