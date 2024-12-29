FROM openjdk:17

EXPOSE 8080

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} kbo-ticketing.jar

ENTRYPOINT ["java", \
 "-javaagent:/root/pinpoint-agent-2.5.1/pinpoint-bootstrap-2.5.1.jar", \
 "-Dpinpoint.applicationName=kbo-ticketing", \
 "-Dspring.profiles.active=prod", \
 "-jar", "/kbo-ticketing.jar"]