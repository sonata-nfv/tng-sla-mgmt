package eu.tng.service_api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserProfile {
    private String customer_name;
    private String customer_email;

    /**
     * @return the customer_name
     */
    public String getCutomerName() {
        return customer_name;
    }

    /**
     * @param customer_name the customer_name to set
     */
    public void setCustomerName(String customer_name) {
        this.customer_name = customer_name;
    }

    /**
     * @return the customer_email
     */
    public String getCustomerEmail() {
        return customer_email;
    }

    /**
     * @param customer_email the customer_email to set
     */
    public void setCustomerEmail(String customer_email) {
        this.customer_email = customer_email;
    }
}