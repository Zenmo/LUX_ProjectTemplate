#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Replaces the tokens "ProjectTemplate" and "projecttemplate" with a project
    name (and its lowercase form) in both file/folder NAMES and file CONTENTS,
    recursively, starting from the folder where this script lives.

.DESCRIPTION
    - "ProjectTemplate" (PascalCase) -> the -ProjectName value (as typed)
    - "projecttemplate" (lowercase)  -> the -ProjectNameLower value

    Replacement is CASE-SENSITIVE. Content replacement skips likely-binary
    files. Folders named in -ExcludeFolders (default: data_Generic, .git) are skipped
    entirely, with their subfolders and files. The script skips itself.

    IMPORTANT - close apps and sync first:
      A folder canNOT be renamed while a file inside it is open in an app
      (Excel, AnyLogic, a File Explorer preview, etc.). Cloud sync (OneDrive /
      SharePoint / Dropbox) can also interfere and leave an empty duplicate
      folder behind. This script now does a PRE-FLIGHT LOCK CHECK and refuses
      to start if any file it would touch is open. For safest results, close
      Excel/AnyLogic and either pause sync or run on a local (non-synced) copy.

.PARAMETER ProjectName
    The replacement for "ProjectTemplate". Popup asks for it in -Interactive.

.PARAMETER ProjectNameLower
    The replacement for "projecttemplate". Defaults to ToLower().

.PARAMETER Root
    Folder to process. Defaults to the folder containing this script.

.PARAMETER ExcludeFolders
    Folder names to skip entirely (with subfolders). Default: data_Generic, .git.

.PARAMETER DryRun
    Show what would change without modifying anything.

.PARAMETER SkipLockCheck
    Skip the pre-flight open-file check (not recommended).

.PARAMETER Interactive
    Set by the .bat launcher: input popup, confirmation, and pause at the end.

.EXAMPLE
    .\Rename-ProjectTemplate.ps1 -ProjectName "MyCoolApp" -DryRun
#>

[CmdletBinding()]
param(
    [Parameter(Position = 0)]
    [string]$ProjectName,

    [Parameter(Position = 1)]
    [string]$ProjectNameLower,

    [string]$Root = $PSScriptRoot,

    [string[]]$ExcludeFolders = @('data_Generic', '.git'),

    [switch]$DryRun,

    [switch]$SkipLockCheck,

    [switch]$Interactive
)

# --- Interactive input popup ----------------------------------------------

# A name is invalid if it contains spaces or characters that are illegal in
# Windows file/folder names ( \ / : * ? " < > | ), or has no uppercase letter.
function Get-InvalidReason {
    param([string]$Name)
    if ([string]::IsNullOrWhiteSpace($Name)) { return "empty" }
    if ($Name -match '\s')                   { return "spaces are not allowed" }
    if ($Name -match '[\\/:*?"<>|]')         { return 'these characters are not allowed: \ / : * ? " < > |' }
    if ($Name -cnotmatch '[A-Z]')            { return "it must contain at least one capital letter" }
    return $null
}

if ($Interactive) {
    Add-Type -AssemblyName Microsoft.VisualBasic
    while ($true) {
        if ([string]::IsNullOrWhiteSpace($ProjectName)) {
            $ProjectName = [Microsoft.VisualBasic.Interaction]::InputBox(
                "Enter the project name (with the capitalization you want).`nNo spaces or special characters, and at least one capital letter.",
                "Rename ProjectTemplate", "")
        }
        if ([string]::IsNullOrWhiteSpace($ProjectName)) { break }   # cancelled / blank

        $reason = Get-InvalidReason $ProjectName
        if (-not $reason) { break }                                 # valid -> continue

        Add-Type -AssemblyName System.Windows.Forms
        [System.Windows.Forms.MessageBox]::Show(
            "Invalid project name '$ProjectName' - $reason.`n`nPlease try again.",
            "Invalid name", 'OK', 'Warning') | Out-Null
        $ProjectName = ""   # force re-prompt
    }
}

