import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistView extends Frame {
    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "root";

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
                findAppointment();
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

        Button viewPoliclinicsButton = new Button("View policlinics");
        viewPoliclinicsButton.addActionListener(e -> showPoliclinics());
        add(viewPoliclinicsButton);
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

    // Method to fetch appointments for the receptionist from the database
    private List<String[]> getAppointments(Integer UserID) {
        List<String[]> appointments = new ArrayList<>();
        String query = """
        
                SELECT a.AppointmentID, a.Patient, a.User, a.Service, a.DateTime, a.Status, a.Notes, p.FirstName, p.LastName
                FROM appointments a
                Join pacients p on a.Patient = p.PacientID
                WHERE a.User = ?
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
                            rs.getString("Notes"),
                            rs.getString("FirstName"),
                            rs.getString("LastName")
                    };
                    appointments.add(transRow);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public void AppointmentsFound(int ID) {
        List<String[]> appointments = getAppointments(ID);
        Frame frame = new Frame("Search Results");
        if (appointments.isEmpty()) {
            frame.setSize(200, 200);
            frame.setVisible(true);
            Label notFoundLabel = new Label("You don't have any appointments!");
            frame.add(notFoundLabel);
            Button ExitButton = new Button("Exit");
            ExitButton.addActionListener(e -> frame.dispose());
            frame.add(ExitButton);
        }
        else {
            TextArea textArea = new TextArea();
            textArea.setBounds(30, 40, 800, 300);
            textArea.setEditable(false);

            // Add column names as the header
            textArea.append(String.format("%-10s %-15s %-15s %-15s %-15s %-15s %-25s %-25s %-25s\n",
                    "AppointmentID", "Patient", "User", "Service", "Date Time", "Status", "Notes", "First Name", "Last Name"));

            // Add data rows to the TextArea
            for (String[] row : appointments) {
                textArea.append(String.format("%-10s %-15s %-15s %-15s %-15s %-15s %-25s %-25s %-25s\n",
                        row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8]));
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
        Label cnp = new Label("CNP:");
        TextField cnpField = new TextField(15);
        Label adress = new Label("Adress:");
        TextField adressFiled = new TextField(15);
        Label email = new Label("Email:");
        TextField emailField = new TextField(15);
        Button addButton = new Button("Add Patient");

        // Add components to the frame
        addPatientFrame.setLayout(new FlowLayout());
        addPatientFrame.add(firstNameLabel);
        addPatientFrame.add(firstNameField);
        addPatientFrame.add(lastNameLabel);
        addPatientFrame.add(lastNameField);
        addPatientFrame.add(phoneLabel);
        addPatientFrame.add(phoneField);
        addPatientFrame.add(cnp);
        addPatientFrame.add(cnpField);
        addPatientFrame.add(adress);
        addPatientFrame.add(adressFiled);
        addPatientFrame.add(email);
        addPatientFrame.add(emailField);
        addPatientFrame.add(addButton);

        // Action for the add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String phone = phoneField.getText();
                String cnp = cnpField.getText();
                String adress = adressFiled.getText();
                String email = emailField.getText();
                boolean success = insertPatient(firstName, lastName, cnp, adress, phone, email);

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


    private void  findAppointment() {
        Frame frame = new Frame();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        frame.setTitle("Find User");
        frame.setSize(400, 700);
        frame.setLayout(new GridBagLayout());
        frame.setVisible(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.gridwidth = 1;
        TextField id = new TextField(20);


        TextField[] textFields = {id};
        String[] labels = {"Enter doctor's ID:"};

        for (int i = 0; i < textFields.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            frame.add(new Label(labels[i]), gbc);

            gbc.gridx = 1;
            frame.add(textFields[i], gbc);
        }
        Button submitButton = new Button("Submit");
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(submitButton, gbc);
        gbc.gridwidth = 1;
        Button CloseButton = new Button("Close");
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(CloseButton, gbc);
        CloseButton.addActionListener(e -> frame.dispose());

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer IDInput = null;
                if (!id.getText().isEmpty()) {
                    try {
                        IDInput = Integer.parseInt(id.getText());
                    }catch (NumberFormatException e1) {
                        throw new IllegalArgumentException("Invalid ID input");
                    }
                }

                AppointmentsFound(IDInput);
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }


    // Method to insert a new patient into the database
    private boolean insertPatient(String firstName, String lastName, String CNP, String Adress, String phone, String email) {
        String query = "INSERT INTO pacients (FirstName, LastName, CNP, Adress, PhoneNumber, email) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, CNP);
            pstmt.setString(4, Adress);
            pstmt.setString(5, phone);
            pstmt.setString(6, email);
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
                SELECT  u.UserID, u.FirstName, u.LastName, u.Salary
                                FROM users u

                """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] salary = new String[]{
                        String.valueOf(rs.getInt("UserID")),
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

    private List<String[]> fetchPoliclinics(){
        List<String[]> policlinic = new ArrayList<>();
        String query = """
                SELECT p.ClinicID, p.Name, p.Adress, s.Monday, s.Tuesday, s.Wednesday, s.Thursday, s.Friday, s.Saturday, s.Sunday
                from policlinics p
                join schedules s on p.Schedule = s.ScheduleID
                """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] userRow = new String[]{
                        String.valueOf(rs.getInt("ClinicID")),
                        rs.getString("Name"),
                        rs.getString("Adress"),
                        rs.getString("Monday"),
                        rs.getString("Tuesday"),
                        rs.getString("Wednesday"),
                        rs.getString("Thursday"),
                        rs.getString("Friday"),
                        rs.getString("Saturday"),
                        rs.getString("Sunday")
                };
                policlinic.add(userRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return policlinic;
    }

    public void showPoliclinics() {
        // Create a new frame (window)
        Frame userDataFrame = new Frame("Policlinics");

        // Fetch user data
        List<String[]> policlinics = fetchPoliclinics();

        // Create a TextArea to display the data in a table-like format
        TextArea textArea = new TextArea();
        textArea.setBounds(30, 40, 800, 300);
        textArea.setEditable(false);

        // Add column names as the header
        textArea.append(String.format("%-10s %-30s %-30s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n",
                "ID", "Name", "Adress", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));

        // Add data rows to the TextArea
        for (String[] row : policlinics) {
            textArea.append(String.format("%-10s %-30s %-30s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n",
                    row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9]));
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
