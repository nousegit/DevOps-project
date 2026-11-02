FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/todo-app-1.0.0.jar /app/todo-app.jar
ENV PORT=7070
EXPOSE 7070
USER 10001
ENTRYPOINT ["java", "-jar", "/app/todo-app.jar"]
