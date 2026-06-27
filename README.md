# Block Party
Block Party is a Minecraft mod that adds _moe_
personifications of every block. Each block person, or _Moe_,
has a profile that the player can use to gain favor with them.

Moes are affectionate, they bring food, pull pranks, perform
chores, and tag along with players on adventures depending on
the block that they personify.

**[Block Party! | Trello](https://trello.com/b/NLqxcyXI/block-party)**

## Plans
The mod is currently undergoing a renaissance after a four-year
hiatus, the original plans and current plans intersect but aren't
parallel.
- Bug fixes, feature assessment, planning.
- Animated mouths for expression and dialogue.
- LLM integration for data-gen.
- Upgrade to the newest Minecraft.

## Function
Moes use block tags to determine what blocks can become a Moe,
deciding the texture, render layers, and behavior. Moes create
a row in a database for global access, and the database is used
to save their favorite locations so they know to always return
or respawn at those locations.

They have expressive eyes that look around and blink. I don't
see any mouths so I may add them in for smiles and mouthing
out dialogue since the camera zooms in when you talk to them.

There's also a conversation system that is encoded in data packs,
which inspired me to use LLMs for data-gen, but it is already
pretty extensive on its own.

## Developer Checks

The active build uses Gradle tasks for local CI:

- `.\gradlew.bat phase1Compliance --no-daemon`: fast cleanup guardrail for inline FQCN and raw SQL table-name regressions.
- `.\gradlew.bat localCi --no-daemon`: fast pre-PR check, currently `phase1Compliance` plus `compileJava`.
- `.\gradlew.bat fullCi --no-daemon`: full local verification, including the active GameTest suite.
- `.\gradlew.bat installGitHooks --no-daemon`: configures Git to run the repo-managed pre-commit hook from `scripts/git-hooks`.

The pre-commit hook intentionally runs only `phase1Compliance`, so ordinary commits stay quick. Use `localCi` or `fullCi` before broader changes.

## Design Notes

- `docs/SAMURAI_ARC.md`: practical progression and lore constraints for the
  Samurai armor, Suzu, Crying Obsidian, torii, and cardinal Moe arcs.
