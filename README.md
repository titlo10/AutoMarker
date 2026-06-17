# AutoMarker

<p align="center">
  <img src="src/main/resources/assets/automarker/icon.png" alt="AutoMarker Logo" width="128" height="128" />
</p>

<p align="center">
  <strong>A Fabric mod that drops markers on ReplayMod timelines when in-game events happen.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.20%2B-blue?style=for-the-badge&logo=minecraft" alt="Minecraft Version" />
  <img src="https://img.shields.io/badge/Loader-Fabric-lightgrey?style=for-the-badge&logo=appveyor" alt="Fabric Loader" />
  <img src="https://img.shields.io/badge/License-GPL--3.0-green?style=for-the-badge" alt="License" />
</p>

---

AutoMarker hooks into ReplayMod and tags your timeline as things happen. Beats scrubbing through a three-hour recording to find the death, advancement, or chat message you actually care about.

## Features

- **Player Deaths** — Drops a "Player Died" marker when you die.
- **Advancements** — Drops an "Advancement: [Name]" marker on completion. Multiple advancements earned at once are merged into a single marker.
- **Dimension Changes** — Drops a "Dimension: [Name]" marker when you cross between worlds (Nether, End, etc.).
- **Chat Keywords** — Drops a "Chat: [Keyword]" marker when specific words hit chat. Good for stream alerts, raid calls, or anything else worth bookmarking. Matching is case-insensitive by default and debounced so a repeated keyword won't flood the timeline.

## Configuration

### In-Game GUI

Install Mod Menu, open AutoMarker's config from the mods list — or press **K** in-game. Toggle death/advancement/dimension markers, choose whether keyword matching ignores case, and set your chat keywords (comma-separated) from there.

### Config File

Settings are saved to `.minecraft/config/replaymod_auto_markers.json`:

```json
{
  "enableDeaths": true,
  "enableAchievements": true,
  "enableDimensionChanges": true,
  "chatKeywords": "marker, highlight, raid",
  "chatCaseInsensitive": true
}
```

## Requirements

- **Minecraft** 1.20+
- **Fabric Loader** ≥ 0.16.0
- **Fabric API**
- **ReplayMod**

Drop the compiled `.jar` into your `mods` folder.

## Building from Source

```bash
git clone https://github.com/titlo10/AutoMarker.git
cd AutoMarker
./gradlew bundleJar
```

Jars land in `build/libs` and `build/bundled`.

## License

GPL-3.0. See [LICENSE](LICENSE) for details.
