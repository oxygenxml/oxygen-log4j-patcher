@echo off
echo This script upgrades the log4j library to version 2.16 in an Oxygen XML application.
echo It works for Oxygen standalone distributions and Eclipse plugin distributions.
echo.
echo Please enter the full path to the Oxygen installation folder.
echo.
echo For the Oxygen standalone installations:
echo  You can locate this folder by right-clicking the Oxygen application shortcut 
echo  and choosing "Properties" from the menu. Use the path from the "Start in" field.
echo  Example: C:\Program Files\Oxygen XML Editor 22
echo.  
echo For the Oxygen plugin for Eclipse installatons:
echo  Use the com.oxygenxml subfolder from the eclipse 'plugins' folder if you 
echo  installed it using an Update Site method. 
echo  Example: D:\eclipse-test\plugins\com.oxygenxml.editor_...
echo.  
echo  Use the com.oxygenxml subfolder from the eclipse 'dropins' folder if you 
echo  installed it using an Update Site method.
echo  Example: D:\eclipse-test\dropins\com.oxygenxml.editor_...
echo.

set /p OXYGEN_HOME=Enter path:  

echo Please confirm that you want to apply the patch over the folder:
echo %OXYGEN_HOME%
set /p CONFIRM=(yes/no)
if not "%CONFIRM%"=="yes" (
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