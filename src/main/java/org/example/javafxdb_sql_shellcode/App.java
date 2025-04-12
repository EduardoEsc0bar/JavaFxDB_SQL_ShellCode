package org.example.javafxdb_sql_shellcode;

import com.example.module03_basicgui_db_interface.Person;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.javafxdb_sql_shellcode.db.ConnDbOps;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * Main JavaFX Application
 * Provides both GUI and CLI interfaces
 */
public class App extends Application {

    private static Scene scene;
    private static ConnDbOps cdbop;
    private static boolean darkMode = false;
    private static Stage primaryStage;

    /**
     * Start the JavaFX application
     * @param stage Primary stage
     * @throws IOException if FXML files cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setResizable(false);
        showSplashScreen();
    }

    /**
     * Display the splash screen
     * @throws IOException if FXML file cannot be loaded
     */
    private void showSplashScreen() throws IOException {
        // Create splash screen
        URL splashResourceUrl = getClass().getResource("/org/example/javafxdb_sql_shellcode/splash_screen.fxml");
        System.out.println("Loading splash screen from: " + splashResourceUrl);

        if (splashResourceUrl == null) {
            throw new IOException("Cannot find splash_screen.fxml in resources");
        }

        FXMLLoader splashLoader = new FXMLLoader(splashResourceUrl);
        scene = new Scene(splashLoader.load(), 850, 560);

        // Apply current theme
        applyTheme(scene);

        primaryStage.setTitle("User Management System");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Create a thread to delay loading the main screen with better debugging
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Splash screen thread started, waiting 3 seconds...");
                Thread.sleep(3000); // 3 seconds delay

                System.out.println("Splash screen delay complete, switching to main view...");
                javafx.application.Platform.runLater(() -> {
                    try {
                        System.out.println("Attempting to load main view...");
                        // Check if main view FXML exists
                        URL mainViewUrl = getClass().getResource("/org/example/javafxdb_sql_shellcode/main_view.fxml");
                        System.out.println("Main view URL: " + mainViewUrl);

                        if (mainViewUrl == null) {
                            System.err.println("ERROR: Cannot find main_view.fxml");
                            return;
                        }

                        fadeToMainScreen();
                    } catch (Exception e) {
                        System.err.println("ERROR during transition to main view:");
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                System.err.println("Splash screen thread interrupted:");
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Transition from splash screen to main screen with fade effect
     * @throws IOException if FXML file cannot be loaded
     */
    private void fadeToMainScreen() throws IOException {
        try {
            System.out.println("Starting fade transition to main view...");
            URL mainViewUrl = getClass().getResource("/org/example/javafxdb_sql_shellcode/main_view.fxml");

            if (mainViewUrl == null) {
                throw new IOException("Cannot find main_view.fxml");
            }

            FXMLLoader mainLoader = new FXMLLoader(mainViewUrl);
            Parent newRoot = mainLoader.load();
            System.out.println("Main view loaded successfully");

            Parent currentRoot = scene.getRoot();
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), currentRoot);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                System.out.println("Fade out complete, setting new scene...");
                scene = new Scene(newRoot, 850, 560);
                // Apply current theme
                applyTheme(scene);
                primaryStage.setScene(scene);
                primaryStage.setTitle("User Management System");
                System.out.println("Main view displayed");
            });

            fadeOut.play();
        } catch (Exception e) {
            System.err.println("ERROR in fadeToMainScreen:");
            e.printStackTrace();
            // If fade transition fails, just show main screen directly
            System.out.println("Attempting to show main screen directly instead...");
            showMainScreen();
        }
    }

    /**
     * Show the main application screen
     * @throws IOException if FXML file cannot be loaded
     */
    private void showMainScreen() throws IOException {
        System.out.println("Showing main screen directly...");
        URL mainViewUrl = getClass().getResource("/org/example/javafxdb_sql_shellcode/main_view.fxml");

        if (mainViewUrl == null) {
            throw new IOException("Cannot find main_view.fxml");
        }

        FXMLLoader mainLoader = new FXMLLoader(mainViewUrl);
        scene = new Scene(mainLoader.load(), 850, 560);

        // Set the scene's style based on current theme
        applyTheme(scene);

        primaryStage.setScene(scene);
        primaryStage.setTitle("User Management System");
        primaryStage.show();
        System.out.println("Main screen displayed successfully");
    }

    /**
     * Toggle dark mode
     * @return Current state of dark mode after toggle
     */
    public static boolean toggleDarkMode() {
        darkMode = !darkMode;
        applyTheme(scene);
        return darkMode;
    }

