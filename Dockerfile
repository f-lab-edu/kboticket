FROM openjdk:17

EXPOSE 8080
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} kbo-ticketing.jar

ENTRYPOINT ["java",
            "-javaagent:/pinpoint/pinpoint-bootstrap-2.5.1.jar",
            "-Dpinpoint.agentId=kbo-agent",
            "-Dpinpoint.applicationName=kbo-ticketing",
            "-jar", "/kbo-ticketing.jar"]
