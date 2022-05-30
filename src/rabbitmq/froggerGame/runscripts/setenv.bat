@REM ************************************************************************************
@REM Description: run Pingclient
@REM Author: Rui Moreira
@REM Date: 20/02/2019
@REM pwd: /Users/rui/Documents/NetBeansProjects/SD/src/edu/ufp/inf/sd/rmi/helloworld
@REM http://docs.oracle.com/javase/tutorial/rmi/running.html
@REM ************************************************************************************

@REM ======================== Use Shell Parameters ========================
@REM Script usage: setenv <role> (where role should be: server / client)
@SET SCRIPT_ROLE=%1

@REM ======================== CHANGE BELOW ACCORDING YOUR PROJECT and PC SETTINGS ========================
@REM ==== PC STUFF ====
@Set USERNAME=joaop

@Set WORKDRIVE=C
@Set JDK=C:\Program Files (x86)\Java\jre1.8.0_321
@REM These vars will be used to check the output folder (whereto classes are generated)
@SET NETBEANS=NetBeans
@SET INTELLIJ=IntelliJ
@REM Set CURRENT_IDE=%INTELLIJ%
@Set CURRENT_IDE=%INTELLIJ%

@REM ==== JAVA NAMING STUFF ====
@Set JAVAPROJ_NAME=FroggerGame
@set JAVAPROJ=D:\Programacao\IntelliJ\IntelliJProjects\ProjetoSD
@Set PACKAGE=froggerGame
@Set PACKAGE_PREFIX=rabbitmq
@Set PACKAGE_PREFIX_FOLDERS=/rabbitmq
@Set SERVICE_NAME_ON_REGISTRY=froggerGameService
@Set CLIENT_CLASS_PREFIX=froggerGame
@Set SERVER_CLASS_PREFIX=froggerGame
@Set CLIENT_CLASS_POSTFIX=Client
@Set SERVER_CLASS_POSTFIX=Server
@Set SERVANT_IMPL_CLASS_POSTFIX=Impl
@Set QUEUE_NAME_PREFIX=froggergame
@Set EXCHANGE_NAME_PREFIX=logs
@Set PRODUCER_CLASS_PREFIX=EmitLog
@Set CONSUMER_CLASS_PREFIX=ReceiveLogs
@Set OBSERVER_CLASS_PREFIX=ObserverGuiClient
@Set SERVER_CLASS_PREFIX=ServerGui


@REM ==== NETWORK STUFF ====
@Set BROKER_HOST=localhost
@REM Set BROKER_PORT=15672
@Set BROKER_PORT=5672

@REM Set SETUP_CLASS_POSTFIX=Setup
@REM Set SERVANT_ACTIVATABLE_IMPL_CLASS_POSTFIX=ActivatableImpl

@REM ==== NETWORK STUFF ====
@REM Must run http server on codebase host:
@REM Python 2: python -m SimpleHTTPServer 8000
@REM Python 3: python -m http.server 8000
@Set MYLOCALIP=localhost
@REM MYLOCALIP=192.168.56.1
@Set REGISTRY_HOST=%MYLOCALIP%
@Set REGISTRY_PORT=1099
@Set SERVER_RMI_HOST=%REGISTRY_HOST%
@Set SERVER_RMI_PORT=1098
@Set SERVER_CODEBASE_HOST=%SERVER_RMI_HOST%
@Set SERVER_CODEBASE_PORT=8000
@Set CLIENT_RMI_HOST=%REGISTRY_HOST%
@Set CLIENT_RMI_PORT=1097
@Set CLIENT_CODEBASE_HOST=%CLIENT_RMI_HOST%
@Set CLIENT_CODEBASE_PORT=8000

@REM ======================== DO NOT CHANGE AFTER THIS POINT ========================
@Set JAVAPACKAGE=%PACKAGE_PREFIX%.%PACKAGE%
@Set JAVAPACKAGEROLE=%PACKAGE_PREFIX%.%PACKAGE%.%SCRIPT_ROLE%
@Set JAVAPACKAGEPATH=%PACKAGE_PREFIX_FOLDERS%/%PACKAGE%/%SCRIPT_ROLE%
@Set JAVASCRIPTSPATH=%PACKAGE_PREFIX_FOLDERS%/%PACKAGE%/runscripts
@REM Set JAVASECURITYPATH=%PACKAGE_PREFIX_FOLDERS%/%PACKAGE%/securitypolicies
@Set JAVASECURITYPATH=rmi\\%PACKAGE%\\securitypolicies
@Set SERVICE_NAME=%SERVICE_PREFIX%Service
@Set BROKER_QUEUE=%QUEUE_NAME_PREFIX%_queue
@Set BROKER_EXCHANGE=%EXCHANGE_NAME_PREFIX%_exchange
@Set SERVICE_URL=rmi://%REGISTRY_HOST%:%REGISTRY_PORT%/%SERVICE_NAME%

@Set SERVANT_ACTIVATABLE_IMPL_CLASS=%JAVAPACKAGEROLE%.%SERVER_CLASS_PREFIX%%SERVANT_ACTIVATABLE_IMPL_CLASS_POSTFIX%
@Set SERVANT_PERSISTENT_STATE_FILENAME=%SERVICE_PREFIX%Persistent.State

