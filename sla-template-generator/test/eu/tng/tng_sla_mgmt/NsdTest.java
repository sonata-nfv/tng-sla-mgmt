package eu.tng.tng_sla_mgmt;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class NsdTest {

	@Test
	public void testGetName() {
		Nsd nsd = new Nsd();
		nsd.setName("This is a Test");
		assertTrue(nsd.getName() == "This is a Test");
	}

	@Test
	public void testSetName() {
		Nsd nsd = new Nsd();
		nsd.setName("This is a Test");
		assertTrue(nsd.getName() == "This is a Test");
	}

	@Test
	public void testGetDescription() {
		Nsd nsd = new Nsd();
		nsd.setDescription("This is a Test");
		assertTrue(nsd.getDescription() == "This is a Test");
	}

	@Test
	public void testSetDescription() {
		Nsd nsd = new Nsd();
		nsd.setDescription("This is a Test");
		assertTrue(nsd.getDescription() == "This is a Test");
	}

	@Test
	public void testGetMonDesc() {
		Nsd nsd = new Nsd();
		ArrayList<String> monDesc = new ArrayList<String>();
		monDesc.add("Bandwidth Testing");
		monDesc.add("CPU Testing");
		monDesc.add("RAM Testing");

		nsd.SetMonDesc(monDesc);
		assertTrue(nsd.GetMonDesc().containsAll(monDesc));
	}

	@Test
	public void testSetMonDesc() {
		Nsd nsd = new Nsd();
		ArrayList<String> monDesc = new ArrayList<String>();
		monDesc.add("Bandwidth Testing");
		monDesc.add("CPU Testing");
		monDesc.add("RAM Testing");

		nsd.SetMonDesc(monDesc);
		assertTrue(nsd.GetMonDesc().containsAll(monDesc));
	}

	@Test
	public void testGetMonMetric() {
		Nsd nsd = new Nsd();
		ArrayList<String> MonMetrics = new ArrayList<String>();
		MonMetrics.add("Bandwidth");
		MonMetrics.add("cpu_perc_usage");
		MonMetrics.add("ram_usage");

		nsd.SetMonMetric(MonMetrics);
		assertTrue(nsd.GetMonMetric().containsAll(MonMetrics));
	}

	@Test
	public void testSetMonMetric() {
		Nsd nsd = new Nsd();
		ArrayList<String> MonMetrics = new ArrayList<String>();
		MonMetrics.add("Bandwidth");
		MonMetrics.add("cpu_perc_usage");
		MonMetrics.add("ram_usage");

		nsd.SetMonMetric(MonMetrics);
		assertTrue(nsd.GetMonMetric().containsAll(MonMetrics));
	}

	@Test
	public void testGetMonUnit() {
		Nsd nsd = new Nsd();
		ArrayList<String> MonUnits = new ArrayList<String>();
		MonUnits.add("Mbps");
		MonUnits.add("Ghz");

		nsd.SetMonMetric(MonUnits);
		assertTrue(nsd.GetMonMetric().containsAll(MonUnits));
	}

	@Test
	public void testSetMonUnit() {
		Nsd nsd = new Nsd();
		ArrayList<String> MonUnits = new ArrayList<String>();
		MonUnits.add("Mbps");
		MonUnits.add("Ghz");

		nsd.SetMonMetric(MonUnits);
		assertTrue(nsd.GetMonMetric().containsAll(MonUnits));
	}
}
