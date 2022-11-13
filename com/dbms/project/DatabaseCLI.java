package com.dbms.project;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.*;
import com.helper.*;

public class DatabaseCLI
{
    // Global constants to define menu start and end bounds, alphabetically
    private static final char MIN_CHOICE = 'A';
    private static final char MAX_CHOICE = 'K';

    /**
     * Main function, handles the menu loop and
     * calls helper functions such as the two login variants.
     * @param args Command line arguments, unused.
     */
    public static void main(String[] args)
    {
        // This loop re-tries the login until a connection is achieved.
        // This loop can be broken using the : syntax, :q or :Q quits.
        Connection connect;
        do
        {
            if (System.console() == null) // If not running in a true console, such as IDE terminal
                connect = LocalLogin.ULogin("library");
            else // Else, as in running in a true console
                connect = LocalLogin.CLogin("library");
        } while(connect == null);

        boolean menuLoop = true; // This is just to avoid an ugly while(true)
        // Menu loop, runs until the user enters the quit option
        while(menuLoop)
        {
            // Prints the visual elements, takes user choice
            PrintMenu();
            String choice = TakeSelection(); // This also cleans input by ensuring it is caps
            ResultSet res;
            int changed;
            // Only the first character inputted is considered
            // This means that "abcdefg" would result in case 'A"
            switch (choice.charAt(0))
            {
                // These cases are largely standardized
                // ResultSet based cases call their respective method, then pass set off to handler
                // Update based cases check how many rows were changed, and print output
                case 'A':
                    res = OptionA(connect);
                    HandleRes(res);
                    break;
                case 'B':
                    res = OptionB(connect);
                    HandleRes(res);
                    break;
                case 'C':
                    res = OptionC(connect);
                    HandleRes(res);
                    break;
                case 'D':
                    res = OptionD(connect);
                    HandleRes(res);
                    break;
                case 'E':
                    res = OptionE(connect);
                    HandleRes(res);
                    break;
                case 'F':
                    changed = OptionF(connect);
                    if (changed > 0)
                        System.out.println("Book returned.");
                    else
                        System.out.println("No book was returned.");
                    break;
                case 'G':
                    changed = OptionG(connect);
                    if (changed > 0)
                        System.out.println("Book borrowed.");
                    else
                        System.out.println("No book was borrowed.");
                    break;
                case 'H':
                    changed = OptionH(connect);
                    if (changed > 0)
                        System.out.println("Book renewed.");
                    else
                        System.out.println("No book could be renewed.");
                    break;
                case 'I':
                    res = OptionI(connect);
                    HandleRes(res);
                    break;
                case 'J':
                    res = OptionJ(connect);
                    HandleRes(res);
                    break;
                case 'K':
                    changed = OptionK(connect);
                    if (changed > 0)
                        System.out.println("Fee paid.");
                    else
                        System.out.println("No fee was paid.");
                    break;
                case 'Q':
                    // System.exit(0) could go in here, but I consider the resulting while(true) ugly
                    System.out.println("Quitting.");
                    menuLoop = false;
            }
        }

        System.exit(0);
    }

    /**
     * Prints out a menu, hard coded into the menuItems String list.
     * This could be loaded from a file, but that seemed to be not needed.
     */
    private static void PrintMenu()
    {
        // Menu in list form
        List<String> menuItems = Arrays.asList(
                "A : List all book information",
                "B : Get the name of the author of a book",
                "C : List the details of every book copy",
                "D : List the details of every library member",
                "E : Get the outstanding books for a member",
                "F : Return a book borrowed by a member",
                "G : Lend a book to a member",
                "H : Renew book for a member",
                "I : Check balance of member",
                "J : Check all unpaid fees for member",
                "K : Mark a fee as paid",
                "Q : Quit");

        String header = "\nMySQL Database Command Line Interface";
        System.out.println(header);
        // This loop prints out a line of *'s equivalent to the header
        for(int i = 0; i < header.length(); i++)
            System.out.print('*');
        System.out.println("");

        // Prints the list of menu entries
        for(int i = 0; i < menuItems.size(); i++)
            System.out.println(menuItems.get(i));
    }

