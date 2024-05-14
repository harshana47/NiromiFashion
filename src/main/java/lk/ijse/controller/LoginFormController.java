package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.db.DbConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable {

    public JFXButton btnLogin;
    public ProgressBar progressBar;
    public MediaView mediaView;
    private File file;
    private MediaPlayer mediaPlayer;
    private Media media;

    @FXML
    private AnchorPane rootNode;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUserName;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        String mediaFileUrl = "file:///C:/Users/Harshana/Downloads/LARS-SKINCARE-VIDEO.mp4";
        Media media = new Media(mediaFileUrl);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);
    }


    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        String userId = txtUserName.getText();
        String password = txtPassword.getText();

   /*     try {
            checkCredential(userId, password);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }*/
        //applyAnimations();
        //simulateLoadingAnimation();
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

   /* private void simulateLoadingAnimation() {
        Duration animationDuration = Duration.seconds(1); // Adjust as needed
        double endProgress = 1.0; // ProgressBar completion value

        KeyFrame startFrame = new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0));
        KeyFrame endFrame = new KeyFrame(animationDuration, new KeyValue(progressBar.progressProperty(), endProgress));

        Timeline timeline = new Timeline(startFrame, endFrame);
        timeline.setOnFinished(event -> navigateToTheDashboard());
        timeline.play();
    }

    private void applyAnimations() {
        // Fade in animation for the loading text
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.play();

        // Scale animation for the loading text
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5));
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }*/



    private void navigateToTheDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/dashboardForm.fxml"));
            Stage stage = (Stage) rootNode.getScene().getWindow(); // Get the stage from the root node
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard Form");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error loading the dashboard page
        }
    }



    @FXML
    void txtPasswordOnAction(ActionEvent event) {
    }

    @FXML
    void txtUserNameOnAction(ActionEvent event) {
    }

}
