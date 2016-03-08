
public class Aragog {
	public static void main(String[] args) {
		String seed = "https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher";
		String key = "concordance";
		Crawler simpleCrawler=new Crawler(seed);
		Crawler focussedCrawler=new Crawler(seed,key);
		Logger.log("Started Crawling...");
		simpleCrawler.crawlParent();
		focussedCrawler.crawlParent();
		Logger.log("End Crawling");
	}
}
