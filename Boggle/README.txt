Project Structure:

Boogle
  |_______Boggle.java
  |_______Ranker.java


Boggle.java: the Driver class, acts as an user interface and takes the file name
Ranker.java: Handles the whole file reading, ranking the documents and printing them.

Instructions to run: 

1. Import the project into eclipse
2. Add input arguments in Run->Run Configurations->Select Arguments Tab->Enter file name in Program arguments field->Click Apply and then Run

After the program execution, all the pages and their correspoding ranks are written to final.txt

Hand ins:
1. The Page rank values for the given six-node example after 1, 10, 100 Iterations are written in : q1.txt
2. The List of perplexity values till the page ranks are converged for the wt2g collection: q2.txt
3. Top 50 pages from wt2g sorted by page rank: Top50PR.txt
4. Top 50 pages from wt2g sorted by inLink Count: Top50InLink.txt
5. The proportions are given in proportions.txt
6. Analysis and comparision is given in analysis.pdf
7. Final list of pages and page ranks i.e output of the whole program is written to final.txt



