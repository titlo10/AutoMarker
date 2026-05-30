# AutoMarker

<p align="center">
  <img src="src/main/resources/assets/automarker/icon.png" alt="AutoMarker Logo" width="128" height="128" />
</p>

<p align="center">
  <strong>Automatically places markers on ReplayMod recordings when in-game events occur.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.20%2B-blue?style=for-the-badge&logo=minecraft" alt="Minecraft Version" />
  <img src="https://img.shields.io/badge/Loader-Fabric-lightgrey?style=for-the-badge&logo=appveyor" alt="Fabric Loader" />
  <img src="https://img.shields.io/badge/License-GPL--3.0-green?style=for-the-badge" alt="License" />
</p>

---

## Description

AutoMarker is a Fabric mod that hooks into ReplayMod to place timeline markers during in-game events. This simplifies editing by helping you find key moments without searching through hours of footage.

---

## Features

- **Player Deaths** - Places a "Player Died" marker when you die.
- **Advancements** - Places an "Advancement: [Name]" marker upon completing any advancement.
- **Chat Keywords** - Places a "Chat: [Keyword]" marker when specified words appear in the chat. Useful for stream alerts, bookmarks, or tracking specific server messages.

---

## Configuration

### In-Game GUI

1. Install Mod Menu for Fabric.
2. Select AutoMarker in the mods list and click Configure, or press **K** in-game to open the configuration screen directly.

From the GUI you can toggle death/advancement markers and manage the comma-separated chat keywords.

### Config File

Settings are saved to `.minecraft/config/replaymod_auto_markers.json`:

```json
{
  "enableDeaths": true,
  "enableAchievements": true,
  "chatKeywords": "marker, highlight, raid"
}
```

---

## Requirements

1. **Minecraft** `1.20+`
2. **Fabric Loader** `>=0.16.0`
3. **Fabric API**
4. **ReplayMod**

To install, place the compiled `.jar` file into your `mods` folder.

---

## Building from Source

```bash
git clone https://github.com/titlo10/AutoMarker.git
cd AutoMarker
./gradlew bundleJar
```

Built jars are located in `build/libs` and `build/bundled`.

---

## License

This project is licensed under the GPL-3.0 License. See the LICENSE file for details.
