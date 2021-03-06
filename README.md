# tng-sla-mgmt
The 5GTANGO Service Platform's (SP) SLA Management repository.  

The SLA Management repository includes the SLAs descriptors examples and schemas, as well as all mechanisms that need to be implemented. 
   
The schema files are written in JSON-Schema      
   
The mechanisms included in the tng-sla-mgmt include:
*  SLA Template Generator : Creates initial and tailored SLA templates for the service provider, and the ﬁnal SLA itself
*  SLA Mapping Mechanism:  Mapping between the high-level requirements described by the end-user towards low-level resources needed by the Service Provider


## Prerequisites to run locally

```
	git clone https://github.com/sonata-nfv/tng-sla-mgmt
```

Before moving on, make sure you have also installed Apache Maven Project the latest, Apache Tomcat 8.5, RabbitMQ, PostgreSQL, 5GTANGO Catalogue and Docker

Install Apache Maven Project
```sh
  apt-cache search maven
  sudo apt-get install maven
```

Install Apache Tomcat
You can use this tutorial here:
```sh
  https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04
```

Install 5GTANGO Catalogue
Follow the installation guide of the following repository:
```sh
  https://github.com/sonata-nfv/tng-cat
```

Install RabbitMq as a meesage bus
Follow the installation guide from the official website:
```sh
  https://www.rabbitmq.com/
  Also, you have to create an "Exchange" in the message broker. You can use any name you want, but then set it's inside the DockerFile at the ENV variable "BROKER_EXCHANGE"
```

Install PostgreSQL database
Follow the installation guide from the official website:
```sh
  https://www.postgresql.org/
Make sure you set the correct environment variables in the DockerFile of the "sla-template-generator" directory
```

Finally, install the sla-manager
```
You can either import the maven project into a Java IDE (Eclipse is the prefered one)
or
Build it and run in Docker Container mode

1) docker build -t tng-sla-mgmt -f sla-template-generator/Dockerfile .
2) docker run tng-sla-mgmt -p 8080:8080

You can then access the SLA manager visiting http://<you_machine_ip>:8080

```

## Usage

The following shows how to use SLA Manager: 

* First, make sure there is a network service descriptor in the 5GTANGO Catalogue - More information on how to upload a Network Service in 5GTANGO Catalogue
 can be found [here](https://github.com/sonata-nfv/tng-cat).

## API Documentation

### SLA Templates

|           Action          | HTTP Method |                  Endpoint            |  
| --------------------------| ----------- | --------------------------------------- |  
| Generate a SLA Template |    `POST`   | `curl -X POST -H "Content-type:application/x-www-form-urlencoded" -d "nsd_uuid=<>&guaranteeId=<>&expireDate=<>&templateName=<>" http://<you_machine_ip>:8080tng-sla-mgmt/api/slas/v1/templates` |  
| Get all SLA Templates |    `GET`    | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/templates/` |  
| Get SLA Template by it's uuid | `GET`    | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/templates/{sla_uuid}` |  
| Delete a SLA Template by it's uuid    |    `DELETE` | `curl -X DELETE http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/templates/{sla_uuid}` |  


### SLA Agreements

|           Action           | HTTP Method |                  Endpoint            |  
| -------------------------- | ----------- | --------------------------------------- |  
| Get existing Agreements| `GET`    | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/agreements/` | 
| Get specific Agreement details by it's uuid | `GET`    | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/agreements/{sla_uuid}` |   
| Get Agreements per (instatiated) NS   | `GET`    | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/agreements/service/{ns_uuid}` |  
| Get Agreement's guarantee terms - SLOs| `GET`  | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/agreements/guarantee-terms/{sla_uuid}` |  


### SLA Management

|           Action          | HTTP Method |                  Endpoint              |  
| --------------------------| ----------- | --------------------------------------- |  
| GET a predefined list of Service Guarantees (SLOs)| `GET` | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/mgmt/guaranteesList` |  
| GET the list with correlated NSs-Templates | `GET`  | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/templates/` |  
| GET a list with NSs that have associated templates | `GET` | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/templates/true` |  
| GET a list with NS that do not have associated templates yet| `GET` | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/templates/false` |  
| GET a list with NS that have associated agreements| `GET`   | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/agreements/true` |  
| GET a list with NS that do not have associated agreements yet| `GET` | `curl -H "Content-type:application/json" http://<you_machine_ip>:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/agreements/false`|  


Expected returned Codes:

* `HTTP` code `200` (`Ok`) 
* `HTTP` code `201` (`Created`)
* `HTTP` code `400` (`Bad Request`)
* `HTTP` code `404` (`Not Found`)


## Development

To contribute to the development of the 5GTANGO SLA Manager, you may use the very same development workflow as for any other 5GTANGO Github project. That is, you have to fork the repository and create pull requests. Moreover, all discussions regarding the 5GTANGO SLAs take place on GitHub, and NOT on the wiki.

### Contributing

You may contribute to the SLA Manager you should:

1. Fork [this repository](https://github.com/sonata-nfv/tng-sla-mgmt);
2. Work on your proposed changes, preferably through submiting [issues](https://github.com/sonata-nfv/tng-sla-mgmt/issues);
3. Push changes on your fork;
3. Submit a Pull Request;
4. Follow/answer related [issues](https://github.com/sonata-nfv/tng-sla-mgmt/issues) (see Feedback-Chanel, below).

### CI Integration

All pull requests are automatically tested by Jenkins and will only be accepted if no test is broken.

# License
All tng-sla-mgmt components are published under Apache 2.0 license. Please see the LICENSE file for more details.

#### Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.
*  Evgenia Kapassa (@ekapassa)
*  Marios Touloupou (@mtouloup)

####  Feedback-Chanel

* You may use the mailing list tango-5g-wp5@lists.atosresearch.eu   
* GitHub issues

