#### Objective
This repository creates a program that mines a given Java GitHub code repository. The program analyses all the commits in that repository and finds commits that have added a parameter to an existing method. 

#### Example
If there is a method named **foo(double x)** in a Java file, and a commit changes this method to be **foo(double x, String y)** then this code identifies the commit number that caused the change, and extracts the previous and current method signatures.
#### Input
URL of the github repository to be mined in `https://repo-name.git` format 
#### Output
The program generates 2 files.
1) **Logfile.txt** -> The intermediate git log file, that contains `git diff` between consecutive commits, in ascending order of time. 

2) **Method_Diff.csv** -> This is a csv file that contains the result, with commit-SHA1 in the 1st column, old method signature in the 2nd column and new method signature in the 3rd column. 

#### Compilation
The program can be run in 2 ways-
- The jar file with all associated dependencies git-mining.jar is uploaded in the repository. So after cloning the project, unzip the folder and open the directory in command-line.
    - To run on windows:
            `C:\> java -jar path\to\git-mining.jar`
	
    - To run on Unix-like OS:

	        $ java -jar git-mining.jar
- The project can also be build by importing in eclipse as maven project. And adding the dependencies in pom.xml file.

#### Working Procedure

- The program starts by asking the user for url of the git repository to be mined in valid format. After getting the git repository url the program clones the repository to the folder where the jar file is installed, using the repository name as the created folder name. 
- When acquiring the repository is finished, the program then creates a Logfile.txt containing diffs between all the consecutive commits in ascending order of time. 
-  From the Log file, the program then tries to match deleted and added lines of each commit for changed method signature using regular expression. And if such methods are found, it is saved in a data structure containing fields commit SHA-1 and old and new method signature.
-  The program might get stuck while matching a regular expression, due to [catastrophic backtracking]. In this cases, the program throws an exception for that specific input line and continues processing the next lines.
- After finishing parsing, the file outputs a csv file, method-diff.csv with old and new method signatures and associated commit-id.

#### Assumption

- Method names aren't changed between commits, only parameters are added to the existing methods. 
- Method names declarations are simple, they do not have annotations, or generic types in angular brackets. 
#### Limitations
- The program doesn't handle if parameters are deleted from a method, or parameter types are changed keeping the number of parameters same. For example the program doesn't handle te following cases.
    - *Old Signature* void foo(int a, in b)
        *New Signature* void foo(int a)
    - *Old Signature* void foo(int a)
         *New Signature* void foo(String a)
- The program doesn't handle parameter changes in constructors.
- As the cloning or mining part isn't multi-threaded, it takes a lot of time for the code to run on large repositories. It is specially cumbersome if internet connection is slow.
- As the repositories are downoaded, and the Logfile contains all the commits, they often require a lot of space.
- The program cannot handle comma seperated listings inside diamonds, treats them as normal comma seperated arguments.
- Throws runtime exception for [catastrophic backtracking]  cases.

#### Tested Repositories
The program has been tested on following popular java repositories.
- [Java Design Patterns]
- [Jodd]

#### Results
The output csv files are uploaded in this repository with following names
- *Method_Diff_jodd.csv*
- *Method_Diff_java_design_patterns.csv*
The Log.txt files couldn't be uploaded because of their size. 
 As it can be seen in the csv files, the program cannot handle multiple comma seperated types in angular brackets.
#### Resources
The following tools and sites has hbeen used or taken help from for developing this program.

* [JGit]-> An implementation of git version control system in pure java
    * For cloning the repository, creating log file from java code.
* [cookbook]-> A GitHub repository that provides examples and code snippets for the JGit Java Git implementation.
    * For being aquainted to using JGit, for implementing methods associated with JGit.
* [diffparser]-> A GitHub repository that parses unified diffs with Java.
    * For acquiring basic idea on processing a git diff file with the symbols.
* [doris] -> A GitHub repository for git mining.
    * For initial idea on Git Mining.Determining User interaction with program
* [stackoverflow]-> Question Answer website on diverse topics of computer science and programming
    * For troubleshooting problems, building concepts, ideas with java parsing and patterns     
    * This [answer] has specifically helped to deal with regular expression matching getting stuck.

#### Further Improvement

Use of Abstract Syntax Trees like [JavaParser] in stead of pattern matching with regular expression.
   [JGit]: <http://www.eclipse.org/jgit/>
   [cookbook]: <https://github.com/centic9/jgit-cookbook>
   [diffparser]: <https://github.com/thombergs/diffparser>
   [stackoverflow]: <https://stackoverflow.com/>
   [doris]: <https://github.com/gingerswede/doris>
   [catastrophic backtracking]: <http://www.regular-expressions.info/catastrophic.html>
   [Java Design Patterns]: <https://github.com/iluwatar/java-design-patterns.git>
   [Jodd]: <https://github.com/oblac/jodd.git>
   [answer]: <https://stackoverflow.com/a/11348374/9392839>