    /**
     * Get current dark mode state
     * @return True if dark mode is active
     */
    public static boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Apply current theme to scene
     * @param scene Scene to apply theme to
     */
    private static void applyTheme(Scene scene) {
        try {
            scene.getStylesheets().clear();

            // Try to load the appropriate CSS file
            URL cssUrl;
            if (darkMode) {
                cssUrl = App.class.getResource("/org/example/javafxdb_sql_shellcode/styles/style-dark.css");
                System.out.println("Attempting to load dark theme from: " + cssUrl);
            } else {
                cssUrl = App.class.getResource("/org/example/javafxdb_sql_shellcode/styles/style.css");
                System.out.println("Attempting to load light theme from: " + cssUrl);
            }

            // If the CSS file is found, apply it
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("Theme applied successfully");
            } else {
                System.err.println("WARNING: Theme CSS file not found. Using default styling.");
                // Continue without applying a stylesheet - JavaFX will use default styling
            }
        } catch (Exception e) {
            System.err.println("Error applying theme: " + e.getMessage());
            e.printStackTrace();
            // Continue without styling rather than crashing the application
        }
    }

    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        cdbop = new ConnDbOps();
        cdbop.connectToDatabase();

        // Check for CLI mode argument
        if (args.length > 0 && args[0].equals("--cli")) {
            runCliMode();
        } else {
            launch(args);
        }
    }

    /**
     * Run the application in CLI mode
     */
    private static void runCliMode() {
        Scanner scan = new Scanner(System.in);

        char input;
        do {
            System.out.println(" ");
            System.out.println("============== Menu ==============");
            System.out.println("| To start GUI,           press 'g' |");
            System.out.println("| To connect to DB,       press 'c' |");
            System.out.println("| To display all users,   press 'a' |");
            System.out.println("| To insert to the DB,    press 'i' |");
            System.out.println("| To query by name,       press 'q' |");
            System.out.println("| To update user,         press 'u' |");
            System.out.println("| To delete user,         press 'd' |");
            System.out.println("| To exit,                press 'e' |");
            System.out.println("===================================");
            System.out.print("Enter your choice: ");
            input = scan.next().charAt(0);

            switch (input) {
                case 'g':
                    // Launch GUI
                    launch();
                    break;
                case 'c':
                    cdbop.connectToDatabase();
                    break;
                case 'a':
                    cdbop.listAllUsers();
                    break;
                case 'i':
                    System.out.print("Enter First Name: ");
                    scan.nextLine(); // Consume newline
                    String firstName = scan.nextLine();
                    System.out.print("Enter Last Name: ");
                    scan.nextLine(); // Consume newline
                    String lastName = scan.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scan.nextLine();
                    System.out.print("Enter Phone: ");
                    String phone = scan.nextLine();
                    System.out.print("Enter Address: ");
                    String address = scan.nextLine();
                    System.out.print("Enter Password: ");
                    String password = scan.nextLine();

                    Person person = new Person(null, firstName, lastName, email, phone, address, password);
                    cdbop.insertUser(person);
                    break;
                case 'q':
                    System.out.print("Enter the name to query: ");
                    scan.nextLine(); // Consume newline
                    String queryName = scan.nextLine();
                    cdbop.queryUserByName(queryName);
                    break;
                case 'u':
                    System.out.print("Enter user ID to update: ");
                    int updateId = scan.nextInt();
                    scan.nextLine(); // Consume newline

                    System.out.print("Enter new First Name: ");
                    String updateFirstName = scan.nextLine();
                    System.out.print("Enter new Last Name: ");
                    String updateLastName = scan.nextLine();
                    System.out.print("Enter new Email: ");
                    String updateEmail = scan.nextLine();
                    System.out.print("Enter new Phone: ");
                    String updatePhone = scan.nextLine();
                    System.out.print("Enter new Address: ");
                    String updateAddress = scan.nextLine();
                    System.out.print("Enter new Password: ");
                    String updatePassword = scan.nextLine();

                    Person updatePerson = new Person(updateId, updateFirstName, updateLastName, updateEmail, updatePhone, updateAddress, updatePassword);
                    boolean updateSuccess = cdbop.updateUser(updatePerson);
                    if (updateSuccess) {
                        System.out.println("User updated successfully.");
                    } else {
                        System.out.println("Failed to update user.");
                    }
                    break;
                case 'd':
                    System.out.print("Enter user ID to delete: ");
                    int deleteId = scan.nextInt();
                    boolean deleteSuccess = cdbop.deleteUser(deleteId);
                    if (deleteSuccess) {
                        System.out.println("User deleted successfully.");
                    } else {
                        System.out.println("Failed to delete user.");
                    }
                    break;
                case 'e':
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            System.out.println(" ");
        } while (input != 'e');

        scan.close();
    }
}