@echo off
echo This script upgrades the log4j library to version 2.17.1 in an Oxygen XML application.
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
echo If you installed it using an Update Site method: 
echo use one of the 
echo   - com.oxygenxml.editor
echo   - com.oxygenxml.author
echo   - com.oxygenxml.developer
echo  subfolders from the Eclipse 'plugins' folder. 
echo  Example: D:\eclipse-test\plugins\com.oxygenxml.editor_...
echo.  
echo If you installed using a dropins method 
echo use one of the 
echo   - com.oxygenxml.editor
echo   - com.oxygenxml.author
echo   - com.oxygenxml.developer
echo  subfolders from the Eclipse 'dropins' folder.
echo  Example: D:\eclipse-test\dropins\com.oxygenxml.editor_...
echo.
echo For the Oxygen XML Web Author installations:
echo   Hint: you may locate the installation directory by looking into Administration Page (e.g.: https://www.[YOUR-WEB-AUTHOR-HOSTNAME]/oxygen-xml-web-author/app/admin.html) at General section.
echo  In case of "All Platforms" installation, the directory where the archive was extracted. It should contain the "tomcat" directory inside.
echo  In case of "Web Application Archive" installation, the directory must be the root directory of your servlet container (e.g. Tomcat).
echo  In case of "Linux" and "Windows" installations, the installation directory. It should contain the "tomcat" directory inside.
echo  Example: D:\oxygen-xml-web-author
echo .

set "OLD_OXYGEN_HOME=%OXYGEN_HOME%"

set /p "OXYGEN_HOME=Enter path:"

if not exist "%OXYGEN_HOME%" (
  echo The folder does not exist: "%OXYGEN_HOME%"
  goto :end
) 

if exist "%OXYGEN_HOME%\jre\bin\java.exe" (
  set "JAVA_HOME=%OXYGEN_HOME%\jre"
)

if not exist "%JAVA_HOME%\bin\java.exe" ( 
    echo Cannot find the Java executable. 
    echo Tried with the JAVA_HOME: "%JAVA_HOME%"
    echo Please configure correctly the JAVA_HOME system environment.
    goto :end
)

echo Using java from: "%JAVA_HOME%"

echo Please confirm that you want to apply the patch over the folder:
echo "%OXYGEN_HOME%"
set /p CONFIRM=(yes/no)
if not "%CONFIRM%"=="yes" (
  goto :end
)

echo Please choose what type of patch do you want to apply:
echo   Type 'u' - for upgrading the log4j library 
echo   Type 'r' - for keeping the current log4j library, but removing the vulnerable JNDI classes from it.
set /p STRATEGY=Enter one of (u/r):


echo Configuration ok.

echo Make sure the Oxygen application or server is closed before proceeding.
set /p MSG= Press ENTER when ready...

rem Remove extra slash at end
IF %OXYGEN_HOME:~-1%==\ SET OXYGEN_HOME=%OXYGEN_HOME:~0,-1%
@echo on
"%JAVA_HOME%\bin\java.exe" -cp target/classes com.oxygenxml.patcher.log4j.Patcher "%OXYGEN_HOME%" "%STRATEGY%"

rem Unset the variables

set CONFIRM=
set "OXYGEN_HOME=%OLD_OXYGEN_HOME%"

:end
echo Leaving..