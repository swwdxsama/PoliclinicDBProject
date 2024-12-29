import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EconomicView extends JFrame {
    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "root";

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
                        String.valueOf(rs.getTimestamp("TransactionDateTime")),
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
                "TransactionID", "PatientID", "AppointmentID", "Amount to pay", "Date Time", "Payment method"));

        // Add data rows to the TextArea
        for (String[] row : transactions) {
            textArea.append(String.format("%-10s %-15s %-15s %-15s %-15s %-15s\n",
                    row[0], row[1], row[2], row[3], row[4], row[5]));
        }

        // Add the TextArea to the frame
        frame.add(textArea);
        Button logoutButton = new Button("Close");
        logoutButton.setBounds(350, 350, 100, 30); // Set position and size
        logoutButton.addActionListener(e -> {
            frame.dispose(); // Close the admin window
            setVisible(true); // Show the login window again
        });
        frame.add(logoutButton);
        // Set the layout and size of the frame
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
        String query = "SELECT UserID, FirstName, LastName FROM users WHERE UserID = ?";
        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the parameter for the query
            pstmt.setInt(1, userID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Display user ID, first name, and last name
                    Label idLabel = new Label("User ID: " + rs.getInt("UserID"));
                    Label nameLabel = new Label("First Name: " + rs.getString("FirstName"));
                    Label lastNameLabel = new Label("Last Name: " + rs.getString("LastName"));
                    frame.add(idLabel);
                    frame.add(nameLabel);
                    frame.add(lastNameLabel);
                } else {
                    Label errorLabel = new Label("No data found for User ID: " + userID);
                    errorLabel.setForeground(Color.RED);
                    frame.add(errorLabel);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Label errorLabel = new Label("Error fetching user data.");
                errorLabel.setForeground(Color.RED);
                frame.add(errorLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error fetching user data.");
            errorLabel.setForeground(Color.RED);
            frame.add(errorLabel);
        }

        Button ShowUserData = new Button("Show User Data");
        ShowUserData.addActionListener(e -> DatabaseConnector.showUserData(userID));
        Button ShowTransactions = new Button("Show Transactions");
        ShowTransactions.addActionListener(e -> transactionsWindow());
        Button LogOutButton = new Button("Log Out");
        LogOutButton.addActionListener(e -> {
            frame.dispose(); // Close the admin window
            setVisible(true); // Show the login window again
        });

        frame.add(ShowUserData);
        frame.add(ShowTransactions);
        frame.add(LogOutButton);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

}
