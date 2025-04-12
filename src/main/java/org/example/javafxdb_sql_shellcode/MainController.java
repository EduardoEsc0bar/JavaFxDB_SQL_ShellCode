package org.example.javafxdb_sql_shellcode;

import com.example.module03_basicgui_db_interface.Person;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.example.javafxdb_sql_shellcode.db.ConnDbOps;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the main view
 * Handles UI interactions and database operations
 */
public class MainController implements Initializable {

    @FXML
    private TextField firstNameField, lastNameField, emailField, phoneField, addressField, passwordField;

    @FXML
    private TableView<Person> userTable;

    @FXML
    private TableColumn<Person, Integer> idColumn;

    @FXML
    private TableColumn<Person, String> nameColumn, emailColumn, phoneColumn, addressColumn;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label statusLabel;

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem fileNewMenuItem, fileOpenMenuItem, fileSaveMenuItem, fileExitMenuItem,
            editClearMenuItem, editDeleteMenuItem, editUpdateMenuItem, viewRefreshMenuItem,
            themeToggleMenuItem, helpAboutMenuItem;

    private ConnDbOps dbOps;
    private ObservableList<Person> userData;
    private File selectedImageFile = null;

    /**
     * Initialize the controller
     * @param url Location
     * @param rb Resources
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize database connection
        dbOps = new ConnDbOps();
        dbOps.connectToDatabase();

        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Load data from database
        refreshTableData();

        // Set up menu keyboard shortcuts
        setupMenuShortcuts();

        // Add listener for table selection
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayPersonDetails(newSelection);
            }
        });
    }

    /**
     * Set up keyboard shortcuts for menu items
     */
    private void setupMenuShortcuts() {
        // File menu shortcuts
        fileNewMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        fileOpenMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        fileSaveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        fileExitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

        // Edit menu shortcuts
        editClearMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        editDeleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        editUpdateMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN));

        // View menu shortcuts
        viewRefreshMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));

        // Theme menu shortcuts
        themeToggleMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
    }

    /**
     * Refresh table data from database
     */
    @FXML
    public void refreshTableData() {
        userData = dbOps.getAllUsers();
        userTable.setItems(userData);
        statusLabel.setText("Data refreshed from database");
    }

    /**
     * Display person details in form fields
     * @param person Person to display details for
     */
    private void displayPersonDetails(Person person) {
        firstNameField.setText(person.getName());
        lastNameField.setText(person.getLastName());
        emailField.setText(person.getEmail());
        phoneField.setText(person.getPhone());
        addressField.setText(person.getAddress());
        passwordField.setText(person.getPassword());

        if (person.getProfilePicture() != null) {
            Image image = person.getProfileImage();
            if (image != null) {
                profileImageView.setImage(image);
            }
        } else {
            // Set default image
            profileImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/javafxdb_sql_shellcode/images/default_profile.png")));
        }

        selectedImageFile = null;
    }

    /**
     * Handle "New" menu item action - clear form
     */
    @FXML
    private void handleNewAction(ActionEvent event) {
        clearForm();
    }

    /**
     * Handle "Open" menu item action - open file chooser
     */
    @FXML
    private void handleOpenAction(ActionEvent event) {
        selectProfileImage();
    }

    /**
     * Handle "Save" menu item action - save current form data
     */
    @FXML
    private void handleSaveAction(ActionEvent event) {
        saveUser();
    }

    /**
     * Handle "Exit" menu item action - close application
     */
    @FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Handle "Clear" menu item action - clear form
     */
    @FXML
    private void handleClearAction(ActionEvent event) {
        clearForm();
    }

    /**
     * Handle "Delete" menu item action - delete selected user
     */
    @FXML
    private void handleDeleteAction(ActionEvent event) {
        deleteUser();
    }

    /**
     * Handle "Update" menu item action - update selected user
     */
    @FXML
    private void handleUpdateAction(ActionEvent event) {
        updateUser();
    }

    /**
     * Handle "Refresh" menu item action - refresh table data
     */
    @FXML
    private void handleRefreshAction(ActionEvent event) {
        refreshTableData();
    }

    /**
     * Handle "Toggle Theme" menu item action - toggle between light and dark theme
     */
    @FXML
    private void handleToggleThemeAction(ActionEvent event) {
        boolean isDarkMode = App.toggleDarkMode();
        themeToggleMenuItem.setText(isDarkMode ? "Light Theme" : "Dark Theme");
    }

    /**
     * Handle "About" menu item action - show about dialog
     */
    @FXML
    private void handleAboutAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("User Management System");
        alert.setContentText("Version 1.0\nDeveloped for CSC311");
        alert.showAndWait();
    }

    /**
     * Clear form fields and reset profile image
     */
    @FXML
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        passwordField.clear();

        // Reset to default image
        profileImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/javafxdb_sql_shellcode/images/default_profile.png")));

        selectedImageFile = null;
        userTable.getSelectionModel().clearSelection();
        statusLabel.setText("Form cleared");
    }

    /**
     * Open file chooser to select profile image
     */
    @FXML
    private void selectProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        selectedImageFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());

        if (selectedImageFile != null) {
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                profileImageView.setImage(image);
                statusLabel.setText("Image selected: " + selectedImageFile.getName());
            } catch (Exception e) {
                statusLabel.setText("Error loading image");
                e.printStackTrace();
            }
        }
    }

    /**
     * Save/add new user to database
     */
    @FXML
    private void saveUser() {
        // Validate form fields
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statusLabel.setText("Error: Name, Email, and Password are required fields");
            return;
        }

        try {
            Person person = new Person(
                    null, // ID will be auto-generated
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText(),
                    passwordField.getText()
            );

            // Add profile picture if selected
            if (selectedImageFile != null) {
                FileInputStream fis = new FileInputStream(selectedImageFile);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum);
                }
                person.setProfilePicture(bos.toByteArray());
                fis.close();
            }

            boolean success = dbOps.insertUser(person);

            if (success) {
                statusLabel.setText("User added successfully");
                clearForm();
                refreshTableData();
            } else {
                statusLabel.setText("Error adding user");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update selected user in database
     */
    @FXML
    private void updateUser() {
        Person selectedPerson = userTable.getSelectionModel().getSelectedItem();

        if (selectedPerson == null) {
            statusLabel.setText("Error: No user selected");
            return;
        }

        // Validate form fields
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()  || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statusLabel.setText("Error: Name, Email, and Password are required fields");
            return;
        }

        try {
            Person updatedPerson = new Person(
                    selectedPerson.getId(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText(),
                    passwordField.getText()
            );

            // Add profile picture if selected
            if (selectedImageFile != null) {
                FileInputStream fis = new FileInputStream(selectedImageFile);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum);
                }
                updatedPerson.setProfilePicture(bos.toByteArray());
                fis.close();
            } else if (selectedPerson.getProfilePicture() != null) {
                // Keep existing profile picture if no new one selected
                updatedPerson.setProfilePicture(selectedPerson.getProfilePicture());
            }

            boolean success = dbOps.updateUser(updatedPerson);

            if (success) {
                statusLabel.setText("User updated successfully");
                clearForm();
                refreshTableData();
            } else {
                statusLabel.setText("Error updating user");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Delete selected user from database
     */
    @FXML
    private void deleteUser() {
        Person selectedPerson = userTable.getSelectionModel().getSelectedItem();

        if (selectedPerson == null) {
            statusLabel.setText("Error: No user selected");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete User");
        confirmAlert.setContentText("Are you sure you want to delete the selected user?");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = dbOps.deleteUser(selectedPerson.getId());

            if (success) {
                statusLabel.setText("User deleted successfully");
                clearForm();
                refreshTableData();
            } else {
                statusLabel.setText("Error deleting user");
            }
        }
    }

    /**
     * Handle table row selection
     */
    @FXML
    private void handleTableSelection(MouseEvent event) {
        Person selectedPerson = userTable.getSelectionModel().getSelectedItem();

        if (selectedPerson != null) {
            displayPersonDetails(selectedPerson);
        }
    }
}