# cxf-spring-boot-starter-system-tests
System Tests for the [cxf-spring-boot-starter](https://github.com/codecentric/cxf-spring-boot-starter) including proxy Nginx and the Spring Boot projekt using the starter both inside [Docker](https://www.docker.com/) Containers, run with [testcontainers-java](https://github.com/testcontainers/testcontainers-java).

[![Build Status](https://travis-ci.org/marcopaga/cxf-spring-boot-starter-system-tests.svg?branch=master)](https://travis-ci.org/marcopaga/cxf-spring-boot-starter-system-tests)


Currently this is a rough starting point. The docker containers are used in an integration test.
The test just works within intellij and not maven. I have to fix some classloading issues.

### HowTo

First run `mvn clean package` which will generate all necessary class files with the help of the cxf-spring-boot-starter-maven-plugin and then generate the needed `target/cxf-spring-boot-starter-system-tests-1.1.1-SNAPSHOT.jar` (Otherwise [you´ll get a FileNotFound](https://github.com/marcopaga/cxf-spring-boot-starter-system-tests/issues/2)).

Now you should be able to run the Test [WeatherServiceEndpointIT.java](https://github.com/marcopaga/cxf-spring-boot-starter-system-tests/blob/master/src/test/java/de/codecentric/cxf/endpoint/WeatherServiceEndpointIT.java) inside Intellij.

### Exploration of Scaling

I'm using haproxy to load-balance the requests between the cxf services. To try it out do:

> cd src/main/resources/docker

> docker-compose up -d

> docker-compose scale java-service=2

To verify the load-balancing have a look at 'http://localhost/env' and see the hostname in the systemEnvironment which will reflect the current docker container name.