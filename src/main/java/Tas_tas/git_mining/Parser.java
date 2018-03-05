package Tas_tas.git_mining;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	private BufferedReader reader;

	private LinkedList<String> lineQueue = new LinkedList<>();

	private List<Pattern> MatchingPatterns = new ArrayList<>();

	private Pattern Param_Pattern, Method_Pattern, Method_Name_Pattern; // Patterns to be matched

	public List<Param_Diff> PD = new ArrayList<>(); // ArrayList to keep track of added parameter in methods

	private final String FILE_HEADER = "commit,old,new"; // Used for CSV

	Parser(InputStream In) {

		Param_Pattern = Pattern.compile("\\((.*?)\\)"); // Extract parameters

		Method_Pattern = Pattern
				.compile("(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\)");// Extract
																												// Method
																												// declaration

		Method_Name_Pattern = Pattern.compile("(\\w+(\\s+)?){2,}\\([^!@#$+%^]*\\)"); // Extract Method Name


		reader = new BufferedReader(new InputStreamReader(In));// Initializing input stream reader

	}

	public void get_diffs() {

		String S; // For processing each line of log file

		String Commit = ""; // Keep track of commit number

		ArrayList<String> Prev_Arr = new ArrayList<>(); // track method state before commit

		ArrayList<String> Next_Arr = new ArrayList<>(); // track method state after commit

		// Run Till End of file

		while (!(S = retLine()).equals("END Of FILE")) {

			// Start of a new Diff, save commit number

			if (S.startsWith("Diff")) {

				String[] tokens = S.split(" ");

				Commit = tokens[3].replace("diff", "");

				// Clear previous method Info

				Prev_Arr.clear();
				Next_Arr.clear();
			}

			// diff within same commit

			else if (S.startsWith("diff")) {

				// Clear previous method Info

				Prev_Arr.clear();
				Next_Arr.clear();
			}

			else if (S.startsWith("index")) {

				continue;

			}

			else if (S.startsWith("@@")) {

				// Clear previous method Info

				Prev_Arr.clear();
				
				if (Next_Arr != null) {
					Next_Arr.clear();
				}
			}

			else if (S.startsWith("---")) {

				continue;

			}

			else if (S.startsWith("+++")) {

				continue;

			}

			else if (S.startsWith("-")) {

				// CHeck if there is any method name in changed line

				Prev_Arr = matchesPattern(S);

			}

			else if (S.startsWith("+")) {
				// CHeck if there is any method name in changed line

				Next_Arr = matchesPattern(S);

				if (!(Prev_Arr.isEmpty()) && !(Next_Arr.isEmpty())) {
					
					//create the parameter difference list
					
					create_param_diff(Commit, Prev_Arr, Next_Arr);

					// Contains patterns

					// Compare if they have same name
				}
			}

		}
		
		//Write param_difffs to csv

		WriteCSV();
		
	}

	
	void WriteCSV() {
		
		FileWriter fw = null;
		
		System.out.println("Writing to csv");
		
		String S;
		
		try {
			
			//Open file writer

			fw = new FileWriter("Method_Diff.csv");
			
			PrintWriter pw = new PrintWriter(fw);
			pw.print(FILE_HEADER);
			pw.println();
			
			//Write the param_diffs to csv

			for (Param_Diff P : PD) {
			
				String recordAsCsv = P.toCsvRow();
			
				pw.println(recordAsCsv);
			}
			
			pw.flush();
			
			// Close the Print Writer
			
			pw.close();
			
			// Close the File Writer
			
			fw.close();
			
			System.out.println("Finished!\nPlease check Method_Diff.csv file");
			
		} catch (IOException ex) {
			Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				fw.close();
			} catch (IOException ex) {
				Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	
	//Takes line input from file
	
	public String retLine() {
		try {
			lineQueue.pollFirst();
			if (lineQueue.isEmpty()) {
				String nextLine = getNextLine();
				if (nextLine != null) {
					lineQueue.addLast(nextLine);
				}
				return nextLine;
			} else {
				return lineQueue.element();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getNextLine() throws IOException {
		String nextLine = reader.readLine();

		if (nextLine != null) {
			return nextLine;
		} else {
			return "END Of FILE";
		}
	}

	
	private ArrayList<String> matchesPattern(String line) {
		ArrayList<String> S_Array = new ArrayList<>();
		if (line == null) {
			return S_Array;
		}

		else {

			Matcher matcher = Method_Pattern.matcher(line);

			// Check if the changed line represents a method

			if (matcher.find()) {

				for (int i = 0; i < matcher.groupCount(); i++) {

					S_Array.add(matcher.group(i)); // Add pattern matched groups to method

				}

				// Extract Parameters

				Matcher matcher2 = Param_Pattern.matcher(line);

				if (matcher2.find()) {

					for (int i = 0; i < matcher2.groupCount(); i++) {
						S_Array.add(matcher2.group(i)); // Add pattern matched groups to method

					}

				}

				// Extract Method Name

				Matcher matcher3 = Method_Name_Pattern.matcher(line);
				if (matcher3.find()) {

					for (int i = 0; i < matcher3.groupCount(); i++) {
						S_Array.add(matcher3.group(i)); // Add pattern matched groups to method

					}

				}
				return S_Array;
			}

			return S_Array;
		}

	}
	
	private void create_param_diff(String Commit,ArrayList<String> Prev_Arr, ArrayList<String> Next_Arr) {
		
		if (Prev_Arr.get(4).equals(Next_Arr.get(4))) {

			// Extract Parameters of Previous signature

			String Prev_Token = Prev_Arr.get(2).substring(Prev_Arr.get(2).indexOf("(") + 1,
					Prev_Arr.get(2).indexOf(")"));

			// Create a list of the parameters

			ArrayList<String> Tokenize = new ArrayList<>(Arrays.asList(Prev_Token.split(",")));
			
			//For trimming and deleting empty strings

			for (int i = 0; i < Tokenize.size(); i++) {
				String Temp = Tokenize.get(i);
				Temp = Temp.trim();
				if (Temp.equals("")) {
					Tokenize.remove(i);
					i--;
					continue;
				}
				Tokenize.set(i, Temp);

			}

			String Next_Token = Next_Arr.get(2).substring(Next_Arr.get(2).indexOf("(") + 1,
					Next_Arr.get(2).indexOf(")"));
			
			ArrayList<String> Tokenize2 = new ArrayList<>(Arrays.asList(Next_Token.split(",")));
			
			for (int i = 0; i < Tokenize2.size(); i++) {
				String Temp = Tokenize2.get(i);
				Temp = Temp.trim();
				if (Temp.equals("")) {
					Tokenize2.remove(i);
					i--;
					continue;
				}
				Tokenize2.set(i, Temp);

				// System.out.println(T);
			}

			if (Tokenize.size() < Tokenize2.size()) {
				// Parameter added

				Param_Diff obj_P = new Param_Diff(Commit, Prev_Arr.get(0), Next_Arr.get(0));
				System.out.println("Commit SHA-1\t" + Commit);
				System.out.println("Old signature\t" + Prev_Arr.get(0));
				System.out.println("New Signature\t" + Next_Arr.get(0));
				PD.add(obj_P);
				Prev_Arr.clear();
				Next_Arr.clear();

			}
		}

	}

}
