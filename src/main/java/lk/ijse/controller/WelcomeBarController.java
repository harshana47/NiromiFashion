package lk.ijse.controller;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeBarController implements Initializable {


    @FXML
    private ProgressBar progressBar;

    @FXML
    private Text loadingText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulateLoadingAnimation();
        applyAnimations();
    }

    private void simulateLoadingAnimation() {
        Duration animationDuration = Duration.seconds(3); // Adjust as needed
        double endProgress = 1.0; // ProgressBar completion value

        KeyFrame startFrame = new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0));
        KeyFrame endFrame = new KeyFrame(animationDuration, new KeyValue(progressBar.progressProperty(), endProgress));

        Timeline timeline = new Timeline(startFrame, endFrame);
        timeline.setOnFinished(event -> navigateToLoginPage());
        timeline.play();
    }

    private void applyAnimations() {
        // Fade in animation for the loading text
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), loadingText);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.play();

        // Scale animation for the loading text
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), loadingText);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }



    private void navigateToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loginForm.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) progressBar.getScene().getWindow(); // Get the stage from the progress bar scene
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error loading the login page
        }
    }
}