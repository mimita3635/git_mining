package Tas_tas.git_mining;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class Repository_Handler {

	public static BufferedReader reader;
	
	public static File cloneRepository(String REMOTE_URL) throws IOException {
		// prepare a new folder for the cloned repository
		System.out.println(System.getProperty("user.dir"));
		String LocalPath = System.getProperty("user.dir") + "\\"
				+ REMOTE_URL.substring(REMOTE_URL.lastIndexOf("/") + 1, REMOTE_URL.lastIndexOf(".git"));
		System.out.println(LocalPath);

		File localPath = new File(LocalPath);
		System.out.println(localPath.toString());
		//
		if (localPath.exists())
			FileUtils.deleteDirectory(localPath);
		/*
		 * if(!localPath.delete()) { throw new
		 * IOException("Could not delete temporary file " + localPath); }
		 */

		// then clone
		System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
		
		try (Git result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(localPath).call()) {
			// Note: the call() returns an opened repository already which needs to be
			// closed to avoid file handle leaks!
			File F=result.getRepository().getDirectory();
			System.out.println("Having repository: " + F);
			//result.close();
			return F;
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			//result.close();
		}
		return null;
	}

	public static Repository openRepository(File F) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		// File F=new File();
		return builder.setGitDir(F).readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
	}


}
