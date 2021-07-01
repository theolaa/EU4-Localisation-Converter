package mainapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LocPrinter {
	private String language;
	private FileWriter fw;
	public LocPrinter(String language) {
		this.language = language;
	}
	
	public void setCurrentFile(File f) {
		String pathname = f.getAbsolutePath();
		pathname = pathname.replaceFirst("_l_.+\\.", "_l_" + language + ".");
		f = new File(pathname);
		try {
			fw = new FileWriter(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printFirstLine() {
		try {
			fw.write(0xef);
			fw.write(0xbb);
			fw.write(0xbf);
			fw.write("l_"+language+":\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void print(String line) {
		try {
			fw.write(line + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeWriter() {
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}