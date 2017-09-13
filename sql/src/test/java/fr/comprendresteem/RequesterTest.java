package fr.comprendresteem;

import org.junit.Assert;
import org.junit.Test;

public class RequesterTest {

	@Test
	public void mentions() throws Exception {
		Assert.assertTrue(104 <= Requester.getMentions("roxane", false).size());
	}
	
	@Test
	public void mentions_including_comments() throws Exception {
		Assert.assertTrue(304 <=Requester.getMentions("roxane", true).size());
	}
	
}
