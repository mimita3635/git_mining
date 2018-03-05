package Tas_tas.git_mining;

import java.io.BufferedReader;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
//import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//import org.dstadler.jgit.helper.CookbookHelper;
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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import io.reflectoring.diffparser.api.DiffParser;
import io.reflectoring.diffparser.api.UnifiedDiffParser;
import io.reflectoring.diffparser.api.model.Diff;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import com.googlecode.javaewah.IteratorUtil;

/**
 * Simple snippet which shows how to retrieve the diffs
 * between two commits
 */
public class App {
	
	private static final String REMOTE_URL = "https://github.com/gingerswede/doris.git";
	
	public static BufferedReader reader;
	private static LinkedList<String> lineQueue = new LinkedList<>();

	private static int lineNumber = 0;

	private static List<Pattern> ignorePatterns = new ArrayList<>();

	private static boolean isEndOfStream = false;

    public static void cloneRepository() throws IOException {
        // prepare a new folder for the cloned repository
        File localPath = new File("C:\\Users\\user\\eclipse-workspace\\git_mining2\\New.git");
        //
        //FileUtils.deleteDirectory(new File(destination))
       /*
        if(!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }*/

        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .call()) {
	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
	        System.out.println("Having repository: " + result.getRepository().getDirectory());
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
    }

    public static Repository openRepository() throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        File F=new File("C:\\Users\\user\\AppData\\Local\\Temp\\TestGitRepository9027401615234628887\\.git");
        return builder.setGitDir(F)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
    }

    public static void main(String[] args) throws IOException, GitAPIException {
    	//cloneRepository();
        try (Repository repository = openRepository()) {
            FileOutputStream FS=new FileOutputStream("C:\\Users\\user\\Documents\\testJgit22.txt");
            
            try (Git git = new Git(repository)) {

                // compare older commit with the newer one, showing an addition
                // and 2 changes
                Iterable<RevCommit> commits = git.log().all().call();
                Iterator<RevCommit> it=commits.iterator();
               
                RevCommit Ra=it.next();
                while(it.hasNext()) {
                    RevCommit Rb=it.next();
                    String s="Diff Between "+Ra.getName()+" "+Rb.getName();
                    byte b[]=s.getBytes();
                    FS.write(b);
                    listDiff(repository, git,
                            Rb,
                            Ra,FS);
                    Ra=Rb;

                }

            }
            InputStream in = new FileInputStream("C:\\Users\\user\\Documents\\testJgit22.txt");
            //Reader unbufferedReader = new InputStreamReader(in);
            //reader = new BufferedReader(new InputStreamReader(in));
            Parser P= new Parser(in);
            P.get_diffs();
            //String Line=P.retLine();
            //System.out.println(Line);
            //P.addIgnorePattern(ignorePattern);
            /*DiffParser parser = new UnifiedDiffParser();
            InputStream in = new FileInputStream("C:\\Users\\user\\Documents\\testJgit.txt");
            List<Diff> diff = parser.parse(in);
            for(Diff D:diff) {
                System.out.println(D.getFromFileName());
                System.out.println(D.getToFileName());
            }*/
            /*
             * RevWalk walk = new RevWalk( repository );
			walk.sort( RevSort.COMMIT_TIME_DESC, true );
			walk.sort( RevSort.REVERSE , true );
			RevCommit commit = walk.next();
			while( commit != null ) {
  				// use commit
  				commit = walk.next();
			}
			walk.close();
             * */
         
        }
    }
    

    public void addIgnorePattern(String ignorePattern) {
        this.ignorePatterns.add(Pattern.compile(ignorePattern));
    }

    
    private static void listDiff(Repository repository, Git git, String oldCommit, String newCommit) throws GitAPIException, IOException {
        //git.diff().setOutputStream(System.out).call();
        //git.diff()
        /*final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(repository, oldCommit))
                .setNewTree(prepareTreeParser(repository, newCommit))
                .setContextLines(5)
                .call();
        System.out.println("Found: " + diffs.size() + " differences");
        for (DiffEntry diff : diffs) {
            
        }
        */
        try {
            
            FileOutputStream FS=new FileOutputStream("C:\\Users\\user\\Documents\\testJgit.txt");
            DiffFormatter df = new DiffFormatter (FS);
            
            df.setRepository( git.getRepository() );
            df.setContext(0);
            List<DiffEntry> entries = df.scan( prepareTreeParser(repository, oldCommit), prepareTreeParser(repository, newCommit) );
            df.format(entries);
            df.flush();
            df.close();
            
        }
        catch(Exception E) {
            E.printStackTrace();
        }
        /*for( DiffEntry entry : entries ) {
            System.out.println( "def"+entry+"abc" );
          }
          */

        //df.format(diffs);
        /*List<DiffEntry> entries = df.scan( oldTreeIter, newTreeIter );

        for( DiffEntry entry : entries ) {
          System.out.println( entry );
        }
         
        for (DiffEntry diff : diffs) {
            System.out.println("Diff: " + diff.getChangeType() + ": " +
                    (diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath() : diff.getOldPath() + " -> " + diff.getNewPath()));
        }
        */
    }
    private static void listDiff(Repository repository, Git git, RevCommit oldCommit, RevCommit newCommit, FileOutputStream FS) throws GitAPIException, IOException {
        //git.diff().setOutputStream(System.out).call();
        //git.diff()
        /*final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(repository, oldCommit))
                .setNewTree(prepareTreeParser(repository, newCommit))
                .setContextLines(5)
                .call();
        System.out.println("Found: " + diffs.size() + " differences");
        for (DiffEntry diff : diffs) {
            
        }
        */
        DiffFormatter df = new DiffFormatter (FS);
        
        df.setRepository( git.getRepository() );
        df.setContext(0);
        List<DiffEntry> entries = df.scan( prepareTreeParser(repository, oldCommit), prepareTreeParser(repository, newCommit) );
        df.format(entries);
        df.flush();
        df.close();
        /*for( DiffEntry entry : entries ) {
            System.out.println( "def"+entry+"abc" );
          }
          */

        //df.format(diffs);
        /*List<DiffEntry> entries = df.scan( oldTreeIter, newTreeIter );

        for( DiffEntry entry : entries ) {
          System.out.println( entry );
        }
         
        for (DiffEntry diff : diffs) {
            System.out.println("Diff: " + diff.getChangeType() + ": " +
                    (diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath() : diff.getOldPath() + " -> " + diff.getNewPath()));
        }
        */
    }
    private static AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        
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
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());
            /*for(RevCommit Rcommit:walk) {
            	
            }*/
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }
}
