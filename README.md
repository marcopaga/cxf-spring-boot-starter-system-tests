# cxf-spring-boot-starter-system-tests
System Tests for the [cxf-spring-boot-starter](https://github.com/codecentric/cxf-spring-boot-starter) including a reverse proxy and the Spring Boot project using the starter both inside [Docker](https://www.docker.com/) Containers.

This test project will run a [terraform](https://www.terraform.io/) script on [AWS](https://aws.amazon.com) to create the needed infrastructure. The Servers are tagged by this script to mark the specific roles.
The provisioning of the servers will be handled by [ansible](https://www.ansible.com/). Here we use the approach of  a [dynamic inventory script](https://docs.ansible.com/ansible/intro_dynamic_inventory.html#example-aws-ec2-external-inventory-script) that will retrieve the state using the aws api. This script is using [boto](http://docs.pythonboto.org/en/latest/).

I recommend installing boto and boto3 in a [python virtualenv](http://docs.python-guide.org/en/latest/dev/virtualenvs/) so you don't need to worry abount your current python installation.

# Setup

## Create a python virtualenv
    virtualenv ~/.virtualenvs/boto

## boto und boto3 installieren
    pip install boto
    pip install boto3
    pip install docker-py

# Before working on the project
    
You need to make sure that the python virtualenv is activated. In order to use amazon web services you need to provide the specific access keys
If you are using *IAM* you may want to check if your access key ist active: https://console.aws.amazon.com/iam/home?region=eu-west-1#/users/dev?section=security_credentials

    source ~/.virtualenvs/boto/bin/activate

    AWS_ACCESS_KEY_ID
    AWS_SECRET_ACCESS_KEY

# Open Issues

## Ansible usage of the virtualenv 

Ansible seems to have issues regarding the virtualenv. [Using Python virtualenv with ansible](https://www.zigg.com/2014/using-virtualenv-python-local-ansible.html) addresses this issue for a static inventory. Since I'm using a ec2 dynaimic inventory I can't use this fix... 

Current error message

    failed: [52.214.200.37] (item=1) => {"failed": true, "item": "1", "msg": "Failed to import docker-py - No module named docker. Try `pip install docker-py`"}