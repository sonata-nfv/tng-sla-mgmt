<p align="center"><img src="https://github.com/sonata-nfv/tng-api-gtw/wiki/images/sonata-5gtango-logo-500px.png" /></p>

# tng-sla-mgmt [![Build Status](https://jenkins.sonata-nfv.eu/buildStatus/icon?job=tng-sla-mgmt/master)](https://jenkins.sonata-nfv.eu/job/tng-sla-mgmt/job/master/)   [![Join the chat at https://gitter.im/sonata-nfv/Lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sonata-nfv/Lobby)

## Overview
5GTANGO's SLA management framework is part of the SONATA powered by 5GTANGO Service Platform.     

The SLA Management repository includes the SLAs descriptors examples and schemas, as well as all mechanisms that are implemented. The schema files are written in JSON-Schema and they are available [here](https://github.com/sonata-nfv/tng-schema/tree/master/sla-template-descriptor). The description of the SLA Descriptors can be found also in the relevant [WiKi page](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/SLA-Descriptors).

The following figure depicts the overall SLA Management Framework architecture. For more details you can see the relevant [WiKi page](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/Architecture).    


<p align="center"><img src="https://github.com/sonata-nfv/tng-sla-mgmt/wiki/images/Y2-Architecture.jpg" width="70%" height="70%"/></p>

Some initial information for the supported features are given below: 

**SLA Templates:**    
Initial SLA Templates are defined. An SLA template refers to an initial advertisement of the provider regarding the attached NS, while it also describes what type of QoS commitments the provider is willing to take. Each template includes a set of speciﬁc Service Level Objectives (SLOs), which deﬁnes the maximum values and thresholds allowed for a set of network and service-speciﬁc parameters, that ar ealso available and re-confgurable [here](https://github.com/sonata-nfv/tng-sla-mgmt/blob/master/sla-template-generator/src/main/resources/slos_list_Y2.json).     

**SLA Agreements:**    
As long as a service is successfully instantiated in the 5GTANGO Service Platform, the Agreement is automatically created. Speciﬁcally, once the deployment of the service is completed, the corresponding SLA template is promoted to an actual agreement that is being enforced (i.e. instance of the SLA template). Once the agreement is established the guaranteed requirements of the service start being monitored and checked for breaches of contract.    

**SLA Violations:**    
The violation of a SLA agreement is important to the customer. For this reason, while monitoring data are gathered by the monitoring manager, are then published
to the SLA manager in case of a SLA violation. An example of an SLA violation can be the following. In this release, availability among others,
is a supported metric by the monitoring manager. Different values of service's availability can be signed in the SLA agreement. If 98% is chosen by the customer, 
this is translated into maximum downtime of his/her service 1.5 seconds in a windows of 60 secs. IF this limit is reached, an alert from the monitoring manager is produced. [here](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/SLA-Violations).    

**License-based SLAs:**    
An important addition to the SLA Manager is the introduction of licenses in the SLAs. 5GTANGO SLA Manager proposes a service-based licensing model, which links a license to a specific customer 
and an instantiated NS, by specifying also the number of allowed NS instances. The model provides three types of licenses: a) trial, which supports limited time of trying the desired
NS before license purchasing, b) public, which comes with no instantiation restrictions, and c) private, which specifies as mandatory the purchase of a license before instantiating a 
NS. It is worth mentioning that licensing is provided "as a service” and it is included into the provided SLAs. More details can be found [here](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/License-based-SLAs).
 
