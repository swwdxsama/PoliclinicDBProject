import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginWindow extends Frame {
    // Declare components
    private Label userLabel, passLabel;
    private TextField userField, passField;
    private Button loginButton;

    public LoginWindow() {
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

    public static void main(String[] args) {
        // Create an instance of the LoginWindow class
        new LoginWindow();
    }
}
