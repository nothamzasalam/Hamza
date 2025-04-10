import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector; // Required for getDataVector

public class MainFrame extends JFrame implements ActionListener {

    private UserRole userRole;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private JButton saveButton;
    private JLabel statusLabel;

    public MainFrame(UserRole role) {
        this.userRole = role;

        setTitle("School Timetable - " + role.name());
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load the base model data
        DefaultTableModel loadedModel = TimetableManager.loadDefaultTimetable();

        // Create the final table model, overriding isCellEditable based on role
        tableModel = new DefaultTableModel(loadedModel.getDataVector(), getColumnIdentifiers(loadedModel)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing only for Admin and Teacher roles
                return (userRole == UserRole.ADMIN || userRole == UserRole.TEACHER);
            }
        };

        timetableTable = new JTable(tableModel);
        timetableTable.setFillsViewportHeight(true);
        timetableTable.setRowHeight(25); // Adjust row height if needed
        timetableTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(timetableTable);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel(" Logged in as: " + role.name());
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        // Add save button only for Admin and Teacher
        if (userRole == UserRole.ADMIN || userRole == UserRole.TEACHER) {
            saveButton = new JButton("Save Timetable");
            saveButton.addActionListener(this);
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(saveButton);
            bottomPanel.add(buttonPanel, BorderLayout.EAST);
        }

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Helper to get column identifiers correctly as a Vector
    private Vector<String> getColumnIdentifiers(DefaultTableModel model) {
        Vector<String> columnIdentifiers = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            columnIdentifiers.add(model.getColumnName(i));
        }
        return columnIdentifiers;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton && (userRole == UserRole.ADMIN || userRole == UserRole.TEACHER)) {
            saveTimetable();
        }
    }

    private void saveTimetable() {
        // Stop any active cell editing before saving
        if (timetableTable.isEditing()) {
            timetableTable.getCellEditor().stopCellEditing();
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Timetable As");
        fileChooser.setSelectedFile(new File(TimetableManager.DEFAULT_FILE));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                TimetableManager.saveTimetable(tableModel, fileToSave.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Timetable saved successfully to " + fileToSave.getName(),
                        "Save Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving timetable: " + ex.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}