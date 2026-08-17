# Conversation Pack Tool

The conversation pack tool is a local authoring utility for Block Party
developers, server owners, modpack authors, and content creators. Its project
graph is the durable model intended to sit behind a later card-based editor.
It does not require Minecraft, NeoForge, an API key, or a running game.

## Authoring Model

Project format 2 separates the authoring graph from Block Party's nested scene
JSON. It includes:

- versioned Block Party target metadata;
- declared cookie and counter state with scope and optional bounds;
- required/provided state and named outcome contracts;
- typed conditions and actions;
- explicit `IMMEDIATE`, `LATER_INTERACTION`, `EXTERNAL_EVENT`, and `PACK_EXIT`
  transitions;
- dialogue, gameplay-gate, and ending cards;
- editor coordinates that do not affect compilation.

Supported typed conditions are `ALWAYS`, `HAS_COOKIE`, `COUNTER`, `HAS_ITEM`,
`HELD_ITEM`, `MOE_HAS_ITEM`, `BLOCK`, and `ELAPSED_TIME`. Supported typed actions
cover cookies, counters, item transfer, time markers, inventory opening, follow
sessions, and ending. Raw mechanics are an explicit opt-in escape hatch and
produce warnings because they cannot be deeply validated or simulated.

Dialogue-to-dialogue edges compile into nested `send_dialogue` actions.
Later-interaction and gameplay-gate transitions close the current dialogue.
Gameplay gates compile into separately triggered scenes so gameplay can occur
before the player returns.

## Commands

Install the local command-line distribution:

```powershell
.\gradlew.bat :tools:conversation-core:installDist
```

The executable is generated at:

```text
tools/conversation-core/build/install/conversation-core/bin/conversation-core.bat
```

Commands:

```text
validate <project.json>
simulate <project.json> [scenario.json]
graph <project.json> <output.mmd>
compile <project.json> <empty-output-directory>
build <project.json> <empty-output-directory>
explain <project.json> <node-id>
catalog <brief.json> <catalog.json>
generate <brief.json> <empty-output-directory>
replay <previous-generation-directory> <empty-output-directory>
```

`build` is the normal creator workflow. It writes a standalone datapack,
Mermaid graph, JSON validation and simulation reports, and a readable Markdown
route report. Compilation and build both refuse non-empty output directories.

`explain` summarizes a card's incoming routes, conditions, effects, and targets.
The same information can later populate a web editor's card inspector.

The main example is
`tools/conversation-core/examples/flower-request.project.json`. The optional
`flowers-ready.scenario.json` supplies inventory state for scenario simulation.
`repeatable-reward.invalid.json` is deliberately unsafe and proves that an
unguarded reward is rejected.

Run the complete deterministic verification workflow with:

```powershell
.\gradlew.bat :tools:conversation-core:conversationToolCheck
```

Generated verification output is written below
`tools/conversation-core/build/tool-check/` and is ignored by Git.

## Iteration Three Generation

Iteration three adds a local, provider-neutral generation pipeline in front of
the same typed project model. A generation brief supplies the creative prompt,
character and repository-document scope, card-count bounds, mechanics
allowlists, response cues, model-call budget, and provider configuration. See
`tools/conversation-core/examples/generation/flower-friendship.brief.json`.

The pipeline performs bounded stages in this order:

1. arc plan;
2. typed conversation graph;
3. validation-driven graph repair (at most two attempts);
4. per-card scene intentions;
5. dialogue writing with mechanics locked;
6. final review, validation, simulation, reports, and datapack compilation.

Every request and response is archived under the output's `generation/`
directory alongside the normalized brief, bounded repository catalog, final
project, build reports, datapack, and generation manifest. `replay` consumes
that archive without contacting a model and must reproduce the project and
datapack byte-for-byte. The deterministic fixture deliberately starts with an
invalid graph so CI exercises the repair path.

The optional `recorded` provider reads responses from `recordedResponses`,
which is required only for that provider and resolved relative to the brief.
For a live generation, set `provider` to `openai`, omit `recordedResponses`, put
the desired model identifier in `model`, and expose `OPENAI_API_KEY` to the
process. The OpenAI integration is isolated behind `NarrativeModel`, uses the
Responses API's JSON output mode, and never writes the API key to an archive.
All provider output still passes through the local typed parser, mechanical
lock, validation, simulation, and compiler. CI never makes network calls.

Generation review is a publication gate. High-severity findings leave the
generated project, review, provenance, and reports available for editing, but
the pipeline does not compile a datapack. The CLI `compile` and `build`
commands, batch compilation/live installation, and both workbench export modes
also refuse a project whose adjacent `review.json` still contains a
high-severity finding. Medium and low findings remain advisory.

Repository cataloging only reads paths explicitly listed by the brief. Each
document is normalized, hashed, and capped at 24,000 characters so prompts do
not silently absorb an entire modpack or repository.

