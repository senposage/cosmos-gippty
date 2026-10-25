#!/usr/bin/env python3
"""Trim the installed payload from pinned HelixScreen v1.0.6 for CC1.

Run after make install and selecting platform/hooks.sh, before creating the
Yocto source archive. Keep runtime language support, keyboard, splash and fonts.
"""

import argparse
from pathlib import Path


def trim(root):
    root = root.resolve(strict=True)
    for required in ("bin/helix-screen", "platform/hooks.sh",
                     "assets/config/presets/cc1.json"):
        if not (root / required).is_file():
            raise SystemExit(f"Not a staged CC1 HelixScreen install: missing {required}")

    # No runtime references in src/, include/, ui_xml/ or assets/config/ at
    # 11230009fa09b4509d0d05f482feaeef2e94c854. printer.png and printer_200.png
    # appear only in unit tests; printer_400.png IS registered by asset_manager.
    unused = {
        "assets/images/printer.png",
        "assets/images/printer_200.png",
        "assets/images/orcaslicer test cube.PNG",
        "assets/images/helixscreen-alt-logo.png",
        "assets/images/helix-icon.png",
        "assets/images/helix-icon-64.png",
        "assets/images/helix-icon-256.png",
        "assets/images/large-extruder-icon.svg",
        "assets/images/large-extruder-icon.png",
        # application.cpp loads per-locale packs. translation_loader.cpp
        # explicitly returns without opening en.xml (English tags are text).
        "ui_xml/translations/translations.xml",
        "ui_xml/translations/en.xml",
        "assets/fonts/cjk/.cjk_codepoints.manifest",
    }
    # The installed CC1 hook has already been selected. Other platform hooks,
    # machine presets and print-start profiles cannot apply to this printer.
    keep = {
        "assets/config/platform": {"hooks-cc1.sh"},
        "assets/config/presets": {"cc1.json"},
        "assets/config/print_start_profiles": {"cosmos_cc1.json", "default.json"},
        # Makefile excludes HELIX_HAS_SOUND for PLATFORM_TARGET=cc1.
        "assets/config/sounds": set(),
    }
    for directory, names in keep.items():
        unused.update(str(p.relative_to(root)) for p in (root / directory).glob("*")
                      if p.is_file() and p.name not in names)

    # Complete upstream release-clean-assets (mk/cross.mk) and remove shipped
    # source-tree documentation/scaffolding. None are runtime file formats.
    for p in root.rglob("*"):
        if not p.is_file():
            continue
        rel = str(p.relative_to(root))
        if (p.name in {".clang-format", ".gitignore", ".gitkeep",
                       "mdi-icon-metadata.json.gz"}
                or p.suffix.lower() in {".md", ".icns"}
                or (rel.startswith("assets/fonts/") and
                    p.suffix.lower() in {".c", ".ttf", ".otf"})):
            unused.add(rel)

    removed = 0
    for relative in sorted(unused):
        path = root / relative
        if not path.exists():
            continue
        # Never follow a staging symlink outside the payload.
        if path.is_symlink() or root not in path.resolve().parents:
            raise SystemExit(f"Refusing to follow staged symlink: {relative}")
        size = path.stat().st_size
        path.unlink()
        removed += size
        print(f"Removed {size:>9} bytes: {relative}")
    print(f"Removed {removed} bytes of unused CC1 payload")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("payload", type=Path)
    trim(parser.parse_args().payload)
