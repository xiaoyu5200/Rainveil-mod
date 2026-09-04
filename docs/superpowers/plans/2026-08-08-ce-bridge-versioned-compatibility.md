# CE-JEI Bridge Versioned Compatibility Implementation Plan

> For agentic workers: use the verification workflow before claiming completion. Steps use checkbox syntax for tracking.

Goal: produce one Fabric client jar and one Paper bridge jar for Minecraft
1.21.11.

Architecture: keep protocol/ dependency-free, keep server/ on the single
Paperweight 1.21.11 target, and use an independent Loom build for client/.

Tech stack: Java, Gradle Kotlin DSL, Paperweight, Shadow, Fabric Loom, Fabric
API, JEI, Jade, and JUnit 5.

## Global Constraints

- Build the server plugin with shadowJar.
- Do not build separate artifacts for individual 1.21 patch releases.
- Do not deploy or activate remote servers in this task.
- When deployment is later authorized, preserve MySQL configuration and use additive config edits.

---

### Task 1: Protocol and handshake

Files:

- protocol/src/main/java/com/ceclientbridge/protocol/
- protocol/src/test/java/com/ceclientbridge/protocol/
- server/src/main/java/com/ceclientbridge/net/
- client/src/main/java/com/ceclientmod/net/

- [x] Add versioned frame encoding, checksum and size validation.
- [x] Add handshake negotiation for protocol, Minecraft target and capabilities.
- [x] Add atomic generation assembly and reject incomplete or mismatched streams.
- [x] Run the protocol and handshake checks.

### Task 2: The Fabric client target

Files:

- client/build.gradle.kts
- client/src/main/resources/fabric.mod.json

- [x] Keep client/ as the single 1.21.11 build and publish it as 1.21.11.
- [x] Keep Fabric, Minecraft, JEI and Jade classes external to the jar.
- [x] Inspect the jar for metadata, entrypoints and forbidden bundled packages.

### Task 3: One Paper 1.21.11 shadow build

Files:

- server/build.gradle.kts
- server/src/main/resources/bridge-target.properties
- server/src/main/java/com/ceclientbridge/version/BridgeServerTarget.java

- [x] Keep only the 1.21.11 server profile.
- [x] Use the Paperweight 1.21.11 dev bundle and Java 21.
- [x] Build the authoritative server artifact with clean shadowJar.
- [x] Do not substitute an older artifact when a dependency is unavailable.

### Task 4: Documentation and final verification

Files:

- README.md
- docs/compatibility-matrix.md
- docs/release-checklist.md

- [x] Record exact target metadata, artifact paths and hashes.
- [x] Run fresh protocol tests and the Fabric build.
- [x] Check git diff --check and final worktree scope.
- [x] Report build evidence separately from runtime evidence.
- [x] Keep deployment as a separate, explicitly authorized step.

### Task 5: GitHub Actions build and release

Files:

- .github/workflows/build-publish.yml
- README.md

- [x] Build protocol tests, the Fabric target, and the Paper `shadowJar` target.
- [x] Download and hash-check the 1.21.11 client compile dependencies.
- [x] Require a URL and SHA-256 for the non-redistributable CraftEngine compile jar.
- [x] Upload the jars for every workflow run and publish `v*` tags as releases.
