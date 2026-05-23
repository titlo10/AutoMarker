# 🎥 AutoMarker

<p align="center">
  <img src="src/main/resources/assets/automarker/icon.png" alt="AutoMarker Logo" width="128" height="128" />
</p>

<p align="center">
  <strong>Automatically adds markers to ReplayMod recordings in Minecraft when trigger events occur.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21%2B-blue?style=for-the-badge&logo=minecraft" alt="Minecraft Version" />
  <img src="https://img.shields.io/badge/Loader-Fabric-lightgrey?style=for-the-badge&logo=appveyor" alt="Fabric Loader" />
  <img src="https://img.shields.io/badge/License-GPL--3.0-green?style=for-the-badge" alt="License" />
</p>

---

## 📖 Description

**AutoMarker** is a utility Fabric mod for Minecraft, specifically designed for content creators, speedrunners, and players using **ReplayMod**. 

The mod automatically places markers on the ReplayMod timeline when specific in-game events occur. No need to manually log timestamps or spend hours searching for highlights in long recordings!

---

## ✨ Features

The mod currently supports automatic marker generation for the following events:

*   💀 **Player Deaths:** Every time you die, a marker named `"Player Died"` is placed on the recording. Perfect for capturing bloopers and fails.
*   🏆 **Advancements & Achievements:** Automatically drops a marker like `"Advancement: [Advancement Name]"` when you complete an advancement, allowing you to jump straight to milestone moments.
*   💬 **Chat Keywords:** The mod monitors the game chat. If a message contains any of your defined keywords (e.g., `marker`), it immediately places a marker like `"Chat: [Keyword]"`. This is extremely useful for:
    *   Stream/donation integration.
    *   Quickly bookmarks via typing in chat.
    *   Tracking specific server plugin logs.

---

## ⚙️ Configuration

You can customize the mod either via the in-game settings GUI or by editing the configuration file directly.

### 🎮 In-Game Settings GUI
1. Install **Mod Menu** for Fabric.
2. Go to the mods list, select **AutoMarker**, and click **Configure**.
3. Alternatively, press **`K`** (default hotkey) in-game to instantly open the configuration screen.
4. From the GUI, you can:
   * Toggle death markers on or off.
   * Toggle advancement markers on or off.
   * Enter comma-separated keywords for the chat listener (e.g., `marker, highlight, raid`).

### 📂 Configuration File
The settings are saved in your Minecraft directory under `.minecraft/config/replaymod_auto_markers.json`.

Example structure:
```json
{
  "enableDeaths": true,
  "enableAchievements": true,
  "chatKeywords": "marker, highlight, raid"
}
```

---

## 🚀 Requirements & Installation

To run this mod, you need:

1. **Minecraft** `1.21` or newer.
2. **Fabric Loader** (version `>=0.16.0`).
3. **Fabric API**.
4. **ReplayMod**.
5. **Mod Menu** *(recommended for GUI configuration)*.

Drop the compiled `.jar` file into your `mods` folder.

---

## 🛠️ Compilation (For Developers)

To build the mod from source code:

1. Clone this repository.
2. Open a terminal in the project directory and run:
   ```bash
   ./gradlew build
   ```
3. The built binaries will be located in the `build/libs` and `build/bundled` folders.

---

## 📄 License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**. See the [LICENSE](file:///home/titlo10/Desktop/mod/fabric-example-mod/LICENSE) file for details.
