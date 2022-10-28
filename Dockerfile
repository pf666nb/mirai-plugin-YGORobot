FROM happysnaker/h-robot-base:1.0

# 插件
ARG VERSION
COPY output/plugin-$VERSION-SNAPSHOT.mirai.jar /app/h-robot-v$VERSION.jar

RUN cd /app  \
 && mkdir plugins  \
 && mv h-robot-v$VERSION.jar plugins