# Scene Arc Authoring

This guide captures the working pattern for BlockParty scene arcs: player
conduct creates attention, time creates familiarity, and conversation pays off
what the player has already done.

Use this when adding future block-class arcs such as Crying Obsidian, Nether
Quartz, Basalt, workstations, or other multi-character progression chains.

## Core Shape

Scene arcs should usually follow this loop:

```text
conduct happened
time passed
player returned
conversation advances
```

Do not use dialogue to explain vanilla mechanics the player is already using.
Dialogue should recognize behavior, reveal character, or mark relationship
progress.

## Conduct Before Conversation

Prefer behavioral triggers over instructional dialogue.

For the wood arc, the meaningful trigger is not "plant a sapling because a Moe
told you to." It is:

```text
player cut a tree
saplings dropped
saplings would expire
wood Moe replenished the forest
player saw or benefited from that replenishment
```

The conduct cookie records that the player has been noticed in a relevant
context. It should unlock the relationship chain, not complete it.

Examples:

```text
oak_replenishment_seen
birch_replenishment_seen
dark_oak_replenishment_seen
```

## Familiarity Chains

For ordinary relationship progression, use short paced exchanges:

```text
exchange_1 = noticed
exchange_2 = familiar
exchange_3 = regular
complete = befriended / normal pool unlocked
```

The emotional weight comes from spacing, not monologues.

Each exchange should:

- require the previous counter value
- require a relevant conduct cookie
- require `elapsed_since_marker` after the first exchange
- set the next familiarity counter
- call `mark_time` for the current exchange

Each complete scene should:

- require the final familiarity counter
- require `elapsed_since_marker` for the final exchange
- set a `*_befriended` player cookie
- set the familiarity counter past the chain
- call any relevant arc refresh action

## Pacing

Use the scene pacing primitive to prevent speedrunning intimacy.

Exchange 1 may happen immediately after relevant conduct. Later exchanges should
require time:

```json
{
  "type": "block_party:elapsed_since_marker",
  "filter": {
    "scope": "player",
    "name": "oak_exchange_1",
    "min_game_days": 1
  }
}
```

Use game time for "come back later" familiarity. Use real time only when the
arc intentionally cares about long absences, return-after-months moments, or
calendar-scale relationships.

## Selection Rules

Scene selection is deliberately light-weight: matching candidates are shuffled,
then the most-specific fulfilled scenes are preferred by filter count. File order
is not priority.

For progression scenes:

- Give every beat an explicit cookie, counter, or marker guard.
- Make mutually exclusive beats visibly mutually exclusive in JSON.
- Do not put two story-critical scenes on the same trigger with the same filters
  and expect one to happen first.
- Use `mark_time` plus `elapsed_since_marker` for "come back later" pacing.
- Use response actions for immediate dialogue chains, and state filters for
  relationship chains across visits.

## Dialogue Density

Keep relationship-chain dialogue tiny. Good chain lines often look almost too
small in isolation:

```text
"You came back."
"Most people don't."
"Morning, @.name."
```

The scene system blocks movement while talking, so avoid using conversations as
ambient narration. If the player right-clicks, they chose a focused exchange;
respect that with short, pointed lines.

## Multi-Character Arcs

When an arc represents a family, culture, class, or faction, do not make one
character carry the whole arc.

Use component cookies:

```text
oak_befriended
birch_befriended
dark_oak_befriended
spruce_befriended
```

Then use an aggregate readiness cookie for payoff scenes:

```text
wood_family_arc_ready
```

Keep aggregate logic centralized in Java when JSON would need awkward OR
branches. For the wood arc, `refresh_wood_family_progression` checks:

```text
oak_befriended
birch_befriended
dark_oak_befriended
spruce_befriended OR jungle_befriended OR acacia_befriended
```

## Payoff Scenes

A payoff scene should feel like a consequence of the relationship web, not a
solo quest reward.

For Yami:

```text
wood conduct happened
Oak, Birch, Dark Oak, and one optional wood member trust the player
wood_family_arc_ready is set
Yami reveals the boots
```

Payoff scenes may require both the aggregate cookie and key component cookies
when that makes the content easier to audit.

## Attention Limits

Attention should feel like being noticed, not like an entity farm.

The active wood attention primitive records each matching sapling-drop conduct
event, but it only keeps one matching active cardinal visitor near the
source/player at a time. Repeated drops refresh attention memory while the first
visitor is still doing the chore instead of summoning duplicates.

Authoring implications:

- Treat attention scenes as the first acknowledgement of conduct.
- Use player-scoped cookies for conduct that should unlock later relationship
  beats.
- Let the active chore create the durable conduct cookie when it completes.
- Avoid writing attention scenes that assume every sapling drop creates a fresh
  character.
- Add a Java-side limit before adding any new attention primitive that can fire
  repeatedly during ordinary play.

## Authoring Checklist

Before adding a new arc:

- Identify the block-class theme in one sentence.
- Identify the conduct trigger that happens through play.
- Decide whether the arc is solo, family, culture, faction, or workstation.
- Name conduct cookies separately from friendship cookies.
- Choose the aggregate readiness cookie, if any.
- Sketch 3 short exchanges and one complete beat per required character.
- Add mutually exclusive guards for scenes that share a trigger.
- Add or reuse a refresh action if readiness depends on OR branches.
- Add tests for conduct recording, pacing primitives, and payoff gates.
- Add an attention duplicate-limit test for any new conduct primitive that
  summons entities.
- Update `docs/SAMURAI_ARC.md` or the relevant arc note with the new model.

## Current Wood Pattern

Current wood chains live in:

```text
src/main/resources/data/block_party/scenes/wood/
```

Current infrastructure:

```text
block_party.world.attention.WoodForestAttention
block_party.world.progression.WoodFamilyProgression
block_party.scene.actions.MarkTimeAction
block_party.scene.actions.RefreshWoodFamilyProgressionAction
```

Current payoff:

```text
src/main/resources/data/block_party/scenes/samurai/yami_boots_payoff.json
```
