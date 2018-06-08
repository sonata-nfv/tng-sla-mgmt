package eu.tng.template_gen;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class Nsd_Test {

	@Test
	public void testGetName() {
		Nsd nsd = new Nsd();
		nsd.setName("TestName");
		assertTrue(nsd.getName() == "TestName");
	}

	@Test
	public void testSetName() {
		Nsd nsd = new Nsd();
		nsd.setName("TestName");
		assertTrue(nsd.getName() == "TestName");
	}

	@Test
	public void testGetVendor() {
		Nsd nsd = new Nsd();
		nsd.setVendor("TestVendor");
		assertTrue(nsd.getVendor() == "TestVendor");
	}

	@Test
	public void testSetVendor() {
		Nsd nsd = new Nsd();
		nsd.setVendor("TestVendor");
		assertTrue(nsd.getVendor() == "TestVendor");
	}

	@Test
	public void testGetVersion() {
		Nsd nsd = new Nsd();
		nsd.setVersion("TestVersion");
		assertTrue(nsd.getVersion() == "TestVersion");
	}

	@Test
	public void testSetVersion() {
		Nsd nsd = new Nsd();
		nsd.setVersion("TestVersion");
		assertTrue(nsd.getVersion() == "TestVersion");
	}

	@Test
	public void testGetDescription() {
		Nsd nsd = new Nsd();
		nsd.setDescription("TestDescription");
		assertTrue(nsd.getDescription() == "TestDescription");

	}

	@Test
	public void testSetDescription() {
		Nsd nsd = new Nsd();
		nsd.setDescription("TestDescription");
		assertTrue(nsd.getDescription() == "TestDescription");
	}

	@Test
	public void testGetMonDesc() {
		Nsd nsd = new Nsd();
	}

	@Test
	public void testSetMonDesc() {
		ArrayList<String> mon_Desc = new ArrayList<String>();
		mon_Desc.add("description1");
		mon_Desc.add("description2");
		mon_Desc.add("description3");

		Nsd nsd = new Nsd();
		nsd.SetMonDesc(mon_Desc);

		assertTrue(nsd.GetMonDesc().size() == 3);
	}

	@Test
	public void testGetMonMetric() {
		ArrayList<String> mon_metric = new ArrayList<String>();
		mon_metric.add("metric1");
		mon_metric.add("metric2");
		mon_metric.add("metric3");

		Nsd nsd = new Nsd();
		nsd.SetMonMetric(mon_metric);

		assertTrue(nsd.GetMonMetric().size() == 3);
	}

	@Test
	public void testSetMonMetric() {
		ArrayList<String> mon_metric = new ArrayList<String>();
		mon_metric.add("metric1");
		mon_metric.add("metric2");
		mon_metric.add("metric3");

		Nsd nsd = new Nsd();
		nsd.SetMonMetric(mon_metric);

		assertTrue(nsd.GetMonMetric().size() == 3);
	}

	@Test
	public void testGetMonUnit() {

		ArrayList<String> mon_unit = new ArrayList<String>();
		mon_unit.add("unit1");
		mon_unit.add("unit2");
		mon_unit.add("unit3");

		Nsd nsd = new Nsd();
		nsd.SetMonUnit(mon_unit);

		assertTrue(nsd.GetMonUnit().size() == 3);
	}

	@Test
	public void testSetMonUnit() {
		ArrayList<String> mon_unit = new ArrayList<String>();
		mon_unit.add("unit1");
		mon_unit.add("unit2");
		mon_unit.add("unit3");

		Nsd nsd = new Nsd();
		nsd.SetMonUnit(mon_unit);

		assertTrue(nsd.GetMonUnit().size() == 3);
	}

}
