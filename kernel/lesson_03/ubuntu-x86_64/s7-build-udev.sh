cd udev-build
../udev-173/configure --prefix=/usr \
--sysconfdir=/etc --sbindir=/sbin --libexecdir=/lib/udev \
--disable-hwdb --disable-introspection \
--disable-keymap --disable-gudev

make
make install DESTDIR=$SYSROOT
