# AutoMarker

[![Available on Modrinth](https://raw.githubusercontent.com/intergrav/devins-badges/7f68fe7afdbda525557fb41097594d2edccfda03/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/replaymod-automarker)
![Available for Fabric](https://raw.githubusercontent.com/intergrav/devins-badges/7f68fe7afdbda525557fb41097594d2edccfda03/assets/cozy/supported/fabric_vector.svg)

AutoMarker marks important moments as they happen, so you can find them quickly when reviewing a ReplayMod recording.

## Features

- Player deaths and PvP kills
- Totem of Undying activations
- Advancements
- Dimension changes
- Custom chat keywords

## Requirements

- Minecraft 1.20–1.21.11 or 26.1.x
- Fabric Loader 0.16.0+
- Fabric API
- ReplayMod

## Installation

Download AutoMarker from [Modrinth](https://modrinth.com/mod/replaymod-automarker) and place the `.jar` in your `mods` folder.

Press **K** in-game to configure marker types and chat keywords. The settings screen is also available through Mod Menu when installed.

## Building

```bash
git clone https://github.com/titlo10/AutoMarker.git
cd AutoMarker
./gradlew bundleJar
```

Built jars are placed in `build/libs`.

## License

[GPL-3.0](LICENSE)
