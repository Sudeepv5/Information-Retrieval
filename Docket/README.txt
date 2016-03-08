Project Structure:

Docket
  |_______Indexer.java


Indexer.java: Reads and Indexes the CACM dump files from the given directory(./cacm/), Ranks the documents for the gievn queries file(./queries.txt)

Instructions to run from Eclipse: 

1. Change the extension of files in ./lib folder from .sudeep to .jar
2. Open the project in Eclipse and Run
3. Give a path to save the index (Eg. ./dict) -> Press Enter
4. Give the folder path which has the files to be indexed ./cacm/ ->Enter


After the program execution, index is saved to ./dict folder. Term frequencies are written to frequencies.txt and top 100 hits for the given 4 queries retreived are printed to console.


Hand ins:
1. Term frequencies are written to frquencies.txt
2. Zipf's plot is given in zipf.xlsx
3. List of Top 100 hits for each query are present in top100.txt and also written to console on execution of project
4. Lucene scoring Vs BM25 scoring stats are given in comparision.txt


Please contact me if you find any illegible content/problem while executing the project.
Email: vaka.s@husky.neu.edu
Ph: 8579991605
