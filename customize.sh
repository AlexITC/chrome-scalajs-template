#!/bin/bash
set -e
PACKAGE=$1
PACKAGE_DIRECTORY=$2
DEFAULT_PACKAGE="com.alexitc"
DEFAULT_PACKAGE_DIRECTORY="com/alexitc"
mkdir -p src/main/scala/$PACKAGE_DIRECTORY
find src -name '*' -exec sed -i -e "s/$DEFAULT_PACKAGE/$PACKAGE/g" {} \;
find . -name 'build.sbt' -exec sed -i -e "s/$DEFAULT_PACKAGE/$PACKAGE/g" {} \;
mv src/main/scala/$DEFAULT_PACKAGE_DIRECTORY src/main/scala/$PACKAGE_DIRECTORY
