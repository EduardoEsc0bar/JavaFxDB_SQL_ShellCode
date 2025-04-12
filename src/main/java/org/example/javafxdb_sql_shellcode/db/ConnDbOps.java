package org.example.javafxdb_sql_shellcode.db;

import com.example.module03_basicgui_db_interface.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

/**
 * Database operations class
 * Handles connections and CRUD operations for the Azure MySQL database
 * @author MoaathAlrajab (Modified)
 */
public class ConnDbOps {
    // Updated MYSQL_SERVER_URL without a database
    final String MYSQL_SERVER_URL = "jdbc:mysql://csc311escobar.mysql.database.azure.com:3306";
    // Once the database is created, we'll use this URL
    final String DB_NAME = "CSC311_DB";
    final String DB_URL = MYSQL_SERVER_URL + "/" + DB_NAME;
    final String USERNAME = "escoe22";
    final String PASSWORD = "FARM123$";

    /**
     * Connect to database and create necessary tables if they don't exist
     * @return true if database has registered users
     */
    public boolean connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found");
            e.printStackTrace();
            return false;
        }
        boolean hasRegistredUsers = false;

        try {
            //First, connect to MYSQL server and create the database if not created
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            statement.close();
            conn.close();

            //Second, connect to the database and create the table "users" if not created
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(200) NOT NULL,"
                    + "email VARCHAR(200) NOT NULL UNIQUE,"
                    + "phone VARCHAR(200),"
                    + "address VARCHAR(200),"
                    + "password VARCHAR(200) NOT NULL,"
                    + "profile_picture MEDIUMBLOB"
                    + ")";
            statement.executeUpdate(sql);

            //check if we have users in the table users
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegistredUsers = true;
                }
            }

            statement.close();
            conn.close();
            System.out.println("Database connection successful!");

        } catch (Exception e) {
            System.out.println("Database connection error:");
            e.printStackTrace();
        }

        return hasRegistredUsers;
    }

    /**
     * Query user by name
     * @param name Name to search for
     * @return Person object with matching name or null if not found
     */
    public Person queryUserByName(String name) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            Person person = null;

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                String password = resultSet.getString("password");
                byte[] profilePicture = resultSet.getBytes("profile_picture");

                person = new Person(id, name, email, phone, address, password, profilePicture);
                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Phone: " + phone + ", Address: " + address);
            }

            preparedStatement.close();
            conn.close();
            return person;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets all users from database
     * @return ObservableList of Person objects
     */
    public ObservableList<Person> getAllUsers() {
        ObservableList<Person> people = FXCollections.observableArrayList();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                String password = resultSet.getString("password");
                byte[] profilePicture = resultSet.getBytes("profile_picture");

                Person person = new Person(id, name, email, phone, address, password, profilePicture);
                people.add(person);

                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Phone: " + phone + ", Address: " + address);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return people;
    }

    /**
     * Print all users to console
     */
    public void listAllUsers() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Phone: " + phone + ", Address: " + address);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a new user into the database
     * @param person
     * @return "A new user was inserted successfully."
     */
    public boolean insertUser(Person person) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO users (name, email, phone, address, password, profile_picture) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getName());
            preparedStatement.setString(2, person.getEmail());
            preparedStatement.setString(3, person.getPhone());
            preparedStatement.setString(4, person.getAddress());
            preparedStatement.setString(5, person.getPassword());

            if (person.getProfilePicture() != null) {
                preparedStatement.setBytes(6, person.getProfilePicture());
            } else {
                preparedStatement.setNull(6, java.sql.Types.BLOB);
            }

            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                System.out.println("A new user was inserted successfully.");
                preparedStatement.close();
                conn.close();
                return true;
            }

            preparedStatement.close();
            conn.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Insert a new user into the database
     *
     * @param firstName
     * @param lastName
     * @param email
     * @param phone
     * @param address
     * @param password
     * @return
     */
    public boolean insertUser(String firstName, String lastName, String email, String phone, String address, String password) {
        Person person = new Person(null, firstName, lastName, email, phone, address, password);
        return insertUser(person);
    }

    /**
     * Insert a new user with profile picture
     *
     * @param firstName
     * @param lastName
     * @param email
     * @param phone
     * @param address
     * @param password
     * @param profilePicture
     * @return
     */
    public boolean insertUser(String firstName, String lastName, String email, String phone, String address, String password, File profilePicture) {
        try {
            Person person = new Person(null, firstName, lastName, email, phone, address, password);

            if (profilePicture != null) { //
                FileInputStream fis = new FileInputStream(profilePicture);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum);
                }
                person.setProfilePicture(bos.toByteArray());
                fis.close();
            }

            return insertUser(person);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing user
     * @param person Person object with updated data (id must be set)
     * @return true if update was successful
     */
    public boolean updateUser(Person person) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "UPDATE users SET name=?, email=?, phone=?, address=?, password=?";

            // Only include profile_picture in update if provided
            if (person.getProfilePicture() != null) {
                sql += ", profile_picture=? WHERE id=?";
            } else {
                sql += " WHERE id=?";
            }

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getName());
            preparedStatement.setString(2, person.getEmail());
            preparedStatement.setString(3, person.getPhone());
            preparedStatement.setString(4, person.getAddress());
            preparedStatement.setString(5, person.getPassword());

            if (person.getProfilePicture() != null) {
                preparedStatement.setBytes(6, person.getProfilePicture());
                preparedStatement.setInt(7, person.getId());
            } else {
                preparedStatement.setInt(6, person.getId());
            }

            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                System.out.println("User was updated successfully.");
                preparedStatement.close();
                conn.close();
                return true;
            }

            preparedStatement.close();
            conn.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a user from the database
     * @param id ID of user to delete
     * @return true if deletion was successful
     */
    public boolean deleteUser(int id) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "DELETE FROM users WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                System.out.println("User was deleted successfully.");
                preparedStatement.close();
                conn.close();
                return true;
            }

            preparedStatement.close();
            conn.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}