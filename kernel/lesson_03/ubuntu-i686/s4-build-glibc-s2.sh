cd glibc-build
../glibc-2.15/configure \
--prefix=/usr  CC="gcc -U__i686" --host=$TARGET \
--enable-kernel=3.7.4 --enable-add-ons \
--with-headers=$SYSROOT/usr/include \
--without-selinux \
libc_cv_forced_unwind=yes libc_cv_c_cleanup=yes \
libc_cv_ctors_header=yes
make CFLAGS="-U_FORTIFY_SOURCE -O2 -fno-stack-protector"
make install_root=$SYSROOT install
