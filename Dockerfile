# Use official Tomcat base image
FROM tomcat:10

# Set working directory to Tomcat webapps
WORKDIR /usr/local/tomcat/webapps/

# Copy the built WAR file into the Tomcat webapps directory (don't rename unless needed)
COPY target/trivia-api.war trivia-api.war

# Expose the correct port
EXPOSE 8088

# Start Tomcat
CMD ["catalina.sh", "run"]