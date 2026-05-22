# NeoForge Target Decision

This document compares practical first-port targets for moving Block Party from Forge 1.19.4 to NeoForge. It is a decision record, not an implementation plan. Do not treat any API names here as final imports until the exact NeoForge build is pinned.

Current baseline:

- Minecraft/Forge: `net.minecraftforge:forge:1.19.4-45.2.0`
- Java toolchain: Java 17
- Build: ForgeGradle `5.1.+`
- High-risk Block Party systems: custom packets/screens, `SavedData` + SQLite, GameTests, resource reload listeners, custom/deferred registries, client render/model/particle registration.

References checked:

- NeoForge Java guidance: https://docs.neoforged.net/user/docs/
- NeoForge 1.20.6 networking: https://docs.neoforged.net/docs/1.20.6/networking/payload/
- NeoForge 1.21.4 getting started: https://docs.neoforged.net/docs/1.21.4/gettingstarted/
- NeoForge 1.21.4 networking: https://docs.neoforged.net/docs/1.21.4/networking/payload/
- NeoForge 1.21.4 SavedData: https://docs.neoforged.net/docs/1.21.4/datastorage/saveddata/
- NeoForge 1.21.4 GameTests: https://docs.neoforged.net/docs/1.21.4/misc/gametest/
- NeoForge 1.21.4 to 1.21.5 migration primer: https://docs.neoforged.net/primer/docs/1.21.5/
- Latest NeoForge docs, currently 26.1: https://docs.neoforged.net/docs/gettingstarted/

## Summary Matrix

| Target | Java/toolchain | Networking | SavedData | GameTest | Reload APIs | Ecosystem relevance | Migration difficulty | Maintenance risk |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| NeoForge 1.20.6 | Java 21; first jump from Java 17 | Modern payload API exists, but earlier shape | `SavedData.Factory` era | Familiar annotation path | Closer to older events | Low to medium; short-lived MC line | Medium | High |
| NeoForge 1.21.4 | Java 21 | Payload API mature enough and documented | `SavedData.Factory`, not yet `SavedDataType` | Familiar `GameTestHolder` / `RegisterGameTestsEvent` path | Still recognizable; some renamed APIs possible | Medium; stronger than 1.20.6, less than latest | Medium-high | Medium |
| NeoForge 1.21.5 / latest practical | Java 21 for 1.21.5; latest 26.1 docs require Java 25 | Newer split client payload registration and continued churn | 1.21.5 reworks SavedData into `SavedDataType` | 1.21.5 changes GameTest internals substantially | Newer reload listener names and sorted listener APIs | Highest if using latest, uneven for 1.21.5 | High to very high | Medium-low for latest, high for 1.21.5 as a stopover |

## Option: NeoForge 1.20.6

### Java Version And Toolchain

NeoForge/Minecraft 1.20.5+ requires Java 21. That means the project must leave Java 17 either way. Moving only to 1.20.6 does not avoid the toolchain upgrade.

### Networking API Stability

1.20.6 already uses the NeoForge payload system through `RegisterPayloadHandlersEvent`, `PayloadRegistrar`, and `CustomPacketPayload`. This is useful because Block Party must replace Forge `SimpleChannel` regardless.

The downside is that 1.20.6 is an earlier form of the modern networking API. It gets Block Party off `SimpleChannel`, but does not buy much long-term stability compared with targeting a later 1.21 API directly.

### SavedData API

1.20.6 is likely closer to the 1.21.4 `SavedData.Factory` model than to latest. That helps `BlockPartyDB`, `HidingSpots`, and `SceneVariables` because their current 1.19.4 design maps cleanly to named `DimensionDataStorage` entries.

### GameTest Support

GameTest support should be familiar enough for the current suite. The current Forge GameTests already use vanilla `GameTest` plus Forge registration annotations, so a 1.20.6 port should mostly be package and run-config migration.

### Resource Reload APIs

Resource reload listeners are closer to the older Forge shape than latest. This lowers first-pass migration risk for `CustomResources`, `BlockAliases`, `MoeSounds`, `MoeTextures`, `Names`, and `Scenes`.

### Modpack Ecosystem Relevance

Weak. Minecraft 1.20.6 was a short-lived bridge release. It is useful as a technical stepping stone but not attractive as the first public NeoForge target.

### Migration Difficulty From Forge 1.19.4

Medium. This is the smallest conceptual jump among the three options, but still requires Java 21, NeoForge build tooling, payload networking, package moves, registry holder changes, and many Minecraft API updates.

### Long-Term Maintenance Risk

High. A successful 1.20.6 port would likely need another port soon. That creates duplicate churn in exactly the systems Block Party most needs to stabilize: networking, GameTests, reload listeners, and persistence.

## Option: NeoForge 1.21.4

### Java Version And Toolchain

NeoForge 1.21.4 uses Java 21. This is the same unavoidable toolchain jump as 1.20.6, but without the current latest line's Java 25 requirement.

### Networking API Stability

1.21.4 has documented custom payload networking with `RegisterPayloadHandlersEvent`, `PayloadRegistrar`, `StreamCodec`, `IPayloadContext`, and `PacketDistributor`. It is a strong enough target for replacing Block Party's Forge `SimpleChannel` implementation.

Compared with latest docs, 1.21.4 appears slightly less split between common and client-only handler registration, which may make the first port easier. The code should still be structured as if client packet handlers are client-only, so a later forward-port is not painful.

