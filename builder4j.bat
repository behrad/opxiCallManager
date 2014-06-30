@echo.
@echo ==================================================================
@echo Builder4J Packaging Utility with Apache Ant 1.6
@echo By Jrad (behrad@dev.java.net)
@echo ==================================================================
@echo.
@echo off

if "%1" == ""   goto ERROR
if "%1" == "-r" goto RUN
if "%1" == "-p" goto PACKAGE
if "%1" == "-n" goto N2A
if "%1" == "-h" goto J2H


:DEFAULT
%java_home%\bin\java.exe -cp ".\lib\ant\ant.jar;.\lib\ant\ant-nodeps.jar;%java_home%\lib\tools.jar" org.apache.tools.ant.Main -f build.xml %1
goto END


:ERROR
@echo Usage: This utility has several options of usage in your workspace,
@echo.
@echo 1)To run a Java class try: "builder4j -r [ProjectHomeDir] your.class.name"
@echo 2)To compile and package your workspace try: "builder4j -p [ProjectHomeDir] SarFileName"
@echo 3)To convert from a native file encoding to ascii try: "builder4j -n inputdir outputdir [UTF-8|cp1256]"
@echo 4)To generate project Javadocs and hylighted HTML source files try: "builder4j -h javasourcedir outputdir
@echo.
@echo.
@echo Read Usage and Try Again!
goto END


:RUN
if "%3" == "" goto RUN_INSIDE
%java_home%\bin\java.exe -cp ".\lib\ant\ant.jar;.\lib\ant\ant-nodeps.jar;%java_home%\lib\tools.jar" org.apache.tools.ant.Main -Dbase.dir=%2 -Dmodule=%3 -f build.xml run
goto END
:RUN_INSIDE
%java_home%\bin\java.exe -cp ".\lib\ant\ant.jar;.\lib\ant\ant-nodeps.jar;%java_home%\lib\tools.jar" org.apache.tools.ant.Main -Dmodule=%2 -f build.xml run
goto END


:PACKAGE
if "%3" == "" goto PACKAGE_INSIDE
%java_home%\bin\java.exe -cp ".\lib\ant\ant.jar;.\lib\ant\ant-nodeps.jar;%java_home%\lib\tools.jar" org.apache.tools.ant.Main -Dbase.dir=%2 -Djar.file.name=%3 -f build.xml hbm-gen
goto END
:PACKAGE_INSIDE
%java_home%\bin\java.exe -cp ".\lib\ant\ant.jar;.\lib\ant\ant-nodeps.jar;%java_home%\lib\tools.jar" org.apache.tools.ant.Main -Djar.file.name=%2 -f build.xml hbm-gen
goto END

:N2A
if "%4" == "" goto N2A2
%java_home%\bin\java.exe -cp ".\lib\ant\ant.jar;.\lib\ant\ant-nodeps.jar;%java_home%\lib\tools.jar" org.apache.tools.ant.Main -Dinput.dir=%2 -Doutput.dir=%3 -Dencoding=%4 -f build.xml n2a
goto END
:N2A2
%java_home%\bin\java.exe -cp ".\lib\ant\ant.jar;.\lib\ant\ant-nodeps.jar;%java_home%\lib\tools.jar" org.apache.tools.ant.Main -Dinput.dir=%2 -Doutput.dir=%3 -Dencoding=UTF-8 -f build.xml n2a
goto END


:J2H
%java_home%\bin\java.exe -cp ".\lib\ant\ant.jar;.\lib\ant\ant-nodeps.jar;%java_home%\lib\tools.jar" org.apache.tools.ant.Main -Djava.src.dir=%2 -Dhtml.src.dir=%3 -f build.xml j2h
goto END


:END