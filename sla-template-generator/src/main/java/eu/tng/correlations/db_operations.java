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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSet;

public class db_operations {

	static Logger logger = LogManager.getLogger("SLAM_Logger");

	static Connection c = null;
	static Statement stmt = null;

	/**
	 * Connect to PostgreSQL
	 */
	public static boolean connectPostgreSQL() {
		boolean connect = false;
		try {

			Class.forName("org.postgresql.Driver");
			/*
			 * c =
			 * DriverManager.getConnection("jdbc:postgresql://localhost:5432/sla-manager",
			 * "postgres", "admin");
			 */

			c = DriverManager
					.getConnection(
							"jdbc:postgresql://" + System.getenv("DATABASE_HOST") + ":" + System.getenv("DATABASE_PORT")
									+ "/" + System.getenv("GTK_DB_NAME"),
							System.getenv("GTK_DB_USER"), System.getenv("GTK_DB_PASS"));

			connect = true;

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Connecting to SLA Manager's internal database.");
			ThreadContext.put("status", "");
			logger.info("Opened sla-manager database successfully");
			ThreadContext.clearAll();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Connecting to SLA Manager's internal database.");
			ThreadContext.put("status", "");
			logger.error("Error connecting to SLA Manager's internal database.");
			ThreadContext.clearAll();

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
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Closing conection to SLA Manager's internal database.");
			ThreadContext.put("status", "");
			logger.info("Succesfully closed the connection to SLA Manager's internal database.");
			ThreadContext.clearAll();
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Closing conection to SLA Manager's internal database.");
			ThreadContext.put("status", "");
			logger.error("Error closing the connection to SLA Manager's internal database.");
			ThreadContext.clearAll();
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
		try {
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS ns_template" + "(ID  SERIAL PRIMARY KEY,"
					+ " NS_UUID TEXT NOT NULL, " + "SLA_UUID  TEXT NOT NULL )";
			stmt.executeUpdate(sql);
			stmt.close();
			result = true;

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Creating table for NS - Template correlations");
			ThreadContext.put("status", "");
			logger.info("Table Created? " + result);
			ThreadContext.clearAll();

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Creating table for NS - Template correlations");
			ThreadContext.put("status", "");
			logger.error("Table Created? " + result);
			ThreadContext.clearAll();
		}

		return result;
	}

	/**
	 * Insert Record ns-template correlation
	 */
	public boolean insertRecord(String tablename, String ns_uuid, String sla_uuid) {
		boolean result = false;
		try {
			c.setAutoCommit(false);
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO " + tablename + " (ns_uuid,sla_uuid) " + "VALUES ('" + ns_uuid + "','" + sla_uuid
					+ "');";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			result = true;

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Insert record to table for NS - Template correlations");
			ThreadContext.put("status", "");
			logger.info("Records ns-template saved successfully? " + result);
			ThreadContext.clearAll();

		} catch (Exception e) {
			e.printStackTrace();
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Insert record to table for NS - Template correlations");
			ThreadContext.put("status", "");
			logger.error("Records ns-template saved successfully? " + result);
			ThreadContext.clearAll();
		}

		return result;
	}

	/*******************************/
	/** OPERATIONS FOR AGREEMENTS **/
	/*******************************/

	/**
	 * Create table if not exist - customer-sla correlation
	 */
	public static void createTableCustSla() {
		try {
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS cust_sla" + "(ID  SERIAL PRIMARY KEY," + " NS_UUID TEXT NOT NULL, "
					+ "NSI_UUID TEXT NULL," + "NS_NAME TEXT NOT NULL," + "SLA_UUID  TEXT NOT NULL,"
					+ "SLA_NAME TEXT NOT NULL," + "SLA_DATE TIMESTAMPTZ DEFAULT Now()," + "SLA_STATUS TEXT NOT NULL,"
					+ "CUST_EMAIL TEXT NOT NULL," + "CUST_UUID  TEXT NOT NULL," + "INST_ID TEXT NOT NULL,"
					+ "INST_STATUS  TEXT NOT NULL )";
			stmt.executeUpdate(sql);
			stmt.close();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Create table for Customer - SLA Agreement correlations");
			ThreadContext.put("status", "");
			logger.error("Table cust_sla created successfully");
			ThreadContext.clearAll();

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Create table for Customer - SLA Agreement correlations");
			ThreadContext.put("status", "");
			logger.error("Error creating cust_sla table.");
			ThreadContext.clearAll();
		}
	}

	/**
	 * Insert Record cust-sla correlation
	 * 
	 */
	public void insertRecordAgreement(String ns_uuid, String ns_name, String sla_uuid, String sla_name,
			String sla_status, String cust_name, String cust_uuid, String inst_status, String correlation_id) {

		cust_uuid = "Tom";
		String cust_email = "tom@example.com";
		try {
			c.setAutoCommit(false);
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO cust_sla "
					+ " (ns_uuid, ns_name, sla_uuid, sla_name, sla_status, cust_email, cust_uuid, inst_status, inst_id) "
					+ "VALUES ('" + ns_uuid + "','" + ns_name + "','" + sla_uuid + "' ,'" + sla_name + "' ,'"
					+ sla_status + "','" + cust_email + "','" + cust_uuid + "', '" + inst_status + "' , '"
					+ correlation_id + "');";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Inserting record for customer-sla correlation");
			ThreadContext.put("status", "");
			logger.info("Records  cust-sla saved successfully");
			ThreadContext.clearAll();

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Inserting record for customer-sla correlation");
			ThreadContext.put("status", "");
			logger.warn("cust-sla correlation was not saved!");
			ThreadContext.clearAll();
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

		String SQL = "UPDATE cust_sla " + "SET inst_status = ?, nsi_uuid = ?" + "WHERE inst_id = ?";
		boolean result = false;
		try {
			PreparedStatement pstmt = c.prepareStatement(SQL);
			pstmt.setString(1, inst_status);
			pstmt.setString(2, nsi_uuid);
			pstmt.setString(3, correlation_id);
			pstmt.executeUpdate();
			result = true;
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Updating SLA Agreement status");
			ThreadContext.put("status", "");
			logger.info("Set status READY?" + result);
			ThreadContext.clearAll();
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Updating SLA Agreement record status");
			ThreadContext.put("status", "");
			logger.warn("Error updating the status");
			ThreadContext.clearAll();
		}

	}

	/**
	 * Violation of an agreement sla_status='VIOLATED'
	 * 
	 * @param nsi_uuid
	 */
	public static void UpdateAgreementStatus(String nsi_uuid) {

		Statement stmt = null;
		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE cust_sla SET sla_status='VIOLATED' WHERE nsi_uuid='" + nsi_uuid + "';";
			stmt.executeUpdate(sql);
			c.commit();
			stmt.close();
			result = true;

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Updating SLA Agreement Violation Status");
			ThreadContext.put("status", "");
			logger.info("SLA Violated?" + result);
			ThreadContext.clearAll();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Updating SLA Agreement Violation Status");
			ThreadContext.put("status", "");
			logger.warn("SLA Violated?" + result);
			ThreadContext.clearAll();
		}
	}

	/**
	 * Change the correlation id of the messaging between MANO - GK for a specific
	 * nsi
	 * 
	 * @param nsi_uuid
	 * @param correlation_id
	 */
	public static void UpdateCorrelationID(String nsi_uuid, String correlation_id) {

		String SQL = "UPDATE cust_sla " + "SET inst_id = ? " + "WHERE nsi_uuid = ?";
		boolean result = false;
		try {
			PreparedStatement pstmt = c.prepareStatement(SQL);
			pstmt.setString(1, correlation_id);
			pstmt.setString(2, nsi_uuid);
			pstmt.executeUpdate();
			result = true;
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Updating correlation id for NS instantiation process");
			ThreadContext.put("status", "");
			logger.info("Correlation id updated?" + result);
			ThreadContext.clearAll();
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Updating correlation id for NS instantiation process");
			ThreadContext.put("status", "");
			logger.warn("Correlation id updated?" + result);
			ThreadContext.clearAll();
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

		Statement stmt = null;
		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "UPDATE cust_sla SET inst_status='" + agreement_status + "' WHERE inst_id='" + correlation_id
					+ "';";
			stmt.executeUpdate(sql);
			c.commit();
			stmt.close();
			result = true;
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Terminating SLA Agreement");
			ThreadContext.put("status", "");
			logger.info("Set status TERMINATED?" + result);
			ThreadContext.clearAll();
		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Terminating SLA Agreement");
			ThreadContext.put("status", "");
			logger.warn("Set status TERMINATED?" + result);
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
		Statement stmt = null;

		JSONObject root = new JSONObject();
		// JSONArray ns_template = new JSONArray();
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
				String cust_uuid = rs.getString("cust_uuid");
				String inst_status = rs.getString("inst_status");
				System.out.print("STATUS ======" + inst_status);
				String inst_id = rs.getString("inst_id");
				String nsi_uuid = rs.getString("nsi_uuid");

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("ns_name", ns_name);
				obj.put("sla_name", sla_name);
				obj.put("sla_date", sla_date);
				obj.put("sla_status", sla_status);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_email", cust_email);
				obj.put("cust_uuid", cust_uuid);
				obj.put("inst_status", inst_status);
				obj.put("correlation_id", inst_id);
				obj.put("nsi_uuid", nsi_uuid);

				agreements.add(obj);
			}
			root.put("agreements", agreements);
			rs.close();
			stmt.close();
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get all SLA Agreement record details");
			ThreadContext.put("status", "");
			logger.info(root);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get all SLA Agreement record details");
			ThreadContext.put("status", "");
			logger.error("Error getting agreement details");
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
		Statement stmt = null;

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
				String cust_uuid = rs.getString("cust_uuid");
				String inst_status = rs.getString("inst_status");
				System.out.print("STATUS ======" + inst_status);
				String inst_id = rs.getString("inst_id");
				String nsi_uuid = rs.getString("nsi_uuid");

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("ns_name", ns_name);
				obj.put("sla_name", sla_name);
				obj.put("sla_date", sla_date);
				obj.put("sla_status", sla_status);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_email", cust_email);
				obj.put("cust_uuid", cust_uuid);
				obj.put("inst_status", inst_status);
				obj.put("correlation_id", inst_id);
				obj.put("nsi_uuid", nsi_uuid);

				agreements.add(obj);
			}
			root.put("agreements", agreements);
			rs.close();
			stmt.close();
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get active SLA Agreement record details");
			ThreadContext.put("status", "");
			logger.info(root);
		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get active SLA Agreement record details");
			ThreadContext.put("status", "");
			logger.error("Error getting active agreements details");
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

		Statement stmt = null;
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
				String cust_uuid = rs.getString("cust_uuid");
				String sla_status = rs.getString("sla_status");

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("nsi_uuid", nsi_uuid);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_uuid", cust_uuid);
				obj.put("sla_status", sla_status);

				cust_sla.add(obj);
			}
			root.put("cust_sla", cust_sla);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get agreement per NS instance id");
			ThreadContext.put("status", "");
			logger.error("Error getting agreement for NSI= " + nsi_uuid);
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
	public JSONObject selectAgreementPerCustomer(String custuuid) {

		Statement stmt = null;
		JSONObject root = new JSONObject();
		JSONArray cust_sla = new JSONArray();

		custuuid = custuuid.trim();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM cust_sla WHERE cust_uuid = '" + custuuid + "' AND inst_status='READY';");

			while (rs.next()) {
				String ns_uuid = rs.getString("ns_uuid");
				String sla_uuid = rs.getString("sla_uuid");
				String cust_uuid = rs.getString("cust_uuid");
				String nsi_uuid = rs.getString("nsi_uuid");

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("nsi_uuid", nsi_uuid);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_uuid", cust_uuid);
				cust_sla.add(obj);
			}
			root.put("cust_sla", cust_sla);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get all agreements per customer");
			ThreadContext.put("status", "");
			logger.error("Error getting agreements for cust_uuid= " + custuuid);
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

		Statement stmt = null;
		JSONObject root = new JSONObject();

		sla_uuid = sla_uuid.trim();
		nsi_uuid = nsi_uuid.trim();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cust_sla WHERE sla_uuid = '" + sla_uuid + "' AND nsi_uuid='"
					+ nsi_uuid + "' AND  inst_status='READY';");

			while (rs.next()) {
				String cust_uuid = rs.getString("cust_uuid");
				String cust_email = rs.getString("cust_email");
				String sla_date = rs.getString("sla_date");

				new JSONObject();
				root.put("cust_uuid", cust_uuid);
				root.put("cust_email", cust_email);
				root.put("sla_date", sla_date);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get specific agreement per nsi and sla");
			ThreadContext.put("status", "");
			logger.error("Error getting agreements for nsi=" + nsi_uuid + " and sla= " + sla_uuid);
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
		Statement stmt = null;
		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "DELETE FROM WHERE NSI_UUID='" + nsi_uuid + "';";
			stmt.executeUpdate(sql);
			c.commit();
			stmt.close();
			result = true;

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Delete Agreement correlation");
			ThreadContext.put("status", "");
			logger.info("Delete agreement for nsi=" + nsi_uuid);

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Delete Agreement correlation");
			ThreadContext.put("status", "");
			logger.error("Error deleting agreement for nsi=" + nsi_uuid);
		}

		return result;
	}

	/**
	 * Count active agreements per sla template
	 * 
	 * @param sla_uuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int countAgreementCorrelationPeriD(String sla_uuid) {

		String SQL = "SELECT count(*) FROM cust_sla where sla_uuid = '" + sla_uuid + "' AND inst_status='READY'";
		int count = 0;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Count active agreements per sla template");
			ThreadContext.put("status", "");
			logger.info("The number of Agreements correlations are = " + count);
		} catch (SQLException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Count active agreements per sla template");
			ThreadContext.put("status", "");
			logger.error("Error counting agreements");
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
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS sla_violations" + "(ID  SERIAL," + " NS_UUID TEXT PRIMARY KEY, "
					+ "SLA_UUID TEXT NOT NULL," + "VIOLATION_TIME TEXT NOT NULL," + "ALERT_STATE TEXT NOT NULL,"
					+ "CUST_UUID  TEXT NOT NULL )";
			stmt.executeUpdate(sql);
			stmt.close();
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Create table for Violation records");
			ThreadContext.put("status", "");
			logger.info("Table sla_violations created successfully");
			ThreadContext.clearAll();
		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Create table for Violation records");
			ThreadContext.put("status", "");
			logger.error("Error creating sla violations table or table already exists");
			ThreadContext.clearAll();
		}
	}

	/**
	 * Insert Record violations
	 * 
	 */
	public static void insertRecordViolation(String nsi_uuid, String sla_uuid, String violation_time,
			String alert_state, String cust_uuid) {

		cust_uuid = "Tom";
		try {
			c.setAutoCommit(false);
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO sla_violations  (ns_uuid, sla_uuid,violation_time, alert_state, cust_uuid ) VALUES ('"
					+ nsi_uuid + "', '" + sla_uuid + "', '" + violation_time + "','" + alert_state + "', '" + cust_uuid
					+ "');  ";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Insert violation record");
			ThreadContext.put("status", "");
			logger.info("Violation record created successfully");
			ThreadContext.clearAll();
		} catch (Exception e) {
			System.out.println("Error, Violation Record with same NSI already exists!");
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Insert violation record");
			ThreadContext.put("status", "");
			logger.warn("Error, Violation Record with same NSI already exists!");
			ThreadContext.clearAll();
		}
	}

	/**
	 * Get violated SLA per ns instance
	 * 
	 * @param nsi_uuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getViolatedSLA(String nsi_uuid) {

		Statement stmt = null;

		String sla_uuid = null;
		String cust_uuid = null;
		JSONObject violated_sla = new JSONObject();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cust_sla WHERE nsi_uuid = '" + nsi_uuid + "';");
			while (rs.next()) {
				sla_uuid = rs.getString("sla_uuid");
				cust_uuid = rs.getString("cust_uuid");
				violated_sla.put("sla_uuid", sla_uuid);
				violated_sla.put("cust_uuid", cust_uuid);

			}
			rs.close();
			stmt.close();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get violated SLA");
			ThreadContext.put("status", "");
			logger.info("Violated SLA ==> " + violated_sla);
			ThreadContext.clearAll();

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get violated SLA");
			ThreadContext.put("status", "");
			logger.warn("Error getting the violated SLA: " + sla_uuid);
			ThreadContext.clearAll();
		}
		return violated_sla;

	}

	/**
	 * 
	 * @param nsi_uuid
	 * @param sla_uuid
	 * @return Get violation data per SLA - Service Instance
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public static JSONObject getViolationData(String nsi_uuid, String sla_uuid) {

		JSONObject violation = new JSONObject();
		Statement stmt = null;

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM sla_violations WHERE nsi_uuid='" + nsi_uuid + "' AND sla_uuid='" + sla_uuid + "';");
			while (rs.next()) {
				String violation_time = rs.getString("violation_time");
				String alert_state = rs.getString("alert_state");
				String cust_uuid = rs.getString("cust_uuid");

				violation.put("violation_time", violation_time);
				violation.put("alert_state", alert_state);
				violation.put("cust_uuid", cust_uuid);
				violation.put("ns_uuid", nsi_uuid);
				violation.put("sla_uuid", sla_uuid);

			}
			System.out.println("VIOLATIONS FROM DB OPERATIONS CLASS ==> " + violation);
			rs.close();
			stmt.close();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get violated SLA");
			ThreadContext.put("status", "");
			logger.info("Violation record ==> " + violation);
			ThreadContext.clearAll();

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get violated SLA");
			ThreadContext.put("status", "");
			logger.warn("Error getting the violated SLA records");
			ThreadContext.clearAll();
		}
		return violation;
	}

	/**
	 * 
	 * @return All Violation data for all SLAs-NS instances
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public static JSONArray getAllViolationData() {

		JSONObject violation_data = new JSONObject();
		JSONArray violations = new JSONArray();
		Statement stmt = null;

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM sla_violations;");
			while (rs.next()) {
				String violation_time = rs.getString("violation_time");
				String alert_state = rs.getString("alert_state");
				String cust_uuid = rs.getString("cust_uuid");
				String nsi_uuid = rs.getString("ns_uuid");
				String sla_uuid = rs.getString("sla_uuid");

				JSONObject obj = new JSONObject();
				obj.put("violation_time", violation_time);
				obj.put("alert_state", alert_state);
				obj.put("cust_uuid", cust_uuid);
				obj.put("nsi_uuid", nsi_uuid);
				obj.put("sla_uuid", sla_uuid);
				violations.add(obj);

			}
			rs.close();
			stmt.close();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get violated SLA");
			ThreadContext.put("status", "");
			logger.info("All Violation records ==> " + violation_data);
			ThreadContext.clearAll();
		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get violated SLA");
			ThreadContext.put("status", "");
			logger.warn("Error getting the violation data");
			ThreadContext.clearAll();
		}
		return violations;
	}

	@SuppressWarnings("unchecked")
	public static int countViolationsPerNsi(String nsi_uuid) {

		String SQL = "SELECT count(*) FROM sla_violations where ns_uuid = '" + nsi_uuid + "' ";
		int count_violations = 0;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count_violations = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get violated SLA");
			ThreadContext.put("status", "");
			logger.warn("Error counting the violations fro the nsi_uuid= " + nsi_uuid);
			ThreadContext.clearAll();
		}
		return count_violations;

	}

	/**
	 * Delete Record
	 */
	public boolean deleteRecord(String tablename, String sla_uuid) {
		Statement stmt = null;
		boolean result = false;

		String SQL = "SELECT count(*) FROM " + tablename + " where SLA_UUID = '" + sla_uuid + "' ";
		int count = 0;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {

		}

		if (count > 0) {
			try {
				c.setAutoCommit(false);
				stmt = c.createStatement();
				String sql = "DELETE from " + tablename + " where SLA_UUID='" + sla_uuid + "';";
				stmt.executeUpdate(sql);
				c.commit();
				stmt.close();
				result = true;
			} catch (Exception e) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				ThreadContext.put("type", "W");
				ThreadContext.put("timestamp", timestamp.toString());
				ThreadContext.put("operation", "Delete violated SLA");
				ThreadContext.put("status", "");
				logger.warn("Error deleting violation for the sla_uuid= " + sla_uuid);
				ThreadContext.clearAll();
			}
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Delete violated SLA");
			ThreadContext.put("status", "");
			logger.info("Violation record for sla_uuid=" + sla_uuid + " was deleted succesfully");
		} else {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "W");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Delete violated SLA");
			ThreadContext.put("status", "");
			logger.warn("Error deleting violation for the sla_uuid= " + sla_uuid);
			ThreadContext.clearAll();
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
		Statement stmt = null;

		JSONObject root = new JSONObject();
		JSONArray ns_template = new JSONArray();
		JSONArray cust_sla = new JSONArray();

		System.out.println(tablename);
		if (tablename == "ns_template") {

			try {
				c.setAutoCommit(false);
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + ";");
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
			} catch (Exception e) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				ThreadContext.put("type", "W");
				ThreadContext.put("timestamp", timestamp.toString());
				ThreadContext.put("operation", "Delete violated SLA");
				ThreadContext.put("status", "");
				logger.warn("Error fetching data from table= "+ tablename);
				ThreadContext.clearAll();
			}

		} else if (tablename == "cust_sla") {

			try {
				c.setAutoCommit(false);
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + ";");

				while (rs.next()) {
					String ns_uuid = rs.getString("ns_uuid");
					String sla_uuid = rs.getString("sla_uuid");
					String cust_uuid = rs.getString("cust_uuid");

					JSONObject obj = new JSONObject();
					obj.put("ns_uuid", ns_uuid);
					obj.put("sla_uuid", sla_uuid);
					obj.put("cust_uuid", cust_uuid);
					cust_sla.add(obj);
				}

				root.put("cust_sla", cust_sla);

				rs.close();
				stmt.close();

			} catch (Exception e) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				ThreadContext.put("type", "W");
				ThreadContext.put("timestamp", timestamp.toString());
				ThreadContext.put("operation", "Delete violated SLA");
				ThreadContext.put("status", "");
				logger.warn("Error fetching data from table= "+ tablename);
				ThreadContext.clearAll();
			}

		}
		return root;
	}

}
