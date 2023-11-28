import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

public class WordCounterApp extends JFrame {

    /*
    To preserve the style when pasting formatted text into the JTextArea
     use the StyledDocument interface along with a JTextPane
     The JTextPane allows you to work with styled text
      including preserving formatting when pasting
    */

    private JTextPane textPane = new JTextPane(); //for input

    private JCheckBox spaceCheckBox = new JCheckBox("Include Spaces");
    private StyledDocument styledDocument = textPane.getStyledDocument();
    private Style regularStyle = styledDocument.addStyle("Regular", null);
    private Style boldStyle = styledDocument.addStyle("Bold", null);
    private Style italicStyle = styledDocument.addStyle("Italic", null);

    private JLabel wordCountLabel = new JLabel("Words: 0"); //to display word count
    private JLabel charCountLabel = new JLabel("Characters: 0");
   // private JLabel paragraphCountLabel = new JLabel("Paragraphs: 0");


    public WordCounterApp() {
        //set up the menu bar
        createMenuBar();


         /*
          set the layout for the frame. Use BorderLayout to arrange the components.
          Add the JTextArea, JLabels, and the JCheckBox to the frame.
         */
        setLayout(new BorderLayout());
        add(new JScrollPane(textPane), BorderLayout.CENTER); // Use a JScrollPane for the JTextArea
        // Create a JPanel for labels
        JPanel labelPanel = new JPanel(new GridLayout(3, 1));
        labelPanel.add(wordCountLabel);
        labelPanel.add(charCountLabel);
        //labelPanel.add(paragraphCountLabel);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(spaceCheckBox, BorderLayout.EAST);
        southPanel.add(labelPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);


        //To update the word count live as the user types or deletes text
        // Add a DocumentListener to the JTextArea
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                countWords();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                countWords();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                countWords();
            }
        });
        // Initialize styles
        StyleConstants.setFontFamily(regularStyle, "SansSerif");
        StyleConstants.setFontSize(regularStyle, 12);
        StyleConstants.setBold(boldStyle, true);
        StyleConstants.setItalic(italicStyle, true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setVisible(true);

    }

    /*
    This method calculates and updates the word, character and paragraph
     */
    private void countWords() {
        // Implement word counting logic here
        String text = textPane.getText();
        // Check if the text is empty
        if (text.trim().isEmpty()) {
            // If text is empty, reset all counts to 0
            wordCountLabel.setText("Words: 0");
            charCountLabel.setText("Characters: 0");
            //paragraphCountLabel.setText("Paragraphs: 0");
            return;
        }

        // Check the state of the space checkbox
        boolean includeSpaces = spaceCheckBox.isSelected();

        // Replace spaces if they should be excluded
        if (!includeSpaces) {
            // Remove all spaces
            text = text.replaceAll("\\s+", " ");
            // Replace space before a new line with an empty string
            text = text.replaceAll("\\n", "\n");
        }

        String[] words = text.split("\\s+");
        int wordCount = words.length;

        // Update charCount to exclude spaces when includeSpaces is false
        int charCount;
        if (includeSpaces) {
            charCount = text.length();
        } else {
            charCount = text.replaceAll("\\s+", "").length();
        }

       /*
       // Update paragraphCount to count paragraphs correctly when includeSpaces is false
        String[] paragraphs;
        int paragraphCount;
        if (includeSpaces) {
            paragraphs = text.split("\n\n");
            paragraphCount = paragraphs.length;
        } else {
            paragraphs = text.split("\n\n");
            paragraphCount = paragraphs.length;
        }

        */

        // Update labels with counts
        wordCountLabel.setText("Words: " + wordCount);
        charCountLabel.setText("Characters: " + charCount);
        //paragraphCountLabel.setText("Paragraphs: " + paragraphCount);
    }
    /*
    This method sets up the menu bar and adds "File" menu with "New," and "Open," options.
    It attaches accelerators for keyboard shortcuts.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        /*
        Defines menu items for "New," and "Open,"
         They are added to the "File" menu with corresponding accelerators.
         Action listeners are attached to perform specific actions when menu items are clicked.
         */

        //the accelerator Ctrl + N is associated with the "New" menu item
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newMenuItem.addActionListener(e -> {
            textPane.setText("");
            resetLabels();
        });
        //the accelerator Ctrl + O is associated with the "Open" menu item
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        openMenuItem.addActionListener(e -> openFile());

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
    }

    private void resetLabels() {
        wordCountLabel.setText("Words: 0");
        charCountLabel.setText("Characters: 0");
       // paragraphCountLabel.setText("Paragraphs: 0");
    }
    /*
    The open method handles the functionality for opening files.
    They use JFileChooser to interact with the file system and perform file I/O operations.
    try { ... } catch (IOException e) { ... }:
    Attempts to open and read the selected file.
    If successful, creates a BufferedReader to read the file line by line.
    Appends each line to a StringBuilder (content) to reconstruct the entire file's content.
    Closes the BufferedReader when done.
    Catch (IOException e) { ... }:
    Handles any IOException that might occur during file reading.
    Prints the exception stack trace.
    Displays an error message dialog using JOptionPane if an error occurs.
     */
    private void openFile() {
        // create a file dialog
        JFileChooser fileChooser = new JFileChooser();

        // Add a filter to only allow TXT files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        // Check if user selected Open
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Check the file extension
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

            if ("txt".equalsIgnoreCase(extension)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    reader.close();

                    // Clear existing text
                    textPane.setText("");

                    // Insert the text with styles
                    styledDocument.insertString(styledDocument.getLength(), content.toString(), regularStyle);
                    countWords();
                    //Catch and handles exceptions related to file reading and text insertion.
                } catch (IOException | BadLocationException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error opening file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid file type. Please select a TXT file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //create the Swing GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WordCounterApp();
            }
        });
    }
}
