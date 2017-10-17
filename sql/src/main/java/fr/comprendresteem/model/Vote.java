package fr.comprendresteem.model;

import java.util.Date;

public class Vote {

	public final String author;
	public final String voter;
	public final String permlink;
	public final Date timestamp;
	public final double weight;

	public Vote(String author, String voter, String permlink, Date timestamp, double weight) {
		super();
		this.author = author;
		this.voter = voter;
		this.permlink = permlink;
		this.timestamp = timestamp;
		this.weight = weight;
	}

	public String getUrl() {
		return author + "/" + permlink;
	}

	@Override
	public String toString() {
		return "Vote [author=" + author + ", voter=" + voter + ", permlink=" + permlink + ", timestamp=" + timestamp
				+ ", weight=" + weight + "]";
	}

}
