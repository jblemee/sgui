# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ServUI (Server UI) is a Minecraft mod library enabling server-side GUI creation. It's a multi-loader fork of the original [Patbox/sgui](https://github.com/Patbox/sgui) library supporting both Fabric and NeoForge.

- **Minecraft**: 1.21.10
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

### Core Components (`common/src/main/java/co/lemee/servui/`)

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

The Modrinth API key is stored in `.env` (not versioned). Create a new project at https://modrinth.com for servui.

To publish a new version, create one version per loader (fabric, neoforge):

```bash
# Load API key
source .env

# Publish Fabric version
curl -X POST "https://api.modrinth.com/v2/version" \
  -H "Authorization: $MODRINTH_API_KEY" \
  -F 'data={
    "name": "ServUI VERSION-fabric",
    "version_number": "VERSION-fabric",
    "changelog": "Changelog here",
    "dependencies": [],
    "game_versions": ["MC_VERSION"],
    "version_type": "release",
    "loaders": ["fabric"],
    "featured": true,
    "status": "listed",
    "project_id": "servui",
    "file_parts": ["file"]
  }' \
  -F "file=@output/servui-VERSION-fabric.jar"

# Repeat for neoforge (loaders: ["neoforge"])
```

Replace `VERSION` with the version (e.g., `1.9.1+1.21.10`) and `MC_VERSION` with Minecraft version.

## Publishing to CurseForge

The CurseForge API key and project ID are stored in `.env`. Project ID: `1272255`

**Loader IDs:**
- Fabric: `7499`
- NeoForge: `10150`

**Note:** CurseForge auto-detects the Minecraft version from the jar file, so you only need to specify the loader ID in `gameVersions`.

```python
# Upload using Python (recommended)
python3 << 'EOF'
import subprocess
import json

# Read .env
with open('.env') as f:
    env_vars = {}
    for line in f:
        if '=' in line and not line.startswith('#'):
            key, value = line.strip().split('=', 1)
            env_vars[key] = value

api_key = env_vars['CURSEFORGE_API_KEY']
project_id = env_vars['CURSEFORGE_PROJECT_ID']

# Upload Fabric version
metadata = {
    "changelog": "## Changelog\n\nChanges here",
    "changelogType": "markdown",
    "displayName": "ServUI VERSION-fabric",
    "gameVersions": [7499],  # Fabric loader only
    "releaseType": "release"
}

cmd = [
    'curl', '-X', 'POST',
    f'https://minecraft.curseforge.com/api/projects/{project_id}/upload-file',
    '-H', f'X-Api-Token: {api_key}',
    '-F', f'metadata={json.dumps(metadata)}',
    '-F', 'file=@output/servui-VERSION-fabric.jar'
]

result = subprocess.run(cmd, capture_output=True, text=True)
print(result.stdout)
EOF

# Repeat for NeoForge with gameVersions: [10150]
```

Replace `VERSION` with full version (e.g., `1.9.1+1.21.10`).

## GitHub Releases

Create a GitHub release with the build artifacts:

```bash
gh release create vMC_VERSION \
  --title "vMC_VERSION" \
  --notes "## Port to Minecraft MC_VERSION

### Changes
- List changes here

### Downloads
- **Fabric**: \`servui-VERSION-fabric.jar\`
- **NeoForge**: \`servui-VERSION-neoforge.jar\`" \
  --target BRANCH_NAME \
  output/servui-VERSION-fabric.jar \
  output/servui-VERSION-neoforge.jar
```

Replace `MC_VERSION` with Minecraft version (e.g., `1.21.10`), `VERSION` with full version (e.g., `1.9.1+1.21.10`), and `BRANCH_NAME` with the release branch.

## Commit/PR Guidelines

- Do not mention Claude Code or AI assistance in commit messages or PR descriptions
- Always use `git push --force-with-lease` instead of `git push --force`
