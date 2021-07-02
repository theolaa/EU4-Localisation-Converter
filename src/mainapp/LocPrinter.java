package mainapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LocPrinter {
	private String language;
	private File baseDir;
	private FileWriter fw;
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
			fw = new FileWriter(fileToWrite);
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
			fw.write("l_"+language+":" + System.lineSeparator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void print(String line) {
		try {
			fw.write(line + System.lineSeparator());
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