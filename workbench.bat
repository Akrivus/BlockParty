@echo off
setlocal
set "BLOCK_PARTY_WORKBENCH_ARGS=%*"
call "%~dp0gradlew.bat" workbench
exit /b %ERRORLEVEL%
