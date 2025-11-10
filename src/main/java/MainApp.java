import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import librarysystem.*;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database
            if (!DatabaseManager.testConnection()) {
                System.out.println("Database connection failed.");
                return;
            }

            DatabaseManager.initializeDatabase();

            // Predefined librarians
            Librarian lib1 = new Librarian(101, "Mr. Sharma");
            Librarian lib2 = new Librarian(102, "Mrs. Sumita");
            DatabaseManager.addLibrarian(lib1);
            DatabaseManager.addLibrarian(lib2);

            // Predefined students
            Student s1 = new Student(201, "Rahul", "CSE", "B.Tech");
            Student s2 = new Student(202, "Priya", "ECE", "B.Tech");
            DatabaseManager.addStudent(s1);
            DatabaseManager.addStudent(s2);

            // Preload books
            Library library = new Library();
            library.addBook(new Book(1001, "Java Programming", "Pearson", "3rd", 2));
            library.addBook(new Book(1002, "Data Structures", "McGraw Hill", "2nd", 1));
            library.addBook(new Book(1003, "Database Systems", "Elsevier", "4th", 3));

            System.out.println("✅ Library Management System initialized successfully!");

            // Load Login Screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("🏛️ Digital Library Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
