package sheet1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Evaluation {

	// P@10
	protected double avgP_at_10;

	// avg BPREF
	protected double avgBPREF;

	// mean average precision
	protected double MAP;

	// geometric MAP
	protected double GMAP;

	// avg NDCG@10
	protected double avgNDCG_at_10;

	private List<List<Integer>> relevanzTable = new ArrayList<List<Integer>>();
	private List<List<Double>> precisionTable = new ArrayList<List<Double>>();
	private List<Double> avgPrecisionTable = new ArrayList<Double>();

	// Main-Methode zum Testen
	public static void main(String[] args) {

		Evaluation eval = null;

		// Pfad zur QREL-Datei
		String qrelpath = "./data/wang.qrel";

		// Pfad zum Verzeichnis mit den TREC-Dateien
		String trecpath = "./data";

		File trec_dir = new File(trecpath);
		File qrel_file = new File(qrelpath);

		// Relevanzurteile einlesen
		List<Set<Integer>> groundtruth = readQueries(qrel_file);

		// alle Trec-Dateien in Verzeichnis evaluieren
		File[] files = trec_dir.listFiles();
		for (File file : files) {
			eval = new Evaluation();
			if (file.getName().endsWith(".trec")) {
				// System.out.println(file.getName());
				eval.evaluate(groundtruth, file);
			}
		}
	}

	// Konstrukor
	public Evaluation() {
		avgP_at_10 = 0d;
		avgBPREF = 0d;
		MAP = 0d;
		GMAP = 0d;
		avgNDCG_at_10 = 0d;
	}

	// vgl. Aufgabenstellung
	protected void evaluate(List<Set<Integer>> groundtruth, File file) {

		createRelevanzTable(groundtruth, file);

		createPrecisionTable();
		
		callculateAvgPrecisionTable();
		
		avgP_at_10 = avgPrecisionTable.get(9); //Index 9 => Rang 10

		MAP = callculateMAP();
		
		GMAP = callculateGMAP();
		
				
		// Ausgabe der Ergebnisse
		printResults();
	}

	private void createRelevanzTable(List<Set<Integer>> groundtruth, File file) {
		// Code zum Einlesen der Dateien teilweise vorgegeben.
		Scanner scanner;
		try {
			scanner = new Scanner(file);

			int oldQueryID = -1;
			List<Integer> tableLine = null;

			while (scanner.hasNextLine()) {
				String line = (String) scanner.nextLine();
				// System.out.println(line);

				String[] splittedLine = line.split(" ");

				int queryID = Integer.parseInt(splittedLine[0]);
				int docID = Integer.parseInt(splittedLine[2]);

				// ändert sich query erzeuge neuen eintrag
				if (oldQueryID != queryID) {
					tableLine = new ArrayList<Integer>();
					relevanzTable.add(tableLine);
				}
				Set<Integer> relDocuments = groundtruth.get(queryID - 1);

				// ist das betrachtete document relevant?
				if (relDocuments.contains(docID)) {
					tableLine.add(1); // ja
				} else {
					tableLine.add(0); // nein
				}
				// System.out.println("old: "+ oldQueryID + "new: "+ queryID);
				oldQueryID = queryID;

			}

			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}



	// Ausgabe der Ergebnisse
	protected void printResults() {// Ausgabe
		System.out
				.println(" P@10\t" + Math.round(getP_at_10() * 1000) / 1000.0);
		System.out.println(" MAP\t" + Math.round(getMAP() * 1000) / 1000.0);
		System.out.println(" GMAP\t" + Math.round(getGMAP() * 1000) / 1000.0);
		System.out.println(" NDCG\t" + Math.round(getNdcg_at_10() * 1000)
				/ 1000.0);
		System.out.println(" BPREF\t" + Math.round(getAvgBPREF() * 1000)
				/ 1000.0);
	}

	private void createPrecisionTable() {
		List<Double> precision;
		int R = -1; // Rang
		int r = 0; // Bisher gefundene relevante Documente
		for (List<Integer> relevanz : relevanzTable) {
			precision = new ArrayList<Double>();
			R = 1;
			r = 0;
			for (Integer rel : relevanz) {
				r += rel;
				precision.add(new Double(r / R));
				R++;
			}
			precisionTable.add(precision);
		}
	}
	
	private void callculateAvgPrecisionTable()
	{
		for(int rang= 1; rang<= precisionTable.size(); rang++){
			avgPrecisionTable.add(callculateAvgPat(rang));
		}
	}
	
	private double callculateAvgPat(int rang){
		int index = rang -1; // avgP at 10 => index 9
		
		int n = precisionTable.size();
		double avgP = 0d;
		
		for(List<Double> P : precisionTable){
			avgP += P.get(index); 
		}
		return  avgP / n;
	}
	
	private double callculateMAP(){
		double sum = 0;
		int n = avgPrecisionTable.size();
		
		for(double AP : avgPrecisionTable){
			sum+= AP;
		}
		return sum / n;
	}
	
	private double callculateGMAP(){
		double sum = 0;
		int n = avgPrecisionTable.size();
		
		for(double AP : avgPrecisionTable){
			sum+= AP;
		}
		double pow = 1d / n;	//nth root => ^1/n
		return  Math.pow(sum, pow);
	}

	// vgl. Aufgabenstellung, bereits implementiert
	protected static List<Set<Integer>> readQueries(File file) {

		Set<Integer> rels_for_query = null;
		int old_query_id = -1;

		List<Set<Integer>> result = new ArrayList<Set<Integer>>();

		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				// eine Zeile als String
				String line = scanner.nextLine();
				// als Character-Array
				String[] splittedLine = line.split(" ");

				int query_id = new Integer(splittedLine[0]).intValue();
				if (query_id != old_query_id) {
					if (rels_for_query != null) {
						result.add(rels_for_query);
					}
					rels_for_query = new HashSet<Integer>();
					old_query_id = query_id;
				}

				int doc_id = new Integer(splittedLine[2]).intValue();
				rels_for_query.add(doc_id);
				// System.out.println(query_id + " " + doc_id);
			}
			scanner.close();

			result.add(rels_for_query);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}

	// GETTER methods

	public double getP_at_10() {
		return avgP_at_10;
	}

	public double getAvgBPREF() {
		return avgBPREF;
	}

	public double getMAP() {
		return MAP;
	}

	public double getGMAP() {
		return GMAP;
	}

	public double getNdcg_at_10() {
		return avgNDCG_at_10;
	}

}
