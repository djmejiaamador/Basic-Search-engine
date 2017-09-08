import java.io.IOException;
import java.nio.file.Path;

public interface  QueryParserInterface {
	
	/**
	 * writes the Query jsonFile
	 *
	 * @param outputLocation
	 *            path of file to be written.
	 */
	public void writeQueryFile(Path file) throws IOException;
	
	
	/**
	 * Read through file and parsed through query. Searches and adds found
	 * results one a time.
	 *
	 *
	 * @param directory
	 *            to traverse
	 * @throws IOException
	 */
	public void parse(Path file, boolean exact) throws IOException; 
}