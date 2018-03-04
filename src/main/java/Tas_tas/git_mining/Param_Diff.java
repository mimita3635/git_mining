package Tas_tas.git_mining;

import java.util.Arrays;

public class Param_Diff {
	String Commit_SHA;
	String Old_sign;
	String New_sign;

	Param_Diff(String A, String B, String C) {
		Commit_SHA = A;
		Old_sign = B;
		New_sign = C;
	}

	public String toCsvRow() {
		String csvRow = "";
		for (String value : Arrays.asList(Commit_SHA, Old_sign, New_sign)) {
			String processed = value;
			if (value.contains("\"") || value.contains(",")) {
				processed = "\"" + value.replaceAll("\"", "\"\"") + "\"";
			}
			csvRow += "," + processed;
		}
		return csvRow.substring(1);
	}
}
