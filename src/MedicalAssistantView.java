public class MedicalAssistantView {

    private static String url = "jdbc:mysql://localhost:3306/projectpoliclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String uid = "root";
    private static String pw = "AdelinMihai06*";
    private int assistantId;

    public MedicalAssistantView(int assistantId) {
        this.assistantId = assistantId;
        createGUI();
    }

    private void createGUI() {
        Frame frame = new Frame("Medical Assistant Dashboard");
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());

        // Add Note Button
        Button addNoteButton = new Button("Add Note");
        addNoteButton.addActionListener(e -> addNoteGUI());
        frame.add(addNoteButton);

        // View Appointments Button
        Button viewAppointmentsButton = new Button("View Appointments");
        viewAppointmentsButton.addActionListener(e -> viewAppointmentsGUI());
        frame.add(viewAppointmentsButton);

        // View Doctors Button
        Button viewDoctorsButton = new Button("View Doctors");
        viewDoctorsButton.addActionListener(e -> viewDoctorsGUI());
        frame.add(viewDoctorsButton);

        // Close Button
        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> frame.dispose());
        frame.add(closeButton);

        frame.setVisible(true);
    }

    private void addNoteGUI() {
        Frame addNoteFrame = new Frame("Add Note");
        addNoteFrame.setSize(400, 300);
        addNoteFrame.setLayout(new FlowLayout());

        Label appointmentLabel = new Label("Appointment ID:");
        TextField appointmentField = new TextField(20);
        Label noteLabel = new Label("Note:");
        TextArea noteArea = new TextArea(5, 30);
        Button saveButton = new Button("Save Note");

        saveButton.addActionListener(e -> {
            String appointmentId = appointmentField.getText();
            String note = noteArea.getText();
            if (!appointmentId.isEmpty() && !note.isEmpty()) {
                addNoteToDatabase(Integer.parseInt(appointmentId), note);
                addNoteFrame.dispose();
            }
        });

        addNoteFrame.add(appointmentLabel);
        addNoteFrame.add(appointmentField);
        addNoteFrame.add(noteLabel);
        addNoteFrame.add(noteArea);
        addNoteFrame.add(saveButton);

        addNoteFrame.setVisible(true);
    }

    private void addNoteToDatabase(int appointmentId, String note) {
        String query = "INSERT INTO notes (AppointmentID, NoteContent) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, appointmentId);
            pstmt.setString(2, note);
            pstmt.executeUpdate();
            System.out.println("Note added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewAppointmentsGUI() {
        Frame viewAppointmentsFrame = new Frame("View Appointments");
        viewAppointmentsFrame.setSize(900, 400);
        viewAppointmentsFrame.setLayout(new FlowLayout());

        TextArea appointmentsArea = new TextArea(20, 100);
        appointmentsArea.setEditable(false);
        List<String[]> appointments = fetchAppointments();

        appointmentsArea.append(String.format("%-15s %-20s %-20s %-20s\n", "AppointmentID", "Patient Name", "Date", "Time"));
        for (String[] appointment : appointments) {
            appointmentsArea.append(String.format("%-15s %-20s %-20s %-20s\n", appointment[0], appointment[1], appointment[2], appointment[3]));
        }

        viewAppointmentsFrame.add(appointmentsArea);

        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> viewAppointmentsFrame.dispose());
        viewAppointmentsFrame.add(closeButton);

        viewAppointmentsFrame.setVisible(true);
    }

    private List<String[]> fetchAppointments() {
        List<String[]> appointments = new ArrayList<>();
        String query = "SELECT a.AppointmentID, CONCAT(p.FirstName, ' ', p.LastName) AS PatientName, a.Date, a.Time " +
                "FROM appointments a JOIN patients p ON a.PatientID = p.PatientID";
        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] appointment = new String[]{
                        String.valueOf(rs.getInt("AppointmentID")),
                        rs.getString("PatientName"),
                        rs.getDate("Date").toString(),
                        rs.getTime("Time").toString()
                };
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private void viewDoctorsGUI() {
        Frame viewDoctorsFrame = new Frame("View Doctors");
        viewDoctorsFrame.setSize(900, 400);
        viewDoctorsFrame.setLayout(new FlowLayout());

        TextArea doctorsArea = new TextArea(20, 100);
        doctorsArea.setEditable(false);
        List<String[]> doctors = fetchDoctors();

        doctorsArea.append(String.format("%-15s %-20s %-20s %-20s\n", "DoctorID", "Name", "Specialization", "Phone"));
        for (String[] doctor : doctors) {
            doctorsArea.append(String.format("%-15s %-20s %-20s %-20s\n", doctor[0], doctor[1], doctor[2], doctor[3]));
        }

        viewDoctorsFrame.add(doctorsArea);

        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> viewDoctorsFrame.dispose());
        viewDoctorsFrame.add(closeButton);

        viewDoctorsFrame.setVisible(true);
    }

    private List<String[]> fetchDoctors() {
        List<String[]> doctors = new ArrayList<>();
        String query = "SELECT d.DoctorID, CONCAT(u.FirstName, ' ', u.LastName) AS Name, d.Specialization, u.PhoneNumber " +
                "FROM doctors d JOIN users u ON d.UserID = u.UserID";
        try (Connection conn = DriverManager.getConnection(url, uid, pw);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] doctor = new String[]{
                        String.valueOf(rs.getInt("DoctorID")),
                        rs.getString("Name"),
                        rs.getString("Specialization"),
                        rs.getString("PhoneNumber")
                };
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }
}
