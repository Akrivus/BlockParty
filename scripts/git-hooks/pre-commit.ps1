$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
Set-Location $repoRoot

$jdk21 = "C:\Program Files\Java\jdk-21.0.11"
if (Test-Path (Join-Path $jdk21 "bin\java.exe")) {
    $env:JAVA_HOME = $jdk21
}

Write-Host "Running Block Party pre-commit guardrail: phase1Compliance"
& .\gradlew.bat phase1Compliance --no-daemon
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}
