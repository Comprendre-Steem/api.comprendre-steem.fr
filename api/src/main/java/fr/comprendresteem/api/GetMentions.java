package fr.comprendresteem.api;

import java.io.IOException;
import java.nio.charset.Charset;
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
import fr.comprendresteem.api.model.response.GetMentionsResponse;
import fr.comprendresteem.model.Mention;

@SuppressWarnings("serial")
@WebServlet({"/getMentions", "/mentions"})
public class GetMentions extends HttpServlet {
	
	private static final List<String> TRUE = Arrays.asList("Y", "true");
	
	private static final String USERNAME = "username";
	private static final String COMMENTS = "comments";
	private static final String OWN_COMMENTS = "own_comments";

    public GetMentions() { }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String[]> params = request.getParameterMap();
		
		boolean includeComments = false;
		boolean includeOwnComments = false;
		String username;
		
		if (!params.containsKey(USERNAME)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter: " + USERNAME);
			return;
		} else {
			List<String> usernames = Arrays.asList(params.get(USERNAME));
			username = usernames.get(0);
		}
		
		if (params.containsKey(COMMENTS)) {
			includeComments = TRUE.contains(Arrays.asList(params.get(COMMENTS)).get(0));
		}
		
		if (params.containsKey(OWN_COMMENTS)) {
			includeOwnComments = TRUE.contains(Arrays.asList(params.get(OWN_COMMENTS)).get(0));
		}
		
		try {
			List<Mention> articles = Requester.getMentions(username, includeComments, includeOwnComments);
			GetMentionsResponse data = new GetMentionsResponse(username, articles);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			try (ServletOutputStream os = response.getOutputStream()) {
				os.write(GsonConfig.GSON().toJson(data).getBytes(Charset.forName("UTF-8")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error: " + e.getMessage());
			return;
		}
	}
}
