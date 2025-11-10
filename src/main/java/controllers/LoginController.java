package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import librarysystem.*;

public class LoginController {
    
    @FXML private RadioButton rbLibrarian;
    @FXML private RadioButton rbStudent;
    @FXML private TextField txtUserId;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblStatus;
    
    @FXML
    private void handleLogin() {
        String userIdText = txtUserId.getText().trim();
        String password = txtPassword.getText().trim();
        
        if (userIdText.isEmpty() || password.isEmpty()) {
            showError("Please enter both User ID and Password!");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdText);
            
            if (rbLibrarian.isSelected()) {
                // Librarian Login
                if (DatabaseManager.verifyLibrarianPassword(userId, password)) {
                    Librarian librarian = DatabaseManager.getLibrarian(userId);
                    if (librarian != null) {
                        showSuccess("Login successful! Welcome, " + librarian.getName());
                        openLibrarianDashboard(librarian);
                    } else {
                        showError("Librarian not found!");
                    }
                } else {
                    showError("Invalid Librarian ID or Password!");
                }
            } else {
                // Student Login
                if (DatabaseManager.verifyStudentPassword(userId, password)) {
                    Student student = DatabaseManager.getStudent(userId);
                    if (student != null) {
                        showSuccess("Login successful! Welcome, " + student.getName());
                        openStudentDashboard(student);
                    } else {
                        showError("Student not found!");
                    }
                } else {
                    showError("Invalid Student ID or Password!");
                }
            }
            
        } catch (NumberFormatException e) {
            showError("User ID must be a number!");
        }
    }
    
    private void openLibrarianDashboard(Librarian librarian) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LibrarianDashboard.fxml"));
            Parent root = loader.load();
            
            LibrarianDashboardController controller = loader.getController();
            controller.setLibrarian(librarian);
            
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Librarian Dashboard - " + librarian.getName());
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open Librarian Dashboard!");
        }
    }
    
    private void openStudentDashboard(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StudentDashboard.fxml"));
            Parent root = loader.load();
            
            StudentDashboardController controller = loader.getController();
            controller.setStudent(student);
            
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Dashboard - " + student.getName());
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open Student Dashboard!");
        }
    }
    
    private void showError(String message) {
        lblStatus.setText("❌ " + message);
        lblStatus.setStyle("-fx-text-fill: red;");
    }
    
    private void showSuccess(String message) {
        lblStatus.setText("✅ " + message);
        lblStatus.setStyle("-fx-text-fill: green;");
    }
}
