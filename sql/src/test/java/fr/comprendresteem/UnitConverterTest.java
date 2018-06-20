package fr.comprendresteem;

import org.junit.Assert;
import org.junit.Test;

public class UnitConverterTest {

	@Test
	public void roxane() throws Exception {
		Assert.assertEquals(70.174, UnitConverter.getSimpleReputation(104553894962739L), 0.0);
	}
	
	@Test
	public void ragepeanut() throws Exception {
		Assert.assertEquals(60.6, UnitConverter.getSimpleReputation(9027225078399L), 0.0);
	}
	
	@Test
	public void checky() throws Exception {
		Assert.assertEquals(25.0, UnitConverter.getSimpleReputation(0L), 0.0);
	}
	
	@Test
	public void a00() throws Exception {
		Assert.assertEquals(-7.909, UnitConverter.getSimpleReputation(-4534522956660L), 0.0);
	}
	
}
