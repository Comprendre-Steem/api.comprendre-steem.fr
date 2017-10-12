package fr.comprendresteem.model;

import java.util.Date;

public class Resteem {

	public final String author;
	public final String account;
	public final String permlink;
	public final Date timestamp;
	
	public Resteem(String author, String account, String permlink, Date timestamp) {
		super();
		this.author = author;
		this.account = account;
		this.permlink = permlink;
		this.timestamp = timestamp;
	}

	public String getUrl() {
		return author + "/" + permlink;
	}

	@Override
	public String toString() {
		return "Resteem [author=" + author + ", account=" + account + ", permlink=" + permlink + ", timestamp="
				+ timestamp + "]";
	}
	
}
