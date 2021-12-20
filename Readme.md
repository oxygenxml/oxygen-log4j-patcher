# Oxygen XML Patch Tool for Apache Log4j vulnerability CVE-2021-44228
This is a tool that updates the log4j version 2 library to version 2.16 in an:
 - Oxygen XML Editor/Author/Developer standalone installation, or
 - Oxygen XML Editor/Author/Developer plugin for Eclipse installation, or
 - Oxygen XML Web Author for all installations

The recommended Oxygen versions to apply this tool on range from 16.1 to 22.1 inclusive.

For the newer Oxygen versions, like 23.1 or 24.0 there are already available kits on the Oxygen website that contain the updated log4j library, at https://www.oxygenxml.com/software_archive.html and https://www.oxygenxml.com/download.html respectively, so there is no need to apply this tool, just get use the latest installation kits to update Oxygen.

It will not work for Web Author or Content Fusion. For these please see the security advisory at https://www.oxygenxml.com/security/advisory/CVE-2021-44228.html to determine the appropriate action. For Oxygen XML Content Fusion you should use this script: https://github.com/oxygenxml/content-fusion-log4j-patcher


## Download 
You can download the tool directly from GitHub, using this link.
https://github.com/oxygenxml/oxygen-log4j-patcher/archive/refs/heads/main.zip

Unzip it to a directory of your choice. Keep track of the directory where you have unzipped the file.

## How to apply it

### On Windows:

 1. Make sure the Oxygen application is closed.
 1. Start a terminal with administrative privileges. For this press the "Start" button, type "cmd", then choose "Run as administrator" from the  menu.
 1. Change directory to the directory of the 'patch.bat' file. ('cd c:\user_home\Downloads\oxygen-log4j-patcher')
 1. Type 'patch.bat' in the command terminal and press ENTER.
 1. Follow the instructions given by the script. 
   
### On Mac:
 1. Make sure you run as an user with administrator privileges.
 1. Change directory to the directory of the 'patch.sh' file. ('cd /Users/user/Downloads/oxygen-log4j-patcher' for instance ) 
 1. Type 'sh patch.sh' in the terminal and press ENTER.
 1. Follow the instructions given by the script.
    
### On Linux:
 1. If the Oxygen application has been installed as a superuser, then type in a terminal: "sudo -s" in order to open a terminal with enough rights.
 1. Change directory to the directory of the 'patch.sh' file. ('cd /home/user/downloads/oxygen-log4j-patcher' for instance )
 1. Type 'sh patch.sh' in the terminal and press ENTER.
 1. Follow the instructions given by the script.


### Limitations:
 1. For Oxygen XML Web Author, the patch won't affect custom data directory and custom DITA-OT directory if they are out of the installation directory.