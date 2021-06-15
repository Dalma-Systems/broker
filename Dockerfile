FROM openjdk:11-jdk-slim

RUN apt-get update && apt-get -y install curl && apt-get clean

ADD broker-api-web/target/dalma-broker-api.jar /app/

EXPOSE 8080

ENTRYPOINT ["java","-jar", "/app/dalma-broker-api.jar"]
