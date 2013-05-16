@echo off
@echo off
setlocal

cd %~dp0

set ANT_HOME=opt\apache-ant-1.7.1
set CLASSPATH=
set ANT_OPTS="-Xmx1024m"

set path=C:\Aura-WasInstall\installer\opt\apache-ant-1.7.1\bin

set JAVA_HOME=C:\IBM\WebSphere7\AppServer\java

ant -f install-using-groovy.xml %*