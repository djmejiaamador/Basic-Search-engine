public class SearchResult implements Comparable<SearchResult> {

	/** # of occurrences of result */
	private int frequency;

	/** initial position of result */
	private int position;

	/** file location of search result */
	private final String path;

	/**
	 * A valid search result.
	 *
	 * @param frequency
	 *            how many times search result was found in file.
	 * @param position
	 *            index position of search result.
	 * @param path
	 *            Which file search result was found.
	 */
	public SearchResult(int frequency, int position, String path) {
		this.frequency = frequency;
		this.position = position;
		this.path = path;

	}

	/**
	 * compares by frequency. If frequencies are the same, compares by position.
	 * If positions are the same, compares by path
	 */
	@Override
	public int compareTo(SearchResult o) {
		if (Integer.compare(frequency, o.frequency) == 0) {
			if (Integer.compare(position, o.position) == 0) {
				return path.compareToIgnoreCase(o.path);
			}
			return Integer.compare(position, o.position);
		}
		return Integer.compare(o.frequency, frequency);
	}

	/**
	 * returns frequency of search result
	 *
	 * @return # of occurrences of query
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * updates frequency
	 *
	 * @param frequency
	 *            Value to add to current frequency
	 */
	public void addFrequency(int frequency) {
		this.frequency += frequency;
	}

	/**
	 * return position of query in file
	 *
	 * @return index of query
	 */
	public int getPosition() {
		return position;

	}

	/**
	 * updates position if lower one available
	 *
	 * @param update
	 *            the lower value
	 */
	public void updatePosition(int update) {
		if (position > update) {
			position = update;
		}
	}

	/**
	 * get path of query
	 *
	 * @return String of path in which query was found
	 */
	public String getPath() {
		return path;
	}

	/**
	 * What to print
	 */
	@Override
	public String toString() {
		return String.format("Frequeny: %d Position: %d Path: %s\n", frequency, position, path);
	}

}