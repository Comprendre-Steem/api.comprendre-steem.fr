package fr.comprendresteem.model;

import java.util.Date;

public class Comment {
	
	public final long id;
	public final String author;
	public final double reputation;
	public final String body;
	public final String url;
	public final String permlink;
	public final Date created;
	public final double payout;
	
	public final long rootId;
	public final String rootTitle;
	
	public Comment(long id, String author, double reputation, String body, String url, String permlink, Date created, double payout, long rootId, String rootTitle) {
		super();
		this.id = id;
		this.author = author;
		this.reputation = reputation;
		this.body = body;
		this.url = url;
		this.permlink = permlink;
		this.created = created;
		this.payout = payout;
		this.rootId = rootId;
		this.rootTitle = rootTitle;
	}

	public String getAuthor() {
		return author;
	}

	public double getReputation() {
		return reputation;
	}

	public String getBody() {
		return body;
	}

	public String getUrl() {
		return url;
	}

	public String getPermlink() {
		return permlink;
	}

	public Date getCreated() {
		return created;
	}

	public double getPayout() {
		return payout;
	}
	
	public long getId() {
		return id;
	}

	public long getRootId() {
		return rootId;
	}

	public String getRootTitle() {
		return rootTitle;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", author=" + author + ", reputation=" + reputation + ", body=" + body + ", url="
				+ url + ", permlink=" + permlink + ", created=" + created + ", payout=" + payout + ", rootId=" + rootId
				+ ", rootTitle=" + rootTitle + "]";
	}
	
}
