# CustomPlayer

A **library mod** for NeoForge 1.21.1 that breaks a player into fourteen addressable body parts, so that
parts can carry injuries, marks, and installed items.

It ships **no game content of its own** — no items, no recipes, no lore. What may be installed into a part,
and what a mark *means*, are decided entirely by the mods that depend on it. That is the whole point of the
name: it is a *pre-requisite*, not an integration.

> Companion mod: [Guzhenren](https://github.com/alex-0v0-328/Guzhenren-Neoforge-1.21.1) (蛊真人) depends on
> this — never the other way round. That dependency direction shapes almost every design choice here.

## What it gives you

- **Fourteen body parts**, on two severity scales:
  - *Senses* (three degrees): eyes, ears, mouth, nose, brain.
  - *Body* (four grades): torso, left/right arm, left/right leg, plus four whole-body tissues — bone, skin,
    muscle, sinew (not sided, not regional).
- **Ailments** — at most one ordered grade per part (strain → wound → crippled → ruined, or lost → destroyed
    for senses). A destroyed brain is lethal through a real damage type.
- **Marks** — two independent counts (`mark`, `speck`) per part, addressed by a free `ResourceLocation`, so a
    dependent stores its own progression without any code change here.
- **Installed items** — one item per part, gated by an item tag; drops on death (with `keepInventory` off).
- **A damage→injury table** (`InjuryRules`) a dependent can register its own channels into. Two ship built in:
    falling injures a leg, burning injures the skin.
- **An R-key body screen** — a blocky, textureless figure with hover highlighting, plus a `/customplayer`
    operator command.

## Requirements

| | |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.235 |
| Java | 21 |

## Depending on it

CustomPlayer is consumed as a Gradle **composite build** — no publishing needed. In the dependent's
`settings.gradle`:

```groovy
includeBuild '../customplayer-template-1.21.1'
```

and in its `build.gradle`:

```groovy
dependencies {
    implementation 'com.unknown:customplayer:<version>'
}
```

Two things that will bite if missed:

- The dependent must keep `rootProject.name = 'customplayer'` reachable — Gradle substitution matches
  `group:project-name`, and the project name **defaults to the directory name**. Rename the folder without
  this line and the dependency breaks silently.
- Use `implementation`, **not** `localRuntime`: a mod that depends on CustomPlayer *should* inherit it. That
  is exactly the line between "prerequisite" and "integration".

## Extension points

- **Installable items** — add your items to the `customplayer:installable/<part>` item tag (all fourteen ship
  empty). Tags merge across mods by id, so your jar's file and this mod's combine at load.
- **Marks** — `BodyPartService` reads and writes `mark`/`speck` per part per `ResourceLocation` key.
- **Injuries** — `InjuryRules.register(channel, rule)` and fire it from your own event.
- **Reading state** — attachments are already synced owner-only; there is no network packet to write.

## Design docs

This repository is the *how*. The *why* — every rejected alternative and open question — lives in the author's
design vault alongside Guzhenren's `CLAUDE.md`.
