<p align="center"><img src="https://github.com/sonata-nfv/tng-api-gtw/wiki/images/sonata-5gtango-logo-500px.png" /></p>

# tng-sla-mgmt [![Build Status](https://jenkins.sonata-nfv.eu/buildStatus/icon?job=tng-sla-mgmt/master)](https://jenkins.sonata-nfv.eu/job/tng-sla-mgmt/job/master/)   [![Join the chat at https://gitter.im/sonata-nfv/5gtango-sp](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sonata-nfv/5gtango-sp)

5GTANGO's SLA management framework is part of the SONATA powered by 5GTANGO Service Platform. The SLA Management repository includes the SLAs descriptors
examples and schemas, as well as all mechanisms that are implemented. The schema files are written in JSON-Schema, and the mechanisms included in the
tng-sla-mgmt for the first release includes the `SLA Template Generator`. It's purpose is to create initial and tailored SLA templates driven from the operator.
The SLA Templates are then available to the Customers, in order to select the desired one during the NS instantiation process. Agreements are also available
to both operator and customer, in order to manage them accordingly.

The violation of a SLA agreement is important to the customer. For this reason, while monitoring data are gathered by the monitoring manager, are then published
to the SLA manager in case of a SLA violation. The afforementioned process is happening through publishing messages in the Message Queue framework (RabbitMQ) that is
used in the project for communication purposes between each micro service. An example of an SLA violation can be the following. In this release, availability among others,
is a supported metric by the monitoring manager. Different values of service's availability can be signed in the SLA agreement. If 98% is chosen by the customer, 
this is translated into maximum downtime of his/her service 1.5 seconds in a windows of 60 secs. IF this limit is reached, an alert from the monitoring manager is produced.
As SLA manager is a consumer of that topic in the MQ, it will consume the alert and mark the agreement as 'VIOLATED'.
 
## Dependencies

### Programming Language
The first release of the SLA Manager has been programmed using JAVA (JDK8). Jersey RESTful Web Services framework is extensively used for the SLA Manager API programming.

### Frameworks
*  Jersey - RESTful Web Services in Java - Version 1.19 (CDDL, XXXXX )
    *  jersey-servlet : 1.19
	*  jersey-json : 1.19
	*  jersey-client : 1.19
*  Apache MAVEN  (Apache 2.0)
    *  maven-compiler-plugin : 2.3.2
	*  maven-checkstyle-plugin : 2.13
*  Apache Tomcat - Version 8.5  (Apache 2.0)
	
### Libraries
*  genson : 0.99 (Apache 2.0)
*  org.json : 20180130 (The JSON License)
*  snakeyaml : 1.21 (Apache 2.0)
*  yamlbeans : 1.13 (MIT)
*  httpclient : 4.5.5 (Apache 2.0)
*  postgresql : 9.1-901.jdbc4 (PostgreSQL Licence)
*  amqp-client :  5.2.0 (Apache 2.0, GPL 2.0, Mozilla Public License)

## Build and run tng-sla-mgmt locally (Container mode using Docker)

```
git clone https://github.com/sonata-nfv/tng-sla-mgmt
docker-compose up --build -d
```


## Configuration

The following configurations are definied into the Dockerfile [here](https://github.com/sonata-nfv/tng-sla-mgmt/blob/master/sla-template-generator/Dockerfile)
*  PostgreSQL 
    *  Specify database host
	*  Specify databse name
	*  Specify authentication
	    *  database username
		*  database password
*  RabbitMQ
    *  Specify RabbitMQ server
	*  Specify the RabbitMQ exchange topic
	
*  5GTANGO Catalogue - Specify the 5GTANGO Catalogue base url


## Usage

The following shows how to use SLA Manager: 

* First, make sure there is a network service descriptor in the 5GTANGO Catalogue - More information on how to upload a Network Service in 5GTANGO Catalogue
 can be found [here](https://github.com/sonata-nfv/tng-cat).
* Then, you can create a SLA template using the end point provided in API's Reference section.
* If you have installed the whole 5GTANGO Service Platform, you can then instantiate a service.
* Choosing one SLA template in the instantiation process, the SLA agreement will be automatically created and appear in the 5GTANGO'S PORTAL.
* At a next stage monitoring manager is gathering data for your service, and if a violation is accured, you will receive a message in the Message Bus.
* This information will be stored in sla manager's database and your SLA agreement status will marked as violated.
* You can then terminate the service and see your SLA agreement marked as TERMINATED

### API References

We have specified this micro-service's API in a swagger-formated file. Please check it [here](https://github.com/sonata-nfv/tng-sla-mgmt/blob/master/doc/sla_rest_api_model.json) 
or on the [SLA Manager WIKI page](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/API-Specification).

### Database

The SLA Manager is using [PosgreSQL](https://www.postgresql.org/) as internal database.  
The database includes the following tables:     
*  `ns_template` - stores and manages correlations between sla templates and network services.
*  `cust_sla` - stores and manages correlations between slas, instatiated network services and the customers. it is also used to manage the Agreements's informations.
*  `sla_violations` - stores and manages all the SLA violations information per network service instance.


## Style guide

Our style guide is really simple:
*  We try to follow a Clean Code philosophy in as much as possible, i.e., classes and methods should do one thing only, have the least number of parameters possible, etc.;
*  we use one (1) Tab for identation.

## Tests
*  Unit Tests are triggered automatically when building the project, and are defined in the [SLA Manager's test folder](https://github.com/ekapassa/tng-sla-mgmt/tree/master/sla-template-generator/src/test/java/eu/tng).
*  Checkstyle Tests are triggered automatically when building the project, and are defined in the [SLA Manager's Checkstyle folder](https://github.com/ekapassa/tng-sla-mgmt/tree/master/sla-template-generator/src/main/resources/Checkstyle).
*  Wider scope (integration and functional) tests involving this micro-service are defined in tng-tests.


## Versioning
*  The first release of the SLA Manager does not support versioning reagrding the code. In the future we can maybe use [SemVer](http://semver.org/) for this kind of versioning.
*  The most up-to-date container version is v4. For the container versions available, see the [link to tags on this repository](https://github.com/sonata-nfv/tng-sla-mgmt/releases).
	
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

## License
All tng-sla-mgmt components are published under Apache 2.0 license. Please see the LICENSE file [here](https://github.com/ekapassa/tng-sla-mgmt/blob/master/LICENSE) for more details.

## Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.
*  Evgenia Kapassa (@ekapassa)
*  Marios Touloupou (@mtouloup)

## Feedback-Chanel
* GitHub issues

