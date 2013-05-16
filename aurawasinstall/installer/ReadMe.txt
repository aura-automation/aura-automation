1: Unzip Aura-WasInstall.zip in C:\ to create C:\Aura-WasInstall
2: Set JAVA_HOME in install.bat
3: Unzip CZM90ML-WinInstallationManager.zip in temp location, 
4: Run install.exe to Install Installation Manager in location C:\IBM\InstallationManager
5: Unzip CZM9KML-Part1.zip CZM9LML-Part2.zip CZM9MML-Part3.zip in location C:\Aura-WasInstall\packages\CZM9KML-Part1, all in one folder
6: Open file C:\Aura-WasInstall\installer\install.properties
   check the location are correct
   repoDirectoryName=C:/Aura-WasInstall/packages/CZM9KML-Part1
   IMDirectoryName=C:/IBM/InstallationManager/eclipse/tools
7: Run install.bat install 
   This should install WebSphere in location C:/IBM/WebSphere8/AppServer
   Create Deployment Manager and Node and start these 2 process as well
