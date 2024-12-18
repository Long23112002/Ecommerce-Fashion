FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY services/images/config/dev/flyway.conf /app/services/images/config/dev/flyway.conf
RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests -Pskip-flyway
FROM eclipse-temurin:17-jdk-alpine
#FROM openjdk:10.0.2
WORKDIR /app
COPY --from=build /app/target/ecommerce-fashion-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

