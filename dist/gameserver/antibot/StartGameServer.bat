@echo off
title L2-Scripts Goddess Of Destruction: Lindvior GS

:start
echo Starting GameServer.
echo.

java -server -Dfile.encoding=UTF-8 -Xms1024m -Xmx2G -cp config:./lib/*:./lib/gameprotect.jar l2s.gameprotect.GameProtect


REM Debug ...
REM java -Dfile.encoding=UTF-8 -cp config;./* -Xmx1G -Xnoclassgc -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7456 l2s.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server restarted ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly ...
echo.
:end
echo.
echo Server terminated ...
echo.

pause
