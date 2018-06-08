# Use an official Maven image
FROM maven:3.5-jdk-8-alpine

# Copy the sla-template-generator directory contents into the container at /sla-template-generator
ADD sla-template-generator /sla-template-generator

# Set the working directory to /sla-template-generator
WORKDIR /sla-template-generator

# Set Environment Variables
ENV DATABASE_URL postgres://tangodefault:tango@postgres:5432/gatekeeper
ENV POSTGRES_PASSWORD tango
ENV POSTGRES_USER tangodefault
ENV DATABASE_HOST postgres
ENV DATABASE_PORT 5432
ENV MQSERVER_URL=amqp://guest:guest@broker:5672
ENV CATALOGUES_URL http://tng-cat:4011/catalogues/api/v2/network-services/

# Run maven install project
RUN mvn clean install

# New container with tomcat8
FROM tomcat:8.5-alpine

# Copy the artifact generated on first container to this one
COPY --from=0 /sla-template-generator/target/tng-sla-mgmt.war /usr/local/tomcat/webapps/

# Make port 8080 available outside this container
EXPOSE 8080

