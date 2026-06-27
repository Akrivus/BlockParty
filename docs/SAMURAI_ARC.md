# Samurai Arc Progression Note

This document keeps the Samurai arc practical. It is not a lore bible. Use it to
answer early worldbuilding questions before they sprawl into one-off systems.

For reusable scene-chain and pacing patterns, see `docs/SCENE_ARC_AUTHORING.md`.

Lore should usually be short, playable, and tied to the speaker. If a beat needs
to be long, it should feel like oral history from a Moe who has a reason to tell
it.

## Core Premise

Moes used to cross into the Overworld through gates. The samurai treated them as
resources. Some Moes first found purpose in the work, then only endless toil.
They rebelled, and Suzu destroyed the last true gates so the Moes could not be
pulled back into that arrangement.

The armor pieces are remnants of the samurai's authority. They do not only
reward the player; they attract Moes who recognize what the player is carrying.
Each piece reopens a different part of the old relationship between Moes and the
Overworld.

The player should prove that return can mean relationship instead of extraction.

## Suzu And The Gates

Suzu, the bell Moe, guards the torii gate boundary. She is not the general cause
of Moe spawning. She is the sealkeeper who decides whether voluntary return is
safe enough to allow.

Early game Moe appearances are need-based: a Moe appears because they need help,
because a block has been awakened, or because a progression relic draws their
attention.

Late game torii behavior should become community-based: after Suzu trusts the
player, Moes can freely visit the Overworld through active torii instead of only
appearing in crisis.

Sakura trees are not naturally occurring. Sakura wood is tied to old gate magic.
Other wood torii can exist and may support character preferences, but sakura
torii should remain special.

## Class Arcs

### Wood Moes

Theme: sustainability, stewardship, and family systems.

Wood Moes ask the player to take from forests responsibly: replant saplings, do
not clear-cut carelessly, and treat replenishable life as something tended
rather than consumed.

This arc answers: do you take, or do you tend?

The wood arc should not be a solo Dark Oak/Yami quest. The samurai divided Moes
by function and called it purpose, but purpose became control. Forest content
should therefore be about families, cultures, and the tension between belonging
and assigned roles.

Oak and birch should establish forest stewardship first. Oak grows almost
everywhere and is emotionally adjacent to many wood families; birch can live
with oak while still having its own forest. Spruce, jungle, acacia, cherry, and
mangrove can express distinct forest cultures. Dark oak/Yami belongs to this
wood-family network as a younger sister figure, not as the whole arc.

Useful player cookies:

```text
oak_replenishment_seen
birch_replenishment_seen
dark_oak_replenishment_seen
oak_befriended
birch_befriended
dark_oak_befriended
spruce_befriended OR jungle_befriended OR acacia_befriended
wood_family_arc_ready
```

Yami gives the boots after enough wood-family trust exists. The boots are the
payoff: the family accepts the player enough for Yami to reveal what she has.

### Dark Oak / Yami

Yami, the Dark Oak cardinal Moe, holds the boots. She is introverted, lives in
dark oak forests with mushrooms, and belongs to a dramatic family that is often
fighting.

The boots begin the armor progression because they change what it means for the
player to walk through the world. Yami should give them as the wood-family
payoff, not as a standalone quest reward.

### Dirt And Grass Moes

Theme: overlooked labor.

Dirt and grass were likely laborers under the samurai. They come forward after
the boots appear because the ground remembers who stepped on it.

This arc should avoid grand speeches. Their story is strongest when expressed
through practical labor, small requests, and recognition.

They should lead to the leg armor piece.

### Ore Moes

Theme: safety and return.

Ore Moes are used to visitors who come for what they hold and seldom stay.
Torches matter because light keeps the way safe. Safety means people can return.

The chest piece belongs here. It has romantic and emotional connotations: the
samurai wore the heart of the armor while failing to see the Moes as people.

Ore content should ask: are you making the cave safe enough for someone to come
back, or only safe enough to take what you wanted?

### Sand And Sandstone Moes

Sand is broader late-game mobility: she goes everywhere, and can remain tied to
the cell phone or full-game unlocks.

Sandstone Moes can carry North Africa and Egypt-inspired structure flavor,
including pyramids and headscarves. They may connect to the sword later, but do
not need to carry the current armor chain.

### Obsidian And Crying Obsidian

Regular obsidian is probably non-cardinal, ore-adjacent, and deeply tied to
portals and dimensions.

Crying Obsidian is the stronger candidate for the final cardinal arc. It shares
Suzu's rare structure niche: like bells, it is too structure-bound and rare to
work as a normal natural spawn. It also already belongs to ruined portals,
broken crossings, and failed return.

Crying Obsidian holds the kabuto and mask. She sees the complete samurai armor
as a way to bind physical reality back together. Because obsidian exists across
all three vanilla dimensions and makes portals possible, her worldview should be
about continuity, binding, and restoration at any cost.

She is a foil to Suzu:

- Suzu destroyed the gates to stop exploitation.
- Crying Obsidian sees broken gates as an injury to reality.
- The player decides whether return becomes consent or conquest.

## Armor Progression

The first implementation should keep this as content plus a few missing
primitives, not a bespoke progression framework.

Progression chain:

