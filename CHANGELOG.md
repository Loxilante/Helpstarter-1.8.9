
# Changelog

## [v0.2.1] - 2026-01-01

### Added
- Username validation for `a|available` and `q|query` commands.

## [v0.2.0] - 2026-01-01

### Added
- Expanded `/hs` command set:
    - `/hs help` displays command usage and descriptions.
    - `/hs a|available [username]` prints available bots for a user.
    - `/hs q|query [username]` queries all bots and prints them grouped by status (Available / Busy / Unavailable).
- Optional `[username]` argument for availability/query commands.
- Tab completion for:
    - `1, 2, 3, a, q, available, query, help`
    - player name completion for relevant subcommands.

## [v0.1.0] - 2026-01-01

### Added
- Initial release of **HelpStarter**. Happy New Year!
- Implemented remote bot list fetching from API based on the player's username.
- Added `/hs <1|2|3>` client command to invite 1-3 bots to player's party automatically.