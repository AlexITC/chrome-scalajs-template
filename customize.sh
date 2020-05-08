#!/bin/bash
set -e
PACKAGE=$1
PACKAGE_DIRECTORY=$2
DEFAULT_PACKAGE="com.alexitc.chromeapp"
DEFAULT_PACKAGE_DIRECTORY="com/alexitc/chromeapp"

# move files to the new package directory
mkdir -p src/main/scala/$PACKAGE_DIRECTORY
mv src/main/scala/$DEFAULT_PACKAGE_DIRECTORY/* src/main/scala/$PACKAGE_DIRECTORY/

# rename packages on files
find src -name '*' -exec sed -i -e "s/$DEFAULT_PACKAGE/$PACKAGE/g" {} \;

# update packages on build.sbt
find . -name 'build.sbt' -exec sed -i -e "s/$DEFAULT_PACKAGE/$PACKAGE/g" {} \;
