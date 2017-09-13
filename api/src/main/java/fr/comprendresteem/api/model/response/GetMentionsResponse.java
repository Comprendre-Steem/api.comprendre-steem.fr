package fr.comprendresteem.api.model.response;

import java.util.Collections;
import java.util.List;

import fr.comprendresteem.model.Mention;;

public class GetMentionsResponse {
	
	public final String username;
	public final long size;
	public final List<Mention> mentions;

	public GetMentionsResponse(String username, List<Mention> mentions) {
		this.username = username;
		this.size = mentions.size();
		this.mentions = Collections.unmodifiableList(mentions);
	}

}
