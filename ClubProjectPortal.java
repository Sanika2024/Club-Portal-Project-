// Import statements
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.Scanner;

public class ClubProjectPortal {
    static final String DB_URL = "jdbc:mysql://localhost:3306/ProjectPortal";
    static final String DB_USER = "root";
    static final String DB_PASS = "Sanika@2025";

    static Scanner sc = new Scanner(System.in);

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        Connection conn = connectToDatabase();
        if (conn == null) return;

        showMainMenu(conn);
    }

    public static void showMainMenu(Connection conn) {
        while (true) {
            System.out.println("\n--- Welcome to Unified Student Platform ---\nSelect user type:\n1. Student\n2. Club\n3. Admin\n4. Exit\nEnter your role: ");
            int role = sc.nextInt(); sc.nextLine();

            switch (role) {
                case 1: studentAccess(conn); break;
                case 2: clubAccess(conn); break;
                case 3: adminLogin(conn); break;
                case 4: System.out.println("Exiting..."); 
                System.exit(0);
                default: System.out.println("Invalid choice.");
            }
        }
    }    

    static Connection connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Connected to database!!!");
            return conn;
        } catch (Exception e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
            return null;
        }
    }

    // ---------- STUDENT SECTION ----------
    static void studentAccess(Connection conn) {
        while (true) {
            System.out.println("\n-- Student Portal --");
            System.out.println("1. Sign Up\n2. Login\n3. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt(); sc.nextLine();
            if (choice == 1) studentSignUp(conn);
            else if (choice == 2) {
                Integer studentId = studentLogin(conn);
                if (studentId != null) studentMenu(conn, studentId);
            }            
            else if (choice == 3) break;
            else System.out.println("Invalid.");
        }
    }

    static void studentSignUp(Connection conn) {
        try {
            System.out.print("Enter student ID: ");
            int id = sc.nextInt();
            sc.nextLine(); // consume newline

            if (studentExists(conn, id)) {
                System.out.println("Student with this ID already exists!");
                return; // or return, or continue based on your logic
            }
            System.out.print("Name: "); String name = sc.nextLine();
            System.out.print("Skills: "); String skills = sc.nextLine();
            System.out.print("Interests: "); String interests = sc.nextLine();
            System.out.print("Username: "); String username = sc.nextLine();
            System.out.print("Password: "); String password = sc.nextLine();

            PreparedStatement ps = conn.prepareStatement("INSERT INTO Student VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);  ps.setString(2, username); ps.setString(3, password); 
            ps.setString(4, name); ps.setString(5, skills); ps.setString(6, interests);
            ps.executeUpdate();
            System.out.println("Signed up successfully!");
        } catch (SQLException e) {
            System.out.println("Error signing up.");
        }
    }

    public static boolean studentExists(Connection conn, int id) throws SQLException {
        String query = "SELECT id FROM student WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
        
    static Integer studentLogin(Connection conn) {
        System.out.print("Username: "); String uname = sc.nextLine();
        System.out.print("Password: "); String pwd = sc.nextLine();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Student WHERE username=? AND password=?");
            ps.setString(1, uname); ps.setString(2, pwd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful!");
                return rs.getInt("id");
            } else {
                System.out.println("Invalid credentials.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error logging in.");
            return null;
        }
    }

    static void studentMenu(Connection conn, int id) {
        while (true) {
            System.out.println("\n-- Student Menu --");
            System.out.println("1. View Profile\n2. Update Profile\n3. Delete Profile\n4. View Clubs\n5. Start Project\n6. Join Project\n7. Get Recommendations\n8. Logout");
            System.out.print("Choice: ");
            int c = sc.nextInt(); sc.nextLine();
            switch (c) {

                case 1: viewStudentProfile(conn, id); break;
                case 2: updateStudentProfile(conn, id); break;
                case 3: deleteOwnStudentProfile(conn, id); break;
                case 4: viewAllClubs(conn); break;
                case 5: startProject(conn, id); break;
                case 6: joinProject(conn, id); break;
                case 7: recommendForStudent(conn, id);break;
                case 8: return;
                default: System.out.println("Invalid.");
            }
        }
    }

    static void viewStudentProfile(Connection conn, int id) {
        System.out.println("\n--- Your Profile ---");
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT username, name, skills, interests FROM Student WHERE id = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                System.out.println("Username : " + rs.getString("username"));
                System.out.println("Name     : " + rs.getString("name"));
                System.out.println("Skills   : " + rs.getString("skills"));
                System.out.println("Interests: " + rs.getString("interests"));
            } else {
                System.out.println("Profile not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    static void updateStudentProfile(Connection conn, int id) {
        System.out.println("\n--- Update Profile ---");
        System.out.println("1. Name\n2. Skills\n3. Interests\n4. All");
        System.out.print("What do you want to update? ");
        int choice = sc.nextInt();
        sc.nextLine();
    
        try {
            if (choice == 1) {
                System.out.print("Enter new name: ");
                String name = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("UPDATE Student SET name = ? WHERE id = ?");
                pst.setString(1, name);
                pst.setInt(2, id);
                pst.executeUpdate();
            } else if (choice == 2) {
                System.out.print("Enter new skills (comma-separated): ");
                String skills = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("UPDATE Student SET skills = ? WHERE id = ?");
                pst.setString(1, skills);
                pst.setInt(2, id);
                pst.executeUpdate();
            } else if (choice == 3) {
                System.out.print("Enter new interests (comma-separated): ");
                String interests = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("UPDATE Student SET interests = ? WHERE id = ?");
                pst.setString(1, interests);
                pst.setInt(2, id);
                pst.executeUpdate();
            } else if (choice == 4) {
                System.out.print("Enter new name: ");
                String name = sc.nextLine();
                System.out.print("Enter new skills (comma-separated): ");
                String skills = sc.nextLine();
                System.out.print("Enter new interests (comma-separated): ");
                String interests = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("UPDATE Student SET name = ?, skills = ?, interests = ? WHERE id = ?");
                pst.setString(1, name);
                pst.setString(2, skills);
                pst.setString(3, interests);
                pst.setInt(4, id);
                pst.executeUpdate();
            } else {
                System.out.println("Invalid option.");
                return;
            }
            System.out.println("Profile updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    static void deleteOwnStudentProfile(Connection conn, int id) {
        System.out.print("Are you sure you want to delete your profile? (yes/no): ");
        String confirm = sc.nextLine();
        if (confirm.equalsIgnoreCase("yes")) {
            try {
                 // Step 1: Delete from dependent tables
                PreparedStatement pst1 = conn.prepareStatement("DELETE FROM ProjectMembers WHERE studentId = ?");
                pst1.setInt(1, id);
                pst1.executeUpdate();

                PreparedStatement pst2 = conn.prepareStatement("DELETE FROM JoinProject WHERE studentId = ?");
                pst2.setInt(1, id);
                pst2.executeUpdate();

                // Step 2: Delete from Student
                PreparedStatement pst3 = conn.prepareStatement("DELETE FROM Student WHERE id = ?");
                pst3.setInt(1, id);
                pst3.executeUpdate();
                System.out.println("Profile deleted. Logging out...");
                showMainMenu(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }    

    static void viewAllClubs(Connection conn) {
        System.out.println("\n--- All Clubs ---");
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name, requiredSkills, description FROM Club");
            while (rs.next()) {
                System.out.println("- " + rs.getString("name") + " | Skills Required: " + rs.getString("requiredSkills") + " | " + rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }      

    static void startProject(Connection conn, int studentId) {
        try {
            System.out.print("Enter project title: ");
            String title = sc.nextLine();
    
            System.out.print("Enter required skills (comma-separated): ");
            String requiredSkills = sc.nextLine();
    
            System.out.print("Enter project description: ");
            String description = sc.nextLine();
    
            System.out.print("Enter number of members required (including you): ");
            int memberLimit = sc.nextInt();
            sc.nextLine();
    
            // Insert into Project table with ownerType = 'student'
            PreparedStatement insertProject = conn.prepareStatement(
                "INSERT INTO Project (title, description, requiredSkills, ownerType, ownerId, memberLimit) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            insertProject.setString(1, title);
            insertProject.setString(2, description);
            insertProject.setString(3, requiredSkills);
            insertProject.setString(4, "student");
            insertProject.setInt(5, studentId);
            insertProject.setInt(6, memberLimit);
    
            int rows = insertProject.executeUpdate();
            if (rows == 0) {
                System.out.println("Failed to create project.");
                return;
            }
    
            // Get generated project ID
            ResultSet generatedKeys = insertProject.getGeneratedKeys();
            int projectId = -1;
            if (generatedKeys.next()) {
                projectId = generatedKeys.getInt(1);
            }
    
            // Add student as a member of the project
            PreparedStatement insertMember = conn.prepareStatement(
                "INSERT INTO ProjectMembers (projectId, studentId) VALUES (?, ?)"
            );
            insertMember.setInt(1, projectId);
            insertMember.setInt(2, studentId);
            insertMember.executeUpdate();
    
            System.out.println("Project '" + title + "' created and you have been added as a member.");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void joinProject(Connection conn, int studentId) {
        try {
            // Step 1: Show available projects
            System.out.println("\n--- Available Projects ---");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, title, requiredSkills FROM Project");
    
            List<Integer> projectIds = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String skills = rs.getString("requiredSkills");
    
                projectIds.add(id);
                System.out.println("Project ID: " + id + " | Title: " + title + " | Skills: " + skills);
            }
    
            if (projectIds.isEmpty()) {
                System.out.println("No projects available right now.");
                return;
            }
    
            // Step 2: Let user select project
            System.out.print("\nEnter Project ID to apply: ");
            int selectedId = sc.nextInt();
            sc.nextLine();
    
            if (!projectIds.contains(selectedId)) {
                System.out.println("Invalid project selection.");
                return;
            }

            // Check if student is already a confirmed member
PreparedStatement checkMember = conn.prepareStatement(
    "SELECT * FROM ProjectMembers WHERE studentId = ? AND projectId = ?"
);
checkMember.setInt(1, studentId);
checkMember.setInt(2, selectedId);
ResultSet memberExists = checkMember.executeQuery();

if (memberExists.next()) {
    System.out.println("You're already a member of this project.");
    return;
}
    
            // Step 3: Check if project is full
            PreparedStatement checkLimit = conn.prepareStatement(
                "SELECT memberLimit FROM Project WHERE id = ?"
            );
            checkLimit.setInt(1, selectedId);
            ResultSet limitRs = checkLimit.executeQuery();
    
            int memberLimit = 0;
            if (limitRs.next()) {
                memberLimit = limitRs.getInt("memberLimit");
            }
    
            // Count confirmed members
            PreparedStatement countMembers = conn.prepareStatement(
            "SELECT COUNT(*) AS count FROM ProjectMembers WHERE projectId = ?"
            );
            countMembers.setInt(1, selectedId);
            ResultSet membersRs = countMembers.executeQuery();
            int currentMembers = membersRs.next() ? membersRs.getInt("count") : 0;

            // Count pending applications
            PreparedStatement countApplicants = conn.prepareStatement(
            "SELECT COUNT(*) AS count FROM JoinProject WHERE projectId = ?"
            );
            countApplicants.setInt(1, selectedId);
            ResultSet applicantsRs = countApplicants.executeQuery();
            int pendingApplicants = applicantsRs.next() ? applicantsRs.getInt("count") : 0;

            int total = currentMembers + pendingApplicants;
            if (total >= memberLimit) {
                System.out.println("Project is full. Cannot apply.");
                return;
            }
    
            // Step 4: Check if already applied
            PreparedStatement check = conn.prepareStatement(
                "SELECT * FROM JoinProject WHERE studentId = ? AND projectId = ?"
            );
            check.setInt(1, studentId);
            check.setInt(2, selectedId);
            ResultSet exists = check.executeQuery();
            if (exists.next()) {
                System.out.println("You've already applied to this project.");
                return;
            }
    
            System.out.print("Do you want to proceed with applying? (yes/no): ");
            String confirm = sc.nextLine();
            if (confirm.equalsIgnoreCase("yes")) {
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO JoinProject (studentId, projectId) VALUES (?, ?)"
                );
                insert.setInt(1, studentId);
                insert.setInt(2, selectedId);
                insert.executeUpdate();
                System.out.println("Applied successfully!");
            } else {
                System.out.println("Application cancelled.");
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    // Recommend events based on interests and projects based on skills
    static void recommendForStudent(Connection conn, int studentId) {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT skills, interests FROM Student WHERE id = ?");
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();

            List<String> studentSkills = new ArrayList<>();
            List<String> studentInterests = new ArrayList<>();

            if (rs.next()) {
                studentSkills = Arrays.asList(rs.getString("skills").toLowerCase().split(","));
                studentInterests = Arrays.asList(rs.getString("interests").toLowerCase().split(","));
            }

            Statement stmt1 = conn.createStatement();
            ResultSet events = stmt1.executeQuery("SELECT Event.title, Event.description, Club.name AS clubName FROM Event JOIN Club ON Event.clubId = Club.id");

            System.out.println("\n Recommended Events (based on your interests):");
            boolean eventFound = false;
            while (events.next()) {
                String title = events.getString("title");
                String desc = events.getString("description").toLowerCase();
                String clubName = events.getString("clubName");

                for (String interest : studentInterests) {
                    if (desc.contains(interest.trim())) {
                        System.out.println(" - " + title + " by " + clubName);
                            eventFound = true;
                        break;
                    }
                }
            }
            if (!eventFound) System.out.println("No relevant events found.");

            Statement stmt2 = conn.createStatement();
            ResultSet projects = stmt2.executeQuery("SELECT * FROM Project");

            System.out.println("\nRecommended Projects (based on your skills):");
            boolean projectFound = false;
            while (projects.next()) {
                String title = projects.getString("title");
                List<String> requiredSkills = Arrays.asList(projects.getString("requiredSkills").toLowerCase().split(","));

                for (String skill : studentSkills) {
                    if (requiredSkills.contains(skill.trim())) {
                        System.out.println(" - " + title);
                        projectFound = true;
                        break;
                    }
                }
            }
            if (!projectFound) System.out.println("No relevant projects found.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- CLUB SECTION ----------
    static void clubAccess(Connection conn) {
        while (true) {
            System.out.println("\n-- Club Portal --");
            System.out.println("1. Sign Up\n2. Login\n3. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt(); sc.nextLine();
            if (choice == 1) clubSignUp(conn);
            else if (choice == 2) {
                Integer studentId = clubLogin(conn);
                if (studentId != null) clubMenu(conn, studentId);
            }            
            else if (choice == 3) break;
            else System.out.println("Invalid.");
        }
    }

    static void clubSignUp(Connection conn) {
        try {
            System.out.print("Enter club ID: ");
            int id = sc.nextInt();
            sc.nextLine(); // consume newline

            if (clubExists(conn, id)) {
                System.out.println("Club with this ID already exists!");
                return; // or return, or continue
            }
            System.out.print("Name: "); String name = sc.nextLine();
            System.out.print("Required Skills: "); String requiredSkills = sc.nextLine();
            System.out.print("Description: "); String description = sc.nextLine();
            System.out.print("Username: "); String username = sc.nextLine();
            System.out.print("Password: "); String password = sc.nextLine();
            System.out.print("Club Head: "); String teamMembers = sc.nextLine();

            PreparedStatement ps = conn.prepareStatement("INSERT INTO Club VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);  ps.setString(2, username); ps.setString(3, password);
            ps.setString(4, name); ps.setString(5, requiredSkills);
            ps.setString(6, description); ps.setString(7, teamMembers);
            ps.executeUpdate();
            System.out.println("Signed up successfully!");
        } catch (SQLException e) {
            System.out.println("Error signing up.");
        }
    }

    public static boolean clubExists(Connection conn, int id) throws SQLException {
        String query = "SELECT id FROM club WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    static Integer clubLogin(Connection conn) {
        System.out.print("Username: "); String uname = sc.nextLine().trim();
        System.out.print("Password: "); String pwd = sc.nextLine().trim();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Club WHERE username=? AND password=?");
            ps.setString(1, uname); ps.setString(2, pwd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful!");
                return rs.getInt("id");
            } else {
                System.out.println("Invalid credentials.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error logging in.");
            return null;
        }
    }

    static void clubMenu(Connection conn, int id) {
        while (true) {
            System.out.println("\n-- Club Menu --");
            System.out.println("1. View Profile\n2. Update Profile\n3. Delete Profile\n4. Manage Team Members\n5. View Events\n6. Add Event\n7. Update Event\n8. Delete Event\n9. Collaborate\n10. Logout");
            System.out.print("Choice: ");
            int c = sc.nextInt(); sc.nextLine();
            switch (c) {
                case 1: viewClubProfile(conn, id); break;
                case 2: updateClubProfile(conn, id); break;
                case 3: deleteOwnClubProfile(conn, id); break;
                case 4: manageClubTeamMembers(conn, id); break;
                case 5: viewEvents(conn, id); break;
                case 6: addEvent(conn, id); break;
                case 7: updateEvent(conn, id); break;
                case 8: deleteEvent(conn, id); break;
                case 9: addCollaboration(conn, id); break;
                case 10: return;
                default: System.out.println("Invalid.");
            }
        }
    }

    static void viewEvents(Connection conn, int clubId) {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM Event WHERE clubId = ?");
            pst.setInt(1, clubId);
            ResultSet rs = pst.executeQuery();
    
            System.out.println("\n--- Your Events ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                                   ", Title: " + rs.getString("title") +
                                   ", Description: " + rs.getString("description") +
                                   ", Date: " + rs.getDate("date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void viewClubProfile(Connection conn, int id) {
        System.out.println("\n--- Your Profile ---");
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT username, name, requiredSkills, description FROM Club WHERE id = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                System.out.println("Username : " + rs.getString("username"));
                System.out.println("Name     : " + rs.getString("name"));
                System.out.println("Skills   : " + rs.getString("requiredSkills"));
                System.out.println("Description: " + rs.getString("description"));
            } else {
                System.out.println("Profile not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void updateClubProfile(Connection conn, int id) {
        System.out.println("\n--- Update Profile ---");
        System.out.println("1. Name\n2. Skills\n3. Description\n4. All");
        System.out.print("What do you want to update? ");
        int choice = sc.nextInt();
        sc.nextLine();
    
        try {
            if (choice == 1) {
                System.out.print("Enter new name: ");
                String name = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("UPDATE Club SET name = ? WHERE id = ?");
                pst.setString(1, name);
                pst.setInt(2, id);
                pst.executeUpdate();
            } else if (choice == 2) {
                System.out.print("Enter new skills (comma-separated): ");
                String requiredSkills = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("UPDATE Club SET requiredSkills = ? WHERE id = ?");
                pst.setString(1, requiredSkills);
                pst.setInt(2, id);
                pst.executeUpdate();
            } else if (choice == 3) {
                System.out.print("Enter new description: ");
                String description = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("UPDATE Club SET description = ? WHERE id = ?");
                pst.setString(1, description);
                pst.setInt(2, id);
                pst.executeUpdate();
            } else if (choice == 4) {
                System.out.print("Enter new name: ");
                String name = sc.nextLine();
                System.out.print("Enter new skills (comma-separated): ");
                String requiredSkills = sc.nextLine();
                System.out.print("Enter new description: ");
                String description = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("UPDATE Club SET name = ?, requiredSkills = ?, description = ? WHERE id = ?");
                pst.setString(1, name);
                pst.setString(2, requiredSkills);
                pst.setString(3, description);
                pst.setInt(4, id);
                pst.executeUpdate();
            } else {
                System.out.println("Invalid option.");
                return;
            }
            System.out.println("Profile updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    static void deleteOwnClubProfile(Connection conn, int id) {
        System.out.print("Are you sure you want to delete your profile? (yes/no): ");
        String confirm = sc.nextLine();
        if (confirm.equalsIgnoreCase("yes")) {
            try {
                 // Step 1: Delete from dependent tables
                PreparedStatement pst1 = conn.prepareStatement("DELETE FROM Event WHERE clubId = ?");
                pst1.setInt(1, id);
                pst1.executeUpdate();

                PreparedStatement pst2 = conn.prepareStatement("DELETE FROM ClubCollaborations WHERE club1_id = ? OR club2_id = ?");
                pst2.setInt(1, id);
                pst2.setInt(2, id);
                pst2.executeUpdate();

                // Step 2: Delete from Student
                PreparedStatement pst3 = conn.prepareStatement("DELETE FROM Club WHERE id = ?");
                pst3.setInt(1, id);
                pst3.executeUpdate();
                System.out.println("Profile deleted. Logging out...");
                showMainMenu(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    static void addEvent(Connection conn, int clubId) {
        try {
            System.out.print("Enter title: ");
            String title = sc.nextLine();
    
            System.out.print("Enter description: ");
            String desc = sc.nextLine();
    
            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = sc.nextLine();
    
            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO Event (clubId, title, description, date) VALUES (?, ?, ?, ?)"
            );
            pst.setInt(1, clubId);
            pst.setString(2, title);
            pst.setString(3, desc);
            pst.setDate(4, Date.valueOf(date));
    
            pst.executeUpdate();
            System.out.println("Event added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void updateEvent(Connection conn, int clubId) {
        try {
            System.out.print("Enter Event ID to update: ");
            int eventId = sc.nextInt(); sc.nextLine();
    
            // Validate ownership
            PreparedStatement check = conn.prepareStatement("SELECT * FROM Event WHERE id = ? AND clubId = ?");
            check.setInt(1, eventId);
            check.setInt(2, clubId);
            ResultSet rs = check.executeQuery();
    
            if (!rs.next()) {
                System.out.println("You do not own this event.");
                return;
            }
    
            System.out.print("New title: ");
            String title = sc.nextLine();
    
            System.out.print("New description: ");
            String desc = sc.nextLine();
    
            System.out.print("New date (YYYY-MM-DD): ");
            String date = sc.nextLine();
    
            PreparedStatement pst = conn.prepareStatement(
                "UPDATE Event SET title = ?, description = ?, date = ? WHERE id = ?"
            );
            pst.setString(1, title);
            pst.setString(2, desc);
            pst.setDate(3, Date.valueOf(date));
            pst.setInt(4, eventId);
    
            pst.executeUpdate();
            System.out.println("Event updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void deleteEvent(Connection conn, int clubId) {
        try {
            System.out.print("Enter Event ID to delete: ");
            int eventId = Integer.parseInt(sc.nextLine().trim()); // safer input
    
            PreparedStatement pst = conn.prepareStatement("DELETE FROM Event WHERE id = ? AND clubId = ?");
            pst.setInt(1, eventId);
            pst.setInt(2, clubId);
    
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Event deleted successfully.");
            } else {
                System.out.println("Event not found or not owned by your club.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Event ID. Please enter a number.");
        }
    }
    
    static void manageClubTeamMembers(Connection conn, int clubId) {
        while (true) {
            System.out.println("\n--- Manage Team Members ---");
            System.out.println("1. View Team Members");
            System.out.println("2. Add Team Member");
            System.out.println("3. Remove Team Member");
            System.out.println("4. Back to Club Menu");
    
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();
    
            switch (choice) {
                case 1:
                    viewClubTeamMembers(conn, clubId);
                    break;
                case 2:
                    addClubTeamMember(conn, clubId);
                    break;
                case 3:
                    removeClubTeamMember(conn, clubId);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    static void viewClubTeamMembers(Connection conn, int clubId) {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT teamMembers FROM Club WHERE id = ?");
            pst.setInt(1, clubId);
            ResultSet rs = pst.executeQuery();
    
            if (rs.next()) {
                String members = rs.getString("teamMembers");
                if (members == null || members.isEmpty()) {
                    System.out.println("No team members listed.");
                } else {
                    System.out.println("Team Members: " + members);
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
    
    static void addClubTeamMember(Connection conn, int clubId) {
        System.out.print("Enter name of the member to add: ");
        String newMember = sc.nextLine().trim();
    
        try {
            // Get existing members
            PreparedStatement pst = conn.prepareStatement("SELECT teamMembers FROM Club WHERE id = ?");
            pst.setInt(1, clubId);
            ResultSet rs = pst.executeQuery();
    
            if (rs.next()) {
                String members = rs.getString("teamMembers");
                List<String> memberList = new ArrayList<>();
                if (members != null && !members.trim().isEmpty()) {
                    memberList = new ArrayList<>(Arrays.asList(members.split(",")));
                }
    
                if (memberList.stream().anyMatch(m -> m.trim().equalsIgnoreCase(newMember))) {
                    System.out.println("Member already in the team.");
                    return;
                }
    
                memberList.add(newMember);
                String updated = String.join(",", memberList);
    
                PreparedStatement update = conn.prepareStatement("UPDATE Club SET teamMembers = ? WHERE id = ?");
                update.setString(1, updated);
                update.setInt(2, clubId);
                update.executeUpdate();
    
                System.out.println("Member added successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
    
    static void removeClubTeamMember(Connection conn, int clubId) {
        System.out.print("Enter name of the member to remove: ");
        String removeName = sc.nextLine().trim();
    
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT teamMembers FROM Club WHERE id = ?");
            pst.setInt(1, clubId);
            ResultSet rs = pst.executeQuery();
    
            if (rs.next()) {
                String members = rs.getString("teamMembers");
                if (members == null || members.trim().isEmpty()) {
                    System.out.println("No team members to remove.");
                    return;
                }
    
                List<String> memberList = new ArrayList<>(Arrays.asList(members.split(",")));
                boolean removed = memberList.removeIf(m -> m.trim().equalsIgnoreCase(removeName));
                if (!removed) {
                    System.out.println("Member not found in the list.");
                    return;
                }
    
                String updated = String.join(",", memberList);
                PreparedStatement update = conn.prepareStatement("UPDATE Club SET teamMembers = ? WHERE id = ?");
                update.setString(1, updated);
                update.setInt(2, clubId);
                update.executeUpdate();
    
                System.out.println("Member removed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }        

    static void addCollaboration(Connection conn, int myClubId) {
        System.out.print("Enter ID of the club you want to collaborate with: ");
        int otherClubId = sc.nextInt();
        sc.nextLine();
    
        try {
            // Check if other club exists
            PreparedStatement check = conn.prepareStatement("SELECT * FROM Club WHERE id = ?");
            check.setInt(1, otherClubId);
            ResultSet rs = check.executeQuery();
    
            if (!rs.next()) {
                System.out.println("Club with ID " + otherClubId + " not found.");
                return;
            }
    
            // Check if collaboration already exists
            PreparedStatement collabCheck = conn.prepareStatement(
                "SELECT * FROM ClubCollaborations WHERE (club1_id = ? AND club2_id = ?) OR (club1_id = ? AND club2_id = ?)"
            );
            collabCheck.setInt(1, myClubId);
            collabCheck.setInt(2, otherClubId);
            collabCheck.setInt(3, otherClubId);
            collabCheck.setInt(4, myClubId);
            ResultSet collabRs = collabCheck.executeQuery();
    
            if (collabRs.next()) {
                System.out.println("Collaboration already exists between these clubs.");
                return;
            }
    
            // Insert collaboration records (both directions)
            PreparedStatement insert = conn.prepareStatement("INSERT INTO ClubCollaborations (club1_id, club2_id) VALUES (?, ?)");
            insert.setInt(1, myClubId);
            insert.setInt(2, otherClubId);
            insert.executeUpdate();
    
            insert.setInt(1, otherClubId);
            insert.setInt(2, myClubId);
            insert.executeUpdate();
    
            System.out.println("Collaboration established between club " + myClubId + " and club " + otherClubId + ".");
    
            // Copy events from otherClub → myClub if not already copied
            PreparedStatement fetchOtherEvents = conn.prepareStatement("SELECT title, description, date FROM Event WHERE clubId = ?");
            fetchOtherEvents.setInt(1, otherClubId);
            ResultSet otherEvents = fetchOtherEvents.executeQuery();
    
            while (otherEvents.next()) {
                String title = otherEvents.getString("title");
                String description = otherEvents.getString("description") + " (Collab with club " + otherClubId + ")";
                Date date = otherEvents.getDate("date");
    
                // Check if similar event already exists for myClub
                PreparedStatement checkEvent = conn.prepareStatement(
                    "SELECT * FROM Event WHERE clubId = ? AND title = ? AND date = ?"
                );
                checkEvent.setInt(1, myClubId);
                checkEvent.setString(2, title);
                checkEvent.setDate(3, date);
                ResultSet eventCheckRs = checkEvent.executeQuery();
    
                if (!eventCheckRs.next()) {
                    PreparedStatement insertEvent = conn.prepareStatement(
                        "INSERT INTO Event (clubId, title, description, date) VALUES (?, ?, ?, ?)"
                    );
                    insertEvent.setInt(1, myClubId);
                    insertEvent.setString(2, title);
                    insertEvent.setString(3, description);
                    insertEvent.setDate(4, date);
                    insertEvent.executeUpdate();
                }
            }
    
            // Copy events from myClub → otherClub if not already copied
            PreparedStatement fetchMyEvents = conn.prepareStatement("SELECT title, description, date FROM Event WHERE clubId = ?");
            fetchMyEvents.setInt(1, myClubId);
            ResultSet myEvents = fetchMyEvents.executeQuery();
    
            while (myEvents.next()) {
                String title = myEvents.getString("title");
                String description = myEvents.getString("description") + " (Collab with club " + myClubId + ")";
                Date date = myEvents.getDate("date");
    
                // Check if similar event already exists for otherClub
                PreparedStatement checkEvent = conn.prepareStatement(
                    "SELECT * FROM Event WHERE clubId = ? AND title = ? AND date = ?"
                );
                checkEvent.setInt(1, otherClubId);
                checkEvent.setString(2, title);
                checkEvent.setDate(3, date);
                ResultSet eventCheckRs = checkEvent.executeQuery();
    
                if (!eventCheckRs.next()) {
                    PreparedStatement insertEvent = conn.prepareStatement(
                        "INSERT INTO Event (clubId, title, description, date) VALUES (?, ?, ?, ?)"
                    );
                    insertEvent.setInt(1, otherClubId);
                    insertEvent.setString(2, title);
                    insertEvent.setString(3, description);
                    insertEvent.setDate(4, date);
                    insertEvent.executeUpdate();
                }
            }
    
            System.out.println("Events have been shared between the collaborating clubs.");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }            

    // ---------- ADMIN SECTION ----------
    static void adminLogin(Connection conn) {
        System.out.print("Admin Username: "); String uname = sc.nextLine();
        System.out.print("Admin Password: "); String pass = sc.nextLine();
        if (uname.equals(ADMIN_USERNAME) && pass.equals(ADMIN_PASSWORD)) {
            adminMenu(conn);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    static void adminMenu(Connection conn) {
        while (true) {
            System.out.println("\n-- Admin Panel --");
            System.out.println("1. View Students\n2. Signup Student\n3. Edit Student\n4. Delete Student\n5. View Clubs\n6. Signup Club\n7. Edit Club\n8. Delete Club\n9. Logout");
            System.out.print("Choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1: viewStudents(conn); break;
                case 2: studentSignUp(conn); break;
                case 3: editStudent(conn); break;
                case 4: deleteStudent(conn); break;
                case 5: viewClubs(conn); break;
                case 6: clubSignUp(conn); break;
                case 7: editClub(conn); break;
                case 8: deleteClub(conn); break;
                case 9: return;
                default: System.out.println("Invalid.");
            }
        }
    }

    static void viewStudents(Connection conn) {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Student");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("name") +
                        " | Skills: " + rs.getString("skills") +
                        " | Interests: " + rs.getString("interests"));
            }
        } catch (SQLException e) { System.out.println("Error."); }
    }

    static void editStudent(Connection conn) {
        System.out.print("Enter ID: "); int id = sc.nextInt(); sc.nextLine();
        System.out.print("New name: "); String name = sc.nextLine();
        System.out.print("New skills: "); String skills = sc.nextLine();
        System.out.print("New interests: "); String interests = sc.nextLine();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE Student SET name=?, skills=?, interests=? WHERE id=?");
            ps.setString(1, name); ps.setString(2, skills); ps.setString(3, interests); ps.setInt(4, id);
            ps.executeUpdate();
            System.out.println("Updated!");
        } catch (SQLException e) { System.out.println("Error."); }
    }

    static void deleteStudent(Connection conn) {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();
        sc.nextLine();
    
        try {
            // Check if the student exists before attempting to delete
            String checkStudentQuery = "SELECT COUNT(*) FROM Student WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkStudentQuery);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            
            // If the student does not exist, notify the user and exit
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Student with ID " + id + " does not exist.");
                return;  // Exit the method if the student doesn't exist
            }
    
            // Step 1: Delete rows from JoinProject where student is involved
            String deleteJoinProjectQuery = "DELETE FROM JoinProject WHERE studentId = ?";
            PreparedStatement ps2 = conn.prepareStatement(deleteJoinProjectQuery);
            ps2.setInt(1, id);
            ps2.executeUpdate();
            
            // Step 2: Delete corresponding rows in ProjectMembers where the student is involved
            String deleteProjectMembersQuery = "DELETE FROM ProjectMembers WHERE studentId = ?";
            PreparedStatement ps1 = conn.prepareStatement(deleteProjectMembersQuery);
            ps1.setInt(1, id);
            ps1.executeUpdate();
    
            // Step 3: Delete projects created by the student, but only if there are no other references to the project
            PreparedStatement psProjects = conn.prepareStatement(
                "DELETE FROM Project WHERE ownerType = 'student' AND ownerId = ? AND NOT EXISTS (SELECT 1 FROM ProjectMembers WHERE projectId = Project.id) AND NOT EXISTS (SELECT 1 FROM JoinProject WHERE projectId = Project.id)"
            );
            psProjects.setInt(1, id);
            psProjects.executeUpdate();
    
            // Step 4: Finally, delete the student from the Student table
            String deleteStudentQuery = "DELETE FROM Student WHERE id = ?";
            PreparedStatement ps3 = conn.prepareStatement(deleteStudentQuery);
            ps3.setInt(1, id);
            ps3.executeUpdate();
            
            System.out.println("Student and related records deleted successfully!");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }        
    
    static void viewClubs(Connection conn) {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Club");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("name") +
                        " | Skills: " + rs.getString("requiredSkills") +
                        " | Desc: " + rs.getString("description"));
            }
        } catch (SQLException e) { System.out.println("Error."); }
    }

    static void editClub(Connection conn) {
        System.out.print("Enter ID: "); int id = sc.nextInt(); sc.nextLine();
        System.out.print("New name: "); String name = sc.nextLine();
        System.out.print("New skills: "); String skills = sc.nextLine();
        System.out.print("New description: "); String desc = sc.nextLine();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE Club SET name=?, requiredSkills=?, description=? WHERE id=?");
            ps.setString(1, name); ps.setString(2, skills); ps.setString(3, desc); ps.setInt(4, id);
            ps.executeUpdate();
            System.out.println("Updated!");
        } catch (SQLException e) { System.out.println("Error."); }
    }

    static void deleteClub(Connection conn) {
        System.out.print("Enter ID: "); 
        int id = sc.nextInt(); 
        sc.nextLine();  // Consume the newline character after integer input
    
        try {
            // Check if the club exists
            PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM Club WHERE id = ?");
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
    
            if (!rs.next()) {
                System.out.println("Club with ID " + id + " does not exist.");
                return; // Exit the method
            }
    
            // Step 1: Delete from dependent tables
            PreparedStatement pst1 = conn.prepareStatement("DELETE FROM Event WHERE clubId = ?");
            pst1.setInt(1, id);
            pst1.executeUpdate();
    
            PreparedStatement pst2 = conn.prepareStatement("DELETE FROM ClubCollaborations WHERE club1_id = ? OR club2_id = ?");
            pst2.setInt(1, id);
            pst2.setInt(2, id);
            pst2.executeUpdate();
    
            // Step 2: Delete from Club
            PreparedStatement pst3 = conn.prepareStatement("DELETE FROM Club WHERE id = ?");
            pst3.setInt(1, id);
            pst3.executeUpdate();
    
            System.out.println("Club and related records deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }        
}
