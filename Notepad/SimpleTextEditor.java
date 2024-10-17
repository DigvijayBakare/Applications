import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.*;
import javax.swing.undo.UndoManager;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

public class SimpleTextEditor extends JFrame implements ActionListener {
    // Text area for the editor
    private JTextArea textArea;
    // File chooser for opening and saving files
    private JFileChooser fileChooser;
    private UndoManager undoManager = new UndoManager();

    private String lastSearchText = "";
    private int lastFoundIndex = -1;

    private File currentFile; // Variable to hold the current file


    public SimpleTextEditor() {
        // Create the main frame
        setTitle("Simple Text Editor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Initialize text area and file chooser
        textArea = new JTextArea();
        fileChooser = new JFileChooser();

        // Add UndoableEditListener
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create File menu
        JMenu fileMenu = new JMenu("File");
        addMenuItem(fileMenu, "New");
        addMenuItem(fileMenu, "Open");
        addMenuItem(fileMenu, "Save");
        addMenuItem(fileMenu, "Save As");
        addMenuItem(fileMenu, "Print");

        // Create Edit menu
        JMenu editMenu = new JMenu("Edit");
        addMenuItem(editMenu, "Cut");
        addMenuItem(editMenu, "Copy");
        addMenuItem(editMenu, "Paste");
        addMenuItem(editMenu, "Undo");
        addMenuItem(editMenu, "Redo");
        addMenuItem(editMenu, "Find");
//        addMenuItem(editMenu, "Find Next");
//        addMenuItem(editMenu, "Find Previous");
        addMenuItem(editMenu, "Replace");

        // Add menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        // Set the menu bar for the frame
        setJMenuBar(menuBar);

        // Add components to the frame
        add(new JScrollPane(textArea), BorderLayout.CENTER); // Add scroll pane for text area

        // Make the frame visible
        setVisible(true);
    }

    private void addMenuItem(JMenu menu, String itemName) {
        JMenuItem menuItem = new JMenuItem(itemName);
        menuItem.addActionListener(this); // Add action listener to each menu item
        menu.add(menuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New":
                textArea.setText(""); // Clear the text area for a new document
                break;
            case "Open":
                openFile(); // Open an existing file
                break;
            case "Save":
                saveFile(); // Save the current document
                break;
            case "Save As": // Add this case for Save As
                saveAsFile();
                break;
            case "Print":
                printDocument(); // Print the document (not implemented here)
                break;
            case "Cut":
                textArea.cut(); // Cut selected text
                break;
            case "Copy":
                textArea.copy(); // Copy selected text
                break;
            case "Paste":
                textArea.paste(); // Paste from clipboard
                break;
            case "Undo":
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Redo":
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotRedoException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Find":
                findText(); // Call findText method when Find is selected
                break;
            case "Replace":
                replaceText(); // Call replaceText method when Replace is selected
                break;
            /*case "Find Next":
                findNext(); // Call findNext method when Find Next is selected
                break;
            case "Find Previous":
                findPrevious(); // Call findPrevious method when Find Previous is selected
                break;*/
            default:
                break;
        }
    }
    private void findNext() {
        if (lastSearchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please perform a search first!", "Find Next", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String content = textArea.getText();
        lastFoundIndex = content.indexOf(lastSearchText, lastFoundIndex + lastSearchText.length()); // Search from the last found index

        if (lastFoundIndex >= 0) {
            highlightFoundText(lastFoundIndex, lastSearchText.length());
        } else {
            JOptionPane.showMessageDialog(this, "No more occurrences found!", "Find Next", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void findPrevious() {
        if (lastSearchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please perform a search first!", "Find Previous", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String content = textArea.getText();
        lastFoundIndex = content.lastIndexOf(lastSearchText, lastFoundIndex - 1); // Search backwards

        if (lastFoundIndex >= 0) {
            highlightFoundText(lastFoundIndex, lastSearchText.length());
        } else {
            JOptionPane.showMessageDialog(this, "No previous occurrences found!", "Find Previous", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void highlightFoundText(int index, int length) {
        textArea.setCaretPosition(index);
        textArea.select(index, index + length);
        textArea.requestFocus(); // Bring focus back to the text area
    }

    private void replaceText() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Find:"));
        JTextField findField = new JTextField();
        panel.add(findField);

        panel.add(new JLabel("Replace with:"));
        JTextField replaceField = new JTextField();
        panel.add(replaceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Replace", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String findText = findField.getText();
            String replaceText = replaceField.getText();

            String content = textArea.getText();
            String updatedContent = content.replace(findText, replaceText);

            if (!content.equals(updatedContent)) {
                textArea.setText(updatedContent); // Update the text area with replaced content
                JOptionPane.showMessageDialog(this, "Replacement done!", "Replace", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Text not found!", "Replace", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /*private void findText() {
        String searchText = JOptionPane.showInputDialog(this, "Find:", "Find", JOptionPane.PLAIN_MESSAGE);
        if (searchText != null && !searchText.isEmpty()) {
            String content = textArea.getText();
            int index = content.indexOf(searchText);

            if (index >= 0) {
                // Highlight found text
                textArea.setCaretPosition(index);
                textArea.select(index, index + searchText.length());
                textArea.requestFocus(); // Bring focus back to the text area
            } else {
                JOptionPane.showMessageDialog(this, "Text not found!", "Find", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }*/

    /*private void findText() {
        String searchText = JOptionPane.showInputDialog(this, "Find:", "Find", JOptionPane.PLAIN_MESSAGE);
        if (searchText != null && !searchText.isEmpty()) {
            lastSearchText = searchText; // Store the last search text
            String content = textArea.getText();
            lastFoundIndex = content.indexOf(searchText); // Find first occurrence

            if (lastFoundIndex >= 0) {
                highlightFoundText(lastFoundIndex, searchText.length());
            } else {
                JOptionPane.showMessageDialog(this, "Text not found!", "Find", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }*/
    private void findText() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Find:"));
        JTextField findField = new JTextField();
        panel.add(findField);

        JButton findNextButton = new JButton("Find Next");
        JButton findPreviousButton = new JButton("Find Previous");

        // Add action listeners for buttons
        findNextButton.addActionListener(e -> {
            lastSearchText = findField.getText();
            findNext();
        });

        findPreviousButton.addActionListener(e -> {
            lastSearchText = findField.getText();
            findPrevious();
        });

        panel.add(findNextButton);
        panel.add(findPreviousButton);

        int result = JOptionPane.showConfirmDialog(this, panel, "Find", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            lastSearchText = findField.getText(); // Store search text
            lastFoundIndex = textArea.getText().indexOf(lastSearchText); // Find first occurrence

            if (lastFoundIndex >= 0) {
                highlightFoundText(lastFoundIndex, lastSearchText.length());
            } else {
                JOptionPane.showMessageDialog(this, "Text not found!", "Find", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void saveFile() {
        // If currentFile is null, show the save dialog to select a file
        if (currentFile == null) {
            int returnValue = fileChooser.showSaveDialog(this); // Show save dialog
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile(); // Set currentFile to selected file
            } else {
                return; // Exit if no file is selected
            }
        }
        // Now save directly to currentFile
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
            writer.write(textArea.getText()); // Write content to the file
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
        }
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this); // Show open dialog
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile(); // Set currentFile to the opened file
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                textArea.setText(""); // Clear existing content
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n"); // Append lines to the text area
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage());
            }
        }
    }

    private void printDocument() {
        try {
            boolean complete = textArea.print(); // Print the content of the text area
            if (complete) {
                JOptionPane.showMessageDialog(this, "Print successful.");
            } else {
                JOptionPane.showMessageDialog(this, "Print canceled.");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Error printing: " + ex.getMessage());
        }
    }

    private void saveAsFile() {
        int returnValue = fileChooser.showSaveDialog(this); // Show save dialog
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile(); // Get the selected file
            currentFile = selectedFile; // Update currentFile to the new file
            saveFile(); // Call saveFile to write content to the new file
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleTextEditor::new); // Run on Event Dispatch Thread
    }
}