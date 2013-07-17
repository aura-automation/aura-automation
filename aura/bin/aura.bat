@echo off
setlocal
REM ----------------------------------------------------------------------------
REM Aura Start Up script
REM
REM Required ENV vars:
REM ------------------
REM   JAVA_HOME - location of a JDK home dir
REM   AURA_HOME - location of Aura home dir
REM Optional Vars
REM   AURA_REPO - default is users home dir	
REM ----------------------------------------------------------------------------

if "%AURA_HOME%"=="" (echo "AURA_HOME" not set. Please set.) else (echo "AURA_HOME" set to %AURA_HOME%)
if "%JAVA_HOME%"=="" (echo "JAVA_HOME" not set. Please set.) else (echo "JAVA_HOME" set to %JAVA_HOME%)

set PATH=%AURA_HOME%/opt/ant/bin;%JAVA_HOME%;%PATH%

if "%AURA_REPO%"=="" (set AURA_REPO=%UserProfile%/.aura_repo ) else (echo "AURA_REPO" set to %AURA_REPO%)

set PROPERTIES_LIB=%AURA_HOME%/properties

set WAS_CLASSPATH=%AURA_REPO%/plugins/was-remote/lib/*
set ANT_LIB=%AURA_HOME%/opt/apache-ant-1.7.1/lib/*;%AURA_HOME%/lib/*

"%JAVA_HOME%/bin/java" -classpath "%PROPERTIES_LIB%;%ANT_LIB%;%WAS_CLASSPATH%" -DCURRENT_DIR=$(pwd) -DAURA_HOME=$AURA_HOME -DAURA_REPO=$AURA_REPO org.apache.tools.ant.launch.Launcher -buildfile %AURA_HOME%/var/common/plugin.xml check-was-plugin
if %errorlevel% neq 0 exit /b %errorlevel%

"%JAVA_HOME%/bin/java" -classpath "%PROPERTIES_LIB%;%ANT_LIB%;%WAS_CLASSPATH%" -DCURRENT_DIR=$(pwd) -DAURA_HOME=$AURA_HOME -DAURA_REPO=$AURA_REPO org.apache.tools.ant.launch.Launcher -buildfile %AURA_HOME%/var/was/deploy.xml %*




