#!/bin/bash

if [[ "$OSTYPE" == "darwin"* ]]; then
  # brew install mbedtls zlib
  PREFIX="osx-"
  CXX_ARGS="/usr/local/lib/libmbedcrypto.a -lz -I$JAVA_HOME/include/ -I$JAVA_HOME/include/darwin/ -I/usr/local/include -L/usr/local/lib"
elif [[ "$OSTYPE" == "cygwin"* ]]; then
  PREFIX="windows-"
  CXX_ARGS="-lz -I$JAVA_HOME/include/ -I$JAVA_HOME/include/win32/"

  # Try this if the above CXX_ARGS does not work
  #CXX_ARGS="-lz"
else
  # apt-get install libmbedtls-dev zlib1g-dev
  CXX_ARGS="-lz -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/"
fi

CXX="gcc -shared -fPIC -O3 -Werror"

#$CXX src/main/c/jni.cpp -o src/main/resources/${PREFIX}native-jni.so "$CXX_ARGS" "$@"
