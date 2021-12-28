# Oxygen XML Patch Tool for Apache Log4j vulnerability CVE-2021-44228, CVE-2021-45046 and CVE-2021-45105
This is a tool that updates the log4j version 2 library to version 2.17 in an:
 - Oxygen XML Editor/Author/Developer standalone installation, or
 - Oxygen XML Editor/Author/Developer plugin for Eclipse installation, or
 - Oxygen XML Web Author for "all platforms" installation

The recommended Oxygen versions to apply this tool on range from 16.1 to 24.0 inclusive.

For newer Oxygen versions, like 22.1, 23.1 or 24.0 there are kits available on the Oxygen website that contain the log4j 2.16 library (resolves CVE-2021-44228 and CVE-2021-45046), at https://www.oxygenxml.com/software_archive.html and https://www.oxygenxml.com/download.html respectively.

It will not work for Web Author or Content Fusion. For these please see the security advisory at https://www.oxygenxml.com/security/advisory/CVE-2021-44228.html to determine the appropriate action. For Oxygen XML Content Fusion you should use this script: https://github.com/oxygenxml/content-fusion-log4j-patcher


## Download 
You can download the tool directly from GitHub, using this link.
https://github.com/oxygenxml/oxygen-log4j-patcher/archive/refs/heads/main.zip

Unzip it to a directory of your choice. Keep track of the directory where you have unzipped the file.

## How to apply it

### On Windows:

 1. Make sure the Oxygen application is closed.
 1. Start a "Command Prompt" window with administrative privileges. For this press the "Start" button, type `cmd`, then choose "Run as administrator" from the menu.
 1. Change directory to the directory of the 'patch.bat' file. For example, if you extracted the archive in your 'Downloads' folder, type `cd %USERPROFILE%/Downloads/oxygen-log4j-patcher-main` in the "Command Prompt" window and press ENTER.
 1. Type `patch.bat` in the "Command Prompt" window and press ENTER.
 1. Follow the instructions given by the script. 
   
### On Mac:
 1. Make sure you run as an user with administrator privileges.
 1. Change directory to the directory of the 'patch.sh' file. (`cd /Users/user/Downloads/oxygen-log4j-patcher-main` for instance ) 
 1. Type `sh patch.sh` in the terminal and press ENTER.
 1. Follow the instructions given by the script.
    
### On Linux:
 1. If the Oxygen application has been installed as a superuser, then type in a terminal: "sudo -s" in order to open a terminal with enough rights.
 1. Change directory to the directory of the 'patch.sh' file. (`cd ~/Downloads/oxygen-log4j-patcher-main` for instance )
 1. Type `sh patch.sh` in the terminal and press ENTER.
 1. Follow the instructions given by the script.


### Limitations:
 1. For Oxygen XML Web Author, the patch won't affect custom data directory and custom DITA-OT directory if they are out of the installation directory.
