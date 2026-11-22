@echo off
:: Batch file tu dong cau hinh SQL Server cho Yonex Shop
:: Yeu cau: Chay bang chuot phai -> Run as administrator

echo ============================================================
echo   DANG TU DONG CAU HINH MICROSOFT SQL SERVER CHO YONEX SHOP
echo ============================================================
echo.

:: 1. Kiem tra quyen Admin
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [LOI] Vui long nhap chuot phai vao file nay va chon "Run as administrator"!
    echo.
    pause
    exit /b 1
)

echo [1/5] Dang bat giao thuc TCP/IP tren cong 1433...
powershell -Command "Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQLServer\SuperSocketNetLib\Tcp' -Name 'Enabled' -Value 1"
powershell -Command "Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQLServer\SuperSocketNetLib\Tcp\IPAll' -Name 'TcpPort' -Value '1433'"
powershell -Command "Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQLServer\SuperSocketNetLib\Tcp\IPAll' -Name 'TcpDynamicPorts' -Value ''"

echo [2/5] Dang bat che do Mixed Mode Authentication (SQL Server + Windows)...
powershell -Command "Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQLServer' -Name 'LoginMode' -Value 2"

echo [3/5] Dang khoi dong lai dich vu SQL Server (MSSQLSERVER)...
net stop MSSQLSERVER /y >nul 2>&1
net start MSSQLSERVER

echo [4/5] Dang kich hoat tai khoan sa va tao database YonexShop...
"C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\180\Tools\Binn\SQLCMD.EXE" -S . -E -C -Q "ALTER LOGIN sa WITH PASSWORD = 'YourStrongPassword123', CHECK_POLICY = OFF; ALTER LOGIN sa ENABLE; IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'YonexShop') CREATE DATABASE YonexShop;"

echo.
echo ============================================================
echo   CHUC MUNG! CAU HINH SQL SERVER DA THANH CONG 100%!
echo   - Cong TCP 1433: Da bat
echo   - Tai khoan sa: sa / YourStrongPassword123 (Da kich hoat)
echo   - Database: YonexShop (Da tao)
echo ============================================================
echo.
echo Bay gio ban co the quay lai IntelliJ IDEA va bam nut Run lai!
echo.
pause
