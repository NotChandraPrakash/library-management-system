package librarysystem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/librarydb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";   // Replace with your MySQL username
    private static final String PASSWORD = "root"; // Replace with your MySQL password

    // Get database connection - using explicit type instead of 'var'
    public static Connection getConnection() {
        try {
            // Load MySQL JDBC driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Test database connection - SILENT VERSION
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
        }
        return false;
    }

    // Initialize database and create tables - SILENT VERSION
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();

                // Use the database
                stmt.executeUpdate("USE librarydb");

                // Create books table
                String createBooksTable = "CREATE TABLE IF NOT EXISTS books (" +
                        "book_id INT PRIMARY KEY, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "publisher VARCHAR(255) NOT NULL, " +
                        "edition VARCHAR(100) NOT NULL, " +
                        "copies INT NOT NULL DEFAULT 0)";
                stmt.executeUpdate(createBooksTable);

                // Create students table - UPDATED with password field
                String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                        "student_id INT PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "department VARCHAR(100) NOT NULL, " +
                        "course VARCHAR(100) NOT NULL, " +
                        "password VARCHAR(100) DEFAULT NULL)";
                stmt.executeUpdate(createStudentsTable);

                // Create librarians table - UPDATED with password field
                String createLibrariansTable = "CREATE TABLE IF NOT EXISTS librarians (" +
                        "librarian_id INT PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "password VARCHAR(100) DEFAULT NULL)";
                stmt.executeUpdate(createLibrariansTable);

                // Create issue_records table
                String createIssueRecordsTable = "CREATE TABLE IF NOT EXISTS issue_records (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "student_id INT NOT NULL, " +
                        "book_id INT NOT NULL, " +
                        "issue_date DATE NOT NULL, " +
                        "return_date DATE NULL, " +
                        "fine_amount INT DEFAULT 0, " +
                        "librarian_id INT NOT NULL, " +
                        "FOREIGN KEY (student_id) REFERENCES students(student_id), " +
                        "FOREIGN KEY (book_id) REFERENCES books(book_id), " +
                        "FOREIGN KEY (librarian_id) REFERENCES librarians(librarian_id))";
                stmt.executeUpdate(createIssueRecordsTable);

                // Add password columns if they don't exist (for existing databases)
                try {
                    stmt.executeUpdate("ALTER TABLE students ADD COLUMN password VARCHAR(100) DEFAULT NULL");
                } catch (SQLException e) {
                    // Column already exists, ignore
                }

                try {
                    stmt.executeUpdate("ALTER TABLE librarians ADD COLUMN password VARCHAR(100) DEFAULT NULL");
                } catch (SQLException e) {
                    // Column already exists, ignore
                }

            }
        } catch (SQLException e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // === BOOK CRUD OPERATIONS ===

    // Add or update book in database - FIXED VERSION (NO MORE DUPLICATE COPIES)
    public static boolean addBook(Book book) {
        String sql = "INSERT IGNORE INTO books (book_id, title, publisher, edition, copies) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, book.getBookId());        // Set book ID parameter
            pstmt.setString(2, book.getTitle());      // Set title parameter
            pstmt.setString(3, book.getPublisher());  // Set publisher parameter
            pstmt.setString(4, book.getEdition());    // Set edition parameter
            pstmt.setInt(5, book.getCopies());        // Set copies parameter

            pstmt.executeUpdate(); // Execute the insert query
            return true; // Return true (INSERT IGNORE won't fail if duplicate)

        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    // Update book copies count
    public static boolean updateBookCopies(int bookId, int newCopies) {
        String sql = "UPDATE books SET copies = ? WHERE book_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newCopies);  // Set new copies count
            pstmt.setInt(2, bookId);     // Set book ID to update

            int rowsAffected = pstmt.executeUpdate(); // Execute update
            return rowsAffected > 0;                  // Return success status

        } catch (SQLException e) {
            System.err.println("Error updating book copies: " + e.getMessage());
            return false;
        }
    }

    // === NEW METHODS FOR UPDATING BOOK DETAILS ===

    // Update book details (title, publisher, edition, copies)
    public static boolean updateBookDetails(int bookId, String title, String publisher, String edition, int copies) {
        String sql = "UPDATE books SET title = ?, publisher = ?, edition = ?, copies = ? WHERE book_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);      // Set new title
            pstmt.setString(2, publisher);  // Set new publisher
            pstmt.setString(3, edition);    // Set new edition
            pstmt.setInt(4, copies);        // Set new copies count
            pstmt.setInt(5, bookId);        // Set book ID to update

            int rowsAffected = pstmt.executeUpdate(); // Execute update
            return rowsAffected > 0;                  // Return success status

        } catch (SQLException e) {
            System.err.println("Error updating book details: " + e.getMessage());
            return false;
        }
    }

    // Update only book stock/copies
    public static boolean updateBookStock(int bookId, int newStock) {
        String sql = "UPDATE books SET copies = ? WHERE book_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newStock);  // Set new stock count
            pstmt.setInt(2, bookId);    // Set book ID to update

            int rowsAffected = pstmt.executeUpdate(); // Execute update
            return rowsAffected > 0;                  // Return success status

        } catch (SQLException e) {
            System.err.println("Error updating book stock: " + e.getMessage());
            return false;
        }
    }

    // Search book by ID and display details for editing
    public static void displayBookForEdit(int bookId) {
        Book book = getBook(bookId);
        if (book != null) {
            System.out.println("\n=== CURRENT BOOK DETAILS ===");
            System.out.println("📖 Book ID: " + book.getBookId());
            System.out.println("📝 Title: " + book.getTitle());
            System.out.println("🏢 Publisher: " + book.getPublisher());
            System.out.println("📄 Edition: " + book.getEdition());
            System.out.println("🔢 Current Stock: " + book.getCopies());
            System.out.println("=".repeat(35));
        } else {
            System.out.println("❌ Book with ID " + bookId + " not found!");
        }
    }

    // Get single book from database
    public static Book getBook(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);          // Set book ID parameter
            ResultSet rs = pstmt.executeQuery(); // Execute query

            if (rs.next()) {                  // If book found
                return new Book(              // Create and return Book object
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("publisher"),
                        rs.getString("edition"),
                        rs.getInt("copies")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving book: " + e.getMessage());
        }
        return null; // Return null if book not found
    }

    // Get all books from database
    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>(); // Create list to store books
        String sql = "SELECT * FROM books";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {               // Loop through all results
                books.add(new Book(           // Add each book to list
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("publisher"),
                        rs.getString("edition"),
                        rs.getInt("copies")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all books: " + e.getMessage());
        }
        return books; // Return list of books
    }

    // Remove book from database
    public static boolean removeBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);          // Set book ID to delete
            int rowsAffected = pstmt.executeUpdate(); // Execute delete
            return rowsAffected > 0;          // Return success status

        } catch (SQLException e) {
            System.err.println("Error removing book: " + e.getMessage());
            return false;
        }
    }

    // === STUDENT CRUD OPERATIONS ===

    // Add student to database - UPDATED with password support
    public static boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name, department, course, password) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name=VALUES(name), department=VALUES(department), course=VALUES(course)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, student.getStudentId());    // Set student ID
            pstmt.setString(2, student.getName());      // Set student name
            pstmt.setString(3, student.getDepartment());// Set department
            pstmt.setString(4, student.getCourse());    // Set course
            pstmt.setString(5, String.valueOf(student.getStudentId())); // Default password = ID

            int rowsAffected = pstmt.executeUpdate();   // Execute insert
            return rowsAffected > 0;                    // Return success status

        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    // Add new student with custom details
    public static boolean addNewStudent(int studentId, String name, String department, String course) {
        String sql = "INSERT IGNORE INTO students (student_id, name, department, course, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);         // Set student ID
            pstmt.setString(2, name);           // Set student name
            pstmt.setString(3, department);     // Set department
            pstmt.setString(4, course);         // Set course
            pstmt.setString(5, String.valueOf(studentId)); // Default password = ID

            int rowsAffected = pstmt.executeUpdate();   // Execute insert
            return rowsAffected > 0;                    // Return success status

        } catch (SQLException e) {
            System.err.println("Error adding new student: " + e.getMessage());
            return false;
        }
    }

    // Get student from database
    public static Student getStudent(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);       // Set student ID parameter
            ResultSet rs = pstmt.executeQuery(); // Execute query

            if (rs.next()) {                  // If student found
                return new Student(           // Create and return Student object
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("course")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student: " + e.getMessage());
        }
        return null; // Return null if student not found
    }

    // === PASSWORD MANAGEMENT ===

    // Update student password
    public static boolean updateStudentPassword(int studentId, String newPassword) {
        String sql = "UPDATE students SET password = ? WHERE student_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);  // Set new password
            pstmt.setInt(2, studentId);       // Set student ID

            int rowsAffected = pstmt.executeUpdate(); // Execute update
            return rowsAffected > 0;                  // Return success status

        } catch (SQLException e) {
            System.err.println("Error updating student password: " + e.getMessage());
            return false;
        }
    }

    // Update librarian password
    public static boolean updateLibrarianPassword(int librarianId, String newPassword) {
        String sql = "UPDATE librarians SET password = ? WHERE librarian_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);  // Set new password
            pstmt.setInt(2, librarianId);     // Set librarian ID

            int rowsAffected = pstmt.executeUpdate(); // Execute update
            return rowsAffected > 0;                  // Return success status

        } catch (SQLException e) {
            System.err.println("Error updating librarian password: " + e.getMessage());
            return false;
        }
    }

    // Verify student password
    public static boolean verifyStudentPassword(int studentId, String password) {
        String sql = "SELECT password FROM students WHERE student_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // If no password set, use ID as default
                if (storedPassword == null) {
                    storedPassword = String.valueOf(studentId);
                }
                return storedPassword.equals(password);
            }
        } catch (SQLException e) {
            System.err.println("Error verifying student password: " + e.getMessage());
        }
        return false;
    }

    // Verify librarian password
    public static boolean verifyLibrarianPassword(int librarianId, String password) {
        String sql = "SELECT password FROM librarians WHERE librarian_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, librarianId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // If no password set, use ID as default
                if (storedPassword == null) {
                    storedPassword = String.valueOf(librarianId);
                }
                return storedPassword.equals(password);
            }
        } catch (SQLException e) {
            System.err.println("Error verifying librarian password: " + e.getMessage());
        }
        return false;
    }

    // === LIBRARIAN CRUD OPERATIONS ===

    // Add librarian to database - UPDATED with password support
    public static boolean addLibrarian(Librarian librarian) {
        String sql = "INSERT INTO librarians (librarian_id, name, password) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name=VALUES(name)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, librarian.getLibrarianId()); // Set librarian ID
            pstmt.setString(2, librarian.getName());     // Set librarian name
            pstmt.setString(3, String.valueOf(librarian.getLibrarianId())); // Default password = ID

            int rowsAffected = pstmt.executeUpdate();    // Execute insert
            return rowsAffected > 0;                     // Return success status

        } catch (SQLException e) {
            System.err.println("Error adding librarian: " + e.getMessage());
            return false;
        }
    }

    // Get librarian from database by ID
    public static Librarian getLibrarian(int librarianId) {
        String sql = "SELECT * FROM librarians WHERE librarian_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, librarianId);      // Set librarian ID parameter
            ResultSet rs = pstmt.executeQuery(); // Execute query

            if (rs.next()) {                   // If librarian found
                return new Librarian(          // Create and return Librarian object
                        rs.getInt("librarian_id"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving librarian: " + e.getMessage());
        }
        return null; // Return null if librarian not found
    }

    // === ISSUE RECORD OPERATIONS ===

    // Issue book to student
    public static boolean issueBook(int studentId, int bookId, int librarianId) {
        String sql = "INSERT INTO issue_records (student_id, book_id, issue_date, librarian_id) " +
                "VALUES (?, ?, CURDATE(), ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);    // Set student ID
            pstmt.setInt(2, bookId);       // Set book ID
            pstmt.setInt(3, librarianId);  // Set librarian ID

            int rowsAffected = pstmt.executeUpdate(); // Execute insert
            return rowsAffected > 0;                  // Return success status

        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            return false;
        }
    }

    // Return book from student
    public static boolean returnBook(int studentId, int bookId, int fine) {
        String sql = "UPDATE issue_records SET return_date = CURDATE(), fine_amount = ? " +
                "WHERE student_id = ? AND book_id = ? AND return_date IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fine);      // Set fine amount
            pstmt.setInt(2, studentId); // Set student ID
            pstmt.setInt(3, bookId);    // Set book ID

            int rowsAffected = pstmt.executeUpdate(); // Execute update
            return rowsAffected > 0;                  // Return success status

        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    // Check if student has issued book
    public static boolean hasIssuedBook(int studentId) {
        String sql = "SELECT COUNT(*) FROM issue_records WHERE student_id = ? AND return_date IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);       // Set student ID parameter
            ResultSet rs = pstmt.executeQuery(); // Execute query

            if (rs.next()) {                  // If result found
                return rs.getInt(1) > 0;      // Return true if count > 0
            }
        } catch (SQLException e) {
            System.err.println("Error checking issued books: " + e.getMessage());
        }
        return false; // Return false by default
    }

    // Get all issued books details
    public static void displayIssuedBooks() {
        String sql = "SELECT ir.id, s.name as student_name, b.title as book_title, " +
                "ir.issue_date, l.name as librarian_name " +
                "FROM issue_records ir " +
                "JOIN students s ON ir.student_id = s.student_id " +
                "JOIN books b ON ir.book_id = b.book_id " +
                "JOIN librarians l ON ir.librarian_id = l.librarian_id " +
                "WHERE ir.return_date IS NULL";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== ISSUED BOOKS ===");
            while (rs.next()) {               // Loop through results
                System.out.println("Issue ID: " + rs.getInt("id") +
                        ", Student: " + rs.getString("student_name") +
                        ", Book: " + rs.getString("book_title") +
                        ", Issue Date: " + rs.getDate("issue_date") +
                        ", Librarian: " + rs.getString("librarian_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error displaying issued books: " + e.getMessage());
        }
    }

    // === NEW METHODS FOR FINE CALCULATION ===

    // Get issue date for a specific student and book
    public static LocalDate getIssueDate(int studentId, int bookId) {
        String sql = "SELECT issue_date FROM issue_records WHERE student_id = ? AND book_id = ? AND return_date IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);  // Set student ID
            pstmt.setInt(2, bookId);     // Set book ID
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDate("issue_date").toLocalDate(); // Convert SQL Date to LocalDate
            }
        } catch (SQLException e) {
            System.err.println("Error getting issue date: " + e.getMessage());
        }
        return null; // Return null if no issue record found
    }

    // Display current fine status for a student
    public static void displayStudentFineStatus(int studentId) {
        String sql = "SELECT ir.book_id, ir.issue_date, b.title, DATEDIFF(CURDATE(), ir.issue_date) as days_issued " +
                "FROM issue_records ir " +
                "JOIN books b ON ir.book_id = b.book_id " +
                "WHERE ir.student_id = ? AND ir.return_date IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int daysIssued = rs.getInt("days_issued");
                String bookTitle = rs.getString("title");
                LocalDate issueDate = rs.getDate("issue_date").toLocalDate();

                System.out.println("\n=== FINE STATUS ===");
                System.out.println("📖 Book: " + bookTitle);
                System.out.println("📅 Issue Date: " + issueDate);
                System.out.println("📊 Days Issued: " + daysIssued);
                System.out.println("⏰ Due Date: " + issueDate.plusDays(7));

                if (daysIssued > 7) {
                    int fine = (daysIssued - 7) * 10;
                    System.out.println("💰 Current Fine: ₹" + fine + " (Late by " + (daysIssued - 7) + " days)");
                } else {
                    int daysLeft = 7 - daysIssued;
                    System.out.println("✅ No fine yet. " + daysLeft + " days left to return.");
                }
            } else {
                System.out.println("✅ No books currently issued.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking fine status: " + e.getMessage());
        }
    }

    // Display student's issued books with due dates and fine status (returns String for GUI)
    public static String displayStudentIssuedBooks(int studentId) {
        StringBuilder output = new StringBuilder();
        String sql = "SELECT ir.book_id, ir.issue_date, b.title, DATEDIFF(CURDATE(), ir.issue_date) as days_issued " +
                "FROM issue_records ir " +
                "JOIN books b ON ir.book_id = b.book_id " +
                "WHERE ir.student_id = ? AND ir.return_date IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            boolean hasBooks = false;
            output.append("=== MY ISSUED BOOKS ===\n\n");

            while (rs.next()) {
                hasBooks = true;
                int bookId = rs.getInt("book_id");
                int daysIssued = rs.getInt("days_issued");
                String bookTitle = rs.getString("title");
                LocalDate issueDate = rs.getDate("issue_date").toLocalDate();
                LocalDate dueDate = issueDate.plusDays(7);

                output.append("📖 Book ID: ").append(bookId).append("\n");
                output.append("   Title: ").append(bookTitle).append("\n");
                output.append("   Issue Date: ").append(issueDate).append("\n");
                output.append("   Due Date: ").append(dueDate).append("\n");
                output.append("   Days Issued: ").append(daysIssued).append("\n");

                if (daysIssued > 7) {
                    int fine = (daysIssued - 7) * 10;
                    output.append("   Status: ⚠️ LATE (Fine: ₹").append(fine)
                            .append(", Late by ").append(daysIssued - 7).append(" days)\n");
                } else {
                    int daysLeft = 7 - daysIssued;
                    output.append("   Status: ✅ OK (").append(daysLeft).append(" days left)\n");
                }
                output.append("\n");
            }

            if (!hasBooks) {
                output.append("✅ No books currently issued.\n");
            }

            return output.toString();

        } catch (SQLException e) {
            System.err.println("Error retrieving student issued books: " + e.getMessage());
            return "❌ Error retrieving issued books.";
        }
    }
}
