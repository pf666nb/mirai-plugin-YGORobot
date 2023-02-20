FROM happysnaker/h-robot-base:1.0

# 插件
ARG VERSION
COPY output/plugin-$VERSION-SNAPSHOT.mirai.jar /app/h-robot-v$VERSION.jar

RUN cd /app  \
 && mkdir plugins  \
 && mv h-robot-v$VERSION.jar plugins


# docker build --build-arg VERSION=3.4.1 -t happysnaker/h-robot:3.4.1  -t happysnaker/h-robot:latest .