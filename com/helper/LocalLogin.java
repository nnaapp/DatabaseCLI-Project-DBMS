package com.helper;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// THESE ARE NOT DIRECT SOURCE FILES
// I HAVE COMPILED THESE INTO A JAR LIBRARY TO BE USED IN FUTURE
// THEY ARE INCLUDED TO PROVE I WROTE THEM

// Class for login related methods, for any localhost MySQL command line program
public class LocalLogin {
    private static Connection connect;

    /**
     * Universal Login method, works in true console and IDE terminals
     * @param schema String name of schema, for database URL
     * @return Connection to database, null on failed connection
     */
    public static Connection ULogin(String schema)
    {
        boolean login = false;
        int failCount = 0;
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        // Repeats until successful login, null return, or a System.exit() call
        while (!login) {
            // Get username input, as well as check for commands
            String user;
            try
            {
                user = BRInput(read, "Username: ");
            }
            catch (IOException e)
            {
                System.out.println("Input exception occurred, try again.");
                continue;
            }
            int cmdRes = CheckCommand(user);
            // Return null equates to repeating the whole login process
            // This can be used to reset when someone realized they put the wrong username in, for example
            if (cmdRes == 1)
                return null;

            // Get password input, as well as check for commands
            String pass;
            try
            {
                pass = BRInput(read, "Password: ");
            }
            catch (IOException e)
            {
                System.out.println("Input exception occurred, try again.");
                continue;
            }
            cmdRes = CheckCommand(pass);
            // Same use case as above
            if (cmdRes == 1)
                return null;

            // Attempt connection
            // A failed attempt results in SQLException, which causes some output and a repeat of loop
            // This cycle can be broken with :q/:Q command, causes program to quit
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

            // Getting here means the login worked, so break the loop
            login = true;
        }

        System.out.println("Login successful.\n");
        return connect;
    }

    /**
     * Console Login method, ONLY works in true console, but it hides your password
     * @param schema String name of schema, for database URL
     * @return Connection to database, null on failed connection
     */
    public static Connection CLogin(String schema)
    {
        // This functions identically to ULogin, but with a few key differences
        // The System.console() is used, which only works in a real console, not an IDE
        // This allows use of System.console().readPassword(), which disables echoing on input,
        // which hides your password. Doing so was impossible in an IDE.
        // Aside from that, the functionality is identical.

        boolean login = false;
        int failCount = 0;
        while(!login)
        {
            String user ;
            try
            {
                user = CMDInput("Username: ", false);
            }
            catch(IOError e)
            {
                System.out.println("Input exception occurred, try again.");
                continue;
            }
            int cmdRes = CheckCommand(user);
            if (cmdRes == 1)
                return null;

            String pass;
            try
            {
                pass = CMDInput("Password: ", true);
            }
            catch(IOError e)
            {
                System.out.println("Input exception occurred, try again.");
                continue;
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

    /**
     * Checks input for : commands. :q/:Q exits program, :r/:R restarts the login process.
     * @param cmd String to check for commands
     * @return Integer associated with found command, 1 for restart, 0 for nothing
     */
    private static int CheckCommand(String cmd)
    {
        // Set input to uppercase to avoid variance
        cmd = cmd.toUpperCase();

        // Return 0 if no command syntax
        if (cmd.charAt(0) != ':')
            return 0;

        // Turn string into array if syntax found
        char[] cmdArr = new char[cmd.length() - 1];
        for(int i = 1; i < cmd.length(); i++)
        {
            cmdArr[i - 1] = cmd.charAt(i);
        }

        // Iterate over array and execute command associated with any valid chars
        for (int i = 0; i < cmdArr.length; i++)
        {
            switch (cmdArr[i])
            {
                case 'Q': // Exit on :q/:Q
                    System.exit(0);
                case 'R': // Return 1, which tells the Login methods to re-try,
                           // use-case being re-trying after typo in username, etc
                    return 1;

            }
        }

        return 0; // Return 0, tells Login methods to proceed.
    }

    /**
     * Gets input using BufferedReader, with a prompt
     * @param read BufferedReader to get input from
     * @param prompt Prompt to present to user
     * @return String of user input
     * @throws IOException
     */
    private static String BRInput(BufferedReader read, String prompt) throws IOException
    {
        System.out.print(prompt);
        return read.readLine();
    }

    /**
     * Gets input using System.console(), with a prompt
     * @param prompt Prompt to present to user
     * @param password Toggles hiding input as it is typed
     * @return String of user input
     * @throws IOError
     */
    private static String CMDInput(String prompt, boolean password) throws IOError
    {
        System.out.print(prompt);
        // Ternary expression, uses readPassword() is password == true, else uses readLine();
        return password ? String.valueOf(System.console().readPassword()) : System.console().readLine();
    }
}