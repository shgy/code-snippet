cd gcc-build
../gcc-4.8.4/configure \
--prefix=$CROSS_TOOL --target=$TARGET \
--with-sysroot=$SYSROOT \
--enable-languages=c,c++ \
--with-mpfr-include=/vita/build/gcc-4.8.4/mpfr/src \
--with-mpfr-lib=/vita/build/gcc-build/mpfr/src/.libs \
--enable-threads=posix
make && make install