    /**
     * Gets user input, in string form, capitalized it,
     * and ensured that the input is within the bounds of the menu.
     * @return The string the user entered, capitalized and with a 1st character within the given bounds.
     */
    private static String TakeSelection()
    {
        String choice;
        do
        {
            try
            {
                // Gets input and capitalized it
                BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(": ");
                choice = read.readLine();
                choice = choice.toUpperCase();
            }
            catch (IOException e)
            {
                System.out.println("Input exception occurred, try again.");
                choice = "0"; // Intentionally invalid, to reset loop
            }
        } while ((choice.charAt(0) < MIN_CHOICE || choice.charAt(0) > MAX_CHOICE) && choice.charAt(0) != 'Q');

        return choice;
    }

    private static ResultSet OptionA(Connection connect)
    {
        ResultSet res;
        try
        {
            String query =
                    "SELECT ISBN, title, genre.name AS genre_name, date_published, publisher, edition, description " +
                            "FROM book NATURAL JOIN genre";
            Statement stmt = connect.createStatement();
            res = stmt.executeQuery(query);
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return null;
        }

        return res;
    }

    private static ResultSet OptionB(Connection connect)
    {
        ResultSet res;
        try
        {
            String query =
                    "SELECT author.first_name, author.middle_name, author.last_name " +
                            "FROM book NATURAL JOIN book_author NATURAL JOIN author WHERE book.ISBN = ?";

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Book ISBN: ");
            String ISBN = read.readLine();

            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, ISBN);
            res = stmt.executeQuery();
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return null;
        }
        catch(IOException e)
        {
            System.out.println("Input exception occurred, returning to menu.");
            return null;
        }

