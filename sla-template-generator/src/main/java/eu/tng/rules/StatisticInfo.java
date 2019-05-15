package eu.tng.rules;

import org.json.simple.JSONObject;

import eu.tng.correlations.db_operations;

public class StatisticInfo {

	/**
	 * Nº SLA Agreements vs. Violations (in the last 24h / 7 days / 30 days) This
	 * function is called every time an agreement is created and every time a
	 * violation occurs call from > db_operations > insertRecordAgreement and 
	 * > db_operations > insertRecordViolation
	 */
	// all
	public void violationspercentageAll() {

		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		double totalAgreements = db.countTotalAgreements();
		double activeAgreements = db.countActiveAgreements();
		double violatedAgreements = db.countViolatedAgreements();
		db_operations.closePostgreSQL();

		double percentage_violated = 0;
		double percentage_active = 0;

		if (totalAgreements > 0) {
			percentage_violated = (violatedAgreements * 100) / totalAgreements;
			percentage_active = (activeAgreements * 100) / totalAgreements;
		}
		
		System.out.println("[*] violationspercentageAll ==> " + percentage_violated);
	}

	// specific days
	public void violationspercentageDays(int d) {

		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		double totalAgreements = db.countTotalAgreementsDateRange(d);
		double activeAgreements = db.countActiveAgreementsDateRange(d);
		double violatedAgreements = db.countViolatedAgreementsDateRange(d);
		db_operations.closePostgreSQL();
		
		double percentage_violated = 0;
		double percentage_active = 0;
		
		if (totalAgreements > 0) {
			percentage_violated = (violatedAgreements * 100) / totalAgreements;
			percentage_active = (activeAgreements * 100) / totalAgreements;
		}
		
		System.out.println("[*] violationspercentageDays= " + d + " ==> " + percentage_violated);

	}

	/**
	 * Nº Licenses Utilized This function is called every time e license is created
	 * call from > db_operations > CreateLicenseInstance
	 */
	public void getLicensesUtilized() {
		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		int licenses_utilized_number = db.countUtilizedLicense();
		db_operations.closePostgreSQL();
		
		System.out.println("[*] getLicensesUtilized ==> " + licenses_utilized_number);

	}

	/**
	 * Nº Licenses Expired This function is called every time e license is expired
	 * call from > rules > LicensePeriodCheck
	 */
	public void getLicensesExpired() {
		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		int licenses_expired_number = db.countExpiredLicense();
		db_operations.closePostgreSQL();
		
		System.out.println("[*] getLicensesExpired ==> " + licenses_expired_number);

	}

	/**
	 * Nº Licenses Acquired This function is called every time e license is bought
	 * call from > service_api > LicensingAPIs > @Path("/buy")
	 */
	public void getLicensesAcquired() {
		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		int licenses_acquired_number = db.countAcquiredLicense();
		db_operations.closePostgreSQL();
		
		System.out.println("[*] getLicensesAcquired ==> " + licenses_acquired_number);

	}

}
