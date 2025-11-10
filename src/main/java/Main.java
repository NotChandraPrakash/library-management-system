//Importing all packages and utilities
import librarysystem.*;
import java.util.*;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Silent database setup
        if (!DatabaseManager.testConnection()) {
            System.out.println("Database connection failed.");
            return;
        }

        DatabaseManager.initializeDatabase();

        Library library = new Library();

        // Predefined librarians - also add to database
        Librarian lib1 = new Librarian(101, "Mr. Sharma");
        Librarian lib2 = new Librarian(102, "Mrs. Sumita");
        DatabaseManager.addLibrarian(lib1);
        DatabaseManager.addLibrarian(lib2);

        // Store librarians in a list
        List<Librarian> librarians = Arrays.asList(lib1, lib2);

        // Predefined students - also add to database
        Student s1 = new Student(201, "Rahul", "CSE", "B.Tech");
        Student s2 = new Student(202, "Priya", "ECE", "B.Tech");
        DatabaseManager.addStudent(s1);
        DatabaseManager.addStudent(s2);

        // Preload books - will be stored in database
        library.addBook(new Book(1001, "Java Programming", "Pearson", "3rd", 2));
        library.addBook(new Book(1002, "Data Structures", "McGraw Hill", "2nd", 1));
        library.addBook(new Book(1003, "Database Systems", "Elsevier", "4th", 3));

        // Only show this final message
        System.out.println("✅ Library Management System initialized successfully!");

        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🏛️  WELCOME TO DIGITAL LIBRARY SYSTEM");
            System.out.println("=".repeat(50));
            System.out.println("1. 👨‍💼 Login as Librarian");
            System.out.println("2. 👨‍🎓 Login as Student");
            System.out.println("3. 🚪 Exit");
            System.out.println("=".repeat(50));
            System.out.print("📝 Enter your choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> librarianMenu(librarians, library);
                case 2 -> studentMenu(librarians, library, s1, s2);
                case 3 -> {
                    System.out.println("👋 Thank you for using Library Management System!");
                    System.out.println("💾 All data has been saved to database");
                    return;
                }
                default -> System.out.println("❌ Invalid choice! Please try again.");
            }
        }
    }

    // ================= Librarian Menu =================
    private static void librarianMenu(List<Librarian> librarians, Library library) {
        System.out.print("🆔 Enter Librarian ID: ");
        int id = sc.nextInt();
        System.out.print("🔐 Enter Password: ");
        String password = sc.next();

        Librarian loggedIn = null;
        for (Librarian l : librarians) {
            if (l.getLibrarianId() == id && DatabaseManager.verifyLibrarianPassword(id, password)) {
                loggedIn = l;
                break;
            }
        }

        if (loggedIn != null) {
            while (true) {
                System.out.println("\n" + "=".repeat(45));
                System.out.println("👨‍💼 LIBRARIAN DASHBOARD");
                System.out.println("=".repeat(45));
                System.out.println("🔐 Logged in as: " + loggedIn.getName());
                System.out.println("=".repeat(45));
                System.out.println("1. ➕ Add New Book");
                System.out.println("2. ➖ Remove Book");
                System.out.println("3. 📚 Show All Books");
                System.out.println("4. 📖 Show Issued Books");
                System.out.println("5. 📝 Update Book Stock");
                System.out.println("6. 🔄 Update Book Details");
                System.out.println("7. 👨‍🎓 Add New Student");      // NEW OPTION
                System.out.println("8. 🔑 Change Password");        // NEW OPTION
                System.out.println("9. 🚪 Logout");
                System.out.println("=".repeat(45));
                System.out.print("📝 Choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1 -> {
                        System.out.print("📖 Enter Book ID: ");
                        int bid = sc.nextInt();
                        sc.nextLine(); // consume newline
                        System.out.print("📝 Enter Title: ");
                        String title = sc.nextLine();
                        System.out.print("🏢 Enter Publisher: ");
                        String pub = sc.nextLine();
                        System.out.print("📄 Enter Edition: ");
                        String ed = sc.nextLine();
                        System.out.print("🔢 Enter Copies: ");
                        int copies = sc.nextInt();

                        library.addBook(new Book(bid, title, pub, ed, copies));
                    }
                    case 2 -> {
                        System.out.print("🗑️ Enter Book ID to remove: ");
                        int bid = sc.nextInt();
                        library.removeBook(bid);
                    }
                    case 3 -> library.showBooks();
                    case 4 -> library.showIssuedBooks();
                    case 5 -> {
                        System.out.print("📖 Enter Book ID to update stock: ");
                        int bid = sc.nextInt();

                        library.showBookForEdit(bid);

                        System.out.print("🔢 Enter new stock quantity: ");
                        int newStock = sc.nextInt();

                        library.updateBookStock(bid, newStock);
                    }
                    case 6 -> {
                        System.out.print("📖 Enter Book ID to update: ");
                        int bid = sc.nextInt();

                        library.showBookForEdit(bid);

                        sc.nextLine(); // consume newline
                        System.out.print("📝 Enter new title: ");
                        String title = sc.nextLine();
                        System.out.print("🏢 Enter new publisher: ");
                        String pub = sc.nextLine();
                        System.out.print("📄 Enter new edition: ");
                        String ed = sc.nextLine();
                        System.out.print("🔢 Enter new stock: ");
                        int copies = sc.nextInt();

                        library.updateBookDetails(bid, title, pub, ed, copies);
                    }
                    case 7 -> {
                        // NEW: Add new student
                        System.out.print("🆔 Enter Student ID: ");
                        int sid = sc.nextInt();
                        sc.nextLine(); // consume newline
                        System.out.print("👤 Enter Student Name: ");
                        String name = sc.nextLine();
                        System.out.print("🏫 Enter Department: ");
                        String dept = sc.nextLine();
                        System.out.print("📚 Enter Course: ");
                        String course = sc.nextLine();

                        library.addNewStudent(sid, name, dept, course);
                    }
                    case 8 -> {
                        // NEW: Change password
                        System.out.print("🔐 Enter new password: ");
                        String newPassword = sc.next();
                        System.out.print("🔐 Confirm new password: ");
                        String confirmPassword = sc.next();

                        if (newPassword.equals(confirmPassword)) {
                            library.changeLibrarianPassword(loggedIn.getLibrarianId(), newPassword);
                        } else {
                            System.out.println("❌ Passwords don't match!");
                        }
                    }
                    case 9 -> {
                        System.out.println("👋 Logging out...");
                        return;
                    }
                    default -> System.out.println("❌ Invalid choice!");
                }
            }
        } else {
            System.out.println("❌ Invalid Librarian ID or Password!");
        }
    }

    // ================= Student Menu =================
    private static void studentMenu(List<Librarian> librarians, Library library, Student... students) {
        System.out.print("🆔 Enter Student ID: ");
        int sid = sc.nextInt();
        System.out.print("🔐 Enter Password: ");
        String password = sc.next();

        Student loggedIn = null;
        for (Student s : students) {
            if (s.getStudentId() == sid && DatabaseManager.verifyStudentPassword(sid, password)) {
                loggedIn = s;
                break;
            }
        }

        // Also check database for any new students added by librarian
        if (loggedIn == null) {
            Student dbStudent = DatabaseManager.getStudent(sid);
            if (dbStudent != null && DatabaseManager.verifyStudentPassword(sid, password)) {
                loggedIn = dbStudent;
            }
        }

        if (loggedIn != null) {
            while (true) {
                System.out.println("\n" + "=".repeat(40));
                System.out.println("👨‍🎓 STUDENT DASHBOARD");
                System.out.println("=".repeat(40));
                System.out.println("🔐 Logged in as: " + loggedIn.getName());
                System.out.println("=".repeat(40));
                System.out.println("1. 📖 Issue Book");
                System.out.println("2. 📚 Return Book");
                System.out.println("3. 💰 Check Fine Status");
                System.out.println("4. 🔑 Change Password");      // NEW OPTION
                System.out.println("5. 🚪 Logout");
                System.out.println("=".repeat(40));
                System.out.print("📝 Choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1 -> {
                        library.showBooks();
                        System.out.print("📖 Enter Book ID to issue: ");
                        int bid = sc.nextInt();

                        library.issueBook(loggedIn, bid, librarians.get(0));
                    }
                    case 2 -> {
                        System.out.print("📚 Enter Book ID to return: ");
                        int bid = sc.nextInt();

                        library.returnBook(loggedIn, bid, librarians.get(0));
                    }
                    case 3 -> {
                        library.checkFineForStudent(loggedIn);
                    }
                    case 4 -> {
                        // NEW: Change password
                        System.out.print("🔐 Enter new password: ");
                        String newPassword = sc.next();
                        System.out.print("🔐 Confirm new password: ");
                        String confirmPassword = sc.next();

                        if (newPassword.equals(confirmPassword)) {
                            library.changeStudentPassword(loggedIn.getStudentId(), newPassword);
                        } else {
                            System.out.println("❌ Passwords don't match!");
                        }
                    }
                    case 5 -> {
                        System.out.println("👋 Logging out...");
                        return;
                    }
                    default -> System.out.println("❌ Invalid choice!");
                }
            }
        } else {
            System.out.println("❌ Invalid Student ID or Password!");
        }
    }
}
