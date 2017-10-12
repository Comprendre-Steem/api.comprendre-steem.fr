package fr.comprendresteem.api.model.response;

public class GetStatusResponse {
	
	public final String username;
	public final StatusVotes votes;
	
	public class StatusVotes {
		public final long received;
		
		public StatusVotes(long received) {
			this.received = received;
		}
	}

	public GetStatusResponse(String username, long totalIncomingVotes) {
		this.username = username;
		this.votes = new StatusVotes(totalIncomingVotes);
	}

}
