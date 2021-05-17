FROM maven:3.8.1-jdk-11

# WORKDIR /app
# COPY src ./src
# COPY pom.xml .

# CMD mvn spring-boot:run
# FROM openjdk:11-jdk-alpine

COPY target/search-service-0.0.1-SNAPSHOT.jar search-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "search-service-0.0.1-SNAPSHOT.jar"]