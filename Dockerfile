# syntax=docker/dockerfile:1
FROM --platform=$BUILDPLATFORM maven:3.9-amazoncorretto-25 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY ./.github/workspace/settings.xml .
COPY src ./src

# Get the GitHub Personal Access Token from the environment and then build the package
RUN --mount=type=secret,id=pat \
    mvn clean package --settings ./settings.xml -Dgithub.pat=$(cat /run/secrets/pat)

FROM --platform=$TARGETPLATFORM amazoncorretto:25
WORKDIR /app

ENV SERVER_PORT=8080
ENV MODEL_HOST=http://model-service:8081

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
