FROM tomcat:8.0-jre8-alpine

RUN rm -rf /usr/local/tomcat/webapps/*
ADD target/api.war /usr/local/tomcat/webapps/ROOT.war

RUN addgroup tomcatusers && adduser -D tomcat && adduser tomcat tomcatusers
RUN chown -R tomcat:tomcatusers /usr/local/tomcat && \
	chgrp -R 0 /usr/local/tomcat && \
	chmod -R g=u /usr/local/tomcat
USER tomcat