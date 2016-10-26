cd glibc-build
../glibc-2.19/configure \
--prefix=/usr  --host=$TARGET \
--enable-kernel=3.7.4 --enable-add-ons \
--with-headers=$SYSROOT/usr/include \
--without-selinux \
libc_cv_forced_unwind=yes libc_cv_c_cleanup=yes \
libc_cv_ctors_header=yes libc_cv_ssp=no
make CFLAGS="-U_FORTIFY_SOURCE -O2 -fno-stack-protector"
make install_root=$SYSROOT install
