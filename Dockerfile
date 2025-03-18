FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/Stargate-ApiGateway-0.0.1-SNAPSHOT.jar app.jar
COPY .env .
EXPOSE 7900
ENTRYPOINT ["java", "-jar", "app.jar"]