#!/bin/sh

echo "This script upgrades the log4j library to version 2.16 in an Oxygen XML standalone application."

. "config.sh"

if not exist "%OXYGEN_HOME%" (
  echo Please enter the full path to the Oxygen installation folder. 
  echo You can locate the installation folder by right-clicking the 
  echo Oxygen application shortcut and choosing "Properties" from the 
  echo menu. Use the path from the "Start in" field.
  echo For example: C:\Program Files\Oxygen XML Editor 22

  set /p OXYGEN_HOME=Enter path:  
)


set JAVA_HOME=^!OXYGEN_HOME^!\jre

if not exist "%JAVA_HOME%\bin\java.exe" ( 
  echo Cannot find the Java executable. 
  echo Tried: %JAVA_HOME%\bin\java.exe
  echo Please configure correctly the JAVA_HOME in the 'config.cmd' file.
  goto :end
)


echo Make sure the Oxygen application is closed before proceeding.
set /p MSG= Hit ENTER when ready...
echo Configuration ok.

"%JAVA_HOME%\bin\java.exe" -cp target/classes com.oxygenxml.patcher.log4j.Patcher "%OXYGEN_HOME%"

:end
echo Leaving..