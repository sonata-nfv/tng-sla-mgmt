# tng-sla-mgmt
The 5GTANGO SP SLA Management repository.  

The SLA Management repository includes the SLAs descriptors and schemata, as well as all mechanisms that need to be implemented. 
   
The schema files are written in JSON-Schema      
   
The mechanisms included in the tng-sla-mgmt include:
*  SLA Template Generator : creates initial and tailored SLA templates for the service provider, and the ﬁnal SLA itself
*  SLA Mapping Mechanism:  mapping between the high-level requirements described by the end-user and the low-level requirements described by the service provider
*  SLA Parameter Analyzer: decide whether the process of the mapping mechanism should be done or not
*  SLA Mapping Repository: store the correlations between the high-level and the low-level requirements (mapping mechanism output)
*  SLA Monitor Analyzer: Compare the QoS parameters from the Mapping Repository, with the computed monitoring measurements and check if there is any violation  

More details can be found on the [5GTANGO SLA Manager Wiki Page](https://github.com/sonata-nfv/tng-sla-mgmt/wiki)

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
The following shows how to run SLA management framework:
* First, make sure there is a network service descriptor in the 5GTANGO Catalogue
* Also there is a corresponding policy descriptor for the specific network service descriptor
* For the generation of a SLA Template use the API call provided below
* For editing a SLA Template use the API call provided below
* For modifying a SLA Template use the API call provided below


## API Documentation

### SLA Templates Management 
The REST interface to the SLA Templates Management supports the following :

**Generate a new template [GET]** 
* Allows to create a new sla template. It takes as input the applicable NS uuid, a template name, and an expiration date. It might include a TemplateId or not.
```sh
  curl "http://localhost:8080/tng-sla-mgmt/api/v1/slas/templategeneration?nsd_uuid=<>&templateName=<>&expireDate=<>"
```
  or
  Use the above link direct in a browser (Mozilla Firefox is the prefered one)   
  
**Edit a SLA Template [GET]**
* Allows to update the template identified by the sla template id (uuid) by *editing specific fields in the template*
```sh
  curl "http://localhost:8080/tng-sla-mgmt/api/v1/slas/edit/templates?uuid=<>&field=<>&old_value=<>&value=<>"
```  
or
  Use the above link direct in a browser (Mozilla Firefox is the prefered one)
  
Note that if old_value and/or value parameters in the above link are in a String format, '<>' must be applied to each one of them.   

**Modify a SLA Template**
* Modify an existing template [GET]
    * Updates the template identified by the sla template id (uuid) by *adding new objectives in the template*
```sh
  curl "http://localhost:8080/tng-sla-mgmt/api/v1/slas/edit/templates/modify?sla_uuid=<>&objectives=<>&objectives=<>&slo_value=<>&slo_value=<>&slo_definition=<>&slo_definition=<>&slo_unit=%&<>&slo_unit=<>&metric=<>&expression=<>&expression_unit=<>&rate=<>&metric=<>&expression=<>&expression_unit=<>&rate=<>&parameter_unit=<>&parameter_definition=<>&parameter_name=<>&parameter_value=<>&parameter_unit=<>&parameter_definition=<>&parameter_name=<>&parameter_value=<>&parameter_unit=<>"
```   
  or
  Use the above link direct in a browser (Mozilla Firefox is the prefered one)   
  

## Development

To contribute to the development of the 5GTANGO SLA Manager, you may use the very same development workflow as for any other 5GTANGO Github project. That is, you have to fork the repository and create pull requests. Moreover, all discussions regarding the 5GTANGO SLAs take place on GitHub, and NOT on the wiki.

### Contributing

You may contribute to the SLA Manager similar to other 5GTANGO (sub-) projects, i.e. by creating pull requests.

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

