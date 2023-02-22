@ echo off
call \java\jdk11fxcp.bat
java -version
set ESPEAK_HOME="C:\Program Files (x86)\eSpeak\command_line"
SET PATH=%ESPEAK_HOME%;%PATH%
echo %ESPEAK_HOME%
rem java -cp .\lib\jsoup-1.14.2.jar;.\lib\gson-2.8.9.jar;.\lib\espeak-1.0.3.jar;.\lib\freetts-1.2.2.jar;.\lib\cmudict04.jar;.\lib\cmulex.jar;.\lib\cmutimelex.jar;.\lib\cmu_time_awb.jar;.\lib\cmu_us_kal.jar;.\lib\en_us.jar;.\lib\freetts-jsapi10.jar;.\lib\jsapi.jar;.\lib\mbrola.jar;.\javafxplayer-1.0.jar;%CLASSPATH% com.metait.javafxplayer.PlayerApplication
java -cp .\javafxplayer.jar;%CLASSPATH% com.metait.javafxplayer.PlayerApplication
rem com.metait.javafxplayer.testapp.HelloWorld