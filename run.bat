@echo off
REM Compile and run Java Swing application without Maven

echo Downloading PostgreSQL JDBC Driver...
if not exist "lib" mkdir lib
if not exist "lib\postgresql-42.7.1.jar" (
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar' -OutFile 'lib\postgresql-42.7.1.jar'"
)

echo Compiling Java files...
if not exist "bin" mkdir bin

javac -encoding UTF-8 -d bin -cp "lib\postgresql-42.7.1.jar" ^
    src\com\oop\project\model\*.java ^
    src\com\oop\project\exception\*.java ^
    src\com\oop\project\util\*.java ^
    src\com\oop\project\repository\*.java ^
    src\com\oop\project\repository\file\*.java ^
    src\com\oop\project\repository\db\*.java ^
    src\com\oop\project\service\*.java ^
    src\com\oop\project\ui\*.java ^
    src\com\oop\project\Main.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Copying resources...
if not exist "bin\com\oop\project" mkdir bin\com\oop\project
copy src\database.properties bin\ >nul 2>&1

echo Running application...
java -cp "bin;lib\postgresql-42.7.1.jar" com.oop.project.Main

pause
