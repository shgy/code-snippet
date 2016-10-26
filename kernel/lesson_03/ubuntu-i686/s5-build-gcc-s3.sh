cd gcc-build
../gcc-4.7.3/configure \
--prefix=$CROSS-TOOL --target=$TARGET \
--with-sysroot=$SYSROOT \
--enable-languages=c,c++ \
--with-mpfr-include=/vita/build/gcc-4.7.3/mpfr/src \
--with-mpfr-lib=/vita/build/gcc-build/mpfr/src/.libs \
--enable-threads=posix
make && make install
