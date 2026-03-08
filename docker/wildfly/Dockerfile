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

COPY target/healthhub-1.0-SNAPSHOT.war \
     ${JBOSS_HOME}/standalone/deployments/healthhub.war

USER 1000