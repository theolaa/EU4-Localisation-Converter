package mainapp;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import com.formdev.flatlaf.FlatLightLaf;

public class MainApp {

    private static final JFrame f = new JFrame();
    private static final GridBagConstraints c = new GridBagConstraints();
    private static final JTextArea status = new JTextArea();
    private static JButton startButton;

    private static Scanner reader;
    private static ArrayList<LocPrinter> printers;
    private static File modsFolder;
    private static File localisationFolder;

    public static void main(String[] args) {
        modsFolder = new File(
                FileSystemView.getFileSystemView().getDefaultDirectory().getPath(), "Paradox Interactive/Europa Universalis IV/mod");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FlatLightLaf.install();
                createAndShowGUI();
            }
        });

    }

    private static String[] getModPaths() {
        ArrayList<String> modPaths = new ArrayList<String>();
        for (File f : modsFolder.listFiles()) {
            if (f.isDirectory()) {
                modPaths.add(f.getName());
            }
        }
        String[] result = new String[modPaths.size()];
        result = modPaths.toArray(result);
        if (!modPaths.isEmpty()) {
            status.setText("");
            updateStatus(modPaths.getFirst() + " Selected");
        } else {
            updateStatus("EU4 Mod Directory Not Found");
            startButton.setEnabled(false);
        }
        return result;
    }

    private static void createAndShowGUI() {

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());
        f.setTitle("EU4 Localisation Converter");

        JPanel topbar = new JPanel(new GridBagLayout());

        status.setEditable(false);

        startButton = new JButton("Start");

        // Select Mod Section
        JLabel selectModLabel = new JLabel("Select Mod: ");
        JComboBox<String> selectMod = new JComboBox<String>(getModPaths());
        selectMod.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("");
                updateStatus(selectMod.getSelectedItem().toString() + " Selected");
            }
        });

        // From Language Section
        JLabel selectFromLanguageLabel = new JLabel("Convert from: ");
        JComboBox<String> selectFromLanguage = new JComboBox<String>(
                new String[]{"English", "French", "German", "Spanish"});

        JLabel checkBoxLabel = new JLabel("To: ");
        JPanel checkboxArea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox englishCheckbox = new JCheckBox("English", true);
        JCheckBox frenchCheckbox = new JCheckBox("French", true);
        JCheckBox germanCheckbox = new JCheckBox("German", true);
        JCheckBox spanishCheckbox = new JCheckBox("Spanish", true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                long startTime = System.currentTimeMillis();
                localisationFolder = new File(modsFolder, selectMod.getSelectedItem().toString() + "/localisation");
                ArrayList<String> langs = new ArrayList<String>();
                if (englishCheckbox.isSelected())
                    langs.add("english");
                if (frenchCheckbox.isSelected())
                    langs.add("french");
                if (germanCheckbox.isSelected())
                    langs.add("german");
                if (spanishCheckbox.isSelected())
                    langs.add("spanish");
                String convertFromLanguage = selectFromLanguage.getSelectedItem().toString().toLowerCase();
                langs.remove(convertFromLanguage);
                convert(localisationFolder, convertFromLanguage, langs);
                updateStatus("Completed in " + (System.currentTimeMillis() - startTime) + "ms");
                updateStatus("\n============================================================");
            }
        });

        JScrollPane scrollPane = new JScrollPane(status);

        c.insets = new Insets(5, 15, 5, 15);
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;

        // Add Select Mod Section
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        topbar.add(selectModLabel, c);

        c.weightx = 1;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 0;
        topbar.add(selectMod, c);

        // Add Select Output Directory Section
        c.weightx = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 3;
        topbar.add(selectFromLanguageLabel, c);

        c.weightx = 1;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 3;
        topbar.add(selectFromLanguage, c);

        // Add To Language Section
        c.weightx = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 4;
        topbar.add(checkBoxLabel, c);

        // Add Checkboxes
        checkboxArea.add(englishCheckbox);
        checkboxArea.add(frenchCheckbox);
        checkboxArea.add(germanCheckbox);
        checkboxArea.add(spanishCheckbox);
        c.gridx = 1;
        c.weightx = 1;
        topbar.add(checkboxArea, c);

        // Add Start Button
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 5;
        topbar.add(startButton, c);

        // Separate Folder Section
        c.gridwidth = 1;
        c.weightx = 0;
        c.gridx = 3;
        c.gridy = 5;
