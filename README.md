-----------------Objective---------------------------
=========================
This repository creates a program that mines a given Java GitHub code repository. The program analyses all the commits in that repository and finds commits that have added a parameter to an existing method. 

------------------Example----------------------------------
=================================
If there is a method named **foo(double x)** in a Java file, and a commit changes this method to be **foo(double x, String y)** then this code identifies the commit number that caused the change, and extracts the previous and current method signatures.

---------------------Output---------------------------------
=============================
The program generates 2 files.
1) **Logfile.txt** -> The intermediate git log file, that contains git diff between consecutive commits, in ascending ordr of time. 

2) **Method_Diff.csv** -> This is a csv file that contains the result, with commit-SHA1 in the 1st column, old method signature in the 2nd column and new method signature in the 3rd column 

-----------------------Compilation--------------------
===================
-----------------------Tested Repositories--------------------------
===============================

------------------------Results---------------------------------------
=============================
