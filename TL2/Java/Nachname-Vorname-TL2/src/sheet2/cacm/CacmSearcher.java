package sheet2.cacm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class CacmSearcher {

	// default Analyzer oder eine eigene Variante?
	private static boolean useStandardAnalyzer = false;

	// Pfad zu den Anfragen
	public static String path2queries = "cacm/query.text";

	// Main-Methode zum Testen
	public static void main(String[] args) throws IllegalArgumentException,
			IOException, ParseException {

		//Zeichenkette des Indexverzeichnisses
		String indexDir = null;// 1
		//Praefix der TREC-Dateinamen 
		String outName = null;
		//der verwendete Analyzer
		Analyzer analyzer = null;

		if (useStandardAnalyzer) {
			indexDir = "idx_cacm_std";
			analyzer = new StandardAnalyzer();
			outName = "std";
		} else {// use MyStemAnalyzer
			indexDir = "idx_cacm_my";
			analyzer = new MyStemAnalyzer();
			outName = "my";
		}

		// welche Similarity (dh IR-Modelle) wird evaluiert
		Similarity[] sims = new Similarity[] {
				new LMJelinekMercerSimilarity(0.2f),
				new LMDirichletSimilarity(), new BM25Similarity(),
				new DefaultSimilarity() };

		// Iteriere ueber die Similarities ... und suche
		for (int i = 0; i < sims.length; i++) {
			Similarity sim = sims[i];
			StringBuilder builder = search(indexDir, sim, analyzer);
			System.err.println("cacm-" + sim.toString() + "-"
					+ analyzer.toString() + ".trec");
			FileWriter fw = new FileWriter(new File("cacm/cacm-" + sim.toString()
					+ "-" + outName + ".trec"));
			fw.write(builder.toString());
			fw.close();
		}
	}

	// implementiert die eigentliche Suche auf Basis mehrerer Felder
	public static StringBuilder search(String indexDir, Similarity sim,
			Analyzer analyzer) throws IOException, ParseException {

		// lies die Anfragen ein
		List<String> queries = CacmHelper.readQueries(path2queries);
		System.out.println("#queries: " + queries.size());

		StringBuilder builder = null;

		//TODO hier bitte implementieren!
		
		if (builder == null){
			throw new RuntimeException("Cannot write TREC file without content!");
		}
		
		return builder;
	}
}
