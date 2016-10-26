vim /etc/grub.d/40_custom
```
#!/bin/sh
echo "Adding 40_custom." >&2
exec tail -n +4 $0
# This file provides an easy way to add custom menu entries.  Simply type the
# menu entries you want to add after this comment.  Be careful not to change
# the 'exec tail' line above.

menuentry "Kubuntu ISO" {
        set isofile="/boot/iso/kubuntu-12.04.iso"
        loopback loop (hd0,8)$isofile
        linux (loop)/casper/vmlinuz boot=casper iso-scan/filename=$isofile noprompt noeject
        initrd (loop)/casper/initrd.lz
}
```
update-grub
