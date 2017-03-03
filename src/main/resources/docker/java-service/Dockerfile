FROM frolvlad/alpine-oraclejdk8:slim
EXPOSE 8080
VOLUME /tmp
ADD cxf-spring-boot-starter-system-tests-*.jar app.jar
RUN sh -c 'touch /app.jar && apk add --no-cache curl'
HEALTHCHECK --interval=10s --timeout=5s \
  CMD curl -f http://localhost:8080/weather/Weather?wsdl || exit 1
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
