package sheet2.cacm;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
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

	// bestimmt den verwendeten Analyzer //TODO: change to false
	public static boolean useStandardAnalyzer = true;

	// Konstruktor
	public CacmIndexer(String indexDir, Analyzer analyzer) throws IOException {
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

		doc = new Document();
		int counter = 1;
		String content = "";
		String line = "";
		String title = "";
		String id = "";
		boolean idTest = false;
		boolean contentTest = false;
		boolean titleTest = false;
		boolean tooFar = false;
		while (scanner.hasNextLine()) {
			
			if(!tooFar) line = scanner.nextLine();
			tooFar = false;
			
			if (line.startsWith(".I")) {
				if (idTest == true) {
					doc.add(new TextField(ID, id, Field.Store.YES));
					doc.add(new TextField(TITLE, title, Field.Store.YES));
					writer.addDocument(doc);
					doc = new Document();
					idTest = false;
					titleTest = false;
				}
				id = line.replace(".I", "");
				idTest = true;
			}
			if (line.startsWith(".T")) {
				while(scanner.hasNextLine()){
					String tmp = scanner.nextLine();
					if(tmp.startsWith(".")){
						line = tmp;
						tooFar = true;
						break;
					}
					else{
						title+=tmp;
					}
				}
				
				titleTest = true;
			}
			if (line.startsWith(".W")) {
				while(scanner.hasNextLine()){
					String tmp = scanner.nextLine();
					if(tmp.startsWith(".")){
						line = tmp;
						tooFar = true;
						break;
					}
					else{
						content+=tmp;
					}
				}
				contentTest = true;
			}

			if (contentTest && titleTest && idTest) {
				doc.add(new TextField(ID, id, Field.Store.YES));
				doc.add(new TextField(TITLE, title, Field.Store.YES));
				doc.add(new TextField(CONTENT, content, Field.Store.YES));
				writer.addDocument(doc);
				doc = new Document();
				idTest = false;
				titleTest = false;
				contentTest = false;
				
				System.out.println("Content: " + content + "\nID: " + id
						+ "\nTITLE: " + title);
				System.out.println("Write Document " + counter++);
				content = "";
				title = "";
				id = "";
			}

		}

		scanner.close();
		// TODO: hier bitte implementieren
	}
}

