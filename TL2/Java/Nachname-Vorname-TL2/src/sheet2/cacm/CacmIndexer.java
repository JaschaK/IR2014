package sheet2.cacm;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

//Indexing http://ir.dcs.gla.ac.uk/resources/test_collections/cacm/
public class CacmIndexer {

	// FIELD NAMES:
	// interne ID (.I)
	public static final String ID = "docid";
	// Titel des Dokuments (.T)
	public static final String TITLE = "title";
	// Inhalt des Dokuments (.W)
	public static final String CONTENT = "content";

	// Analyzer
	public Analyzer analyzer = null;

	// IndexWriter fuer den schreibenden Zugriff
	public IndexWriter writer;

	// bestimmt den verwendeten Analyzer
	public static boolean useStandardAnalyzer = false;

	// Konstruktor
	public CacmIndexer(String indexDir, Analyzer analyzer)
			throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));

		this.analyzer = analyzer;

		IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);

		writer = new IndexWriter(dir, iwc); // 3 modified
	}

	// Main-Methode zum Testen
	public static void main(String[] args) throws Exception {

		String indexDir = null;// 1
		String dataDir = "cacm/cacm.all"; // 2

		Analyzer analyzer = null;

		if (useStandardAnalyzer) {
			indexDir = "idx_cacm_std";
			analyzer = new StandardAnalyzer();
		} else {// verwende MyStemAnalyzer
			indexDir = "idx_cacm_my";
			analyzer = new MyStemAnalyzer();
		}

		long start = System.currentTimeMillis();
		CacmIndexer indexer = new CacmIndexer(indexDir, analyzer);
		int numIndexed;
		try {
			numIndexed = indexer.index(dataDir);
		} finally {
			indexer.close();
		}
		long end = System.currentTimeMillis();

		System.out.println("Indexing " + numIndexed + " files took "
				+ (end - start) + " milliseconds");
	}

	// as before, nothing new :-)
	public int index(String dataDir) throws Exception {

		File f = new File(dataDir);
		indexFile(f);
		
		return writer.numDocs(); // 5
	}

	// as before, nothing new :-)
	public void close() throws IOException {
		writer.close(); // 4
	}

	// Hier wird die eigentliche Indexierung durchgefuehrt
	public void indexFile(File file) throws Exception {

		System.out.println("Indexing " + file.getCanonicalPath());

		Scanner scanner = new Scanner(file);

		// Lucene's document representation
		Document doc = null;

		while (scanner.hasNextLine()) {

			String line = (String) scanner.nextLine();

			// TODO: hier bitte implementieren
		}
		
		// TODO: hier bitte implementieren
	}
}
