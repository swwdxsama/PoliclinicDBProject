import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistView extends Frame {
    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "AdelinMihai06*";

    private int userID;
    private Label welcomeLabel;
    private Button viewAppointmentsButton, addPatientButton, addBonFiscalButton, addAppointmentButton, viewSalaryButton,
            addEmployeeDetailsButton, viewEmployeeDetailsButton, logoutButton;

    // Constructor
    public ReceptionistView(int userID) {
        this.userID = userID;
        init();
    }

    // Initialize the interface for the receptionist
    private void init() {
        setTitle("Receptionist Dashboard");
        setSize(500, 400);
        setLayout(new FlowLayout());

        // Welcome label
        welcomeLabel = new Label("Welcome, Receptionist " + userID);
        add(welcomeLabel);

        // Button to view appointments
        viewAppointmentsButton = new Button("View Appointments");
        viewAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAppointments();
            }
        });
        add(viewAppointmentsButton);

        // Button to add a new patient
        addPatientButton = new Button("Add Patient");
        addPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPatient();
            }
        });
        add(addPatientButton);

        // Button to add a new Bon Fiscal
        addBonFiscalButton = new Button("Add Bon Fiscal");
        addBonFiscalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBonFiscal();
            }
        });
        add(addBonFiscalButton);

        // Button to add a new appointment
        addAppointmentButton = new Button("Add Appointment");
        addAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAppointment();
            }
        });
        add(addAppointmentButton);

        // Button to view salary
        viewSalaryButton = new Button("View Salary");
        viewSalaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewSalary();
            }
        });
        add(viewSalaryButton);

        // Button to add employee details
        addEmployeeDetailsButton = new Button("Add Employee Details");
        addEmployeeDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployeeDetails();
            }
        });
        add(addEmployeeDetailsButton);

        // Button to view employee details
        viewEmployeeDetailsButton = new Button("View Employee Details");
        viewEmployeeDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewEmployeeDetails();
            }
        });
        add(viewEmployeeDetailsButton);

        // Button to logout
        logoutButton = new Button("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DatabaseConnector(); // Opens the login window again
            }
        });
        add(logoutButton);

        setVisible(true);

        // Add window closing functionality
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    // Method to show appointments for the receptionist
    private void showAppointments() {
        // Create a new frame to display appointments
        Frame appointmentFrame = new Frame("Appointments");

        // Fetch appointments from the database
        List<String[]> appointments = fetchAppointments();

        // Create a TextArea to display the data
        TextArea textArea = new TextArea();
        textArea.setBounds(30, 40, 800, 300);
        textArea.setEditable(false);

        // Add header for appointments
        textArea.append(String.format("%-10s %-20s %-20s %-10s %-15s\n", "PatientID", "First Name", "Last Name", "Date", "Doctor"));

        // Add data rows to the TextArea
        for (String[] appointment : appointments) {
            textArea.append(String.format("%-10s %-20s %-20s %-10s %-15s\n",
                    appointment[0], appointment[1], appointment[2], appointment[3], appointment[4]));
        }

        // Add the TextArea to the frame
        appointmentFrame.add(textArea);

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setBounds(350, 350, 100, 30);
        closeButton.addActionListener(e -> appointmentFrame.dispose());
        appointmentFrame.add(closeButton);

        // Set the layout and size
        appointmentFrame.setLayout(null);
        appointmentFrame.setSize(900, 400);
        appointmentFrame.setVisible(true);
    }

    // Method to fetch appointments for the receptionist from the database
    private List<String[]> fetchAppointments() {
        List<String[]> appointments = new ArrayList<>();
        String query = """
                SELECT a.PatientID, p.FirstName, p.LastName, a.AppointmentDate, d.FirstName AS DoctorFirstName, d.LastName AS DoctorLastName
                FROM appointments a
                JOIN patients p ON a.PatientID = p.PatientID
                JOIN medicalstaff m ON a.DoctorID = m.MedicalID
                JOIN users d ON m.UserID = d.UserID
                WHERE a.ReceptionistID = ?;
                """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userID); // Set the ReceptionistID to the logged-in user's ID
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] appointment = new String[]{
                        String.valueOf(rs.getInt("PatientID")),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("AppointmentDate"),
                        rs.getString("DoctorFirstName") + " " + rs.getString("DoctorLastName")
                };
                appointments.add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    // Method to add a new patient
    private void addPatient() {
        // Create a frame for adding a patient
        Frame addPatientFrame = new Frame("Add Patient");

        // Components for patient data
        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField(20);
        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField(20);
        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField(15);
        Button addButton = new Button("Add Patient");

        // Add components to the frame
        addPatientFrame.setLayout(new FlowLayout());
        addPatientFrame.add(firstNameLabel);
        addPatientFrame.add(firstNameField);
        addPatientFrame.add(lastNameLabel);
        addPatientFrame.add(lastNameField);
        addPatientFrame.add(phoneLabel);
        addPatientFrame.add(phoneField);
        addPatientFrame.add(addButton);

        // Action for the add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String phone = phoneField.getText();
                boolean success = insertPatient(firstName, lastName, phone);

                if (success) {
                    Label successLabel = new Label("Patient added successfully.");
                    addPatientFrame.add(successLabel);
                } else {
                    Label errorLabel = new Label("Error adding patient.");
                    addPatientFrame.add(errorLabel);
                }

                addPatientFrame.revalidate();
                addPatientFrame.repaint();
            }
        });

        addPatientFrame.setSize(300, 200);
        addPatientFrame.setVisible(true);
    }

    // Method to insert a new patient into the database
    private boolean insertPatient(String firstName, String lastName, String phone) {
        String query = "INSERT INTO patients (FirstName, LastName, PhoneNumber) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phone);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0; // Return true if at least one row was inserted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to add a Bon Fiscal (Invoice)
    private void addBonFiscal() {
        Frame addBonFiscalFrame = new Frame("Add Bon Fiscal");

        // Components for Bon Fiscal data
        Label patientIdLabel = new Label("Patient ID:");
        TextField patientIdField = new TextField(20);
        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField(20);
        Label dateLabel = new Label("Date:");
        TextField dateField = new TextField(20);
        Button addButton = new Button("Add Bon Fiscal");

        // Add components to the frame
        addBonFiscalFrame.setLayout(new FlowLayout());
        addBonFiscalFrame.add(patientIdLabel);
        addBonFiscalFrame.add(patientIdField);
        addBonFiscalFrame.add(amountLabel);
        addBonFiscalFrame.add(amountField);
        addBonFiscalFrame.add(dateLabel);
        addBonFiscalFrame.add(dateField);
        addBonFiscalFrame.add(addButton);

        // Action for the add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int patientId = Integer.parseInt(patientIdField.getText());
                double amount = Double.parseDouble(amountField.getText());
                String date = dateField.getText();

                boolean success = insertBonFiscal(patientId, amount, date);
                if (success) {
                    Label successLabel = new Label("Bon Fiscal added successfully.");
                    addBonFiscalFrame.add(successLabel);
                } else {
                    Label errorLabel = new Label("Error adding Bon Fiscal.");
                    addBonFiscalFrame.add(errorLabel);
                }

                addBonFiscalFrame.revalidate();
                addBonFiscalFrame.repaint();
            }
        });

        addBonFiscalFrame.setSize(300, 200);
        addBonFiscalFrame.setVisible(true);
    }

    // Method to insert a Bon Fiscal into the database
    private boolean insertBonFiscal(int patientId, double amount, String date) {
        String query = "INSERT INTO bonfiscal (PatientID, Amount, Date) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, patientId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, date);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0; // Return true if at least one row was inserted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to add an appointment for a patient
    private void addAppointment() {
        Frame addAppointmentFrame = new Frame("Add Appointment");

        // Components for Appointment data
        Label patientIdLabel = new Label("Patient ID:");
        TextField patientIdField = new TextField(20);
        Label doctorIdLabel = new Label("Doctor ID:");
        TextField doctorIdField = new TextField(20);
        Label dateLabel = new Label("Date:");
        TextField dateField = new TextField(20);
        Button addButton = new Button("Add Appointment");

        // Add components to the frame
        addAppointmentFrame.setLayout(new FlowLayout());
        addAppointmentFrame.add(patientIdLabel);
        addAppointmentFrame.add(patientIdField);
        addAppointmentFrame.add(doctorIdLabel);
        addAppointmentFrame.add(doctorIdField);
        addAppointmentFrame.add(dateLabel);
        addAppointmentFrame.add(dateField);
        addAppointmentFrame.add(addButton);

        // Action for the add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int patientId = Integer.parseInt(patientIdField.getText());
                int doctorId = Integer.parseInt(doctorIdField.getText());
                String date = dateField.getText();

                boolean success = insertAppointment(patientId, doctorId, date);
                if (success) {
                    Label successLabel = new Label("Appointment added successfully.");
                    addAppointmentFrame.add(successLabel);
                } else {
                    Label errorLabel = new Label("Error adding appointment.");
                    addAppointmentFrame.add(errorLabel);
                }

                addAppointmentFrame.revalidate();
                addAppointmentFrame.repaint();
            }
        });

        addAppointmentFrame.setSize(300, 200);
        addAppointmentFrame.setVisible(true);
    }

    // Method to insert an appointment into the database
    private boolean insertAppointment(int patientId, int doctorId, String date) {
        String query = "INSERT INTO appointments (PatientID, DoctorID, AppointmentDate) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctorId);
            pstmt.setString(3, date);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to view salary details for an employee
    private void viewSalary() {
        Frame viewSalaryFrame = new Frame("View Salary");

        // Create components for displaying salary data
        TextArea textArea = new TextArea();
        textArea.setBounds(30, 40, 800, 300);
        textArea.setEditable(false);

        // Add headers
        textArea.append(String.format("%-10s %-20s %-20s %-10s\n", "EmployeeID", "First Name", "Last Name", "Salary"));

        // Fetch and display salary data
        List<String[]> salaries = fetchEmployeeSalaries();
        for (String[] salary : salaries) {
            textArea.append(String.format("%-10s %-20s %-20s %-10s\n", salary[0], salary[1], salary[2], salary[3]));
        }

        // Add TextArea to frame
        viewSalaryFrame.add(textArea);

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setBounds(350, 350, 100, 30);
        closeButton.addActionListener(e -> viewSalaryFrame.dispose());
        viewSalaryFrame.add(closeButton);

        // Set layout and size
        viewSalaryFrame.setLayout(null);
        viewSalaryFrame.setSize(900, 400);
        viewSalaryFrame.setVisible(true);
    }

    // Method to fetch employee salary details from the database
    private List<String[]> fetchEmployeeSalaries() {
        List<String[]> salaries = new ArrayList<>();
        String query = """
                SELECT e.EmployeeID, u.FirstName, u.LastName, e.Salary
                FROM employees e
                JOIN users u ON e.UserID = u.UserID;
                """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] salary = new String[]{
                        String.valueOf(rs.getInt("EmployeeID")),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        String.valueOf(rs.getDouble("Salary"))
                };
                salaries.add(salary);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salaries;
    }

    // Method to add employee details
    private void addEmployeeDetails() {
        Frame addEmployeeFrame = new Frame("Add Employee Details");

        // Components for employee data
        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField(20);
        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField(20);
        Label salaryLabel = new Label("Salary:");
        TextField salaryField = new TextField(20);
        Button addButton = new Button("Add Employee");

        // Add components to the frame
        addEmployeeFrame.setLayout(new FlowLayout());
        addEmployeeFrame.add(firstNameLabel);
        addEmployeeFrame.add(firstNameField);
        addEmployeeFrame.add(lastNameLabel);
        addEmployeeFrame.add(lastNameField);
        addEmployeeFrame.add(salaryLabel);
        addEmployeeFrame.add(salaryField);
        addEmployeeFrame.add(addButton);

        // Action for the add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                double salary = Double.parseDouble(salaryField.getText());

                boolean success = insertEmployeeDetails(firstName, lastName, salary);
                if (success) {
                    Label successLabel = new Label("Employee added successfully.");
                    addEmployeeFrame.add(successLabel);
                } else {
                    Label errorLabel = new Label("Error adding employee.");
                    addEmployeeFrame.add(errorLabel);
                }

                addEmployeeFrame.revalidate();
                addEmployeeFrame.repaint();
            }
        });

        addEmployeeFrame.setSize(300, 200);
        addEmployeeFrame.setVisible(true);
    }

    // Method to insert employee details into the database
    private boolean insertEmployeeDetails(String firstName, String lastName, double salary) {
        String query = "INSERT INTO employees (FirstName, LastName, Salary) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setDouble(3, salary);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to view employee details
    private void viewEmployeeDetails() {
        Frame viewEmployeeFrame = new Frame("View Employee Details");

        // Create components for displaying employee data
        TextArea textArea = new TextArea();
        textArea.setBounds(30, 40, 800, 300);
        textArea.setEditable(false);

        // Add headers
        textArea.append(String.format("%-10s %-20s %-20s %-10s\n", "EmployeeID", "First Name", "Last Name", "Salary"));

        // Fetch and display employee data
        List<String[]> employees = fetchEmployeeDetails();
        for (String[] employee : employees) {
            textArea.append(String.format("%-10s %-20s %-20s %-10s\n", employee[0], employee[1], employee[2], employee[3]));
        }

        // Add TextArea to frame
        viewEmployeeFrame.add(textArea);

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setBounds(350, 350, 100, 30);
        closeButton.addActionListener(e -> viewEmployeeFrame.dispose());
        viewEmployeeFrame.add(closeButton);

        // Set layout and size
        viewEmployeeFrame.setLayout(null);
        viewEmployeeFrame.setSize(900, 400);
        viewEmployeeFrame.setVisible(true);
    }

    // Method to fetch employee details from the database
    private List<String[]> fetchEmployeeDetails() {
        List<String[]> employees = new ArrayList<>();
        String query = """
                SELECT e.EmployeeID, u.FirstName, u.LastName, e.Salary
                FROM employees e
                JOIN users u ON e.UserID = u.UserID;
                """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] employee = new String[]{
                        String.valueOf(rs.getInt("EmployeeID")),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        String.valueOf(rs.getDouble("Salary"))
                };
                employees.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }
}
