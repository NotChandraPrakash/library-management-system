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

public class StudentDashboardController {
    
    @FXML private Label lblStudentInfo;
    @FXML private Button btnLogout;
    
    // View Available Books Tab
    @FXML private TableView<Book> tblAvailableBooks;
    @FXML private TableColumn<Book, Integer> colBookId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colPublisher;
    @FXML private TableColumn<Book, String> colEdition;
    @FXML private TableColumn<Book, Integer> colCopiesAvailable;
    
    // Issue Book Tab
    @FXML private TextField txtIssueBookId;
    @FXML private TextField txtIssueDays;
    @FXML private Label lblIssueBookStatus;
    
    // Return Book Tab
    @FXML private TextField txtReturnBookId;
    @FXML private Label lblReturnBookStatus;
    
    // My Issued Books Tab
    @FXML private TextArea txtMyIssuedBooks;
    
    // Change Password Tab
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblChangePasswordStatus;
    
    private Student student;
    private Library library = new Library();
    
    @FXML
    public void initialize() {
        // Initialize table columns
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colEdition.setCellValueFactory(new PropertyValueFactory<>("edition"));
        colCopiesAvailable.setCellValueFactory(new PropertyValueFactory<>("copies"));
        
        loadAvailableBooks();
        // Do NOT call loadMyIssuedBooks() here because initialize() is invoked
        // during FXMLLoader.load(), which happens before the caller (LoginController)
        // has a chance to call setStudent(). Calling loadMyIssuedBooks() here
        // can cause a NullPointerException when `student` is null.
        // If the student was already set earlier (unlikely), call it safely.
        if (this.student != null) {
            loadMyIssuedBooks();
        }
    }
    
    public void setStudent(Student student) {
        this.student = student;
        lblStudentInfo.setText("Logged in as: " + student.getName());
        // Now that the student is set, safely load issued books for this student
        loadMyIssuedBooks();
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
    private void handleRefreshAvailableBooks() {
        loadAvailableBooks();
    }
    
    private void loadAvailableBooks() {
        List<Book> books = DatabaseManager.getAllBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        tblAvailableBooks.setItems(bookList);
    }
    
    @FXML
    private void handleIssueBook() {
        try {
            int bookId = Integer.parseInt(txtIssueBookId.getText().trim());
            
            // Capture console output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);
            
            // Note: Library needs Librarian object, using a default/system librarian
            Librarian defaultLibrarian = new Librarian(0, "System");
            library.issueBook(student, bookId, defaultLibrarian);
            
            System.out.flush();
            System.setOut(old);
            
            String output = baos.toString();
            if (output.contains("✅")) {
                showIssueBookStatus("✅ Book issued successfully! Due date is set.", "green");
            } else {
                showIssueBookStatus("❌ " + output.trim(), "red");
            }
            
            txtIssueBookId.clear();
            txtIssueDays.clear();
            loadAvailableBooks();
            loadMyIssuedBooks();
            
        } catch (NumberFormatException e) {
            showIssueBookStatus("❌ Book ID must be a number!", "red");
        }
    }
    
    private void showIssueBookStatus(String message, String color) {
        lblIssueBookStatus.setText(message);
        lblIssueBookStatus.setStyle("-fx-text-fill: " + color + ";");
    }
    
    @FXML
    private void handleReturnBook() {
        try {
            int bookId = Integer.parseInt(txtReturnBookId.getText().trim());
            
            // Capture console output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);
            
            // Note: Library needs Librarian object, using a default/system librarian
            Librarian defaultLibrarian = new Librarian(0, "System");
            library.returnBook(student, bookId, defaultLibrarian);
            
            System.out.flush();
            System.setOut(old);
            
            String output = baos.toString();
            if (output.contains("✅")) {
                showReturnBookStatus("✅ Book returned successfully!", "green");
            } else if (output.contains("💰") || output.contains("Fine")) {
                showReturnBookStatus("⚠️ Book returned with fine applied.", "orange");
            } else {
                showReturnBookStatus("❌ " + output.trim(), "red");
            }
            
            txtReturnBookId.clear();
            loadAvailableBooks();
            loadMyIssuedBooks();
            
        } catch (NumberFormatException e) {
            showReturnBookStatus("❌ Book ID must be a number!", "red");
        }
    }
    
    private void showReturnBookStatus(String message, String color) {
        lblReturnBookStatus.setText(message);
        lblReturnBookStatus.setStyle("-fx-text-fill: " + color + ";");
    }
    
    @FXML
    private void handleRefreshMyBooks() {
        loadMyIssuedBooks();
    }
    
    private void loadMyIssuedBooks() {
        String issuedBooks = DatabaseManager.displayStudentIssuedBooks(student.getStudentId());
        txtMyIssuedBooks.setText(issuedBooks);
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
        
        library.changeStudentPassword(student.getStudentId(), newPassword);
        showChangePasswordStatus("✅ Password changed successfully!", "green");
        
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }
    
    private void showChangePasswordStatus(String message, String color) {
        lblChangePasswordStatus.setText(message);
        lblChangePasswordStatus.setStyle("-fx-text-fill: " + color + ";");
    }
}
