package eu.tng.service_api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateTemplateModel {

    @XmlElement public String param1;
    @XmlElement public String param2;
}