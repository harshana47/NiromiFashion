package lk.ijse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.model.User;
import lk.ijse.repository.UserRepo;

import java.sql.SQLException;
import java.util.List;

public class RegistrationFormController {
    @FXML
    private TableColumn<User, String> colId;

    @FXML
    private TableColumn<User, String> colName;

    @FXML
    private TableColumn<User, String> colPassword;

    @FXML
    private TableColumn<User, String> colPhone;

    @FXML
    private TableView<User> tblUsers;

    @FXML
    private TextField txtUserId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtPhone;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private UserRepo userRepo = new UserRepo();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = userRepo.getAllUsers();
            userList.setAll(users);
            tblUsers.setItems(userList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading users: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) {
        String userId = txtUserId.getText();
        String name = txtName.getText();
        String password = txtPassword.getText();
        String phone = txtPhone.getText();

        try {
            boolean isSaved = userRepo.saveUser(userId, name, password, phone);
            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "User saved successfully!").show();
                initialize();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to save user!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error saving user: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String userId = txtUserId.getText();
        if (userId.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a user ID to delete.").show();
            return;
        }

        try {
            boolean isDeleted = userRepo.deleteUser(userId);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "User deleted successfully!").show();
                initialize();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete user. User not found.").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error deleting user: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String userId = txtUserId.getText();
        if (userId.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a user ID to search.").show();
            return;
        }

        try {
            User user = userRepo.getUserById(userId);
            if (user != null) {
                txtName.setText(user.getName());
                txtPassword.setText(user.getPassword());
                txtPhone.setText(user.getPhone());
            } else {
                new Alert(Alert.AlertType.ERROR, "User not found.").show();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error searching user: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String userId = txtUserId.getText();
        String name = txtName.getText();
        String password = txtPassword.getText();
        String phone = txtPhone.getText();

        if (userId.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a user ID to update.").show();
            return;
        }

        try {
            boolean isUpdated = userRepo.updateUser(userId, name, password, phone);
            if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "User updated successfully!").show();
                initialize();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update user. User not found.").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error updating user: " + e.getMessage()).show();
        }
    }

    private void clearFields() {
        txtUserId.clear();
        txtName.clear();
        txtPassword.clear();
        txtPhone.clear();
    }
}
