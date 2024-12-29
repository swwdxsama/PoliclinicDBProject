import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminView extends JFrame {


    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "root";

    public List<String[]> fetchUserData() {
        List<String[]> userData = new ArrayList<>();
        String query = """
                SELECT u.UserID, u.CNP, u.FirstName, u.LastName, u.Adress, u.PhoneNumber,
                       u.Email, u.IBAN, u.HireDate, u.Salary, u.department,
                       r.RoleName
                FROM users u
                JOIN rolesandpermissions r ON u.Role = r.RoleID
                """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] userRow = new String[]{
                        String.valueOf(rs.getInt("UserID")),
                        String.valueOf(rs.getLong("CNP")),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Adress"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Email"),
                        rs.getString("IBAN"),
                        rs.getDate("HireDate").toString(),
                        rs.getString("RoleName"),
                        String.valueOf(rs.getDouble("Salary")),
                        rs.getString("department")
                };
                userData.add(userRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userData;
    }


    public void showUserDataAWT() {
        // Create a new frame (window)
        Frame userDataFrame = new Frame("User Data");

        // Fetch user data
        List<String[]> userData = fetchUserData();

        // Create a TextArea to display the data in a table-like format
        TextArea textArea = new TextArea();
        textArea.setBounds(30, 40, 800, 300);
        textArea.setEditable(false);

        // Add column names as the header
        textArea.append(String.format("%-10s %-15s %-15s %-15s %-15s %-15s %-25s %-20s %-15s %-10s %-15s %-15s\n",
                "UserID", "CNP", "First Name", "Last Name", "Address", "Phone", "Email", "IBAN", "Hire Date", "Role", "Salary", "Department"));

        // Add data rows to the TextArea
        for (String[] row : userData) {
            textArea.append(String.format("%-10s %-15s %-15s %-15s %-15s %-15s %-25s %-20s %-15s %-10s %-15s %-15s\n",
                    row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9], row[10], row[11]));
        }

        // Add the TextArea to the frame
        userDataFrame.add(textArea);
        Button logoutButton = new Button("Close");
        logoutButton.setBounds(350, 350, 100, 30); // Set position and size
        logoutButton.addActionListener(e -> {
            userDataFrame.dispose(); // Close the admin window
            setVisible(true); // Show the login window again
        });
        userDataFrame.add(logoutButton);
        // Set the layout and size of the frame
        userDataFrame.setLayout(null);
        userDataFrame.setSize(900, 400);

        userDataFrame.setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }


    public void showUserData(int userID) {
        // Create a new frame for detailed user data
        Frame userDataFrame = new Frame("User Data");

        // Set the layout
        userDataFrame.setLayout(new FlowLayout());

        // Query to fetch all user data except the password
        String query = "SELECT * FROM users WHERE UserID = ?";
        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the parameter for the query
            pstmt.setInt(1, userID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Get metadata to dynamically fetch columns
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        if (!columnName.equalsIgnoreCase("password")) { // Exclude the password
                            String columnValue = rs.getString(i);
                            Label dataLabel = new Label(columnName + ": " + columnValue);
                            userDataFrame.add(dataLabel);
                        }
                    }
                } else {
                    Label errorLabel = new Label("No data found for User ID: " + userID);
                    errorLabel.setForeground(Color.RED);
                    userDataFrame.add(errorLabel);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error fetching user data.");
            errorLabel.setForeground(Color.RED);
            userDataFrame.add(errorLabel);
        }

        // Add a close button
        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> userDataFrame.dispose());
        userDataFrame.add(closeButton);

        // Set window size and visibility
        userDataFrame.setSize(400, 300);
        userDataFrame.setVisible(true);

        // Add window closing functionality
        userDataFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                userDataFrame.dispose(); // Close the user data window
            }
        });
    }


    public AdminView(int userID, String password, String url, String uid, String pw) {
        // Create a new frame for the admin view
        Frame adminFrame = new Frame("Admin View");

        // Set the layout
        adminFrame.setLayout(new FlowLayout());

        // Add a welcome label
        Label welcomeLabel = new Label("Welcome, Admin!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.BLUE);
        adminFrame.add(welcomeLabel);

        // Query to fetch only the user's name and ID
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
                    adminFrame.add(idLabel);
                    adminFrame.add(nameLabel);
                    adminFrame.add(lastNameLabel);
                } else {
                    Label errorLabel = new Label("No data found for User ID: " + userID);
                    errorLabel.setForeground(Color.RED);
                    adminFrame.add(errorLabel);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Label errorLabel = new Label("Error fetching user data.");
                errorLabel.setForeground(Color.RED);
                adminFrame.add(errorLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error fetching user data.");
            errorLabel.setForeground(Color.RED);
            adminFrame.add(errorLabel);
        }

        // Add a button to show detailed user data
        Button showDataButton = new Button("Show User Data");
        Button showAllUsers = new Button("Show all users");
        showDataButton.addActionListener(e -> showUserData(userID));
        showAllUsers.addActionListener(e-> showUserDataAWT());
        adminFrame.add(showDataButton);
        adminFrame.add(showAllUsers);

        // Add a logout button
        Button logoutButton = new Button("Logout");
        logoutButton.addActionListener(e -> {
            adminFrame.dispose(); // Close the admin window
            setVisible(true); // Show the login window again
        });
        adminFrame.add(logoutButton);

        // Set window size and visibility
        adminFrame.setSize(300, 200);
        adminFrame.setVisible(true);

        // Add window closing functionality
        adminFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                adminFrame.dispose(); // Close the admin window
                setVisible(true); // Show the login window again
            }
        });

        // Hide the login window
        setVisible(false);
    }
}
