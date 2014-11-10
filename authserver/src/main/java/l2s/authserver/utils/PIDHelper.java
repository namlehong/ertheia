package l2s.authserver.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class PIDHelper {

	public static String getPID()
	{
		String pidString = ManagementFactory.getRuntimeMXBean().getName();
		return pidString.split("@")[0];
	}

	public static void writePID()
	{
		try {
			 
			String pid = getPID();
	
			File file = new File("l2as.pid");
	
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
	
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(pid);
			bw.close();
	
			System.out.println("Write PID:"+pid);
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
