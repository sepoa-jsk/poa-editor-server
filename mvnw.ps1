# Maven Wrapper for PowerShell (Windows)
# mvnw.cmd 에서 호출되거나 직접 실행 가능
param([Parameter(ValueFromRemainingArguments)][string[]]$MvnArgs)

$ErrorActionPreference = 'Stop'

# ── maven-wrapper.properties 읽기 ────────────────────────────────────────────
$BaseDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$PropsFile = Join-Path $BaseDir '.mvn\wrapper\maven-wrapper.properties'

if (-not (Test-Path $PropsFile)) {
    Write-Error "maven-wrapper.properties 파일을 찾을 수 없습니다: $PropsFile"
    exit 1
}

$DistUrl = (Get-Content $PropsFile | Where-Object { $_ -match '^distributionUrl=' }) -replace '^distributionUrl=', ''

if (-not $DistUrl) {
    Write-Error "distributionUrl을 읽을 수 없습니다."
    exit 1
}

# ── Maven 캐시 경로 ───────────────────────────────────────────────────────────
$DistFilename = [System.IO.Path]::GetFileNameWithoutExtension($DistUrl)
$DistsDir     = Join-Path $env:USERPROFILE '.m2\wrapper\dists'
$MavenHome    = Join-Path $DistsDir $DistFilename
$MavenExe     = Join-Path $MavenHome 'bin\mvn.cmd'

# ── Maven 다운로드 (캐시 없을 때만) ──────────────────────────────────────────
if (-not (Test-Path $MavenExe)) {
    Write-Host "Maven 다운로드 중: $DistUrl"
    if (-not (Test-Path $DistsDir)) { New-Item -ItemType Directory -Path $DistsDir -Force | Out-Null }

    $TmpZip = Join-Path $DistsDir 'maven-download.zip'
    try {
        Invoke-WebRequest -Uri $DistUrl -OutFile $TmpZip -UseBasicParsing
    } catch {
        Write-Error "다운로드 실패: $_"
        exit 1
    }

    Expand-Archive -Path $TmpZip -DestinationPath $DistsDir -Force
    Remove-Item $TmpZip -Force
    Write-Host "Maven 설치 완료: $MavenHome"
}

# ── Java 확인 ─────────────────────────────────────────────────────────────────
$JavaExe = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { 'java' }
if (-not (Get-Command $JavaExe -ErrorAction SilentlyContinue)) {
    Write-Error "Java를 찾을 수 없습니다. JAVA_HOME을 설정하거나 Java 17을 설치하세요."
    exit 1
}

# ── Maven 실행 ────────────────────────────────────────────────────────────────
& $MavenExe @MvnArgs
exit $LASTEXITCODE
