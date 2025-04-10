import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Vector;

public class TimetableManager {

    public static final String DEFAULT_FILE = "timetable_data.txt";

    public static void saveTimetable(DefaultTableModel model, String filePath) throws IOException {
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            Vector<String> headers = new Vector<>();
            for (int i = 0; i < model.getColumnCount(); i++) {
                headers.add(model.getColumnName(i));
            }
            writer.write(String.join(",", headers));
            writer.newLine();

            int rowCount = model.getRowCount();
            int colCount = model.getColumnCount();

            for (int i = 0; i < rowCount; i++) {
                Vector<String> rowStrings = new Vector<>();
                for (int j = 0; j < colCount; j++) {
                    Object cell = model.getValueAt(i, j);
                    String cellString = (cell != null) ? cell.toString().replace(",", ";") : "";
                    rowStrings.add(cellString);
                }
                writer.write(String.join(",", rowStrings));
                writer.newLine();
            }
        }
    }

    public static DefaultTableModel loadTimetable(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return createDefaultTimetableModel();
        }

        Vector<String> headers = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String[] headerArray = line.split(",");
                for (String header : headerArray) {
                    headers.add(header.trim());
                }
            } else {
                 return createDefaultTimetableModel();
            }

            while ((line = reader.readLine()) != null) {
                 if (line.trim().isEmpty()) continue;

                String[] cellArray = line.split(",", -1);
                Vector<Object> row = new Vector<>();
                for(String cell : cellArray) {
                    row.add(cell.replace(";", ",").trim());
                }

                while(row.size() < headers.size()) {
                    row.add("");
                }
                while(row.size() > headers.size()) {
                    row.removeElementAt(row.size() - 1);
                }
                data.add(row);
            }

        } catch (IOException e) {
            System.err.println("Error loading timetable from " + filePath + ": " + e.getMessage());
            return createDefaultTimetableModel();
        }

        if (headers.isEmpty()) {
             return createDefaultTimetableModel();
        }

        DefaultTableModel loadedModel = new DefaultTableModel(data, headers);

        return loadedModel;
    }

    public static DefaultTableModel loadDefaultTimetable() {
        return loadTimetable(DEFAULT_FILE);
    }

    public static void saveDefaultTimetable(DefaultTableModel model) throws IOException {
         saveTimetable(model, DEFAULT_FILE);
    }

    private static DefaultTableModel createDefaultTimetableModel() {
        String[] columnNames = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        Object[][] data = {
                {"09:00-10:00", "Math", "Physics", "Math", "Chemistry", "English"},
                {"10:00-11:00", "Physics", "English", "Chemistry", "Math", "History"},
                {"11:00-12:00", "Break", "Break", "Break", "Break", "Break"},
                {"12:00-13:00", "History", "Biology", "English", "Biology", "Math"},
                {"13:00-14:00", "Chemistry", "Math", "Physics", "English", "Biology"}
        };
        // Return a standard editable model now
        return new DefaultTableModel(data, columnNames);
    }
}