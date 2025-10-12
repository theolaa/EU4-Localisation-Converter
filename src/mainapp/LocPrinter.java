package mainapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class LocPrinter {
    private final String language;
    private final File baseDir;
    private OutputStreamWriter writer;

    public LocPrinter(File baseDir, String language) {
        this.baseDir = baseDir;
        this.language = language;
    }

    public void setCurrentFile(String dirExtension, File sourceFile) {
        new File(baseDir, dirExtension).mkdirs();
        String name = sourceFile.getName();
        name = name.replaceFirst("_l_.*\\.yml", "_l_" + language + ".yml");
        File destFile = new File(baseDir, dirExtension + "/" + name);
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream(destFile),
                    StandardCharsets.UTF_8.newEncoder()
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void printFirstLine() {
        try {
            writer.write("\ufeff");
            writer.write("l_" + language + ":" + System.lineSeparator());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void print(String line) {
        try {
            writer.write(line);
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

    public String getLanguage() {
        return language;
    }
}