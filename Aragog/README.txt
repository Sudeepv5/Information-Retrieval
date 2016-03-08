Project Structure:
	Aragog
	|_______Aragog.java
	|_______Crawler.java
	|_______Logger.java

Aragog.java: Main Class to handle the inputs(seed, key) and invoke focussed/simple crawler.
Crawler.java: Class to implement the crawling logic and to keep track of crawled links/relevant links.
Logger.java: Utility Class to handle print and file writting functions.

External Package Used: Jsoup-1.8.3

Directions to Compile and Run from Eclipse:

1. Set up Project [IMPORTANT] 
a. Open lib folder in Aragog->Rename jsoup-1.8.3.sudeep to jsoup-1.8.3.jar

2. Import the project to Eclipse
a. File->Import->General->Existing Projects into Workspace->Click Next
b. Check 'Select root directory'->Browse and Select Aragog folder->Click Finish

3. To start the Crawler, run the Aragog Class(Main) from IDE and after the crawling is completed the respective Urls are written to /data/simple.txt and /data/focussed.txt


P.S. The above process is tested on CCIS Linux machine using Eclipse IDE


Please contact me if you find any illegible content/problem while setting up the project.
Email: vaka.s@husky.neu.edu
Ph: 8579991605