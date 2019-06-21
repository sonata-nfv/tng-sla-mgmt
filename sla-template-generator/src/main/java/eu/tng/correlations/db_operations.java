/*
 * 
 *  Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  ALL RIGHTS RESERVED.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Neither the name of the SONATA-NFV, 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  nor the names of its contributors may be used to endorse or promote
 *  products derived from this software without specific prior written
 *  permission.
 *  
 *  This work has been performed in the framework of the SONATA project,
 *  funded by the European Commission under Grant number 671517 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the SONATA
 *  partner consortium (www.sonata-nfv.eu).
 *  
 *  This work has been performed in the framework of the 5GTANGO project,
 *  funded by the European Commission under Grant number 761493 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the 5GTANGO
 *  partner consortium (www.5gtango.eu).
 * 
 */

package eu.tng.correlations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.postgresql.util.PSQLException;

import java.sql.ResultSet;

public class db_operations {

	static Logger logger = LogManager.getLogger();

	static Connection c = null;
	static Statement stmt = null;

	static String class_name = db_operations.class.getSimpleName();

	/**
	 * Connect to PostgreSQL
	 */
	public static boolean connectPostgreSQL() {
		boolean connect = false;
		try {

			/*
			 * c =
			 * DriverManager.getConnection("jdbc:postgresql://localhost:5432/sla-manager",
			 * "postgres", "admin");
			 */

			Class.forName("org.postgresql.Driver");
			c = DriverManager
					.getConnection(
							"jdbc:postgresql://" + System.getenv("DATABASE_HOST") + ":" + System.getenv("DATABASE_PORT")
									+ "/" + System.getenv("GTK_DB_NAME"),
							System.getenv("GTK_DB_USER"), System.getenv("GTK_DB_PASS"));

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "Connecting to SLA Manager's internal database. Class: " + class_name;
			String message = "Connected to the PostgreSQL server successfully.";
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			connect = true;
		}

		catch (SQLException | ClassNotFoundException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Connecting to SLA Manager's internal database. Class: " + class_name;
			String message = "Error connecting to SLA Manager's internal database. ==> " + e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			connect = false;
		}
		return connect;
	}

