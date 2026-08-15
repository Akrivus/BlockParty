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

The default `recorded` provider reads responses from `recordedResponses`,
relative to the brief. For a live generation, set `provider` to `openai`, put
the desired model identifier in `model`, and expose `OPENAI_API_KEY` to the
process. The OpenAI integration is isolated behind `NarrativeModel`, uses the
Responses API's JSON output mode, and never writes the API key to an archive.
All provider output still passes through the local typed parser, mechanical
lock, validation, simulation, and compiler. CI never makes network calls.

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

## Iteration Four Workbench

The local Conversation Workbench is a card-based authoring surface over the
same Java core. Install its standalone distribution with:

For a complete prompt-to-export walkthrough, see
[`CONVERSATION_TOOL_TUTORIAL.md`](CONVERSATION_TOOL_TUTORIAL.md).

```powershell
.\gradlew.bat conversation-workbench:installDist
```

Launch it with a project file or an Iteration 3 generation directory:

```powershell
tools\conversation-workbench\build\install\conversation-workbench\bin\conversation-workbench.bat tools\conversation-core\examples\flower-request.project.json
```

The server binds only to the loopback interface and opens a browser locally.
Pass `--no-open` for a headless launch or `--port <port>` to choose a port.
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
  empty directory.

The browser never compiles or validates projects independently. Its local API
delegates those operations to `conversation-core`, preserving one definition
of the project format and generated datapack behavior. Projects with validation
errors can be explored and repaired, but cannot be saved or exported.

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

The **Review** panel displays the brief, token and call totals, per-stage model
metadata, archived requests and structured responses, review findings, and
scene intentions. A generated card's intention also appears in its inspector.

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