//		topbar.add(separateLanguageFolders, c);

        // Controls
        f.add(topbar, BorderLayout.PAGE_START);

        // Status Bar
        f.add(scrollPane, BorderLayout.CENTER);

        f.setPreferredSize(new Dimension(800, 600));
        f.setMinimumSize(new Dimension(500, 300));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private static void convert(File locDirectory, String convertFromLanguage,
                                ArrayList<String> convertToLanguages) {
        updateStatus("\nLocalisation Directory: " + locDirectory.getAbsolutePath());
        updateStatus("Converting from: " + convertFromLanguage);
        updateStatus("Converting to: " + convertToLanguages.toString() + "\n");
        printers = new ArrayList<LocPrinter>();
        for (String convertToLanguage : convertToLanguages) {
            printers.add(new LocPrinter(localisationFolder, convertToLanguage));
        }

        cleanOtherLanguages(locDirectory, convertFromLanguage, convertToLanguages);
        processLocalisation("", convertFromLanguage, printers, false);

    }

    /**
     * Copies creates copies of existing localisation files in the chosen languages into the same directory in which they are found.
     *
     * @param directoryExtension  The current working folder, composed with the `outputFolder`
     * @param convertFromLanguage The language that is being converted from
     * @param printers            The list of LocPrinters that will copy the loc files to each language
     */
    private static void processLocalisation(String directoryExtension, String convertFromLanguage, ArrayList<LocPrinter> printers, boolean isLanguageFolder) {
        updateStatus("\nProcessing: " + directoryExtension);
        File currentDirectory = new File(localisationFolder, directoryExtension);
        File[] files = currentDirectory.listFiles();
        if (files == null) {
            updateStatus("No Localisation Files Found");
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                if (f.getName().equals(convertFromLanguage)) {
                    processLocalisation(directoryExtension + "/" + f.getName(), convertFromLanguage, printers, true);
                } else {
                    processLocalisation(directoryExtension + "/" + f.getName(), convertFromLanguage, printers, isLanguageFolder);
                }
            } else {
                if (!f.getName().contains(".yml")) {
                    updateStatus("Ignoring " + f.getName());
                    continue;
                }
                updateStatus("Copying " + f.getName());
                try {
                    reader = new Scanner(f, StandardCharsets.UTF_8);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (LocPrinter lp : printers) {
                    if (isLanguageFolder) {
                        String printerLang = lp.getLanguage();
                        String newDirExtension = directoryExtension.replace(convertFromLanguage, printerLang);
                        lp.setCurrentFile(newDirExtension, f);
                    } else {
                        lp.setCurrentFile(directoryExtension, f);
                    }
                    lp.printFirstLine();
                }
                reader.nextLine();
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    for (LocPrinter lp : printers) {
                        lp.print(line + (reader.hasNext() ? System.lineSeparator() : ""));
                    }
                }
                for (LocPrinter lp : printers) {
                    lp.closeWriter();
                }
                reader.close();
            }
        }
        updateStatus("\nFinished Processing: " + directoryExtension);
        updateStatus();
    }

    private static void emptyFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    emptyFolder(f);
                } else {
                    f.delete();
                }
            }
            folder.delete();
        }
    }


    private static void cleanOtherLanguages(File locDirectory, String convertFromLanguage, ArrayList<String> convertToLanguages) {
        // Delete other language folders
        for (String lang : convertToLanguages) {
            emptyFolder(new File(locDirectory, lang));
        }
        cleanOtherLanguages(locDirectory, convertFromLanguage);
    }

    public static void cleanOtherLanguages(File dir, String convertFromLanguage) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                cleanOtherLanguages(f, convertFromLanguage);
            } else {
                String name = f.getName();
                if (name.contains(".yml") && !name.contains("_l_" + convertFromLanguage)) {
                    f.delete();
                }
            }
        }
    }

    private static void updateStatus() {
        System.out.println();
        status.append("\n");
    }

    private static void updateStatus(String message) {
        System.out.println(message);
        status.append(message + "\n");
    }

}