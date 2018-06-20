package fr.comprendresteem.api.model.response;

import java.util.List;

import fr.comprendresteem.model.Comment;

public class GetCommentsResponse {
	
	public final String username;
	public final long size;
	public final List<Comment> comments;

	public GetCommentsResponse(String username, List<Comment> comments) {
		this.username = username;
		this.size = comments.size();
		this.comments = comments;
	}
}
