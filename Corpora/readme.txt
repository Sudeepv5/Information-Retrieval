Project Structure:

Corpora
  |_______Indexer.java
  |_______BM25.java


Indexer.java: Reads the corpus file and creates the inverted index for all words into an output file.
BM25.java: Reads the inverted index file, query file and ranks all the documents with ranks sorted by relevance to the queries.

Instructions to run from cmd: 

1. Cd to the directory "/Corpora/src/" - in which has the above mentioned files
2. Make sure it has the required files - tccorpus.txt, queries.txt
3. Compile the Indexer.java file using javac command 	>javac Indexer.java
4. Run the Indexer with >java Indexer tccorpus.txt index.out
5. Compile the BM.java file using javac command 	>javac BM25.java	(Ignore the warnings!)
6. Run the BM25 with >java BM25 index.out queries.txt 100 > results.eval
7. The scores can be found in the output results.eval file


After the program execution, inverted index is saved in index.out as an Object. The BM25 scores are written to the file results.eval


Hand ins:
1. BM25 scores for the relevant 100 documents for each query are written to results.eval
2. Explaination for this assignment is written in implementation.txt


Please contact me if you find any illegible content/problem while executing the project.
Email: vaka.s@husky.neu.edu
Ph: 8579991605
