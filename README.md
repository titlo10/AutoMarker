# 🎥 AutoMarker

<p align="center">
  <img src="src/main/resources/assets/automarker/icon.png" alt="AutoMarker Logo" width="128" height="128" />
</p>

<p align="center">
  <strong>Automatically places markers on ReplayMod recordings when in-game events happen.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21%2B-blue?style=for-the-badge&logo=minecraft" alt="Minecraft Version" />
  <img src="https://img.shields.io/badge/Loader-Fabric-lightgrey?style=for-the-badge&logo=appveyor" alt="Fabric Loader" />
  <img src="https://img.shields.io/badge/License-GPL--3.0-green?style=for-the-badge" alt="License" />
</p>

---

## 📖 What it does

AutoMarker is a Fabric mod that hooks into ReplayMod and drops timeline markers whenever something noteworthy happens in-game. No more scrubbing through hours of footage looking for that one moment.

---

## ✨ Features

*   💀 **Player Deaths** — places a `"Player Died"` marker every time you die. Good for bloopers, speedrun resets, or just knowing where it all went wrong.
*   🏆 **Advancements** — drops a `"Advancement: [Name]"` marker when you complete one. Jump straight to milestone moments without guessing timestamps.
*   💬 **Chat Keywords** — watches the chat for words you define. When a match appears, it places a `"Chat: [Keyword]"` marker. Handy for stream/donation alerts, quick manual bookmarks, or catching server plugin messages.

---

## ⚙️ Configuration

### 🎮 In-Game GUI

1. Install **Mod Menu** for Fabric.
2. Open the mods list, select **AutoMarker**, and click **Configure**.
3. Or press **`K`** in-game to open the config screen directly.

From there you can toggle death and advancement markers, and enter comma-separated keywords for the chat listener (e.g. `marker, highlight, raid`).

### 📂 Config File

Settings are saved to `.minecraft/config/replaymod_auto_markers.json`.

```json
{
  "enableDeaths": true,
  "enableAchievements": true,
  "chatKeywords": "marker, highlight, raid"
}
```

---

## 🚀 Requirements & Installation

1. **Minecraft** `1.21+`
2. **Fabric Loader** `>=0.16.0`
3. **Fabric API**
4. **ReplayMod**

Drop the `.jar` into your `mods` folder and you're done.

---

## 🛠️ Building from Source

```bash
git clone https://github.com/titlo10/AutoMarker.git
cd AutoMarker
./gradlew bundleJar
```

Output goes to `build/libs` and `build/bundled`.

---

## 📄 License

GPL-3.0. See [LICENSE](LICENSE) for details.
