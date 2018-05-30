package eu.tng.template_gen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CreateTemplate {
	
	Nsd getNsd = new Nsd();

	@SuppressWarnings("unchecked")
	public JSONObject createTemplate(String nsd_uuid, String templateName, String expireDate, String guaranteeID) {

		/** get network service descriptor for the given nsId */
		GetGuarantee guarantee = new GetGuarantee();
		guarantee.getGuarantee(guaranteeID);
		/** get network service descriptor for the given nsId */
		GetNsd nsd = new GetNsd();
		nsd.getNSD(nsd_uuid);
		
		/** GENERATE THE TEMPLATE **/
		/**************************/

		/** useful variables **/
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // iso date format yyyy-MM-dd'T'HH:mm'Z'
		df.setTimeZone(tz);

		/** current date */		
		Date date = new Date();
		String offered_date = df.format(date);
				
		/** valid until date */
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dateInString = expireDate;
		Date date2 = null;
		try {
			date2 = formatter.parse(dateInString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String validUntil = df.format(date2);
		
		
		/** generate the template */
		// ** root element **/
		JSONObject root = new JSONObject();
		root.put("descriptor_schema",
				"https://raw.githubusercontent.com/sonata-nfv/tng-schema/master/service-descriptor/nsd-schema.yml");
		root.put("vendor", "tango-sla-mgmt");
		root.put("name", templateName);
		root.put("version", "0.1");
		root.put("author", "Evgenia Kapassa, Marios Touloupou");
		root.put("description", "");
		/** sla_template object **/
		JSONObject sla_template = new JSONObject();
		sla_template.put("template_name", templateName);
		sla_template.put("offered_date", offered_date);
		sla_template.put("valid_until", validUntil);
		root.put("sla_template", sla_template);
		/** ns object **/
		JSONObject ns = new JSONObject();
		ns.put("ns_uuid", nsd_uuid);
		ns.put("ns_name", getNsd.getName());
		ns.put("ns_vendor", getNsd.getVendor());
		ns.put("ns_version", getNsd.getVersion());
		ns.put("ns_description", getNsd.getDescription());
		sla_template.put("ns", ns);
		/** guaranteeTerms array **/
		JSONArray guaranteeTerms = new JSONArray();
		JSONObject guaranteeTerms_obj = new JSONObject();
		guaranteeTerms_obj = guarantee.getGuarantee(guaranteeID);
		guaranteeTerms.add(guaranteeTerms_obj);

		ns.put("guaranteeTerms", guaranteeTerms);
	
		return root;

	}


}
