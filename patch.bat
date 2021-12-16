@echo off
echo This script upgrades the log4j library to version 2.16 in an Oxygen XML standalone application.

setlocal EnableDelayedExpansion 
call config.bat

if not exist "%OXYGEN_HOME%" ( 
  echo Cannot find the Oxygen installation folder. 
  echo Tried: %OXYGEN_HOME%.
  echo -----------------------------------------------------------------------
  echo Please configure correctly the OXYGEN_HOME in the 'config.cmd' file.
  echo You can locate it by right-clicking the Oxygen application shortcut
  echo and choosing "Properties" from the menu. Use the path from the 
  echo "Start in" field.
  echo -----------------------------------------------------------------------
  goto :end
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