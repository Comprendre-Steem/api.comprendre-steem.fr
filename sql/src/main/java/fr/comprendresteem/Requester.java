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

import fr.comprendresteem.model.Comment;
import fr.comprendresteem.model.Curation;
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
				+ "OFFSET 0 ROWS FETCH NEXT 250 ROWS ONLY;";
		
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
	
	public static List<Vote> getOutgoingVotes(String username, long offset, long limit) throws SQLException {
		List<Vote> votes = new ArrayList<>();
		
		String sql = "SELECT author, voter, permlink, timestamp, weight "
				+ "FROM TxVotes "
				+ "(NOLOCK) "
				+ "WHERE ID IN ("
				+ "    SELECT MAX(ID) " 
				+ "    FROM TxVotes " 
				+ "    (NOLOCK) " 
				+ "    WHERE voter = ? " 
				+ "    GROUP BY permlink, author " 
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
	
	public static List<Curation> getCuration(String username, String tag, int dayBack) throws SQLException {
		List<Curation> curations = new ArrayList<>();
		
		String sql = "SELECT c.title, c.url, v.author, v.permlink, v.weight, JSON_QUERY(c.json_metadata, '$.tags') AS tags "
				+ "FROM TxVotes AS v "
				+ "JOIN Comments AS c ON c.permlink = v.permlink "
				+ "WHERE v.voter = ? "
				+ "AND v.timestamp >= CONVERT(DATE, DATEADD(day, ?, GETDATE())) AND v.timestamp < CONVERT(DATE, DATEADD(day, ?, GETDATE())) "
				+ "AND c.created >= DATEADD(day, -8, GETDATE()) "
				+ "AND c.depth = 0 "
				+ "AND (ISJSON(c.json_metadata) > 0 AND JSON_QUERY(c.json_metadata, '$.tags') LIKE '%' + ? + '%');";
		
		try (PreparedStatement stat = getDb().prepareStatement(sql)) {
			int idx = 1;
			stat.setString(idx++, username);
			stat.setInt(idx++, -(1+dayBack));
			stat.setInt(idx++, -dayBack);
			stat.setString(idx++, tag);
			
			try (ResultSet rs = stat.executeQuery()) {
				while (rs.next()) {
					String title = rs.getString("title");
					String url = rs.getString("url");
					String author = rs.getString("author");
					String permlink = rs.getString("permlink");
					int weight = rs.getInt("weight");
					String[] tags = rs.getString("tags").split(",");
					
					curations.add(new Curation(title, url, author, permlink, weight, tags));
				}
			}
		}
		
		return curations;
	}

	public static List<Comment> getComments(String username, int timelaps) throws SQLException {
		List<Comment> data = new ArrayList<>();
		
		String sql = "SELECT "
				+ "c.root_comment AS articleID, "
				+ "c.root_title AS articleTitle, "
				+ "c.ID AS commentID, "
				+ "c.author AS commentAuthor, "
				+ "c.author_reputation AS commentReputation, "
				+ "c.url AS commentURL, "
				+ "c.created AS commentCreated, "
				+ "c.body AS commentBody, "
				+ "c.pending_payout_value AS commentPayout, "
				+ "c.permlink AS commentPermlink "
				+ "FROM Comments AS c "
				// Include only response to user
				+ "WHERE c.parent_author = ? "
				// Exclude comment that received and answer
				+ "AND NOT EXISTS (SELECT ID FROM Comments AS c2 WHERE c2.parent_permlink = c.permlink AND c2.author = ?) "
				// Exclude Comments that have been voted
				+ "AND NOT EXISTS (SELECT ID FROM TxVotes AS v WHERE c.permlink = v.permlink AND voter = ? AND timestamp > DATEADD(DAY, ?, GETDATE())) "
				// In the last 7 days
				+ "AND c.created > DATEADD(DAY, ?, GETDATE()) "
				+ "ORDER BY c.created DESC;";
		
		try (PreparedStatement stat = getDb().prepareStatement(sql)) {
			int idx = 1;
			stat.setString(idx++, username);
			stat.setString(idx++, username);
			stat.setString(idx++, username);
			stat.setInt(idx++, -timelaps);
			stat.setInt(idx++, -timelaps);
			
			try (ResultSet rs = stat.executeQuery()) {
				while (rs.next()) {
					
					Date commentCreated = rs.getTimestamp("commentCreated");
					String articleTitle = rs.getString("articleTitle");
					
					long articleID = rs.getLong("articleID");
					long commentID = rs.getLong("commentID");
					String commentAuthor = rs.getString("commentAuthor");
					double commentReputation = UnitConverter.getSimpleReputation(rs.getLong("commentReputation"));
					String commentURL = rs.getString("commentURL");
					String commentBody = rs.getString("commentBody");
					double commentPayout = rs.getDouble("commentPayout");
					String commentPermlink = rs.getString("commentPermlink");
					
					data.add(new Comment(commentID, commentAuthor, commentReputation, commentBody, commentURL, commentPermlink, commentCreated, commentPayout, articleID, articleTitle));
				}
			}
		}
		
		return data;
	}
	
}
