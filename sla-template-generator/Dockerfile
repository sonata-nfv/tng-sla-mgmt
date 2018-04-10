# Use an official Maven image
FROM maven:3.5-jdk-8-alpine

# Copy the sla-template-generator directory contents into the container at /sla-template-generator
ADD sla-template-generator /sla-template-generator

# Set the working directory to /sla-template-generator
WORKDIR /sla-template-generator

# Run maven install project
RUN mvn clean install

#Download Tomcat zip
RUN curl -O http://mirrors.myaegean.gr/apache/tomcat/tomcat-8/v8.5.30/bin/apache-tomcat-8.5.30.tar.gz

#Unzip Tomcat
RUN tar xzf apache-tomcat-8.5.30.tar.gz

#Copy war file to Tomcat directory
RUN cp target/tng-sla-mgmt.war apache-tomcat-8.5.30/webapps/

#Start Tomcat Webserver
CMD apache-tomcat-8.5.30/bin/startup.sh && tail -f apache-tomcat-8.5.30/logs/catalina.out

# Make port 8080 available outside this container
EXPOSE 8080
