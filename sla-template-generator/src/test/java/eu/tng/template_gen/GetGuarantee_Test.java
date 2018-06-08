package eu.tng.template_gen;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.junit.Test;

public class GetGuarantee_Test {

	GetGuarantee gt = new GetGuarantee();

	@Test
	public void testGetGuarantee() {

		ArrayList<String> guarantees = new ArrayList<String>();
		guarantees.add("g1");
		guarantees.add("g2");
		guarantees.add("g3");
		guarantees.add("g4");
		guarantees.add("g5");

		assertTrue(gt.getGuarantee(guarantees).size() == 5);

	}

}
