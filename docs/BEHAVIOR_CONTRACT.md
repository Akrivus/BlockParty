# Block Party Behavior Contract

This document defines player-facing gameplay behavior that should not change during maintenance or feature work. It intentionally avoids implementation details except where needed to clarify what a player can observe. If current behavior is incomplete or uncertain, the contract says so explicitly.

## Scope

Changes should preserve the current observable behavior first. New features, balance changes, bug fixes, and redesigned interactions should happen after the relevant behavior is covered.

Status labels:

- `Implemented`: observable in the current codebase.
- `Partially implemented`: some player-facing pieces exist, but the full promised behavior is not visible yet.
- `Intended`: described by the README or data model, but not confirmed as current gameplay.
- `Uncertain`: static analysis could not prove the exact expected player result.

## Core Fantasy

`Implemented / Intended`

Block Party turns Minecraft blocks into companion NPCs called Moes. A Moe is not a generic pet: it represents the block it came from. The block origin should remain visible through its name, texture, scale, traits, sounds, and behavior hooks.

The player-facing fantasy to preserve:

- Each valid block can become a distinct block-person companion.
- Moes are affectionate companions, not disposable mobs.
- Moes remember who claimed them.
- Moes can be interacted with through dialogue and companion-management UI.
- Moes can retreat back into the world as blocks and later return.
- Over time, Moes are expected to support affection, food, pranks, chores, and adventuring behaviors.

## Block-Origin Rules

`Implemented`

Only blocks that the mod considers valid Moe origins should become Moes. Using the Moe spawn egg on an invalid block should fail and tell the player that the chosen block cannot be spawned as a Moe.

When a player successfully creates a Moe from a valid block:

- The clicked block is consumed from the world without dropping itself.
- A Moe appears adjacent to the face the player clicked.
- The Moe visibly represents the consumed block.
- The Moe's family name should be based on the source block's localized name.
- The Moe's texture should come from the source block's Moe texture when available, with a sensible fallback path.
- The Moe's size should reflect the source block's physical volume unless that block is configured to ignore volume.
- The Moe should inherit special block traits such as glow, wings, cat features, pronoun tags, dere tags, blood-type tags, and zodiac tags when those tags apply.
- If the source block had persistent block-entity data, that data should be preserved through the Moe's lifecycle where current behavior supports it.

`Uncertain`

The current behavior around blocks with complex block states should be preserved until tested. Changes should not intentionally simplify all source blocks to default states unless tests show that was already what players saw.

## Ownership And Identity

`Implemented`

When a player creates or claims a Moe:

- The Moe belongs to that player.
- The Moe is added to that player's NPC list.
- The Yearbook and Cell Phone should show only NPC records available to that player.
- Player-owned interaction should matter: right-click, left-click, shift-right-click, and shift-left-click are meaningful owner interactions.

Moes should not despawn merely because players move far away.

## Personality Generation

`Implemented / Partially implemented`

Each Moe should have a generated profile that is visible through names, pronouns, stats, dialogue substitutions, UI, sound/visual presentation, and later behavior.

The generated profile includes:

- given name
- family name from block origin
- gender and honorific
- pronouns
- blood type
- dere/personality type
- zodiac sign
- emotion
- health
- food, exhaustion, saturation
- stress and relaxation
- loyalty and affection
- age

Name behavior to preserve:

- Given names should come from the mod's loaded name lists.
- Names should be unique against the mod's claimed-name list when possible.
- If names run out, current fallback behavior should be preserved until intentionally redesigned.

Trait behavior to preserve:

- Block tags can force or influence gender, blood type, dere type, and zodiac sign.
- If no tag forces a trait, the current default/random behavior should remain stable enough that the same categories still exist.
- Cat-feature Moes should use cat-like ambient sound behavior.
- Winged Moes should be treated as flying companions where current behavior supports it.
- Glowing blocks should produce the appropriate visible glow layer.

`Uncertain`

The exact order in which constructor defaults, block tags, randomization, and spawn finalization win should be captured in parity tests before a port changes it.

## Normal Presence

`Implemented / Partially implemented`

A visible Moe should behave like a persistent companion entity:

- It has a name tag visible nearby.
- It displays health above or near its name when rendered nearby.
- It can hold items and render held/head/special overlays.
- Its expressive face and eyes should render according to its current emotion.
- It should animate on the client.
- It should make block/personality-appropriate sounds for speech, hurt, attack, death, steps, and other sound categories.
- It should keep an inventory accessible through its current menu behavior.

`Partially implemented`

Needs such as hunger, loneliness, stress, action choice, and sleep have saved fields and update hooks, but current player-facing behavior is not fully implemented. Changes should preserve the fields and any visible UI/stat behavior without inventing new needs behavior incidentally.

