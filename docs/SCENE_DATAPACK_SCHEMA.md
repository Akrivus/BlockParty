# Scene And Datapack Content Schema

This document is the author-facing contract for Block Party data-driven content.
It describes the active NeoForge scene/datapack surface: what content can express
today, what defaults are applied, and what fails closed.

The goal is to make authored mechanics and generated datapacks target the same
small behavior language.

## Authoring Contract

This document is the compatibility surface for scene-pack authors and generated
scene packs. Do not depend on Java class names, package names, or implementation
details that are not described here.

Stable author-facing surfaces:

- resource locations for triggers, filters, actions, response icons, traits, and
  content files
- JSON field names and documented defaults
- fail-closed behavior for unknown filters
- documented fallback behavior for unknown actions
- scoped cookie/counter state for Moes, players, and the world
- social-affinity matcher fields and signal fields

Non-contract implementation details:

- Java package names
- concrete helper class names
- internal database table layout
- renderer/model implementation details
- GameTest helper names

If a content idea cannot be expressed with this schema, add a narrow Java
primitive first, then expose it here as a filter, action, resource shape, or
documented limitation. Do not encode one-off story logic directly in entity,
item, or screen classes.

## Authoring Workflow

Recommended workflow for human-authored or Codex-authored packs:

1. Start with one scene and one trigger.
2. Add the smallest set of filters needed to protect the scene.
3. Add one dialogue/action beat.
4. Run `/reload` and check logs.
5. Trigger the scene in game.
6. Add cookies/counters only after the first scene fires.
7. Split larger stories into multiple scenes chained by responses or state.

Generated packs should prefer explicit, repetitive JSON over clever implicit
state. A scene pack is easier to debug when every trigger, filter, action, and
state key has a clear author-facing name.

## Resource Layout

Block Party loads content from normal Minecraft data/resource packs. The active
server-side content roots are:

- `data/<namespace>/scenes/*.json`
- `data/<namespace>/moes/social_affinities/*.json`
- `data/<namespace>/moes/names/*.json`
- `data/<namespace>/moes/aliases/*.json`
- `data/<namespace>/moes/textures/*.json`
- `data/<namespace>/moes/sounds/*.json`
- `data/<namespace>/tags/block/moe/**/*.json`

The primary mechanic authoring surface is `data/<namespace>/scenes/*.json`.
Social tuning lives in `data/<namespace>/moes/social_affinities/*.json`.
Texture/sound overrides live under `data/<namespace>/moes/textures` and
`data/<namespace>/moes/sounds`.

Scene IDs are the file path under `scenes` without `.json`. For example:

```text
data/block_party/scenes/bell/intro.json
```

has scene ID:

```text
block_party:bell/intro
```

When scene triggers, filters, and action IDs are written with the `minecraft`
namespace, Block Party remaps them to `block_party`. Prefer explicit
`block_party:*` IDs in authored content.

## Scene Shape

A scene file has this shape:

```json
{
  "trigger": "block_party:right_click",
  "filters": [
    "block_party:always"
  ],
  "actions": [
    {
      "type": "block_party:send_dialogue",
      "action": {
        "text": "Hello, @name.",
        "tooltip": true,
        "speaker": {
          "identity": "character",
          "position": "left",
          "animation": "wave",
          "emotion": "happy",
          "speaks": false,
          "scale": 1.0
        },
        "responses": [
          {
            "icon": "block_party:next_response",
            "text": "Continue",
            "actions": [
              "block_party:end"
            ]
          }
        ]
      }
    }
  ]
}
```

Fields:

- `trigger`: optional scene trigger. Defaults to `block_party:null`.
- `filters`: optional array. Defaults to an empty array, which means no filters
  block the scene.
- `actions`: optional array. Defaults to an empty array.

All filters must pass for a scene to run. If multiple scenes match the same
trigger, candidates are shuffled and the first fulfilled scene is selected.

## Triggers

Supported trigger values:

