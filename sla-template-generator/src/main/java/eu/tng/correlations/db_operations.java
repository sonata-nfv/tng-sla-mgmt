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
	public void connectPostgreSQL() {
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sla-manager", "postgres", "admin");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened sla-manager database successfully");
	}

	/**
	 * Create table if not exist
	 */
	public void createTableNSTemplate() {
		try {
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS ns_template" + "(ID  SERIAL PRIMARY KEY,"
					+ " NS_UUID TEXT NOT NULL, " + "SLA_UUID  TEXT NOT NULL )";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println("Table ns_template created successfully");
	}

	/**
	 * Insert Record
	 */
	public void insertRecord(String tablename, String ns_uuid, String sla_uuid) {

		try {
			c.setAutoCommit(false);
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO " + tablename + " (ns_uuid,sla_uuid) " + "VALUES ('" + ns_uuid + "','" + sla_uuid
					+ "');";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
			System.out.println("Records created successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Delete Record
	 */
	public void deleteRecord(String tablename, String sla_uuid) {
		Statement stmt = null;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "DELETE from " + tablename + " where SLA_UUID='" + sla_uuid + "';";
			stmt.executeUpdate(sql);
			c.commit();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Records with sla_uuid:" + sla_uuid + " deleted successfully");
	}

	/**
	 * Select all records
	 * 
	 * @return
	 */
	public JSONObject selectAllRecords(String tablename) {
		Statement stmt = null;

		JSONObject root = new JSONObject();
		JSONArray ns_template = new JSONArray();

		if (tablename == "ns_template") {

			try {
				c.setAutoCommit(false);
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + ";");
				while (rs.next()) {
					int id = rs.getInt("id");
					String ns_uuid = rs.getString("ns_uuid");
					String sla_uuid = rs.getString("sla_uuid");
					System.out.println("ID = " + id);
					System.out.println("NS_UUID = " + ns_uuid);
					System.out.println("SLA_UUID = " + sla_uuid);

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
				System.exit(0);
			}

		} else {

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
