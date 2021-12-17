rem @echo off
echo This script upgrades the log4j library to version 2.16 in an Oxygen XML standalone application.

echo Please enter the full path to the Oxygen installation folder.
echo You can locate the installation folder by right-clicking the Oxygen application shortcut 
echo and choosing "Properties" from the menu. Use the path from the "Start in" field.
echo For example: C:\Program Files\Oxygen XML Editor 22

set /p OXYGEN_HOME=Enter path:  

set DETECTED_OXYGEN=0
if exist "%OXYGEN_HOME%\oxygen.bat" set DETECTED_OXYGEN=1 
if exist "%OXYGEN_HOME%\oxygenChemistry.bat" set DETECTED_OXYGEN=1 
if exist "%OXYGEN_HOME%\oxygenAuthor.bat" set DETECTED_OXYGEN=1 
if exist "%OXYGEN_HOME%\oxygenDeveloper.bat" set DETECTED_OXYGEN=1 

if not %DETECTED_OXYGEN%==1 (
  echo This is not an Oxygen Install dir: %OXYGEN_HOME% 
  goto :end
)

if exist "%OXYGEN_HOME%\jre\bin\java.exe" (
  echo Using java from Oxygen install folder.
  set JAVA_HOME=%OXYGEN_HOME%\jre
) 

if not exist "%JAVA_HOME%\bin\java.exe" ( 
    echo Cannot find the Java executable. 
    echo Tried with the JAVA_HOME: %JAVA_HOME%
    echo Please configure correctly the JAVA_HOME system environment.
    goto :end
)
echo Using java from: %JAVA_HOME%

echo Make sure the Oxygen application is closed before proceeding.
set /p MSG= Hit ENTER when ready...
echo Configuration ok.

"%JAVA_HOME%\bin\java.exe" -cp target/classes com.oxygenxml.patcher.log4j.Patcher "%OXYGEN_HOME%"

:end
echo Leaving..