rem @echo off
SET CURRENTDIR="%cd%"
set javaSrc=".\MoteurFusion\src"
set javaLib=".\MoteurFusion\lib"
javac -d bin -cp %javaLib%\*.jar %javaSrc%\sra\interpreter\*.java
javac -d bin -sourcepath %javaSrc% -cp %javaLib%\*.jar %javaSrc%\controller\*.java

cd /d %CURRENTDIR%
java .\bin\controller\MainController