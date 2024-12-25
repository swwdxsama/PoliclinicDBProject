import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.lang.System;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class DatabaseConnector extends Frame {
    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "root";
    private BufferedReader reader;
    private Connection con;

    private Label userLabel, passLabel;
    private TextField userField, passField;
    private Button loginButton;

    public static void showUserDataAWT() {
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
     //       setVisible(true); // Show the login window again
        });
        userDataFrame.add(logoutButton);
        // Set the layout and size of the frame
        userDataFrame.setLayout(null);
        userDataFrame.setSize(900, 400);

        userDataFrame.setVisible(true);
    }

    public static List<String[]> fetchUserData() {
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

    public List<String[]> fetchDoctorData() {
        List<String[]> doctorData = new ArrayList<>();
        String query = """
            SELECT users.UserID, users.CNP, users.FirstName, users.LastName, users.Adress, 
                   users.PhoneNumber, users.Email, users.IBAN, users.HireDate, users.Salary, 
                   users.department, rolesandpermissions.RoleName, 
                   medicalstaff.MedicalID, medicalstaff.Specialization, 
                   medicalstaff.Degree, medicalstaff.ParafCode, medicalstaff.ScientificTitle
            FROM users
            INNER JOIN rolesandpermissions ON users.Role = rolesandpermissions.RoleID
            INNER JOIN medicalstaff ON users.UserID = medicalstaff.UserID
            """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] userRow = new String[]{
                        String.valueOf(rs.getInt("UserID")),
                        String.valueOf(rs.getInt("MedicalID")),
                        String.valueOf(rs.getLong("CNP")),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Degree"),
                        rs.getString("Specialization"),
                        rs.getString("ScientificTitle"),
                        String.valueOf(rs.getDouble("ParafCode")),
                        rs.getString("Adress"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Email"),
                        rs.getString("IBAN"),
                        rs.getDate("HireDate").toString(),
                        rs.getString("RoleName"),
                        String.valueOf(rs.getDouble("Salary")),
                        rs.getString("department")
                };
                doctorData.add(userRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctorData;
    }




    private void init() {
        // Înregistrează driverul MySQL și realizează conexiunea
        try {
            // Încarcă driverul MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException: " + e);
        }

        // Inițializează conexiunea
        con = null;
        try {
            con = DriverManager.getConnection(url, uid, pw);
            System.out.println("Conexiune reușită la baza de date!");
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex);
            System.exit(1);
        }

        // Setează reader-ul pentru input
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public static String authenticateUser(int userId, String password) {
        String query = "SELECT Role FROM users WHERE UserID = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set query parameters
            pstmt.setInt(1, userId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if any row exists
                if (rs.next()) {
                    return rs.getString("Role"); // Return the user's role
                } else {
                    return "false"; // Return false if no user is found
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "false"; // Return false if there's an error
        }
    }





    public static void showDoctorData(int userID) {
        // Create a new frame for detailed doctor data
        Frame doctorDataFrame = new Frame("Doctor Data");

        // Set the layout
        doctorDataFrame.setLayout(new FlowLayout());

        // Query to fetch doctor data based on user ID
        String query = """
            SELECT users.UserID, users.CNP, users.FirstName, users.LastName, users.Adress, 
                   users.PhoneNumber, users.Email, users.IBAN, users.HireDate, users.Salary, 
                   users.department, rolesandpermissions.RoleName, 
                   medicalstaff.MedicalID, medicalstaff.Specialization, 
                   medicalstaff.Degree, medicalstaff.ParafCode, medicalstaff.ScientificTitle
            FROM users
            INNER JOIN rolesandpermissions ON users.Role = rolesandpermissions.RoleID
            INNER JOIN medicalstaff ON users.UserID = medicalstaff.UserID
            WHERE users.UserID = ?
            """;

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
                        String columnValue = rs.getString(i);

                        // Exclude any sensitive or irrelevant columns if needed
                        if (!columnName.equalsIgnoreCase("password")) {
                            Label dataLabel = new Label(columnName + ": " + columnValue);
                            doctorDataFrame.add(dataLabel);
                        }
                    }
                } else {
                    Label errorLabel = new Label("No data found for User ID: " + userID);
                    errorLabel.setForeground(Color.RED);
                    doctorDataFrame.add(errorLabel);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error fetching doctor data.");
            errorLabel.setForeground(Color.RED);
            doctorDataFrame.add(errorLabel);
        }

        // Add a close button
        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> doctorDataFrame.dispose());
        doctorDataFrame.add(closeButton);

        // Set window size and visibility
        doctorDataFrame.setSize(500, 400);
        doctorDataFrame.setVisible(true);

        // Add window closing functionality
        doctorDataFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                doctorDataFrame.dispose(); // Close the doctor data window
            }
        });
    }


    // Method to display all user data except the password
    public static void showUserData(int userID) {
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


    public void LoginWindow() {
        // Set the window title
        setTitle("Login Window");

        // Set the layout manager
        setLayout(new FlowLayout());

        // Initialize components
        userLabel = new Label("User ID:");
        passLabel = new Label("Password:");
        userField = new TextField(20);
        passField = new TextField(20);
        passField.setEchoChar('*'); // Mask the password input
        loginButton = new Button("Login");

        // Add components to the frame
        add(userLabel);
        add(userField);
        add(passLabel);
        add(passField);
        add(loginButton);

        // Button click event
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = userField.getText();
                String password = passField.getText();
                System.out.println("User ID: " + userID);
                System.out.println("Password: " + password);
                // Here you can add authentication logic
                Integer ID = Integer.parseInt(userID);
                String authentified = authenticateUser(ID, password);
                if (authentified.equals("6")) {
                    AdminView admin = new AdminView( ID, password, url, uid, pw);
                }
                else if (authentified.equals("4")) {
                    DoctorView doc = new DoctorView(ID, password);
                }
                else if (authentified.equals("1")) {
                    HRView HR = new HRView(ID);
                }
                else if (authentified.equals("5")) {
                    ReceptionistView R = new ReceptionistView(ID);
                }
                else System.out.println("error authentifying user");
            }
        });

        // Set window size and visibility
        setSize(300, 200);
        setVisible(true);



        // Add window closing functionality
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }



    public DatabaseConnector() {
        init();
        LoginWindow();
    }
}
