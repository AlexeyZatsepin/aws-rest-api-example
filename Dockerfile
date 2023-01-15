FROM openjdk:17-jdk-slim-buster
MAINTAINER ozatsepin
COPY target/tui-test-task-api-0.0.1-SNAPSHOT.jar tui-test-task-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/tui-test-task-api-0.0.1-SNAPSHOT.jar"]