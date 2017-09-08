import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {

	public static final Pattern SPLIT_REGEX = Pattern.compile("<a href=.*a>");

	// https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a
	// https://docs.oracle.com/javase/tutorial/networking/urls/creatingUrls.html
	// https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_URL

	/**
	 * Removes the fragment component of a URL (if present), and properly
	 * encodes the query string (if necessary).
	 *
	 * @param url
	 *            url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Fetches the HTML (without any HTTP headers) for the provided URL. Will
	 * return null if the link does not point to a HTML page.
	 *
	 * @param url
	 *            url to fetch HTML from
	 * @return HTML as a String or null if the link was not HTML
	 */
	public static String fetchHTML(URL url) {
		try {
			return HTTPFetcher.fetchHTML(url.toString());

		} catch (IOException e) {
			System.out.println("someting went wrong");
		}
		return null;

	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of
	 * the anchor tags in the provided HTML. The links will be converted to
	 * absolute using the base URL and cleaned (removing fragments and encoding
	 * special characters as necessary).
	 *
	 * @param base
	 *            base url used to convert relative links to absolute3
	 * @param html
	 *            raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 */
	public static ArrayList<URL> listLinks(URL base, String html) {
		ArrayList<URL> links = new ArrayList<URL>();
		// URL base = new URL("http://www.cs.usfca.edu/~sjengle/cs212/");
		Pattern pattern = Pattern.compile("(?mis)(?:<\\s*a[^>]*href\\s*=\\s*\")([^\"]*)");

		Matcher matcher = pattern.matcher(html);

		while (matcher.find()) {

			URL absolute;
			try {
				absolute = clean(new URL(base, matcher.group(1)));
				if (absolute.getProtocol().startsWith("http")) {
					links.add(absolute);
				}
			} catch (MalformedURLException e) {
				System.out.println("Not a link?");
			}

		}

		return links;
	}
}
