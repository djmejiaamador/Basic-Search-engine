import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	private static final Logger log = LogManager.getLogger();
	private final ReadWriteLock lock;

	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

	@Override
	public void add(String word, String path, int position) {
		lock.lockReadWrite();
		try {
			super.add(word, path, position);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public void addAll(String[] words, String file, int start) {
		lock.lockReadWrite();
		try {
			super.addAll(words, file, start);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public void addAll(InvertedIndex other) {
		lock.lockReadWrite();
		try {
			super.addAll(other);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public void outputFile(Path outputLocation) throws IOException {
		log.debug("Writing to: " + outputLocation.toString());
		lock.lockReadOnly();
		try {
			super.outputFile(outputLocation);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public boolean contains(String key) {
		lock.lockReadOnly();
		try {
			return super.contains(key);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public int numWords() {
		lock.lockReadOnly();
		try {
			return super.numWords();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public int numPath(String word) {
		lock.lockReadOnly();
		try {
			return super.numPath(word);
		} finally {
			lock.unlockReadOnly();
		}

	}

	@Override
	public ArrayList<SearchResult> exactSearch(String[] words) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(words);
		} finally {
			lock.unlockReadOnly();
		}

	}

	@Override
	public ArrayList<SearchResult> partialSearch(String[] words) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(words);
		} finally {
			lock.unlockReadOnly();
		}

	}

}
