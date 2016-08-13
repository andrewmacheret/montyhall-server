from java:8-jre-alpine

# Copy the built jar into the current dir
COPY *.jar app.jar

# Touch it to set a current timestamp
# (not necessary but nice to have)
RUN touch app.war

# Expose a port for the java server
EXPOSE 80

# Run the java server
CMD ["java", "-jar", "app.jar", "--server.port=80"]
