package fr.comprendresteem.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.comprendresteem.Requester;
import fr.comprendresteem.api.model.response.GetVotesResponse;
import fr.comprendresteem.model.Vote;

@SuppressWarnings("serial")
@WebServlet({"/getIncomingVotes"})
public class GetIncomingVotes extends HttpServlet {
	
	private static final String USERNAME = "username";
	private static final String OFFSET = "offset";
	private static final String LIMIT = "limit";
	
	private static final Long PAGE_MAX_SIZE = 9999L;

    public GetIncomingVotes() { }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String[]> params = request.getParameterMap();
		
		long offset = 0;
		long limit = 30;
		String username;
		
		if (!params.containsKey(USERNAME)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter: " + USERNAME);
			return;
		} else {
			List<String> usernames = Arrays.asList(params.get(USERNAME));
			username = usernames.get(0);
		}
		
		if (params.containsKey(OFFSET)) {
			String value = Arrays.asList(params.get(OFFSET)).get(0);
			if (isNumber(value)) {
				offset = Long.valueOf(value);
			}
		}
		
		if (params.containsKey(LIMIT)) {
			String value = Arrays.asList(params.get(LIMIT)).get(0);
			if (isNumber(value)) {
				Long userLimit = Long.valueOf(value);
				limit = (userLimit < PAGE_MAX_SIZE) ? userLimit: PAGE_MAX_SIZE;
			}
		}
		
		try {
			List<Vote> articles = Requester.getIncomingVotes(username, offset, limit);
			GetVotesResponse data = new GetVotesResponse(username, articles);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			try (ServletOutputStream os = response.getOutputStream()) {
				os.write(GsonConfig.GSON().toJson(data).getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error: " + e.getMessage());
			return;
		}
	}

	private boolean isNumber(String value) {
		return value.matches("(\\d)*");
	}
}
