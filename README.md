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

# License
All tng-sla-mgmt components are published under Apache 2.0 license. Please see the LICENSE file for more details.

#### Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.
*  Evgenia Kapassa (@ekapassa)
*  Marios Touloupou (@mtouloup)

####  Feedback-Chanel

* You may use the mailing list tango-5g-wp5@lists.atosresearch.eu   
* GitHub issues

