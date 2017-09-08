import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;


public class QueryParser implements QueryParserInterface {

	private final TreeMap<String, ArrayList<SearchResult>> resultMap;
	private final InvertedIndexInterface index;


	public QueryParser(InvertedIndexInterface index) {
		this.index = index;
		resultMap = new TreeMap<>();

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
	@Override
	public void parse(Path file, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			String line = null;
			while ((line = reader.readLine()) != null) {

				String[] words = WordParser.parseWords(line);

				if (words.length == 0) {
					continue;
				}

				Arrays.sort(words);

				if (exact) {
					resultMap.put(String.join(" ", words), index.exactSearch(words));
				} else {
					resultMap.put(String.join(" ", words), index.partialSearch(words));
				}
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
		JSONWriter.queryJSON(resultMap, file);
	}
}
