# Built-In Volume: Samurai Arc

This document is the working bible for the built-in Block Party volume. Keep it
practical enough to drive scenes and primitives, but coherent enough that the
prepackaged datapack can become the reference example for later volumes.

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

The deeper secret is that the armor is not only equipment. The samurai tried to
make himself immortal by becoming his armor: a counterfeit Moe path of
human-to-object-to-spirit. The pieces are the separated organs of that failed
transformation. Equipping them reunites a body of authority before the player
understands what they are assembling.

The player should prove that return can mean relationship instead of extraction.
The ending is not the player choosing Suzu or Crying Obsidian as a faction. The
player creates the conditions where the Moes can stop defining themselves around
the samurai and choose a shared future.

## Volume Thesis

The built-in volume teaches the story engine by making the player arrange the
world until reconciliation becomes possible.

The player does not solve the Moes' emotional history by decree. They put things
in the right place:

- forests are replenished instead of stripped
- overlooked ground Moes are recognized
- caves become safe enough for return, not only extraction
- armor pieces are gathered into one dangerous truth
- the mask is broken and the trapped spirit is confronted
- Suzu and Crying Obsidian can finally argue about the future instead of the war

The final unlock is open torii gates as voluntary community movement, not a
reward chest. The player has made return safe enough for the Moes to choose it.

## Suzu And The Gates

Suzu, the bell Moe, guards the torii gate boundary. She is not the general cause
of Moe spawning. She is the sealkeeper who decides whether voluntary return is
safe enough to allow.

Suzu is not simply anti-human. She is anti-knight: she distrusts anyone becoming
the kind of armed authority that once made the gates into a system of control.
As the player collects armor pieces, her concern should sharpen from suspicion
to alarm. She knows the pieces were separated for a reason.

Early game Moe appearances are need-based: a Moe appears because they need help,
because a block has been awakened, or because a progression relic draws their
attention.

Late game torii behavior should become community-based: after Suzu trusts the
player, Moes can freely visit the Overworld through active torii instead of only
appearing in crisis.

Torii construction is not gated on Sakura world generation. Shrine frames accept
the data-driven `block_party:shrine_frame_blocks` tag, with beam tips selected by
`block_party:shrine_edge_blocks`; Sakura remains a decorative option rather than
a progression prerequisite. Activating a valid shrine introduces Suzu and sets
the shared `torii_gate_opened` world cookie.

Suzu should oppose using the sword on the mask. Her objection is not cowardice:
her generation did not truly defeat the samurai, they took him apart. Reuniting
the armor and striking the mask risks undoing the sacrifice that made the seal
possible.

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

### Dirt And Ground Cardinal Moes

Theme: overlooked labor.

Dirt is a corporeal population type, like stone. Dirt Moes are individuals with
homes, routines, and social clustering. They see growing plant life on them as a
form of enlightenment.

Grass Block is the clearest cardinal expression of that ideal. Podzol and
Mycelium represent different avenues of growth, while Coarse Dirt can express
either youthful innocence or malicious ignorance. These surface identities
carry the authored ground arc without requiring a separate flower presentation.

Sand is the outward-facing traveler of the family: a beach-and-desert influencer
who introduces the cell phone. Red Sand is her cousin. Both remain authored
cardinal identities rather than ordinary corporeal dirt residents.

The ground story should avoid grand speeches. Its strongest form is practical
labor, small requests, and recognition. A ground cardinal should lead the player
to the leg armor piece by giving that overlooked work a specific voice.

### Ore Moes

Theme: safety and return.

Ore Moes are used to visitors who come for what they hold and seldom stay.
Torches matter because light keeps the way safe. Safety means people can return.
The arrival trigger observes ordinary cave lighting: after collecting enough of
an ore's drop, placing a torch on natural Overworld base stone within torii
influence creates an encounter opportunity. It is not a constructed ritual.

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

Crying Obsidian gives the player the final armor piece, **Masked Samurai's
Kabuto** (`masked_samurai_kabuto`). With it, the player has the complete armor
set and its special effect becomes active. Suzu does not trust that completed
set: it has reunited too much of the samurai's authority.

