# Module 5 Assinment

## Overview

This document summarizes the 
enhancements made to the User Management System, integrating the JavaFX UI with Azure MySQL database connectivity. 
The application now features a complete CRUD interface for user management with improved UI, theme support, and additional functionality.

## Key Enhancements

### 1. Database Integration
- **Azure Backend**
- **Complete CRUD Operations**: Added edit and delete functionality to the shell code

### 2. UI Improvements
- **Splash Screen**: Added a splash screen with the FSC logo and loading animation
- **Theme Support**: Implemented light and dark themes with on-the-fly switching
- **Keyboard Shortcuts**: Added shortcuts for all menu items (ex: Ctrl+F, Ctrl+S)

### 3. Profile Picture Support
- **Image Upload**: Added functionality to select and upload profile pictures
- **Image Storage**: Images are stored in the database as binary data


## Features Guide

### Theme Switching
Users can toggle between light and dark themes without restarting the application:
- **Via Menu**: Settings > Toggle Theme
- **Via Keyboard**: Ctrl+T

### Keyboard Shortcuts
- **Ctrl+N**: New record (clear form)
- **Ctrl+O**: Open profile picture selector
- **Ctrl+S**: Save current record
- **Ctrl+Q**: Quit application
- **Ctrl+L**: Clear form
- **Ctrl+D**: Delete selected record
- **Ctrl+U**: Update selected record
- **Ctrl+R**: Refresh data from database

### User Management
- **Add Users**: Fill in the form and click "Add" or press Ctrl+S
- **Edit Users**: Select a user, modify the form, and click "Edit" or press Ctrl+U
- **Delete Users**: Select a user and click "Delete" or press Ctrl+D
- **Search/Filter**: Search functionality to find specific users

### Profile Pictures
- Click on the profile picture area or use the "Open Profile Picture" menu option
- Select an image file from your system
- Image will be displayed in the UI and stored in the database

## Technical Implementation Details

### Database Schema

```sql
CREATE TABLE users (
    id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    phone VARCHAR(200),
    address VARCHAR(200),
    password VARCHAR(200) NOT NULL,
    profile_picture MEDIUMBLOB
);
```

### Theme Implementation
The application uses external CSS files for styling:
- `style.css`: Light theme
- `style-dark.css`: Dark theme variant of the styling

### Azure Database Connection
The application connects to an Azure MySQL database

## Running Instructions


### Launch Methods

Run "App" (src/main/java/org/example/javafxdb_sql_shellcode/App.java)