@Set PATH=%PATH%;.;%JDK%\bin

@REM Set JAVAPROJ_CLASSES=build\classes\
IF "%CURRENT_IDE%"=="%NETBEANS%" (
    @Set JAVAPROJ_CLASSES=build\\classes\\
    @Set JAVAPROJ_SRC=src
    @Set JAVAPROJ_DIST=dist
    @Set JAVAPROJ_DIST_LIB=lib
)
IF "%CURRENT_IDE%"=="%INTELLIJ%" (
    @Set JAVAPROJ_CLASSES=out\\production\\%JAVAPROJ_NAME%\\
    @Set JAVAPROJ_SRC=src
    @Set JAVAPROJ_DIST=out\\artifacts\\%JAVAPROJ_NAME%\\
    @Set JAVAPROJ_DIST_LIB=lib
)

@set JAVAPROG_CLASSES_FOLDER=%JAVAPROJ%\%JAVAPROJ_CLASSES%
@set JAVAPROJ_DIST_FOLDER=%JAVAPROJ%\%JAVAPROJ_DIST%
@set JAVAPROJ_JAR_FILE=%JAVAPROJ_NAME%.jar
@Set JAVA_LIB_FOLDER=%JAVAPROJ%\\%JAVAPROJ_DIST_LIB%
@Set JAVA_RABBITMQ_TOOLS=%JAVA_LIB_FOLDER%\\amqp-client-5.11.0.jar;%JAVA_LIB_FOLDER%\\slf4j-api-1.7.30.jar;%JAVA_LIB_FOLDER%\\slf4j-simple-1.7.30.jar

@set CLASSPATH=.;%JAVAPROG_CLASSES_FOLDER%;D:\Programacao\IntelliJ\IntelliJProjects\ProjetoSD\lib\jig-1.7.5.jar;%JAVAPROJ_JAR_FILE%;%JAVA_RABBITMQ_TOOLS%;
@REM @set CLASSPATH=.;%JAVAPROJ_DIST_FOLDER%/%JAVAPROJ_JAR_FILE%.jar

@Set ABSPATH2CLASSES=%JAVAPROJ%\%JAVAPROJ_CLASSES%
@Set ABSPATH2SRC=%JAVAPROJ%\%JAVAPROJ_SRC%
@Set ABSPATH2DIST=%JAVAPROJ%/%JAVAPROJ_DIST%

@REM #java.rmi.server.codebase property specifies the location from which classes of this server can be downloaded.
@REM Set SERVER_CODEBASE=http://%SERVER_CODEBASE_HOST%:%SERVER_CODEBASE_PORT%/%JAVAPROJ_CLASSES%
@REM Set CLIENT_CODEBASE=http://%CLIENT_CODEBASE_HOST%:%CLIENT_CODEBASE_PORT%/%JAVAPROJ_CLASSES%
@Set SERVER_CODEBASE=http://%SERVER_CODEBASE_HOST%:%SERVER_CODEBASE_PORT%/%JAVAPROJ_JAR_FILE%
@Set CLIENT_CODEBASE=http://%CLIENT_CODEBASE_HOST%:%CLIENT_CODEBASE_PORT%/%JAVAPROJ_JAR_FILE%

@REM #Policy tool editor: /Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home/bin/policytool
@REM Set SERVER_SECURITY_POLICY=file:///%JAVAPROJ%/%JAVAPROJ_SRC%/%JAVASECURITYPATH%/serverAllPermition.policy
@REM Set CLIENT_SECURITY_POLICY=file:///%JAVAPROJ%/%JAVAPROJ_SRC%/%JAVASECURITYPATH%/clientAllPermition.policy
@REM Set SETUP_SECURITY_POLICY=file:///%JAVAPROJ%/%JAVAPROJ_SRC%/%JAVASECURITYPATH%/setup.policy
@REM Set RMID_SECURITY_POLICY=file:///%JAVAPROJ%/%JAVAPROJ_SRC%/%JAVASECURITYPATH%/rmid.policy
@REM Set GROUP_SECURITY_POLICY=file:///%JAVAPROJ%/%JAVAPROJ_SRC%/%JAVASECURITYPATH%/group.policy

@Set SERVER_SECURITY_POLICY=file:///%JAVAPROJ%\\%JAVAPROJ_SRC%\\%JAVASECURITYPATH%\\serverAllPermition.policy
@Set CLIENT_SECURITY_POLICY=file:///%JAVAPROJ%\\%JAVAPROJ_SRC%\\%JAVASECURITYPATH%\\clientAllPermition.policy
@Set SETUP_SECURITY_POLICY=file:///%JAVAPROJ%\\%JAVAPROJ_SRC%\\%JAVASECURITYPATH%\\setup.policy
@Set RMID_SECURITY_POLICY=file:///%JAVAPROJ%\\%JAVAPROJ_SRC%\\%JAVASECURITYPATH%\\rmid.policy
@Set GROUP_SECURITY_POLICY=file:///%JAVAPROJ%\\%JAVAPROJ_SRC%\\%JAVASECURITYPATH%\\group.policy

