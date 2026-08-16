# Conversation Tool Tutorial

This tutorial takes a scene pack from source project to exported Block Party
datapack. It uses the included flower-request example, requires no API key, and
does not require Minecraft to be running.

Run commands from the BlockParty repository directory unless a step says
otherwise. PowerShell examples use Windows paths.

## What the tools produce

There are three layers:

1. A generation brief describes the story, constraints, allowed mechanics, and
   model provider.
2. A `project.json` is the editable card graph and durable source file.
3. An exported datapack contains the Block Party scene JSON used by the game.

Keep the project or generation directory as source. The exported datapack is a
build artifact and can always be regenerated.

## 1. Launch the workbench

```powershell
.\gradlew.bat workbench
```

The repository-level launcher is equivalent:

```powershell
.\workbench.bat
```

The tool builds as needed, starts a loopback-only server, and opens its start
screen in the default browser. The terminal stays occupied while it runs;
press `Ctrl+C` there when finished.

The start screen provides four paths:

- **New from prompt** creates a source project and opens Generation Studio;
- **New blank pack** creates a minimal valid introduction and ending;
- **Open existing pack** accepts `project.json` or a generation directory;
- **Recent packs** remembers paths in this browser.

New source projects default to:

```text
authoring\<pack-id>\project.json
```

The location can be changed under **Advanced location** before creation.

## 2. Open the included example

```powershell
.\workbench.bat tools\conversation-core\examples\flower-request.project.json
```

Avoid saving changes to the checked-in fixture. To make a durable working copy,
create a blank pack from the start screen or copy the fixture into `authoring`.

If a browser should not open automatically:

```powershell
.\workbench.bat tools\conversation-core\examples\flower-request.project.json --no-open
```

Open the local address printed in the terminal yourself. Add `--port 18765` if
you need a fixed port.

Direct blank creation remains available for automation:

```powershell
.\workbench.bat --new authoring\my-pack\project.json
```

`--new` refuses to overwrite an existing path. Use the Project settings panel
to replace the starter pack identity, contract, and entry information.

## 3. Read the graph

The center canvas contains one card per conversation state:

- coral cards are dialogue shown to the player;
- green cards are gameplay gates checked on a later interaction;
- gold cards are endings;
- solid connections are immediate dialogue transitions;
- dashed connections leave the current dialogue or wait for gameplay.

Drag cards to organize the canvas. Card positions are saved as editor metadata
and do not alter compiled dialogue behavior.

Use the left-side search and type buttons to narrow a larger pack. Selecting a
card in the outline or canvas opens its inspector on the right.

## 4. Edit a card

Select the `introduction` card and change its dialogue text. The workbench marks
the project as unsaved and validates it after a short delay.

Player responses contain:

- **Label:** optional phrase displayed with the response;
- **Cue:** the Block Party response icon or cue identifier;
- **Target:** the destination card ID;
- **Transition:** when that destination becomes active.

Each dialogue card supports at most three player responses, matching the in-game
dialogue UI. AI generation keeps the first three if a model returns more.

The common transition choices are:

- `IMMEDIATE` — nest the destination dialogue in the current interaction;
- `LATER_INTERACTION` — end now and make the next scene available later;
- `EXTERNAL_EVENT` — wait for gameplay or state outside the current dialogue;
- `PACK_EXIT` — leave the scene pack.

Conditions and actions use typed controls generated from the Java authoring
schema. Choose a primitive type, then fill only the fields that apply to it.
State fields offer the project's declared state, while scope, comparison, and
counter-operation fields use enumerated choices. Raw mechanics retain a JSON
field because their structure is intentionally outside the typed model.

Response actions use the same controls inside each response.

Use **New card** to add a dialogue card. Card IDs use lowercase letters,
numbers, and underscores. Renaming an ID updates its known incoming links.
Deleting a linked card intentionally leaves validation errors so the broken
routes are visible and can be repaired explicitly.

Select a card and choose **Connect**, then select its destination. Dialogue
cards receive a new immediate player response; gameplay and ending-style cards
use their next-card route. Choose **Arrange** to lay out cards by their distance
from the project entry.

Choose **Project** to edit pack identity, namespace, entry card, declared state,
required/provided state, named outcomes, and raw-mechanics policy. Renaming a
state there refactors typed condition and action references.