## Documentation
Besides this README file, more documentation is available in the [WiKi](https://github.com/sonata-nfv/tng-sla-mgmt/wiki) belonging to this repository. Additional information are available in the 5GTANGO project's deliverables:
* [5GTANGO D2.2 Architecture Design](https://5gtango.eu/project-outcomes/deliverables/2-uncategorised/31-d2-2-architecture-design.html)
* [5GTANGO D5.1 Service platform operational first prototype](https://5gtango.eu/project-outcomes/deliverables/43-d5-1-service-platform-operational-first-prototype.html)

## Dependencies

`tng-sla-mgmt` expects the following environment:
*   JAVA JDK8
*   Docker >= 1.10 (Apache 2.0)    
*   RabbitMQ >= 3.5 (Mozilla Public License)
*   PostgreSQL >= 3.0 (PostgreSQL Licence)
*   A Catalogue (MongoDB) to where the descriptors can be requested from (https://github.com/sonata-nfv/tng-cat)

`tng-sla-mgmt` has the following dependencies:
*  Jersey - RESTful Web Services in Java - Version 1.19 
    *  jersey-servlet : 1.19
	*  jersey-json : 1.19
	*  jersey-client : 1.19
*  Apache MAVEN  (Apache 2.0)
    *  maven-compiler-plugin : 2.3.2
	*  maven-checkstyle-plugin : 2.13
*  Apache Tomcat - Version 8.5  (Apache 2.0)
*  Libraries
	*  genson : 0.99 (Apache 2.0)
	*  org.json : 20180130 (The JSON License)
	*  snakeyaml : 1.21 (Apache 2.0)
	*  yamlbeans : 1.13 (MIT)
	*  httpclient : 4.5.5 (Apache 2.0)
	*  postgresql : 9.1-901.jdbc4 (PostgreSQL Licence)
	*  amqp-client :  5.2.0 (Apache 2.0, GPL 2.0, Mozilla Public License)

## Installation
You can follow the installation guide [here](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/Installation), for installing the 5GTANGO SLA Manager.     
Enjoy!

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
*  Catalogue - Specify the 5GTANGO Catalogue base url

## Usage
The following shows how to use SLA Manager: 
* First, make sure there is a network service descriptor in the 5GTANGO Catalogue - More information on how to upload a Network Service in 5GTANGO Catalogue
 can be found [here](https://github.com/sonata-nfv/tng-cat ).
* Then, you can create a SLA template using the end point provided in API's Reference section.    
    *  During the SLA Template creation you can define guarantees, expiration date, license information, as well as a desired deployment flavour.
* If you have installed the whole 5GTANGO Service Platform, you can then instantiate a service.
* Choosing one SLA template in the instantiation process, the SLA agreement will be automatically created and appear in the 5GTANGO'S Portal.
* The SLA Manager is checking the license, based on its type (Public, Trial, Private)
* At a next stage monitoring manager is gathering data for your service, and if a violation is accured, you will receive a message in the RabbitMQ.
* This information will be stored in sla manager's database and your SLA agreement status will marked as violated.
* You can then terminate the service and see your SLA agreement marked as TERMINATED    

For more information you can see the relevant [WiKi page](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/SLA-Workflow) related to the SLA workflow.

### API References

For all the available endpoints you can visit the  relevant [SLAM Swagger API documentation](https://github.com/sonata-nfv/tng-sla-mgmt/blob/master/doc/sla_rest_api_model.json) 
or on the Wiki API specification [here](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/API-Specification).  


### Database

The SLA Manager is using PosgtreSQL, for storing SLA records and correlations. The detailed description of all the correlations can be found [here](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/SLA-Correlations).  
The database includes the following tables:     
*  `ns_template` - stores and manages correlations between sla templates and network services.
*  `cust_sla` - stores and manages correlations between slas, instatiated network services and the customers. it is also used to manage the Agreements's informations.
*  `sla_violations` - stores and manages all the SLA violations information per network service instance.
*  `sla_licensing` - stores and manages all the Licensing information per SLA and the corresponding service.

### Logging 
`tng-sla-mgmt` uses the [Apache Log4j 2](http://logging.apache.org/log4j/2.x/) logging services, to produce logs in the 5GTANGO JSON format as described [here](https://git.cs.upb.de/5gtango/UserStories/issues/376) (authentication needed).       

```json
{
  "type": "I",
  "timestamp": "2018-11-26 10:0:00 UTC",
  "component": "tng-sla-mgmt",
  "operation": "sla template creation",
  "message": "temlate created",
  "status": "201"
}
```     

More details can be found in the Wiki page [here](https://github.com/sonata-nfv/tng-sla-mgmt/wiki/Logging) 
    
## Style guide

Our style guide is really simple:
*  We try to follow a Clean Code philosophy in as much as possible, i.e., classes and methods should do one thing only, have the least number of parameters possible, etc.;
*  we use four (4) Tab for identation.

## Tests
*  Unit Tests are triggered automatically when building the project, and are defined in the [SLA Manager's test folder](https://github.com/ekapassa/tng-sla-mgmt/tree/master/sla-template-generator/src/test/java/eu/tng )
*  Checkstyle Tests are triggered automatically when building the project, and are defined in the [SLA Manager's Checkstyle folder](https://github.com/ekapassa/tng-sla-mgmt/tree/master/sla-template-generator/src/main/resources/Checkstyle )
*  Wider scope (integration and functional) tests involving this micro-service are defined in [tng-tests](https://github.com/sonata-nfv/tng-tests )

## Versioning
*  The SLA Manager does not support versioning reagrding the code. In the future we can maybe use [SemVer](http://semver.org/) for this kind of versioning.
*  The most up-to-date container version is v4.0 For the container versions available, see the [link to tags on this repository](https://github.com/sonata-nfv/tng-sla-mgmt/releases).
	
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

## Relevant Publications

* E. Kapassa et al., “Introducing Licensing throughout SLAs in NFV Environment”, 16th International Conference on the Economics of Grids, Clouds, Systems, and Services, Leeds, UK, 2019 - not published yet
* E. Kapassa, M. Touloupou, P. Stavrianos, G. Xilouris, D. Kyriazis "Managing and Optimizing Quality of Service in 5G Environments Across the Complete SLA Lifecycle", Advances in Science, Technology and Engineering Systems Journal, vol. 4, no. 1, pp. 329-342, 2019. 
* M. Touloupou, E. Kapassa et al., “An Integrated SLA Management Framework in a 5G Environment”, 22nd Conference on Innovation in Clouds, Internet and Networks and Workshops (ICIN), Paris, France, 2019.
* E. Kapassa, M. Touloupou, D. Kyriazis, "SLAs in 5G: A Complete Framework Facilitating VNF- and NS- Tailored SLAs Management," 32nd International Conference on Advanced Information Networking and Applications Workshops (WAINA), Krakow, Poland, 2018.
* E. Kapassa, M. Touloupou, A. Mavrogiorgou, D. Kyriazis, "5G & SLAs: Automated proposition and management of agreements towards QoS enforcement," 21st Conference on Innovation in Clouds, Internet and Networks and Workshops (ICIN), Paris, France, 2018.


## Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.
*  Evgenia Kapassa (@ekapassa)
*  Marios Touloupou (@mtouloup)

## Feedback-Chanel
* GitHub issues

