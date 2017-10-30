package fr.comprendresteem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.comprendresteem.model.Mention;
import fr.comprendresteem.model.Resteem;
import fr.comprendresteem.model.Vote;

public class Requester {

	private static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String JDBC_URL = "jdbc:sqlserver://sql.steemsql.com;databaseName=DBSteem";
	private static final String DB_USERNAME = "steemit";
	private static final String DB_PASSWORD = "steemit";

	private Requester() {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			// Should not happen since driver is bundled in the application (see pom.xml)
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(),e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private static Connection getDb() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER);
			Connection db = DriverManager.getConnection(JDBC_URL,DB_USERNAME,DB_PASSWORD);
			db.setReadOnly(true);
			return db;
		} catch (ClassNotFoundException e) {
			// Should not happen
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(),e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static List<Mention> getMentions(String username, boolean includeComments, boolean includeOwnComments) throws SQLException {
		List<Mention> articles = new ArrayList<>();
		
		// TODO use PrepareStatement
		String query = "SELECT author, root_title, title, url, created, category "
				+ "FROM Comments "
				+ "(NOLOCK) "
				+ "WHERE CONTAINS((title, body), '@%s') AND (title LIKE '%%@%s%%' OR body LIKE '%%@%s%%') "
				+ "%s "
				+ "%s "
				+ "ORDER BY created DESC "
				+ "OFFSET 0 ROWS FETCH NEXT 2000 ROWS ONLY;";
		
		String sql = String.format(query, username, username, username, 
				includeComments ? "" : "AND depth = 0", 
				includeOwnComments ? "" : "AND author NOT IN ('" + username +"')"
			);
		
		try (PreparedStatement stat = getDb().prepareStatement(sql); ResultSet rs = stat.executeQuery()) {
			while (rs.next()) {
				String author = rs.getString("author");
				String title = rs.getString("root_title");
				String permlink = rs.getString("url");
				Date created = rs.getTimestamp("created");
				String category = rs.getString("category");
				articles.add(new Mention(author, title, permlink, created, category));
			}
		}
		
		return articles;
	}
	
	public static List<Vote> getIncomingVotes(String username, long offset, long limit) throws SQLException {
		List<Vote> votes = new ArrayList<>();
		
		String sql = "SELECT author, voter, permlink, timestamp, weight "
				+ "FROM TxVotes "
				+ "(NOLOCK) "
				+ "WHERE ID IN ("
				+ "    SELECT MAX(ID) " 
				+ "    FROM TxVotes " 
				+ "    (NOLOCK) " 
				+ "    WHERE author = ? " 
				+ "    GROUP BY permlink, voter " 
				+ ") "
				+ "ORDER BY timestamp DESC "
				+ "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;";
		
		try (PreparedStatement stat = getDb().prepareStatement(sql)) {
			int idx = 1;
			stat.setString(idx++, username);
			stat.setLong(idx++, offset);
			stat.setLong(idx++, limit);
			
			try (ResultSet rs = stat.executeQuery()) {
				while (rs.next()) {
					String author = rs.getString("author");
					String voter = rs.getString("voter");
					String permlink = rs.getString("permlink");
					Date timestamp = rs.getTimestamp("timestamp");
					double weight = rs.getDouble("weight") / 100.0;
					
					votes.add(new Vote(author, voter, permlink, timestamp, weight));
				}
			}
		}
		
		return votes;
	}
	
	public static Long getTotalIncomingVotes(String username) throws SQLException {
		Long total = -1L;
		
		String sql = "SELECT COUNT(*) "
				+ "FROM TxVotes "
				+ "(NOLOCK) "
				+ "WHERE ID IN ("
				+ "    SELECT MAX(ID) " 
				+ "    FROM TxVotes " 
				+ "    (NOLOCK) " 
				+ "    WHERE author = ? " 
				+ "    GROUP BY permlink, voter " 
				+ ");";
		
		try (PreparedStatement stat = getDb().prepareStatement(sql)) {
			int idx = 1;
			stat.setString(idx++, username);
			
			try (ResultSet rs = stat.executeQuery()) {
				if (rs.next()) {
					total = rs.getLong(1);
				}
			}
		}
		
		return total;
	}
	
	public static List<Resteem> getResteem(String username) throws SQLException {
		List<Resteem> resteem = new ArrayList<>();
		
		String sql = "SELECT * "
				+ "FROM Reblogs "
				+ "(NOLOCK) "
				+ "WHERE author = ? "
				+ "ORDER BY timestamp DESC "
				+ "OFFSET 0 ROWS FETCH NEXT 2000 ROWS ONLY;";
		
		try (PreparedStatement stat = getDb().prepareStatement(sql)) {
			int idx = 1;
			stat.setString(idx++, username);
			
			try (ResultSet rs = stat.executeQuery()) {
				while (rs.next()) {
					String author = rs.getString("author");
					String account = rs.getString("account");
					String permlink = rs.getString("permlink");
					Date timestamp = rs.getTimestamp("timestamp");
					
					resteem.add(new Resteem(author, account, permlink, timestamp));
				}
			}
		}
		
		return resteem;
	}
	
}
