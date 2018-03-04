package Tas_tas.git_mining;

import java.io.BufferedReader;
import java.io.File;
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
//import com.csvreader.CsvWriter;

public class Parser {

    public BufferedReader reader;
    private LinkedList<String> lineQueue = new LinkedList<>();

    private int lineNumber = 0;

    private List<Pattern> ignorePatterns = new ArrayList<>();

    private List<Pattern> MatchingPatterns = new ArrayList<>();

    Pattern Ptrn, Pttrn2, Ptrn3;
    private boolean isEndOfStream = false;

    public List<Param_Diff> PD = new ArrayList<>();

    private final String FILE_HEADER = "commit,old,new";
    private final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    Parser(InputStream In) {
        Ptrn = Pattern.compile("\\((.*?)\\)");//Extract parameters
        Pttrn2 = Pattern.compile("(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\)");//Extract Method declaration
        Ptrn3 = Pattern.compile("(\\w+(\\s+)?){2,}\\([^!@#$+%^]*\\)");
        //MatchingPatterns.add(Ptrn);
        MatchingPatterns.add(Pttrn2);
        reader = new BufferedReader(new InputStreamReader(In));
    }

    public List<Param_Diff> get_diffs() {
        String S;
        String Commit_1 = "";
        String Commit_2 = "";
        String filename1 = "";
        String filename2 = "";
        ArrayList<String> Prev_Arr = new ArrayList<>();

        ArrayList<String> Next_Arr = new ArrayList<>();

        //Run Till End of file
        while (!(S = retLine()).equals("END Of FILE")) {
            System.out.println(S);
            //Start of a new Diff, save commit numbers
            if (S.startsWith("Diff")) {
                String[] tokens = S.split(" ");
                Commit_2 = tokens[3].replace("diff", "");
                filename1 = "";
                filename2 = "";
                //System.out.println(tokens[3]);
            } //diff within same commit
            else if (S.startsWith("diff")) {
                filename1 = "";
                filename2 = "";
            } else if (S.startsWith("index")) {
                continue;
            } else if (S.startsWith("@@")) {
                Prev_Arr.clear();
                if (Next_Arr != null) {
                    Next_Arr.clear();
                }
            } else if (S.startsWith("---")) {
                filename1 = S.substring(S.indexOf("/") + 1);
                //System.out.println(filename1+"aaa");

            } else if (S.startsWith("+++")) {
                if (!(filename1.equals("dev/null"))) {
                    filename2 = S.substring(S.indexOf("/") + 1);
                    //System.out.println(filename2+"bbb");

                }
            } else if (S.startsWith("-")) {
                Prev_Arr = matchesPattern(S);
                /*System.out.println(S);
				Matcher matcher =Ptrn.matcher(S);
				if(matcher.find()) {
					System.out.println("dash");
				}
                 */
                //Prev_Arr.clear();
                /*
				if((Prev_Arr=matchesPattern(S))!=null) {
					
					/*
					for(String str: Prev_Arr) {
						System.out.println(str+"  mmm");
					}
					String Tokens=Prev_Arr.get(2).substring(Prev_Arr.get(2).indexOf("(")+1, Prev_Arr.get(2).indexOf(")"));
					//System.out.println("Tok\t"+Tokens);
					String[] Tokenize=Tokens.split(",");
					for(String T: Tokenize) {
						T.trim();
						//System.out.println(T);
					}
	                
				}*/

            }
            else if (S.startsWith("+")) {
              //  System.out.println(S + "\tDDDD");
                Next_Arr = matchesPattern(S);
                /*System.out.println(S);
				Matcher matcher =Ptrn.matcher(S);
				if(matcher.find()) {
					System.out.println("dash");
				}
                 */
                //ArrayList<String> S_Arr;
                /*
				if((Next_Arr=matchesPattern(S))!=null) {
					//Next_Arr.clear();   
					
					/*for(String str: Next_Arr) {
						//System.out.println(str+"  fff");
					}*/
 /*
					String Tokens=Next_Arr.get(2).substring(Next_Arr.get(2).indexOf("(")+1, Next_Arr.get(2).indexOf(")"));
					//System.out.println("Tok\t"+Tokens);
					String[] Tokenize=Tokens.split(",");
					for(String T: Tokenize) {
						T.trim();
						//System.out.println(T);
					}
                 */
            
                 
            if (!(Prev_Arr.isEmpty()) && !(Next_Arr.isEmpty())) {
                if (Prev_Arr.get(4).equals(Next_Arr.get(4))) {

                    String Prev_Token = Prev_Arr.get(2).substring(Prev_Arr.get(2).indexOf("(") + 1, Prev_Arr.get(2).indexOf(")"));
                    //System.out.println("Tok\t"+Tokens);
                    String[] Tokenize = Prev_Token.split(",");
                    for (String T : Tokenize) {
                        T = T.trim();
                        //System.out.println(T);
                    }

                    String Next_Token = Next_Arr.get(2).substring(Next_Arr.get(2).indexOf("(") + 1, Next_Arr.get(2).indexOf(")"));
                    //System.out.println("Tok\t"+Tokens);
                    String[] Tokenize2 = Next_Token.split(",");
                    for (String T : Tokenize2) {
                        T = T.trim();
                        //System.out.println(T);
                    }

                    if (Tokenize.length <= Tokenize2.length) {
                        //Parameter added

                        Param_Diff obj_P = new Param_Diff(Commit_2, Prev_Arr.get(0), Next_Arr.get(0));
                        System.out.println("Commit SHA-1\t" + Commit_2);
                        System.out.println("Old signature\t" + Prev_Arr.get(0));
                        System.out.println("New Signature\t" + Next_Arr.get(0));
                        PD.add(obj_P);
                        Prev_Arr.clear();
                        Next_Arr.clear();
                  
                    }
                }
            }
            }

        }

        WriteCSV();
        return PD;
    }

 
    
