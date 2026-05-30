# Technical Debt Register

This file tracks unresolved debt only. Resolved migration notes, completed test backlogs, and historical Forge-port tasks belong in git history, compatibility notes, or focused architecture docs.

Risk scale:

- `Maintenance risk`: how likely this is to make future changes harder, riskier, or slower.
- `Gameplay importance`: how visible or damaging this is to player-facing behavior.
- `Owner surface`: the package or document that should absorb the eventual fix.

## Current Priorities

### Keep classes thin and package boundaries visible

- Evidence: `Moe` and several service-style classes are large coordination points. The AI goal, chore, social-behavior, environmental movement, idle routine, gift-memory, environmental-memory, attention-definition, and block-profile slices now have visible package ownership, but `Moe` still exposes a broad identity, persistence, interaction, and inventory surface.
- Maintenance risk: High. The codebase is interconnected enough that fat files make ownership, review, and regression analysis harder.
- Gameplay importance: Medium. This is mostly a maintainability risk, but mistakes in central classes affect core gameplay.
- Owner surface: `entities`, `entities.movement`, `entities.social`, `entities.environment`, `entities.preferences`, `entities.chores`, `world`, and `scene`.
- Direction: prefer small domain services and value objects. Keep entity methods as state accessors or orchestration entry points; put scanning, scoring, planning, lifecycle, and persistence helpers in named collaborators. The next cleanup slices should shrink `Moe` by moving cohesive non-movement clusters such as inventory/menu interaction or persistence serialization.

### Keep cleanup guardrails active

- Evidence: recent cleanup reduced inline fully qualified class names and consolidated table/NBT constants, but these are easy regressions to reintroduce during routine feature work.
- Phase 1 baseline: as of 2026-05-29, `src/main/java` has 223 inline fully qualified class-name tokens after excluding `package`/`import` lines and string literals. Breakdown: `java.util` 125, `net.minecraft` 52, `block_party` 44, `java.sql` 2. Largest files by count are `BlockPartyDB.java` 54, `Moe.java` 32, `CustomBlocks.java` 11, `MoeLifecycleGameTests.java` 11, and `DecorativeContentGameTests.java` 10.
- Phase 1 cleanup measurement: as of 2026-05-29, the same scan reports 30 inline fully qualified class-name tokens.
- Guardrail: `./gradlew phase1Compliance` is wired into `check` and fails if inline FQCN usage rises above the Phase 1 budget or raw SQL table names are introduced in source strings.
- Maintenance risk: Medium. Fully qualified names obscure the actual dependency graph, and duplicated persistence identifiers make world compatibility harder to reason about.
- Gameplay importance: Low.
- Owner surface: `build.gradle`, all Java packages, and persistence-facing docs.
- Direction: keep `phase1Compliance` passing. Use imports for normal dependencies. Keep fully qualified names only where they intentionally disambiguate same-named classes or avoid an import collision that would reduce readability. Use constants for table names and repeated NBT keys.

### Scene/datapack authoring diagnostics need a better content-author path

- Evidence: `SCENE_DATAPACK_SCHEMA.md` documents the active scene surface, but there is no machine-readable JSON Schema and some malformed non-scene content still fails at reload/runtime rather than giving author-oriented diagnostics. Scene actions now fail parsing for unknown action IDs and malformed action payloads.
- Maintenance risk: Medium. Generated scene packs and third-party packs need predictable validation.
- Gameplay importance: Medium. Bad scene data can silently disable content or produce hard-to-debug behavior.
- Owner surface: `registry.resources`, `scene`, `scene.actions`, `SceneObservationFactories`, and `docs/SCENE_DATAPACK_SCHEMA.md`.
- Direction: add a schema or validator tests for scene, social-affinity, names, aliases, sounds, and texture metadata. Unknown filters should keep failing closed.

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
- Direction: defer explicit versioning until release prep, before public save compatibility matters. Keep this visible as a release-track item; in the meantime, document table ownership and keep no-op update/round-trip tests in place.

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
