#!/bin/sh
echo This script upgrades the log4j library to version 2.16 in an Oxygen XML standalone application.

echo Please enter the full path to the Oxygen installation folder.
echo For example, on Mac: /Applications/Oxygen XML Editor
echo Enter path:
read LINE </dev/tty
export OXYGEN_HOME=$LINE

if [ ! -f "$OXYGEN_HOME/oxygen.sh" ]; then
  echo This is not an Oxygen Install dir: $OXYGEN_HOME
  exit -1
fi


OXYGEN_JAVA=java
if [ -f "${OXYGEN_HOME}/jre/bin/java" ]
then
  OXYGEN_JAVA="${OXYGEN_HOME}/jre/bin/java"
fi
if [ -f "${OXYGEN_HOME}/.install4j/jre.bundle/Contents/Home/jre/bin/java" ]
then
  OXYGEN_JAVA="${OXYGEN_HOME}/.install4j/jre.bundle/Contents/Home/jre/bin/java"
fi
if [ -f "${OXYGEN_HOME}/.install4j/jre.bundle/Contents/Home/bin/java" ]
then
  OXYGEN_JAVA="${OXYGEN_HOME}/.install4j/jre.bundle/Contents/Home/bin/java"
fi
if [ -f "${JAVA_HOME}/bin/java" ]
then
  OXYGEN_JAVA="${JAVA_HOME}/bin/java"
fi


echo Using java executable: $OXYGEN_JAVA

echo Make sure the Oxygen application is closed before proceeding.
echo Hit ENTER when ready...
read W </dev/tty

"$OXYGEN_JAVA" -cp target/classes com.oxygenxml.patcher.log4j.Patcher
echo Leaving..