    void WriteCSV() {
        FileWriter fw = null;
        System.out.println("Shuru");
        String S;
        try {
            
            fw = new FileWriter("WriteTest.csv");
            PrintWriter pw = new PrintWriter(fw);
            pw.print(FILE_HEADER);
            pw.println();
            
            for (Param_Diff P : PD) {
                String recordAsCsv = P.toCsvRow();
                //pw.print(P.Commit_SHA);
                //S="\"" +P.Old_sign+"\"";
                pw.println(recordAsCsv);
             // pw.println();
                //Fw.append(P.Commit_SHA + "," + P.Old_sign + "," + P.New_sign + "\n");
            }
            //Write to file for the first row
            /*
            pw.print("Hello guys");
            pw.print(",");
            pw.print("Java Code Online is maing");
            pw.print(",");
            pw.println("a csv file and now a new line is going to come");
            //Write to file for the second row
            pw.print("Hey");
            pw.print(",");
            pw.print("It's a");
            pw.print(",");
            pw.print("New Line");*/
            //Flush the output to the file
            pw.flush();
            //Close the Print Writer
            pw.close();
            //Close the File Writer
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*
            FileWriter fileWriter = null;
            
            try {
            fileWriter = new FileWriter("tesst.csv");
            
            //Write the CSV file header
            fileWriter.append(FILE_HEADER.toString());
            
            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            //Write a new student object list to the CSV file
            for (Param_Diff P : PD) {
            //fileWriter.append(String.valueOf(P.Commit_SHA));
            fileWriter.append(P.Commit_SHA);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(P.Old_sign);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(P.New_sign);
            //fileWriter.append(COMMA_DELIMITER);
            //fileWriter.append(student.getGender());
            //fileWriter.append(COMMA_DELIMITER);
            //fileWriter.append(String.valueOf(student.getAge()));
            fileWriter.append(NEW_LINE_SEPARATOR);
            }
            
            
            
            System.out.println("CSV file was created successfully !!!");
            
            } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
            } finally {
            
            try {
            fileWriter.flush();
            fileWriter.close();
            } catch (IOException e) {
            System.out.println("Error while flushing/closing fileWriter !!!");
            e.printStackTrace();
            }
            
            }
             */
 /*
            try {
            
            PrintWriter pw = new PrintWriter(new File("test.csv"));
            StringBuilder sb = new StringBuilder();
            sb.append("commit");
            sb.append(',');
            sb.append("old");
            sb.append(',');
            sb.append("new");
            
            sb.append('\n');

            for (Param_Diff P : PD) {
            sb.append(P.Commit_SHA);
            sb.append('\n');
            }
            pw.write(sb.toString());
            pw.close();
            System.out.println("done!");
             */
 /*
            FileWriter Fw = new FileWriter("C:\\Users\\user\\Documents\\GitHub\\Project_Mining\\CSV.csv");
            
            Fw.append("Commit SHA-1,Old Signature,New Signature\n");
            for (Param_Diff P : PD) {
            Fw.append(P.Commit_SHA + "," + P.Old_sign + "," + P.New_sign + "\n");
            }
            Fw.flush();
            Fw.close();
            System.out.println("Shesh");
            
            
            } catch (Exception E) {
            E.printStackTrace();
            }
             */
        }
    }

    public String retLine() {
        try {
            lineQueue.pollFirst();
            lineNumber++;
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

    public void addIgnorePattern(String ignorePattern) {
        this.ignorePatterns.add(Pattern.compile(ignorePattern));
    }

    private String getNextLine() throws IOException {
        String nextLine = reader.readLine();
        while (matchesIgnorePattern(nextLine)) {
            nextLine = reader.readLine();
        }

        if (nextLine != null) {
            return nextLine;
        } else {
            return "END Of FILE";
        }
    }

    private boolean matchesIgnorePattern(String line) {
        if (line == null) {
            return false;
        } else {
            for (Pattern pattern : ignorePatterns) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    return true;
                }
            }
            return false;
        }
    }

    /*
    private ArrayList<String> matchesPattern(String line) {
        ArrayList<String> S_Array = new ArrayList<>();
        if (line == null) {
            return S_Array;
        } else {
            
            // for (Pattern pattern : MatchingPatterns) {
            //System.out.println("Dash");
            Matcher matcher = Ptrn3.matcher(line);
            if (matcher.find()) {
                System.out.println(matcher.groupCount());
                for (int i = 0; i < matcher.groupCount(); i++) {
                    S_Array.add(matcher.group(i));
                    //System.out.println(matcher.group(i));
                }
                //return true;
                Matcher matcher2 = Ptrn.matcher(line);
                if (matcher2.find()) {
                    //System.out.println(matcher2.groupCount());
                    for (int i = 0; i < matcher2.groupCount(); i++) {
                        S_Array.add(matcher2.group(i));
                        //System.out.println(matcher2.group(i));
                    }

                }
                /*
                    Matcher matcher3 = Ptrn3.matcher(line);
                    if (matcher3.find()) {
                    	//System.out.println(matcher3.groupCount());
                    	for(int i=0; i<matcher3.groupCount();i++) {
                    		S_Array.add(matcher3.group(i));
                    		//System.out.println(matcher3.group(i)+"\tyyy");
                    	}
                    
                    }*/
    /*
                return S_Array;
            }

            return S_Array;
        }
        //return null;
    }

    */
    private ArrayList<String> matchesPattern(String line) {
        if (line == null) {
            return null;
        } else {
        	ArrayList<String> S_Array=new ArrayList<>();
           // for (Pattern pattern : MatchingPatterns) {
            	//System.out.println("Dash");
                Matcher matcher = Pttrn2.matcher(line);
                if (matcher.find()) {
                	//System.out.println(matcher.groupCount());
                	for(int i=0; i<matcher.groupCount();i++) {
                		S_Array.add(matcher.group(i));
                		//System.out.println(matcher.group(i));
                	}
                    //return true;
                	Matcher matcher2 = Ptrn.matcher(line);
                    if (matcher2.find()) {
                    	//System.out.println(matcher2.groupCount());
                    	for(int i=0; i<matcher2.groupCount();i++) {
                    		S_Array.add(matcher2.group(i));
                    		//System.out.println(matcher2.group(i));
                    	}
                    
                    }
                    Matcher matcher3 = Ptrn3.matcher(line);
                    if (matcher3.find()) {
                    	//System.out.println(matcher3.groupCount());
                    	for(int i=0; i<matcher3.groupCount();i++) {
                    		S_Array.add(matcher3.group(i));
                    		//System.out.println(matcher3.group(i)+"\tyyy");
                    	}
                    
                    }
                    return S_Array;
                }
                
                return S_Array;
            }
            //return null;
       }
     
 /*
    private boolean matchesPattern(String line) {
        if (line == null) {
            return false;
        } else {
            for (Pattern pattern : MatchingPatterns) {
            	System.out.println("Dash");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                	System.out.println(matcher.groupCount());
                	for(int i=0; i<matcher.groupCount();i++)
                		System.out.println(matcher.group(i));
                    return true;
                }
            }
            return false;
        }
    }
     */
}
