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
import fr.comprendresteem.api.model.response.GetCommentsResponse;
import fr.comprendresteem.model.Comment;

@SuppressWarnings("serial")
@WebServlet({"/getComments"})
public class GetComments extends HttpServlet {
	
	private static final String USERNAME = "username";
	private static final String TIMELAPS = "days";
	
    public GetComments() { }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int timelaps = 7;
		
		@SuppressWarnings("unchecked")
		Map<String, String[]> params = request.getParameterMap();
		
		String username;
		
		if (!params.containsKey(USERNAME)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter: " + USERNAME);
			return;
		} else {
			List<String> usernames = Arrays.asList(params.get(USERNAME));
			username = usernames.get(0);
		}
		
		if (params.containsKey(TIMELAPS)) {
			timelaps = Integer.valueOf(Arrays.asList(params.get(TIMELAPS)).get(0));
		}
		
		try {
			List<Comment> comments = Requester.getComments(username, timelaps);
			GetCommentsResponse data = new GetCommentsResponse(username, comments);
			
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

}
