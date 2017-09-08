import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class IndexBuilder implements IndexBuilderInterface {

	private final InvertedIndex index;

	public IndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * Reads a file, strips all HTML and parses words then adds words and paths
	 * and # of occurrences to inverted index
	 *
	 * @param paths:
	 *            Array list of files to read.
	 * @param index
	 *            The Inverted Index data structure to which words will be
	 *            added.
	 * @throws IOException
	 */
	@Override
	public void buildIndex(ArrayList<Path> paths) throws IOException {
		for (Path file : paths) {
			buildIndex(file, index);
		}
	}

	/**
	 * Reads a file, strips all HTML and parses words then adds words and paths
	 * and # of occurrences to inverted index
	 *
	 * @param paths:
	 *            files to read.
	 * @param index
	 *            The Inverted Index data structure to which words will be
	 *            added.
	 * @throws IOException
	 */
	public static void buildIndex(Path path, InvertedIndex index) throws IOException {
		int counter = 1;
		byte[] buffer;
		buffer = Files.readAllBytes(path);
		String whole = new String(buffer, StandardCharsets.UTF_8);
		whole = HTMLCleaner.stripHTML(whole);
		String[] textArray = WordParser.parseWords(whole);
		index.addAll(textArray, path.toString(), counter);
	}
}