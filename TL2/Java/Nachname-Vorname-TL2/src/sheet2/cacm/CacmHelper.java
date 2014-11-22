package sheet2.cacm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CacmHelper {

	// Methode zum Einlesen der Anfragetexte
	public static List<String> readQueries(String filename)
			throws FileNotFoundException {

		List<String> queryTexts = new LinkedList<String>();

		Scanner scanner = new Scanner(new File(filename));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();

			if (line.startsWith(".W")) {
				String queryText = scanner.nextLine().trim();
				while (scanner.hasNextLine()) {
					line = scanner.nextLine().trim();
					if (line.startsWith("."))
						break;
					queryText += " " + line;
				}
				queryTexts.add(queryText);
				System.out.println(queryText);
			}
		}

		scanner.close();

		return queryTexts;
	}

	// Methode zur Generierung der QREL-Datei
	public static StringBuilder generateQRels(String filename)
			throws IOException {

		StringBuilder builder = new StringBuilder();
		
		Scanner scanner = new Scanner(new File(filename));

		//TODO hier bitte implementieren!

		scanner.close();

		return builder;
	}

	// Main-Methode zum Testen und schreiben der QREL-Datei.
	public static void main(String[] args) {
		StringBuilder builder;
		try {
			// generate qrels file
			builder = CacmHelper.generateQRels("cacm/qrels.text");

			if (builder == null) {
				throw new RuntimeException("Error!");
			}

			FileWriter fw = new FileWriter(new File("cacm/cacm.qrel"));
			fw.write(builder.toString());
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
