#!/bin/bash
cd linux-3.7.4
make bzImage
make modules
make INSTALL_MOD_PATH=$SYSROOT modules_install