Crying Obsidian sees the completed armor as a way to bind physical reality back
together. Because obsidian exists across all three vanilla dimensions and makes
portals possible, her worldview should be about continuity, binding, and
restoration at any cost.

Crying Obsidian wants knights, but not because she worships the samurai. She
wants resolution and strength. To her, Suzu has been guarding a wound: broken
gates, scattered armor, a mask that still matters, and no future path. She wants
the player strong enough to call the old authority back and end it.

She is a foil to Suzu:

- Suzu destroyed the gates to stop exploitation.
- Crying Obsidian sees broken gates as an injury to reality.
- Suzu fears restored authority.
- Crying Obsidian fears permanent separation.
- The player creates the conditions where return can become consent instead of
  conquest.

## Armor Progression

The first implementation should keep this as content plus a few missing
primitives, not a bespoke progression framework.

Progression chain:

1. Yami gives the boots.
2. Boots make the ground-cardinal arc eligible.
3. Ground arc gives the leg armor.
4. Leg armor makes ore cardinal Moes eligible.
5. Ore arc gives the dou.
6. Full armor progression makes Crying Obsidian eligible.
7. Crying Obsidian gives the Masked Samurai's Kabuto; the full-set effect
   becomes active.
8. The player combines the sword with the sealed mask in a crafting table,
   breaking it and summoning the spectral samurai.
9. Defeating the samurai frees the ending conversation.
10. Suzu and Crying Obsidian reconcile enough to open the torii gates.

Existing scene cookies and counters should carry most state. Prefer player
cookies for personal progression:

```text
samurai_boots_obtained
samurai_legs_obtained
samurai_dou_obtained
samurai_kabuto_obtained
samurai_sword_obtained
samurai_spirit_defeated
torii_visitor_mode_unlocked
dark_oak_arc_complete
ground_arc_complete
ore_arc_complete
crying_obsidian_arc_complete
```

Use world cookies only for truly shared world state, such as a torii visitor mode
that changes the world for all players.

## Sword, Mask, And Boss Trigger

The sword is not just the final equipment slot. The armor represents authority;
the sword represents judgment. It should be the tool that breaks the sealed
mask and forces the old claim to appear.

The climax is a crafting-table event:

```text
player has reunited the armor
player obtains the sword
player combines the sword with the sealed mask in a crafting table
the mask breaks
the spectral samurai is forced into the Overworld
boss fight begins
```

The event should feel irreversible. The player commits the act; Suzu warned
against it, and Crying Obsidian insisted that unresolved power must be faced.

The spectral samurai is not loot. He is the obstacle that must be gone before
Suzu can trust the gates again. The spirit is blocked from returning to the Moe
realm, so the fight happens in the Overworld. Defeating him ends his claim
forever.

Useful cookies:

```text
samurai_sword_obtained
samurai_mask_broken
samurai_spirit_summoned
samurai_spirit_defeated
```

The mask is not an early reward or a player-owned prerequisite. When the ghost
is defeated, the player receives the broken mask alone: a useless relic that
records what happened rather than a new armor upgrade. It should be a dedicated,
non-equipable broken-mask item rather than a second kabuto armor variant.

## Ending Shape

The ending should not be a binary faction choice where the player picks Suzu or
Crying Obsidian. The stronger ending is reconciliation between Moes.

After the samurai is gone:

- Suzu no longer has to guard the seal as an emergency measure against him.
- Crying Obsidian no longer has to treat broken gates as the only unresolved
  proof that the old world still matters.
- They do not need to agree about the gates immediately.
- They do need to agree that the next gates will be built on consent.

Opening the torii gates is the ending because the Moes reach an understanding,
not because the player "wins" an argument. The player arranged the world so the
conversation could happen.

Ending implementation can stay simple at first:

```text
samurai_spirit_defeated
=> torii_visitor_mode_unlocked
```

`torii_visitor_mode_unlocked` is world state. Once Suzu opens the gates, the
same active-torii rule applies in every dimension rather than being a separate
per-player reward.

## Missing Primitives

