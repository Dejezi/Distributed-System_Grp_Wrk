@echo off
javac %1
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b
)
java %~n1
pause