1. Yami gives the boots.
2. Boots make dirt and grass cardinal Moes eligible.
3. Dirt or grass arc gives the leg armor.
4. Leg armor makes ore cardinal Moes eligible.
5. Ore arc gives the dou.
6. Full armor progression makes Crying Obsidian eligible.
7. Crying Obsidian presents the kabuto and mask.
8. Suzu reacts to the mask choice.

Existing scene cookies and counters should carry most state. Prefer player
cookies for personal progression:

```text
samurai_boots_obtained
samurai_legs_obtained
samurai_dou_obtained
samurai_kabuto_obtained
dark_oak_arc_complete
grass_arc_complete
dirt_arc_complete
ore_arc_complete
crying_obsidian_arc_complete
```

Use world cookies only for truly shared world state, such as a torii visitor mode
that changes the world for all players.

## Mask Choice

The mask is allowed to be a McGuffin at first.

Keep Mask:

- the player keeps a very strong protective relic, likely an infinite
  Totem-of-Undying-style effect
- Crying Obsidian remains validated
- the ending can point toward a future expansion arc

Destroy or purify Mask:

- Suzu rewards the player
- the samurai arc closes cleanly
- torii visitor mode can unlock

The first version does not need a complex fork. A cookie and reward difference
is enough.

## Missing Primitives

The audit's main conclusion stands: the scene system already carries most of
the arc. The missing pieces should be small and reusable.

### Progression-Gated Spawn Rules

Spawn logic needs a reusable condition that can query player or world
progression state.

Example need:

```text
player has samurai_boots_obtained
=> dirt and grass cardinals may appear
```

This should not be Samurai-specific. It should become a general spawn condition
surface that can ask whether a player cookie, player counter, world cookie, or
world counter passes.

### Cardinal Arc Completion State

Arc completion can be normal player cookies. Avoid adding a relationship or
faction framework for this.

Scenes can set and query:

```text
dark_oak_arc_complete
grass_arc_complete
ore_arc_complete
```

### Samurai Armor Metadata

Scenes can give items, but multiple systems will need to ask what the player has
assembled. Add a small service or helper for armor progression queries so spawn
rules, Suzu scenes, and Crying Obsidian scenes do not repeatedly scan inventory.

Conceptual API:

```java
SamuraiProgression.getPieces(player)
SamuraiProgression.hasPiece(player, piece)
SamuraiProgression.hasCompleteArmor(player)
```

Implementation can still be cookie-backed.

### Torii Visitor System

The Suzu ending does not require full companion adventuring. It needs voluntary
visitors.

Add a periodic system that can spawn eligible Moes near active torii after the
proper world or player unlock. This is a community feature, not full AI.

Design sentence:

```text
I decided to stop by.
```

## Defer

Do not build these first:

- boss AI
- faction infrastructure
- full adventuring AI
- a custom progression framework

The spectral samurai can begin as dialogue, a combat entity, and a defeat
condition. Fancy boss behavior can come after the arc is playable.

## First Feature Tickets

1. Add progression-gated spawn eligibility that can query scoped cookies and
   counters. Initial engine support is active through reusable progression
   gates, including wood-family readiness for Yami's boots payoff.
2. Add Samurai progression helper backed by player cookies. Initial helper
   support is active for armor piece queries and the first boots gate.
3. Author oak/birch stewardship scenes.
4. Author enough wood-family culture/tension scenes to set
   `wood_family_arc_ready`, or the equivalent component cookies.
5. Author Yami boots payoff scenes.
6. Gate dirt and grass encounter eligibility behind boots.
7. Author dirt or grass leg-armor scenes.
8. Gate ore cardinal spawns behind leg armor.
9. Author ore dou scenes.
10. Add Crying Obsidian encounter eligibility for full armor.
11. Add mask choice cookies and rewards.
12. Add torii visitor mode unlocked by the Suzu-aligned ending.

## Active Phase 3 Slice

The wood-family replenishment spine is now implemented as quiet behavior first:

- oak, birch, spruce, acacia, jungle, and dark oak sapling drops can summon
  matching wood-family representatives after relevant tree cutting
- wood-family Moes collect dropped saplings and replant them without explaining
  vanilla tree mechanics to the player
- jungle and dark oak use a two-by-two sapling placement chore
- successful replenishment records player conduct cookies
- observed replenishment sets conduct cookies, but not friendship by itself
- the wood-family readiness cookie is refreshed from completed friendship chains
- Yami's boots scene is gated by `wood_family_arc_ready`

## Wood Character Scene Chain Shape

Wood-family familiarity should be paced by repeated visits and forest conduct,
not by spam-clicking dialogue. Each required wood-family character should use a
small chain:

```text
wood/<character>_exchange_1.json
wood/<character>_exchange_2.json
wood/<character>_exchange_3.json
wood/<character>_complete.json
```

Each exchange should:

- require the previous stage counter/cookie
- require recent relevant replenishment or place familiarity
- require `elapsed_since_marker` for the previous exchange
- end by incrementing that character's familiarity counter
- end by calling `mark_time` for the current exchange

The complete scene should set the character's `*_befriended` player cookie and
call `refresh_wood_family_progression`. The aggregate readiness rule is:

```text
oak_befriended
birch_befriended
dark_oak_befriended
spruce_befriended OR jungle_befriended OR acacia_befriended
```

The boots path should require Oak, Birch, Dark Oak, and one of Spruce, Jungle,
or Acacia to be befriended. The concrete payoff scene can require
`wood_family_arc_ready` for the optional branch, because that cookie is only set
after one of the optional friendships is complete.
