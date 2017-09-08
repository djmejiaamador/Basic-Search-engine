import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public interface IndexBuilderInterface {
	
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
	public void buildIndex(ArrayList<Path> paths) throws IOException;

}