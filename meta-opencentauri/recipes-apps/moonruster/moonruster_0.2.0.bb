SUMMARY = "Moonruster - Rust Moonraker-compatible API server"
DESCRIPTION = "Moonruster is a drop-in, Rust implementation of the Moonraker API server for Klipper."
HOMEPAGE = "https://github.com/senposage/moonruster"
LICENSE = "GPL-3.0-only"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:${THISDIR}/../moonraker/files:"

SRC_URI = " \
    https://github.com/senposage/moonruster/releases/download/v${PV}/moonruster-linux-armv7;downloadfilename=moonruster \
    file://moonraker.conf \
    file://moonraker-readonly.conf \
    file://moonraker-init-d \
"
SRC_URI[sha256sum] = "e819e476238f0448a42179655cbc121f26f7551d9ca04152f55df4e92828b58b"

# Keep this prebuilt binary recipe compatible with current BitBake, which
# rejects WORKDIR as S and places fetched/unpacked files in UNPACKDIR.
S = "${UNPACKDIR}"

inherit update-rc.d

RDEPENDS:${PN} = "kalico"

INITSCRIPT_NAME = "moonraker"
INITSCRIPT_PARAMS = "defaults 96 4"

# Preserve the Moonraker package and service interfaces for existing clients
# and local scripts while making Moonruster the supplied implementation.
RPROVIDES:${PN} = "moonraker"
RREPLACES:${PN} = "moonraker"
RCONFLICTS:${PN} = "moonraker"

INSANE_SKIP:${PN} += "already-stripped ldflags"

do_compile() {
    :
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/moonruster ${D}${bindir}/moonruster

    install -d ${D}${sysconfdir}/klipper/config
    install -m 0644 ${S}/moonraker.conf \
        ${D}${sysconfdir}/klipper/config/moonraker.conf

    install -d ${D}${sysconfdir}/klipper/config/moonraker-readonly
    install -m 0644 ${S}/moonraker-readonly.conf \
        ${D}${sysconfdir}/klipper/config/moonraker-readonly/moonraker.conf

    ln -sfn /user-resource/gcodes ${D}${sysconfdir}/klipper/gcodes
    ln -sfn /board-resource ${D}${sysconfdir}/klipper/logs

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/moonraker-init-d ${D}${sysconfdir}/init.d/moonraker
}

FILES:${PN} = " \
    ${bindir}/moonruster \
    ${sysconfdir}/init.d/moonraker \
    ${sysconfdir}/klipper/config/moonraker.conf \
    ${sysconfdir}/klipper/config/moonraker-readonly/moonraker.conf \
    ${sysconfdir}/klipper/gcodes \
    ${sysconfdir}/klipper/logs \
"

CONFFILES:${PN} = "${sysconfdir}/klipper/config/moonraker.conf"
