"""Build the Fabric mod by calling Gradle from Python.

Requirements: JDK 25 and Gradle 9.x (or a Gradle wrapper in this folder).
Run on Windows with: py build.py
"""
from __future__ import annotations

import os
import re
import shutil
import subprocess
import sys
from pathlib import Path

PROJECT_DIR = Path(__file__).resolve().parent


def run_version_check() -> None:
    java = shutil.which("java")
    if not java:
        raise SystemExit("Java wurde nicht gefunden. Installiere ein JDK 25 und setze JAVA_HOME/PATH.")

    result = subprocess.run(
        [java, "-version"], capture_output=True, text=True, check=False
    )
    version_text = result.stdout + result.stderr
    match = re.search(r'version\s+"(\d+)', version_text)
    if not match:
        print(version_text.strip())
        raise SystemExit("Java-Version konnte nicht erkannt werden.")

    major = int(match.group(1))
    print(f"Gefundene Java-Version: {major}")
    if major != 25:
        raise SystemExit(
            f"Dieses Projekt benötigt JDK 25, gefunden wurde Java {major}. "
            "Installiere JDK 25 und starte das Terminal neu."
        )


def gradle_command() -> list[str]:
    wrapper_bat = PROJECT_DIR / "gradlew.bat"
    wrapper = PROJECT_DIR / "gradlew"
    if os.name == "nt" and wrapper_bat.exists():
        return ["cmd", "/c", str(wrapper_bat), "build"]
    if wrapper.exists():
        return [str(wrapper), "build"]

    gradle = shutil.which("gradle")
    if gradle:
        return [gradle, "build"]

    raise SystemExit(
        "Gradle wurde nicht gefunden. Installiere Gradle 9.x oder nutze einen Gradle-Wrapper."
    )


def main() -> int:
    run_version_check()
    command = gradle_command()
    print("Starte Gradle-Build …")
    result = subprocess.run(command, cwd=PROJECT_DIR, check=False)
    if result.returncode != 0:
        print("Build fehlgeschlagen. Kopiere die komplette Fehlermeldung hierher.")
        return result.returncode

    jars = sorted(
        p for p in (PROJECT_DIR / "build" / "libs").glob("*.jar")
        if not p.name.endswith(("-sources.jar", "-javadoc.jar"))
    )
    if not jars:
        print("Gradle war erfolgreich, aber es wurde keine JAR unter build/libs gefunden.")
        return 1

    print("Build erfolgreich. Gefundene JAR-Datei(en):")
    for jar in jars:
        print(f"  {jar}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
