FROM openjdk:17

EXPOSE 8080
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} kbo-ticketing.jar
COPY /root/pinpoint-agent-2.5.1/pinpoint-bootstrap-2.5.1.jar /pinpoint/pinpoint-bootstrap-2.5.1.jar

ENTRYPOINT ["java",
            "-javaagent:/pinpoint/pinpoint-bootstrap-2.5.1.jar",
            "-Dpinpoint.agentId=kbo-agent",
            "-Dpinpoint.applicationName=kbo-ticketing",
            "-jar", "/kbo-ticketing.jar"]