`Ctrl+Z` undoes, `Ctrl+Shift+Z` redoes, and `Ctrl+S` saves.

## 5. Use validation feedback

The Checks section displays errors and warnings. Select a diagnostic to focus
its card.

The dialogue card's **Formatting and dynamic text** section lists the markup
and runtime substitutions available to authors. Automatic generation receives
the same reference as mandatory context, so it may reuse a scene across Moe or
player state without inventing unsupported placeholder names.

Errors block saving and export. Typical examples include:

- a response targeting a card that does not exist;
- a missing ending or pack outcome;
- state used without a declaration;
- an immediate transition into a gameplay gate;
- a reward route without a completion guard.

Warnings identify suspicious but potentially intentional behavior, such as
two root scenes that may match the same interaction.

## 6. Simulate routes

Choose **Simulate**. The three scenario fields accept JSON objects:

```json
{
  "quest_accepted": true
}
```

```json
{
  "friendship": 2
}
```

```json
{
  "#minecraft:small_flowers": 3
}
```

These represent cookies, counters, and inventory respectively. Empty scenarios
use `{}`.

Run the simulation to see route counts, endings, cycles, card traces, and
external requirements. A missing inventory requirement is treated as gameplay
that happens between conversations rather than a simulator failure.

Select a route and use **Previous** and **Next** to step through every entered
card, player choice, state change, external acquisition, and ending. **Remember
scenario** keeps the scenario in this browser for the next session.

## 7. Save and export

**Save project** atomically replaces the opened source file after validation.
It never writes a partially valid project.

While a project is unsaved, the browser keeps a device-local recovery copy. If
the workbench or browser closes unexpectedly, reopening the same source path
offers to restore it. A successful save removes the recovery copy.

Choose **Export** after the project is clean. The output directory is prefilled
as:

```text
<directory where the workbench was launched>\dist\<pack-id>
```

For the example, launching from the repository directory proposes:

```text
C:\path\to\BlockParty\dist\flower_request
```

You may edit this path or copy a directory path from Windows Explorer. The
target must either not exist or be an empty directory; this prevents accidental
overwrites.

For faster in-game iteration, select **Export into live mod resources**. This
developer-only option writes just the compiled scene JSON directly to
`src\main\resources\data\block_party\scenes\<pack-id>`. Re-exporting replaces
only that pack directory, after which `/reload` can pick up the changes in a
running development world.

The option is off by default. Datapack and modpack creators should normally
leave it off because live export does not create a standalone distributable
pack, and its output lives inside the mod source tree where it can appear as a
source-control change.

Successful export creates:

```text
flower_request\
├── project.json
├── graph.mmd
├── reports and route summaries
└── datapack\
    ├── pack.mcmeta
    └── data\...
```

Install or distribute the `datapack` directory. Retain `project.json` for later
editing.

## 8. Validate and build without the workbench

The CLI provides the same authoritative operations for automation:

```powershell
$tool = "tools\conversation-core\build\install\conversation-core\bin\conversation-core.bat"
& $tool validate authoring\flower-request.project.json
& $tool simulate authoring\flower-request.project.json
& $tool build authoring\flower-request.project.json authoring\flower-build
```

`build` and workbench export both refuse non-empty output directories.

## 9. Generate a project from a brief

Iteration 3 generation is optional. The included fixture uses recorded model
responses, so it is deterministic and free:

```powershell
$tool = "tools\conversation-core\build\install\conversation-core\bin\conversation-core.bat"
& $tool generate tools\conversation-core\examples\generation\flower-friendship.brief.json authoring\generated-flower
```

Open the resulting generation directory directly:

```powershell
tools\conversation-workbench\build\install\conversation-workbench\bin\conversation-workbench.bat authoring\generated-flower
```

The workbench finds `project.json` inside it. The directory also retains the
brief, bounded content catalog, stage requests and responses, review, reports,
and generated datapack.

To prove an archived generation is reproducible without contacting a model:

```powershell
& $tool replay authoring\generated-flower authoring\replayed-flower
```

For a live generation, set the brief's provider to `openai`, choose its model,
and provide `OPENAI_API_KEY` in the launching process. The key is never written
to the generation archive.

## 10. Generate and review in the workbench

Choose **Generate** in the top toolbar. Fill in the creative prompt and card
bounds, then list only the repository documents that should become model
context. Use **Preview catalog** before generation to inspect each included
path, character count, and content hash.

