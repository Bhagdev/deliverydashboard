FROM java
MAINTAINER tanmay ambre
COPY ./target/ddapi-0.0.1-SNAPSHOT.jar /usr/local/ddapi/
RUN ls -al /usr/local/ddapi
RUN java -version
RUN echo $JAVA_HOME
EXPOSE 8090
VOLUME /logs
WORKDIR /usr/local/ddapi
ENTRYPOINT java -jar ddapi-0.0.1-SNAPSHOT.jar > /logs/ddapi.system.out.log
