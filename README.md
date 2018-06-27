# tng-sla-mgmt
The 5GTANGO SP SLA Management repository.  

The SLA Management repository includes the SLAs descriptors and schemata, as well as all mechanisms that need to be implemented. 
   
The schema files are written in JSON-Schema      
   
The mechanisms included in the tng-sla-mgmt include:
*  SLA Template Generator : creates initial and tailored SLA templates for the service provider, and the ﬁnal SLA itself
*  SLA Mapping Mechanism:  mapping between the high-level requirements described by the end-user and the low-level requirements described by the service provider


## Prerequisites to run locally
Before moving on, make sure you have also installed Apache Maven Project the latest and Apache Tomcat 8.5

Install Apache Apache Maven Project
```sh
  apt-cache search maven
  sudo apt-get install maven
```

Install Apache Apache Tomcat
You can use this tutorial here:
```sh
  https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04
```

Install 5GTANGO Catalogue
Follow the the installation guide of the following repository:
```sh
  https://github.com/sonata-nfv/tng-cat
```

Or, if you have docker and docker-compose installed, you can run:
```sh
  docker-compose up
```

## Usage

The following shows how to use SLA Manager: 

* First, make sure there is a network service descriptor in the 5GTANGO Catalogue - More information on how to upload a Network Service in 5GTANGO Catalogue
 can be found [here](https://github.com/sonata-nfv/tng-cat).

## API Documentation

### SLA Templates

|           Action          | HTTP Method |                  Endpoint            |  
| --------------------------| ----------- | --------------------------------------- |  
| Create a new SLA Template |    `POST`   | `curl -X POST -H "Content-type:application/x-www-form-urlencoded" -d "nsd_uuid=<>&guaranteeId=<>&expireDate=<>&templateName=<>" http://localhost:8080tng-sla-mgmt/api/slas/v1/templates` |  
| Get existing SLA Template |    `GET`    | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/templates/` |  
| Get specific SLA Template based on uuid | `GET`    | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/templates/{sla_uuid}` |  
| Delete a SLA Template     |    `DELETE` | `curl -X DELETE http://localhost:8080/tng-sla-mgmt/api/slas/v1/templates/{sla_uuid}` |  


### SLA Agreements

|           Action           | HTTP Method |                  Endpoint            |  
| -------------------------- | ----------- | --------------------------------------- |  
| Get existing Agreements| `GET`    | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/agreements/` | 
| Get specific Agreement details based on uuid | `GET`    | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/agreements/{sla_uuid}` |   
| Get Agreements per (instatiated) NS   | `GET`    | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/agreements/service/{ns_uuid}` |  
| Get Agreement guarantee terms| `GET`  | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/agreements/guarantee-terms/{sla_uuid}` |  


### SLA Management

|           Action          | HTTP Method |                  Endpoint              |  
| --------------------------| ----------- | --------------------------------------- |  
| Access a predefined list of Service Guarantees (SLOs)| `GET` | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/mgmt/guaranteesList` |  
| Access a list with all the ns template correlations | `GET`  | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/templates/` |  
| Access a list with NS that have associated templates | `GET` | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/templates/true` |  
| Access a list with NS that do not have associated templates yet| `GET` | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/templates/false` |  
| Access a list with NS that have associated agreements| `GET`   | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/agreements/true` |  
| Access a list with NS that do not have associated agreements yet| `GET` | `curl -H "Content-type:application/json" http://localhost:8080/tng-sla-mgmt/api/slas/v1/mgmt/services/agreements/false`|  


Expected returned data for all the mentioned endpoint is:

* `HTTP` code `200` (`Ok`) 
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

