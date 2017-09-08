import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public interface InvertedIndexInterface {

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word
	 *            word to clean and add to index
	 * @param position
	 *            position word was found
	 */
	public void add(String word, String path, int position);

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
	public void addAll(String[] words, String file, int start);

	/**
	 * Adds an invertedIndex to current data structure
	 * 
	 * @param other
	 *            other inverted index
	 */
	public void addAll(InvertedIndex other);

	/**
	 * writes the jsonFile
	 *
	 * @param outputLocation
	 *            path of file to be written.
	 * @throws IOException
	 */
	public void outputFile(Path outputLocation) throws IOException;

	/**
	 * prints inverted index as string
	 */
	public String toString();

	/**
	 * checks to see if inverted index has a word
	 *
	 * @param key:
	 *            word to check for
	 * @return if word exist as key in Inverted Index
	 */
	public boolean contains(String key);

	/**
	 * returns number of words in Inverted index
	 *
	 * @return # of keys in Inverted Index
	 */
	public int numWords();

	/**
	 * get number of paths for given word
	 *
	 * @param word:
	 *            word to look at
	 * @return in how many files word was found
	 */
	public int numPath(String word);

	/**
	 * givens list of queries words, searches for those exact words in the
	 * inverted index and makes them into Instances of SearchResult then adds
	 * them into a sorted array list of them.
	 *
	 * @param queries
	 *            list of words to look for in inverted index
	 * @return
	 */
	public ArrayList<SearchResult> exactSearch(String[] words);

	/**
	 * givens list of queries words, does a partial search for those words in
	 * the inverted index and makes them into Instances of SearchResult then
	 * adds them into a sorted array list of them.
	 *
	 * @param queries
	 *            list of words to look for in inverted index
	 * @return
	 */
	public ArrayList<SearchResult> partialSearch(String[] words);

}
