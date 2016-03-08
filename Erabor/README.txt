Project Structure:

Erabor
  |_______Evaluator.java


Evaluator.java: Reads the relevance judgements(cacm.txt) and Lucene ranking scores(scores.txt) to calculate the metrics.


After the program execution, the metrics(Precision, Recall, nDCG) are printed for each document in the result set. MAP and P@20 values is printed at the bottom.

