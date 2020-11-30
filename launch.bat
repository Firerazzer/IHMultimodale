rem @echo off
SET CURRENTDIR="%cd%"
set javaSrc=".\MoteurFusion\src"
set javaLib=".\MoteurFusion\lib"
javac -d bin -cp %javaLib%\*.jar %javaSrc%\sra\interpreter\*.java
javac -d bin -sourcepath %javaSrc% -cp %javaLib%\*.jar %javaSrc%\controller\*.java


cd /d sra5
start /b sra_on.bat
cd /d %CURRENTDIR%

cd /d %CURRENTDIR%/icar
start /b Icarivy.bat > NUL
cd /d %CURRENTDIR%

start /b processing-java --sketch=%cd%/Palette --run > NUL

cd /d bin
java controller.MainController
cd /d %CURRENTDIR%

cd /d %CURRENTDIR%