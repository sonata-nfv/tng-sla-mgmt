package eu.tng.service_api;

import java.sql.Timestamp;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JwtTokenDecode {
	
	final static Logger logger = LogManager.getLogger();


	public static JSONObject DecodeToken(String token) {
		String jwtToken = token;
		String[] split_string = jwtToken.split("\\.");
		String base64EncodedHeader = split_string[0];
		String base64EncodedBody = split_string[1];
		String base64EncodedSignature = split_string[2];

		Base64 base64Url = new Base64(true);
		/*
		 * System.out.println("~~~~~~~~~ JWT Header ~~~~~~~"); String header = new
		 * String(base64Url.decode(base64EncodedHeader));
		 * System.out.println("JWT Header : " + header);
		 */

		/** ~~~~~~~~~ JWT Body ~~~~~~~ **/
		String sbody = new String(base64Url.decode(base64EncodedBody));

		JSONParser parser = new JSONParser();
		JSONObject auth_info = null;
		try {
			auth_info = (JSONObject) parser.parse(sbody);

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Get JSON jwt body";
			String message = "JWT body feched succesfully! --> " + sbody;
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		} catch (ParseException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Get JSON jwt body";
			String message = "Error fetching jwt body --> " + e;
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return auth_info;
	}

}
