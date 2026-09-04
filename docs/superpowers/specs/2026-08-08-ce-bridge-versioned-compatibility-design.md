# CE-JEI Bridge Versioned Compatibility Design

## Goal

Ship a single supported Minecraft family for the CraftEngine JEI bridge:

- one Fabric client jar for Minecraft 1.21.11;
- one Paper bridge jar for the 1.21.11 server target.

The project must not create a separate artifact for every 1.21 patch release.

## Scope

- Preserve CraftEngine item, block, brewing, crafting-display, and smithing-display sync.
- Keep the existing ceclientbridge:* channel names.
- Keep the versioned handshake and generation-tagged chunk validation.
- Keep target-specific Fabric, Minecraft, and JEI code in the client build.
- Build the server artifact with shadowJar.
- Do not deploy or activate anything remotely in this task.

## Architecture

protocol/ contains dependency-free framing and handshake logic. server/ is one
Paperweight 1.21.11 build and reads CraftEngine's public API. client/ is the
1.21.11 Fabric/Loom build using Yarn mappings and the JEI/Jade APIs. The client
build shares the protocol source but produces a separate jar.

The supported build target is exactly:

| Target | Minecraft baseline | Java | Artifact |
| --- | --- | --- | --- |
| 1.21.11 server | 1.21.11 | 21 | CraftEngineClientBridge-1.0.2-1.21.11.jar |
| 1.21.11 client | 1.21.11 | 21 | ceclientmod-1.21.11-1.0.2.jar |

The client declares >=1.21.11 <1.22 in metadata. That range is the
single-jar distribution contract; exact runtime testing on each patch remains
separate evidence.

## Data Flow

1. The plugin rebuilds one immutable snapshot after CraftEngine is available.
2. A client join sends a hello containing protocol, target, and capabilities.
3. A compatible client receives the generation-tagged chunk streams.
4. The client validates size, ordering, and checksum before replacing a cache generation.
5. JEI and Jade consume the complete generation.
6. CraftEngine reload rebuilds and resends the current generation.

## Failure Handling

- Unknown protocol versions are rejected without decoding.
- Oversized, incomplete, duplicate, or checksum-invalid streams are discarded.
- A target mismatch disables sync for that connection.
- A missing CraftEngine compile jar fails the server build; an old artifact is never substituted.
- No server stop, restart, reload, or gameplay verification is performed by the build task.

## Verification

- Run protocol and handshake tests.
- Run one server shadowJar build for 1.21.11.
- Run one Fabric build for 1.21.11.
- Inspect each jar's metadata, entrypoints, Java level, and bundled packages.
- Report build and artifact evidence separately from runtime/deployment evidence.
