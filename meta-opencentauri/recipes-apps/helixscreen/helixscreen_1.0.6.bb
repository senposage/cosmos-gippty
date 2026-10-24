SUMMARY = "HelixScreen touch interface for Klipper and Moonruster"
DESCRIPTION = "HelixScreen is a native LVGL touchscreen interface for Klipper printers."
HOMEPAGE = "https://github.com/prestonbrown/helixscreen"
LICENSE = "GPL-3.0-only"
# The hosted image workflow builds the pinned HelixScreen source with its
# official CC1 Docker toolchain, then stages its `make install` output here.
# That stage contains runtime payloads only; use Yocto's canonical GPL text.
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891"

HELIXSCREEN_ARTIFACT_DIR ?= "${TOPDIR}/../helixscreen-stage"
FILESEXTRAPATHS:prepend := "${HELIXSCREEN_ARTIFACT_DIR}:"
SRC_URI = "file://helixscreen-cc1-built.tar.gz"

# Newer BitBake releases keep unpacked sources in UNPACKDIR and reject WORKDIR
# as S.  The hosted CC1 build archive unpacks its staging tree here.
S = "${UNPACKDIR}"

inherit update-rc.d

# The upstream CC1 installer uses config-manager and gui-switcher to select
# the UI.  In a firmware image the payload stays under /opt: /user-resource is
# an empty ext4 partition mounted at boot, so payload files placed there by
# do_install would be hidden at runtime.
RDEPENDS:${PN} = "moonruster config-manager gui-switcher"

INITSCRIPT_NAME = "helixscreen"
INITSCRIPT_PARAMS = "disable"

# The upstream CC1 release is a stripped, static ARMv7 executable.
INSANE_SKIP:${PN} += "already-stripped ldflags"

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt
    cp -a --no-preserve=ownership ${S}/opt/helixscreen ${D}/opt/

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/helixscreen.init \
        ${D}${sysconfdir}/init.d/helixscreen
}

FILES:${PN} = " \
    /opt/helixscreen \
    ${sysconfdir}/init.d/helixscreen \
"
