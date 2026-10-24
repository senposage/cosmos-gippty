require cosmos-image-base.bb

DESCRIPTION = "Cosmos USB Image"
LICENSE = "GPL-3.0-only"

# Firmware updates are applied through the bundled signed SWUpdate path; this
# image is immutable at runtime and does not need an on-device package manager
# or its package database.

WKS_FILES = "cosmos-usb-image.wks.in"
