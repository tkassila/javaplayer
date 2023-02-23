#! /usr/bin/sh
export JAVA_HOME=/home/tk/.sdkman/candidates/java/11.0.18.fx-librca/
# java -version
export ESPEAK_HOME="."
export PATH=$ESPEAK_HOME:$PATH
echo $ESPEAK_HOME
export FXSCALE="-Dprism.allowhidpi=true -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.0"
echo $FXSCALE
java -version
# echo java -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.win.uiScaleX=2.5 -Dsun.java2d.win.uiScaleY=2.5 -cp ./lib/jsoup-1.14.2.jar:./lib/gson-2.8.9.jar:./lib/espeak-1.0.3.jar:./lib/freetts-1.2.2.jar:./lib/cmudict04.jar:./lib/cmulex.jar:./lib/cmutimelex.jar:./lib/cmu_time_awb.jar:./lib/cmu_us_kal.jar:./lib/en_us.jar:./lib/freetts-jsapi10.jar:./lib/jsapi.jar:./lib/mbrola.jar:./javafxplayer.jar:$CLASSPATH com.metait.javafxplayer.PlayerApplication
DISPLAY=:0 java -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.win.uiScaleX=2.5 -Dsun.java2d.win.uiScaleY=2.5 -cp ./javafxplayer.jar:./lib/jsoup-1.14.2.jar:./lib/gson-2.8.9.jar:./lib/espeak-1.0.3.jar:./lib/freetts-1.2.2.jar:./lib/cmudict04.jar:./lib/cmulex.jar:./lib/cmutimelex.jar:./lib/cmu_time_awb.jar:./lib/cmu_us_kal.jar:./lib/en_us.jar:./lib/freetts-jsapi10.jar:./lib/jsapi.jar:./lib/mbrola.jar:$CLASSPATH com.metait.javafxplayer.PlayerApplication
# java -cp /home/tk/Documents/Java/project/javafx/javafxplayer/out/artifacts/javafxplayer.jar com.metait.javafxplayer.testapp.HelloWorld
