package com.helper.mysql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;

public class LocalLogin {
    private static Connection connect;
    public static Connection ULogin(String schema)
    {
        boolean login = false;
        int failCount = 0;
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        while (!login) {
            String user = "";
            System.out.print("Username: ");
            try
            {
                user = read.readLine();
            } catch (IOException e) { e.printStackTrace(); }

            String pass = "";
            System.out.print("Password: ");
            try
            {
                pass = read.readLine();
            } catch (IOException e) { e.printStackTrace(); }

            try
            {
                connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + schema, user, pass);
            }
            catch (SQLException e)
            {
                failCount++;
                System.out.println("Invalid username or password. Try again.");
                if (failCount > 3)
                {
                    System.out.println
                            ("Ensure that your locally hosted MySQL database is running, and that your credentials are valid.");
                }
                System.out.println("");
                continue;
            }

            login = true;
        }

        System.out.println("Login successful.\n");
        return connect;
    }

    public static Connection CLogin(String schema)
    {
        boolean login = false;
        int failCount = 0;
        while(!login)
        {
            String user = "";
            System.out.print("Username: ");
            user = System.console().readLine();

            String pass = "";
            System.out.print("Password: ");
            pass = String.valueOf(System.console().readPassword());

            try
            {
                connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + schema, user, pass);
            }
            catch (SQLException e)
            {
                failCount++;
                System.out.println("Invalid username or password. Try again.");
                if (failCount > 3)
                {
                    System.out.println
                            ("Ensure that your locally hosted MySQL database is running, and that your credentials are valid.");
                }
                System.out.println("");
                continue;
            }

            login = true;
        }

        System.out.println("Login successful.\n");
        return connect;
    }
}