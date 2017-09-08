import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * traverse a directory of HTML files and its sub-director
 *
 */
public class HTMLTraverser {

	/**
	 * Safely starts the recursive traversal with the proper padding. Users of
	 * this class can access this method, so some validation is required.
	 *
	 * @param directory
	 *            to traverse
	 * @throws IOException
	 */
	public static ArrayList<Path> traverse(Path path) throws IOException {

		ArrayList<Path> paths = new ArrayList<>();
		traverse(path, paths);
		return paths;
	}

	/**
	 * runs through each file and adds it to an array of path. If subdirectory
	 * is found it will make a recursive call and travel the subdirectory
	 *
	 * @param directory
	 *            to traverse
	 * @param paths
	 *            array list of all files found
	 * @throws IOException
	 */
	private static void traverse(Path path, ArrayList<Path> paths) throws IOException {

		if (Files.isDirectory(path)) {

			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {

				for (Path file : listing) {
					traverse(file, paths);
				}
			}
		} else {

			if (path.toString().toLowerCase().endsWith(".html") || path.toString().toLowerCase().endsWith(".htm")) {
				paths.add(path);
			}
		}
	}

	/**
	 * Prints an array list of Paths.
	 *
	 * @param htmlFiles
	 *            array of HTML files found
	 */
	public void printHtmlist(ArrayList<Path> htmlFiles) {

		for (Path p : htmlFiles) {
			System.out.println("file: " + p.toString());
		}
	}

}
