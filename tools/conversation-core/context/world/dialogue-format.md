# Dialogue Formatting And Dynamic Tokens

Scene dialogue supports a small formatting language. Use it sparingly and keep
the underlying line natural when formatting is removed.

- `*bold*` or `<b>bold</b>`
- `/italic/` or `<i>italic</i>`
- `_underlined_` or `<u>underlined</u>`
- `-struck-` or `<s>struck</s>`
- `<color=cyan>colored text</color>`; supported colors are black, navy, green,
  teal, maroon, purple, orange, silver, gray, blue, lime, cyan, red, magenta,
  yellow, and white.

Dialogue text may use these runtime substitutions:

- `@name`: the speaking Moe's given name.
- `@family_name`: the speaking Moe's family name.
- `@.name`: the current player's name.
- `@social.name`: the most interesting nearby Moe's given name.
- `@nearby.name`: the nearest nearby Moe's given name.
- `@nearby.names`: up to three nearby Moe given names, comma-separated.
- `@cookie_name`: the speaking Moe's cookie value.
- `@.cookie_name`: the current player's cookie value.
- `#counter_name`: the speaking Moe's counter value.
- `#.counter_name`: the current player's counter value.

Only use cookie and counter tokens declared by the pack. Name substitutions may
be empty when no matching player or nearby Moe exists, so write lines that still
make sense in that case or use them only in appropriately gated scenes.

Formatting works in response labels, but runtime substitutions do not. Do not
put `@` or `#` tokens in player response labels.

There are currently no runtime tokens for favorite items, favorite mobs, close
friends, or recent conversants. Do not invent tokens for those concepts.
