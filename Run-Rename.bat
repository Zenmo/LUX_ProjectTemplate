@echo off
REM Double-click this file to run the rename in THIS folder.
REM It launches the PowerShell script with an input popup and bypasses
REM the execution policy for this one run only (no system changes).

cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0Rename-ProjectTemplate.ps1" -Interactive

REM Safety net: keep the window open if PowerShell failed to start at all.
if errorlevel 1 pause
