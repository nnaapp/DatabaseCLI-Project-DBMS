package com.helper.mysql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LocalLogin {
    private static final List<String> cmds = Arrays.asList(":Q", ":R");

    private static Connection connect;
    public static Connection ULogin(String schema)
    {
        boolean login = false;
        int failCount = 0;
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        while (!login) {
            String user = "";
            try
            {
                user = BRInput(read, "Username: ");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            int cmdRes = CheckCommand(user);
            if (cmdRes == 1)
                return null;

            String pass = "";
            try
            {
                pass = BRInput(read, "Password: ");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            cmdRes = CheckCommand(pass);
            if (cmdRes == 1)
                return null;

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
            user = CMDInput("Username: ", false);
            int cmdRes = CheckCommand(user);
            if (cmdRes == 1)
                return null;

            String pass = "";
            pass = CMDInput("Password: ", true);
            cmdRes = CheckCommand(pass);
            if (cmdRes == 1)
                return null;

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

    private static int CheckCommand(String cmd)
    {
        cmd = cmd.toUpperCase();

        if (!cmds.contains(cmd))
            return 0;

        switch (cmd)
        {
            case ":Q": // Exit on :q/:Q
                System.exit(0);
            case ":R": // Return 1, which tells the Login methods to re-try,
                       // use-case being re-trying after typo in username, etc
                return 1;

        }

        return 0; // Return 0, tells Login methods to proceed.
    }

    private static String BRInput(BufferedReader read, String prompt) throws IOException
    {
        String str = "";
        System.out.print(prompt);
        str = read.readLine();

        return str;
    }

    private static String CMDInput(String prompt, boolean password)
    {
        System.out.print(prompt);
        String str = password ? String.valueOf(System.console().readPassword()) : System.console().readLine();
        return str;
    }
}