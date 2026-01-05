# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SGui (Server GUI) is a Minecraft mod library enabling server-side GUI creation. It's a multi-loader port of the original [Patbox/sgui](https://github.com/Patbox/sgui) library supporting both Fabric and NeoForge.

- **Minecraft**: 1.21.5
- **Java**: 21
- **Mappings**: Official Mojang mappings

## Build Commands

```bash
# Build all platforms (requires JDK 21+)
./gradlew build

# Build specific platform
./gradlew :fabric:build
./gradlew :neoforge:build

# Run development client for testing
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

Build outputs go to `output/` directory with classifiers: `-fabric`, `-neoforge`.

## Architecture

### Multi-Loader Structure (Architectury)

```
common/     # Platform-agnostic core - all shared API and implementation
fabric/     # Fabric-specific entry point, mixins, and metadata
neoforge/   # NeoForge-specific entry point, mixins, and metadata
```

Each platform module shadows the common module into its JAR for standalone distribution.

### Core Components (`common/src/main/java/eu/pb4/sgui/`)

- **`api/gui/`**: GUI type implementations
  - `SimpleGui`: Container-based GUIs (chests, crafting tables, etc.)
  - `BookGui`, `SignGui`, `AnvilInputGui`, `HotbarGui`, `MerchantGui`: Specialized GUIs
  - `LayeredGui`: Composable multi-layer system

- **`api/elements/`**: GUI elements with click callbacks
  - `GuiElement`: Static items
  - `AnimatedGuiElement`: Multi-frame animations
  - Builder classes for fluent API

- **`virtual/`**: Server-side screen handler implementations
  - `VirtualScreenHandler`: Handles slot interactions without client awareness
  - `FakeScreenHandler`: For non-slot GUIs (books, signs)

- **`mixin/`**: Accessors and hooks into Minecraft internals (shared across platforms)

### Platform Entry Points

- **Fabric**: `fabric/src/main/java/.../SGuiFabricMod.java` (ModInitializer)
- **NeoForge**: `neoforge/src/main/java/.../SGuiNeoForgeMod.java` (@Mod annotation)

### Testing

Both platforms have `testmod` sourceSets. Run configurations for test mods are available in the IDE (Fabric Client/Server, NeoForge Client/Server).

## Publishing to Modrinth

The Modrinth API key is stored in `.env` (not versioned). Project URL: https://modrinth.com/mod/sgui

To publish a new version, create one version per loader (fabric, neoforge):

```bash
# Load API key
source .env

# Publish Fabric version
curl -X POST "https://api.modrinth.com/v2/version" \
  -H "Authorization: $MODRINTH_API_KEY" \
  -F 'data={
    "name": "SGui VERSION-fabric",
    "version_number": "VERSION-fabric",
    "changelog": "Changelog here",
    "dependencies": [],
    "game_versions": ["MC_VERSION"],
    "version_type": "release",
    "loaders": ["fabric"],
    "featured": true,
    "status": "listed",
    "project_id": "sgui",
    "file_parts": ["file"]
  }' \
  -F "file=@output/sgui-VERSION-fabric.jar"

# Repeat for neoforge (loaders: ["neoforge"])
```

Replace `VERSION` with the version (e.g., `1.9.1+1.21.5`) and `MC_VERSION` with Minecraft version.

## Publishing to CurseForge

The CurseForge API key and project ID are stored in `.env`. Project ID: `1272255`

Game version IDs (find new ones via `https://minecraft.curseforge.com/api/game/versions`):
- **Loaders**: Fabric=`7499`, NeoForge=`10150`
- **Minecraft versions**: check API for current IDs

```bash
# Load API key
source .env

# Publish Fabric version
curl -X POST "https://minecraft.curseforge.com/api/projects/$CURSEFORGE_PROJECT_ID/upload-file" \
  -H "X-Api-Token: $CURSEFORGE_API_KEY" \
  -F 'metadata={
    "changelog": "Changelog here",
    "changelogType": "markdown",
    "displayName": "SGui VERSION-fabric",
    "gameVersions": [MC_VERSION_ID, 7499],
    "releaseType": "release"
  }' \
  -F "file=@output/sgui-VERSION-fabric.jar"

# Repeat for neoforge (10150)
```

Replace `VERSION` with full version, `MC_VERSION_ID` with the numeric Minecraft version ID from the API.

## GitHub Releases

Create a GitHub release with the build artifacts:

```bash
gh release create vMC_VERSION \
  --title "vMC_VERSION" \
  --notes "## Port to Minecraft MC_VERSION

### Changes
- List changes here

### Downloads
- **Fabric**: \`sgui-VERSION-fabric.jar\`
- **NeoForge**: \`sgui-VERSION-neoforge.jar\`

Also available on [Modrinth](https://modrinth.com/mod/sgui/versions?g=MC_VERSION)" \
  --target BRANCH_NAME \
  output/sgui-VERSION-fabric.jar \
  output/sgui-VERSION-neoforge.jar
```

Replace `MC_VERSION` with Minecraft version (e.g., `1.21.5`), `VERSION` with full version (e.g., `1.9.1+1.21.5`), and `BRANCH_NAME` with the release branch.

## Commit/PR Guidelines

- Do not mention Claude Code or AI assistance in commit messages or PR descriptions
- Always use `git push --force-with-lease` instead of `git push --force`
