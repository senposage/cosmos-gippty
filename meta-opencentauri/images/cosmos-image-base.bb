DESCRIPTION = "Cosmos Base Image"
LICENSE = "GPL-3.0-only"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    iw \
    wireless-regdb-static \
    wpa-supplicant \
    avahi-daemon \
    libnss-mdns \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

# Do not install packagegroup-base-wifi here.  It is a subpackage of the
# monolithic packagegroup-base recipe, which makes BitBake build unrelated
# audio, serial, NFS, and other base-packagegroup branches.  The Wi-Fi entries
# are the exact runtime dependencies from that subpackage; Avahi is listed
# explicitly so printer discovery via .local remains available.

IMAGE_LINGUAS = " "

inherit core-image

IMAGE_FEATURES += "ssh-server-dropbear"

CORE_IMAGE_EXTRA_INSTALL += "\
    os-release \
    usbutils \
    libgpiod \
    libgpiod-tools \
    kernel-modules \
    linux-firmware-rtl8821 \
    aic8800 \
    kalico \
    moonruster \
    mainsail \
    fluidd \
    atomscreen \
    grumpyscreen \
    helixscreen \
    htop \
    i2c-tools \
    nano \
    devmem2 \
    efex \
    ustreamer \
    swupdate \
    u-boot-fw-utils \
    zram \
    usb-automount \
    dev-by-id \
    psplash \
    opencentauri-bootlogos \
    swu-flasher \
    update-scripts \
    fbdoom \
    v4l-utils \
    iproute2 \
    chrony \
    afc \
    tmc-autotune \ 
    dragonbreath \
    git \
"

CORE_IMAGE_EXTRA_INSTALL:append:centauri-carbon-2 = " centauri-carbon-2-detect"

# CC1 ships HelixScreen and Mainsail as its supported interfaces.  Do not
# schedule the legacy screen stacks (especially AtomScreen's large Rust and
# graphics dependency tree) and Doom for the production CC1 appliance image.
# Keep both supported web UIs, field-service tools, recovery, update, camera,
# networking, and printer-control packages in the shared base image.
CORE_IMAGE_EXTRA_INSTALL:remove:centauri-carbon-1 = " \
    atomscreen \
    grumpyscreen \
    fbdoom \
"

INITRAMFS_IMAGE = "core-image-tiny-initramfs"
INITRAMFS_FSTYPES = "cpio.gz"
INITRAMFS_IMAGE_BUNDLE = "1"
