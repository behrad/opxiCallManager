@echo off
REM rmdir "\\opxiMasterServer\c$\Program Files\Micromethod\SIPMethod\sipapps\opxiCallManager" /S /Q
REM copy \java\workspace\opxiCallManager\build\opxiCallManager.sar "\\opxiMasterServer\c$\Program Files\Micromethod\SIPMethod\sipapps"
copy \java\workspace\opxiCallManager\build\opxiCallManager.sar "\\opxiMasterServer\c$"
pause