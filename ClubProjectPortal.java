// Import statements
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        while (true) {
            System.out.println("\n--- Welcome to Unified Student Platform ---\nSelect user type:\n1. Student\n2. Club\n3. Admin\n4. Exit\nEnter your role: ");
            int role = sc.nextInt(); sc.nextLine();

            switch (role) {
                case 1: studentAccess(conn); break;
                case 2: clubAccess(conn); break;
                case 3: adminLogin(conn); break;
                case 4: System.out.println("Exiting..."); return;
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
            System.out.print("ID: "); int id = sc.nextInt(); sc.nextLine();
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
                case 1: viewStudents(conn); break;
                case 2: editStudent(conn); break;
                case 3: deleteStudent(conn); break;
                case 4: viewClubs(conn); break;
                case 5: startProject(conn, id); break;
                case 6: joinProject(conn, id); break;
                case 7: recommendForStudent(conn, id);;
                case 8: return;
                default: System.out.println("Invalid.");
            }
        }
    }

    static void joinProject(Connection conn, int studentId){
        try {
            // Fetch student skills
            PreparedStatement pst = conn.prepareStatement("SELECT skills FROM Student WHERE id = ?");
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            
            List<String> studentSkills = new ArrayList<>();
            if (rs.next()) {
                studentSkills = Arrays.asList(rs.getString("skills").toLowerCase().split(","));
            }

            // Fetch all projects
            Statement stmt = conn.createStatement();
            ResultSet projects = stmt.executeQuery("SELECT * FROM Project");

            Map<Integer, String> matchingProjects = new HashMap<>();
            int count = 1;

            System.out.println("\nProjects matching your skills:");
            while (projects.next()) {
                int projectId = projects.getInt("id");
                String title = projects.getString("title");
                String requiredSkills = projects.getString("requiredSkills").toLowerCase();
                List<String> requiredSkillsList = Arrays.asList((requiredSkills).split(","));

                for (String skill : studentSkills) {
                    if (requiredSkillsList.contains(skill.trim())) {
                        matchingProjects.put(count, title + " (ID: " + projectId + ")");
                        System.out.println(count + ". " + title + " - requires: " + requiredSkills);
                        break;
                    }
                }
                count++;
            }

        /*    while (projects.next()) {
                int projectId = projects.getInt("id");
                String title = projects.getString("title");
                String requiredSkills = projects.getString("requiredSkills").toLowerCase();
            
                List<String> requiredSkillsList = Arrays.asList(requiredSkills.split(","));
            
                for (String skill : studentSkills) {
                    for (String required : requiredSkillsList) {
                        if (required.trim().equalsIgnoreCase(skill.trim())) {
                            matchingProjects.put(count, title + " (ID: " + projectId + ")");
                            System.out.println(count + ". " + title + " - requires: " + requiredSkills);
                            break;
                        }
                    }
                }
                count++;
            }
            
*/
            if (matchingProjects.isEmpty()) {
                System.out.println("No matching projects found.");
                return;
            }

            System.out.print("Enter the number of the project you want to join: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (matchingProjects.containsKey(choice)) {
                String selected = matchingProjects.get(choice);
                int projectId = Integer.parseInt(selected.replaceAll("[^0-9]", ""));

                // Insert into ProjectMembers table
                PreparedStatement insert = conn.prepareStatement("INSERT INTO ProjectMembers (projectId, studentId) VALUES (?, ?)");
                insert.setInt(1, projectId);
                insert.setInt(2, studentId);
                insert.executeUpdate();

                System.out.println("You have successfully joined the project: " + selected);
            } else {
                System.out.println("Invalid choice.");
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
    
            // Insert into Project table with ownerType = 'student'
            PreparedStatement insertProject = conn.prepareStatement(
                "INSERT INTO Project (title, description, requiredSkills, ownerType, ownerId) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            insertProject.setString(1, title);
            insertProject.setString(2, description);
            insertProject.setString(3, requiredSkills);
            insertProject.setString(4, "student");
            insertProject.setInt(5, studentId);
    
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
            System.out.print("ID: "); int id = sc.nextInt(); sc.nextLine();
            System.out.print("Name: "); String name = sc.nextLine();
            System.out.print("Required Skills: "); String requiredSkills = sc.nextLine();
            System.out.print("Description: "); String description = sc.nextLine();
            System.out.print("Username: "); String username = sc.nextLine();
            System.out.print("Password: "); String password = sc.nextLine();
            System.out.print("Team  Members: "); String teamMembers = sc.nextLine();

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
            System.out.println("1. View Profile\n2. Update Profile\n3. Delete Profile\n4. View Events\n5. Add Event\n6. Update Event\n7. Delete Event\n8. Collaborate\n9. Logout");
            System.out.print("Choice: ");
            int c = sc.nextInt(); sc.nextLine();
            switch (c) {
                case 1: viewClubs(conn); break;
                case 2: editClub(conn); break;
                case 3: deleteClub(conn); break;
                case 4: viewEvents(conn, id); break;
                case 5: addEvent(conn, id); break;
                case 6: updateEvent(conn, id); break;
                case 7: deleteEvent(conn, id); break;
                case 8: collaborateWithClub(conn, id); break;
                case 9: return;
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
            int eventId = sc.nextInt(); sc.nextLine();
    
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
            e.printStackTrace();
        }
    }

    static void collaborateWithClub(Connection conn, int clubId) {
        try {
            // List other clubs
            PreparedStatement pst = conn.prepareStatement("SELECT id, name FROM Club WHERE id != ?");
            pst.setInt(1, clubId);
            ResultSet rs = pst.executeQuery();
    
            Map<Integer, Integer> clubMap = new HashMap<>();
            int count = 1;
            System.out.println("\n-- Available Clubs to Collaborate With --");
            while (rs.next()) {
                int otherId = rs.getInt("id");
                System.out.println(count + ". " + rs.getString("name") + " (ID: " + otherId + ")");
                clubMap.put(count, otherId);
                count++;
            }
    
            if (clubMap.isEmpty()) {
                System.out.println("No other clubs available for collaboration.");
                return;
            }
    
            System.out.print("Enter number of club to collaborate with: ");
            int choice = sc.nextInt(); sc.nextLine();
            int club2Id = clubMap.getOrDefault(choice, -1);
    
            if (club2Id == -1) {
                System.out.println("Invalid club selection.");
                return;
            }
    
            // Show club's own events
            PreparedStatement events = conn.prepareStatement("SELECT id, title FROM Event WHERE clubId = ?");
            events.setInt(1, clubId);
            ResultSet eventRs = events.executeQuery();
    
            Map<Integer, Integer> eventMap = new HashMap<>();
            int ec = 1;
            System.out.println("\n-- Your Events --");
            while (eventRs.next()) {
                int eid = eventRs.getInt("id");
                String title = eventRs.getString("title");
                System.out.println(ec + ". " + title + " (ID: " + eid + ")");
                eventMap.put(ec, eid);
                ec++;
            }
    
            if (eventMap.isEmpty()) {
                System.out.println("No events found to collaborate on.");
                return;
            }
    
            System.out.print("Enter id of your event to collaborate on: ");
            int eventChoice = sc.nextInt(); sc.nextLine();
            int eventId = eventMap.getOrDefault(eventChoice, -1);
    
            if (eventId == -1) {
                System.out.println("Invalid event selection.");
                return;
            }
    
            // Insert into Collaboration table
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO Collaboration (club1Id, club2Id, eventId) VALUES (?, ?, ?)"
            );
            insert.setInt(1, clubId);
            insert.setInt(2, club2Id);
            insert.setInt(3, eventId);
            insert.executeUpdate();
    
            System.out.println("Collaboration created successfully.");
    
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
            System.out.println("1. View Students\n2. Add Student\n3. Edit Student\n4. Delete Student\n5. View Clubs\n6. Add Club\n7. Edit Club\n8. Delete Club\n9. Logout");
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
        System.out.print("Enter ID: "); int id = sc.nextInt(); sc.nextLine();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Student WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate();
            System.out.println("Deleted!");
        } catch (SQLException e) { System.out.println("Error."); }
    }

    static void viewClubs(Connection conn) {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Club");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("name") +
                        " | Skills: " + rs.getString("required_skills") +
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
            PreparedStatement ps = conn.prepareStatement("UPDATE Club SET name=?, required_skills=?, description=? WHERE id=?");
            ps.setString(1, name); ps.setString(2, skills); ps.setString(3, desc); ps.setInt(4, id);
            ps.executeUpdate();
            System.out.println("Updated!");
        } catch (SQLException e) { System.out.println("Error."); }
    }

    static void deleteClub(Connection conn) {
        System.out.print("Enter ID: "); int id = sc.nextInt(); sc.nextLine();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Club WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate();
            System.out.println("Deleted!");
        } catch (SQLException e) { System.out.println("Error."); }
    }
}
