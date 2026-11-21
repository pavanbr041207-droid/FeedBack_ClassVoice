# UNTESTED in S-01 (no Docker in the build sandbox). Used for free container hosts later.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -r -u 1001 classvoice
COPY --from=build /app/target/classvoice.jar app.jar
USER classvoice
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xmx256m -XX:+UseSerialGC"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
