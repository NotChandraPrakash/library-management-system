package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import librarysystem.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class LibrarianDashboardController {
    
    @FXML private Label lblLibrarianName;
    @FXML private Button btnLogout;
    
    // View Books Tab
    @FXML private TableView<Book> tblBooks;
    @FXML private TableColumn<Book, Integer> colBookId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colPublisher;
    @FXML private TableColumn<Book, String> colEdition;
    @FXML private TableColumn<Book, Integer> colCopies;
    
    // Add Book Tab
    @FXML private TextField txtAddBookId;
    @FXML private TextField txtAddTitle;
    @FXML private TextField txtAddPublisher;
    @FXML private TextField txtAddEdition;
    @FXML private TextField txtAddCopies;
    @FXML private Label lblAddBookStatus;
    
    // Remove Book Tab
    @FXML private TextField txtRemoveBookId;
    @FXML private Label lblRemoveBookStatus;
    
    // Issued Books Tab
    @FXML private TextArea txtIssuedBooks;
    
    // Add Student Tab
    @FXML private TextField txtAddStudentId;
    @FXML private TextField txtAddStudentName;
    @FXML private TextField txtAddStudentDept;
    @FXML private TextField txtAddStudentCourse;
    @FXML private Label lblAddStudentStatus;
    
    // Change Password Tab
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblChangePasswordStatus;
    
    private Librarian librarian;
    private Library library = new Library();
    
    @FXML
    public void initialize() {
        // Initialize table columns
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colEdition.setCellValueFactory(new PropertyValueFactory<>("edition"));
        colCopies.setCellValueFactory(new PropertyValueFactory<>("copies"));
        
        loadBooks();
        loadIssuedBooks();
    }
    
    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
        lblLibrarianName.setText("Logged in as: " + librarian.getName());
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("🏛️ Digital Library Management System");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRefreshBooks() {
        loadBooks();
    }
    
    private void loadBooks() {
        List<Book> books = DatabaseManager.getAllBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        tblBooks.setItems(bookList);
    }
    
    @FXML
    private void handleAddBook() {
        try {
            int bookId = Integer.parseInt(txtAddBookId.getText().trim());
            String title = txtAddTitle.getText().trim();
            String publisher = txtAddPublisher.getText().trim();
            String edition = txtAddEdition.getText().trim();
            int copies = Integer.parseInt(txtAddCopies.getText().trim());
            
            if (title.isEmpty() || publisher.isEmpty() || edition.isEmpty()) {
                showAddBookStatus("Please fill all fields!", "red");
                return;
            }
            
            Book newBook = new Book(bookId, title, publisher, edition, copies);
            library.addBook(newBook);
            
            showAddBookStatus("✅ Book added successfully!", "green");
            clearAddBookFields();
            loadBooks();
            
        } catch (NumberFormatException e) {
            showAddBookStatus("❌ Book ID and Copies must be numbers!", "red");
        }
    }
    
    private void clearAddBookFields() {
        txtAddBookId.clear();
        txtAddTitle.clear();
        txtAddPublisher.clear();
        txtAddEdition.clear();
        txtAddCopies.clear();
    }
    
    private void showAddBookStatus(String message, String color) {
        lblAddBookStatus.setText(message);
        lblAddBookStatus.setStyle("-fx-text-fill: " + color + ";");
    }
    
    @FXML
    private void handleRemoveBook() {
        try {
            int bookId = Integer.parseInt(txtRemoveBookId.getText().trim());
            
            // Capture console output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);
            
            library.removeBook(bookId);
            
            System.out.flush();
            System.setOut(old);
            
            String output = baos.toString();
            if (output.contains("✅")) {
                showRemoveBookStatus("✅ Book removed successfully!", "green");
            } else {
                showRemoveBookStatus("❌ Book not found!", "red");
            }
            
            txtRemoveBookId.clear();
            loadBooks();
            
        } catch (NumberFormatException e) {
            showRemoveBookStatus("❌ Book ID must be a number!", "red");
        }
    }
    
    private void showRemoveBookStatus(String message, String color) {
        lblRemoveBookStatus.setText(message);
        lblRemoveBookStatus.setStyle("-fx-text-fill: " + color + ";");
    }
    
    @FXML
    private void handleRefreshIssuedBooks() {
        loadIssuedBooks();
    }
    
    private void loadIssuedBooks() {
        // Capture console output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        
        DatabaseManager.displayIssuedBooks();
        
        System.out.flush();
        System.setOut(old);
        
        txtIssuedBooks.setText(baos.toString());
    }
    
    @FXML
    private void handleAddStudent() {
        try {
            int studentId = Integer.parseInt(txtAddStudentId.getText().trim());
            String name = txtAddStudentName.getText().trim();
            String department = txtAddStudentDept.getText().trim();
            String course = txtAddStudentCourse.getText().trim();
            
            if (name.isEmpty() || department.isEmpty() || course.isEmpty()) {
                showAddStudentStatus("Please fill all fields!", "red");
                return;
            }
            
            Student student = new Student(studentId, name, department, course);
            DatabaseManager.addStudent(student);
            
            showAddStudentStatus("✅ Student added successfully!", "green");
            clearAddStudentFields();
            
        } catch (NumberFormatException e) {
            showAddStudentStatus("❌ Student ID must be a number!", "red");
        }
    }
    
    private void clearAddStudentFields() {
        txtAddStudentId.clear();
        txtAddStudentName.clear();
        txtAddStudentDept.clear();
        txtAddStudentCourse.clear();
    }
    
    private void showAddStudentStatus(String message, String color) {
        lblAddStudentStatus.setText(message);
        lblAddStudentStatus.setStyle("-fx-text-fill: " + color + ";");
    }
    
    @FXML
    private void handleChangePassword() {
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showChangePasswordStatus("Please fill all fields!", "red");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showChangePasswordStatus("❌ Passwords do not match!", "red");
            return;
        }
        
        library.changeLibrarianPassword(librarian.getLibrarianId(), newPassword);
        showChangePasswordStatus("✅ Password changed successfully!", "green");
        
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }
    
    private void showChangePasswordStatus(String message, String color) {
        lblChangePasswordStatus.setText(message);
        lblChangePasswordStatus.setStyle("-fx-text-fill: " + color + ";");
    }
}
