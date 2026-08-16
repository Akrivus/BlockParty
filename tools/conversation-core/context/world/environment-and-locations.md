# Environment And Named Locations

Scene selectors may establish factual context about time and place. Supported
environment concepts include Minecraft time periods, exact or wrapped day-time
ranges, clear/rain/thunder weather, dimensions, biome IDs or biome tags,
altitude, sky visibility, light level, and nearby blocks. Treat locked selector
values as true for the scene, but do not redundantly announce every selector in
dialogue.

Named scene locations are explicit narrative bookmarks containing a dimension
and block position. NPC scope is a Moe's personal memory, player scope belongs
to the current player relationship, and world scope is shared. A scene may only
refer to a named location after content has recorded it with
`REMEMBER_LOCATION`; dialogue alone never records movement or a place. Named
locations are distinct from inferred place memories such as a garden, shelter,
or shrine.

`REMEMBER_LOCATION` may capture the Moe, active player, home, current routine
anchor, or remembered place. Scene assignments can then direct a Moe toward a
named location or toward an owner, dialogue player, social target, or nearby
Moe. Assigning movement is not the same as arriving: use assignment status
filters and assignment arrival/failure triggers for follow-up content.
Assignments never imply teleportation or cross-dimensional portal travel.

For autonomous routines, use `routine_tick` with environmental/personality
filters and scene selection metadata for group, weight, and cooldown. Movement
to a block uses `ASSIGN_NEAR_BLOCK`; the runtime resolves one valid nearby block
and standing position instead of changing targets continuously. Use timed wait,
animation, emotion, pose, and look actions for visible staging rather than
writing bracketed stage directions into dialogue. Assignment IDs and terminal
results connect departure cards to the correct arrival or failure cards.
