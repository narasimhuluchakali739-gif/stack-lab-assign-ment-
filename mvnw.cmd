@echo off
setlocal
if not defined JAVA_HOME (
  where java >nul 2>nul
  if errorlevel 1 (
    echo Java JDK 21 is required. Install a JDK, then set JAVA_HOME to its install folder.
    exit /b 1
  )
  for /f "delims=" %%J in ('where java') do if not defined JAVA_EXE set "JAVA_EXE=%%J"
  for %%I in ("%JAVA_EXE%") do set "JAVA_HOME=%%~dpI.."
)
if not exist "%JAVA_HOME%\bin\java.exe" (
  echo JAVA_HOME does not point to a JDK: %JAVA_HOME%
  echo Set JAVA_HOME to the JDK install folder, not its bin folder.
  exit /b 1
)
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (mvn %* & exit /b %ERRORLEVEL%)
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\apache-maven-3.9.9"
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $d=Join-Path $env:USERPROFILE '.m2\wrapper'; New-Item -ItemType Directory -Force -Path $d | Out-Null; $z=Join-Path $d 'maven.zip'; $tmp=Join-Path $d 'extract'; $m=Join-Path $d 'apache-maven-3.9.9'; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip' -OutFile $z; if (Test-Path $tmp) { Remove-Item -LiteralPath $tmp -Recurse -Force }; New-Item -ItemType Directory -Path $tmp | Out-Null; Add-Type -AssemblyName System.IO.Compression.FileSystem; [System.IO.Compression.ZipFile]::ExtractToDirectory($z,$tmp); if (Test-Path $m) { Remove-Item -LiteralPath $m -Recurse -Force }; Move-Item -LiteralPath (Join-Path $tmp 'apache-maven-3.9.9') -Destination $m; Remove-Item -LiteralPath $tmp -Recurse -Force; Remove-Item -LiteralPath $z -Force"
  if errorlevel 1 exit /b 1
)
"%MAVEN_HOME%\bin\mvn.cmd" %*
