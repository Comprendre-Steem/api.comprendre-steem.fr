package fr.comprendresteem;

import org.junit.Assert;
import org.junit.Test;

public class RequesterTest {

	@Test
	public void mentions() throws Exception {
		Assert.assertTrue(104 <= Requester.getMentions("roxane", false, false).size());
	}
	
	@Test
	public void mentions_including_comments() throws Exception {
		Assert.assertTrue(304 <= Requester.getMentions("roxane", true, false).size());		
	}
	
	@Test
	public void including_ownComments() throws Exception {
		Assert.assertTrue(Requester.getMentions("roxane", true, false).size() <= Requester.getMentions("roxane", true, true).size());
	}
	
	@Test
	public void incomingVotes() throws Exception {
		Assert.assertEquals(20, Requester.getIncomingVotes("roxane", 10, 20).size());
	}
}
