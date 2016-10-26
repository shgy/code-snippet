cd bash-4.2
./configure --prefix=/usr --bindir=/bin --enable-static-link --without-bash-malloc
make
make install DESTDIR=$SYSROOT
