import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorView extends JFrame {
    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "root";

    private List<String[]> getAppointments(Integer UserID) {
        List<String[]> appointments = new ArrayList<>();
        String query = """
        SELECT AppointmentID, Patient, User, Service, DateTime, Status, Notes
        FROM appointments
        WHERE User = ?
        """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, UserID); // Set the UserID parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] transRow = new String[]{
                            String.valueOf(rs.getInt("AppointmentID")),
                            String.valueOf(rs.getInt("Patient")),
                            String.valueOf(rs.getInt("User")),
                            String.valueOf(rs.getInt("Service")),
                            String.valueOf(rs.getTimestamp("DateTime")),
                            rs.getString("Status"),
                            rs.getString("Notes")
                    };
                    appointments.add(transRow);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }



    public DoctorView(int userID, String password) {
        // Create a new frame for the admin view
        Frame doctorFrame = new Frame("Admin View");

        // Set the layout
        doctorFrame.setLayout(new FlowLayout());

        // Add a welcome label
        Label welcomeLabel = new Label("Welcome, Doctor!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.BLUE);
        doctorFrame.add(welcomeLabel);

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
                    doctorFrame.add(idLabel);
                    doctorFrame.add(nameLabel);
                    doctorFrame.add(lastNameLabel);
                } else {
                    Label errorLabel = new Label("No data found for User ID: " + userID);
                    errorLabel.setForeground(Color.RED);
                    doctorFrame.add(errorLabel);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Label errorLabel = new Label("Error fetching user data.");
                errorLabel.setForeground(Color.RED);
                doctorFrame.add(errorLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error fetching user data.");
            errorLabel.setForeground(Color.RED);
            doctorFrame.add(errorLabel);
        }

        // Add a button to show detailed user data
        Button showDataButton = new Button("Show User Data");

        showDataButton.addActionListener(e -> DatabaseConnector.showDoctorData(userID));

        doctorFrame.add(showDataButton);


        // Add a logout button
        Button logoutButton = new Button("Logout");
        logoutButton.addActionListener(e -> {
            doctorFrame.dispose(); // Close the admin window
            setVisible(true); // Show the login window again
        });
        doctorFrame.add(logoutButton);

        // Set window size and visibility
        doctorFrame.setSize(300, 200);
        doctorFrame.setVisible(true);

        // Add window closing functionality
        doctorFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                doctorFrame.dispose(); // Close the admin window
                setVisible(true); // Show the login window again
            }
        });

        // Hide the login window
        setVisible(false);
    }


}
