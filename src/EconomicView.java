import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EconomicView extends JFrame {
    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "root";

    public EconomicView() {
    }

    public List<String[]> getTransactions() {
        List<String[]> transactions = new ArrayList<>();
        String query = """
                SELECT TransactionID, Patient, Appointment, Amount, TransactionDateTime, PaymentMethod FROM transactions;
        """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] transRow = new String[]{
                        String.valueOf(rs.getInt("TransactionID")),
                        String.valueOf(rs.getInt("Patient")),
                        String.valueOf(rs.getInt("Appointment")),
                        String.valueOf(rs.getFloat("Amount")),
                        String.valueOf(rs.getDate("TransactionDateTime")),
                        rs.getString("PaymentMethod")
                };
                transactions.add(transRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public void transactionsWindow() {
        List<String[]> transactions = getTransactions();
        Frame frame = new Frame();
        TextArea textArea = new TextArea();
        textArea.setBounds(30, 40, 500, 300);
        textArea.setEditable(false);

        // Add column names as the header
        textArea.append(String.format("%-10s %-15s %-15s %-15s %-15s %-15s \n",
                "TransactionID", "PatientID", "AppointmentID", "Amount to pay", "Date", "Payment method"));

        // Add data rows to the TextArea
        for (String[] row : transactions) {
            textArea.append(String.format("%-10s %-15s %-15s %-15s %-15s %-15s\n",
                    row[0], row[1], row[2], row[3], row[4], row[5]));
        }

        // Add the TextArea to the frame
        frame.add(textArea);
        Button logoutButton = new Button("Close");
        logoutButton.setBounds(350, 350, 100, 30); // Set position and size
        logoutButton.addActionListener(e -> frame.dispose());
        frame.add(logoutButton);

        frame.setLayout(null);
        frame.setSize(900, 400);

        frame.setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public EconomicView(int userID) {
        Frame frame = new Frame();
        frame.setTitle("Economic");
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());
        frame.setVisible(true);
        Label welcome = new Label("Welcome Economic user");
        welcome.setFont(new Font("Arial", Font.BOLD, 16));
        welcome.setForeground(Color.BLUE);
        frame.add(welcome);

        Button ShowUserData = new Button("Show User Data");
        ShowUserData.addActionListener(e -> DatabaseConnector.showUserData(userID));
        Button ShowTransactions = new Button("Show Transactions");
        ShowTransactions.addActionListener(e -> transactionsWindow());
        Button LogOutButton = new Button("Log Out");
        LogOutButton.addActionListener(e -> {
            frame.dispose();
            setVisible(true);
        });
        Button AddTransactionButton = new Button("Add New Transaction");
        AddTransactionButton.addActionListener(e -> addTransactionWindow());
        Button ShowSalaries = new Button("Show All Salaries");
        ShowSalaries.addActionListener(e -> showSalariesWindow());
        Button AddSalaryPaymentButton = new Button("Add Salary Payment");
        AddSalaryPaymentButton.addActionListener(e -> addSalaryPaymentWindow());

        frame.add(ShowUserData);
        frame.add(ShowTransactions);
        frame.add(AddTransactionButton);
        frame.add(ShowSalaries);
        frame.add(LogOutButton);
        frame.add(AddSalaryPaymentButton);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public void addTransactionWindow() {
        Frame frame = new Frame("Add Transaction");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 2));

        Label patientLabel = new Label("Patient ID:");
        TextField patientField = new TextField();
        Label appointmentLabel = new Label("Appointment ID:");
        TextField appointmentField = new TextField();
        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        Label dateLabel = new Label("Transaction Date (YYYY-MM-DD):");
        TextField dateField = new TextField();
        Label paymentMethodLabel = new Label("Payment Method:");
        TextField paymentMethodField = new TextField();

        Button addButton = new Button("Add Transaction");
        addButton.addActionListener(e -> {
            try {
                // Verifică dacă câmpurile sunt completate
                if (patientField.getText().isEmpty() || appointmentField.getText().isEmpty() ||
                        amountField.getText().isEmpty() || dateField.getText().isEmpty() ||
                        paymentMethodField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Toate câmpurile sunt obligatorii!", "Eroare", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Parsează valorile
                int patientId = Integer.parseInt(patientField.getText().trim());
                int appointmentId = Integer.parseInt(appointmentField.getText().trim());
                float amount = Float.parseFloat(amountField.getText().trim());
                String date = dateField.getText().trim();
                String paymentMethod = paymentMethodField.getText().trim();

                // Execută interogarea pentru inserare
                String query = """
                    INSERT INTO transactions (Patient, Appointment, Amount, TransactionDateTime, PaymentMethod) 
                    VALUES (?, ?, ?, ?, ?);
                """;

                try (Connection conn = DriverManager.getConnection(url, uid, pw);
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setInt(1, patientId);
                    pstmt.setInt(2, appointmentId);
                    pstmt.setFloat(3, amount);
                    pstmt.setDate(4, Date.valueOf(date));
                    pstmt.setString(5, paymentMethod);

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Transaction added successfully.");
                    frame.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error adding transaction.", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Câmpurile Patient ID, Appointment ID și Amount trebuie să fie numerice!", "Eroare", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Datele introduse nu sunt valide!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(patientLabel);
        frame.add(patientField);
        frame.add(appointmentLabel);
        frame.add(appointmentField);
        frame.add(amountLabel);
        frame.add(amountField);
        frame.add(dateLabel);
        frame.add(dateField);
        frame.add(paymentMethodLabel);
        frame.add(paymentMethodField);
        frame.add(new Label());
        frame.add(addButton);

        frame.setVisible(true);
    }

    public void showSalariesWindow() {
        Frame frame = new Frame("Salaries of All Users");
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        List<String[]> salaries = getSalaries();

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        frame.add(textArea, BorderLayout.CENTER);

        textArea.append(String.format("%-10s %-20s %-20s %-10s\n", "UserID", "First Name", "Last Name", "Salary"));

        for (String[] row : salaries) {
            textArea.append(String.format("%-10s %-20s %-20s %-10s\n", row[0], row[1], row[2], row[3]));
        }

        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> frame.dispose());
        frame.add(closeButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public List<String[]> getSalaries() {
        List<String[]> salaries = new ArrayList<>();
        String query = """
                SELECT UserID, FirstName, LastName, Salary FROM users;
        """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] userRow = new String[]{
                        String.valueOf(rs.getInt("UserID")),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        String.valueOf(rs.getFloat("Salary"))
                };
                salaries.add(userRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salaries;
    }

    // New function for adding salary payments
    public void addSalaryPaymentWindow() {
        Frame frame = new Frame("Add Salary Payment");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 2));

        Label userLabel = new Label("User ID:");
        TextField userField = new TextField();
        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        Label dateLabel = new Label("Payment Date (YYYY-MM-DD):");
        TextField dateField = new TextField();
        Label paymentTypeLabel = new Label("Payment Type:");
        TextField paymentTypeField = new TextField();

        Button addButton = new Button("Add Payment");
        addButton.addActionListener(e -> {
            try {
                // Verifică dacă câmpurile sunt completate
                if (userField.getText().isEmpty() || amountField.getText().isEmpty() ||
                        dateField.getText().isEmpty() || paymentTypeField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Toate câmpurile sunt obligatorii!", "Eroare", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Parsează valorile
                int userId = Integer.parseInt(userField.getText().trim());
                float amount = Float.parseFloat(amountField.getText().trim());
                String date = dateField.getText().trim();
                String paymentType = paymentTypeField.getText().trim();

                // Execută interogarea pentru inserare
                String query = """
                    INSERT INTO salarypayments (User, Amount, PaymentDate, PaymentType) 
                    VALUES (?, ?, ?, ?);
                """;

                try (Connection conn = DriverManager.getConnection(url, uid, pw);
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setInt(1, userId);
                    pstmt.setFloat(2, amount);
                    pstmt.setDate(3, Date.valueOf(date));
                    pstmt.setString(4, paymentType);

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Salary payment added successfully.");
                    frame.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error adding salary payment.", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Câmpurile User ID și Amount trebuie să fie numerice!", "Eroare", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Datele introduse nu sunt valide!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(userLabel);
        frame.add(userField);
        frame.add(amountLabel);
        frame.add(amountField);
        frame.add(dateLabel);
        frame.add(dateField);
        frame.add(paymentTypeLabel);
        frame.add(paymentTypeField);
        frame.add(new Label());
        frame.add(addButton);

        frame.setVisible(true);
    }
}
