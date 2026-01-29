# PowerShell script to compile and run Java Swing application

Write-Host "Downloading PostgreSQL JDBC Driver..." -ForegroundColor Green
if (!(Test-Path "lib")) {
    New-Item -ItemType Directory -Path "lib" | Out-Null
}

if (!(Test-Path "lib\postgresql-42.7.1.jar")) {
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar" -OutFile "lib\postgresql-42.7.1.jar"
}

Write-Host "Compiling Java files..." -ForegroundColor Green
if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# Get all Java files
$javaFiles = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

# Compile
javac -encoding UTF-8 -d bin -cp "lib\postgresql-42.7.1.jar" $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    Read-Host "Press Enter to continue"
    exit 1
}

Write-Host "Copying resources..." -ForegroundColor Green
Copy-Item "src\database.properties" -Destination "bin\" -Force

Write-Host "Running application..." -ForegroundColor Green
java -cp "bin;lib\postgresql-42.7.1.jar" com.oop.project.Main

Read-Host "Press Enter to continue"
