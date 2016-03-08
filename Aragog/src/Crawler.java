import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Crawler {
	
	public class Site {
		public String Url;
		public int level;
		
		public Site(String Url,int level)
		{
			this.Url=Url;
			this.level=level;
		}
	}
	
	Site seed;
	String key;
	final int level = 5;
	HashSet<String> allUrls = new HashSet<String>();
	HashSet<String> relUrls = new HashSet<String>();
	ArrayList<Site> frontier = new ArrayList<Site>();
	
	public Crawler(String seed, String key)
	{
		this.seed=new Site(seed.trim(), 1);
		this.key=key.trim().toLowerCase();
	}
	
	public Crawler(String seed)
	{
		this.seed=new Site(seed.trim(), 1);
		this.key="";
	}
	
	public void crawlParent()
	{
		frontier.add(seed);
		allUrls.add(seed.Url);
		try 
		{
			crawlChildren();
			logResults();
		} 
		catch (IOException e) {
			//e.printStackTrace();
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}
	int index=0;
	private void crawlChildren() throws IOException, InterruptedException {
		Document childDoc;
		long start=0,end=0,delay=0;
		while(relUrls.size()<1000 && !frontier.isEmpty())
		{
			Site child=frontier.get(index++);
			if(child.level>level)
				break;
			end=System.currentTimeMillis();
			if((delay=(end-start)/1000)<1000){
				Thread.sleep(1000-delay);
			}
			childDoc = Jsoup.connect(child.Url).timeout(0).get();
			start=System.currentTimeMillis();
			String linkBody = childDoc.body().text().toLowerCase();
			String canUrl=childDoc.head().select("link[rel=canonical]").attr("href");
			child.Url=child.Url.split("#")[0];
			canUrl=canUrl.split("#")[0];
			if((key=="" || child.level==1 || linkBody.indexOf(key) >= 0) && 
				!relUrls.contains(child.Url) && !relUrls.contains(canUrl))
			{
				relUrls.add(child.Url);
				relUrls.add(canUrl);
				System.out.println(child.level+": "+child.Url+ " : "+relUrls.size());
				Elements hrefs = childDoc.select("a[href]");
				for(Element href : hrefs)
				{
					String link= href.attr("href");
					String absLink="https://en.wikipedia.org"+link;
					absLink=absLink.split("#")[0];
					if(link.indexOf("/wiki/") == 0 && link.indexOf(':') < 0 &&
					   absLink.indexOf("/wiki/Main_Page") < 0 &&
					   !allUrls.contains(absLink) && 
					   !relUrls.contains(absLink))
					{
						allUrls.add(absLink);
						frontier.add(new Site(absLink,child.level+1));
					}
				}
			}
		}
	}
	
	private void logResults() {
		String header="Crawling ended at level - "+ frontier.get(index-1).level +
					  " with "+ relUrls.size()+
					  " relevant Urls after crawling a total of "+ index +
					  " Urls with key '" + key + "'.";
		String fileName="." + File.separator + "data" +File.separator + ((key=="")?"simple.txt":"focussed.txt");
		
		Logger.logFile(header, relUrls, fileName);
	}
}
