package Tas_tas.git_mining;

import java.util.Arrays;

/*
 * Class to keep track of csv records
 * 
 * 
 * */

public class Param_Diff {
	String Commit_SHA;
	String Old_sign;
	String New_sign;

	Param_Diff(String Commit, String Old, String New) {
		Commit_SHA = Commit;
		Old_sign = Old;
		New_sign = New;
	}
	
	
	/*
	 * Converting the Param_Diffs to CSV rows
	 * */
	

	public String toCsvRow() {
		String csvRow = "";
		for (String value : Arrays.asList(Commit_SHA, Old_sign, New_sign)) {
			String processed = value;
			
			//Converting to STrings for handling commas
			
			if (value.contains("\"") || value.contains(",")) {
				processed = "\"" + value.replaceAll("\"", "\"\"") + "\"";
			}
			csvRow += "," + processed;
		}
		return csvRow.substring(1);
	}
}
