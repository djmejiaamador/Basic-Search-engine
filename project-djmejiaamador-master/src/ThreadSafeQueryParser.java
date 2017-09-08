import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadSafeQueryParser implements QueryParserInterface {

	private final TreeMap<String, ArrayList<SearchResult>> resultMap;
	private final ThreadSafeInvertedIndex index;

	private final WorkQueue queue;

	private static final Logger log = LogManager.getLogger();

	public ThreadSafeQueryParser(ThreadSafeInvertedIndex index, WorkQueue queue) {
		this.index = index;
		resultMap = new TreeMap<>();
		this.queue = queue;
	}

	@Override
	public void parse(Path file, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				queue.execute(new ParserMinion(line, exact));
			}
		}
		queue.finish();
	}

	/**
	 * Read through file and parsed through query. Searches and adds found
	 * results one a time.
	 *
	 *
	 * @param directory
	 *            to traverse
	 * @throws IOException
	 */
	private void parseHelper(String line, boolean exact) throws IOException {

		String[] words = WordParser.parseWords(line);

		if (words.length != 0) {

			Arrays.sort(words);
			String key = String.join(" ", words);
			ArrayList<SearchResult> list = (exact) ? index.exactSearch(words) : index.partialSearch(words);
			synchronized (resultMap) {
				resultMap.put(key, list);
			}
		}

	}

	/**
	 * writes the Query jsonFile
	 *
	 * @param outputLocation
	 *            path of file to be written.
	 */
	@Override
	public void writeQueryFile(Path file) throws IOException {
		synchronized (resultMap) {
			JSONWriter.queryJSON(resultMap, file);
		}
	}

	private class ParserMinion implements Runnable {

		private String line;
		private boolean exact;

		ParserMinion(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			try {
				log.debug("working on line:" + line);
				parseHelper(line, exact);
				log.debug("done with line:" + line);

			} catch (IOException e) {
				log.debug("run for parse method ");
			}
		}
	}

}