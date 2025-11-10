package librarysystem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Library {
    private static final int MAX_ISSUE_DAYS = 7;  // Maximum days to keep book
    private static final int FINE_PER_DAY = 10;   // Fine per day after due date

    // Add book - stores in database SILENTLY
    public void addBook(Book book) {
        DatabaseManager.addBook(book); // Add book to database - NO PRINT MESSAGES
    }

    // Remove book - removes from database
    public void removeBook(int bookId) {
        boolean success = DatabaseManager.removeBook(bookId);
        if (success) {
            System.out.println("✅ Book with ID " + bookId + " removed from database.");
        } else {
            System.out.println("❌ Book not found in database or removal failed!");
        }
    }

    // Show all books - fetches from database
    public void showBooks() {
        List<Book> books = DatabaseManager.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("📚 No books available in library database.");
            return;
        }
        System.out.println("\n=== BOOKS IN LIBRARY ===");
        books.forEach(System.out::println);
    }

    // Show issued books - fetches from database
    public void showIssuedBooks() {
        DatabaseManager.displayIssuedBooks();
    }

    // === NEW METHODS FOR BOOK MANAGEMENT ===

    // Update book stock only
    public void updateBookStock(int bookId, int newStock) {
        Book book = DatabaseManager.getBook(bookId);
        if (book == null) {
            System.out.println("❌ Book with ID " + bookId + " not found!");
            return;
        }

        boolean success = DatabaseManager.updateBookStock(bookId, newStock);
        if (success) {
            System.out.println("✅ Stock updated successfully!");
            System.out.println("📖 " + book.getTitle() + " stock changed to: " + newStock + " copies");
        } else {
            System.out.println("❌ Failed to update stock!");
        }
    }

    // Update complete book details
    public void updateBookDetails(int bookId, String title, String publisher, String edition, int copies) {
        boolean success = DatabaseManager.updateBookDetails(bookId, title, publisher, edition, copies);
        if (success) {
            System.out.println("✅ Book details updated successfully!");
            System.out.println("📖 Updated: " + title + " by " + publisher + ", Edition: " + edition + ", Stock: " + copies);
        } else {
            System.out.println("❌ Failed to update book details!");
        }
    }

    // Display book details for editing
    public void showBookForEdit(int bookId) {
        DatabaseManager.displayBookForEdit(bookId);
    }

    // === NEW METHODS FOR PASSWORD MANAGEMENT ===

    // Change student password
    public void changeStudentPassword(int studentId, String newPassword) {
        boolean success = DatabaseManager.updateStudentPassword(studentId, newPassword);
        if (success) {
            System.out.println("✅ Password changed successfully!");
            System.out.println("🔐 Your new password has been updated in the database.");
        } else {
            System.out.println("❌ Failed to change password!");
        }
    }

    // Change librarian password
    public void changeLibrarianPassword(int librarianId, String newPassword) {
        boolean success = DatabaseManager.updateLibrarianPassword(librarianId, newPassword);
        if (success) {
            System.out.println("✅ Password changed successfully!");
            System.out.println("🔐 Your new password has been updated in the database.");
        } else {
            System.out.println("❌ Failed to change password!");
        }
    }

    // === NEW METHODS FOR STUDENT MANAGEMENT ===

    // Add new student (for librarians)
    public void addNewStudent(int studentId, String name, String department, String course) {
        boolean success = DatabaseManager.addNewStudent(studentId, name, department, course);
        if (success) {
            System.out.println("✅ New student added successfully!");
            System.out.println("👨‍🎓 Student: " + name + " (ID: " + studentId + ")");
            System.out.println("🏫 Department: " + department + ", Course: " + course);
            System.out.println("🔐 Default password set to: " + studentId);
        } else {
            System.out.println("❌ Failed to add student! (Student ID may already exist)");
        }
    }

    // Issue book - stores in database with validation
    public void issueBook(Student student, int bookId, Librarian librarian) {
        if (DatabaseManager.hasIssuedBook(student.getStudentId())) {
            System.out.println("❌ Error! " + student.getName() +
                    " has already issued a book. Return it first!");
            return;
        }

        Book book = DatabaseManager.getBook(bookId);
        if (book == null) {
            System.out.println("❌ Book with ID " + bookId + " not found in database!");
            return;
        }

        if (book.getCopies() > 0) {
            DatabaseManager.addStudent(student);
            DatabaseManager.addLibrarian(librarian);

            boolean issueSuccess = DatabaseManager.issueBook(
                    student.getStudentId(),
                    bookId,
                    librarian.getLibrarianId()
            );

            if (issueSuccess) {
                DatabaseManager.updateBookCopies(bookId, book.getCopies() - 1);

                System.out.println("✅ Book issued successfully!");
                System.out.println("📖 " + book.getTitle() + " by " + book.getPublisher() +
                        " issued to " + student.getName());
                System.out.println("📅 Due date: " + LocalDate.now().plusDays(MAX_ISSUE_DAYS));
            } else {
                System.out.println("❌ Failed to issue book. Database error occurred!");
            }
        } else {
            System.out.println("❌ Book not available! No copies left in stock.");
        }
    }

    // Return book - UPDATED with real fine calculation from database
    public void returnBook(Student student, int bookId, Librarian librarian) {
        Book book = DatabaseManager.getBook(bookId);
        if (book == null) {
            System.out.println("❌ Book with ID " + bookId + " not found!");
            return;
        }

        // Get actual issue date from database
        LocalDate issueDate = DatabaseManager.getIssueDate(student.getStudentId(), bookId);
        if (issueDate == null) {
            System.out.println("❌ No issued book found for student " + student.getName());
            return;
        }

        // Calculate fine using REAL issue date from database
        LocalDate today = LocalDate.now();
        long daysIssued = ChronoUnit.DAYS.between(issueDate, today);
        int fine = (daysIssued > MAX_ISSUE_DAYS) ? (int) ((daysIssued - MAX_ISSUE_DAYS) * FINE_PER_DAY) : 0;

        // Return book in database with calculated fine
        boolean returnSuccess = DatabaseManager.returnBook(
                student.getStudentId(),
                bookId,
                fine
        );

        if (returnSuccess) {
            DatabaseManager.updateBookCopies(bookId, book.getCopies() + 1);

            System.out.println("✅ Book returned successfully!");
            System.out.println("📅 Issue date: " + issueDate);
            System.out.println("📅 Return date: " + today);
            System.out.println("📊 Total days: " + daysIssued);

            if (fine > 0) {
                System.out.println("💰 Fine applied: ₹" + fine +
                        " (Late by " + (daysIssued - MAX_ISSUE_DAYS) + " days)");
            } else {
                System.out.println("👍 No fine applied - returned on time!");
            }
        } else {
            System.out.println("❌ Failed to return book. Database error occurred!");
        }
    }

    // NEW METHOD: Check and display fine for student BEFORE returning
    public void checkFineForStudent(Student student) {
        DatabaseManager.displayStudentFineStatus(student.getStudentId());
    }
}
