FROM openjdk:8-jre-slim
ADD /target/admin-api-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch admin-api-0.0.1-SNAPSHOT.jar'
RUN mkdir /tmp/tomcat static
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 9096