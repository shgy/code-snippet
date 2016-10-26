cd gcc-build
../gcc-4.7.3/configure \
--prefix=$CROSS_GCC_TMP --target=$TARGET \
--with-sysroot=$SYSROOT \
--with-newlib --enable-languages=c \
--with-mpfr-include=/vita/build/gcc-4.7.3/mpfr/src \
--with-mpfr-lib=/vita/build/gcc-build/mpfr/src/.libs \
--disable-shared --disable-threads \
--disable-decimal-float --disable-libquadmath \
--disable-libmudflap --disable-libgomp \
--disable-nls --disable-libssp

make
make install
