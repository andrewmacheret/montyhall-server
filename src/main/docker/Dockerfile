from java:8-jre-alpine

# Copy the built jar into the current dir
COPY *.jar app.jar

# Expose a port for the java server
EXPOSE 80

# Run the java server
CMD ["java", "-jar", "app.jar", "--server.port=80"]