The audit's main conclusion stands: the scene system already carries most of
the arc. The missing pieces should be small and reusable.

### Progression-Gated Spawn Rules

Spawn logic needs a reusable condition that can query player or world
progression state.

Example need:

```text
player has samurai_boots_obtained
=> ground cardinals for the ground arc may appear
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
ground_arc_complete
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

### Full Armor Set Effect

The completed set must expose one reliable gameplay state as soon as the player
receives Masked Samurai's Kabuto. The exact mechanical effect is intentionally
open, but it must be derived from the complete-set query instead of adding a
second progression path.

Scenes only need to know that the set is complete. Suzu's distrust is a story
reaction to that fact, not a separate armor-state system.

Implementation note: `SamuraiProgression.Piece.KABUTO` currently recognizes
`samurai_kabuto`. Align it with `masked_samurai_kabuto` before content awards
the final set piece, and update the associated registry coverage at the same
time.

### Torii Visitor System

The Suzu ending does not require full companion adventuring. It needs voluntary
visitors.

After Suzu opens the gates, add a periodic system that lets corporeal Moes enter
the area around an active torii in any dimension. This is a community feature,
not full AI: it increases ordinary population presence around a gate rather than
creating crisis spawns or companions.

Design sentence:

```text
I decided to stop by.
```

The system needs a bounded local population rule: active torii are recognized
in their own dimension, and corporeal Moe arrivals are capped per gate/area so
they cannot become an uncontrolled general spawn multiplier.

### Sword/Mask Event Primitive

The sword and sealed mask need a small crafting-completion primitive before the
boss fight becomes fancy. It validates player progression and detects the
sword/mask crafting result.

Minimum behavior:

```text
requires full armor progression
requires samurai_sword_obtained
sets samurai_mask_broken
sets samurai_spirit_summoned
spawns or schedules the spectral samurai encounter
```

## Locked Implementation Strategy

Build the volume as narrow, playable slices. Each slice should leave a usable
piece of the reference datapack behind and introduce only the engine surface
needed by its scenes. Do not make Suzu and Crying Obsidian into player-selected
routes; their scenes are two perspectives required for the same ending.

### Slice 1: Lock The Progression Vocabulary

Extend `SamuraiProgression` and the documented cookie vocabulary to cover the
whole volume before adding content that depends on it.

Required state:

```text
armor: boots, legs, dou, Masked Samurai's Kabuto
relics: sword, broken mask (post-boss only)
climax: mask_broken, spirit_summoned, spirit_defeated
ending: suzu_trust_ready, crying_obsidian_arc_complete,
        torii_visitor_mode_unlocked
