FROM --platform=$BUILDPLATFORM maven:3.9-amazoncorretto-25 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM --platform=$TARGETPLATFORM amazoncorretto:25
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