	/**
	 * Close connection with PostgreSQL
	 */
	public static void closePostgreSQL() {
		try {
			c.close();
		} catch (SQLException e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Closing conection to SLA Manager's internal database. Class: " + class_name;
			String message = "Error closing the connection to SLA Manager's internal database. == > " + e.getMessage();
			String status = String.valueOf(e.getErrorCode());
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}

	/*******************************/
	/** OPERATIONS FOR TEMPLATES **/
	/*******************************/

	/**
	 * Create table if not exist - ns-template correlation
	 */
	public boolean createTableNSTemplate() {
		boolean result = false;
		// Statement stmt = null;

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS ns_template" + "(ID  SERIAL PRIMARY KEY,"
					+ " NS_UUID TEXT NOT NULL, " + "SLA_UUID  TEXT NOT NULL," + "license_type  TEXT NOT NULL,"
					+ "license_exp_date  TEXT NOT NULL," + "allowed_instances  TEXT NOT NULL,"
					+ "license_status  TEXT NOT NULL," + "d_flavour_name TEXT)";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			result = true;
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Creating table for NS - Template correlations. Class: " + class_name;
			String message = "ns_template table Created? " + result;
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return result;
	}

	/**
	 * Insert Record ns-template correlation
	 */
	public boolean insertRecord(String tablename, String ns_uuid, String sla_uuid, String license_type,
			String license_exp_date, String allowed_instances, String license_status, String d_flavour_name) {
		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "INSERT INTO " + tablename
					+ " (ns_uuid,sla_uuid, license_type, license_exp_date, allowed_instances, license_status,d_flavour_name) "
					+ "VALUES ('" + ns_uuid + "','" + sla_uuid + "', '" + license_type + "','" + license_exp_date
					+ "','" + allowed_instances + "','" + license_status + "','" + d_flavour_name + "');";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			result = true;
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Insert record to table for NS - Template correlations. Class: " + class_name;
			String message = "Records ns-template saved successfully? " + result;
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		}
		return result;
	}

	/**
	 * 
	 * @param nsd_uuid
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public static JSONObject selectSlasPerNS(String nsd_uuid) {

		JSONObject slas_obj = new JSONObject();
		JSONArray slas = new JSONArray();

		String sla_uuid = "";
		String sla_name = "";
		String sla_vendor = "";
		String sla_version = "";

		String license_type = "";
		String license_exp_date = "";
		String allowed_instancesString = "";
		int allowed_instances = 0;

		String d_flavor = "";

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ns_template WHERE ns_uuid = '" + nsd_uuid + "';");

			while (rs.next()) {

				sla_uuid = rs.getString("sla_uuid");
				d_flavor = rs.getString("d_flavour_name");
				license_type = rs.getString("license_type");
				license_exp_date = rs.getString("license_exp_date");
				allowed_instancesString = rs.getString("allowed_instances");
				allowed_instances = Integer.parseInt(allowed_instancesString);

				try {
					String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid;
					URL object = new URL(url);

					HttpURLConnection con = (HttpURLConnection) object.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("Accept", "application/json");
					con.setRequestMethod("GET");

					con.getResponseCode();
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					// get the core sla from the catalogue
					JSONParser parser = new JSONParser();
					Object existingTemplates = parser.parse(response.toString());
					JSONObject sla_template = (JSONObject) parser.parse(response.toString());

					JSONObject slad = (JSONObject) sla_template.get("slad");

					sla_name = (String) slad.get("name");
					sla_vendor = (String) slad.get("vendor");
					sla_version = (String) slad.get("version");

				} catch (Exception e) {

					// logging
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					String timestamps = timestamp.toString();
					String operation = "Geting SLAD from catalogue. Class:" + class_name;
					String type = "E";
					String message = "[*] Error: SLA with uuid=" + sla_uuid + " NOT Found!";
					String status = String.valueOf(404);
					logger.error(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type, timestamps, operation, message, status);
				}

				JSONObject sla_info = new JSONObject();
				sla_info.put("uuid", sla_uuid);
				sla_info.put("name", sla_name);
				sla_info.put("vendor", sla_vendor);
				sla_info.put("version", sla_version);
				sla_info.put("license_type", license_type);
				sla_info.put("license_exp_date", license_exp_date);
				sla_info.put("allowed_instances", allowed_instances);
				sla_info.put("d_flavor", d_flavor);

				slas.add(sla_info);
			}

			slas_obj.put("slas", slas);

			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Get associated SLAs for NSD_UUID=" + nsd_uuid + "  Class: " + class_name;
			String message = ("Warning: Nor succesful parsing of sla records from database ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return slas_obj;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getLicenseinfoTemplates(String sla_uuid, String ns_uuid) {

		JSONObject license_info = new JSONObject();
		String license_status = "";
		String license_type = "";
		String license_exp_date = "";
		String allowed_instancesString = "";
		int allowed_instances = 0;

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM ns_template WHERE sla_uuid = '" + sla_uuid + "' AND ns_uuid='" + ns_uuid + "';");

			while (rs.next()) {
				license_status = rs.getString("license_status");
				license_type = rs.getString("license_type");
				license_exp_date = rs.getString("license_exp_date");
				allowed_instancesString = rs.getString("allowed_instances");
				allowed_instances = Integer.parseInt(allowed_instancesString);

				license_info.put("license_status", license_status);
				license_info.put("license_type", license_type);
				license_info.put("license_exp_date", license_exp_date);
				license_info.put("allowed_instances", allowed_instances);
			}
			rs.close();
			stmt.close();
			c.commit();
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get license status. Class: " + class_name;
			String message = ("Error Getting license status ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return license_info;
	}

	/**
	 * 
	 * @param sla_uuid
	 * @param ns_uuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getSpecificFlavour(String ns_uuid, String sla_uuid) {

		JSONObject dflavour_info = new JSONObject();

		String d_flavour_name = "";

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM ns_template WHERE sla_uuid = '" + sla_uuid + "' AND ns_uuid='" + ns_uuid + "';");
			while (rs.next()) {
				d_flavour_name = rs.getString("d_flavour_name");
				dflavour_info.put("d_flavour_name", d_flavour_name);
			}
			dflavour_info.put("sla_uuid", sla_uuid);
			dflavour_info.put("nsd_uuid", ns_uuid);

			rs.close();
			stmt.close();
			c.commit();
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Getting flavours. Class: " + class_name;
			String message = ("Error getting flavour name ==> " + e.getMessage());
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return dflavour_info;
	}

	/*******************************/
	/** OPERATIONS FOR AGREEMENTS **/
	/*******************************/

	/**
	 * Create table if not exist - customer-sla correlation
	 */
	public static void createTableCustSla() {

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS cust_sla" + "(ID  SERIAL PRIMARY KEY," + " NS_UUID TEXT NOT NULL, "
					+ "NSI_UUID TEXT NULL," + "NS_NAME TEXT NOT NULL," + "SLA_UUID  TEXT NOT NULL,"
					+ "SLA_NAME TEXT NOT NULL," + "SLA_DATE TEXT NOT NULL," + "SLA_STATUS TEXT NOT NULL,"
					+ "CUST_EMAIL TEXT NOT NULL," + "CUST_USERNAME  TEXT NOT NULL," + "INST_ID TEXT NOT NULL,"
					+ "INST_STATUS  TEXT NOT NULL )";

			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();

			ThreadContext.clearAll();

		} catch (SQLException e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Create table for Customer - SLA Agreement correlations Class: " + class_name;
			String message = ("Error creating cust_sla table: " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}

	/**
	 * Insert Record cust-sla correlation
	 * 
	 */
	public void insertRecordAgreement(String ns_uuid, String ns_name, String sla_uuid, String sla_name,
			String sla_status, String cust_email, String cust_username, String inst_status, String correlation_id) {

		/** useful variables **/
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // '
		df.setTimeZone(tz);
		/** current date */
		Date date = new Date();
		String sla_date = df.format(date);

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "INSERT INTO cust_sla "
					+ " (ns_uuid, ns_name, sla_uuid, sla_name, sla_date, sla_status, cust_email, cust_username, inst_status, inst_id) "
					+ "VALUES ('" + ns_uuid + "','" + ns_name + "','" + sla_uuid + "' ,'" + sla_name + "' ,'" + sla_date
					+ "','" + sla_status + "','" + cust_email + "','" + cust_username + "', '" + inst_status + "' , '"
					+ correlation_id + "');";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Inserting record for customer-sla correlation. Class: " + class_name;
			String message = ("[*] Success. Agreement record inserted.");
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Inserting record for customer-sla correlation. Class: " + class_name;
			String message = "[*] Error creating records for cust-sla ==> " + e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		}
	}

	/**
	 * Update record for agreement in order to include ns instance id and status
	 * ready
	 * 
	 * @param inst_status
	 * @param correlation_id
	 * @param nsi_uuid
	 */
	public static void UpdateRecordAgreement(String inst_status, String correlation_id, String nsi_uuid) {

		String SQL = "UPDATE cust_sla SET inst_status = ?, nsi_uuid = ? WHERE inst_id = ?";
		boolean result = false;
		try {
			c.setAutoCommit(false);
			PreparedStatement pstmt = c.prepareStatement(SQL);
			pstmt.setString(1, inst_status);
			pstmt.setString(2, nsi_uuid);
			pstmt.setString(3, correlation_id);
			pstmt.executeUpdate();
			result = true;

			pstmt.close();
			c.commit();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Updating SLA Agreement status. Class: " + class_name;
			String message = "[*] Set status READY?" + result;
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Updating SLA Agreement status";
			String message = "[*] Set status READY?" + result + " ==> " + e.getMessage();
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

	}

	/**
	 * Violation of an agreement sla_status='VIOLATED'
	 * 
	 * @param nsi_uuid
	 */
	public static void UpdateAgreementStatus(String nsi_uuid) {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE cust_sla SET sla_status='VIOLATED' WHERE nsi_uuid='" + nsi_uuid + "';";
			stmt.executeUpdate(sql);
			result = true;
			stmt.close();
			c.commit();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Updating SLA Agreement Violation Status. Class: " + class_name;
			String message = "[*] SLA Violated?" + result;
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		} catch (SQLException e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Updating SLA Agreement Violation Status. Class: " + class_name;
			String message = "[*] SLA Violated?" + result + " ==> " + e.getMessage();
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}

	/**
	 * Change the correlation id of the messaging between MANO - GK for a specific
	 * nsi
	 * 
	 * @param nsi_uuid
	 * @param correlation_id
	 * @throws SQLException 
	 */
	public static void UpdateCorrelationID(String nsi_uuid, String correlation_id) throws SQLException {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE cust_sla SET inst_id = '" + correlation_id + "' WHERE nsi_uuid='" + nsi_uuid + "';";
			stmt.executeUpdate(sql);
			result = true;
			stmt.close();
			c.commit();

		} catch (PSQLException e) {

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Updating correlation id for NS instantiation process. Class: " + class_name;
			String message = ("[*] Correlation id updated?" + result);
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}

	/**
	 * Terminate an agreement inst_status='TERMINATED'
	 * 
	 * @param agreement_status
	 * @param correlation_id
	 * @return
	 */
	public static boolean TerminateAgreement(String agreement_status, String correlation_id) {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE cust_sla SET inst_status='" + agreement_status + "' WHERE inst_id='" + correlation_id
					+ "';";
			stmt.executeUpdate(sql);

			stmt.close();
			result = true;
			c.commit();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Terminating SLA Agreement. Class: " + class_name;
			String message = " [*] Set status TERMINATED? " + result;
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		} catch (SQLException e) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Terminating SLA Agreement. Class: " + class_name;
			String message = ("[*] Set status TERMINATED?" + result);
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;
	}

	/**
	 * Get all Agreements
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getAgreements() {
		JSONObject root = new JSONObject();
		JSONArray agreements = new JSONArray();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM cust_sla WHERE inst_status = 'READY' OR inst_status = 'TERMINATED';");
			while (rs.next()) {
				String ns_uuid = rs.getString("ns_uuid");
				String ns_name = rs.getString("ns_name");
				String sla_uuid = rs.getString("sla_uuid");
				String sla_name = rs.getString("sla_name");
				String sla_date = rs.getString("sla_date");
				String sla_status = rs.getString("sla_status");
				String cust_email = rs.getString("cust_email");
				String cust_username = rs.getString("cust_username");
				String inst_status = rs.getString("inst_status");
				String inst_id = rs.getString("inst_id");
				String nsi_uuid = rs.getString("nsi_uuid");

				/** useful variables **/
				TimeZone tz = TimeZone.getTimeZone("UTC");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // iso date format
																					// yyyy-MM-dd'T'HH:mm'Z'
				df.setTimeZone(tz);

				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date date2 = null;
				try {
					date2 = formatter.parse(sla_date);
				} catch (ParseException e) {
					// e.printStackTrace();
				}
				String sla_date_new = df.format(date2);

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("ns_name", ns_name);
				obj.put("sla_name", sla_name);
				obj.put("sla_date", sla_date_new);
				obj.put("sla_status", sla_status);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_email", cust_email);
				obj.put("cust_username", cust_username);
				obj.put("inst_status", inst_status);
				obj.put("correlation_id", inst_id);
				obj.put("nsi_uuid", nsi_uuid);

				agreements.add(obj);
			}
			root.put("agreements", agreements);
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get all SLA Agreement record details. Class: " + class_name;
			String message = ("[*] Error getting agreement details ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return root;
	}

	/**
	 * Get all Agreements
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getActiveAgreements() {

		JSONObject root = new JSONObject();
		// JSONArray ns_template = new JSONArray();
		JSONArray agreements = new JSONArray();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cust_sla WHERE inst_status = 'READY';");

			while (rs.next()) {
				String ns_uuid = rs.getString("ns_uuid");
				String ns_name = rs.getString("ns_name");
				String sla_uuid = rs.getString("sla_uuid");
				String sla_name = rs.getString("sla_name");
				String sla_date = rs.getString("sla_date");
				String sla_status = rs.getString("sla_status");
				String cust_email = rs.getString("cust_email");
				String cust_username = rs.getString("cust_username");
				String inst_status = rs.getString("inst_status");
				String inst_id = rs.getString("inst_id");
				String nsi_uuid = rs.getString("nsi_uuid");

				JSONObject license_info_record = getLicenseInfo(sla_uuid, cust_username, ns_uuid);
				String license_type = (String) license_info_record.get("license_type");
				String license_status = (String) license_info_record.get("license_status");
				int license_allowed_instances = 0;
				license_allowed_instances = (Integer) license_info_record.get("allowed_instances");
				int license_current_instances = 0;
				license_current_instances = (Integer) license_info_record.get("current_instances");
				String license_expiration_date = (String) license_info_record.get("license_expiration_date");

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("ns_name", ns_name);
				obj.put("sla_name", sla_name);
				obj.put("sla_date", sla_date);
				obj.put("sla_status", sla_status);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_email", cust_email);
				obj.put("cust_username", cust_username);
				obj.put("inst_status", inst_status);
				obj.put("correlation_id", inst_id);
				obj.put("nsi_uuid", nsi_uuid);
				obj.put("license_type", license_type);
				obj.put("license_status", license_status);
				obj.put("license_allowed_instances", license_allowed_instances);
				obj.put("license_current_instances", license_current_instances);
				obj.put("license_expiration_date", license_expiration_date);

				agreements.add(obj);
			}
			root.put("agreements", agreements);
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get active SLA Agreement record details. Class: " + class_name;
			String message = ("[*] Error getting active sla agreements ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return root;
	}

	/**
	 * Get agreement per NS instance id
	 * 
	 * @param nsi_uuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject selectAgreementPerNSI(String nsi_uuid) {

		JSONObject root = new JSONObject();
		JSONArray cust_sla = new JSONArray();

		nsi_uuid = nsi_uuid.trim();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM cust_sla WHERE nsi_uuid = '" + nsi_uuid + "' AND inst_status='READY'; ");
			while (rs.next()) {
				String ns_uuid = rs.getString("ns_uuid");
				String sla_uuid = rs.getString("sla_uuid");
				String cust_username = rs.getString("cust_username");
				String sla_status = rs.getString("sla_status");

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("nsi_uuid", nsi_uuid);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_username", cust_username);
				obj.put("sla_status", sla_status);

				cust_sla.add(obj);
			}
			root.put("cust_sla", cust_sla);
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get  SLA Agreement per nsi. Class: " + class_name;
			String message = ("[*] Error getting agreement per nsi ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return root;
	}

	/**
	 * Get all agreements per customer
	 * 
	 * @param custuuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject selectAgreementPerCustomer(String cust_username) {

		JSONObject root = new JSONObject();
		JSONArray cust_sla = new JSONArray();

		cust_username = cust_username.trim();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM cust_sla WHERE cust_username = '" + cust_username + "' AND inst_status='READY';");

			while (rs.next()) {
				String ns_uuid = rs.getString("ns_uuid");
				String sla_uuid = rs.getString("sla_uuid");
				cust_username = rs.getString("cust_username");
				String nsi_uuid = rs.getString("nsi_uuid");

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("nsi_uuid", nsi_uuid);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_username", cust_username);
				cust_sla.add(obj);
			}
			root.put("cust_sla", cust_sla);
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get  SLA Agreement per customer. Class: " + class_name;
			String message = ("[*] Error getting agreement per customer  ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return root;
	}

	/**
	 * Get all agreements per customer
	 * 
	 * @param custuuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject selectAgreementPerSLA(String sla_uuid) {

		JSONObject root = new JSONObject();
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cust_sla WHERE sla_uuid = '" + sla_uuid + "' LIMIT 1;");

			while (rs.next()) {
				String ns_name = rs.getString("ns_name");
				root.put("ns_name", ns_name);
			}
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get ns name from SLA Agreement per sla uuid. Class: " + class_name;
			String message = ("[*] Error getting ns name  ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return root;
	}

	/**
	 * Get specific agreement per nsi and sla
	 * 
	 * @param sla_uuid
	 * @param nsi_uuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject selectAgreementPerSlaNs(String sla_uuid, String nsi_uuid) {

		JSONObject root = new JSONObject();
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT * FROM cust_sla WHERE sla_uuid='" + sla_uuid + "' AND nsi_uuid='"
					+ nsi_uuid + "' AND inst_status='READY';");

			while (rs.next()) {
				String cust_username = rs.getString("cust_username");
				String cust_email = rs.getString("cust_email");
				String sla_date = rs.getString("sla_date");

				root.put("cust_username", cust_username);
				root.put("cust_email", cust_email);
				root.put("sla_date", sla_date);
			}

			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get specific agreement per nsi and sla. Class: " + class_name;
			String message = ("[*] Error Getting specific agreement per nsi and sla ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return root;
	}

	/**
	 * Delete Agreement correlation
	 * 
	 * @param nsi_uuid
	 * @return
	 */
	public boolean deleteAgreement(String nsi_uuid) {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "DELETE FROM  cust_sla WHERE NSI_UUID='" + nsi_uuid + "';";
			stmt.executeUpdate(sql);

			stmt.close();
			c.commit();
			result = true;

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Delete Agreement correlation. Class: " + class_name;
			String message = ("[*] Error deleting agreement ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return result;
	}

	/**
	 * Count active agreements per sla template
	 * 
	 * @param sla_uuid
	 * @return
	 */
	public int countAgreementCorrelationPeriD(String sla_uuid) {

		String SQL = "SELECT count(*) FROM cust_sla where sla_uuid = '" + sla_uuid + "' AND inst_status='READY'";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}

			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count active agreements per sla template. Class: " + class_name;
			String message = ("[*] Error counting agreements ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;

	}

	/**
	 * Count active agreements
	 */
	public int countActiveAgreements() {

		String SQL = "SELECT count(*) FROM cust_sla where inst_status='READY'";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}

			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count active agreements. Class: " + class_name;
			String message = ("[*] Error counting agreements ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	/**
	 * Count active agreements in a date range
	 */
	public int countActiveAgreementsDateRange(int days) {

		Timestamp currentDate = new Timestamp(System.currentTimeMillis());
		LocalDate minusDates = LocalDate.now().minusDays(days);

		String SQL = "SELECT count(*) FROM cust_sla where inst_status='READY' AND sla_date BETWEEN '" + minusDates
				+ "' AND '" + currentDate + "'";

		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count active agreements. Class: " + class_name;
			String message = ("[*] Error counting agreements ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	/**
	 * Count violated agreements
	 */
	public int countViolatedAgreements() {

		String SQL = "SELECT count(*) FROM cust_sla where sla_status='VIOLATED'";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count violated agreements. Class: " + class_name;
			String message = ("[*] Error counting violated agreements ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	/**
	 * Count violated agreements in date range
	 */
	public int countViolatedAgreementsDateRange(int days) {

		Timestamp currentDate = new Timestamp(System.currentTimeMillis());
		LocalDate minusDates = LocalDate.now().minusDays(days);

		String SQL = "SELECT count(*) FROM cust_sla where inst_status='VIOLATED' AND sla_date BETWEEN '" + minusDates
				+ "' AND '" + currentDate + "'";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count violated agreements ";
			String message = ("Error counting violated agreements ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	/**
	 * Count total agreements
	 */
	public int countTotalAgreements() {

		String SQL = "SELECT count(*) FROM cust_sla where inst_status='VIOLATED' OR inst_status='READY'";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count total agreements. Class: " + class_name;
			String message = ("[*] Error counting total agreements ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	/**
	 * Count total agreements in a date range
	 */
	public int countTotalAgreementsDateRange(int days) {

		Timestamp currentDate = new Timestamp(System.currentTimeMillis());
		LocalDate minusDates = LocalDate.now().minusDays(days);

		String SQL = "SELECT count(*) FROM cust_sla where (inst_status='READY' OR inst_status='VIOLATED') AND sla_date BETWEEN '"
				+ minusDates + "' AND '" + currentDate + "'";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count total agreements. Class: " + class_name;
			String message = ("[*] Error counting total agreements ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	/*******************************/
	/** OPERATIONS FOR VIOLATIONS **/
	/*******************************/

	/**
	 * Create table if not exist - sla_violations
	 */
	public static void createTableViolations() {

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS sla_violations" + "(ID  SERIAL," + " NSI_UUID TEXT PRIMARY KEY, "
					+ "SLA_UUID TEXT NOT NULL," + "VIOLATION_TIME TEXT NOT NULL," + "ALERT_STATE TEXT NOT NULL,"
					+ "CUST_USERNAME  TEXT NOT NULL )";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Create table for Violation records. Class: " + class_name;
			String message = ("[*] Error creating table sla_violations created successfully");
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}

	/**
	 * Insert Record violations
	 * 
	 */
	public static void insertRecordViolation(String nsi_uuid, String sla_uuid, String violation_time,
			String alert_state, String cust_username) {

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "INSERT INTO sla_violations  (nsi_uuid, sla_uuid,violation_time, alert_state, cust_username ) VALUES ('"
					+ nsi_uuid + "', '" + sla_uuid + "', '" + violation_time + "','" + alert_state + "', '"
					+ cust_username + "');  ";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();

		} catch (SQLException e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Insert violation record. Class: " + class_name;
			String message = ("[*] Error inserting violation record ==> " + e.getMessage());
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}

	/**
	 * Get violated SLA per ns instance
	 * 
	 * @param nsi_uuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getViolatedSLA(String nsi_uuid) {

		String sla_uuid = null;
		String cust_username = null;
		JSONObject violated_sla = new JSONObject();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cust_sla WHERE nsi_uuid = '" + nsi_uuid + "';");
			while (rs.next()) {
				sla_uuid = rs.getString("sla_uuid");
				cust_username = rs.getString("cust_username");
				violated_sla.put("sla_uuid", sla_uuid);
				violated_sla.put("cust_username", cust_username);

			}
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get violated SLA. Class: " + class_name;
			String message = ("[*] Error getting the violated SLA ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return violated_sla;

	}

	/**
	 * 
	 * @param nsi_uuid
	 * @param sla_uuid
	 * @return Get violation data per SLA - Service Instance
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getViolationData(String nsi_uuid, String sla_uuid) {

		JSONObject violation = new JSONObject();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM sla_violations WHERE nsi_uuid='" + nsi_uuid + "' AND sla_uuid='" + sla_uuid + "';");
			while (rs.next()) {
				String violation_time = rs.getString("violation_time");
				String alert_state = rs.getString("alert_state");
				String cust_username = rs.getString("cust_username");

				violation.put("violation_time", violation_time);
				violation.put("alert_state", alert_state);
				violation.put("cust_username", cust_username);
				violation.put("nsi_uuid", nsi_uuid);
				violation.put("sla_uuid", sla_uuid);

			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get violated SLA. Class: " + class_name;
			String message = ("[*] Error getting the violated SLA records ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return violation;
	}

	/**
	 * 
	 * @return All Violation data for all SLAs-NS instances
	 */
	@SuppressWarnings({ "unchecked" })
	public static JSONArray getAllViolationData() {

		JSONArray violations = new JSONArray();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM sla_violations;");
			while (rs.next()) {
				String violation_time = rs.getString("violation_time");
				String alert_state = rs.getString("alert_state");
				String cust_username = rs.getString("cust_username");
				String nsi_uuid = rs.getString("nsi_uuid");
				String sla_uuid = rs.getString("sla_uuid");

				JSONObject obj = new JSONObject();
				obj.put("violation_time", violation_time);
				obj.put("alert_state", alert_state);
				obj.put("cust_username", cust_username);
				obj.put("nsi_uuid", nsi_uuid);
				obj.put("sla_uuid", sla_uuid);
				violations.add(obj);

			}
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get violated SLA data. Class: " + class_name;
			String message = ("Error getting the violated SLA  data==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return violations;
	}

	public static int countViolationsPerNsi(String nsi_uuid) {

		String SQL = "SELECT count(*) FROM sla_violations where nsi_uuid = '" + nsi_uuid + "' ";
		int count_violations = 0;

		try {

			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count_violations = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Counting violations. Class: " + class_name;
			String message = ("[*] Error counting the violations ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count_violations;

	}

	/**
	 * Delete Record
	 */
	public boolean deleteRecord(String tablename, String sla_uuid) {

		boolean result = false;

		String SQL = "SELECT count(*) FROM " + tablename + " where SLA_UUID = '" + sla_uuid + "' ";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Deleting record. Class: " + class_name;
			String message = ("[*] Error: " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		if (count > 0) {
			try {
				c.setAutoCommit(false);
				stmt = c.createStatement();
				String sql = "DELETE from " + tablename + " where SLA_UUID='" + sla_uuid + "';";
				stmt.executeUpdate(sql);
				stmt.close();
				c.commit();
				result = true;

			} catch (SQLException e) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "E";
				String operation = "Delete violated SLA. Class: " + class_name;
				String message = ("Error deleting violation for the sla_uuid= " + sla_uuid);
				String status = "";
				logger.debug(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

			}
		} else {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Delete violated SLA. Class: " + class_name;
			String message = ("[*] Error deleting violation for the sla_uuid= " + sla_uuid);
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;
	}

	/**
	 * Select all records
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject selectAllRecords(String tablename) {

		JSONObject root = new JSONObject();
		JSONArray ns_template = new JSONArray();
		JSONArray cust_sla = new JSONArray();

		if (tablename.equals("ns_template")) {

			try {
				c.setAutoCommit(false);
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM ns_template;");

				while (rs.next()) {
					String ns_uuid = rs.getString("ns_uuid");
					String sla_uuid = rs.getString("sla_uuid");

					JSONObject obj = new JSONObject();
					obj.put("ns_uuid", ns_uuid);
					obj.put("sla_uuid", sla_uuid);

					ns_template.add(obj);
				}

				root.put("ns_template", ns_template);

				rs.close();
				stmt.close();
				c.commit();

			} catch (SQLException e) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "E";
				String operation = "Get all records";
				String message = ("[*] Error feching data from table = " + tablename);
				String status = "";
				logger.debug(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);
			}

		} else if (tablename.equals("cust_sla")) {

			try {
				c.setAutoCommit(false);
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + ";");

				while (rs.next()) {
					String ns_uuid = rs.getString("ns_uuid");
					String sla_uuid = rs.getString("sla_uuid");
					String cust_username = rs.getString("cust_username");

					JSONObject obj = new JSONObject();
					obj.put("ns_uuid", ns_uuid);
					obj.put("sla_uuid", sla_uuid);
					obj.put("cust_username", cust_username);
					cust_sla.add(obj);
				}

				root.put("cust_sla", cust_sla);

				rs.close();
				stmt.close();
				c.commit();

			} catch (SQLException e) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "E";
				String operation = "Get all records";
				String message = ("[*] Error feching data from table = " + tablename);
				String status = "";
				logger.debug(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

			}

		}

		return root;
	}

	/*******************************/
	/** OPERATIONS FOR LICENSING **/
	/*******************************/

	/**
	 * Create table sla_licensing for storing licensing records
	 */
	public static void createTableLicensing() {
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS sla_licensing " + "(ID  SERIAL," + " NSI_UUID TEXT, "
					+ "SLA_UUID TEXT," + "NS_UUID TEXT," + "CUST_USERNAME TEXT ," + "CUST_EMAIL  TEXT,"
					+ "license_type  TEXT," + "license_exp_date  TEXT," + "allowed_instances  TEXT,"
					+ "current_instances  TEXT," + "license_status  TEXT," + "correlation_id TEXT)";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Create table for Licenses. Class: " + class_name;
			String message = ("Error creating table sla_licensing ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}

	/**
	 * Get a list with licensing information
	 * 
	 * @return licenses
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray getAllLicenses() {

		JSONArray licenses = new JSONArray();
		Statement stmt = null;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM sla_licensing;");
			while (rs.next()) {
				String sla_uuid = rs.getString("sla_uuid");
				String ns_uuid = rs.getString("ns_uuid");
				String nsi_uuid = rs.getString("nsi_uuid");
				String cust_username = rs.getString("cust_username");
				String cust_email = rs.getString("cust_email");
				String license_type = rs.getString("license_type");
				String license_exp_date = rs.getString("license_exp_date");
				String allowed_instances = rs.getString("allowed_instances");
				String current_instances = rs.getString("current_instances");
				String license_status = rs.getString("license_status");

				JSONObject licenses_data = new JSONObject();
				licenses_data.put("sla_uuid", sla_uuid);
				licenses_data.put("ns_uuid", ns_uuid);
				licenses_data.put("nsi_uuid", nsi_uuid);
				licenses_data.put("cust_username", cust_username);
				licenses_data.put("cust_email", cust_email);
				licenses_data.put("license_type", license_type);
				licenses_data.put("license_exp_date", license_exp_date);
				licenses_data.put("allowed_instances", allowed_instances);
				licenses_data.put("current_instances", current_instances);
				licenses_data.put("license_status", license_status);

				licenses.add(licenses_data);

			}
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get licensing information";
			String message = ("[*] Error getting licensing information ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return licenses;
	}

	/**
	 * Insert licence record in sla_licensing table
	 * 
	 * @param sla_uuid
	 * @param ns_uuid
	 * @param nsi_uuid
	 * @param cust_username
	 * @param cust_email
	 * @param license_type
	 * @param license_exp_date
	 * @param allowed_instances
	 * @param current_instances
	 * @param license_status
	 */
	public static void insertLicenseRecord(String sla_uuid, String ns_uuid, String nsi_uuid, String cust_username,
			String cust_email, String license_type, String license_exp_date, String allowed_instances,
			String current_instances, String license_status, String correlation_id) {

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "INSERT INTO sla_licensing  (sla_uuid, ns_uuid,nsi_uuid, cust_username, cust_email, license_type, license_exp_date, allowed_instances, current_instances, license_status,correlation_id) VALUES ('"
					+ sla_uuid + "', '" + ns_uuid + "', '" + nsi_uuid + "','" + cust_username + "', '" + cust_email
					+ "' ,'" + license_type + "','" + license_exp_date + "','" + allowed_instances + "','"
					+ current_instances + "','" + license_status + "', '" + correlation_id + "');  ";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();

		} catch (SQLException e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Insert license record";
			String message = ("[*] Error creating license record ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}

	public boolean deleteLicenseRecord(String sla_uuid, String cust_username, String ns_uuid) {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "DELETE FROM sla_licensing WHERE NS_UUID='" + ns_uuid + "' AND sla_uuid='" + sla_uuid
					+ "' AND cust_username='" + cust_username + "';";
			stmt.executeUpdate(sql);
			result = true;

			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Delete License record";
			String message = ("[*] Error deleting licensing record ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getLicenseInfo(String sla_uuid, String cust_username, String ns_uuid) {

		JSONObject license_info = new JSONObject();
		String license_status = "";
		String license_type = "";
		String license_expiration_date = "";
		String allowed_instances_string = "";
		String current_instances_string = "";
		int allowed_instances = 0;
		int current_instances = 0;

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM sla_licensing WHERE sla_uuid = '" + sla_uuid
					+ "' AND ns_uuid='" + ns_uuid + "' AND  cust_username='" + cust_username + "';");

			while (rs.next()) {
				license_status = rs.getString("license_status");
				license_type = rs.getString("license_type");
				license_expiration_date = rs.getString("license_exp_date");
				allowed_instances_string = rs.getString("allowed_instances");
				current_instances_string = rs.getString("current_instances");

				allowed_instances = Integer.parseInt(allowed_instances_string);
				current_instances = Integer.parseInt(current_instances_string);

				license_info.put("license_status", license_status);
				license_info.put("license_type", license_type);
				license_info.put("license_expiration_date", license_expiration_date);
				license_info.put("allowed_instances", allowed_instances);
				license_info.put("current_instances", current_instances);

			}
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get license status";
			String message = ("[*] Error Getting license status ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return license_info;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getSpecificLicense(String nsi_uuid) {

		JSONObject license_info = new JSONObject();
		String sla_uuid = "";
		String ns_uuid = "";
		String cust_username = "";
		String cust_email = "";
		String license_status = "";
		String license_type = "";
		String license_expiration_date = "";
		String allowed_instances = "";
		String current_instances = "";

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM sla_licensing WHERE nsi_uuid = '" + nsi_uuid + "';");

			while (rs.next()) {
				sla_uuid = rs.getString("sla_uuid");
				ns_uuid = rs.getString("ns_uuid");
				cust_username = rs.getString("cust_username");
				cust_email = rs.getString("cust_email");
				license_status = rs.getString("license_status");
				license_type = rs.getString("license_type");
				license_expiration_date = rs.getString("license_exp_date");
				allowed_instances = rs.getString("allowed_instances");
				current_instances = rs.getString("current_instances");

				license_info.put("sla_uuid", sla_uuid);
				license_info.put("ns_uuid", ns_uuid);
				license_info.put("nsi_uuid", nsi_uuid);
				license_info.put("cust_username", cust_username);
				license_info.put("cust_email", cust_email);
				license_info.put("license_status", license_status);
				license_info.put("license_type", license_type);
				license_info.put("license_expiration_date", license_expiration_date);
				license_info.put("allowed_instances", allowed_instances);
				license_info.put("current_instances", current_instances);

				// get names from the agreements table
				Statement stmt_names = null;

				try {
					c.setAutoCommit(false);
					stmt_names = c.createStatement();
					ResultSet rs_name = stmt
							.executeQuery("SELECT * FROM cust_sla WHERE nsi_uuid = '" + nsi_uuid + "';");

					while (rs_name.next()) {
						String sla_name = rs_name.getString("sla_name");
						String ns_name = rs_name.getString("ns_name");

						license_info.put("sla_name", sla_name);
						license_info.put("ns_name", ns_name);
					}
					rs_name.close();
					stmt_names.close();
					c.commit();

				} catch (SQLException e) {
					// logging
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					String timestamps = timestamp.toString();
					String type = "E";
					String operation = "Get specific license record. Class: " + class_name;
					String message = ("[*] Error Getting names from cust_sla  ==> " + e.getMessage());
					String status = "";
					logger.debug(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type, timestamps, operation, message, status);
				}

			}
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get specific license record";
			String message = ("[*] Error Getting specific license  ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		if (license_info.size() == 0) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "Get specific license record";
			String message = ("[*] This license record does not exist. Invalid NSI");
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return license_info;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getLicensePerCorrID(String correlation_id) {

		JSONObject license_info = new JSONObject();
		String sla_uuid = "";
		String ns_uuid = "";
		String nsi_uuid = "";
		String cust_username = "";
		String cust_email = "";
		String license_status = "";
		String license_type = "";
		String license_expiration_date = "";
		String allowed_instances = "";
		String current_instances = "";

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM sla_licensing WHERE correlation_id = '" + correlation_id + "';");

			while (rs.next()) {
				sla_uuid = rs.getString("sla_uuid");
				ns_uuid = rs.getString("ns_uuid");
				nsi_uuid = rs.getString("nsi_uuid");
				cust_username = rs.getString("cust_username");
				cust_email = rs.getString("cust_email");
				license_status = rs.getString("license_status");
				license_type = rs.getString("license_type");
				license_expiration_date = rs.getString("license_exp_date");
				allowed_instances = rs.getString("allowed_instances");
				current_instances = rs.getString("current_instances");

				license_info.put("sla_uuid", sla_uuid);
				license_info.put("ns_uuid", ns_uuid);
				license_info.put("nsi_uuid", nsi_uuid);
				license_info.put("cust_username", cust_username);
				license_info.put("cust_email", cust_email);
				license_info.put("license_status", license_status);
				license_info.put("license_type", license_type);
				license_info.put("license_expiration_date", license_expiration_date);
				license_info.put("allowed_instances", allowed_instances);
				license_info.put("current_instances", current_instances);

			}
			rs.close();
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get specific license record";
			String message = ("[*] Error Getting specific license  ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return license_info;
	}

	public static boolean CreateLicenseInstance(String correlation_id, String license_status, String nsi_uuid) {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE sla_licensing SET nsi_uuid='" + nsi_uuid + "', license_status='" + license_status
					+ "' WHERE correlation_id='" + correlation_id + "';";
			stmt.executeUpdate(sql);
			result = true;

			stmt.close();
			c.commit();

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "UPDATE License instance. Class: " + class_name;
			String message = ("[*] Set license status ACTIVE ?" + result);
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		} catch (SQLException e) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "UPDATE License instance. Class: " + class_name;
			String message = ("[*] Set license status ACTIVE ?" + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;
	}

	/**
	 * Count license records per customer
	 * 
	 * @param cust_username
	 * @param sla_uuid
	 * @return
	 */
	public static int countLicensePerCustSLA(String cust_username, String sla_uuid) {

		String SQL = "SELECT count(*) FROM sla_licensing WHERE cust_username='" + cust_username + "' AND sla_uuid='"
				+ sla_uuid + "'";
		int count_licenses = 0;
		try {

			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count_licenses = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Counting Licenses";
			String message = ("[*] Error counting the Licenses ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count_licenses;

	}

	public static boolean UpdateLicenseStatus(String sla_uuid, String ns_uuid, String cust_username,
			String license_status) {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE sla_licensing SET license_status='" + license_status + "' WHERE sla_uuid='" + sla_uuid
					+ "' AND ns_uuid='" + ns_uuid + "' AND cust_username='" + cust_username + "';";
			stmt.executeUpdate(sql);
			result = true;
			stmt.close();
			c.commit();

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Updating SLA Licensing status";
			String message = " [*] Error updating license staus: " + e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;

	}

	public static boolean deactivateLicenseForNSI(String nsi_uuid, String license_status) {

		Statement stmt = null;
		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE sla_licensing SET license_status='" + license_status + "' WHERE nsi_uuid='" + nsi_uuid
					+ "';";
			stmt.executeUpdate(sql);

			stmt.close();
			c.commit();
			result = true;

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Deactivating SLA Licensing";
			String message = "[*] Error de-activating License: " + e.getMessage();
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;

	}

	public static boolean deactivateLicense(String correlation_id, String license_status) {

		Statement stmt = null;
		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE sla_licensing SET license_status='" + license_status + "' WHERE correlation_id='"
					+ correlation_id + "';";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			result = true;

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Deactivating SLA Licensing";
			String message = "[*] Error de-activating License with correlation id='" + correlation_id + "' : "
					+ e.getMessage();
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;

	}

	public static boolean UpdateLicenseCorrelationID(String sla_uuid, String ns_uuid, String cust_username,
			String correlation_id) {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE sla_licensing SET correlation_id='" + correlation_id + "' WHERE sla_uuid='" + sla_uuid
					+ "' AND ns_uuid='" + ns_uuid + "' AND cust_username='" + cust_username + "';";
			stmt.executeUpdate(sql);

			stmt.close();
			c.commit();
			result = true;

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Updating SLA Licensing correlation id";
			String message = "[*] Error updating SLA Licensing correlation id: " + e.getMessage();
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;

	}

	public static boolean UpdateLicenseCorrelationIDperNSI(String nsi_uuid, String correlation_id) throws SQLException {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE sla_licensing SET correlation_id='" + correlation_id + "' WHERE nsi_uuid='" + nsi_uuid
					+ "';";
			stmt.executeUpdate(sql);

			stmt.close();
			c.commit();
			result = true;

		} catch (PSQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Updating SLA Licensing correlation id";
			String message = "[*] Error updating SLA Licensing correlation id: " + e.getMessage();
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;

	}

	public static int countActiveLicensePerCustSLA(String cust_username, String sla_uuid, String license_status) {

		String SQL = "SELECT count(*) FROM sla_licensing WHERE cust_username='" + cust_username + "' AND sla_uuid='"
				+ sla_uuid + "' AND license_status='active'";
		int count_active_licenses = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count_active_licenses = rs.getInt(1);
			}
			c.commit();
			stmt.close();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Counting active Licenses per customer and sla";
			String message = ("Number of Licenses  ==> " + count_active_licenses);
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Counting Active Licenses";
			String message = ("[*] Error counting the active licenses ==> " + e.getMessage());
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count_active_licenses;

	}

	/**
	 * 
	 * @param sla_uuid
	 * @param ns_uuid
	 * @param cust_username
	 * @param current_instances
	 * @return
	 */
	public static boolean UpdateLicenseCurrentInstances(String sla_uuid, String ns_uuid, String cust_username,
			String current_instances) {

		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE sla_licensing SET current_instances='" + current_instances + "' WHERE sla_uuid='"
					+ sla_uuid + "' AND ns_uuid='" + ns_uuid + "' AND cust_username='" + cust_username + "';";
			stmt.executeUpdate(sql);

			stmt.close();
			result = true;
			c.commit();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Updating License Current Instances";
			String message = "License current instances updated? " + result + "! ";
			String message2 = "The current instances for NS_UUID=" + ns_uuid + " and CUST_USERNAME=" + cust_username
					+ " are=" + current_instances;
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message + message2, status);

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Updating SLA Licensing current instances";
			String message = "[*] Error updating license current instances: " + e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return result;

	}

	/**
	 * @return Number of utilized licenses
	 */
	public int countUtilizedLicense() {

		String SQL = "SELECT count(*) FROM sla_licensing WHERE license_status='active'";
		int count = 0;
		try {

			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count utilized licenses";
			String message = ("[*] Error counting utilized licenses ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	/**
	 * @return Number of Acquired (bought) licenses
	 */
	public int countAcquiredLicense() {

		String SQL = "SELECT count(*) FROM sla_licensing";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();

		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count Acquired licenses";
			String message = ("[*] Error counting Acquired licenses ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	/**
	 * @return Number of Expired Licenses
	 */
	public int countExpiredLicense() {

		String SQL = "SELECT count(*) FROM sla_licensing WHERE license_status='expired'";
		int count = 0;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			stmt.close();
			c.commit();
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Count expired licenses";
			String message = ("[*] Error counting expired licenses ==> " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return count;
	}

	public boolean delete_license(String sla_uuid) {

		String SQL = "DELETE FROM sla_licensing WHERE sla_uuid = '" + sla_uuid + "'";
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			stmt.executeUpdate(SQL);

			stmt.close();
			c.commit();
			return true;
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Delete Licensing Info";
			String message = ("[*] Error deleting license with error: " + e.getMessage());
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
			return false;
		}

	}

}
