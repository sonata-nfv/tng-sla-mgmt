package eu.tng.correlations;

public class Cust_sla_corr {

	/**
	 * Create a correlation between an instatiated network service, a customer and a
	 * sla
	 */
	public void createCustSlaCorr(String ns_uuid, String sla_uuid, String cust_uuid) {

		String tablename = "cust_sla";

		db_operations dbo = new db_operations();

		dbo.connectPostgreSQL();
		dbo.createTableCustSla();
		dbo.insertRecordAgreement(ns_uuid, sla_uuid, cust_uuid);
		dbo.closePostgreSQL();

	}

	/**
	 * Delete a correlation between a network service and a sla template
	 */
	public static void deleteNsTempCorr(String sla_uuid) {
		String tablename = "cust_sla";
		db_operations dbo = new db_operations();
		dbo.connectPostgreSQL();
		dbo.deleteRecord(tablename, sla_uuid);
		dbo.closePostgreSQL();
	}

}
