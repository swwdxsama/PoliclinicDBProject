import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ReceptionistView extends JFrame{
    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "AdelinMihai06*";

    public void InsertPatient(String CNP, String FirstName, String LastName, String Address, String Email, String Phone){
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, uid, pw);

            // Prepare the SQL query with placeholders
            String query = "INSERT INTO pacients (CNP, FirstName, LastName, Adress, email, PhoneNumber) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the values in the query
            statement.setString(1, CNP);
            statement.setString(2, FirstName);
            statement.setString(3, LastName);
            statement.setString(4, Address);
            statement.setString(5, Email);
            statement.setString(6, Phone);
            // Execute the query
            int rowsInserted = statement.executeUpdate();

            // Check if the insert was successful
            if (rowsInserted > 0) {
                System.out.println("Pacient data inserted successfully!");
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void AddPattientWindow(){
        Frame frame = new Frame();
        frame.setTitle("Add Patient");
        frame.setSize(400, 600);
        frame.setLayout(new GridBagLayout());
        frame.setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(new Label("Input data for new patient:"), gbc);

        gbc.gridwidth = 1;

        TextField cnp = new TextField(20);
        TextField firstName = new TextField(20);
        TextField lastName = new TextField(20);
        TextField address = new TextField(20);
        TextField email = new TextField(20);
        TextField phone = new TextField(20);


        TextField[] textFields = {cnp, firstName, lastName, address, email, phone};
        String[] labels = {"Enter CNP:", "First Name:", "Last Name:", "Address:", "Email:", "Phone:"};

        for (int i = 0; i < textFields.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            frame.add(new Label(labels[i]), gbc);

            gbc.gridx = 1;
            frame.add(textFields[i], gbc);
        }



        Button submitButton = new Button("Submit");
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(submitButton, gbc);
        gbc.gridwidth = 1;

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    String CNPInput = cnp.getText();
                    String FirstNameInput = firstName.getText();
                    String LastNameInput = lastName.getText();

                    String AddressInput = address.getText();
                    String EmailInput = email.getText();
                    String PhoneInput = phone.getText();

                    InsertPatient(CNPInput, FirstNameInput, LastNameInput, AddressInput, EmailInput, PhoneInput);
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }
//update

    public ReceptionistView(int userID) {
        Frame frame = new Frame("Receptionist");
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());
        frame.setVisible(true);
        Label welcome = new Label("Welcome HR user");
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
        Button showUserData = new Button("Show User Data");
        showUserData.addActionListener(e -> DatabaseConnector.showUserData(userID));
        frame.add(showUserData);
        Button addPatientButton = new Button("Add Patient");
        addPatientButton.addActionListener(e -> AddPattientWindow());
        frame.add(addPatientButton);
        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> frame.dispose());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        frame.add(closeButton);
    }
}
