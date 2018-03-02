package Tas_tas.git_mining;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Parser {

	
	public  BufferedReader reader;
	private  LinkedList<String> lineQueue = new LinkedList<>();

	private  int lineNumber = 0;

	private  List<Pattern> ignorePatterns = new ArrayList<>();
	
	private  List<Pattern> MatchingPatterns = new ArrayList<>();

	Pattern Ptrn, Pttrn2;
	private  boolean isEndOfStream = false;
	
	public List<Param_Diff> PD=new ArrayList<>();
	
	Parser(InputStream In){
		Ptrn=Pattern.compile("\\((.*?)\\)");
		Pttrn2=Pattern.compile("(\\w+(\\s+)?){2,}\\([^!@#$+%^]*\\)");
		//MatchingPatterns.add(Ptrn);
		MatchingPatterns.add(Pttrn2);
		reader=new BufferedReader(new InputStreamReader(In));
	}
	
	public List<Param_Diff> get_diffs(){
		String S;
		String Commit_1;
		String Commit_2;
		String filename1="";
		String filename2="";
		//Run Till End of file
		while(!(S=retLine()).equals("END Of FILE")) {
			//Start of a new Diff, save commit numbers
			if(S.startsWith("Diff")) {
				String[] tokens= S.split(" ");
				Commit_2=tokens[3];
				//System.out.println(tokens[3]);
			}
			
			else if(S.startsWith("index")) continue;
			else if(S.startsWith("@@")) continue;
			else if(S.startsWith("---")) {
				filename1=S.substring(S.indexOf("/")+1);
				//System.out.println(filename1+"aaa");
				
			}
			else if(S.startsWith("+++")) {
				if(!(filename1.equals("dev/null"))){
					filename1=S.substring(S.indexOf("/")+1);
					//System.out.println(filename2+"bbb");
					
				}
			}
			
			else if(S.startsWith("-")) {
				/*System.out.println(S);
				Matcher matcher =Ptrn.matcher(S);
				if(matcher.find()) {
					System.out.println("dash");
				}
				*/
				if(matchesPattern(S)==true) {
					
				}
			}
		}
		return PD;
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

        if(nextLine!=null) return nextLine;
        else return "END Of FILE";
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

}
