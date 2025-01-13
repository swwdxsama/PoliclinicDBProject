import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public void addNote(String note, int appointmentID){
        String query = "update appointments " +
                "set Notes = ? " +
                "where AppointmentID = ?;";
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, uid, pw);
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the values in the query
            statement.setString(1, note);
            statement.setString(2, String.valueOf(appointmentID));
            // Execute the query
            int rowsInserted = statement.executeUpdate();

            // Check if the insert was successful
            if (rowsInserted > 0) {
                System.out.println("Note added successfully");
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDiagnostic(String note, int appointmentID){
        String query = "update consultation " +
                "set diagnostic = ? " +
                "where ConsultationID = ?;";
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, uid, pw);
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the values in the query
            statement.setString(1, note);
            statement.setString(2, String.valueOf(appointmentID));
            // Execute the query
            int rowsInserted = statement.executeUpdate();

            // Check if the insert was successful
            if (rowsInserted > 0) {
                System.out.println("Diagnose added successfully");
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDiagnosisWindow(int userID){
        Frame frame = new Frame();
        frame.setTitle("Add diagnosis");
        frame.setSize(500, 300);
        frame.setLayout(new GridBagLayout());
        frame.setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        frame.add(new Label("Enter the consultation ID and a diagnosis:"), gbc);

        gbc.gridwidth = 1;

        TextField AppointmentID = new TextField(20);
        TextField Note = new TextField(20);


        TextField[] textFields = {AppointmentID, Note};
        String[] labels = {"Enter consultation ID:", "Diagnosis:"};

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
        Button closeButton = new Button("Close");
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        closeButton.addActionListener(e -> frame.dispose());
        frame.add(closeButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String AppointmentIDInput = AppointmentID.getText();
                String NoteInput = Note.getText();
                try {
                    addDiagnostic(NoteInput, Integer.parseInt(AppointmentIDInput));
                }catch (Exception NumberFormatException) {
                    System.out.println("invalid ID input");
                }
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                frame.dispose(); // Close the admin window
                setVisible(true); // Show the login window again
            }
        });

    }

    public void  addConcediuWindow(){

    }

    public void modifyAppointmentWindow(int userID){
        Frame frame = new Frame();
        frame.setTitle("Add note");
        frame.setSize(500, 300);
        frame.setLayout(new GridBagLayout());
        frame.setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        frame.add(new Label("Enter the appointment ID and a note for this appointment:"), gbc);

        gbc.gridwidth = 1;

        TextField AppointmentID = new TextField(20);
        TextField Note = new TextField(20);


        TextField[] textFields = {AppointmentID, Note};
        String[] labels = {"Enter Appointment ID:", "Note:"};

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
        Button closeButton = new Button("Close");
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        closeButton.addActionListener(e -> frame.dispose());
        frame.add(closeButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String AppointmentIDInput = AppointmentID.getText();
                String NoteInput = Note.getText();
                try {
                    addNote(NoteInput, Integer.parseInt(AppointmentIDInput));
                }catch (Exception NumberFormatException) {
                    System.out.println("invalid ID input");
                }
                }
        });
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                frame.dispose(); // Close the admin window
                setVisible(true); // Show the login window again
            }
        });

    }

    public void addAppointmentQuery(int UserID, String PatientID, String Service, String DateTime, String Status, String Notes) {
        String query = "INSERT INTO appointments (Patient, User, Service, DateTime, Status, Notes) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, uid, pw);
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the values in the query
            statement.setString(1, PatientID);
            statement.setString(2, String.valueOf(UserID));
            statement.setString(3, Service);
            statement.setString(4, DateTime);
            statement.setString(5, Status);
            statement.setString(6, Notes);
            // Execute the query
            int rowsInserted = statement.executeUpdate();

            // Check if the insert was successful
            if (rowsInserted > 0) {
                System.out.println("Appointment added successfully");
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        }

    private List<String[]> getConsultations(Integer UserID) {
        List<String[]> appointments = new ArrayList<>();
        String query = """
        
                SELECT c.ConsultationID, c.ConsultationDate, c.Diagnostic, p.FirstName, p.LastName
                FROM consultation c
                Join pacients p on c.Patient = p.PacientID
                WHERE c.medic = ?
        """;

        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, UserID); // Set the UserID parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] transRow = new String[]{
                            String.valueOf(rs.getInt("ConsultationID")),
                            String.valueOf(rs.getTimestamp("ConsultationDate")),
                            String.valueOf(rs.getString("Diagnostic")),
                            String.valueOf(rs.getString("FirstName")),
                            String.valueOf(rs.getString("LastName")),
                    };
                    appointments.add(transRow);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public void ConsultationsFound(int ID) {
        List<String[]> appointments = getConsultations(ID);
        Frame frame = new Frame("Search Results");
        if (appointments.isEmpty()) {
            frame.setSize(200, 200);
            frame.setVisible(true);
            Label notFoundLabel = new Label("You don't have any consultations!");
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
            textArea.append(String.format("%-10s %-20s %-50s %-30s %-30s\n",
                    "ConsultationID", "Consultation Date", "Diagnostic", "First Name", "Last Name"));

            // Add data rows to the TextArea
            for (String[] row : appointments) {
                textArea.append(String.format("%-10s %-20s %-50s %-30s %-30s\n",
                        row[0], row[1], row[2], row[3], row[4]));
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


    public void addConsultation(int PatientID, int DoctorID, String Diagnosis, String DateTime, int appointmentID) {
        String query = "insert into consultation (ConsultationDate, Diagnostic, Medic, Appointment, Patient) values (?, ?, ?, ?, ?) ";
        try {
            // Connect to the databases
            Connection connection = DriverManager.getConnection(url, uid, pw);
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the values in the query
            statement.setString(1, DateTime);
            statement.setString(2, Diagnosis);
            statement.setString(3, String.valueOf(DoctorID));
            statement.setString(4, String.valueOf(appointmentID));
            statement.setString(5, String.valueOf(PatientID));
            // Execute the query
            int rowsInserted = statement.executeUpdate();

            // Check if the insert was successful
            if (rowsInserted > 0) {
                System.out.println("ConsultaTion added successfully");
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addConsultationWindow(int userID){
        Frame frame = new Frame();
        frame.setTitle("Add Consultation");
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
        frame.add(new Label("Input data for a consultation:"), gbc);

        gbc.gridwidth = 1;

        TextField PatientID = new TextField(20);
        TextField AppointmentID = new TextField(20);
        TextField Diagnostic = new TextField(20);
        TextField DateTime = new TextField(20);
        //TextField Notes = new TextField(20);



        TextField[] textFields = {PatientID, AppointmentID, Diagnostic, DateTime};
        String[] labels = {"Enter PatientID:", "AppointmentID:", "Diagnosis:", "Date and time:"};

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
        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> frame.dispose());
        frame.add(closeButton, gbc);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String PacientIDInput = PatientID.getText();
                String AppointmentIDInput = AppointmentID.getText();
                String DiagnosticInput = Diagnostic.getText();
                String DateTimeInput = DateTime.getText();
                addConsultation(Integer.parseInt(PacientIDInput), userID, DiagnosticInput, DateTimeInput, Integer.parseInt(AppointmentIDInput));
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public void addAppointmentWindow(int userID){
        Frame frame = new Frame();
        frame.setTitle("Add Appointment");
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
        frame.add(new Label("Input data for a new appointment:"), gbc);

        gbc.gridwidth = 1;

        TextField PatientID = new TextField(20);
        TextField Service = new TextField(20);
        TextField DateTime = new TextField(20);
        TextField Status = new TextField(20);
        TextField Notes = new TextField(20);



        TextField[] textFields = {PatientID, Service, DateTime, Status, Notes};
        String[] labels = {"Enter PatientID:", "Service:", "Date and Time:", "Status:", "Notes:"};

        for (int i = 0; i < textFields.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            frame.add(new Label(labels[i]), gbc);

            gbc.gridx = 1;
            frame.add(textFields[i], gbc);
        }

        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> frame.dispose());
        frame.add(closeButton, gbc);

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

                String PacientIDInput = PatientID.getText();
                String ServiceInput = Service.getText();
                String DateTimeInput = DateTime.getText();
                String StatusInput = Status.getText();
                String NotesInput = Notes.getText();

                addAppointmentQuery(userID, PacientIDInput, ServiceInput, DateTimeInput, StatusInput, NotesInput);
                //InsertPatient(CNPInput, FirstNameInput, LastNameInput, AddressInput, EmailInput, PhoneInput);
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
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


    public DoctorView(int userID, String password) {
        // Create a new frame for the admin view
        Frame doctorFrame = new Frame("Doctor View");

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

        Button showAppointmentsButton = new Button("Show Appointments");
        showAppointmentsButton.addActionListener(e -> AppointmentsFound(userID));
        doctorFrame.add(showAppointmentsButton);
        Button showConsultationsButton = new Button("Show Consultations");
        showConsultationsButton.addActionListener(e -> ConsultationsFound(userID));
        doctorFrame.add(showConsultationsButton);

        Button addAppointmentButton = new Button("Add Appointment");
        addAppointmentButton.addActionListener(e -> addAppointmentWindow(userID));
        doctorFrame.add(addAppointmentButton);
        Button addConsultationButton = new Button("Add Consultation");
        addConsultationButton.addActionListener(e -> addConsultationWindow(userID));
        doctorFrame.add(addConsultationButton);

        Button AddNote = new Button("Add Note to an appointment");
        AddNote.addActionListener(e -> modifyAppointmentWindow(userID));
        doctorFrame.add(AddNote);

        Button AddDiagnosis = new Button("Add Diagnosis");
        AddDiagnosis.addActionListener(e -> addDiagnosisWindow(userID));
        doctorFrame.add(AddDiagnosis);
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
