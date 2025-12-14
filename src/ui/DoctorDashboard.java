package ui;

import dao.*;
import model.*;
import util.CSVExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class DoctorDashboard extends JFrame {
    private final User user;
    private final Doctor doctor;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordDAO recordDAO = new MedicalRecordDAO();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    public DoctorDashboard(User user, Doctor doctor) {
        this.user = user;
        this.doctor = doctor;

        setTitle("Doctor Dashboard - " + (user != null ? user.getFullName() : ""));
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(null);
        topPanel.setBackground(new Color(0, 140, 200));
        topPanel.setPreferredSize(new Dimension(1000, 70));

        JLabel title = new JLabel("DOCTOR CONTROL PANEL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(20, 15, 400, 40);
        topPanel.add(title);

        JButton btnBackToLogin = new JButton("Back to Login");
        btnBackToLogin.setBackground(new Color(220, 54, 54));
        btnBackToLogin.setForeground(Color.WHITE);
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBackToLogin.setBounds(820, 15, 150, 35);

        btnBackToLogin.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to log out and go back to login?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        topPanel.add(btnBackToLogin);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Appointments", appointmentsPanel());
        tabs.addTab("Medical Records", medicalRecordsPanel());
        tabs.addTab("Prescriptions", prescriptionsPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private JPanel appointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"AppointmentID", "PatientID", "Date", "Status", "Notes"}, 0);

        JTable table = new JTable(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton approve = new JButton("Approve");
        JButton cancel = new JButton("Cancel");
        JButton complete = new JButton("Complete");
        JButton refresh = new JButton("Refresh");

        JButton export = new JButton("Export CSV");

        styleButton(approve, new Color(0, 153, 76), Color.WHITE);
        styleButton(cancel, new Color(220, 53, 69), Color.WHITE);
        styleButton(complete, new Color(255, 165, 0), Color.WHITE);
        styleButton(refresh, new Color(0, 102, 204), Color.WHITE);
        styleButton(export, new Color(46,164,79), Color.WHITE);

        bottom.add(approve);
        bottom.add(cancel);
        bottom.add(complete);
        bottom.add(refresh);
        bottom.add(export);     // ✅ add export button to bottom panel
        panel.add(bottom, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);
        export.addActionListener(e -> CSVExporter.export(table, "Appointments "));

        refresh.addActionListener(e -> {
            model.setRowCount(0);
            List<Appointment> list = appointmentDAO.getByDoctor(doctor.getDoctorID());
            for (Appointment a : list)
                model.addRow(new Object[]{
                        a.getAppointmentID(),
                        a.getPatientID(),
                        a.getAppointmentDate(),
                        a.getStatus(),
                        a.getNotes()
                });
        });

        approve.addActionListener(e -> changeStatus(table, model, "Approved"));
        cancel.addActionListener(e -> changeStatus(table, model, "Cancelled"));
        complete.addActionListener(e -> changeStatus(table, model, "Completed"));

        refresh.doClick();
        return panel;
    }

    private void changeStatus(JTable table, DefaultTableModel model, String status) {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select appointment");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        appointmentDAO.updateStatus(id, status);
        model.setValueAt(status, r, 3);
    }

    private JPanel medicalRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"RecordID","PatientID","Diagnosis","Lab Test","Doctor Notes","CreatedAt"}, 0);
        JTable table = new JTable(model);


        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton export = new JButton("Export CSV");

        styleButton(add, new Color(0, 153, 255), Color.WHITE);
        styleButton(update, new Color(255,165,0), Color.WHITE);
        styleButton(delete, new Color(220,53,69), Color.WHITE);
        styleButton(refresh, new Color(108,117,125), Color.WHITE);
        styleButton(export, new Color(46,164,79), Color.WHITE);

        JPanel bottom = new JPanel();
        bottom.add(add);
        bottom.add(update);
        bottom.add(delete);
        bottom.add(refresh);
        bottom.add(export);
        panel.add(bottom, BorderLayout.SOUTH);

        export.addActionListener(e -> CSVExporter.export(table, "Medical_Records"));



        // ===== REFRESH LOAD =====
        refresh.addActionListener(e -> {
            model.setRowCount(0);
            List<MedicalRecord> list = recordDAO.getAll();
            for (MedicalRecord m : list) {
                model.addRow(new Object[]{
                        m.getMedicalRecordID(),
                        m.getPatientID(),
                        m.getAttribute1(),
                        m.getAttribute2(),
                        m.getAttribute3(),
                        m.getCreatedAt()
                });
            }
        });


        // ===== ADD RECORD =====
        add.addActionListener(e -> {
            JTextField pid = new JTextField();
            JTextField a1 = new JTextField();
            JTextField a2 = new JTextField();
            JTextField a3 = new JTextField();

            Object[] form = {
                    "PatientID:", pid,
                    "Attribute1:", a1,
                    "Attribute2:", a2,
                    "Attribute3:", a3
            };

            int ok = JOptionPane.showConfirmDialog(this, form, "Add Medical Record", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                try {
                    MedicalRecord m = new MedicalRecord();
                    m.setPatientID(Integer.parseInt(pid.getText().trim()));
                    m.setAttribute1(a1.getText().trim());
                    m.setAttribute2(a2.getText().trim());
                    m.setAttribute3(a3.getText().trim());
                    recordDAO.add(m);
                    refresh.doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid data input");
                }
            }
        });


        // ===== UPDATE RECORD =====
        update.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select a record to update");
                return;
            }

            int id = (int) model.getValueAt(r, 0);

            JTextField pid = new JTextField(model.getValueAt(r,1).toString());
            JTextField a1 = new JTextField(model.getValueAt(r,2).toString());
            JTextField a2 = new JTextField(model.getValueAt(r,3).toString());
            JTextField a3 = new JTextField(model.getValueAt(r,4).toString());

            Object[] form = {
                    "PatientID:", pid,
                    "Attribute1:", a1,
                    "Attribute2:", a2,
                    "Attribute3:", a3
            };

            if (JOptionPane.showConfirmDialog(this, form, "Update Medical Record", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    MedicalRecord m = new MedicalRecord();
                    m.setMedicalRecordID(id);
                    m.setPatientID(Integer.parseInt(pid.getText().trim()));
                    m.setAttribute1(a1.getText().trim());
                    m.setAttribute2(a2.getText().trim());
                    m.setAttribute3(a3.getText().trim());
                    recordDAO.update(m);
                    refresh.doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid data");
                }
            }
        });


        // ===== DELETE RECORD =====
        delete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select record to delete");
                return;
            }
            int id = (int) model.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete record #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                recordDAO.delete(id);
                refresh.doClick();
            }
        });


        refresh.doClick();
        return panel;
    }

    private JPanel prescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"PrescriptionID", "AppointmentID", "Medication", "Dosage", "Instructions", "CreatedAt"}, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);




        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Prescription");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");


        styleButton(btnAdd, new Color(46, 164, 79), Color.WHITE);
        styleButton(btnUpdate, new Color(255, 165, 0), Color.WHITE);
        styleButton(btnDelete, new Color(220, 53, 69), Color.WHITE);
        styleButton(btnRefresh, new Color(0, 102, 204), Color.WHITE);




        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);



        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            for (Prescription p : prescriptionDAO.getAll()) {
                model.addRow(new Object[]{
                        p.getPrescriptionID(),
                        p.getAppointmentID(),
                        p.getAttribute1(),
                        p.getAttribute2(),
                        p.getAttribute3(),
                        p.getCreatedAt()
                });
            }
        });

        btnAdd.addActionListener(e -> {
            JTextField apptId = new JTextField();
            JTextField a1 = new JTextField();
            JTextField a2 = new JTextField();
            JTextField a3 = new JTextField();
            Object[] form = {"AppointmentID:", apptId, "Attribute1:", a1, "Attribute2:", a2, "Attribute3:", a3};
            if (JOptionPane.showConfirmDialog(this, form, "Add Prescription", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    Prescription p = new Prescription();
                    p.setAppointmentID(Integer.parseInt(apptId.getText().trim()));
                    p.setAttribute1(a1.getText().trim());
                    p.setAttribute2(a2.getText().trim());
                    p.setAttribute3(a3.getText().trim());
                    prescriptionDAO.add(p);
                    JOptionPane.showMessageDialog(this, "Prescription added!");
                    btnRefresh.doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid data.");
                }
            }
        });

        btnUpdate.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select a prescription.");
                return;
            }
            int id = (int) model.getValueAt(r, 0);
            JTextField a1 = new JTextField(model.getValueAt(r, 2).toString());
            JTextField a2 = new JTextField(model.getValueAt(r, 3).toString());
            JTextField a3 = new JTextField(model.getValueAt(r, 4).toString());
            Object[] form = {"Attribute1:", a1, "Attribute2:", a2, "Attribute3:", a3};
            if (JOptionPane.showConfirmDialog(this, form, "Update Prescription", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                Prescription p = new Prescription();
                p.setPrescriptionID(id);
                p.setAttribute1(a1.getText().trim());
                p.setAttribute2(a2.getText().trim());
                p.setAttribute3(a3.getText().trim());
                prescriptionDAO.update(p);
                JOptionPane.showMessageDialog(this, "Updated successfully.");
                btnRefresh.doClick();
            }
        });

        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select prescription to delete.");
                return;
            }
            int id = (int) model.getValueAt(r, 0);
            prescriptionDAO.delete(id);
            JOptionPane.showMessageDialog(this, "Deleted!");
            btnRefresh.doClick();
        });

        btnRefresh.doClick();
        return panel;
    }
}
