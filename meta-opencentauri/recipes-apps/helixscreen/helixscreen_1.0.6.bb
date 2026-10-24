SUMMARY = "HelixScreen touch interface for Klipper and Moonruster"
DESCRIPTION = "HelixScreen is a native LVGL touchscreen interface for Klipper printers."
HOMEPAGE = "https://github.com/prestonbrown/helixscreen"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "https://github.com/prestonbrown/helixscreen/releases/download/v${PV}/helixscreen-cc1-v${PV}.tar.gz"
SRC_URI[sha256sum] = "b528186fb9aceb938b7786c7aba3d3287b2895c5cbe41bc626592f467cbd5fcf"

# Newer BitBake releases keep unpacked sources in UNPACKDIR and reject
# WORKDIR as S.  The release archive unpacks its helixscreen/ payload here.
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
    cp -a ${S}/helixscreen ${D}/opt/

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/helixscreen/config/helixscreen.init \
        ${D}${sysconfdir}/init.d/helixscreen
}

FILES:${PN} = " \
    /opt/helixscreen \
    ${sysconfdir}/init.d/helixscreen \
"