- `block_party:creation`
- `block_party:hiding_spot_discovered`
- `block_party:phone_call`
- `block_party:follow_started`
- `block_party:follow_ended`
- `block_party:party_invite`
- `block_party:wait`
- `block_party:dismiss`
- `block_party:shift_left_click`
- `block_party:left_click`
- `block_party:shift_right_click`
- `block_party:right_click`
- `block_party:hurt`
- `block_party:attack`
- `block_party:stare`
- `block_party:every_tick`
- `block_party:random_tick`
- `block_party:null`

Priority matters. A scene can interrupt the current scene only when its trigger
priority is greater than the active trigger priority. Equal-priority triggers are
ignored until the active scene clears.

Current priorities, high to low:

- `creation`, `hiding_spot_discovered`, `phone_call`: 8
- `follow_started`, `follow_ended`, `party_invite`, `wait`, `dismiss`,
  `shift_left_click`: 7
- `left_click`, `shift_right_click`: 6
- `right_click`: 5
- `hurt`: 4
- `attack`: 3
- `stare`: 2
- `every_tick`, `random_tick`: 1
- `null`: 0

## Filter Forms

Filters may be strings:

```json
"block_party:if_raining"
```

or objects:

```json
{
  "type": "block_party:loyalty",
  "operation": "at_least",
  "value": 8
}
```

Objects may also use the legacy wrapper form:

```json
{
  "filter": {
    "type": "block_party:block",
    "name": "minecraft:bell"
  }
}
```

Unknown filters fail closed. A scene with an unknown filter does not pass.

## Simple Filters

These filters take no extra fields:

- `block_party:always`
- `block_party:never`
- `block_party:is_corporeal`
- `block_party:is_cardinal`
- `block_party:is_following`
- `block_party:can_follow_across_dimensions`
- `block_party:if_raining`
- `block_party:if_sunny`
- `block_party:if_full_moon`
- `block_party:if_gibbous_moon`
- `block_party:if_half_moon`
- `block_party:if_crescent_moon`
- `block_party:if_new_moon`
- `block_party:if_morning`
- `block_party:if_noon`
- `block_party:if_evening`
- `block_party:if_night`
- `block_party:if_midnight`
- `block_party:if_dawn`
- `block_party:if_blood_type_ab`
- `block_party:if_blood_type_b`
- `block_party:if_blood_type_a`
- `block_party:if_blood_type_o`
- `block_party:if_himedere`
- `block_party:if_kuudere`
- `block_party:if_tsundere`
- `block_party:if_yandere`
- `block_party:if_deredere`
- `block_party:if_dandere`
- `block_party:if_angry`
- `block_party:if_begging`
- `block_party:if_confused`
- `block_party:if_crying`
- `block_party:if_mischievous`
- `block_party:if_embarrassed`
- `block_party:if_happy`
- `block_party:if_normal`
- `block_party:if_pained`
- `block_party:if_psychotic`
- `block_party:if_scared`
- `block_party:if_sick`
- `block_party:if_snooty`
- `block_party:if_smitten`
- `block_party:if_tired`
- `block_party:if_male`
- `block_party:if_female`
- `block_party:if_nonbinary`

## Numeric Filters

Numeric filters compare an actual value against `value`.

Supported operations:

- `equals` default
- `greater_than`
- `greater_than_equals`
- `at_least`
- `less_than`
- `less_than_equals`
- `at_most`

Supported numeric filters:

- `block_party:if_time`: current day time modulo 24000
- `block_party:health`
- `block_party:food_level`
- `block_party:loyalty`
- `block_party:stress`
- `block_party:target_affection`
- `block_party:target_loyalty`
- `block_party:target_trust`
- `block_party:target_relationship_stress`
- `block_party:follow_ticks_remaining`
- `block_party:anchor_distance`
- `block_party:anchor_priority`
- `block_party:social_affinity`
- `block_party:social_tension`
- `block_party:social_interest`

