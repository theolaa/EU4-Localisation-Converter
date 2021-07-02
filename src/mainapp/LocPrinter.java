package mainapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class LocPrinter {
	private String language;
	private File baseDir;
	private OutputStreamWriter writer;
	private boolean separateFolder;
	public LocPrinter(File baseDir, String language, boolean separateFolder) {
		this.baseDir = baseDir;
		this.language = language;
		this.separateFolder = separateFolder;
	}
	
	public void setCurrentFile(File f) {
		String pathname;
		if (separateFolder) {
			if (f.getAbsolutePath().contains(File.separator + "replace" + File.separator)) {
				pathname = f.getParentFile().getAbsolutePath();
			} else {
				pathname = baseDir.getAbsolutePath() + "/" + language + "/";
			}
		} else {
			 pathname = f.getParentFile().getAbsolutePath();
		}
		String filename = f.getName().replaceFirst("_l_.+\\.", "_l_" + language + ".");
		File folderToWrite = new File(pathname);
		folderToWrite.mkdirs();
		File fileToWrite = new File(pathname + "/" + filename);
		try {
			writer = new OutputStreamWriter(
				     new FileOutputStream(fileToWrite),
				     Charset.forName("UTF-8").newEncoder()
				 );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printFirstLine() {
		try {
			writer.write("\ufeff");
			writer.write("l_"+language+":" + System.lineSeparator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void print(String line) {
		try {
			writer.write(line + System.lineSeparator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}