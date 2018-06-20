package fr.comprendresteem;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import fr.comprendresteem.model.Curation;
import fr.comprendresteem.model.Resteem;
import fr.comprendresteem.model.Vote;

public class RequesterTest {

	@Test
	public void mentions() throws Exception {
		Assert.assertTrue(104 <= Requester.getMentions("roxane", false, false).size());
	}
	
	@Test
	public void mentions_including_comments() throws Exception {
		Assert.assertTrue(250 == Requester.getMentions("roxane", true, false).size());		
	}
	
	@Test
	public void including_ownComments() throws Exception {
		Assert.assertTrue(Requester.getMentions("roxane", true, false).size() <= Requester.getMentions("roxane", true, true).size());
	}
	
	@Test
	public void incomingVotes() throws Exception {
		Assert.assertEquals(20, Requester.getIncomingVotes("roxane", 10, 20).size());
	}
	
	@Test
	public void outgoingVotes() throws Exception {
		List<Vote> res = Requester.getOutgoingVotes("oroger", 10, 20);
		Assert.assertEquals(20, res.size());
	}

	@Test
	public void getTotalIncomingVotes() throws Exception {
		Assert.assertTrue(6500L < (long) Requester.getTotalIncomingVotes("roxane"));
	}
	
	@Test
	public void getResteem() throws Exception {
		List<Resteem> resteem = Requester.getResteem("roxane");
		Assert.assertTrue(350 < resteem.size());
		Assert.assertEquals("2017-06-23 21:40:06.0", resteem.get(resteem.size()-1).timestamp.toString());
	}
	
	@Test
	public void getCuration() throws Exception {
		List<Curation> curations = Requester.getCuration("roxane", "fr", 0);
		Assert.assertFalse(curations.isEmpty());
	}
}
