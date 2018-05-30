# Use an official Maven image
FROM maven:3.5-jdk-8-alpine

# Copy the sla-template-generator directory contents into the container at /sla-template-generator
ADD sla-template-generator /sla-template-generator

# Set the working directory to /sla-template-generator
WORKDIR /sla-template-generator

# Run maven install project
RUN mvn clean install

# New container with tomcat8
FROM tomcat:8.5-alpine

# Copy the artifact generated on first container to this one
COPY --from=0 /sla-template-generator/target/tng-sla-mgmt.war /usr/local/tomcat/webapps/

# Make port 8080 available outside this container
EXPOSE 8080

