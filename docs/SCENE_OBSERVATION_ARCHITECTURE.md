# Scene Observation Architecture

Scene filters are product vocabulary, not just implementation residue. The old
Forge branch exposed a broad filter surface for NPC state, player state, target
state, cookies, counters, held items, block identity, names, weather, moon phase,
and time of day. The active NeoForge branch should keep that vocabulary
recognizable while only enabling filters whose backing state exists.

## Active Filters

These filters are implemented and should be treated as supported behavior:

- Simple Moe state: `always`, `never`, `is_corporeal`, `is_ethereal`.
- World state: `if_raining`, `if_sunny`, moon phase filters, and time-of-day
  filters including `if_time`.
- Moe profile state: blood type, dere, emotion, and gender filters.
- Numeric Moe state: `health`, `food_level`, `loyalty`, and `stress`.
- Scene variables keyed by Moe database ID: `counter` and `has_cookie`.
- Runtime identity checks: `self`, `block`, `held_item`, `player_held_item`,
  and `name`.

`SceneObservationFactories` is the active factory boundary. It keeps parsing
logic out of the reload listener and makes unsupported filters visibly fail
closed instead of silently becoming `always`.

## Deferred Scaffolding

These IDs remain registered because old data and future scene content may refer
to them, but they do not currently have enough active backing state to be safe:

- `player_counter`
- `player_has_cookie`
- `family_name`

The old Forge code had player-scoped scene variable helpers and a family-name
surface on the base NPC abstraction. The current NeoForge build stores scene
variables by Moe database ID and has no active `BlockPartyNPC`/Senpai base
surface. Until those systems are restored intentionally, these filters must fail
closed so scenes do not fire under false assumptions.

## Over-Pruning Guardrail

Unknown filters also fail closed. This is deliberate: a missing or unported scene
filter should disable that scene, not make it easier to trigger. That preserves
player-facing safety while keeping unfinished product hooks visible for a later
NPC/scenes pass.
