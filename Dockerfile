FROM m.daocloud.io/docker.io/library/maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM m.daocloud.io/docker.io/library/tomcat:9.0-jdk17-temurin-noble
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/online-exam-system.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
