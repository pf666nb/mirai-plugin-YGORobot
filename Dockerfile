FROM openjdk:11


WORKDIR /app

RUN buildDeps='git vim screen' \
 && cd /app \
 && apt-get -y update \
 && apt-get install  -y $buildDeps \
 && wget -O mcl.zip https://github.com/iTXTech/mirai-console-loader/releases/download/v1.2.2/mcl-1.2.2.zip \
 && unzip mcl.zip \
 && chmod +x mcl \
 && rm -rf mcl.zip

RUN HRobotVersion='3.2.1' \
 && cd /app \
 && wget -O h-robot.jar https://github.com/happysnaker/mirai-plugin-HRobot/releases/download/$HRobotVersion/plugin-$HRobotVersion-SNAPSHOT.mirai.jar \
 && mkdir plugins && mv h-robot.jar plugins \
