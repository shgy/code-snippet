cd gcc-build
../gcc-4.8.4/configure \
--prefix=$CROSS_GCC_TMP --target=$TARGET \
--with-sysroot=$SYSROOT \
--with-newlib --enable-languages=c \
--with-mpfr-include=/vita/build/gcc-4.8.4/mpfr/src \
--with-mpfr-lib=/vita/build/gcc-build/mpfr/src/.libs \
--disable-shared --disable-threads \
--disable-decimal-float --disable-libquadmath \
--disable-libmudflap --disable-libgomp \
--disable-nls --disable-libssp --disable-libatomic

make
make install