Example:

```json
{
  "type": "block_party:stress",
  "operation": "at_least",
  "value": 10
}
```

Most object filters support `"not": true` to invert the result.

## String And Trait Filters

String filters compare against `value`.

Supported operations:

- `equals` default
- `not_equals`
- `prefix`
- `suffix`
- `contains`
- `matches`

Supported string/trait filters:

- `block_party:name`
- `block_party:has_cookie`
- `block_party:social_target_name`
- `block_party:social_target_blood_type`
- `block_party:social_target_dere`
- `block_party:social_target_zodiac`
- `block_party:social_target_emotion`

Example:

```json
{
  "type": "block_party:name",
  "operation": "contains",
  "value": "Suzu"
}
```

## Counter And Cookie Filters

Counters and cookies are stored in explicit scopes:

- `npc` / `moe`: per Moe database ID, the default for `counter` and `has_cookie`
- `player`: per target player UUID
- `world` / `global`: shared world progression state

Counter filter:

```json
{
  "type": "block_party:counter",
  "name": "bell_intro_seen",
  "operation": "equals",
  "value": 0
}
```

Cookie filter:

```json
{
  "type": "block_party:has_cookie",
  "name": "temple_awakened",
  "value": "true"
}
```

If `has_cookie` omits `value`, it passes when the named cookie exists.

Scoped filter IDs:

- `block_party:player_counter`
- `block_party:player_has_cookie`
- `block_party:world_counter`
- `block_party:world_has_cookie`

The generic `counter` and `has_cookie` filters also accept `"scope": "player"`
or `"scope": "world"`.

`block_party:family_name` matches the Moe-specific localized family name for
the source block, such as `Suzu` for `minecraft:bell`. It accepts the same
string operations as `name`.

## Identity, Item, And Block Filters

Entity identity:

```json
{
  "type": "block_party:self",
  "name": "block_party:moe"
}
```

Block identity:

```json
{
  "type": "block_party:block",
  "name": "minecraft:bell"
}
```

Block tag:

```json
{
  "type": "block_party:block",
  "name": "#minecraft:logs"
}
```

Held item:

```json
{
  "type": "block_party:player_held_item",
  "hand": "main_hand",
  "name": "block_party:invite",
  "count": {
    "operation": "at_least",
    "value": 1
  }
}
```

Supported filters:

- `block_party:self`
- `block_party:block`
- `block_party:held_item`
- `block_party:player_held_item`
- `block_party:player_has_item`
- `block_party:moe_has_item`
- `block_party:has_item`
- `block_party:social_target_block`

For item and block `name`, prefix with `#` to match a tag.

Inventory item filters search a whole inventory rather than only one hand.
`has_item` is an alias for `moe_has_item`.

```json
{
  "type": "block_party:player_has_item",
  "item": "minecraft:apple",
  "count": 2
}
```

## Follow, Routine, Anchor, And Social Filters

Follow intent values:

- `phone_call`
- `party_invite`
- `follow_request`
- `come_here`
- `wait`
- `dismiss`

Routine intent values:

- `idle`
- `relax`
- `rest`
- `sleep`
- `gather`
- `visit`
- `worship`
- `chore`

Anchor type values:

- `home`
- `garden`
- `location`
- `sapling`
- `shrine`

Supported filters:

- `block_party:follow_intent`
- `block_party:follow_player_is_target`
- `block_party:routine_intent`
- `block_party:explicit_routine_intent`
- `block_party:has_anchor`
- `block_party:anchor_type`
- `block_party:anchor_distance`
- `block_party:anchor_priority`
- `block_party:anchor_player_owned`
- `block_party:has_social_target`
- `block_party:social_affinity`
- `block_party:social_tension`
- `block_party:social_interest`
- `block_party:social_visual`
- `block_party:social_reaction`
- `block_party:social_target_name`
- `block_party:social_target_block`
- `block_party:social_target_blood_type`
- `block_party:social_target_dere`
- `block_party:social_target_zodiac`
- `block_party:social_target_emotion`