## Validation and Simulation

Validation currently detects:

- unsupported project targets and versions;
- duplicate or invalid card and state identifiers;
- undeclared or mistyped state references;
- invalid cues and resource identifiers;
- broken links and missing endings;
- immediate transitions into gameplay gates;
- raw mechanics without explicit authorization;
- state that is written but never read, or read but never established;
- mismatched pack contracts and undeclared outcomes;
- statically out-of-bounds counter assignments;
- potentially ambiguous root scenes sharing a trigger;
- reward paths without a negative completion guard set before reward delivery.

Simulation applies cookie, counter, inventory, and item-reward effects to each
branch. It records gameplay requirements, state changes, routes, endings, and
cycles. Inventory requirements absent from the starting scenario are treated as
external gameplay events and appear in the report.

The compiler owns namespacing. A local state such as `quest_completed` becomes:

```text
<namespace>.<pack-id>.quest_completed
```

Generated manifests include project/target format information, the pack
contract, and a SHA-256 hash of the normalized source project.

## Deferred Work

Modpack scanning and runtime AI remain separate later projects. Generation
currently produces multiple distributable scene packs rather than one
unbounded conversation.

## Conversation Workbench

The local Conversation Workbench is a card-based authoring surface over the
same Java core. From the repository, the default launch is:

For a complete prompt-to-export walkthrough, see
[`CONVERSATION_TOOL_TUTORIAL.md`](CONVERSATION_TOOL_TUTORIAL.md).

```powershell
.\gradlew.bat workbench
```

The repository-level `.\workbench.bat` launcher provides the same start screen.
It offers **New from prompt**, **New blank pack**, **Open existing pack**, and
device-local recent packs. New projects default to
`authoring\<pack-id>\project.json`; verified exports default to
`dist\<pack-id>`.

Use `.\gradlew.bat installConversationTools` when standalone distributions are
needed. `.\gradlew.bat checkConversationTools` runs the complete tool-specific
verification set. Fully qualified `:tools:conversation-workbench:*` task paths
remain available for CI and focused development.

The server binds only to the loopback interface and opens a browser locally.
The standalone or repository batch launcher still accepts a project path,
`--no-open`, and `--port <port>` for direct and headless launches.
It does not require Minecraft, Node, a hosted service, or an API key.

The workbench provides:

- a draggable card canvas with dialogue, gameplay-gate, and ending routes;
- card search and type filters;
- Java-schema-driven condition and action controls, including response actions;
- project metadata, state declarations, contracts, dialogue, response,
  transition, and ending editing;
- direct card connections and automatic graph arrangement;
- undo/redo and unsaved-change protection;
- device-local crash recovery and remembered simulation scenarios;
- debounced validation with diagnostics linked to affected cards;
- scenario-based route simulation with step-by-step traces and external
  requirements;
- atomic validated saves back to the opened `project.json`;
- verified export of the source project, graph, reports, and datapack to an
  empty directory;
- optional live-resource export of one pack directly to
  `src/main/resources/data/block_party/scenes/<pack-id>` for `/reload` testing.
  This unchecked developer option replaces only the selected pack directory;
  standalone export remains the recommended choice for distributable packs.

The browser never compiles or validates projects independently. Its local API
delegates those operations to `conversation-core`, preserving one definition
of the project format and generated datapack behavior. Projects with validation
errors can be explored and repaired, but cannot be saved or exported.

## Filter-driven batch authoring

Iteration 9 adds deterministic CLI batches for routine and ambient dialogue.
A versioned `*.batch.json` defines prompt families, reusable selector tags,
optional `each` or explicit `product` matrices, and variation counts. Expansion
creates one editable `brief.json` and `project.json` per deterministic job under
`jobs/<job-id>`; model transcripts and compiled intermediates stay in the
ignored `generated` directory.

```powershell
$tool = "tools\conversation-core\build\install\conversation-core\bin\conversation-core.bat"
& $tool batch plan authoring\routines\resting\resting.batch.json
& $tool batch expand authoring\routines\resting\resting.batch.json
& $tool batch generate authoring\routines\resting\resting.batch.json --resume
& $tool batch validate authoring\routines\resting\resting.batch.json
& $tool batch compile authoring\routines\resting\resting.batch.json dist\resting
& $tool batch install authoring\routines\resting\resting.batch.json --live
```

From a repository checkout, installation is optional. The root Gradle shorthand
builds and runs the CLI directly:

```powershell
.\gradlew.bat tool -Pargs="batch plan authoring\routines\resting\resting.batch.json"
```

For scripts that should avoid Gradle property quoting, set
`BLOCK_PARTY_CONVERSATION_ARGS` and run the same task. With no arguments,
`tool` prints CLI help.

