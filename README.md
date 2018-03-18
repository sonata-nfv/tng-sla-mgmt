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

## Installation
ToDo

## API Documentation
`wip`

### SLA Templates Management 
The REST interface to the SLA Templates Management supports the following :
* Create a new template [POST]  
	* Allows to create a new sla template. It takes as input the applicable NS uuid, a template name, and an expiration date. It might include a TemplateId or not.
    * `curl "http://tng-sla-manager:8080/tng-sla-mgmt/slas/templategeneration?nsd_uuid=<nsd-uuid>&templateName=<template-name>&expireDate=<date-of-expiration>"`
	
* Modify an existing template [PUT]
    * Updates the template identified by the sla template id (uuid).
	* `curl "http://tng-sla-manager:8080/tng-sla-mgmt/slas/templates?uuid=<sla-template-uuid>"`


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