if ([string]::IsNullOrWhiteSpace($ProjectName)) {
    Write-Host "No project name entered - nothing to do." -ForegroundColor Yellow
    if ($Interactive) { Read-Host "Press Enter to close" }
    return
}

# Validate again for the scripted (non-popup) path too.
$reason = Get-InvalidReason $ProjectName
if ($reason) {
    Write-Host "Invalid project name '$ProjectName' - $reason." -ForegroundColor Red
    if ($Interactive) { Read-Host "Press Enter to close" }
    return
}

# --- Setup -----------------------------------------------------------------

if ([string]::IsNullOrWhiteSpace($ProjectNameLower)) { $ProjectNameLower = $ProjectName.ToLower() }
if ([string]::IsNullOrWhiteSpace($Root)) { $Root = (Get-Location).Path }
$Root = $Root.TrimEnd('\', '/')

# Case-sensitive token -> replacement pairs (an array, because hashtable keys
# are case-INSENSITIVE and would collide).
$replacements = @(
    @{ Find = 'ProjectTemplate'; Replace = $ProjectName },
    @{ Find = 'projecttemplate'; Replace = $ProjectNameLower }
)

$selfPath = $MyInvocation.MyCommand.Path

function Convert-Tokens {
    param([string]$Text)
    foreach ($pair in $replacements) { $Text = $Text.Replace($pair.Find, $pair.Replace) }
    return $Text
}

function Test-Excluded {
    param([string]$FullName)
    if ($ExcludeFolders.Count -eq 0) { return $false }
    $rel = $FullName.Substring($Root.Length).TrimStart('\', '/')
    foreach ($segment in ($rel -split '[\\/]')) {
        foreach ($ex in $ExcludeFolders) { if ($segment -eq $ex) { return $true } }
    }
    return $false
}

# Returns $true only for a genuine sharing violation (file open in another app).
function Test-FileLocked {
    param([string]$Path)
    try {
        $fs = [System.IO.File]::Open($Path, [System.IO.FileMode]::Open,
              [System.IO.FileAccess]::Read, [System.IO.FileShare]::None)
        $fs.Close(); $fs.Dispose()
        return $false
    }
    catch [System.IO.IOException] { return $true }
    catch { return $false }   # permission/other -> not an "open in app" lock
}

# --- Gather the files we care about (non-excluded, not this script) --------

$allFiles = @(Get-ChildItem -LiteralPath $Root -Recurse -File -Force -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -ne $selfPath -and -not (Test-Excluded $_.FullName) })

# --- PRE-FLIGHT: refuse to start if anything is open -----------------------

if (-not $SkipLockCheck -and -not $DryRun) {
    Write-Host "Checking for open/locked files..." -ForegroundColor DarkGray
    $locked = @($allFiles | Where-Object { Test-FileLocked $_.FullName })

    if ($locked.Count -gt 0) {
        Write-Host ""
        Write-Host "These files are OPEN in another program and would cause a partial rename:" -ForegroundColor Red
        $locked | ForEach-Object { Write-Host "   $($_.FullName)" -ForegroundColor Red }
        Write-Host ""
        Write-Host "Close Excel / AnyLogic / Explorer previews (and consider pausing OneDrive)," -ForegroundColor Yellow
        Write-Host "then run again. Nothing was changed." -ForegroundColor Yellow

        if ($Interactive) {
            Add-Type -AssemblyName System.Windows.Forms
            $list = ($locked | Select-Object -First 15 | ForEach-Object { $_.Name }) -join "`n"
            [System.Windows.Forms.MessageBox]::Show(
                "These files are open in another program:`n`n$list`n`nClose them (Excel, AnyLogic, Explorer preview) and run again. Nothing was changed.",
                "Cannot start - files are open", 'OK', 'Error') | Out-Null
            Read-Host "Press Enter to close"
        }
        return
    }
}

# --- Confirmation (interactive only) --------------------------------------

if ($Interactive -and -not $DryRun) {
    Add-Type -AssemblyName System.Windows.Forms
    $exText = if ($ExcludeFolders.Count) { $ExcludeFolders -join ', ' } else { '(none)' }
    $msg = "Folder:`n$Root`n`n" +
           "ProjectTemplate  ->  $ProjectName`n" +
           "projecttemplate  ->  $ProjectNameLower`n`n" +
           "Excluded folders: $exText`n`n" +
           "This edits file contents AND renames files/folders in place. Continue?"
    if ([System.Windows.Forms.MessageBox]::Show($msg, "Confirm", 'YesNo', 'Warning') -ne 'Yes') {
        Write-Host "Cancelled." -ForegroundColor Yellow
        Read-Host "Press Enter to close"
        return
    }
}

Write-Host "Root folder : $Root"
Write-Host "ProjectName : $ProjectName  (ProjectTemplate)"
Write-Host "Lowercase   : $ProjectNameLower  (projecttemplate)"
Write-Host "Excluding   : $($ExcludeFolders -join ', ')"
if ($DryRun) { Write-Host "MODE        : DRY RUN (no changes will be made)" -ForegroundColor Yellow }
Write-Host ""

$contentChanged = 0
$namesChanged   = 0
$failed         = @()

# --- 1) Replace tokens inside file CONTENTS --------------------------------

foreach ($file in $allFiles) {
    try { $original = [System.IO.File]::ReadAllText($file.FullName) }
    catch { Write-Warning "Could not read (skipped): $($file.FullName)"; continue }

    if ($original.IndexOf([char]0) -ge 0) { continue }   # skip binaries (xlsx, alpx, etc.)

    $updated = Convert-Tokens $original
    if ($updated -ne $original) {
        if ($DryRun) { Write-Host "[content] would update: $($file.FullName)" -ForegroundColor Cyan }
        else {
            try {
                [System.IO.File]::WriteAllText($file.FullName, $updated)
                Write-Host "[content] updated: $($file.FullName)"
            }
            catch { Write-Warning "Could not write: $($file.FullName)"; $failed += $file.FullName; continue }
        }
        $contentChanged++
    }
}

# --- 2) Rename FILES and FOLDERS (deepest first) ---------------------------

$items = @(Get-ChildItem -LiteralPath $Root -Recurse -Force -ErrorAction SilentlyContinue) |
    Where-Object { $_.FullName -ne $selfPath -and -not (Test-Excluded $_.FullName) } |
    Sort-Object { ($_.FullName -split '[\\/]').Count } -Descending

foreach ($item in $items) {
    $newName = Convert-Tokens $item.Name
    if ($newName -eq $item.Name) { continue }

    if ($DryRun) {
        Write-Host "[rename]  would rename: $($item.FullName)  ->  $newName" -ForegroundColor Cyan
        $namesChanged++
        continue
    }

    try {
        Rename-Item -LiteralPath $item.FullName -NewName $newName -ErrorAction Stop
        # Verify the rename actually took effect.
        $newFull = Join-Path (Split-Path $item.FullName -Parent) $newName
        if (Test-Path -LiteralPath $newFull) {
            Write-Host "[rename]  $($item.Name)  ->  $newName"
            $namesChanged++
        }
        else {
            Write-Warning "Rename reported success but target is missing: $($item.FullName)"
            $failed += $item.FullName
        }
    }
    catch {
        Write-Warning "Could not rename '$($item.FullName)': $($_.Exception.Message)"
        $failed += $item.FullName
    }
}

# --- Summary ---------------------------------------------------------------

Write-Host ""
Write-Host "Done. Content changes: $contentChanged | Items renamed: $namesChanged" -ForegroundColor Green
if ($failed.Count -gt 0) {
    Write-Host ""
    Write-Host "$($failed.Count) item(s) FAILED (likely open in an app or blocked by sync):" -ForegroundColor Red
    $failed | ForEach-Object { Write-Host "   $_" -ForegroundColor Red }
    Write-Host "Close the apps / pause OneDrive and run again on the affected items." -ForegroundColor Yellow
}
if ($DryRun) { Write-Host "(Dry run - nothing was actually modified.)" -ForegroundColor Yellow }

if ($Interactive) { Write-Host ""; Read-Host "Press Enter to close" }
