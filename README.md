# cxf-spring-boot-starter-system-tests
System Tests for the [cxf-spring-boot-starter](https://github.com/codecentric/cxf-spring-boot-starter) including a reverse proxy and the Spring Boot project using the starter both inside [Docker](https://www.docker.com/) Containers, run with [testcontainers-java](https://github.com/testcontainers/testcontainers-java).

[![Build Status](https://travis-ci.org/marcopaga/cxf-spring-boot-starter-system-tests.svg?branch=master)](https://travis-ci.org/marcopaga/cxf-spring-boot-starter-system-tests)

Here you can find the infrastructure for the current setup:

![Infrastructure Diagram](infrastructure.jpeg)

# Local python installation

## Create a python virtualenv
    virtualenv ~/.virtualenvs/boto

## boto und boto3 installieren
    pip install boto
    pip install boto3

# Before working on the project
    
You need to make sure that the python virtualenv is activated. In order to use amazon web services you need to provide the sepcific access keys. 

    source ~/.virtualenvs/boto/bin/activate

    AWS_ACCESS_KEY_ID
    AWS_SECRET_ACCESS_KEY