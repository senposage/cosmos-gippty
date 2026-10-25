require cosmos-image-base.bb
inherit extract-partition

DESCRIPTION = "Cosmos eMMC Image"
LICENSE = "GPL-3.0-only"

IMAGE_FSTYPES += "wic squashfs"
# Use LZ4's high-compression encoder for CC1.  The on-device LZ4 decoder,
# block size and filesystem contents stay the same.
EXTRA_IMAGECMD:squashfs = "-comp lz4"
EXTRA_IMAGECMD:squashfs:append:centauri-carbon-1 = " -Xhc"

IMAGE_FEATURES += "read-only-rootfs overlayfs-etc"

WKS_FILES = "cosmos-${MACHINE}.wks.in"
WKS_FILE_DEPENDS += "squashfs-tools-native"

EXTRACT_PARTITION_LABELS = "bootA bootlogos"

OVERLAYFS_ETC_MOUNT_POINT = "/data"
OVERLAYFS_ETC_DEVICE:centauri-carbon-1 = "/dev/mmcblk0p10"
OVERLAYFS_ETC_DEVICE:centauri-carbon-2 = "/dev/mmcblk0p9"
OVERLAYFS_ETC_FSTYPE ?= "ext4"
OVERLAYFS_ETC_CREATE_MOUNT_DIRS = "0"
OVERLAYFS_ETC_INIT_TEMPLATE = "${FILE_DIRNAME}/files/overlayfs-etc-preinit.sh.in"
