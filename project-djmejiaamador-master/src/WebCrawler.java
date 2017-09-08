import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebCrawler {

	private static final Logger log = LogManager.getLogger();
	private final WorkQueue queue;
	private final ThreadSafeInvertedIndex index;
	private HashSet<URL> urls;
	private int totalURLS;

	public WebCrawler(WorkQueue queue, ThreadSafeInvertedIndex index) {
		this.queue = queue;
		this.index = index;
		urls = new HashSet<URL>();
		totalURLS = 0;
	}

	public void crawl(URL seed, int limit) {
		totalURLS += limit;
		urls.add(seed);
		queue.execute(new CrawlerMinion(seed));
		queue.finish();
	}

	public String toStrong() {
		return "printing web crawker";
	}

	private class CrawlerMinion implements Runnable {

		private URL url;

		CrawlerMinion(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			try {

				int counter = 1;
				String html = HTTPFetcher.fetchHTML(url.toString());
				if (html == null) {
					return;
				}
				log.debug(url.toString());
				String whole = HTMLCleaner.stripHTML(html);
				String[] textArray = WordParser.parseWords(whole);
				index.addAll(textArray, url.toString(), counter);

				ArrayList<URL> links = LinkParser.listLinks(url, html);

				synchronized (urls) {
					for (URL link : links) {
						if (urls.size() >= totalURLS) {
							break;
						}
						if (!urls.contains(link)) {

							urls.add(link);
							queue.execute(new CrawlerMinion(link));
						}
					}
				}
				log.debug("reached crawler:");
			} catch (UnknownHostException e) {
				log.debug("Invalid host");
				e.printStackTrace();
			} catch (MalformedURLException e) {
				log.debug("Bad URL");
				e.printStackTrace();
			} catch (IOException e) {
				log.debug("something went wrong");
			}

		}
	}

}