```

Expose reusable scene filters for an individual armor piece and complete armor,
plus one refresh action where a scene changes derived progression state. The
helper remains a convenience layer over ordinary player cookies; it is not a
new general-purpose quest system.

### Slice 2: Make Eligibility Data-Driven

Complete the reusable progression gate for cardinal spawn eligibility. It must
be able to query scoped cookies and counters without referring to the Samurai
arc by name.

Initial gates:

```text
boots -> ground-cardinal arc eligibility
legs  -> ore cardinal eligibility
full armor progression -> Crying Obsidian eligibility
```

Keep encounter eligibility separate from scene completion. A character being
allowed to appear is not the same thing as the player completing their arc.

### Slice 3: Complete The Wood-To-Boots Vertical Slice

Author the stewardship and family scenes through Yami's boots payoff. This is
the first complete example of the engine: conduct is observed, attention is
earned, familiarity grows over time, a family state is derived, and a relic is
given.

Playable end state: the player earns the boots, a ground cardinal can begin the
ground arc, and ordinary Dirt Moes remain corporeal world residents.

### Slice 4: Add Ground And Ore Conduct

Author ground-cardinal practical labor scenes that award the leg armor, then ore
safety-and-return scenes that award the dou. Add only the attention definitions
those scenes truly need: overlooked ground work, cave safety, and safe return.

Playable end state: the player has enough armor for Crying Obsidian to take
interest, and Suzu's warnings have escalated alongside that recognition.

### Slice 5: Add Gate And Portal Recognition

Add small, reusable world-recognition surfaces for torii and ruined portals:
construction or discovery triggers, active/nearby filters, and anchors where a
scene needs a character to approach or wait. Use these to author Suzu's gate
scenes and Crying Obsidian's portal-bound introduction.

Playable end state: the two cardinal perspectives are present in the world
before the climax, rather than appearing only to explain it.

### Slice 6: Deliver The Sword/Mask Event

Implement the minimum irreversible crafting-table event: validate the full
armor and sword, break the sealed mask used by the recipe, mark the spirit
summoned, then spawn or schedule the encounter.

Playable end state: a player can deliberately bring the samurai back into the
Overworld after hearing both Suzu's warning and Crying Obsidian's argument.

### Slice 7: Deliver A Minimal Samurai Encounter

Add the spectral samurai as a combat entity with entrance dialogue, a
defeat condition, and persistence/cleanup rules. Prioritize clear encounter
ownership and reliable completion cookies over elaborate boss AI.

Playable end state: defeating the spirit permanently sets
`samurai_spirit_defeated` and allows the ending scenes to begin.

### Slice 8: Reconciliation And Visitor Mode

Author the Suzu/Crying Obsidian ending conversation, have Suzu open the gates,
and add the periodic active-torii population system. Corporeal Moes should be
able to enter the area around active torii in any dimension. This should feel
like a community choosing to cross, not new crisis spawns or companions.

Playable end state: the gates reopen as voluntary movement and the built-in
volume has a clear, replayable ending state.

### Acceptance Rule

Do not advance a slice merely because its code compiles. Advance when its
scenes can be played from a fresh world using documented prerequisites, its
state is visible through cookies/counters or ordinary game behavior, and it
does not require a hidden manual setup step. Each completed slice should add a
small example to the authoring documentation if it introduces a reusable scene
primitive.

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
   support is active for armor piece queries and the first boots gate. Align its
   final-piece mapping with `masked_samurai_kabuto`.
3. Author oak/birch stewardship scenes.
4. Author enough wood-family culture/tension scenes to set
   `wood_family_arc_ready`, or the equivalent component cookies.
5. Author Yami boots payoff scenes.
6. Gate ground-cardinal encounter eligibility behind boots.
7. Author ground-cardinal leg-armor scenes.
8. Gate ore cardinal spawns behind leg armor.
9. Author ore dou scenes.
10. Add Crying Obsidian encounter eligibility for full armor.
11. Add sword acquisition and the sword/sealed-mask crafting-table event.
12. Add spectral samurai dialogue/combat/defeat condition and award the
    non-equipable broken-mask relic.
13. Add Suzu/Crying Obsidian reconciliation scenes.
14. Add Suzu's all-dimension torii population mode unlocked after the ghost is
    defeated, including corporeal Moe presence around active gates.

## Active Phase 3 Slice

The wood-family arrival spine uses collection plus deliberate planting for Oak,
Birch, Spruce, Jungle, Acacia, and Dark Oak:

- shrine activation must have set the shared `torii_gate_opened` world cookie
- item pickups in `block_party:moe/progression/tracked_items` are recorded in a
  player-scoped ledger keyed by item id; the Oak requirement reads the
  `minecraft:oak_log` ledger entry
- after 64 of a family's specific log item, planting its vanilla sapling in a
  valid location can introduce that family's cardinal
- the planting must be within the 2048-block horizontal Manhattan influence of
  a persisted torii, matching the existing pink-sky, Fuji, and firefly presentation
- a nearby active Oak cardinal suppresses duplicate arrivals
- encounter scenes can use `block_party:reset_progression_counters` with the
  `minecraft:oak_log` item id to consume that ledger entry, pacing the next encounter by
  another 64 qualifying pickups without consuming progress on mere eligibility
- Oak sapling-drop attention remains recorded for compatibility, but no longer
  summons the cardinal; the other wood families retain the legacy attention
  arrival until they migrate to their own collection and planting definitions

The older replenishment behavior remains useful after a cardinal has arrived:

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
