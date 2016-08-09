from java:8-jre-alpine

COPY *.jar app.jar
RUN touch app.war
EXPOSE 80
CMD ["java", "-jar", "app.jar", "--server.port=80"]
