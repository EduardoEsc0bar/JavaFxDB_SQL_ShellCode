package org.example.javafxdb_sql_shellcode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://csc311escobar.mysql.database.azure.com:3306";
        String username = "escoe22";
        String password = "FARM123$";

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected successfully!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed:");
            e.printStackTrace();
        }
    }
}
