package fr.comprendresteem.model;

import java.util.Date;

public class Mention {
	
	public final String author;
	public final String title;
	public final String permlink;
	public final Date created;
	public final String category;

	public Mention(String author, String title, String permlink, Date created, String category) {
		this.author = author;
		this.title = title;
		this.permlink = permlink;
		this.created = created;
		this.category = category;
	}

	@Override
	public String toString() {
		return "Article [author=" + author + ", title=" + title + ", permlink=" + permlink + ", created=" + created + ", category=" + category +"]";
	}
	
}
