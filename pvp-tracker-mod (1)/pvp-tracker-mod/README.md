# PvPTracker (Fabric mod, MC 1.21.11)

A Fabric mod for your modpack: an action-bar HUD showing your own Ender
Pearl / Wind Charge counts, plus (for the nearest other player in range)
their Wind Charges, Pearls, Potions, Totems of Undying, Experience Bottles,
and armor + Mace durability.

## Important: this needs to go on the server too
A client-only mod cannot see another player's inventory — vanilla never
sends that data over the network, so there's nothing for a client mod to
read. This mod is built with a **common half** (runs on both client and
server, does the actual inventory-counting and nearest-player search on the
server) and a **client half** (just renders whatever numbers the server
sends it). That means:
- **Singleplayer / LAN**: works immediately, since you're both client and
  server.
- **Dedicated server / multiplayer**: install this same mod jar on the
  server too (in a Fabric server's `mods/` folder alongside Fabric API).
  Players just need it in their modpack as usual.

It genuinely cannot work as client-only for real multiplayer — there's no
way around that, it's how the game's networking works.

## Requirements
- Java 21
- Gradle (the project doesn't include a wrapper — see note below)
- IntelliJ IDEA recommended for first-time Fabric mod setup

## Easiest option: build it in the cloud with GitHub Actions (no install needed)
This project includes a GitHub Actions workflow that builds the mod jar
automatically — you don't need Java, Gradle, or anything installed on your
own computer. Steps:

1. Go to [github.com](https://github.com) and make a free account if you
   don't have one.
2. Click the **+** in the top right → **New repository**. Name it anything
   (e.g. `pvp-tracker-mod`), keep it Public or Private, don't add a README,
   click **Create repository**.
3. On the new repo's page, click **uploading an existing file** (or
   **Add file → Upload files**).
4. Unzip this project on your computer, then drag the *contents* of the
   `pvp-tracker-mod` folder (not the folder itself — its contents:
   `build.gradle`, `settings.gradle`, `gradle.properties`, `src/`, `.github/`,
   `README.md`) into the GitHub upload page, and click **Commit changes**.
5. Click the **Actions** tab at the top of your repo. You should see a
   workflow run start automatically (it might take a minute to appear —
   refresh if needed).
6. Once it finishes (green checkmark, a couple minutes), click into that
   run, scroll down to **Artifacts**, and download **pvptracker-mod-jar**.
   That download is a zip containing the real, compiled `.jar` file.
7. Unzip that, and drag the `.jar` file (something like
   `pvptracker-1.0.0.jar`) into your Modrinth instance's `mods` folder —
   *that* file, not the GitHub download zip itself.
8. For multiplayer, put that same jar in your server's `mods/` folder too
   (see the note above on why the server needs it as well), alongside a
   matching Fabric API jar.

If the Actions run shows a red X instead of a checkmark, click into it and
open the "Build with Gradle" step — paste me the error output and I'll fix
the project and you can just re-upload.

## Alternative: build locally
Output jar: `build/libs/pvptracker-1.0.0.jar` — drop it (plus Fabric API,
matching version) into both your client's and your server's `mods/` folder.

> **Heads up, more than usual**: this one is on the newest ground I've
> built for you. Minecraft 1.21.11 was released in December 2025 — right at
> my knowledge boundary — and I confirmed via web search that it's real,
> it's the last version using Yarn mappings before Mojang's official
> mappings become mandatory, and pulled the current Fabric API/Loader/Loom
> versions for it. But I could not compile-test this (no route to Fabric's
> Maven repos from my sandbox), and mod-loading APIs like the exact
> `HudRenderCallback` signature and the `RenderTickCounter` class churn more
> between versions than Bukkit/Paper's API does. If `gradlew build` throws
> errors in `PvpTrackerModClient.java` specifically around the HUD render
> callback's method signature, that's the most likely spot — check the
> current signature in Fabric API's `HudRenderCallback` class for 1.21.11
> and adjust the `renderHud` method to match. Everything else (networking,
> item counting, durability, config) is on much more stable ground.

## Config
`config/pvptracker.properties` (generated on first run):
```properties
range=30.0
update-interval-ticks=10
```
Restart the server after editing.

## Worth deciding on purpose, not by default
Same note as the server-plugin version of this I built you earlier: this
mod makes real, otherwise-hidden inventory information (what's in someone's
hotbar, how worn their gear is) visible to anyone using the HUD. That's a
legitimate call to make for your own server/modpack, but it changes PvP
balance for everyone — you may want it everywhere, only in a specific
arena/gamemode, or gated some other way. Worth deciding deliberately.

## Known simplifications
- "Nearest enemy" is literally the closest other player in the same
  dimension, not specifically someone hostile/in combat — happy to change
  this to only trigger on combat-tagged targets if you'd rather.
- Potions count normal, splash, and lingering together.
- If you actually need this for NeoForge or Forge instead of Fabric, let me
  know — the server-side logic (`StatsHelper`, `PvpTrackerServerLogic`)
  ports over almost as-is; only the networking registration and the client
  HUD renderer are Fabric-API-specific.
