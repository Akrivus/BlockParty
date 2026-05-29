# Technical Debt Register

This file tracks unresolved debt only. Resolved migration notes, completed test backlogs, and historical Forge-port tasks belong in git history, compatibility notes, or focused architecture docs.

Risk scale:

- `Maintenance risk`: how likely this is to make future changes harder, riskier, or slower.
- `Gameplay importance`: how visible or damaging this is to player-facing behavior.
- `Owner surface`: the package or document that should absorb the eventual fix.

## Current Priorities

### Keep classes thin and package boundaries visible

- Evidence: `Moe` and several service-style classes are large coordination points. New content systems can easily add another method to an already broad class instead of creating a focused collaborator.
- Maintenance risk: High. The codebase is interconnected enough that fat files make ownership, review, and regression analysis harder.
- Gameplay importance: Medium. This is mostly a maintainability risk, but mistakes in central classes affect core gameplay.
- Owner surface: `entities`, `entities.movement`, `entities.social`, `entities.environment`, `entities.preferences`, `entities.chores`, `world`, and `scene`.
- Direction: prefer small domain services and value objects. Keep entity methods as state accessors or orchestration entry points; put scanning, scoring, planning, and persistence helpers in named collaborators.

### Avoid fully qualified class names in implementation code

- Evidence: recent work has exposed places where code uses entire package names inline instead of imports or smaller adapter types.
- Maintenance risk: Medium. Fully qualified names obscure the actual dependency graph and make file-level structure harder to scan.
- Gameplay importance: Low.
- Owner surface: all Java packages.
- Direction: use imports for normal dependencies. Keep fully qualified names only where they intentionally disambiguate same-named classes or avoid an import collision that would reduce readability.

### Scene/datapack authoring diagnostics need a better content-author path

- Evidence: `SCENE_DATAPACK_SCHEMA.md` documents the active scene surface, but there is no machine-readable JSON Schema and some malformed content still fails at reload/runtime rather than giving author-oriented diagnostics.
- Maintenance risk: Medium. Generated scene packs and third-party packs need predictable validation.
- Gameplay importance: Medium. Bad scene data can silently disable content or produce hard-to-debug behavior.
- Owner surface: `registry.resources`, `scene`, `scene.actions`, `SceneObservationFactories`, and `docs/SCENE_DATAPACK_SCHEMA.md`.
- Direction: add a schema or validator tests for scene, social-affinity, names, aliases, sounds, and texture metadata. Unknown filters should keep failing closed; unknown actions should be explicitly documented and tested.

### Scene action registry and parser can drift

- Evidence: `ScenesReloadListener` parses actions such as `open_inventory`, `give_item`, `take_item`, `wait`, and `dismiss`; the registry-facing `SceneActions` list is a separate declaration.
- Maintenance risk: Medium. Scene-pack authors and generated packs need one authoritative list of legal action IDs.
- Gameplay importance: Medium.
- Owner surface: `SceneActions`, `ScenesReloadListener`, `SCENE_DATAPACK_SCHEMA.md`, scene parser tests.
- Direction: derive the documented/action registry list from one source or add a regression test that fails when parser-supported IDs and registered/documented IDs diverge.

### Scene state scope is per-Moe, not a full authoring model yet

- Evidence: cookies/counters are per Moe database ID. Player/global progression and shared pack-level state are not first-class authoring surfaces.
- Maintenance risk: Medium. Authors may try to model story progression through ad hoc Java additions or fragile JSON workarounds.
- Gameplay importance: Medium to high for content-forward features.
- Owner surface: `scene.data`, `BlockPartyDB`, `SCENE_DATAPACK_SCHEMA.md`.
- Direction: decide whether player/global state is needed before adding more one-off filters. If added, give it explicit ownership, serialization, and tests.

### Profile and social signal generation is still spread across multiple surfaces

- Evidence: names, traits, block tags, social affinity resources, NPC rows, and spawn defaults all participate in Moe identity.
- Maintenance risk: Medium. New content can accidentally change identity generation or make old rows inconsistent.
- Gameplay importance: High. Moe identity is core player-facing behavior.
- Owner surface: `entities.Moe`, `db.records.NPC`, `registry.resources`, `scene.traits`, `entities.social`.
- Direction: keep tests around row round-trips, tag-derived traits, social-affinity matching, and old-world defaults. Consolidate only when the replacement surface is clearer than the current split.

### Persistence APIs need explicit schema/version boundaries

- Evidence: SQLite rows, world `SavedData`, entity NBT, and controller payloads all preserve parts of Moe identity and state. Long-term world compatibility needs a documented migration/version story.
- Maintenance risk: Medium.
- Gameplay importance: High. Losing rows, owner lists, hidden positions, or entity identity damages worlds.
- Owner surface: `db`, `entities.data`, `entities.Moe`, `MoeInHiding`, `network.payload`.
- Direction: document table ownership, add schema versioning before incompatible changes, and keep no-op update/round-trip tests in place.

### Client visual parity remains partly manual

- Evidence: renderer, screen, particle, skybox, armor, transparent block, and texture-path behavior cannot be fully validated by server GameTests.
- Maintenance risk: Medium.
- Gameplay importance: High for release polish.
- Owner surface: `client`, `registry.resources`, `docs/RELEASE_CHECKLIST.md`.
- Direction: keep manual screenshot checks in the release checklist until there is an automated client visual harness.

### Planned behavior should enter through small primitives

- Evidence: chores, pranks, adventuring, place memory, attention, social behavior, and scene packs are now content-forward areas. Adding each mechanic directly to `Moe` would make the central class harder to maintain.
- Maintenance risk: High.
- Gameplay importance: High for the project direction.
- Owner surface: `entities.chores`, `entities.environment`, `entities.movement`, `entities.social`, `world`, `scene`.
- Direction: add Java primitives only when JSON cannot express the behavior. Each primitive should have a narrow API, GameTests, and an author-facing hook when it is intended for scene packs.

## Cleanup Rules

- Remove entries from this file when the code is fixed and covered by tests or documentation.
- Do not keep "resolved" sections here. Put compatibility decisions in `COMPATIBILITY_NOTES.md`.
- Do not use this file as a regression backlog. Test coverage strategy lives in `TESTING_STRATEGY.md`; content authoring contracts live in `SCENE_DATAPACK_SCHEMA.md`.
- Prefer package-level cleanup issues over one-line TODO inventory. The goal is to improve ownership boundaries, not maintain a second task tracker.
