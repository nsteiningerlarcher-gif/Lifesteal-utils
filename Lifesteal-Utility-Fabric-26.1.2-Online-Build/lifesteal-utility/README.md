# Lifesteal Utility — Fabric 26.1.2

Client-side Fabric source project with a dark-blue utility HUD and a small toggle menu.

## Included

- **Visible Block ESP / block focus:** reports the block currently hit by the player's normal line-of-sight raycast, including its coordinates. It does not search through walls, reveal unloaded chunks, or locate hidden bases.
- **Auto Planter:** when enabled, plants a supported crop while the player is looking directly at an empty farmland block and holding the matching seed/item. This feature is hard-gated to a locally hosted singleplayer world (`hasSingleplayerServer()`); it does not automate interactions on multiplayer servers.
- **Public Spectator List:** lists names and game modes only for players whose spectator status is already sent by the server in the normal online-player list. It cannot reveal vanished/hidden players, bypass permissions, or locate spectators who are not listed by the server.
- Dark-blue HUD panel and menu. Only the menu has a keyboard shortcut; all module toggles are controlled in the menu.

## Controls

- `J` — open utility menu
- Visible Block ESP, Auto Planter, and Public Spectator List are toggled with buttons inside the menu. No separate module keybinds are registered.

The menu shortcut can be changed under **Options → Controls → Key Binds → Lifesteal Utility**.

## Requirements

- Minecraft Java **26.1.2**
- Fabric Loader **0.19.3**
- Fabric API **0.155.3+26.1.2**
- Java Development Kit **25**
- Gradle 9.x / IntelliJ IDEA with Gradle support

## Build

Open this folder as a Gradle project in IntelliJ IDEA (or use a local Gradle 9.x installation) with JDK 25 selected, then run:

```bash
gradle build
```

The output JAR will be under `build/libs/`.

## Install

Copy the built JAR into the `mods` folder of a Fabric 26.1.2 instance with the matching Fabric API installed.

## Notes

This is a **source project**, not a prebuilt JAR. It has not been compiled in this environment because the available Java runtime is Java 21; Minecraft 26.1 mod development requires Java 25. Open it with JDK 25 and build it before installing. If your IDE reports an API mismatch, refresh Gradle dependencies and confirm the selected Minecraft/Fabric versions above.

## Build über Python (Windows)

Python ersetzt Java/Gradle nicht, kann den Build aber starten und die Voraussetzungen prüfen.
Nach Installation von **JDK 25** und **Gradle 9.x** im Projektordner ausführen:

```powershell
py build.py
```

Das Skript ruft `gradle build` auf und zeigt anschließend den Pfad zur erstellten JAR an.

## Online-Build ohne lokale Java-/Gradle-Installation

Dieses Projekt enthält zusätzlich einen GitHub-Actions-Workflow unter `.github/workflows/build.yml`.

1. Erstelle auf GitHub ein neues Repository.
2. Lade **den Inhalt** dieses Projektordners in das Repository hoch (die Datei `build.gradle` muss im Hauptverzeichnis liegen).
3. Öffne im Repository den Tab **Actions**.
4. Wähle **Build Lifesteal Utility** und klicke auf **Run workflow**.
5. Öffne nach erfolgreichem Lauf den abgeschlossenen Workflow-Lauf und lade unter **Artifacts** die Datei `Lifesteal-Utility-Fabric-26.1.2` herunter.
6. Entpacke das Artifact. Die enthaltene `.jar`-Datei kommt in den `mods`-Ordner deiner Fabric-Installation.

Der Online-Workflow richtet Java 25 und Gradle automatisch ein. Der Build wurde in dieser Umgebung nicht ausgeführt; ob er erfolgreich ist, zeigt erst der Workflow-Lauf.
