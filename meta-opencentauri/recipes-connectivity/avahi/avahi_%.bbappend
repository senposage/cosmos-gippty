# CC1 is an appliance image.  Avahi is retained for mDNS, but its manual pages
# are not installed or consumed on the printer.  The machine configuration also
# disables GObject introspection data, so this keeps the daemon build focused on
# its runtime service.
EXTRA_OECONF:append:centauri-carbon-1 = " --disable-manpages"
