package Tas_tas.git_mining;

import java.io.BufferedReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.util.ArrayList;
import java.util.LinkedList;
//import com.googlecode.javaewah.IteratorUtil;

/**
 * Simple snippet which shows how to retrieve the diffs between two commits
 */
public class App {

	private static String REMOTE_URL = "https://github.com/gingerswede/doris.git";

	public static void main(String[] args) throws IOException, GitAPIException {
		System.out.println("Please enter The Java GitHub code repository name in https://repo_name.git format");
		
		Scanner S = new Scanner(System.in);

		REMOTE_URL = S.nextLine().trim(); //Take as input repo_name

		System.out.println("Given URL\t"+REMOTE_URL);
		
		File F = Repository_Handler.cloneRepository(REMOTE_URL);
		
		try (Repository repository = Repository_Handler.openRepository(F)) {
		
			FileOutputStream FS = new FileOutputStream("Logfile.txt");
			
			try (Git git = new Git(repository)) {

				// compare older commit with the newer one, showing an addition
				// and 2 changes
				Iterable<RevCommit> commits = git.log().all().call();
				Iterator<RevCommit> it = commits.iterator();

				RevCommit Ra = it.next();
				while (it.hasNext()) {
					RevCommit Rb = it.next();
					String s = "Diff Between " + Ra.getName() + " " + Rb.getName();
					byte b[] = s.getBytes();
					FS.write(b);
					listDiff(repository, git, Rb, Ra, FS);
					Ra = Rb;

				}

			} finally {

				FS.close();
				// repository.close();
			}
			InputStream in = new FileInputStream("Logfile.txt");
			// Reader unbufferedReader = new InputStreamReader(in);
			// reader = new BufferedReader(new InputStreamReader(in));
			Parser P = new Parser(in);
			P.get_diffs();
			// String Line=P.retLine();
			// System.out.println(Line);
			// P.addIgnorePattern(ignorePattern);
			/*
			 * DiffParser parser = new UnifiedDiffParser(); InputStream in = new
			 * FileInputStream("C:\\Users\\user\\Documents\\testJgit.txt"); List<Diff> diff
			 * = parser.parse(in); for(Diff D:diff) {
			 * System.out.println(D.getFromFileName());
			 * System.out.println(D.getToFileName()); }
			 */
			/*
			 * RevWalk walk = new RevWalk( repository ); walk.sort(
			 * RevSort.COMMIT_TIME_DESC, true ); walk.sort( RevSort.REVERSE , true );
			 * RevCommit commit = walk.next(); while( commit != null ) { // use commit
			 * commit = walk.next(); } walk.close();
			 */

			in.close();
			// repository.close();

		}
	}

	private static void listDiff(Repository repository, Git git, RevCommit oldCommit, RevCommit newCommit,
			FileOutputStream FS) throws GitAPIException, IOException {
		// git.diff().setOutputStream(System.out).call();
		// git.diff()
		/*
		 * final List<DiffEntry> diffs = git.diff()
		 * .setOldTree(prepareTreeParser(repository, oldCommit))
		 * .setNewTree(prepareTreeParser(repository, newCommit)) .setContextLines(5)
		 * .call(); System.out.println("Found: " + diffs.size() + " differences"); for
		 * (DiffEntry diff : diffs) {
		 * 
		 * }
		 */
		DiffFormatter df = new DiffFormatter(FS);

		df.setRepository(git.getRepository());
		df.setContext(0);
		List<DiffEntry> entries = df.scan(prepareTreeParser(repository, oldCommit),
				prepareTreeParser(repository, newCommit));
		df.format(entries);
		df.flush();
		df.close();
		/*
		 * for( DiffEntry entry : entries ) { System.out.println( "def"+entry+"abc" ); }
		 */

		// df.format(diffs);
		/*
		 * List<DiffEntry> entries = df.scan( oldTreeIter, newTreeIter );
		 * 
		 * for( DiffEntry entry : entries ) { System.out.println( entry ); }
		 * 
		 * for (DiffEntry diff : diffs) { System.out.println("Diff: " +
		 * diff.getChangeType() + ": " + (diff.getOldPath().equals(diff.getNewPath()) ?
		 * diff.getNewPath() : diff.getOldPath() + " -> " + diff.getNewPath())); }
		 */
	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
		// from the commit we can build the tree which allows us to construct the
		// TreeParser
		// noinspection Duplicates

		try (RevWalk walk = new RevWalk(repository)) {
			Iterable<RevWalk> R;
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}

			walk.dispose();

			return treeParser;
		}
	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
		// from the commit we can build the tree which allows us to construct the
		// TreeParser
		// noinspection Duplicates

		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit commit = walk.parseCommit(repository.resolve(objectId));
			RevTree tree = walk.parseTree(commit.getTree().getId());
			/*
			 * for(RevCommit Rcommit:walk) {
			 * 
			 * }
			 */
			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}

			walk.dispose();

			return treeParser;
		}
	}
}
