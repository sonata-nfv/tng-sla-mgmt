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
 */

package eu.tng.template_gen;

import java.util.ArrayList;

public class Nsd {
	private static String name;
	private static String vendor;
	private static String version;
	private static String description;
	private static ArrayList<String> mon_desc = new ArrayList<String>();
	private static ArrayList<String> mon_metric = new ArrayList<String>();
	private static ArrayList<String> mon_unit = new ArrayList<String>();

	// public method to get the ns name
	public String getName() {
		return name;
	}

	// public method to set the ns name
	@SuppressWarnings("static-access")
	public void setName(String name) {
		this.name = name;
	}

	// public method to get the ns name
	public String getVendor() {
		return vendor;
	}

	// public method to set the ns name
	@SuppressWarnings("static-access")
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	// public method to get the ns name
	public String getVersion() {
		return version;
	}

	// public method to set the ns name
	@SuppressWarnings("static-access")
	public void setVersion(String version) {
		this.version = version;
	}

	// public method to get the ns description
	public String getDescription() {
		return description;
	}

	// public method to set the ns description
	@SuppressWarnings("static-access")
	public void setDescription(String description) {
		this.description = description;
	}

	// public method to get the ns mon_desc
	public ArrayList<String> GetMonDesc() {
		return mon_desc;
	}

	// public method to set the ns mon_desc
	@SuppressWarnings("static-access")
	public void SetMonDesc(ArrayList<String> mon_desc) {
		this.mon_desc = mon_desc;
	}

	// public method to get the ns mon_metric
	public ArrayList<String> GetMonMetric() {
		return mon_metric;
	}

	// public method to set the ns mon_metric
	@SuppressWarnings("static-access")
	public void SetMonMetric(ArrayList<String> mon_metric) {
		this.mon_metric = mon_metric;
	}

	// public method to get the ns mon_unit
	public ArrayList<String> GetMonUnit() {
		return mon_unit;
	}

	// public method to set the ns mon_unit
	@SuppressWarnings("static-access")
	public void SetMonUnit(ArrayList<String> mon_unit) {
		this.mon_unit = mon_unit;
	}

}
