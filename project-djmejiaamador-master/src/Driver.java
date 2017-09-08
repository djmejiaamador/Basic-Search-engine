import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Driver {
	private static final Logger log = LogManager.getLogger(Driver.class);

	/**
	 * Parsers command-line parameters.
	 *
	 * @param args
	 *            command-line parameters
	 */
	public static void main(String[] args) {

		WorkQueue queue = null;
		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index;
		IndexBuilderInterface builder;
		QueryParserInterface parser;
		WebCrawler crawler = null;

		if (map.hasFlag("-threads") || map.hasFlag("-url")) {
			try {
				queue = new WorkQueue(map.threadNum("-threads", 5));
			} catch (NumberFormatException e) {
				log.debug("invalid number number of threads");
			}
			ThreadSafeInvertedIndex threadSafe = new ThreadSafeInvertedIndex();
			index = threadSafe;
			parser = new ThreadSafeQueryParser(threadSafe, queue);
			builder = new MultiThreadedIndexBuilder(queue, threadSafe);

		} else {
			index = new InvertedIndex();
			parser = new QueryParser(index);
			builder = new IndexBuilder(index);
		}

		// log.debug("numebr of threads:{}", queue.size());

		if (map.hasFlag("-url")) {
			crawler = new WebCrawler(queue, (ThreadSafeInvertedIndex) index);
			try {
				crawler.crawl(new URL(map.get("-url")), map.getInteger("-limit", 50));
			} catch (MalformedURLException e) {
				System.out.println(crawler);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (map.hasFlag("-path")) {
			try {
				ArrayList<Path> paths = HTMLTraverser.traverse(map.getPath("-path"));
				builder.buildIndex(paths);
			} catch (IOException | NumberFormatException | NullPointerException e) {
				System.out.println("Unable to build index from the path: " + map.getPath("-path"));
			}
		}

		if (map.hasFlag("-index")) {

			try {
				index.outputFile(map.getPath("-index", "index.json"));
			} catch (IOException | NumberFormatException e) {
				log.debug("Threaded: Could not produce outputfile given path: " + map.getPath("-index"));
				System.out.println("Coud not produce outputfile given path: " + map.getPath("-index"));
			}
		}

		if (map.hasFlag("-query")) {
			try {
				parser.parse(map.getPath("-query"), map.containsKey("-exact"));
			} catch (Exception e) {
				System.out.println("Unable to build index from the path: " + map.getPath("-path"));
			}
		}

		if (map.containsKey("-results")) {
			try {
				log.debug("Multithreading query writing file ");
				parser.writeQueryFile(map.getPath("-results", "results.json"));
				log.debug("Multithreading Done writing file ");
			} catch (Exception e) {
				System.out.println("Unable to build index from the path: " + map.getPath("-path"));
			}
		}

		if (map.containsKey("-url") && map.containsKey("-port")) {
			try {
				crawler.crawl(new URL(map.get("-url")), map.getInteger("-limit", 50));
				Server server = new Server(map.getInteger("-port", 8080));

				ServletHandler handler = new ServletHandler();
				handler.addServletWithMapping(new ServletHolder(new MessageServlet((ThreadSafeInvertedIndex) index)),
						"/");
				handler.addServletWithMapping(CookieConfigServlet.class, "/config");
				server.setHandler(handler);
				server.start();
				server.join();
			} catch (MalformedURLException e) {
				System.out.println(crawler);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (queue != null) {
			queue.shutdown();
		}

	}
}