Batch selectors use validated `SCENE_FILTER` project conditions rather than raw
mechanics. The CLI locks selectors onto generated root scenes, while the
workbench exposes their JSON for intentional author edits. `--resume` never
overwrites an existing project; targeted replacement requires an exact
`--only <job-id> --force` selection. Live installation stages compilation and
replaces only pack directories owned by the batch.

Environment selectors may use `time_period`, `weather`, `dimension`, `biome`,
`altitude`, `can_see_sky`, `light_level`, `near_block`, and named-location
filters. Locked selectors are supplied to the generation prompt as factual
scene context, so generated dialogue can assume the selected time and place
without redesigning those mechanics.

Projects may capture named locations from a Moe, player, home, anchor, or
remembered place; assign a Moe to a named location or supported entity target;
clear that assignment; and branch on assignment lifecycle filters and
triggers. Generated dialogue treats assignment as intent and does not claim
arrival until an `assignment_arrived` scene runs.

Routine cards can use selection group, weight, and cooldown fields in the
Workbench. Iteration 12 actions expose nearby block destinations, assignment
result consumption, timed waits, temporary animation/emotion, simple poses,
and assignment look targets. The project graph remains the behavior sequence;
the tool does not create a second route or behavior-tree format.

See `tools/conversation-core/examples/batch/resting.batch.json` for the current
OpenAI authoring example. `resting-ci.batch.json` is the deliberately small
recorded-provider fixture used only by deterministic verification.

## Iteration Six Generation Studio

The workbench's **Generate** panel turns an authoring brief into a bounded scene
pack without leaving the local tool. Creators can edit the prompt, characters,
repository documents, card bounds, dialogue limit, provider, model, call
budget, recorded-response directory, and output directory. **Preview catalog**
shows the exact bounded repository documents and hashes before any provider
call.

Generation runs asynchronously through catalog, arc-plan, graph, optional graph
repair, intentions, dialogue, review, and build stages. The workbench polls
local progress while Java retains ownership of budgets, validation, repair,
simulation, compilation, and archives. When complete, the generated project
becomes the open workbench project.

Minimum and maximum card counts are generation targets, not correctness gates.
A mechanically complete graph outside the requested range is retained with a
`CARD_COUNT_OUTSIDE_TARGET` warning instead of receiving filler cards or being
discarded solely because of its size.

The generation brief's dialogue-character limit is enforced after prose
generation, with one rewrite attempt for oversized lines. Response labels are
limited to 64 characters. A separate dialogue-style direction controls voice
without mixing presentation preferences into the world-context documents.

The **Review** panel displays the brief, token and call totals, per-stage model
metadata, archived requests and structured responses, review findings, and
scene intentions. A generated card's intention also appears in its inspector.
High-severity review findings also appear in the normal **Checks** panel and
mark their associated cards. They block compilation and export, but never
prevent opening or editing the source project. After correcting the project,
use **Re-run review** to save the edited source, replace the stale `review.json`,
and unlock export when no high-severity findings remain. Manual review calls
are archived alongside the original generation stages.

**Revise** requests two or three prose alternatives for the selected card.
Alternatives are not applied automatically. Java checks response count, applies
only dialogue text and optional response labels, compares the complete project
mechanics fingerprint, and validates the result before returning it to the
editor. Accepted revisions remain an ordinary undoable unsaved edit. Requests
and responses are archived beneath `generation/revisions/` when a generation
archive is available.

The `recorded` provider keeps generation and revision tests deterministic and
offline. The `openai` provider reads `OPENAI_API_KEY` only in the Java process;
the browser, project, and archive never receive the key.

## Iteration Eight World Context

Generation briefs can identify block or character subjects. With automatic
context enabled, the standalone tool reads Block Party block-tag JSON directly,
resolves nested tag membership, and adds compact world, Moe-identity, trait,
and optional block-profile prompts to the catalog. Minecraft and NeoForge do
not need to be running.

The built-in context manifest maps trusted Block Party tags to versioned prompt
documents. Dere and blood-type conflicts use the same precedence as the game
and appear as preview warnings. Unknown tags remain visible but never inject
arbitrary prompt text. Generation archives the resolved bundle as
`context.json`; replay reuses the archived catalog so later tag changes cannot
alter a recorded run.

## Iteration Thirteen Mechanics-Aware Generation

Explicit environmental and behavioral wording is treated as mechanics rather
than flavor. The graph stage receives the scene-filter catalog, valid enum
values, action fields, and asynchronous assignment patterns. Inferred
`SCENE_FILTER` conditions remain in the project when batch-locked selectors are
applied instead of being discarded.

High-confidence omissions—such as requested time, weather, idle routine state,
travel, block targets, animation, waiting, or returning home—participate in
graph repair and are archived in `mechanics-audit.json`. Movement remains
event-driven: post-arrival staging belongs on an `assignment_arrived` card
keyed to the assignment ID, not immediately after the departure action.
