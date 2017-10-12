package fr.comprendresteem.api.model.response;

import java.util.Collections;
import java.util.List;

import fr.comprendresteem.model.Resteem;;

public class GetResteemResponse {
	
	public final String username;
	public final long size;
	public final List<Resteem> resteem;

	public GetResteemResponse(String username, List<Resteem> resteem) {
		this.username = username;
		this.size = resteem.size();
		this.resteem = Collections.unmodifiableList(resteem);
	}

}
