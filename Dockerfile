FROM maven

WORKDIR /app

ADD . /app


RUN mvn clean install

RUN curl -O http://mirrors.myaegean.gr/apache/tomcat/tomcat-8/v8.5.28/bin/apache-tomcat-8.5.28.tar.gz
RUN tar xzf apache-tomcat-8.5.28.tar.gz
COPY target/tng-sla-mgmt.war apache-tomcat-8.5.28/webapps/
CMD apache-tomcat-8.5.28/bin/startup.sh && tail -f apache-tomcat-8.5.28/logs/catalina.out

EXPOSE 8080




