FROM openjdk:11
LABEL maintainer="gs97ahn@naver.com"

ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]