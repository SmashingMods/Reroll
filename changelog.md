# Changes
- Add Curios support.
- Refactor capabilities to LazyOptional wherever they are required.

# Fixes
- Fix capabilities so that the reroll capability is only returned when it should be and not on every get capability call.
- When a player dies, persist their capabilities during clone event.
- Remove testing code in ServerMessageHandler and properly set cooldown.
- Improved ConfigHandler rerollItems array to check the array size of the split string.