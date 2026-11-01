FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /opt/app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY ../src ./src
RUN mvn clean package -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/app.war"]