Set **Max dialogue characters** to the largest line the in-game speech bubble
should display; new briefs default to 160. The dialogue stage rejects longer
lines and rewrites them once, and response-button labels are capped at 64
characters. **Dialogue voice** is a free-form style direction. Its default is
concise, playful internet-anime banter, and it can be replaced for a specific
pack without changing world or character context.

Set a primary block subject such as `minecraft:crying_obsidian` to include
world rules and applicable block traits automatically. The preview groups
mandatory world rules, tag-derived traits, block profiles, and creator-selected
documents and reports conflicting exclusive tags. Turn off automatic context
only when intentionally testing a context-free brief.

The recorded flower fixture uses:

```text
Provider: recorded
Model: fixture
Recorded responses: tools/conversation-core/examples/generation/responses/flower
```

Choose a new empty output directory and start generation. The stage display
advances through arc planning, graph creation and repair, intentions, dialogue,
review, and completion. A successful run automatically opens its generated
project.

Choose **Review** to inspect the generation brief, usage totals, archived stage
requests and responses, and final review. Selecting a generated dialogue card
also shows the temporary scene intention that guided its prose.

## 11. Request a mechanics-locked revision

Select a dialogue card and choose **Revise**. Describe a prose change such as:

```text
Make this warmer and shorter, while preserving the meaning of every response.
```

Choose the recorded provider for the deterministic example or the OpenAI
provider for a live request. The alternatives panel shows proposed dialogue,
response labels when supplied, and a rationale. Nothing changes until **Accept
alternative** is selected.

Acceptance is server-checked. Only dialogue and response labels may change;
conditions, actions, state, rewards, targets, transitions, IDs, and contracts
must retain the same mechanics fingerprint. The accepted result is still
unsaved and can be undone before saving.

## 12. Install standalone tools and run checks

Build redistributable launchers when the tools need to run outside a repository
checkout:

```powershell
.\gradlew.bat installConversationTools
```

This creates the core and workbench launchers below `tools\...\build\install`.
Re-run the install task after changing Java code or browser assets.

Installation is not required inside the repository. Run a CLI command directly
through the root shorthand:

```powershell
.\gradlew.bat tool -Pargs="batch plan authoring\routines\resting\resting.batch.json"
```

Before committing tooling changes:

```powershell
.\gradlew.bat checkConversationTools
```

The checks cover valid and unsafe examples, simulation, compilation,
generation repair, archive replay, workbench resources, and workbench export.

## 13. Batch-generate routine dialogue

Use a batch specification when many low-stakes packs share a prompt family but
need different runtime filters. Start with a read-only preview:

```powershell
& $tool batch plan authoring\routines\resting\resting.batch.json
```

`plan` reports job, family, existing-project, and maximum-call counts without
writing files or contacting a provider. Then materialize versioned briefs and
generate only missing projects:

```powershell
& $tool batch expand authoring\routines\resting\resting.batch.json
& $tool batch generate authoring\routines\resting\resting.batch.json --resume
```

Open any `jobs\<job-id>\project.json` in the workbench for normal card editing.
The project is the source of truth; future resume runs skip it. Before export,
validate the complete set and either compile a standalone datapack or update
the mod's live resources:

```powershell
& $tool batch validate authoring\routines\resting\resting.batch.json
& $tool batch compile authoring\routines\resting\resting.batch.json dist\resting
& $tool batch install authoring\routines\resting\resting.batch.json --live
```

`batch install --live` is for development `/reload` testing. It replaces only
the batch projects' exact pack directories beneath Block Party's scene
resources.

## Troubleshooting

### The export directory is not empty

Choose a new pack directory or move the previous export elsewhere. Export does
not merge into or delete an existing directory.

### The workbench cannot save

Resolve red validation errors first. Warnings do not prevent saving.

### The browser did not open

Use the `http://localhost:<port>/` address printed in the terminal. The server
must remain running while the page is open.

### Changes to the UI do not appear

Stop and restart the workbench. If using an installed distribution, re-run
`installConversationTools` first because browser assets are packaged into it.

### NeoForm reports an `output.jar` access error

That Windows file-lock issue belongs to the Minecraft artifact build rather
than the standalone conversation tools. Re-running the affected Gradle command
after the lock clears normally reuses the completed artifact.