## Dialogue Behavior

`Implemented`

Moes communicate through data-driven scenes.

Player-facing rules:

- Owner interactions can trigger scenes.
- Right-click should be able to open dialogue when matching scene data exists.
- Left-click or shift interactions can trigger different scenes when matching scene data exists.
- Looking at a Moe and random server ticks can also trigger scenes when data provides matching content.
- Dialogue appears in a custom dialogue screen with the speaker shown unless the speaker is the narrator.
- Dialogue text can advance character by character.
- Dialogue can play speech sounds.
- Responses can appear as icons or text depending on scene configuration.
- Choosing a response sends that choice back to the server and advances the scene.
- Closing dialogue should close the visible dialogue UI and leave the game unpaused.

Data-pack behavior to preserve:

- Scenes are loaded from data resources.
- Each scene has a trigger, filters, and actions.
- A scene should run only when its filters pass.
- If multiple scenes match, the current random selection behavior should be preserved.
- Scene responses can chain into more dialogue or actions.
- Dialogue substitutions such as Moe name/profile substitutions should continue to work.

`Uncertain`

If a new high-priority scene interrupts an active scene, preserve current interruption behavior first, then review later. Do not silently change scene priority rules during unrelated work.

## Hiding And Retreat Behavior

`Implemented`

A Moe can retreat into its block form. This is one of the most important behaviors to preserve.

When a Moe hides:

- The Moe disappears as a visible NPC.
- The original block appears at the Moe's position.
- The hidden Moe remains associated with that block position.
- The hidden state is tracked as part of the Moe's persistent record.
- The block is not just an ordinary block from the player's perspective; interacting with or disturbing it can reveal the Moe.

When a hidden Moe is revealed:

- The block at the hiding position is removed.
- The same Moe record is restored into a visible Moe entity.
- The Moe should keep its owner, name, block identity, traits, stats, and other persistent profile data.
- If block-entity data was preserved, it should be restored into the Moe lifecycle as current behavior allows.
- A hiding-discovered scene can trigger when the Moe returns.

Player/world actions that should reveal or affect hidden Moes:

- breaking or starting to break the hiding block
- block break completion
- piston movement affecting the hiding block
- falling-block conversion involving the hiding block
- hide timers expiring, for timed hide modes
- the hiding block becoming air

If the hiding block is gone and the hidden Moe is hurt/exposed, current behavior can mark the Moe dead. Preserve this until a separate design decision changes it.

`Uncertain`

Hide timers across save/load need parity testing. If current behavior is buggy, document the baseline before fixing it.

## Favorite Locations, Homes, Gardens, And Shrines

`Partially implemented / Intended`

The README says the database saves favorite locations so Moes can always return or respawn there. The current codebase exposes several location concepts: home position, shrine records, garden lantern records, generic locative block records, and shrine-list sync to clients.

Player-facing contract:

- Claimed location blocks should remember the player who claimed them.
- Location records should survive world save/load.
- Destroying a claimed location block should remove its persistent location record where current behavior supports deletion.
- Shrine locations should be available to client-side systems after login.
- Shrine-related visuals or skybox behavior should continue to find nearby shrine positions when the current game state allows it.
- Moes should preserve their home/favorite-location fields across save/load.
- Any current return/respawn behavior tied to favorite locations should remain unchanged.

`Uncertain`

I did not confirm a complete player-facing workflow where a Moe chooses, returns to, or respawns at a favorite location. Treat location persistence as a contract to preserve, but do not add new return/respawn behavior without dedicated tests.

## Yearbook And Cell Phone

`Implemented / Partially implemented`

The Yearbook and Cell Phone are companion-management tools.

Yearbook behavior:

- Using a Yearbook should open the player's known NPC list if the player has known NPCs.
- Using a Yearbook on one of the player's Moes should open that Moe's entry.
- Using a Yearbook on someone else's Moe should not claim or expose private control of it.
- The Yearbook entry should show the Moe's current profile information and preview entity according to current UI behavior.

Cell Phone behavior:

- Using a Cell Phone should open the player's known NPC list.
- Calling a listed Moe should attempt to find that Moe and teleport it near the player.
- After a phone teleport, the Moe should be set to following mode according to current behavior.
- The phone call should respect persistent NPC records and forced chunk loading behavior enough to find an existing Moe when current behavior supports it.

`Uncertain`

If a listed Moe cannot be found, current failure behavior should be preserved and documented during manual testing.

## Chores

`Intended`

The README says Moes perform chores depending on the block they personify. I did not find confirmed current chore gameplay.

Current NeoForge contract:

- Do not remove profile stats, inventory behavior, block tags, sounds, or scene hooks that future chore behavior may depend on.
- Do not introduce new chore outcomes during current-version stabilization unless they already existed in testable gameplay.
- If any chore behavior is discovered in manual testing, record it as a parity requirement before future version moves.

Expected future player-facing shape:

- Chores should feel tied to the Moe's block origin.
- Chores should be companion-like and helpful, not destructive unless explicitly prank-related.
- Chores should respect ownership and persistence.

## Pranks

`Intended`

The README says Moes pull pranks. I did not find confirmed current prank gameplay.

Current NeoForge contract:

- Preserve prank-related stats, sounds, dialogue hooks, and personality categories.
- Do not add new world-changing prank behavior during current NeoForge stabilization.
- If current data packs or manual play reveal prank scenes, preserve their triggers, dialogue, and outcomes exactly.

Expected future player-facing shape:

- Pranks should be personality/block-origin flavored.
- Pranks should be observable to the player through dialogue, sound, emotion, item/world interaction, or UI state.
- Pranks should persist any resulting relationship/stat changes.

## Adventuring And Following

`Partially implemented / Intended`

The README says Moes can tag along with players on adventures. Current observable support includes ownership, a following flag, phone teleport, combat stats, health, attack/hurt/death sounds, inventory, and persistence.

Port contract:

- Calling a Moe to the player should continue to place it near the player and mark it as following when current behavior succeeds.
- Moes should not despawn from distance.
- Moes should preserve health and inventory across saves.
- Moes should continue to have combat-relevant attributes and sounds.
- Any current follow/adventure behavior found through manual testing should be preserved before adding richer following AI.

`Uncertain`

I did not find a complete visible follow AI loop. Do not treat richer adventuring behavior as part of the current NeoForge baseline unless parity testing proves it already works.

## Persistence

`Implemented`

Persistence is player-facing because Moes are companions. During a port, losing identity or ownership is a behavior regression.

Must persist:

- player ownership
- player's known NPC list
- claimed names
- Moe name and block-origin family identity
- source block state
- source block-entity persistent data where current behavior supports it
- gender, pronouns, honorific, blood type, dere type, zodiac, emotion
- health, food, exhaustion, saturation, stress, relaxation, loyalty, affection, slouch, age
- inventory contents
- home/favorite-location fields
- hidden-vs-visible state
- hidden block position
- dead/removed state where current behavior marks it
- shrine, garden, and location records where current gameplay creates them

Persistence must survive:

- normal save/load
- server stop/start
- chunk unload/reload
- dimension changes where current behavior supports them
- phone lookup/call behavior
- hidden Moe reveal after reload, subject to current baseline behavior

The SQLite database filename/location and world saved data should remain compatible unless a migration is intentionally written and tested.

## Death And Removal

`Implemented / Uncertain`

Current player-facing removal behavior is conservative:

- Moes should not vanish just because no player is nearby.
- A visible corporeal Moe that dies may retreat/hide rather than simply disappearing.
- A hidden Moe whose hiding block is gone can be marked dead.
- A player can remove dead or estranged NPCs from their visible list through current UI behavior.

`Uncertain`

The full hard-delete behavior for NPC records is not clearly visible. Changes should not delete records more aggressively than current behavior.

## Rendering, Sounds, And Seasonal Behavior

`Implemented`

Visual and audio identity are part of the gameplay contract:

- Moes render as character models with block-origin textures.
- Some blocks have special overlays.
- Glow-capable block Moes should glow.
- Emotion layers should show the current emotion.
- Moes should blink/look expressive according to current animation/model behavior.
- Nearby name/health rendering should remain available.
- Sounds should use block-specific overrides when provided and default Moe sounds otherwise.
- Christmas texture variants should still apply for tagged festive blocks during the current Christmas date window.
- Halloween/Christmas helper behavior should not be removed even if only some seasonal content currently uses it.

## Data Pack And Resource Behavior

`Implemented`

The game must preserve data-driven customization:

- block tags determine which blocks spawn Moes and which traits/features they get.
- names load from data resources.
- Moe textures load from data resources.
- Moe sounds load from data resources.
- block aliases load from data resources.
- dialogue scenes load from data resources.
- scene filters and actions remain addressable by the same resource IDs.

Existing worlds and data packs should not need renamed IDs unless a deliberate migration map is provided.

## Non-Goals During Port

When changing these systems, do not intentionally:

- redesign Moe personalities
- rebalance stats
- make invalid blocks spawnable
- change owner privacy
- replace the dialogue system with a different interaction model
- add new chore, prank, or adventure mechanics
- delete old NPC records automatically
- change registry/resource IDs
- change persistent database semantics
- simplify hiding into ordinary block placement

Block Party gameplay should continue to feel the same, including the currently unfinished edges. Improvements can come afterward with their own tests and design notes.