Social filters accept optional `radius`; the default is `8.0`.

Example:

```json
{
  "type": "block_party:social_tension",
  "radius": 10,
  "operation": "greater_than",
  "value": 0.5
}
```

Social visual values:

- `affinity`
- `fame`
- `interest`
- `tension`
- `none`

Social reaction values:

- `celebrate`
- `cling`
- `fluster_retreat`
- `shy_retreat`
- `show_off`
- `observe`
- `none`

## Attention, Place Memory, Observation, And Gift Filters

Recent content-forward systems expose authoring hooks for attention, remembered
places, environmental observations, gifts, and social-place behavior.

Attention filters:

- `block_party:has_attention`
- `block_party:attention_type`
- `block_party:attention_source`
- `block_party:attention_item`
- `block_party:attention_count`
- `block_party:attention_block`

Remembered-place filters:

- `block_party:if_remembers_place`
- `block_party:if_remembers_house`
- `block_party:if_remembers_shelter`
- `block_party:if_remembers_garden`
- `block_party:if_remembers_grove`
- `block_party:if_remembers_field`
- `block_party:if_remembers_workshop`
- `block_party:if_remembers_waterfront`
- `block_party:if_remembers_cave`
- `block_party:if_remembers_shrine`
- `block_party:if_remembers_farm`
- `block_party:if_at_remembered_place`
- `block_party:if_remembered_place_overcrowded`
- `block_party:if_remembered_place_invalid`
- `block_party:remembered_place_type`
- `block_party:remembered_place_score`
- `block_party:remembered_place_occupancy`
- `block_party:remembered_place_capacity`
- `block_party:remembered_place_anchor_type`

Environmental observation filters:

- `block_party:if_has_environmental_observation`
- `block_party:if_observed_awe`
- `block_party:if_observed_affinity`
- `block_party:if_observed_tension`
- `block_party:observed_block`
- `block_party:observed_signal_layer`
- `block_party:observed_affinity`
- `block_party:observed_tension`
- `block_party:observed_interest`

Gift and item-preference filters:

- `block_party:if_has_gift_memory`
- `block_party:if_liked_gift`
- `block_party:if_disliked_gift`
- `block_party:if_interesting_gift`
- `block_party:if_begged_for_gift`
- `block_party:gift_preference`
- `block_party:gift_aversion`
- `block_party:gift_interest`
- `block_party:gift_begging`
- `block_party:gift_item`
- `block_party:held_item_preference`
- `block_party:held_item_begging`

Social-place filters:

- `block_party:if_social_place`
- `block_party:if_social_place_share`
- `block_party:if_social_place_orbit`
- `block_party:if_social_place_guard`
- `block_party:if_social_place_avoid`
- `block_party:social_place_behavior`
- `block_party:social_place_type`
- `block_party:social_place_distance`
- `block_party:social_place_owner_name`

These filters are intentionally descriptive rather than script-like. If a scene
needs a new world scan or scoring rule, add that rule in Java as a small
observation primitive, then expose one filter ID here.

## Action Forms

Actions may be strings or objects.

String actions currently only support `block_party:end`. Unknown action IDs and
non-`end` string actions fail scene parsing with an author-oriented error.

```json
"block_party:end"
```

Object actions use `type` plus either inline fields or an `action` payload:

```json
{
  "type": "block_party:set_routine_intent",
  "action": {
    "intent": "worship"
  }
}
```

Unknown object action types fail scene parsing with an author-oriented error.
When an object action includes an `action` field, that field must be an object
payload.

## Dialogue Action

`block_party:send_dialogue` opens dialogue for the target player and waits for a
response.

