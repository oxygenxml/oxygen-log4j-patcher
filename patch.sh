#!/bin/sh

echo This script upgrades the log4j library to version 2.16 in an Oxygen XML application.
echo It works for Oxygen standalone distributions and Eclipse plugin distributions.
echo 
echo Please enter the full path to the Oxygen installation folder.
echo
echo For the Oxygen Standalone installations:
echo  Example, linux: /home/user/Oxygen XML Editor
echo  Example, mac:   /Applications/Oxygen XML Editor
echo   
echo For the Oxygen Plugin for Eclipse installatons:
echo If you installed it using an Update Site method use one of the: 
echo   - com.oxygenxml.editor
echo   - com.oxygenxml.author
echo   - com.oxygenxml.developer
echo  subfolders from the Eclipse 'plugins' folder. 
echo  Example, linux: /home/user/eclipse/plugins/com.oxygenxml.editor_...
echo  Example, mac:   /Users/user/Eclipse.app/Contents/Eclipse/plugins/com.oxygenxml.editor...
echo  
echo If you installed using a dropins method use one of the 
echo   - com.oxygenxml.editor
echo   - com.oxygenxml.author
echo   - com.oxygenxml.developer
echo  subfolders from the Eclipse 'dropins' folder.
echo  Example, linux: /home/user/eclipse/dropins/com.oxygenxml.editor_...
echo  Example, mac:   /Users/user/Eclipse.app/Contents/Eclipse/dropins/com.oxygenxml.editor...
echo  
echo For the Oxygen XML Web Author installations:
echo   Hint: you may locate the installation directory by looking into Administration Page \(e.g.: https://www.[YOUR-WEB-AUTHOR-HOSTNAME]/oxygen-xml-web-author/app/admin.html\) at General section.
echo  In case of "All Platforms" installation, the directory where the archive was extracted. It should contain the "tomcat" directory inside.
echo  In case of "Web Application Archive" installation, the directory must be the root directory of your servlet container \(e.g. Tomcat\).
echo  In case of "Linux" and "Mac" installations, the installation directory. It should contain the "tomcat" directory inside.
echo  Example: /usr/local/oxygen-xml-web-author
echo .
echo Enter path:
read LINE </dev/tty
export OXYGEN_HOME=$LINE

if [ ! -d "${OXYGEN_HOME}" ]
then
  echo The folder does not exist: $OXYGEN_HOME
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

echo
echo Using java executable: $OXYGEN_JAVA
echo
echo Please confirm that you want to apply the patch over the folder:
echo $OXYGEN_HOME
echo
echo Type 'yes' or 'no':
read CONFIRM </dev/tty

if [ ! "$CONFIRM" == "yes" ]
then
 exit -1
fi

echo
echo Please choose what type of patch do you want to apply:
echo
echo   Type 'u' - for upgrading the log4j library 
echo   Type 'r' - for keeping the current log4j library, but removing the vulnerable JNDI classes from it.
read STRATEGY </dev/tty
echo
echo Make sure the Oxygen application or server is closed before proceeding.
echo Press ENTER when ready...
read W </dev/tty

"$OXYGEN_JAVA" -cp target/classes com.oxygenxml.patcher.log4j.Patcher "$STRATEGY"
echo Leaving..
