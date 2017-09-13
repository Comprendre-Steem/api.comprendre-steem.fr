package fr.comprendresteem.model;

import java.util.Date;

public class Mention {
	
	public final String author;
	public final String title;
	public final String permlink;
	public final Date created;

	public Mention(String author, String title, String permlink, Date created) {
		this.author = author;
		this.title = title;
		this.permlink = permlink;
		this.created = created;
	}

	@Override
	public String toString() {
		return "Article [author=" + author + ", title=" + title + ", permlink=" + permlink + ", created=" + created + "]";
	}
	
}
