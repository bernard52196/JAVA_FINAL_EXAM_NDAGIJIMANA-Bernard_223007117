package util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileWriter;

public class CSVExporter {

    public static void export(JTable table, String fileNamePrefix) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File(fileNamePrefix + ".csv"));

        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {

                TableModel model = table.getModel();
                int colCount = model.getColumnCount();
                int rowCount = model.getRowCount();

                // Write header
                for (int i = 0; i < colCount; i++) {
                    fw.write(model.getColumnName(i));
                    if (i < colCount - 1) fw.write(",");
                }
                fw.write("\n");

                // Write rows
                for (int r = 0; r < rowCount; r++) {
                    for (int c = 0; c < colCount; c++) {
                        Object value = model.getValueAt(r, c);
                        fw.write(value != null ? value.toString() : "");
                        if (c < colCount - 1) fw.write(",");
                    }
                    fw.write("\n");
                }

                JOptionPane.showMessageDialog(null, "Exported Successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Export Failed!");
            }
        }
    }
}