```json
{
  "type": "block_party:send_dialogue",
  "action": {
    "text": "The gate remembers you.",
    "tooltip": true,
    "sound": "block_party:moe.say",
    "speaker": {
      "identity": "character",
      "position": "left",
      "animation": "wave",
      "emotion": "normal",
      "speaks": false,
      "voice": "block_party:moe.say",
      "scale": 1.0
    },
    "responses": [
      {
        "icon": "block_party:next_response",
        "text": "Listen",
        "actions": [
          "block_party:end"
        ]
      }
    ]
  }
}
```

Fields:

- `text`: dialogue text. Defaults to `""`.
- `tooltip`: boolean. Defaults to `false`.
- `sound`: optional sound ID.
- `speaker`: optional speaker object.
- `responses`: optional response array.

Speaker fields:

- `identity`: `character` default, or `narrator`
- `position`: `left` default, `center`, or `right`
- `animation`: string, default `DEFAULT`
- `emotion`: string, default `NORMAL`
- `speaks`: boolean, default `false`
- `voice`: optional voice/sound ID used only when `speaks` is true
- `scale`: float, default `1.0`

Response icon values:

- `block_party:green_checkmark`
- `block_party:red_x`
- `block_party:chat_bubble`
- `block_party:lovely_heart`
- `block_party:trusty_armor`
- `block_party:stressful_skull`
- `block_party:leather_bag`
- `block_party:anvil`
- `block_party:next_response`
- `block_party:close_dialogue`
- `block_party:open_dialogue`

Response fields:

- `icon`: response icon. Defaults to `block_party:close_dialogue`.
- `text`: optional response text.
- `actions`: actions to queue when this response is chosen.

## State Actions

Stat actions:

```json
{
  "type": "block_party:loyalty",
  "action": {
    "operation": "add",
    "value": 1
  }
}
```

Supported stat action types:

- `block_party:health`
- `block_party:food_level`
- `block_party:loyalty`
- `block_party:stress`

Stat operations:

- `add` default
- `subtract`
- `set`

Cookie action:

```json
{
  "type": "block_party:cookie",
  "action": {
    "operation": "set",
    "name": "temple_awakened",
    "value": "true"
  }
}
```

Cookie operations:

- `set` default
- `delete`

Counter action:

```json
{
  "type": "block_party:counter",
  "action": {
    "operation": "add",
    "name": "bell_warning_count",
    "value": 1
  }
}
```

Counter operations:

- `add` default
- `subtract`
- `set`
- `delete`

Cookie and counter actions default to `npc` scope. Use `"scope": "player"` or
`"scope": "world"` on `block_party:cookie` and `block_party:counter`, or use the
scoped action IDs:

- `block_party:player_cookie`
- `block_party:player_counter`
- `block_party:world_cookie`
- `block_party:world_counter`

## Movement And Routine Actions

Start follow session:

```json
{
  "type": "block_party:start_follow_session",
  "action": {
    "intent": "party_invite",
    "ticks": 1200,
    "can_change_dimension": false,
    "trigger_scene": false
  }
}
```

Fields:

- `intent`: follow intent. Defaults to `follow_request`.
- `ticks`: duration in ticks. Defaults to `1200`.
- `can_change_dimension`: boolean. Defaults to `false`.
- `trigger_scene`: boolean. Defaults to `false`.

Set routine intent:

```json
{
  "type": "block_party:set_routine_intent",
  "action": {
    "intent": "worship"
  }
}
```

Other movement/routine actions:

```json
{ "type": "block_party:clear_routine_intent" }
{ "type": "block_party:go_to_anchor", "action": { "speed": 1.0 } }
{ "type": "block_party:set_home_to_anchor" }
{ "type": "block_party:clear_follow_session" }
{ "type": "block_party:wait" }
{ "type": "block_party:dismiss" }
```

Notes:

- `clear_routine_intent` sets the Moe back to `idle`.
- `go_to_anchor` moves toward the current routine anchor at `speed`.
- `set_home_to_anchor` copies the current routine anchor into the Moe home.
- `clear_follow_session`, `wait`, and `dismiss` all clear the active follow
  session.

## Inventory And Item Actions

