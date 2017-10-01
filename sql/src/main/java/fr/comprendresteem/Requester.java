package fr.comprendresteem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.comprendresteem.model.Mention;

public class Requester {

	private Requester() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			// Should not happen
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private static Connection getDb() throws SQLException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection db = DriverManager.getConnection("jdbc:sqlserver://sql.steemsql.com;databaseName=DBSteem","steemit","steemit");
			db.setReadOnly(true);
			return db;
		} catch (ClassNotFoundException e) {
			// Should not happen
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static List<Mention> getMentions(String username, boolean includeComments, boolean includeOwnComments) throws SQLException {
		List<Mention> articles = new ArrayList<>();
		
		String query = "SELECT author, title, url, created "
				+ "FROM Comments "
				+ "(NOLOCK) "
				+ "WHERE CONTAINS((title, body), '@%s') AND (title LIKE '%%@%s%%' OR body LIKE '%%@%s%%') "
				+ "%s "
				+ "%s "
				+ "ORDER BY created DESC;";
		
		String sql = String.format(query, username, username, username, 
				includeComments ? "" : "AND depth = 0", 
				includeOwnComments ? "" : "AND author NOT IN ('" + username +"')"
			);
		
		try (PreparedStatement stat = getDb().prepareStatement(sql); ResultSet rs = stat.executeQuery()) {
			while (rs.next()) {
				String author = rs.getString("author");
				String title = rs.getString("title");
				String permlink = rs.getString("url");
				Date created = rs.getDate("created");
				articles.add(new Mention(author, title, permlink, created));
			}
		}
		
		return articles;
	}
	
}
