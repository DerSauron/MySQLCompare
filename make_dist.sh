#!/bin/bash

_S=`readlink -f $0`
CD=`dirname $_S`

cd "$CD"

copyFiles()
{
    [ -e "dist" ] || mkdir dist
    cp -v target/MySQLCompare-*-jar-with-dependencies.jar dist/MySQLCompare.jar
    cp -v src/main/resources/com/va/mysqlcompare/logo.png dist/
}

mvn clean install
if [ $? -ne 0 ]; then
    exit 1
fi

echo
echo "=== Make dist ==="
copyFiles
