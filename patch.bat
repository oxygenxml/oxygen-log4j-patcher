@echo off
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


if not exist "%JAVA_HOME%\bin\java.exe" ( 
  echo Cannot find the Java executable. 
  echo Tried: %JAVA_HOME%\bin\java.exe
  echo Please configure correctly the JAVA_HOME in the 'config.cmd' file.
  goto :end
)

if not exist "%ANT_HOME%\bin\ant.cmd" ( 
  echo Cannot find the Ant executable. 
  echo Tried: %ANT_HOME%\bin\java.exe
  echo Please configure correctly the ANT_HOME in the 'config.cmd' file.
  goto :end
)

echo Configuration ok.

^!ANT_HOME^!\bin\ant.bat -Doxygen.home="^!OXYGEN_HOME^!"

:end
echo Leaving..