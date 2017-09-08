import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONWriter {

	/**
	 * Returns a String with the specified number of tab characters.
	 *
	 * @param times
	 *            number of tab characters to include
	 * @return tab characters repeated the specified number of times
	 */
	public static String indent(int times) {
		char[] tabs = new char[times];
		Arrays.fill(tabs, '\t');
		return String.valueOf(tabs);
	}

	/**
	 * Returns a quoted version of the provided text.
	 *
	 * @param text
	 *            text to surround in quotes
	 * @return text surrounded by quotes
	 */
	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	/**
	 * Writes the set of elements as a JSON array at the specified indent level.
	 *
	 * @param writer
	 *            writer to use for output
	 * @param elements
	 *            elements to write as JSON array
	 * @param level
	 *            number of times to indent the array itself
	 * @throws IOException
	 */
	private static void asArray(Writer writer, TreeSet<Integer> elements, int level) throws IOException {

		Iterator<Integer> iterator = elements.iterator();
		writer.write("[");

		if (iterator.hasNext()) {

			writer.write(System.lineSeparator());
			writer.write(indent(level + 1));
			writer.write(iterator.next().toString());
		}

		while (iterator.hasNext()) {

			writer.write(",");
			writer.write(System.lineSeparator());
			writer.write(indent(level + 1));
			writer.write(iterator.next().toString());
		}

		writer.write(System.lineSeparator());
		writer.write(indent(level));
		writer.write("]");
	}

	/**
	 * Writes the set of elements as a JSON array to the path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON array
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			int level = 0;
			asArray(writer, elements, level);
		}

	}

	/**
	 * Writes the map of elements as a JSON object to the path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON object
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asObject(Writer writer, TreeMap<String, TreeSet<Integer>> elements, int level)
			throws IOException {
		TreeSet<String> keys = new TreeSet<>(elements.keySet());

		writer.write("{");
		writer.write(System.lineSeparator());
		if (!keys.isEmpty()) {

			for (String k : keys.headSet(keys.last())) {

				writer.write(indent(level + 1));
				writer.write("\"" + k + "\": ");
				asArray(writer, elements.get(k), level + 1);
				writer.write(",");
				writer.write(System.lineSeparator());
			}

			writer.write(indent(level));
			writer.write("\"" + keys.last() + "\": ");
			asArray(writer, elements.get(keys.last()), level + 1);
		}
		writer.write(indent(level));
		writer.write(System.lineSeparator());
		writer.write("}");
	}

	/**
	 * Writes the set of elements as a JSON object with a nested array to the
	 * path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON object with a nested array
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

			int level = 0;
			TreeSet<String> keys = new TreeSet<>(elements.keySet());

			writer.write("{");
			writer.write(System.lineSeparator());
			if (!keys.isEmpty()) {
				for (String k : keys.headSet(keys.last())) {

					writer.write(indent(level + 1));
					writer.write("\"" + k + "\": ");
					asObject(writer, elements.get(k), level + 1);
					writer.write(",");
					writer.write(System.lineSeparator());
				}
				writer.write(indent(level));
				writer.write("\"" + keys.last() + "\": ");
				asObject(writer, elements.get(keys.last()), level + 1);
			}
			writer.write(indent(level));
			writer.write(System.lineSeparator());
			writer.write("}");
			writer.flush();
		}
	}

	/**
	 * Write the query map to a file
	 *
	 * @param queryMap
	 *            data structure to be written to file
	 * @param file
	 *            destination of output
	 * @throws IOException
	 */
	public static void queryJSON(TreeMap<String, ArrayList<SearchResult>> queryMap, Path file) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			int level = 0;
			writer.write("[");
			writer.write(System.lineSeparator());
			if (queryMap != null) {
				writeQuery(queryMap, writer, level + 1);
			}
			writer.write("]");
			writer.flush();
		}
	}

	/**
	 * Write the query word of the map
	 *
	 * @param queryMap
	 *            map containing data
	 * @param writer
	 *            the writer
	 * @param level
	 *            indentation level
	 * @throws IOException
	 */
	private static void writeQuery(TreeMap<String, ArrayList<SearchResult>> queryMap, BufferedWriter writer, int level)
			throws IOException {
		TreeSet<String> keys = new TreeSet<>(queryMap.keySet());

		if (!keys.isEmpty()) {
			for (String k : keys.headSet(keys.last())) {
				writer.write(indent(level));
				writer.write("{");
				writer.write(System.lineSeparator());
				writer.write(indent(level + 1));
				writer.write("\"queries\": " + "\"" + k + "\",");
				writeResult(queryMap.get(k), writer, level + 1);
				writer.write(indent(level));
				writer.write("}");
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			writer.write(indent(level));
			writer.write("{");
			writer.write(System.lineSeparator());
			writer.write(indent(level + 1));
			writer.write("\"queries\": " + "\"" + keys.last() + "\",");
			writeResult(queryMap.get(keys.last()), writer, level + 1);
		}
		writer.write(indent(level));
		writer.write("}");
		writer.write(System.lineSeparator());

	}

	/**
	 * write the result of the for a given querie word
	 *
	 * @param found
	 *            Array of found result for each a given key
	 * @param writer
	 *            the file writer
	 * @param level
	 * @throws IOException
	 */
	private static void writeResult(ArrayList<SearchResult> found, BufferedWriter writer, int level)
			throws IOException {
		Iterator<SearchResult> iterator = found.iterator();

		writer.write(System.lineSeparator());
		writer.write(indent(level));
		writer.write("\"results\"" + ": [");

		if (iterator.hasNext()) {
			writer.write(System.lineSeparator());
			writer.write(indent(level + 1));
			writer.write("{");
			writer.write(System.lineSeparator());
			writeResultValues(iterator.next(), writer, level);
			writer.write(indent(level + 1));
			writer.write("}");

		}
		while (iterator.hasNext()) {

			writer.write(",");
			writer.write(System.lineSeparator());
			writer.write(indent(level + 1));
			writer.write("{");
			writer.write(System.lineSeparator());
			writeResultValues(iterator.next(), writer, level);
			writer.write(indent(level + 1));
			writer.write("}");
		}

		writer.write(System.lineSeparator());
		writer.write(indent(level));
		writer.write("]");
		writer.write(System.lineSeparator());

	}

	/**
	 * write the information/values of a given search result
	 *
	 * @param found
	 * @param writer
	 * @param level
	 * @throws IOException
	 */
	private static void writeResultValues(SearchResult found, BufferedWriter writer, int level) throws IOException {

		writer.write(indent(level + 2));
		writer.write("\"where\": \"" + found.getPath() + "\",");
		writer.write(System.lineSeparator());
		writer.write(indent(level + 2));
		writer.write("\"count\": " + found.getFrequency() + ",");
		writer.write(System.lineSeparator());
		writer.write(indent(level + 2));
		writer.write("\"index\": " + found.getPosition());
		writer.write(System.lineSeparator());
	}
}