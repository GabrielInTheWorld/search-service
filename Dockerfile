FROM maven:3.8.1-jdk-11

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src src
RUN mvn package -DskipTests
CMD ["mvn", "spring-boot:run"]
