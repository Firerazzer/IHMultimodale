rem @echo off
SET CURRENTDIR="%cd%"
set javaSrc=".\MoteurFusion\src"
set javaLib=".\MoteurFusion\lib"
javac -d bin -cp %javaLib%\*.jar %javaSrc%\sra\interpreter\*.java
javac -d bin -sourcepath %javaSrc% -cp %javaLib%\*.jar %javaSrc%\controller\*.java


cd /d sra5
start cmd /k sra_on.bat
cd /d %CURRENTDIR%

cd /d %CURRENTDIR%/icar
start cmd /k Icarivy.bat
cd /d %CURRENTDIR%

start cmd /k Palette.bat

cd /d bin
start cmd /k Probe.bat
java controller.MainController
cd /d %CURRENTDIR%

cd /d %CURRENTDIR%