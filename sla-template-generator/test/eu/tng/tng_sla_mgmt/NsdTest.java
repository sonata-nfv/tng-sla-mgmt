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
