package fr.comprendresteem.api.model.response;

import java.util.Collections;
import java.util.List;

import fr.comprendresteem.model.Vote;;

public class GetIncomingVotesResponse {
	
	public final String username;
	public final long size;
	public final List<Vote> votes;

	public GetIncomingVotesResponse(String username, List<Vote> votes) {
		this.username = username;
		this.size = votes.size();
		this.votes = Collections.unmodifiableList(votes);
	}

}
