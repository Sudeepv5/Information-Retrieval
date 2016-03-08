
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.jsoup.Jsoup;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class Indexer {
	//private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
	private static Analyzer sAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);

	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();
	private static Map<String,Long> termFrequencies = new HashMap<String,Long>();
	private static IndexReader reader=null;

	Indexer(String indexDir) throws IOException 
	{
		FSDirectory dir = FSDirectory.open(new File(indexDir));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,sAnalyzer);
		writer = new IndexWriter(dir, config);
	}

	public static void main(String[] args) throws IOException 
	{
		System.out.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");
		String indexLocation = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();

		Indexer indexer = null;
		try {
			indexLocation = s;
			indexer = new Indexer(s);
		} catch (Exception ex) {
			System.out.println("Cannot create index..." + ex.getMessage());
			System.exit(-1);
		}

		//1. Creating Index
		try 
		{
			System.out.println("Enter the FULL path to add into the index (q=quit): (e.g. /home/mydir/docs or c:\\Users\\mydir\\docs)");
			System.out.println("[Acceptable file types: .xml, .html, .html, .txt]");
			s = br.readLine();

			// try to add file into the index
			System.out.println("Started Indexing");
			indexer.indexFileOrDirectory(s);
		} catch (Exception e) {
			System.out.println("Error indexing " + s + " : "+ e.getMessage());
		}

		indexer.closeIndex();

		reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));

		//2. Get term frequencies
		indexer.buildTermFreqs(indexLocation);

		//3. Read Queries and Score the docs
		indexer.readQueries();
	}

	private void readQueries() 
	{
		Scanner in=null;
		try 
		{
			File f=new File("./queries.txt");
			in=new Scanner(f.getCanonicalFile());
			int queryID=0;
			while(in.hasNextLine())
			{
				String query=in.nextLine();		
				computeScore(query,++queryID);
			}

		} 
		catch (Exception e) {System.out.println("Error reading Queries"); e.printStackTrace();}
		finally
		{
			in.close();
		}

	}

	private void computeScore(String query, int id) throws Exception 
	{

		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
		Query q = new QueryParser(Version.LUCENE_47, "contents",sAnalyzer).parse(query);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("Stats for Query "+id+" --> "+ query);
		System.out.println("ID\tDocID\tScore\t\tPreview");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			String line=(i + 1) + " " +d.get("filename") +" " + hits[i].score;
			System.out.println(line);
		}
		System.out.println("------------------------------------------------------------");
	}

	private String docPreview(Document d) {
		String content=d.get("contents");
		return (content.length()>20 ? content.substring(0, 19) :content);
	}

	private void buildTermFreqs(String indexLocation) throws IOException  {

		Terms terms = MultiFields.getFields(reader).terms("contents");
		TermsEnum teItr=terms.iterator(null);
		BytesRef term=null;
		Long total=0L;
		while((term=teItr.next())!=null)
		{
			String word=term.utf8ToString();
			Long count=reader.totalTermFreq(new Term("contents",term));
			total+=count;
			termFrequencies.put(word,count);
		}

		List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(termFrequencies.entrySet());

		Collections.sort(list,new Comparator<Map.Entry<String, Long>>(){
			public int compare(Map.Entry<String, Long> p1, Map.Entry<String, Long> p2) {
				return p2.getValue().compareTo(p1.getValue());
			}
		});	
		int i=0;
		Long prev=0L;
		FileWriter fr=new FileWriter(new File("./frequencies.txt"));
		fr.write("Rank\tTerm\tFrequeny" + System.lineSeparator());
		for(Map.Entry<String, Long> ent: list){
			//fr.write(ent.getKey()+":\t"+ent.getValue()+ System.lineSeparator());
			if(prev!=ent.getValue())
				i++;
			fr.write(i+"\t"+ent.getKey()+"\t"+(ent.getValue()) +System.lineSeparator());
			prev=ent.getValue();
		}

		fr.close();
		System.out.println("Term Frequencies written to frequencies.txt");
	}

	public void indexFileOrDirectory(String fileName) throws IOException 
	{
		addFiles(new File(fileName));
		int originalNumDocs = writer.numDocs();
		for (File f : queue) {
			try {
				Document doc = new Document();

				String fileContent=Jsoup.parse(f,null).text();
				doc.add(new TextField("contents", fileContent,Field.Store.YES));
				doc.add(new StringField("path", f.getPath(), Field.Store.YES));
				doc.add(new StringField("filename", f.getName(),Field.Store.YES));

				writer.addDocument(doc);
				//System.out.println("Added: " + f);

			} catch (Exception e) {
				System.out.println("Could not add: " + f);
			} finally {

			}
		}

		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println((newNumDocs - originalNumDocs) + " documents indexed");

		queue.clear();
	}

	private void addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		}
		else {
			String filename = file.getName().toLowerCase();
			if (filename.endsWith(".htm") || filename.endsWith(".html")
					|| filename.endsWith(".xml") || filename.endsWith(".txt")) {
				queue.add(file);
			} 
			else {
				System.out.println("Skipped " + filename);
			}
		}
	}

	public void closeIndex() throws IOException {
		writer.close();
	}
}
