import java.io.*;
import java.util.*;

public class BM25 {
	
	Map<String,HashMap<String,Integer>> invIndex=new HashMap<String,HashMap<String,Integer>>();
	Map<String, Integer> tokenCount=new HashMap<String, Integer>();
	Map<String,HashMap<String,Integer>> queIndex=new HashMap<String,HashMap<String,Integer>>();

	static int limit =0;
	Double fullCount=0.0;
	final Double k1=1.2;
	final Double k2=100.0;
	final Double b=0.75;
	
	public void readIndex(String path)
	{
		FileInputStream fis=null;
		ObjectInputStream ois=null;
		try 
		{
			fis=new FileInputStream(path);
			ois=new ObjectInputStream(fis);
			invIndex=(HashMap<String,HashMap<String,Integer>>)ois.readObject();
			tokenCount=(HashMap<String, Integer>)ois.readObject();
			
			for(String docID:tokenCount.keySet())
				fullCount+=tokenCount.get(docID);
		} 
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{try {ois.close();} catch (IOException e){e.printStackTrace();}}
	}
	
	public void readQuery(String path)
	{
		try 
		{
			File f=new File(path);
			Scanner in=new Scanner(f.getCanonicalFile());
			int queryID=0;
			while(in.hasNextLine())
			{
				String[] query=in.nextLine().split(" ");		
				computeScore(query,++queryID);
			}
			in.close();	
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	private void computeScore(String[] query,int id) 
	{
		Map<String, Double> bm25=new HashMap<String, Double>();
		Double N=tokenCount.size()*1.0;
		Double avgdl=fullCount/N;
		for(String word:query)
		{
			if(word.trim().equals(""))
				continue;
			
			HashMap<String,Integer> indexes=invIndex.get(word);
			Double docScore=0.0;
			Double qfi=0.0;
			for(String term:query)
				if(term.equals(word))
					qfi++;
			
			for(String docID:indexes.keySet()){
				Double nqi=indexes.size()*1.0;
				Double fqi=new Double(indexes.get(docID));
				Double idf=Math.log((N-nqi+0.5)/(nqi+0.5));
				Double D=new Double(tokenCount.get(docID));
				Double K=k1*(1-b+b*(D/avgdl));
				docScore=(idf) * (fqi*(k1+1)/(fqi+K)) * (qfi*(k2+1)/(k2+qfi));
				
				if(bm25.containsKey(docID))
					bm25.put(docID, bm25.get(docID)+docScore);
				else
					bm25.put(docID, docScore);
			}
		}
		sortandPrintScores(bm25,id);
		bm25.clear();
	}
	
	public void sortandPrintScores(Map<String, Double> bm25,int id)
	{
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(bm25.entrySet());

		Collections.sort(list,new Comparator<Map.Entry<String, Double>>(){
			public int compare(Map.Entry<String, Double> p1, Map.Entry<String, Double> p2) {
				return p2.getValue().compareTo(p1.getValue());
			}
		});
		
		writeOutput(list,id);
	}
	
	public void writeOutput(List<Map.Entry<String, Double>> list, int id)
	{
		int rank=0;
		for(Map.Entry<String, Double> item:list)
		{
			String line=id + " Q0 " + item.getKey() + " " + (++rank) + " " + item.getValue() + " " + "Corpora";
			System.out.println(line);
			if(rank==limit)
				break;
		}
	}
	


	public static void main(String[] args) {
		
		if(args.length!=3){
			System.out.println("Invalid Arguments for BM25"+args.length);
			return;
		}
		limit=new Integer(args[2]);
		BM25 bm=new BM25();
		System.out.println("Reading Inverted Index");
		bm.readIndex(System.getProperty("user.dir")+File.separator+args[0]);
		System.out.println("Reading Queries and Computing Scores");
		bm.readQuery(System.getProperty("user.dir")+File.separator+args[1]);
		System.out.println("Completed");
	}
}
