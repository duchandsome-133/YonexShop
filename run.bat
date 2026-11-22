@echo off
setlocal
echo ===================================================
echo     DANG KHOI CHAY YONEX SPORTS SHOP (SPRING BOOT)
echo ===================================================
if not defined JAVA_HOME (
    set "JAVA_HOME=C:\Program Files\Java\jdk-22"
)
call "%~dp0mvn.cmd" spring-boot:run
endlocal
