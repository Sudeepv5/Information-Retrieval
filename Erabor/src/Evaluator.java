import java.io.File;
import java.text.DecimalFormat;
import java.util.*;


public class Evaluator {
	
	private class Document
	{
		String docID;
		Boolean relevant;
		
		Document(String docID,Boolean rel)
		{
			this.docID=docID;
			this.relevant=rel;
		}
	}
	
	Map<String,HashMap<String,Document>> relJudge=new HashMap<String, HashMap<String,Document>>();
	
	public void readRelevance(String filename)
	{
		Scanner in=null;
		try 
		{
			File f=new File(filename);
			in=new Scanner(f.getCanonicalFile());
			while(in.hasNextLine())
			{
				String[] line=in.nextLine().split(" ");
				String queryID=line[0];
				String docID=line[2];
				Boolean rel=(line[3].equals("1"))?true:false;
				if(!rel)
					continue;
				HashMap<String,Document> docs=null;
				if(relJudge.containsKey(queryID))
					docs=relJudge.get(queryID);
				else
					docs=new HashMap<String,Document>();
				docs.put(docID, new Document(docID,rel));
				relJudge.put(queryID, docs);
			}
		} 
		catch (Exception e) {System.out.println("Error reading Relevance Judgements"); e.printStackTrace();}
		finally
		{
			in.close();
		}
	}
	
	public void readScores(String filename)
	{
		Scanner in=null;
		try 
		{
			File f=new File(filename);
			in=new Scanner(f.getCanonicalFile());
			Double relevant=0.0,retrieved=0.0,totalRel=0.0,sumPrecision=0.0,map=0.0;
			int queryCount=0;
			int rLevel=0;
			HashMap<String,Document> docs=null;
			List<Double> iDCG=new ArrayList<Double>();
			Double dcg=0.0;
			DecimalFormat df = new DecimalFormat("#.#####");
			while(in.hasNextLine())
			{
				String[] line=in.nextLine().split(" ");
				if(line[0].equals("#"))
				{
					queryCount++;
					docs=relJudge.get(line[1]);
					
					if(relevant!=0)
						map+=sumPrecision/relevant;
					sumPrecision=0.0;
					relevant=0.0;
					retrieved=0.0;
					dcg=0.0;
					totalRel=(double) docs.values().size();
					iDCG.clear();
					iDCG.add(1.0);
					int rel=0;
					for(int i=1;i<=100;i++)
					{
						rel=(i<totalRel)?1:0;
						iDCG.add(iDCG.get(i-1)+(rel*Math.log(2.0)/Math.log(i+1)));
					}
					System.out.println("Relevance Judgements for Query ID: "+line[1]);
					System.out.println("Rank\tDocID\t\tDoc Score\tRelevance\tPrecision\tRecall\t\tnCDG");
				}
				else
				{
					int rank=new Integer(line[0]);
					rLevel=0;
					retrieved++;
					if(docs.containsKey(line[1])){
						relevant++;
						rLevel=1;
						sumPrecision+=relevant/retrieved;
					}
					
					String pk=(rank==20)?"(P@20)":"";
					String precision=df.format(relevant/retrieved)+pk;
					String recall=df.format(relevant/totalRel);
					
					if(rank==1)
						dcg=1.0;
					else
						dcg+=rLevel*Math.log(2.0)/Math.log(rank);
					
					String nDCG=df.format(dcg/iDCG.get(rank-1));
					
					String buff=rank+"\t"+line[1]+"\t"+line[2]+"\t"+rLevel+"\t\t"+precision+"\t\t"+recall+"\t\t"+nDCG;
					System.out.println(buff);
				}
			}
			map+=sumPrecision/relevant;
			
			System.out.println("MAP Value: "+(map/queryCount));
		} 
		catch (Exception e) {System.out.println("Error reading Scores"); e.printStackTrace();}
		finally
		{
			in.close();
		}
	}
	
	
	public static void main(String[] args) {
		Evaluator eval=new Evaluator();
		System.out.println("Reading Relevance Judgements..");
		eval.readRelevance("./cacm.txt");
		System.out.println("Reading Document Scores and Evaluating..");
		eval.readScores("./scores.txt");
	}
	
	

}
