package ui;

import dao.*;
import model.*;
import util.CSVExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

public class PatientDashboard extends JFrame {
    private final User user;
    private final Patient patient;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final MedicalRecordDAO recordDAO = new MedicalRecordDAO();
    private final BillingDAO billingDAO = new BillingDAO();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    public PatientDashboard(User user, Patient patient) {
        this.user = user;
        this.patient = patient;

        setTitle("Patient Dashboard - " + user.getFullName());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(null);
        topPanel.setBackground(Color.BLUE);
        topPanel.setPreferredSize(new Dimension(1000, 70));

        JLabel title = new JLabel("PATIENT PORTAL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(20, 15, 300, 40);
        topPanel.add(title);

        JButton btnBackToLogin = new JButton("Back to Login");
        styleButton(btnBackToLogin, new Color(220, 54, 54), Color.WHITE);
        btnBackToLogin.setBounds(820, 15, 150, 35);

        btnBackToLogin.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out and go back to Login?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        topPanel.add(btnBackToLogin);
        add(topPanel, BorderLayout.NORTH);

        // ===== TABS =====
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Appointments", appointmentsPanel());
        tabs.addTab("Medical Records", medicalRecordsPanel());
        tabs.addTab("Billing", billingPanel());
        tabs.addTab("Prescriptions", prescriptionsPanel());
        add(tabs, BorderLayout.CENTER);
    }


    // =====================================================================
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
    // =====================================================================


    // ======================= APPOINTMENTS PANEL ==========================
    private JPanel appointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"AppointmentID", "DoctorID", "Date", "Status", "Notes"}, 0
        );

        JTable table = new JTable(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // ✅ Only add once (bug fix)
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton book = new JButton("Book Appointment");
        JButton cancel = new JButton("Cancel Appointment");
        JButton delete = new JButton("Delete Appointment");
        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");

        styleButton(book,   new Color(0,123,255), Color.WHITE);
        styleButton(cancel, new Color(255,165,0), Color.WHITE);
        styleButton(delete, new Color(220,53,69), Color.WHITE);
        styleButton(refresh,new Color(108,117,125),Color.WHITE);
        styleButton(export, new Color(46,164,79), Color.WHITE);

        bottom.add(book);
        bottom.add(cancel);
        bottom.add(delete);
        bottom.add(refresh);
        bottom.add(export);

        panel.add(bottom, BorderLayout.SOUTH);

        export.addActionListener(e -> CSVExporter.export(table, "My_Appointments"));

        // 🔄 Refresh load
        refresh.addActionListener(e -> {
            model.setRowCount(0);
            List<Appointment> list = appointmentDAO.getByPatient(patient.getPatientID());
            for (Appointment a : list)
                model.addRow(new Object[]{
                        a.getAppointmentID(),
                        a.getDoctorID(),
                        a.getAppointmentDate(),
                        a.getStatus(),
                        a.getNotes()
                });
        });

        // ➕ Book appointment
        book.addActionListener(e -> {
            List<Doctor> docs = doctorDAO.getAll();
            if (docs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No doctors available");
                return;
            }

            String[] opts = docs.stream()
                    .map(d -> d.getDoctorID() + " - " + d.getName())
                    .toArray(String[]::new);

            JComboBox<String> cb = new JComboBox<>(opts);
            JTextField dt = new JTextField("YYYY-MM-DD HH:MM:SS");
            JTextArea notes = new JTextArea(3, 20);

            Object[] form = {"Doctor:", cb, "DateTime:", dt, "Notes:", new JScrollPane(notes)};
            int ok = JOptionPane.showConfirmDialog(this, form, "Book Appointment", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                try {
                    String sel = (String) cb.getSelectedItem();
                    int docId = Integer.parseInt(sel.split(" - ")[0]);

                    Appointment a = new Appointment();
                    a.setDoctorID(docId);
                    a.setPatientID(patient.getPatientID());
                    a.setAppointmentDate(Timestamp.valueOf(dt.getText().trim()));
                    a.setStatus("Pending");
                    a.setNotes(notes.getText().trim());

                    appointmentDAO.add(a);
                    refresh.doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input");
                }
            }
        });

        // 🚫 Cancel Appointment
        cancel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select appointment");
                return;
            }
            int id = (int) model.getValueAt(r, 0);
            String status = model.getValueAt(r, 3).toString();

            if ("Completed".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Cannot cancel");
                return;
            }

            appointmentDAO.updateStatus(id, "Cancelled");
            model.setValueAt("Cancelled", r, 3);
        });

        // ❌ Delete Appointment
        delete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select appointment");
                return;
            }
            int id = (int) model.getValueAt(r, 0);

            if (JOptionPane.showConfirmDialog(this, "Delete appointment #" + id + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                appointmentDAO.delete(id);
                refresh.doClick();
            }
        });

        refresh.doClick();
        return panel;
    }



    // ======================= MEDICAL RECORDS PANEL =======================
    private JPanel medicalRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"MedicalRecordID","PatientID","Diagnosis","Lab Test","Doctor notes","CreatedAt"}, 0
        );

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton refresh = new JButton("Refresh");
        JButton export  = new JButton("Export CSV");
        styleButton(refresh,new Color(108,117,125),Color.WHITE);
        styleButton(export,new Color(46,164,79),Color.WHITE);
        bottom.add(refresh);
        bottom.add(export);
        panel.add(bottom, BorderLayout.SOUTH);

        export.addActionListener(e -> CSVExporter.export(table, "My_Medical_Records"));

        refresh.addActionListener(e -> {
            model.setRowCount(0);
            List<MedicalRecord> list = recordDAO.getByPatient(patient.getPatientID());
            for (MedicalRecord m : list)
                model.addRow(new Object[]{
                        m.getMedicalRecordID(),
                        m.getPatientID(),
                        m.getAttribute1(),
                        m.getAttribute2(),
                        m.getAttribute3(),
                        m.getCreatedAt()
                });
        });

        refresh.doClick();
        return panel;
    }



    // =========================== BILLING PANEL ===========================
    private JPanel billingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"BillingID","PatientName","Amount","PaymentMethod","CreatedAt"}, 0
        );

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton refresh = new JButton("Refresh");
        JButton export  = new JButton("Export CSV");
        styleButton(refresh,new Color(108,117,125),Color.WHITE);
        styleButton(export,new Color(46,164,79),Color.WHITE);
        bottom.add(refresh);
        bottom.add(export);
        panel.add(bottom, BorderLayout.SOUTH);

        export.addActionListener(e -> CSVExporter.export(table, "My_Billing"));

        refresh.addActionListener(e -> {
            model.setRowCount(0);
            List<Billing> list = billingDAO.getByPatient(patient.getPatientID());
            for (Billing b : list)
                model.addRow(new Object[]{
                        b.getBillingID(),
                        b.getPatientName(),
                        b.getAmount(),
                        b.getPaymentMethod(),
                        b.getCreatedAt()
                });
        });

        refresh.doClick();
        return panel;
    }



    // ======================= PRESCRIPTIONS PANEL =========================
    private JPanel prescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"PrescriptionID","AppointmentID","Medication","Dosage","Instructions","CreatedAt"}, 0
        );

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton refresh = new JButton("Refresh");
        JButton export  = new JButton("Export CSV");
        styleButton(refresh,new Color(108,117,125),Color.WHITE);
        styleButton(export,new Color(46,164,79),Color.WHITE);
        bottom.add(refresh);
        bottom.add(export);
        panel.add(bottom, BorderLayout.SOUTH);

        export.addActionListener(e -> CSVExporter.export(table, "My_Prescriptions"));

        refresh.addActionListener(e -> {
            model.setRowCount(0);

            List<Appointment> appointments = appointmentDAO.getByPatient(patient.getPatientID());
            List<Prescription> prescriptions = prescriptionDAO.getAll();

            for (Appointment ap : appointments) {
                for (Prescription p : prescriptions) {
                    if (p.getAppointmentID() != null && p.getAppointmentID() == ap.getAppointmentID()) {
                        model.addRow(new Object[]{
                                p.getPrescriptionID(),
                                p.getAppointmentID(),
                                p.getAttribute1(),
                                p.getAttribute2(),
                                p.getAttribute3(),
                                p.getCreatedAt()
                        });
                    }
                }
            }
        });

        refresh.doClick();
        return panel;
    }
}
