# Scene Observation Architecture

Scene filters are product vocabulary, not just implementation residue. The old
Forge branch exposed a broad filter surface for NPC state, player state, target
state, cookies, counters, held items, block identity, names, weather, moon phase,
and time of day. The active NeoForge branch should keep that vocabulary
recognizable while only enabling filters whose backing state exists.

## Active Filters

These filters are implemented and should be treated as supported behavior:

- Simple Moe state: `always`, `never`, `is_corporeal`, `is_cardinal`.
- World state: `if_raining`, `if_sunny`, moon phase filters, and time-of-day
  filters including `if_time`.
- Moe profile state: blood type, dere, emotion, and gender filters.
- Numeric Moe state: `health`, `food_level`, `loyalty`, and `stress`.
- Scene variables scoped to Moe, player, or world state: `counter`,
  `has_cookie`, `player_counter`, `player_has_cookie`, `world_counter`, and
  `world_has_cookie`.
- Runtime identity checks: `self`, `block`, `held_item`, `player_held_item`,
  `name`, and `family_name`.
- Target relationship and social state: target affection/loyalty/trust/stress,
  social target identity, social affinity/tension/interest, social visual, and
  social reaction filters.
- Attention, follow, anchor, remembered-place, environmental observation,
  social-place, gift-memory, and held-item preference filters.

`SceneObservationFactories` is the active factory boundary. It keeps parsing
logic out of the reload listener and makes unsupported filters visibly fail
closed instead of silently becoming `always`.

## Active Restored Scaffolding

`family_name` is active again. It matches the localized Moe family name derived
from the source block profile, such as `Suzu` for a bell Moe. This matters for
old-style block-family scenes and for generated content that wants to address a
block family without relying on one concrete block ID.

## Over-Pruning Guardrail

Unknown filters also fail closed. This is deliberate: a missing or unported scene
filter should disable that scene, not make it easier to trigger. That preserves
player-facing safety while keeping unfinished product hooks visible for a later
NPC/scenes pass.

Registry-backed item, block, and entity references are stricter than generic
unknown filters. A scene that references an unknown item, block, or entity is
rejected during reload after logging a validation issue, because those references
would otherwise be able to throw when the scene is evaluated.
