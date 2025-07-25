import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationFormMySQL extends JFrame {
    // Form fields
    private JTextField tfName, tfContact, tfDob;
    private JTextArea taAddress;
    private JRadioButton rbMale, rbFemale;
    private JCheckBox cbTerms;
    private JButton btnRegister, btnReset, btnExit;
    private DefaultTableModel tableModel;
    private JTable table;

    // DB connection details
    private final String DB_URL = "jdbc:mysql://localhost:3306/registrationdb";
    private final String USER = "root"; // Default for XAMPP
    private final String PASS = "";     // Default for XAMPP

    public RegistrationFormMySQL() {
        setTitle("Registration Form");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(20, 30, 80, 25);
        add(lblName);
        tfName = new JTextField();
        tfName.setBounds(100, 30, 120, 25);
        add(tfName);

        JLabel lblGender = new JLabel("Gender:");
        lblGender.setBounds(20, 60, 80, 25);
        add(lblGender);
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        rbMale.setBounds(100, 60, 60, 25);
        rbFemale.setBounds(160, 60, 80, 25);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        add(rbMale);
        add(rbFemale);

        JLabel lblDOB = new JLabel("DOB (YYYY-MM-DD):");
        lblDOB.setBounds(20, 90, 120, 25);
        add(lblDOB);
        tfDob = new JTextField();
        tfDob.setBounds(140, 90, 80, 25);
        add(tfDob);

        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setBounds(20, 120, 80, 25);
        add(lblAddress);
        taAddress = new JTextArea();
        taAddress.setBounds(100, 120, 120, 50);
        add(taAddress);

        JLabel lblContact = new JLabel("Contact:");
        lblContact.setBounds(20, 180, 80, 25);
        add(lblContact);
        tfContact = new JTextField();
        tfContact.setBounds(100, 180, 120, 25);
        add(tfContact);

        cbTerms = new JCheckBox("Accept Terms And Conditions.");
        cbTerms.setBounds(20, 210, 200, 25);
        add(cbTerms);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(20, 240, 90, 25);
        add(btnRegister);

        btnReset = new JButton("Reset");
        btnReset.setBounds(110, 240, 90, 25);
        add(btnReset);

        btnExit = new JButton("Exit");
        btnExit.setBounds(60, 270, 90, 25);
        add(btnExit);

        // Table for right side
        tableModel = new DefaultTableModel(new String[]{
            "ID", "Name", "Gender", "DOB", "Contact", "Address"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(250, 30, 400, 250);
        add(scrollPane);

        // Load existing data
        loadTableData();

        // Action listeners
        btnRegister.addActionListener(e -> register());
        btnReset.addActionListener(e -> resetForm());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void register() {
        if (!cbTerms.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please accept the terms and conditions.");
            return;
        }
        String name = tfName.getText();
        String gender = rbMale.isSelected() ? "Male" : (rbFemale.isSelected() ? "Female" : "");
        String dob = tfDob.getText();
        String address = taAddress.getText();
        String contact = tfContact.getText();

        if (name.isEmpty() || gender.isEmpty() || dob.isEmpty() || address.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        // Save to DB
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "INSERT INTO registrations (name, gender, dob, address, contact) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, gender);
            pst.setDate(3, Date.valueOf(dob));
            pst.setString(4, address);
            pst.setString(5, contact);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            loadTableData();
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT * FROM registrations";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getDate("dob"),
                        rs.getString("contact"),
                        rs.getString("address")
                });
            }
        } catch (Exception ex) {
            // ignore for now
        }
    }

    private void resetForm() {
        tfName.setText("");
        rbMale.setSelected(false);
        rbFemale.setSelected(false);
        tfDob.setText("");
        taAddress.setText("");
        tfContact.setText("");
        cbTerms.setSelected(false);
    }

    public static void main(String[] args) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!");
        }
        SwingUtilities.invokeLater(() -> new RegistrationFormMySQL().setVisible(true));
    }
}