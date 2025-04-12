package com.example.module03_basicgui_db_interface;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the splash screen
 */
public class SplashScreenController implements Initializable {

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressBar progressBar;

    /**
     * Initializing the controller
     *
     * @param url Location
     * @param rb Resources
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusLabel.setText("Loading application...");

        // Simulate the loading process of the splash screen into the main screen
        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    int progress = i;
                    javafx.application.Platform.runLater(() -> {
                        progressBar.setProgress(progress / 100.0);
                        if (progress < 30) {
                            statusLabel.setText("Initializing...");
                        } else if (progress < 60) {
                            statusLabel.setText("Connecting to database...");
                        } else if (progress < 90) {
                            statusLabel.setText("Loading user interface...");
                        } else {
                            statusLabel.setText("Ready to launch!");
                        }
                    });
                    Thread.sleep(20); // Adjust "20" to change the speed of the bar's progress
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}