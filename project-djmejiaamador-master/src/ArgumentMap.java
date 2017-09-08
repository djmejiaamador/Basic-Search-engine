import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses command-line arguments into flag/value pairs, and stores those pairs
 * in a map for easy access.
 */
public class ArgumentMap {

	private final Map<String, String> map;

	/**
	 * Initializes the argument map.
	 */
	public ArgumentMap() {

		map = new HashMap<>();
	}

	/**
	 * Initializes the argument map and parses the specified arguments into
	 * key/value pairs.
	 *
	 * @param args
	 *            command line arguments
	 *
	 * @see #parse(String[])
	 */
	public ArgumentMap(String[] args) {

		this();
		parse(args);
	}

	/**
	 * Parses the specified arguments into key/value pairs and adds them to the
	 * argument map.
	 *
	 * @param args
	 *            command line arguments
	 */
	public void parse(String[] args) {

		String key = null;
		String value = null;
		for (int i = 0; i < args.length; i++) {
			if (isFlag(args[i])) {
				key = args[i];
				value = null;
				if (i + 1 != args.length) {
					if (isValue(args[i + 1])) {
						value = args[i + 1];
					}
				}
				map.put(key, value);
			}

		}

	}

	/**
	 * This should check to see if the argument is a valid flag or not valid
	 * flags are strings that start with "-"
	 *
	 * @param arg
	 * @return true or false depending is arg is a a flag or not
	 */
	public static boolean isFlag(String arg) {

		if (arg == null) {
			return false;
		}

		arg = arg.trim();
		return arg.startsWith("-") && (arg.length() > 1);
	}

	/**
	 * This is to check is arg is valid value.
	 *
	 * @param arg
	 *            string value in question
	 * @return true or false depending if the input string arg is a value or not
	 */
	public static boolean isValue(String arg) {

		if (arg == null) {
			return false;
		}
		arg = arg.trim();
		return !arg.startsWith("-") && !arg.isEmpty();
	}

	/**
	 * Returns the number of unique flags stored in the argument map.
	 *
	 * @return number of flags
	 */
	public int numFlags() {

		return map.size();
	}

	/**
	 * Determines whether the specified flag is stored in the argument map.
	 *
	 * @param flag
	 *            flag to test
	 *
	 * @return true if the flag is in the argument map
	 */
	public boolean hasFlag(String flag) {

		return map.containsKey(flag);
	}

	/**
	 * ; Determines whether the specified flag is stored in the argument map and
	 * has a non-null value stored with it.
	 *
	 * @param flag
	 *            flag to test
	 *
	 * @return true if the flag is in the argument map and has a non-null value
	 */
	public boolean hasValue(String flag) {

		return map.get(flag) != null;
	}

	/**
	 * Returns the value for the specified flag as a String object.
	 *
	 * @param flag
	 *            flag to get value for
	 *
	 * @return value as a String or null if flag or value was not found
	 */
	public String getString(String flag) {

		return map.get(flag);
	}

	/**
	 * Returns path representation of string value for a given key
	 *
	 * @param flag
	 *            key value of map
	 *
	 * @return The path for a certain key. null if nothing there.
	 */
	public Path getPath(String flag) {

		if (map.get(flag) == null) {
			return null;
		}

		return Paths.get(map.get(flag));
	}

	/**
	 * Returns path representation of string value for a given key If not value
	 * match for key then use default value
	 *
	 * @param flag
	 *            key value of map
	 * @param def
	 *            default value
	 * @return The path for a certain key. null if nothing there.
	 */
	public Path getPath(String flag, String def) {

		if (map.get(flag) == null) {
			return Paths.get(def);
		}

		return Paths.get(map.get(flag));
	}

	/**
	 * Returns the value for the specified flag as a String object. If the flag
	 * is missing or the flag does not have a value, returns the specified
	 * default value instead.
	 *
	 * @param flag
	 *            flag to get value for
	 * @param defaultValue
	 *            value to return if flag or value is missing
	 * @return value of flag as a String, or the default value if the flag or
	 *         value is missing
	 */
	public String getString(String flag, String defaultValue) {

		if (map.get(flag) == null) {
			return defaultValue;
		}

		return map.get(flag);
	}

	/**
	 * gets number of thread. Throws error if invalid input
	 * @param defaultValue
	 * @return
	 * @throws NumberFormatException
	 */
	public int threadNum(String flag, int defaultValue) throws NumberFormatException {
		if ((map.get(flag) == null) || (Integer.parseInt(map.get(flag)) < 1)) {
			return defaultValue;
		}
		return Integer.parseInt(map.get(flag));
	}

	/**
	 * Returns the value for the specified flag as an int value. If the flag is
	 * missing or the flag does not have a value, returns the specified default
	 * value instead.
	 *
	 * @param flag
	 *            flag to get value for
	 * @param defaultValue
	 *            value to return if the flag or value is missing
	 * @return value of flag as an int, or the default value if the flag or
	 *         value is missing
	 */
	public int getInteger(String flag, int defaultValue) {

		try {
			return Integer.parseInt(map.get(flag));
		} catch (NumberFormatException | NullPointerException e) {
			return defaultValue;
		}
	}

	@Override
	public String toString() {

		return map.toString();
	}

	/**
	 * checks to see if argument map is empty.
	 *
	 * @return true or false depending if map is empty or not.
	 */
	public boolean isEmpty() {

		return map.isEmpty();
	}

	/**
	 * checks to see whether argument map contains a key
	 *
	 * @param key
	 *            a key in the map
	 * @return true or false depending if key exist in map
	 */
	public boolean containsKey(String key) {

		return map.containsKey(key);
	}

	/**
	 * returns size of map
	 *
	 * @return int value of total key-value parings
	 */
	public int size() {

		return map.size();
	}

	/**
	 * adds a key-value paring to the map
	 *
	 * @param key:
	 *            String key value
	 * @param value
	 *            String value to be stored in given key
	 */
	public void put(String key, String value) {

		map.put(key, value);
	}

	/**
	 * gets string representation of path passed in as command line arg
	 *
	 * @param key
	 *            the flag to look for
	 * @return the String value of a given flag. null if countains no value
	 */
	public String get(String key) {

		if (map.get(key) != null) {
			return map.get(key);
		}

		return null;
	}

}
