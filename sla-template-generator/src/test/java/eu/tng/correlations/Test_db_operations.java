package eu.tng.correlations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Test_db_operations {

	db_operations dbo = new db_operations();

	@Test
	public void testCreateTableNSTemplate() {

		dbo.connectPostgreSQL();
		assertTrue(dbo.createTableNSTemplate() == true);
		dbo.closePostgreSQL();
	}

	@Test
	public void testInsertRecord() {
		dbo.connectPostgreSQL();
		assertTrue(dbo.insertRecord("ns_template", "test", "test") == true);
		dbo.closePostgreSQL();
	}

	@Test
	public void testSelectAllRecords() {
		dbo.connectPostgreSQL();
		assertFalse(dbo.selectAllRecords("ns_template").isEmpty());
		dbo.closePostgreSQL();
	}

	@Test
	public void testDeleteRecord() {
		dbo.connectPostgreSQL();
		assertTrue(dbo.deleteRecord("ns_template", "test") == true);
		dbo.closePostgreSQL();
	}
	
	
}
