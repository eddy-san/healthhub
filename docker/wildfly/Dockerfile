FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -DskipTests clean package

FROM quay.io/wildfly/wildfly:latest-jdk21

ENV JBOSS_HOME=/opt/jboss/wildfly

USER root

RUN mkdir -p ${JBOSS_HOME}/modules/com/microsoft/sqlserver/main

COPY docker/wildfly/modules/com/microsoft/sqlserver/main/module.xml \
     ${JBOSS_HOME}/modules/com/microsoft/sqlserver/main/module.xml

COPY docker/wildfly/modules/com/microsoft/sqlserver/main/mssql-jdbc.jar \
     ${JBOSS_HOME}/modules/com/microsoft/sqlserver/main/mssql-jdbc.jar

COPY docker/wildfly/standalone.xml \
     ${JBOSS_HOME}/standalone/configuration/standalone.xml

COPY --from=builder /build/target/*.war \
     ${JBOSS_HOME}/standalone/deployments/ROOT.war

RUN chown -R 1000:0 ${JBOSS_HOME}

USER 1000

EXPOSE 8080 9990