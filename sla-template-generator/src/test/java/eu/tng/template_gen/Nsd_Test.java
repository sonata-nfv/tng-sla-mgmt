/*
 * 
 *  Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  ALL RIGHTS RESERVED.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *              http://www.apache.org/licenses/LICENSE-2.0
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
 *
 */

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
