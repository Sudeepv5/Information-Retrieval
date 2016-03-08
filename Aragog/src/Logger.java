import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Logger {
	
	public static void log(String dump)
	{
		System.out.println(dump);
	}
	
	public static void log(List<Object> dumps)
	{
		int i=1;
		for(Object d : dumps)
		{
			System.out.println((i++) +". "+d);
		}
	}
	
	public static void logFile(String header, Set<String> list, String fileName)
	{
		FileWriter fw = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile());
			fw.write(header+System.lineSeparator());
			for(String Url : list){
				fw.write(Url);
				fw.write(System.lineSeparator());
			}	
			Logger.log("Written to file");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			try { fw.close(); } catch (IOException ignore) {}
		}
	}

}
