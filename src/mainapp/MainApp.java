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
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatLightLaf;

public class MainApp {

	private static JFrame f = new JFrame();
	private static GridBagConstraints c;
	private static JTextArea status = new JTextArea();
	private static JCheckBox separateLanguageFolders;

	private static Scanner reader;
	private static ArrayList<LocPrinter> printers;
	private static File inputFolder;
	private static File modFolder;
	private static File outputFolder;

	public static void main(String[] args) {
		inputFolder = new File(
				System.getProperty("user.home") + "/Documents/Paradox Interactive/Europa Universalis IV/mod");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FlatLightLaf.install();
				createAndShowGUI();
			}
		});

	}

	private static String[] getModPaths() {
		ArrayList<String> modPaths = new ArrayList<String>();
		for (File f : inputFolder.listFiles()) {
			if (f.isDirectory()) {
				modPaths.add(f.getName());
			}
		}
		String[] result = new String[modPaths.size()];
		result = modPaths.toArray(result);
		if (!modPaths.isEmpty()) {
			status.setText(modPaths.get(0) + " Selected");
		}
		return result;
	}

	private static void createAndShowGUI() {
		c = new GridBagConstraints();

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setLayout(new BorderLayout());
		f.setTitle("EU4 Localisation Converter");

		JPanel topbar = new JPanel(new GridBagLayout());

		status.setEditable(false);

		// Select Mod Section
		JLabel selectModLabel = new JLabel("Select Mod: ");
		JComboBox<String> selectMod = new JComboBox<String>(getModPaths());
		selectMod.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				status.setText(selectMod.getSelectedItem().toString() + " Selected");
			}
		});

		// Output Directory Section
		JLabel selectOutputDirectoryLabel = new JLabel("Select Output Directory: ");
		JTextField selectOutputDirectory = new JTextField(
				System.getProperty("user.home") + "\\Desktop\\EU4 Localisation Converter Output\\");
		JButton openOutputDirectory = new JButton("Open Output Directory");
		openOutputDirectory.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Desktop.getDesktop().open(new File(selectOutputDirectory.getText()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// From Language Section
		JLabel selectFromLanguageLabel = new JLabel("Convert from: ");
		JComboBox<String> selectFromLanguage = new JComboBox<String>(
				new String[] { "English", "French", "German", "Spanish" });

		JLabel checkBoxLabel = new JLabel("To: ");
		JPanel checkboxArea = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JCheckBox englishCheckbox = new JCheckBox("English", true);
		JCheckBox frenchCheckbox = new JCheckBox("French", true);
		JCheckBox germanCheckbox = new JCheckBox("German", true);
		JCheckBox spanishCheckbox = new JCheckBox("Spanish", true);

		separateLanguageFolders = new JCheckBox("Use Separate Language Folders", false);

		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				long startTime = System.currentTimeMillis();
				modFolder = new File(inputFolder, selectMod.getSelectedItem() + "/localisation");
				outputFolder = new File(selectOutputDirectory.getText());
				emptyFolder(outputFolder);
				outputFolder.mkdir();
				ArrayList<String> langs = new ArrayList<String>();
				if (englishCheckbox.isSelected())
					langs.add("english");
				if (frenchCheckbox.isSelected())
					langs.add("french");
				if (germanCheckbox.isSelected())
					langs.add("german");
				if (spanishCheckbox.isSelected())
					langs.add("spanish");
				convert(modFolder, outputFolder, selectFromLanguage.getSelectedItem().toString().toLowerCase(), langs);
				updateStatus("\nCompleted in " + (System.currentTimeMillis() - startTime) + "ms");
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
		c.gridy = 1;
		topbar.add(selectOutputDirectoryLabel, c);

		c.weightx = 1;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 1;
		topbar.add(selectOutputDirectory, c);

		c.weightx = 1;
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridy = 1;
		topbar.add(openOutputDirectory, c);

		// Add Select Output Directory Section
		c.weightx = 0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		topbar.add(selectFromLanguageLabel, c);

		c.weightx = 1;
		c.gridwidth = 3;
		c.gridx = 1;
		c.gridy = 2;
		topbar.add(selectFromLanguage, c);

		// Add To Language Section
		c.weightx = 0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 3;
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
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 4;
		topbar.add(startButton, c);

		// Separate Folder Section
		c.gridwidth = 1;
		c.weightx = 0;
		c.gridx = 3;
		c.gridy = 4;
		topbar.add(separateLanguageFolders, c);

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

	private static void convert(File locDirectory, File outputDirectory, String convertFromLanguage,
			ArrayList<String> convertToLanguages) {
		updateStatus("\nMod Directory: " + locDirectory.getAbsolutePath());
		updateStatus("Output Directory: " + outputDirectory.getAbsolutePath() + "\n");
		updateStatus("Converting from: " + convertFromLanguage);
		updateStatus("Converting to: " + convertToLanguages.toString());
		printers = new ArrayList<LocPrinter>();
		for (String convertToLanguage : convertToLanguages) {
			printers.add(new LocPrinter(outputDirectory, convertToLanguage, separateLanguageFolders.isSelected()));
		}

		proccessDirectory("", convertFromLanguage);

	}

	private static void proccessDirectory(String directoryExtension, String convertFromLanguage) {
		File[] files = new File(modFolder, directoryExtension).listFiles();
		File currentDirectory = new File(outputFolder, directoryExtension);
		currentDirectory.mkdirs();
		for (File f : files) {
			if (f.isDirectory()) {
				proccessDirectory(directoryExtension + "/" + f.getName(), convertFromLanguage);
			} else {
				File file = new File(currentDirectory, f.getName());
				if (!file.getName().contains("_l_" + convertFromLanguage + ".yml"))
					continue;
				try {
					reader = new Scanner(f, "UTF-8");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				for (LocPrinter lp : printers) {
					lp.setCurrentFile(file);
					lp.printFirstLine();
				}
				reader.nextLine();
				while (reader.hasNextLine()) {
					String line = reader.nextLine();
					for (LocPrinter lp : printers) {
						lp.print(line);
					}
				}
				for (LocPrinter lp : printers) {
					lp.closeWriter();
				}

			}
		}
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

	private static void updateStatus(String message) {
		System.out.println(message);
		status.append(message + "\n");
	}

}