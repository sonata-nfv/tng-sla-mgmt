package eu.tng.rules;

import eu.tng.correlations.db_operations;
import java.io.IOException;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;

public class StatisticInfo {

	// define the push gateway registry
    CollectorRegistry registry = new CollectorRegistry();

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
		
		// create the gauge metric for the push gatewayl
        Gauge gauge_percentage_violated = Gauge.build().name("percentage_violated_agreements").help("The percentage of the violated agreements").register(registry);
        System.out.println("[*] gauge_percentage_violated ==> " + gauge_percentage_violated);
        try {
            // Set the value of the metric
        	gauge_percentage_violated.set(percentage_violated);
            System.out.println("[*] gauge_percentage_violated ==> " + gauge_percentage_violated);
            System.out.println("[*] gauge_percentage_violated with value ==> " + gauge_percentage_violated);


        } finally {
            PushGateway pg = new PushGateway(System.getenv("MONITORING_PUSH_GATEWAY"));
            System.out.println("[*] pg => "+ pg.toString());

            try {
                pg.pushAdd(registry, "SLA_job");
                System.out.print("Success");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("[*] error to push gateway => "+ e);

            }
        }
		
		System.out.println("[*] violationspercentageAll ==> " + percentage_violated);
	}


	/**
	 * Nº Licenses Utilized This function is called every time e license is created
	 * call from > db_operations > CreateLicenseInstance
	 */
	public void getLicensesUtilized() {
		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		double licenses_utilized_number = db.countUtilizedLicense();
		db_operations.closePostgreSQL();
		
		// create the gauge metric for the push gateway
        Gauge gauge_licenses_utilized_number = Gauge.build().name("licenses_utilized_number").help("The number of utiized licenses").register(registry);
        try {
            // Set the value of the metric
        	gauge_licenses_utilized_number.set(licenses_utilized_number);
        } finally {
            PushGateway pg = new PushGateway(System.getenv("MONITORING_PUSH_GATEWAY"));
            try {
                pg.pushAdd(registry, "SLA_job");
                System.out.print("Success");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
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
		
		// create the gauge metric for the push gateway
        Gauge gauge_licenses_expired_number = Gauge.build().name("licenses_expired_number").help("The number of expired licenses").register(registry);
        try {
            // Set the value of the metric
        	gauge_licenses_expired_number.set(licenses_expired_number);
        } finally {
            PushGateway pg = new PushGateway(System.getenv("MONITORING_PUSH_GATEWAY"));
            try {
                pg.pushAdd(registry, "SLA_job");
                System.out.print("Success");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
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
		
		// create the gauge metric for the push gateway
        Gauge gauge_licenses_acquired_number = Gauge.build().name("licenses_acquired_number").help("The number of acquired licenses").register(registry);
        try {
            // Set the value of the metric
        	gauge_licenses_acquired_number.set(licenses_acquired_number);
        } finally {
            PushGateway pg = new PushGateway(System.getenv("MONITORING_PUSH_GATEWAY"));
            try {
                pg.pushAdd(registry, "SLA_job");
                System.out.print("Success");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
		System.out.println("[*] getLicensesAcquired ==> " + licenses_acquired_number);

	}

}
