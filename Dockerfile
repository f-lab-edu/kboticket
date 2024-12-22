FROM openjdk:17

EXPOSE 8080

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} kbo-ticketing.jar

ENTRYPOINT ["java","-jar", \
 "-javaagent:/usr/local/pinpoint-agent-2.5.2/pinpoint-bootstrap-2.5.2.jar", \
 "-Dpinpoint.applicationName=kbo-ticketing", \
 "-Dpinpoint.config=/usr/local/pinpoint-agent-2.5.2/pinpoint-root.config", \
 "-Dspring.profiles.active=prod", \
 "/app.jar"]