### SavedData API

This is the biggest practical advantage. NeoForge 1.21.4 still documents `SavedData` subclasses with `SavedData.Factory` and `DimensionDataStorage#computeIfAbsent(factory, name)`. That is a direct conceptual migration from the current `computeIfAbsent(load, create, KEY)` usage.

Block Party has three important saved-data classes and SQLite lifecycle hooks. Avoiding the 1.21.5 `SavedDataType` rewrite during the first port reduces risk.

### GameTest Support

1.21.4 supports `@GameTestHolder` and `RegisterGameTestsEvent`, matching the current style closely. The existing GameTest suite can remain the main safety net while production APIs are migrated.

### Resource Reload APIs

1.21.4 is new enough to expose NeoForge reload listener behavior but old enough that the reload model is still recognizable. Some event names may already differ from Forge 1.19.4, but this should be a normal port rather than a redesign.

### Modpack Ecosystem Relevance

Moderate. It is more relevant than 1.20.6 and close enough to the 1.21 ecosystem to be useful for compatibility work. It is not the strongest long-term public target, and the docs now mark 1.21.4 as no longer actively maintained, but it is a good first parity target.

### Migration Difficulty From Forge 1.19.4

Medium-high. There is real Minecraft API churn from 1.19.4 to 1.21.4, but it avoids the extra 1.21.5 persistence and GameTest rewrites. For Block Party's current code, this looks like the best balance of modernity and survivability.

### Long-Term Maintenance Risk

Medium. 1.21.4 should not be treated as the final forever branch. It is a sensible first NeoForge port target, then a forward-port target can be chosen after Block Party has passing parity tests on NeoForge.

## Option: NeoForge 1.21.5 / Latest Practical Target

### Java Version And Toolchain

NeoForge 1.21.5 remains in the Java 21 era, but the current latest docs are for NeoForge 26.1 and require Java 25. Jumping directly to latest would mean upgrading from Java 17 to Java 25, not merely Java 21.

That is a large infrastructure jump for a project that still needs to preserve old world data, SQLite shading, GameTests, and client rendering behavior.

### Networking API Stability

Latest networking docs show the same broad payload model but with a clearer split: common payload registration through `RegisterPayloadHandlersEvent`, client handler registration through `RegisterClientPayloadHandlersEvent`, and `ClientPacketDistributor` for client-to-server sends.

That is probably the right long-term architecture. It is also more work for the first port because Block Party currently has a single `AbstractMessage` hierarchy that branches on `NetworkDirection`.

### SavedData API

1.21.5 is a major negative for the first port. The 1.21.4 to 1.21.5 primer says `SavedData` was reworked around `SavedDataType`, moving save/load/factory logic out of the old pattern.

Block Party depends heavily on `SavedData` for:

- `BlockPartyDB`
- `HidingSpots`
- `SceneVariables`
- SQLite load/unload timing
- player-owned NPC lists

Taking the `SavedDataType` rewrite at the same time as the first loader port would make failures harder to diagnose.

### GameTest Support

1.21.5 also changes GameTest internals significantly. The primer lists changes around server creation, generated tests, structure utilities, and `TestFunction` to `TestData`. Latest docs go further toward data-driven test instances and environments.

Block Party's existing GameTests are a major migration safety net, so the first port should keep them as easy to revive as possible.

### Resource Reload APIs

Newer versions move toward newer client/server reload listener event names and sorted listener APIs. This is manageable, but it stacks with the SavedData and GameTest rewrites.

### Modpack Ecosystem Relevance

Highest if "latest" means a currently active Minecraft/NeoForge line. However, 1.21.5 specifically looks like an awkward middle target: newer than 1.21.4, but with major API churn and not necessarily the strongest ecosystem anchor compared with later lines.

### Migration Difficulty From Forge 1.19.4

High to very high. This combines the Forge-to-NeoForge migration, the 1.19.4-to-modern Minecraft migration, networking rewrite, SavedData redesign, GameTest redesign, reload listener churn, and likely larger rendering changes.

### Long-Term Maintenance Risk

For actual latest, lower long-term risk but much higher first-port risk. For 1.21.5 specifically, high maintenance risk as an intermediate because it introduces disruptive APIs without being today's latest line.

## Recommendation

Use **NeoForge 1.21.4 as the first NeoForge port target**.

The reason is not that 1.21.4 is the best final release line. It is that 1.21.4 offers the best first-port tradeoff for Block Party:

- Java 21 is required, but Java 25 is avoided.
- Modern payload networking is present, so the `SimpleChannel` rewrite is real port work, not throwaway work.
- `SavedData.Factory` keeps persistence close enough to the current `SavedData` design to protect SQLite/world-data behavior.
- GameTest registration remains close enough to the current suite that tests can come back early.
- Resource reload and deferred-register migration are modern enough to expose real NeoForge issues without also taking the 1.21.5+ data/test rewrites.

Recommended path:

1. Port to NeoForge 1.21.4 until the mod loads, regression tests pass, and the current GameTests run.
2. Stabilize parity for networking, SavedData, resource reload, and client rendering.
3. Only then choose the public release target, likely a newer maintained NeoForge line, with the 1.21.4 port acting as the controlled bridge.

Avoid NeoForge 1.20.6 unless a smaller technical spike is needed for build-tooling confidence. Avoid 1.21.5/latest as the first port unless the goal changes from "preserve behavior quickly" to "absorb all modern API churn immediately."
