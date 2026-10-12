# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## 21.1.2 - 2026-10-11

### Added
  - `display_options_button` option in the `[client]` section of hats.toml. Set it to `false` to hide the "Hats" button on the Options screen, then restart the game. Existing config files don't get the new line automatically, so add `display_options_button = false` under `[client]` yourself. That button is the only way to open the Hat Placement Editor, so turn it back on if you need the editor.

### Fixed
  - `disabled_entities` and `hat_blacklist` were ignored when the list was spread across multiple lines.
  - Mobs that despawned kept their hat in the world save forever.
  - Hats no longer clips into view in first person when mods like Epic Fight or Better Combat render your body.

---

## 21.1.1 - 2026-07-12

### Added
  - Config option to stop specific entities from spawning with hats. Set `disabled_entities` in hats.toml to a list of full entity ids (e.g. "minecraft:zombie") or namespaces to exclude a whole mod (e.g. "minecraft").

---

## 21.1.0 - 2026-04-29

### Added
  - Trading
  - Around 22 new hats and plenty more accesories.
  - Support for multiloader(NeoForge/Fabric)
  - Ported to 1.21.1



