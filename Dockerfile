FROM openjdk:11-jdk
MAINTAINER SAMEER

COPY jmx/jmx_prometheus_javaagent-0.3.0.jar /jmx/jmx_prometheus_javaagent-0.3.0.jar
COPY jmx/config.yml /jmx/config.yml
RUN chgrp -R 0 /jmx && chmod -R g=u /jmx

USER 1006
ARG JAR_FILE
COPY target/kafka-consumer-producer-1.0.0.jar /app.jar


EXPOSE 8080 8089
ENTRYPOINT ["java", "-Xms256m","-Xmx2048m", "-Djava.security.egd=file:/dev/./urandom" ,"-Dcom.sun.management.jmxremote","-Dcom.sun.management.jmxremote.port=9999","-Dcom.sun.management.jmxremote.authenticate=false","-Dcom.sun.management.jmxremote.ssl=false","-javaagent:/jmx/jmx_prometheus_javaagent-0.3.0.jar=8089:/jmx/config.yml","-Dspring.profiles.active=default","-jar" ,"app.jar"]
