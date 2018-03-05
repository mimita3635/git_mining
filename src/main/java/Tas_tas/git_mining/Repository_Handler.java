package Tas_tas.git_mining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

public class Repository_Handler {

	public static BufferedReader reader;
	
	public static File cloneRepository(String REMOTE_URL) throws IOException {
		
		// prepare a new folder for the cloned repository
		
		String LocalPath = System.getProperty("user.dir") + "\\"
				+ REMOTE_URL.substring(REMOTE_URL.lastIndexOf("/") + 1, REMOTE_URL.lastIndexOf(".git"));
		

		File localPath = new File(LocalPath);
		
		// If already exists, then delete
		
		if (localPath.exists())
			FileUtils.deleteDirectory(localPath);
		
		// then clone
		System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
		
		try (Git result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(localPath).call()) {
			
			File F=result.getRepository().getDirectory();

			System.out.println("Having repository: " + F);
			
			return F;
		} catch (InvalidRemoteException e) {
			
			e.printStackTrace();
		} catch (TransportException e) {
			
			e.printStackTrace();
		} catch (GitAPIException e) {
		
			e.printStackTrace();
		}
		
		return null;
	}

	
	public static Repository openRepository(File F) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
	
		return builder.setGitDir(F).readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
	}
	
	

	public static void listDiff(Repository repository, Git git, RevCommit oldCommit, RevCommit newCommit,
		
			FileOutputStream FS) throws GitAPIException, IOException {

		DiffFormatter df = new DiffFormatter(FS);

		df.setRepository(git.getRepository());
		df.setContext(0);
		List<DiffEntry> entries = df.scan(prepareTreeParser(repository, oldCommit),
				prepareTreeParser(repository, newCommit));
		df.format(entries);
		df.flush();
		df.close();

	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
		// from the commit we can build the tree which allows us to construct the
		// TreeParser
		// noinspection Duplicates

		try (RevWalk walk = new RevWalk(repository)) {
			
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}

			walk.dispose();

			return treeParser;
		}
	}
	


}
