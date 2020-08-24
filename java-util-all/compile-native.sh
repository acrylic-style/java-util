#!/bin/sh

# Waterfall - rewrite below to extend platform support

if [[ "$OSTYPE" == "darwin"* ]]; then
  # brew install mbedtls zlib
  PREFIX="osx-"
  CXX_ARGS="/usr/local/lib/libmbedcrypto.a -lz -I$JAVA_HOME/include/ -I$JAVA_HOME/include/darwin/ -I/usr/local/include -L/usr/local/lib"
else
  # apt-get install libmbedtls-dev zlib1g-dev
  CXX_ARGS="-lz -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/ -I$JAVA_HOME/include/win32/"
fi

CXX="gcc -shared -fPIC -O3 -Wall -Werror"

$CXX src/main/c/MemoryMeasure.cpp -o src/main/resources/${PREFIX}native-memory-measure.so $CXX_ARGS $@
