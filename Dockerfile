FROM openjdk:11

WORKDIR /app

# 基础组件
RUN buildDeps='git vim screen' \
 && apt-get -y update \
 && apt-get install  -y $buildDeps \
 && rm -rf /etc/localtime \
 && ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# mcl
RUN cd /app \
 && wget -O mcl.zip https://github.com/iTXTech/mirai-console-loader/releases/download/v2.1.1/mcl-2.1.1.zip \
 && unzip mcl.zip \
 && chmod +x mcl \
 && rm -rf mcl.zip

# 插件
COPY output/plugin-3.3-SNAPSHOT.mirai.jar /app/h-robot-v3.3.jar
RUN cd /app  \
 && mkdir plugins  \
 && mv h-robot-v3.3.jar plugins