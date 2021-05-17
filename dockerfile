FROM maven:3.8.1-jdk-11

COPY target/search-service-0.0.1-SNAPSHOT.jar search-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "search-service-0.0.1-SNAPSHOT.jar"]