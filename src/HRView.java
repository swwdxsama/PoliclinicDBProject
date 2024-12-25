import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HRView extends JFrame {
    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "root";

    public void inputUser(String cnp, String firstName, String lastName, String password, String address, String email, String phone, String iban, float salary, String department, int role) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, uid, pw);

            // Prepare the SQL query with placeholders
            String query = "INSERT INTO users (CNP, FirstName, LastName, password, Adress, Email, PhoneNumber, IBAN, Salary, Department, Role, HireDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the values in the query
            statement.setString(1, cnp);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, password);
            statement.setString(5, address);
            statement.setString(6, email);
            statement.setString(7, phone);
            statement.setString(8, iban);
            statement.setFloat(9, salary);
            statement.setString(10, department);
            statement.setInt(11, role);
            statement.setDate(12, new java.sql.Date(System.currentTimeMillis()));

            // Execute the query
            int rowsInserted = statement.executeUpdate();

            // Check if the insert was successful
            if (rowsInserted > 0) {
                System.out.println("User data inserted successfully!");
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void AddUserWindow() {
        Frame frame = new Frame();
        frame.setTitle("Add User");
        frame.setSize(400, 600);
        frame.setLayout(new GridBagLayout());
        frame.setVisible(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(new Label("Input data for new user:"), gbc);

        gbc.gridwidth = 1;

        TextField cnp = new TextField(20);
        TextField firstName = new TextField(20);
        TextField lastName = new TextField(20);
        TextField password = new TextField(20);
        TextField address = new TextField(20);
        TextField email = new TextField(20);
        TextField phone = new TextField(20);
        TextField iban = new TextField(20);
        TextField salary = new TextField(20);
        TextField department = new TextField(20);
        Choice role = new Choice();
        role.add("HR");
        role.add("Doctor");
        role.add("Economic");
        role.add("Asistent Medical");
        role.add("Receptionist");

        TextField[] textFields = {cnp, firstName, lastName, password, address, email, phone, iban, salary, department};
        String[] labels = {"Enter CNP:", "First Name:", "Last Name:", "Password:", "Address:", "Email:", "Phone:", "IBAN:", "Salary:", "Department:"};

        for (int i = 0; i < textFields.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            frame.add(new Label(labels[i]), gbc);

            gbc.gridx = 1;
            frame.add(textFields[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = 11;
        frame.add(new Label("Role:"), gbc);

        gbc.gridx = 1;
        frame.add(role, gbc);

        Button submitButton = new Button("Submit");
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(submitButton, gbc);
        gbc.gridwidth = 1;

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String CNPInput = cnp.getText();
                    String FirstNameInput = firstName.getText();
                    String LastNameInput = lastName.getText();
                    String PasswordInput = password.getText();
                    String AddressInput = address.getText();
                    String EmailInput = email.getText();
                    String PhoneInput = phone.getText();
                    String IBANInput = iban.getText();
                    float Salary = Float.parseFloat(salary.getText());
                    String DepartmentInput = department.getText();
                    int RoleInput = role.getSelectedIndex() + 1;

                    // Call your inputUser method here
                    inputUser(CNPInput, FirstNameInput, LastNameInput, PasswordInput, AddressInput, EmailInput, PhoneInput, IBANInput, Salary, DepartmentInput, RoleInput);

                } catch (NumberFormatException ex) {
                    System.err.println("Invalid salary input!");
                    Dialog d = new Dialog(frame, "Error", true);
                    d.add(new Label("Please enter a valid number for salary."));
                    d.pack();
                    d.setVisible(true);
                }
            }
        });
    }

    public HRView(int userID) {
        Frame HRFrame = new Frame();
        HRFrame.setTitle("HR View");
        HRFrame.setSize(300, 200);
        HRFrame.setLayout(new FlowLayout());
        HRFrame.setVisible(true);
        Label welcome = new Label("Welcome HR user");
        welcome.setFont(new Font("Arial", Font.BOLD, 16));
        welcome.setForeground(Color.BLUE);
        HRFrame.add(welcome);
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
                    HRFrame.add(idLabel);
                    HRFrame.add(nameLabel);
                    HRFrame.add(lastNameLabel);
                } else {
                    Label errorLabel = new Label("No data found for User ID: " + userID);
                    errorLabel.setForeground(Color.RED);
                    HRFrame.add(errorLabel);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Label errorLabel = new Label("Error fetching user data.");
                errorLabel.setForeground(Color.RED);
                HRFrame.add(errorLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error fetching user data.");
            errorLabel.setForeground(Color.RED);
            HRFrame.add(errorLabel);
        }
        Button ShowUsersButton = new Button("Show Users");
        ShowUsersButton.addActionListener(e -> DatabaseConnector.showUserDataAWT());
        Button ShowUserData = new Button("Show User Data");
        ShowUserData.addActionListener(e -> DatabaseConnector.showUserData(userID));
        Button AddUserButton = new Button("Add User");
        AddUserButton.addActionListener(e -> AddUserWindow());
        Button ModifyUserButton = new Button("Modify User");
        Button DeleteUserButton = new Button("Delete User");
        Button LogOutButton = new Button("Log Out");
        LogOutButton.addActionListener(e -> {
            HRFrame.dispose(); // Close the admin window
            setVisible(true); // Show the login window again
        });
        HRFrame.add(ShowUserData);
        HRFrame.add(ShowUsersButton);
        HRFrame.add(AddUserButton);
        HRFrame.add(ModifyUserButton);
        HRFrame.add(DeleteUserButton);
        HRFrame.add(LogOutButton);
    }

}