        return res;
    }

    private static ResultSet OptionC(Connection connect)
    {
        ResultSet res;
        try
        {
            String query =
                    "SELECT ISBN, title, barcode FROM copy NATURAL JOIN book ORDER BY barcode ASC";
            Statement stmt = connect.createStatement();
            res = stmt.executeQuery(query);
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return null;
        }

        return res;
    }

    private static ResultSet OptionD(Connection connect)
    {
        ResultSet res;
        try
        {
            String query =
                    "SELECT card_no, first_name, middle_name, last_name FROM member ORDER BY card_no ASC";
            Statement stmt = connect.createStatement();
            res = stmt.executeQuery(query);
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return null;
        }

        return res;
    }

    private static ResultSet OptionE(Connection connect)
    {
        ResultSet res;
        try
        {
            String query =
                    "SELECT book.ISBN, title, copy.barcode, date_borrowed, renewals_no " +
                            "FROM borrow NATURAL JOIN copy NATURAL JOIN book " +
                            "WHERE card_no = ? AND borrow.date_returned IS NULL";

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Member card number: ");
            String mem = read.readLine();

            if (!CheckMemberExists(connect, mem))
            {
                System.out.println("Member could not be found.");
                return null;
            }

            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, mem);
            res = stmt.executeQuery();
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return null;
        }
        catch(IOException e)
        {
            System.out.println("Input exception occurred, returning to menu.");
            return null;
        }

        return res;
    }

    private static int OptionF(Connection connect)
    {
        int changed;
        try
        {
            List<String> codeList = GetOutstandingBooks(connect, false);

            if (codeList == null)
            {
                System.out.println("Invalid member.");
                return 0;
            }
            else if (codeList.size() == 1)
            {
                System.out.println("No outstanding books for that member.");
                return 0;
            }

            System.out.println("");
            for (int i = 1; i < codeList.size(); i++)
            {
                System.out.println(codeList.get(i));
            }
            System.out.println("");

            String query =
                    "UPDATE borrow SET date_returned = now() WHERE barcode = ? AND date_returned IS NULL";

            String code = GetInputFromList(codeList, "Barcode to return: ", true);
            if (code == null)
                return 0;

            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, code);
            changed = stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return 0;
        }
        catch(IOException e)
        {
            System.out.println("Input exception occurred, returning to menu.");
            return 0;
        }

        return changed;
    }

    private static int OptionG(Connection connect)
    {
        int changed;
        ResultSet res;
        String query;
        try
        {
            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Member card number: ");
            String mem = read.readLine();

            if (!CheckMemberExists(connect, mem))
            {
                System.out.println("Member could not be found.");
                return 0;
            }

            Statement stmt = connect.createStatement();
            query = "SELECT copy.barcode FROM copy LEFT OUTER JOIN borrow ON copy.barcode = borrow.barcode " +
                    "GROUP BY copy.barcode " +
                    "HAVING sum(CASE WHEN date_borrowed IS NOT NULL AND date_returned IS NULL THEN 1 ELSE 0 END) = 0";
            res = stmt.executeQuery(query);
            if (!res.next())
            {
                System.out.println("No books are available to borrow.");
                return 0;
            }

            List<String> codeList = new ArrayList<String>();
            do
            {
                codeList.add(res.getString("barcode"));
            } while(res.next());

            System.out.println("Available books: ");
            for (int i = 0; i < codeList.size(); i++)
            {
                System.out.println(codeList.get(i));
            }
            System.out.println("");

            String code = GetInputFromList(codeList, "Barcode to borrow: ", false);
            if (code == null)
                return 0;

            query = "INSERT INTO borrow VALUES (?, ?, now(), null, 0, null)";
            PreparedStatement prepStmt = connect.prepareStatement(query);
            prepStmt.setString(1, mem);
            prepStmt.setString(2, code);
            changed = prepStmt.executeUpdate();

        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return 0;
        }
        catch(IOException e)
        {
            System.out.println("Input exception occurred, returning to menu.");
            return 0;
        }

        return changed;
    }

    private static int OptionH(Connection connect)
    {
        int changed;
        try
        {
            List<String> codeList = GetOutstandingBooks(connect, true);

            if (codeList == null)
            {
                System.out.println("Invalid member.");
                return 0;
            }
            else if (codeList.size() == 1)
            {
                System.out.println("No outstanding books for that member.");
                return 0;
            }

            System.out.println("");
            for (int i = 1; i < codeList.size(); i++)
            {
                System.out.println(codeList.get(i));
            }
            System.out.println("");

            String query = "UPDATE borrow SET renewals_no = renewals_no + 1 WHERE card_no = ? AND barcode = ?";

            String code = GetInputFromList(codeList, "Barcode to renew: ", true);
            if (code == null)
                return 0;

            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, codeList.get(0));
            stmt.setString(2, code);
            changed = stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return 0;
        }
        catch(IOException e)
        {
            System.out.println("Input exception occurred, returning to menu.");
            return 0;
        }

        return changed;
    }

    private static ResultSet OptionI(Connection connect)
    {
        ResultSet res;
        try
        {
            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Member card number: ");
            String mem = read.readLine();

            if (!CheckMemberExists(connect, mem))
            {
                System.out.println("Member could not be found.");
                return null;
            }

            String query = "WITH outstanding AS (SELECT * FROM borrow WHERE card_no = ? AND paid IS NOT TRUE) " +
                    "SELECT coalesce(sum(days) * 0.25, 0) AS fees " +
                    "FROM (SELECT datediff(CASE WHEN date_returned IS NULL THEN now() ELSE date_returned END, date_borrowed) " +
                    "- (14 * (renewals_no + 1)) AS days FROM outstanding) AS diff " +
                    "WHERE days > 0";

            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, mem);
            res = stmt.executeQuery();
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return null;
        }
        catch(IOException e)
        {
            System.out.println("Input exception occurred, returning to menu.");
            return null;
        }

        return res;
    }

    private static ResultSet OptionJ(Connection connect)
    {
        ResultSet res;
        try
        {
            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Member card number: ");
            String mem = read.readLine();

            if (!CheckMemberExists(connect, mem))
            {
                System.out.println("Member could not be found.");
                return null;
            }

            res = GetOverdueBooks(connect, mem);
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return null;
        }
        catch(IOException e)
        {
            System.out.println("Input exception occurred, returning to menu.");
            return null;
        }

        return res;
    }

    private static int OptionK(Connection connect)
    {
        int changed;

        try
        {
            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Member card number: ");
            String mem = read.readLine();

            if (!CheckMemberExists(connect, mem))
            {
                System.out.println("Member could not be found.");
                return 0;
            }

            ResultSet res = GetOverdueBooks(connect, mem);

            if (!res.next())
            {
                System.out.println("No unpaid books could be found.");
                return 0;
            }

            List<String> codeList = new ArrayList<String>();
            System.out.println("");
            do
            {
                String tempCode = res.getString("barcode");
                codeList.add(tempCode);
                System.out.println(tempCode);
            } while(res.next());

            String code = GetInputFromList(codeList, "\nBarcode of book being repaid: ", false);
            if (code == null)
                return 0;

            String query = "UPDATE borrow SET paid = TRUE WHERE card_no = ? AND barcode = ? AND paid IS NOT TRUE";
            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, mem);
            stmt.setString(2, code);
            changed = stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Exception occurred, check connection.");
            return 0;
        }
        catch(IOException e)
        {
            System.out.println("Input exception occurred, returning to menu.");
            return 0;
        }

        return changed;
    }

    /**
     * Gets list of outstanding books for a user, can be only renewable books,
     * or books regardless of if renewable or not
     * @param connect Connection to database
     * @param renewable If the books should be limited to renewable or not
     * @return List of barcodes, first entry is member card number. Null if SQLException.
     * @throws SQLException
     * @throws IOException
     */
    private static List<String> GetOutstandingBooks(Connection connect, boolean renewable) throws SQLException, IOException
    {
        // This block alters the query depending on the renewable argument
        String query;
        if (!renewable)
            query = "SELECT borrow.card_no, book.ISBN, title, copy.barcode, date_borrowed, renewals_no " +
                    "FROM borrow NATURAL JOIN copy NATURAL JOIN book " +
                    "WHERE card_no = ? AND borrow.date_returned IS NULL";
        else
            query = "SELECT borrow.card_no, book.ISBN, title, copy.barcode, date_borrowed, renewals_no " +
                    "FROM borrow NATURAL JOIN copy NATURAL JOIN book " +
                    "WHERE card_no = ? AND borrow.date_returned IS NULL AND renewals_no < 2";

        // Gets and checks member card number using CheckMemberExists(), returns null if member is invalid
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Member card number: ");
        String mem = read.readLine();

        if (!CheckMemberExists(connect, mem))
            return null;

        // Generated result set from query and member card no
        PreparedStatement stmt = connect.prepareStatement(query);
        stmt.setString(1, mem);
        ResultSet res = stmt.executeQuery();

        // Checks if set is irregular or invalid
        if (!ResultSetParse.CheckResultsValid(res))
            return null;

        // Returns if set is regular but empty, otherwise, generates list of barcodes
        List<String> codeList = new ArrayList<String>();
        if (!res.next())
            return null;
        codeList.add(res.getString("card_no"));
        do
        {
            codeList.add(res.getString("barcode"));
        } while(res.next());

        return codeList;
    }

    /**
     *  Takes a member card number and generates a set of overdue books associated with them.
     * @param connect Connection to database
     * @param memCardNum Valid member card number
     * @return Result set of overdue books for given member
     * @throws SQLException
     */
    private static ResultSet GetOverdueBooks(Connection connect, String memCardNum) throws SQLException
    {
        String query =
                "WITH outstanding AS (SELECT * FROM borrow WHERE card_no = ? AND paid IS NOT TRUE) " +
                        "SELECT book.ISBN, book.title, copy.barcode, date_borrowed, date_returned, days * 0.25 AS fees " +
                        "FROM (SELECT card_no, barcode, date_borrowed, date_returned, " +
                        "datediff(CASE WHEN date_returned IS NULL THEN now() ELSE date_returned END, date_borrowed) " +
                        "- (14 * (renewals_no + 1)) AS days FROM outstanding) AS diff " +
                        "NATURAL JOIN copy NATURAL JOIN book " +
                        "WHERE days > 0";

        PreparedStatement stmt = connect.prepareStatement(query);
        stmt.setString(1, memCardNum);
        ResultSet res = stmt.executeQuery();

        return res;
    }

    /**
     * Checks if a member card number exists in the database instance
     * @param connect Connection to database
     * @param memCardNum Member card number to be checked
     * @return True if member exists, false if not.
     * @throws SQLException
     */
    private static boolean CheckMemberExists(Connection connect, String memCardNum) throws SQLException
    {
        // Works by generically selecting from members, limited to provided number
        // A real member will not have an empty set
        String query = "SELECT * FROM member WHERE card_no = ?";

        PreparedStatement stmt = connect.prepareStatement(query);
        stmt.setString(1, memCardNum);
        ResultSet res = stmt.executeQuery();
        if (!res.next())
            return false;

        return true;
    }

    /**
     * Checks regularity of result set, if regular, attempts to print set out to console
     * @param res Any ResultSet
     * @return True if print was attempted, false if set was irregular
     */
    private static boolean HandleRes(ResultSet res)
    {
        if (!ResultSetParse.CheckResultsValid(res))
        {
            System.out.println("\nNo results.");
            return false;
        }

        ResultSetParse.PrintResultSet(res);
        return true;
    }

    /**
     * Gets user input from a list of strings, this uses a while loop
     * which can be broken using the command :q or :Q
     * @param list List of strings, will not break if empty
     * @param prompt Prompt to present to user
     * @param trimFirst Do not consider first element of list if true,
     *                  useful for lists with 0 indexed identifiers
     * @return Validated user input from the list, or null if loop was terminated
     * @throws IOException
     */
    private static String GetInputFromList(List<String> list, String prompt, boolean trimFirst)
    {
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        boolean validSelect = false;

        // Lists are passed BY REFERENCE.
        // This copies the contents, not the object itself.
        List<String> temp = new ArrayList<>(List.copyOf(list));
        if (trimFirst) // This is why we want a unique copy, to trim 0 element.
            temp.remove(0);

        // Gets user input, checks for : commands (:Q, here), checks if valid input
        String select = null;
        while(!validSelect)
        {
            try
            {
                System.out.print(prompt);
                select = read.readLine();
                int cmdRes = CheckSubpromptCommand(select);
                if (cmdRes == 1)
                    return null;

                if (temp.contains(select))
                {
                    validSelect = true;
                    continue;
                }
                System.out.println("Invalid selection. Select one from list above.\n");
            }
            catch(IOException e)
            {
                System.out.println("Input exception occurred, try again.");
            }
        }

        return select;
    }

    /**
     * Checks input for command syntax. : denotes an input command.
     * :q/:Q mean to back out of the input prompt, here.
     * @param cmd String to check for commands
     * @return Integer associated with found command, 1 for quit, 0 for nothing
     */
    private static int CheckSubpromptCommand(String cmd)
    {
        // Set input to uppercase to avoid variance
        cmd = cmd.toUpperCase();

        // Return 0 if no command syntax
        if (cmd.charAt(0) != ':')
            return 0;

        // Turn string into array if syntax found
        char[] cmdArr = new char[cmd.length() - 1];
        for (int i = 1; i < cmd.length(); i++)
        {
            cmdArr[i - 1] = cmd.charAt(i);
        }

        // Iterate over array and execute command associated with any valid chars
        for (int i = 0; i < cmdArr.length; i++)
        {
            switch (cmdArr[i])
            {
                case 'Q': // Return to menu on :q/:Q
                    return 1;
            }
        }

        return 0; // Return 0, means proceed
    }
}
