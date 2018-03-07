package Tas_tas.git_mining;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;

import java.util.Scanner;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Simple snippet which shows how to retrieve the diffs between two commits
 */
public class App {

	//URL for accessing remote repository
	private static String REMOTE_URL = "";
	
	private static String m_uriRegExp = "^(http(s?))://.*\\.git";;
	

	public static void main(String[] args) throws IOException, GitAPIException {
		
		System.out.println("Please enter The Java GitHub code repository name in https://repo_name.git format");
		
		Scanner S = new Scanner(System.in);

		REMOTE_URL = S.nextLine().trim(); // Take as input repo_name

		// Check for error
		while (!Pattern.compile(m_uriRegExp).matcher(REMOTE_URL).matches()) {

			System.out.println("Please Enter valid URI");

			REMOTE_URL = S.nextLine().trim(); // Take as input repo_name

		}
		S.close();
		System.out.println("Given URL\t"+REMOTE_URL);
		
		File F = Repository_Handler.cloneRepository(REMOTE_URL);
		
		try (Repository repository = Repository_Handler.openRepository(F)) {
		
			//Write log to file
			FileOutputStream FS = new FileOutputStream("Logfile.txt");
			
			try (Git git = new Git(repository)) {

				// compare older commit with the newer one
		
				Iterable<RevCommit> commits = git.log().all().call();  //git log
				Iterator<RevCommit> it = commits.iterator();

				RevCommit Ra = it.next();
				while (it.hasNext()) {
					RevCommit Rb = it.next();
					String s = "Diff Between " + Ra.getName() + " " + Rb.getName();
					byte b[] = s.getBytes();
					FS.write(b);
					Repository_Handler.listDiff(repository, git, Rb, Ra, FS);
					Ra = Rb;

				}

			} finally {
				
				FS.close();
				// repository.close();
			}
		
			InputStream in = new FileInputStream("Logfile.txt");

			Parser P = new Parser(in);
			
			P.get_diffs();

			in.close();
			

		}
	}


	
}
