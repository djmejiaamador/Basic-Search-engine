import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiThreadedIndexBuilder implements IndexBuilderInterface {

	private static final Logger log = LogManager.getLogger();
	private final WorkQueue queue;
	private final ThreadSafeInvertedIndex index;

	public MultiThreadedIndexBuilder(WorkQueue queue, ThreadSafeInvertedIndex index) {
		this.queue = queue;
		this.index = index;
		log.debug("created mutli builder");
	}

	/**
	 * Reads a file, strips all HTML and parses words then adds words and paths
	 * and # of occurrences to inverted index but Multi-threaded.
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

			queue.execute(new IndexMinion(file, index));
		}
		queue.finish();
	}

	/**
	 * Minion class takes care of tasks that build index
	 * 
	 * @author douglasmejia
	 *
	 */
	private static class IndexMinion implements Runnable {

		private Path path;
		private ThreadSafeInvertedIndex index;

		public IndexMinion(Path path, ThreadSafeInvertedIndex index) {
			this.path = path;
			this.index = index;
			log.debug("Minion created ");
		}

		@Override
		public void run() {
			try {
				log.debug("working on paths");
				InvertedIndex local = new InvertedIndex();
				IndexBuilder.buildIndex(path, local);
				index.addAll(local);
				log.debug("complete work on paths");

			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
		}
	}
}