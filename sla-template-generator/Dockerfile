# Use an official Maven image
FROM maven:3.5-jdk-8-alpine

# Copy the sla-template-generator directory contents into the container at /sla-template-generator
ADD sla-template-generator /sla-template-generator

# Set the working directory to /sla-template-generator
WORKDIR /sla-template-generator

# Run maven install project
RUN mvn clean install -XX:MinHeapFreeRatio=10 \
					  -XX:MaxHeapFreeRatio=70 \
					  -XX:CompressedClassSpaceSize=64m \
					  -XX:ReservedCodeCacheSize=64m \
					  -XX:MaxMetaspaceSize=128 \
					  -Xms128m \
					  -Xmx500m \

# New container with tomcat8
FROM tomcat:8.5-alpine

# Delete existing ROOT folder
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy the artifact generated on first container to this one
COPY --from=0 /sla-template-generator/target/ROOT.war /usr/local/tomcat/webapps/

# Set pgSQL Environment Variables
ENV DATABASE_HOST son-postgres
ENV GTK_DB_NAME gatekeeper
ENV GTK_DB_USER sonatatest
ENV GTK_DB_PASS sonata
ENV DATABASE_PORT 5432


# Set Catalogue Environment Variables
ENV CATALOGUES_URL http://tng-cat:4011/catalogues/api/v2/

# Set MESSAGE BUS Environment Variables
ENV MQSERVER amqp://guest:guest@broker:5672
ENV BROKER_EXCHANGE son-kernel
ENV BROKER_HOST broker

# Set MONITORING URL Environment Variables
ENV MONITORING_URL http://son-monitor-manager:8000/api/v1/

# Make port 8080 available outside this container
EXPOSE 8080

