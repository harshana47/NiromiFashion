package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import lk.ijse.db.DbConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CashierFormController implements Initializable {
    public JFXButton btnLogin;
    public TextField txtPassword;
    public TextField txtUserName;
    public AnchorPane rootNode;
    public MediaView mediaView;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        String mediaFileUrl = "file:///C:/Users/Harshana/Downloads/LARS-SKINCARE-VIDEO.mp4";
        Media media = new Media(mediaFileUrl);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String userId = txtUserName.getText();
        String password = txtPassword.getText();

        try {
            if (checkCredential(userId, password)) {
                navigateToTheCashier();
            }
        } catch (SQLException | IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }


    private boolean checkCredential(String userId, String password) throws SQLException, IOException {
        String sql = "SELECT password FROM user WHERE userId = ?";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1, userId);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            String dbPw = resultSet.getString("password");

            if (password.equals(dbPw)) {
                navigateToTheCashier();
            } else {
                new Alert(Alert.AlertType.ERROR, "Sorry! Password is incorrect!").show();
            }

        } else {
            new Alert(Alert.AlertType.INFORMATION, "Sorry! User ID not found!").show();
        }
        return false;
    }

    private void navigateToTheCashier() throws IOException {
        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/orderForm.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) this.rootNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Order Form");
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
