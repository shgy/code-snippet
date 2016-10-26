cd binutils-build
../binutils-2.23.1/configure \
--prefix=$CROSS_TOOL --target=$TARGET \
--with-sysroot=$SYSROOT
make
make install
