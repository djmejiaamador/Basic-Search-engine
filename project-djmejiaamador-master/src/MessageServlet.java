import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// http://commons.apache.org/proper/commons-lang/download_lang.cgi

@SuppressWarnings("serial")
public class MessageServlet extends HttpServlet {

	public static final String VISIT_DATE = "Visited";
	public static final String VISIT_COUNT = "Count";

	private static final String TITLE = "Search Engine";
	private static Logger log = Log.getRootLogger();
	private final ThreadSafeInvertedIndex index;

	private ConcurrentLinkedQueue<String> messages;
	private static String query;
	private static ArrayList<SearchResult> results;
	private static ArrayList<String> searchedQueries;

	public MessageServlet(ThreadSafeInvertedIndex index) {
		super();
		this.index = index;
		messages = new ConcurrentLinkedQueue<>();
		results = new ArrayList<>();
		searchedQueries = new ArrayList<>();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body>%n");

		out.printf("<h1>Search results</h1>%n%n");
		out.printf("<h2> query: %s</h2>%n%n", query);

		// Keep in mind multiple threads may access at once
		for (SearchResult r : results) {
			out.printf("<p> <a href=\"%s\"> %s </a> </p>%n%n", r.getPath(), r.getPath());
		}
		out.printf("<h2>Things searched for:</h2>%n%n");
		for (String s : searchedQueries) {
			out.printf("<p> %s </p>%n%n", s);
		}

		printForm(request, response);

		out.printf("<p>This request was handled by thread %s.</p>%n", Thread.currentThread().getName());
		// times stamp

		Map<String, Cookie> cookies = getCookieMap(request);
		Cookie visitDate = cookies.get(VISIT_DATE);
		Cookie visitCount = cookies.get(VISIT_COUNT);

		out.printf("<p>");

		// Update visit count as necessary and output information.
		if ((visitDate == null) || (visitCount == null)) {
			visitCount = new Cookie(VISIT_COUNT, "0");
			visitDate = new Cookie(VISIT_DATE, "");

			out.printf("You have never been to this webpage before! ");
			out.printf("Thank you for visiting.");
		} else {
			int count = Integer.parseInt(visitCount.getValue());
			visitCount.setValue(Integer.toString(count + 1));

			String decoded = URLDecoder.decode(visitDate.getValue(), StandardCharsets.UTF_8.name());
			log.info("Encoded: " + visitDate.getValue() + ", Decoded: " + decoded);

			out.printf("You have visited this website %s times. ", visitCount.getValue());
			out.printf("Your last visit was on %s.", decoded);
		}

		out.printf("</p>%n");

		// Checks if the browser indicates visits should not be tracked.
		// This is not a standard header!
		// Try this in Safari private browsing mode.
		if (request.getIntHeader("DNT") != 1) {
			String encoded = URLEncoder.encode(getLongDate(), StandardCharsets.UTF_8.name());
			visitDate.setValue(encoded);
			response.addCookie(visitDate);
			response.addCookie(visitCount);
		} else {
			clearCookies(request, response);
			out.printf("<p>Your visits will not be tracked.</p>");
		}
		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");
		query = request.getParameter("query");
		String delete = request.getParameter("delete");

		query = query == null ? "" : query;

		if (!query.equals("")) {
			searchedQueries.add(query);
			String[] words = WordParser.parseWords(query);

			Arrays.sort(words);
			results = index.partialSearch(words);

		} else {
			results.clear();
		}
		
		if(delete !=null){
			searchedQueries.clear();
		}

		// Avoid XSS attacks using Apache Commons StringUtils
		// Comment out if you don't have this library installed
		query = StringEscapeUtils.escapeHtml4(query);

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Search:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"query\" maxlength=\"50\" size=\"20\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.printf("<p><input type=\"submit\" value=\"submit\"></p>\n%n");
		out.printf("</form>\n%n");
		
		out.printf("<form method=\"post\" action=\"%s\">%n",request.getServletPath());
		out.printf("<p><input type=\"submit\" name=\"delete\" value=\"Delete history\"></p>\n%n");
		out.printf("</form>\n%n");
	}

	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

	/**
	 * Gets the cookies form the HTTP request, and maps the cookie name to the
	 * cookie object.
	 *
	 * @param request
	 *            - HTTP request from web server
	 * @return map from cookie key to cookie value
	 */
	public Map<String, Cookie> getCookieMap(HttpServletRequest request) {
		HashMap<String, Cookie> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie);
			}
		}

		return map;
	}

	/**
	 * Clears all of the cookies included in the HTTP request.
	 *
	 * @param request
	 *            - HTTP request
	 * @param response
	 *            - HTTP response
	 */
	public void clearCookies(HttpServletRequest request, HttpServletResponse response) {

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

	/**
	 * Returns the current date and time in a long format.
	 *
	 * @return current date and time
	 * @see #getShortDate()
	 */
	public static String getLongDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}
