import java.io.File;

public class Boggle {
	
	public static void main(String[] args) {
		Ranker head=new Ranker();
		System.out.println("Loading File "+ args[0]+"...");
		head.loadPages("."+File.separator+args[0]);
		System.out.println("Initializing...");
		head.loadSinkPages();
		head.init();
		System.out.println("Calculating Ranks...");
		head.updateRanks();
		System.out.println("Writting to File...");
		head.sortAndWrite();
		System.out.println("Completed!");
	}
}
