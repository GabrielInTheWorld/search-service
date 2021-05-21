# FROM maven:3.8.1-jdk-11

# WORKDIR /app
# COPY pom.xml .
# RUN mvn dependency:resolve
# COPY src src
# RUN mvn package -DskipTests
# CMD ["mvn", "spring-boot:run"]

FROM node:13

WORKDIR /app

RUN rm -rf node_modules
RUN rm -rf package-lock.json

COPY src/node/package.json package.json
RUN npm install

# Application lays in /app/src
COPY src/node ./

CMD [ "npm", "start" ]