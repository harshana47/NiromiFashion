package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.db.DbConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {

    public JFXButton btnLogin;
    @FXML
    private AnchorPane rootNode;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUserName;

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        String userId = txtUserName.getText();
        String password = txtPassword.getText();

   /*     try {
            checkCredential(userId, password);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }*/
        navigateToTheDashboard();

    }

    private void checkCredential(String userId, String password) throws SQLException, IOException {
        String sql = "SELECT name, password FROM User WHERE userId = ?";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1, userId);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            String dbPw = resultSet.getString("password");

            if (password.equals(dbPw)) {
                navigateToTheDashboard();
            } else {
                new Alert(Alert.AlertType.ERROR, "Sorry! Password is incorrect!").show();
            }

        } else {
            new Alert(Alert.AlertType.INFORMATION, "Sorry! User ID not found!").show();
        }
    }

    private void navigateToTheDashboard() throws IOException {
        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/dashboardForm.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) this.rootNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }

    @FXML
    void linkRegistrationOnAction(ActionEvent event) throws IOException {
        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/registrationForm.fxml"));

        Scene scene = new Scene(rootNode);
        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setTitle("Registration Form");

        stage.show();
    }

    @FXML
    void txtPasswordOnAction(ActionEvent event) {
    }

    @FXML
    void txtUserNameOnAction(ActionEvent event) {
    }

}
