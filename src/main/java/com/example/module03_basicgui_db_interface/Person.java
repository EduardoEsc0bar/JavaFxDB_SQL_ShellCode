package com.example.module03_basicgui_db_interface;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

/**
 * Person model class for database and UI integration
 * This class represents a user in the system and maps to the database schema
 */
public class Person {
    private String firstName;
    private String lastName;
    private String dept;
    private String major;
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;
    private byte[] profilePicture; // Store image as byte array for database? looked it up since I couldn't find out how to do it on my own

    /**
     * Default constructor
     */
    public Person() {
    }

    /**
     * Constructor with all fields except image, name split as first and last name
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param email
     * @param phone
     * @param address
     * @param password
     */
    public Person(Integer id, String firstName, String lastName, String email, String phone, String address, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    /**
     * Constructor with basic fields, name separated into first and last name
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param dept
     * @param major
     */
    public Person(Integer id, String firstName, String lastName, String dept, String major) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.dept = dept;
    }

    /**
     * Constructor with all fields including image, name in this constructor is only "name", not both first and last name
     *
     * @param id
     * @param name
     * @param email
     * @param phone
     * @param address
     * @param password
     * @param profilePicture
     */
    public Person(Integer id, String name, String email, String phone, String address, String password, byte[] profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Get the profile picture as a JavaFX Image
     * @return Image object from byte array data
     */
    public Image getProfileImage() {
        if (profilePicture != null && profilePicture.length > 0) {
            return new Image(new ByteArrayInputStream(profilePicture));
        }
        return null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}