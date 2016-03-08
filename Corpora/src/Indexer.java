import java.io.*;
import java.util.*;

public class Indexer {
	
	Map<String,HashMap<String,Integer>> invIndex=new HashMap<String,HashMap<String,Integer>>();
	Map<String, Integer> tokenCount=new HashMap<String, Integer>();
	
	public void readCorpus(String path)
	{
		try 
		{
			File f=new File(path);
			Scanner in=new Scanner(f.getCanonicalFile());
			String docID="";
			while(in.hasNextLine())
			{
				String line=in.nextLine();
				if(line.indexOf("#")==0)
					docID=line.split(" ")[1];
				else
					processLine(line,docID);
			}
			in.close();	
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processLine(String line, String docID) {
		String[] words=line.split(" ");
		int count=0;
		for(String word:words)
		{
			if(!isNumber(word) && !word.equals(""))
			{
				HashMap<String, Integer> indexes;
				if(invIndex.containsKey(word))
				{
					indexes=invIndex.get(word);
					if(indexes.containsKey(docID))
						indexes.put(docID, (Integer)indexes.get(docID) + 1);
					else
						indexes.put(docID, 1);
				}
				else
				{
					indexes=new HashMap<String, Integer>();
					indexes.put(docID,1);
				}
				invIndex.put(word, indexes);
				count++;
			}
		}
		
		if(tokenCount.containsKey(docID))
			tokenCount.put(docID, tokenCount.get(docID) + count);
		else
			tokenCount.put(docID, count);
	}
	
	public static boolean isNumber(String str)  
	{  
	  try  {  Integer.parseInt(str);  }  
	  catch(NumberFormatException nfe)  {  return false;  }  
	  return true;  
	}
	
	public void writeFile(String fileName)
	{
		FileOutputStream fos=null;
		ObjectOutputStream oos=null;
		try 
		{
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos=new FileOutputStream(file);
			oos=new ObjectOutputStream(fos);
			oos.writeObject(invIndex);
			oos.writeObject(tokenCount);
		}
		catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {fos.close();oos.close(); } catch (IOException ignore) {}
		}
	}
	

	public static void main(String[] args) {
		if(args.length!=2){
			System.out.println("Invalid Arguments for Indexer");
			return;
		}
		Indexer in=new Indexer();
		System.out.println("Reading from File");
		in.readCorpus(System.getProperty("user.dir")+File.separator+args[0]);
		System.out.println("Index Created");
		System.out.println("Writting to File");
		in.writeFile(args[1]);
		System.out.println("Completed");
	}
}
