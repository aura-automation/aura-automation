#!/bin/sh

# save current state
PREVIOUS_DIR=`pwd`
PREVIOUS_ANT_HOME=$ANT_HOME
PREVIOUS_CLASSPATH=$CLASSPATH
MY_ANT_VERSION=1.7.1

# now change the dir to the root of the installer
SHELL_NAME=$0
SHELL_PATH=`dirname ${SHELL_NAME}`

if [ "." = "$SHELL_PATH" ]
then
   SHELL_PATH=`pwd`
fi
cd ${SHELL_PATH}

# set ANT_HOME
ANT_HOME=opt/apache-ant-${MY_ANT_VERSION}
export ANT_HOME

# overwrite CLASSPA:q
TH for Ant
CLASSPATH=
export CLASSPATH

# increase memory to 1gb
ANT_OPTS="-Xmx1024m"
export ANT_OPTS

# run the install
chmod +x "opt/apache-ant-${MY_ANT_VERSION}/bin/ant"
opt/apache-ant-${MY_ANT_VERSION}/bin/ant -nouserlib -f install-using-groovy.xml $@
 

# restore previous state
cd ${PREVIOUS_DIR}
ANT_HOME=${PREVIOUS_ANT_HOME}
export ANT_HOME
CLASSPATH=${PREVIOUS_CLASSPATH}
export CLASSPATH