Open Moe inventory:

```json
{
  "type": "block_party:open_inventory"
}
```

`open_inventory` opens the active Moe's inventory for the dialogue target, or the
Moe owner when no dialogue target is set.

Give an item:

```json
{
  "type": "block_party:give_item",
  "action": {
    "item": "minecraft:cookie",
    "count": 3,
    "target": "player"
  }
}
```

Fields:

- `item`: item ID. Required for a non-empty stack.
- `count`: item count. Defaults to `1`.
- `target`: `player` default, or `moe`.

Take an item:

```json
{
  "type": "block_party:take_item",
  "action": {
    "item": "minecraft:apple",
    "count": 2,
    "source": "player",
    "destination": "moe"
  }
}
```

Fields:

- `item`: item ID or item tag when matching inventory contents.
- `count`: item count. Defaults to `1`.
- `source`: `player` default, or `moe`.
- `destination`: `moe` default, `player`, or `discard`.

The default `take_item` behavior is gift-like: remove the item from the player
and store it in the Moe inventory. If the Moe inventory cannot hold the item,
the action does not remove it from the player.

## Hide And Sleep Actions

Hide:

```json
{
  "type": "block_party:hide",
  "action": {
    "until": "one_second_passes"
  }
}
```

Sleep at home:

```json
{
  "type": "block_party:sleep_at_home",
  "action": {
    "until": "exposed"
  }
}
```

Hide-until values:

- `exposed` default
- `one_second_passes`

`sleep_at_home` only succeeds when the Moe can sleep at its home position.

## Voicemail Action

```json
{
  "type": "block_party:create_voicemail",
  "action": {
    "text": "The gate is quiet. I will wait.",
    "tooltip": true,
    "speaker": {
      "identity": "character",
      "emotion": "normal"
    },
    "sound": "block_party:moe.say",
    "delay_minutes": 60
  }
}
```

Fields:

- `text`: voicemail dialogue text. Defaults to `""`.
- `tooltip`: boolean. Defaults to `true`.
- `speaker`: optional speaker object.
- `sound`: optional sound ID.
- `delay_seconds`: optional delay, preferred for short delays.
- `delay_minutes`: optional delay. Defaults to `60` when `delay_seconds` is
  absent.

## Social Affinity Shape

Social affinities live in `data/<namespace>/moes/social_affinities/*.json`.

```json
{
  "rules": [
    {
      "observer": {
        "block_tag": "minecraft:logs",
        "dere": "dandere"
      },
      "target": {
        "block": "minecraft:netherrack"
      },
      "affinity": 0.0,
      "tension": 0.35,
      "interest": 0.3
    }
  ]
}
```

Matcher fields for `observer` and `target`:

- `block`
- `block_tag`
- `blood_type`
- `dere`
- `zodiac`
- `gender`
- `emotion`

Signal fields:

- `affinity`
- `tension`
- `interest`

Signals are added to built-in blood-type signals and clamped between `0.0` and
`1.0`.

Malformed social affinity files fail closed so one bad datapack file does not
break reload.

## Authoring Guidelines

- Prefer `block_party:*` IDs.
- Use one scene per mechanic beat. Let responses chain beats together.
- Choose cookie/counter scope intentionally: `npc` for one Moe, `player` for one
  player's progression, and `world` for shared progression.
- Use filters to protect one-time scenes, for example a counter equal to `0`.
- Use routine intents and anchors for movement-like behavior before adding Java.
- Add a tiny scene first, then expand it once it fires reliably in game.
- Treat unknown filters as disabled content and unknown actions as ending the
  scene.

## Current Limitations

- There is no formal JSON Schema file yet.
- Scene parse errors can still be runtime-facing rather than content-author
  diagnostics.
- Unknown object actions become `end`.
- Unknown filters fail closed.
- Passive spawning, boss progression, and structure recognition should call into
  this scene surface through small Java primitives rather than one-off JSON
  hacks.
