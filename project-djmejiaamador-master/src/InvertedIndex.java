import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

// TODO Use the @Override annotation
// TODO Only Javadoc overridden methods if something importance is different
// TODO Otherwise only Javadoc new methods

public class InvertedIndex implements InvertedIndexInterface {
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	public InvertedIndex() {
		index = new TreeMap<>();
	}

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word
	 *            word to clean and add to index
	 * @param position
	 *            position word was found
	 */
	private void addHelper(String word, String path, int position) {

		if (index.containsKey(word) && index.get(word).containsKey(path)) {
			index.get(word).get(path).add(position);
		} else if (index.containsKey(word) && !(index.get(word).containsKey(path))) {
			index.get(word).put(path, new TreeSet<Integer>());
		} else {
			index.put(word, new TreeMap<String, TreeSet<Integer>>());
			index.get(word).put(path, new TreeSet<Integer>());
		}

		index.get(word).get(path).add(position);
	}

	
	@Override
	public void add(String word, String file, int start) {
		addHelper(word, file, start++);
	}

	
	@Override
	public void addAll(String[] words, String file, int start) {
		for (String word : words) {
			addHelper(word, file, start++);
		}
	}


	@Override
	public void addAll(InvertedIndex other) { 
		for (String word : other.index.keySet()) { 
			if (this.index.containsKey(word) == false) {
				this.index.put(word, other.index.get(word)); 
			} else { 
				for(String path : other.index.get(word).keySet()){
					if(!index.get(word).containsKey(path)){
						index.get(word).put(path, other.index.get(word).get(path));
					}else{
						index.get(word).get(path).addAll(other.index.get(word).get(path));
					}
				}
			}
		}
	}
	
	
	
	@Override
	public void outputFile(Path outputLocation) throws IOException {

		JSONWriter.asNestedObject(index, outputLocation);
	}

	
	@Override
	public String toString() {

		StringBuilder whole = new StringBuilder();
		for (String w : index.keySet()) {

			whole.append("Key: " + w);
			for (String p : index.get(w).keySet()) {

				whole.append("\tpath : " + p.toString());
				whole.append("\t\t" + index.get(w).get(p));
			}
		}
		return whole.toString();
	}

	
	@Override
	public boolean contains(String key) {

		return index.containsKey(key);
	}

	
	@Override
	public int numWords() {

		return index.size();
	}

	
	@Override
	public int numPath(String word) {

		if (index.get(word) == null) {
			return 0;
		}

		return index.get(word).size();
	}

	
	@Override
	public ArrayList<SearchResult> exactSearch(String[] words) {
		ArrayList<SearchResult> results = new ArrayList<>();
		Map<String, SearchResult> map = new TreeMap<>();
		for (String word : words) {

			if (index.containsKey(word)) {
				searchHelper(word, map, results);
			}
		}

		Collections.sort(results);
		return results;
	}


	@Override
	public ArrayList<SearchResult> partialSearch(String[] words) {

		ArrayList<SearchResult> results = new ArrayList<>();
		Map<String, SearchResult> map = new TreeMap<>();
		for (String word : words) {

			for (String key : index.tailMap(word).keySet()) {

				if (key.startsWith(word)) {
					searchHelper(key, map, results);
				} else {
					break;
				}
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Adds founds values to a map of Search results if values are new, else
	 * update current frequency if result already exists. functionality relies
	 * on the mutability of objects/
	 * 
	 * @param key
	 *            value to look for
	 * @param map
	 *            where found values are stored
	 * @param results
	 *            where found results are stored
	 */
	private void searchHelper(String key, Map<String, SearchResult> map, ArrayList<SearchResult> results) {
		for (Map.Entry<String, TreeSet<Integer>> entry : index.get(key).entrySet()) {

			if (map.containsKey(entry.getKey())) {

				map.get(entry.getKey()).addFrequency(entry.getValue().size());
				map.get(entry.getKey()).updatePosition(entry.getValue().first());
			} else {

				SearchResult sr = new SearchResult(entry.getValue().size(), entry.getValue().first(), entry.getKey());
				map.put(entry.getKey(), sr);
				results.add(sr);
			}
		}
	}

	
}



