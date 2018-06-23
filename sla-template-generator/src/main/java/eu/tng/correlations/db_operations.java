/*
 * Copyright (c) 2017 5GTANGO, UPRC ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the 5GTANGO, UPRC nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the 5GTANGO project, funded by the European
 * Commission under Grant number 761493 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the 5GTANGO partner consortium
 * (www.5gtango.eu).
 *
 * @author Evgenia Kapassa (MSc), UPRC
 * 
 * @author Marios Touloupou (MSc), UPRC
 * 
 */

package eu.tng.correlations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSet;

public class db_operations {

	static Connection c = null;
	static Statement stmt = null;

	/**
	 * Connect to PostgreSQL
	 */
	public static boolean connectPostgreSQL() {
		boolean connect = false;
		try {

			Class.forName("org.postgresql.Driver");
//			 c =
//			 DriverManager.getConnection("jdbc:postgresql://localhost:5432/sla-manager","postgres",
//			 "admin");
			c = DriverManager
					.getConnection(
							"jdbc:postgresql://" + System.getenv("DATABASE_HOST") + ":" + System.getenv("DATABASE_PORT")
									+ "/" + System.getenv("GTK_DB_NAME"),
							System.getenv("GTK_DB_USER"), System.getenv("GTK_DB_PASS"));
			connect = true;
			System.out.println("Opened sla-manager database successfully");

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			connect = false;
		}
		return connect;
	}

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
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		System.out.println("Table Created? " + result);
		return result;
	}

	/**
	 * Create table if not exist - customer-sla correlation
	 */
	public static void createTableCustSla() {
		try {
			stmt = c.createStatement();
			String sql = "CREATE TABLE cust_sla" + "(ID  SERIAL PRIMARY KEY," + " NS_UUID TEXT NOT NULL, "
					+ "NS_NAME TEXT NOT NULL," + "SLA_UUID  TEXT NOT NULL," + "SLA_NAME TEXT NOT NULL,"
					+ "SLA_DATE TIMESTAMPTZ DEFAULT Now()," + "SLA_STATUS TEXT NOT NULL," + "CUST_EMAIL TEXT NOT NULL,"
					+ "CUST_UUID  TEXT NOT NULL," + "INST_ID TEXT NOT NULL," + "INST_STATUS  TEXT NOT NULL )";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println("Table cust_sla created successfully");

	}

	/**
	 * Create table if not exist - sla_violations
	 */
	public void createTableViolations() {
		try {
			stmt = c.createStatement();
			String sql = "CREATE TABLE sla_violations" + "(ID  SERIAL PRIMARY KEY," + " NS_UUID TEXT NOT NULL, "
					+ "SLA_UUID TEXT NOT NULL," + "VIOLATION_TIME TEXT NOT NULL," + "ALERT_STATE TEXT NOT NULL,"
					+ "CUST_UUID  TEXT NOT NULL )";
			stmt.executeUpdate(sql);
			stmt.close();
			System.out.println("Table sla_violations created successfully");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.out.println("Error creating sla violations table or already exists");

		}

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

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Records created successfully? " + result);

		return result;
	}

	/**
	 * Insert Record cust-sla correlation
	 * 
	 */
	public void insertRecordAgreement(String ns_uuid, String ns_name, String sla_uuid, String sla_name,
			String sla_status, String cust_name, String cust_uuid, String inst_status, String correlation_id) {

		try {
			c.setAutoCommit(false);
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO cust_sla "
					+ " (ns_uuid, ns_name, sla_uuid, sla_name, sla_status, cust_email, cust_uuid, inst_status, inst_id) "
					+ "VALUES ('" + ns_uuid + "','" + ns_name + "','" + sla_uuid + "' ,'" + sla_name + "' ,'"
					+ sla_status + "','" + cust_name + "','" + cust_uuid + "', '" + inst_status + "' , '"
					+ correlation_id + "');";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			System.out.println("Records created successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Insert Record cust-sla correlation
	 * 
	 */
	public void insertRecordViolation(String ns_uuid, String sla_uuid, String violation_time, String alert_state,
			String cust_uuid) {

		try {
			c.setAutoCommit(false);
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO sla_violations  (ns_uuid, sla_uuid,violation_time, alert_state, cust_uuid ) VALUES ('"
					+ ns_uuid + "', '" + sla_uuid + "', '" + violation_time + "','" + alert_state + "', '" + cust_uuid
					+ "');  ";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			System.out.println("Violation record created successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public JSONArray getViolatedSLA(String ns_uuid) {

		Statement stmt = null;

		String sla_uuid = null;
		String cust_uuid = null;
		JSONArray violated_sla = new JSONArray();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM cust_sla WHERE ns_uuid='" + ns_uuid + "' AND inst_status='READY';");
			while (rs.next()) {
				sla_uuid = rs.getString("sla_uuid");
				cust_uuid = rs.getString("cust_uuid");
				System.out.println("sla_uuid = " + sla_uuid);
				System.out.println("cust_uuid = " + cust_uuid);

				violated_sla.add(sla_uuid);
				violated_sla.add(cust_uuid);

			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return violated_sla;
	}

	/**
	 * Update Record cust-sla correlation
	 * 
	 */
	public static void UpdateRecordAgreement(String inst_status, String correlation_id) {

		try {
			c.setAutoCommit(false);
			Statement stmt = c.createStatement();
			System.out.println(correlation_id);

			String sql = "UPDATE cust_sla set inst_status = '" + inst_status + "' where inst_id = '" + correlation_id
					+ "' ; ";
			stmt.executeUpdate(sql);
			stmt.close();

			c.commit();
			System.out.println("Update done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Delete Record
	 */
	public boolean deleteRecord(String tablename, String sla_uuid) {
		Statement stmt = null;
		boolean result = false;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "DELETE from " + tablename + " where SLA_UUID='" + sla_uuid + "';";
			stmt.executeUpdate(sql);
			c.commit();
			stmt.close();
			result = true;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println("Records with deleted? " + result);

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
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
			}

		}

		return root;
	}

	/**
	 * Select all records
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
			ResultSet rs = stmt.executeQuery("SELECT * FROM cust_sla WHERE inst_status='READY';");
			while (rs.next()) {
				String ns_uuid = rs.getString("ns_uuid");
				String ns_name = rs.getString("ns_name");
				String sla_uuid = rs.getString("sla_uuid");
				String sla_name = rs.getString("sla_name");
				String sla_date = rs.getString("sla_date");
				String sla_status = rs.getString("sla_status");
				String cust_email = rs.getString("cust_email");
				String cust_uuid = rs.getString("cust_uuid");

				JSONObject obj = new JSONObject();
				obj.put("ns_uuid", ns_uuid);
				obj.put("ns_name", ns_name);
				obj.put("sla_name", sla_name);
				obj.put("sla_date", sla_date);
				obj.put("sla_status", sla_status);
				obj.put("sla_uuid", sla_uuid);
				obj.put("cust_email", cust_email);
				obj.put("cust_uuid", cust_uuid);

				agreements.add(obj);
			}

			root.put("agreements", agreements);

			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println(root);
		return root;
	}

	/**
	 * Get agreement per NS uuid
	 */
	@SuppressWarnings("unchecked")
	public JSONObject selectAgreementPerNS(String nsuuid) {

		Statement stmt = null;
		JSONObject root = new JSONObject();
		JSONArray cust_sla = new JSONArray();

		nsuuid = nsuuid.trim();

		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM cust_sla WHERE ns_uuid = '" + nsuuid + "' AND inst_status='READY'; ");
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
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		return root;
	}

	/**
	 * Get agreement per customer
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
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		return root;
	}

	/**
	 * Close connection with PostgreSQL
	 */
	public void closePostgreSQL() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}