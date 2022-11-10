package com.dbms.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Character;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.*;

///
/// BEFORE SUBMITTING:
/// GO THROUGH AND REMOVE ALL printStackTrace() CALLS
/// REPLACE WITH LEGIT ERROR HANDLING
///
/// THOROUGHLY TEST FOR CRASHES, WRITE TEST CASES
///
/// ALL REFERENCES TO com.helper.mysql BECOME JAR LIB WHEN PROGRAM COMPLETE
///

public class DatabaseCLI
{
    private static final char MIN_CHOICE = 'A';
    private static final char MAX_CHOICE = 'K';

    public static void main(String[] args)
    {
        // CHANGE TO COMPILED HELPER LIB WHEN COMPLETED
        Connection connect = null;
        if (System.console() == null) { connect = com.helper.mysql.LocalLogin.ULogin("library"); }
        else { connect = com.helper.mysql.LocalLogin.CLogin("library"); }

        boolean menuLoop = true;
        while(menuLoop) {
            PrintMenu();
            char choice = TakeSelection();
            ResultSet res = null;
            int changed = 0;
            switch (choice) {
                case 'A':
                    res = OptionA(connect);
                    if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res)) {
                        break;
                    }
                    com.helper.mysql.ResultSetParse.PrintResultSet(res);
                    break;
                case 'B':
                    res = OptionB(connect);
                    if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res)) {
                        break;
                    }
                    com.helper.mysql.ResultSetParse.PrintResultSet(res);
                    break;
                case 'C':
                    res = OptionC(connect);
                    if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res)) {
                        break;
                    }
                    com.helper.mysql.ResultSetParse.PrintResultSet(res);
                    break;
                case 'D':
                    res = OptionD(connect);
                    if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res)) {
                        break;
                    }
                    com.helper.mysql.ResultSetParse.PrintResultSet(res);
                    break;
                case 'E':
                    res = OptionE(connect);
                    if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res)) {
                        break;
                    }
                    com.helper.mysql.ResultSetParse.PrintResultSet(res);
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
                    if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res)) { break; }
                    com.helper.mysql.ResultSetParse.PrintResultSet(res);
                    break;
                case 'J':
                    res = OptionJ(connect);
                    if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res)) { break; }
                    com.helper.mysql.ResultSetParse.PrintResultSet(res);
                    break;
                case 'K':
                    changed = OptionK(connect);
                    if (changed > 0)
                        System.out.println("Fee paid.");
                    else
                        System.out.println("No fee was paid.");
                    break;
                case 'Q':
                    System.out.println("Quitting.");
                    menuLoop = false;
            }
        }

        System.exit(0);
    }

    private static void PrintMenu()
    {
        List<String> menuItems = Arrays.asList(
                "A: List all book information",
                "B: Get the name of the author of a book",
                "C: List the details of every book copy",
                "D: List the details of every library member",
                "E: Get the outstanding books for a member",
                "F: Return a book borrowed by a member",
                "G: Lend a book to a member",
                "H: Renew book for a member",
                "I: Check balance of member",
                "J: Check all unpaid fees for member",
                "K: Mark a fee as paid",
                "Q: Quit");

        String header = "\nMySQL Database Command Line Interface";
        System.out.println(header);
        for(int i = 0; i < header.length(); i++) { System.out.print('*'); }
        System.out.println("");

        for(int i = 0; i < menuItems.size(); i++)
        {
            System.out.println(menuItems.get(i));
        }
    }

    private static char TakeSelection()
    {
        char choice = '0';
        while ((choice < MIN_CHOICE || choice > MAX_CHOICE) && choice != 'Q') {
            try
            {
                BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(": ");
                choice = (char) read.read();
                choice = Character.toUpperCase(choice);
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        return choice;
    }

    private static ResultSet OptionA(Connection connect)
    {
        ResultSet res = null;
        try
        {
            String query =
                    "SELECT ISBN, title, genre.name AS genre_name, date_published, publisher, edition, description FROM book NATURAL JOIN genre";
            Statement stmt = connect.createStatement();
            res = stmt.executeQuery(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    private static ResultSet OptionB(Connection connect)
    {
        ResultSet res = null;
        try
        {
            String query =
                    "SELECT author.first_name, author.middle_name, author.last_name FROM book NATURAL JOIN book_author NATURAL JOIN author WHERE book.ISBN = ?";

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Book ISBN: ");
            String ISBN = read.readLine();

            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, ISBN);
            res = stmt.executeQuery();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    private static ResultSet OptionC(Connection connect)
    {
        ResultSet res = null;
        try
        {
            String query =
                    "SELECT ISBN, title, barcode FROM copy NATURAL JOIN book ORDER BY barcode ASC";
            Statement stmt = connect.createStatement();
            res = stmt.executeQuery(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    private static ResultSet OptionD(Connection connect)
    {
        ResultSet res = null;
        try
        {
            String query =
                    "SELECT card_no, first_name, middle_name, last_name FROM member ORDER BY card_no ASC";
            Statement stmt = connect.createStatement();
            res = stmt.executeQuery(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    private static ResultSet OptionE(Connection connect)
    {
        ResultSet res = null;
        try
        {
            String query =
                    "SELECT book.ISBN, title, copy.barcode, date_borrowed, renewals_no FROM borrow NATURAL JOIN copy NATURAL JOIN book WHERE card_no = ? AND borrow.date_returned IS NULL";

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
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    private static int OptionF(Connection connect)
    {
        int changed = 0;
        try
        {
            List<String> codeList = GetOutstandingBooks(connect, false);

            if (codeList == null)
            {
                System.out.println("Invalid member.");
                return 0;
            }

            if (codeList.size() == 1)
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

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            boolean validCode = false;
            codeList.remove(0);
            String code = "";
            while(!validCode)
            {
                System.out.print("Barcode to return: ");
                code = read.readLine();
                if (codeList.contains(code))
                {
                    validCode = true;
                    continue;
                }
                System.out.println("Invalid barcode. Select one from list above.\n");
            }

            // IMPLEMENT DATE SELECTION FOR FUN MAYBE
            //System.out.print("When it was returned (YYYY-MM-DD HH:MM:SS OR now): ");
            //String date = read.readLine();

            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, code);
            changed = stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return changed;
    }

    private static int OptionG(Connection connect)
    {
        int changed = 0;
        ResultSet res = null;
        String query = "";
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

            boolean validCode = false;
            String code = "";
            while (!validCode)
            {
                System.out.print("Borrowed book barcode: ");
                code = read.readLine();
                if (codeList.contains(code))
                {
                    validCode = true;
                    continue;
                }
                System.out.println("Invalid barcode. Select one from the list above.\n");
            }

            query = "INSERT INTO borrow VALUES (?, ?, now(), null, 0, null)";
            PreparedStatement prepStmt = connect.prepareStatement(query);
            prepStmt.setString(1, mem);
            prepStmt.setString(2, code);
            changed = prepStmt.executeUpdate();

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return changed;
    }

    private static int OptionH(Connection connect)
    {
        int changed = 0;
        try
        {
            List<String> codeList = GetOutstandingBooks(connect, true);

            if (codeList == null)
            {
                System.out.println("Invalid member.");
                return 0;
            }

            if (codeList.size() == 1)
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

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            boolean validCode = false;
            String code = "";
            while(!validCode)
            {
                System.out.print("Barcode to renew: ");
                code = read.readLine();
                if (codeList.subList(1, codeList.size()).contains(code))
                {
                    validCode = true;
                    continue;
                }
                System.out.println("Invalid barcode. Select one from list above.\n");
            }

            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, codeList.get(0));
            stmt.setString(2, code);
            changed = stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return changed;
    }

    private static ResultSet OptionI(Connection connect)
    {
        ResultSet res = null;
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
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    private static ResultSet OptionJ(Connection connect)
    {
        ResultSet res = null;
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
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    private static int OptionK(Connection connect)
    {
        int changed = 0;

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

            if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res))
            {
                System.out.println("Invalid member.");
                return 0;
            }

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

            String code;
            System.out.print("\nBarcode of book being repaid: ");
            do
            {
                code = read.readLine();
            } while(!codeList.contains(code));

            String query = "UPDATE borrow SET paid = TRUE WHERE card_no = ? AND barcode = ? AND paid IS NOT TRUE";
            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, mem);
            stmt.setString(2, code);
            changed = stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return changed;
    }

    /**
     * @param connect
     * @param renewable
     * @return List<String>, first entry is member card number. Null if SQLException.
     * @throws SQLException
     * @throws IOException
     */
    private static List<String> GetOutstandingBooks(Connection connect, boolean renewable) throws SQLException, IOException
    {
        String query;
        if (!renewable)
            query = "SELECT borrow.card_no, book.ISBN, title, copy.barcode, date_borrowed, renewals_no " +
                    "FROM borrow NATURAL JOIN copy NATURAL JOIN book " +
                    "WHERE card_no = ? AND borrow.date_returned IS NULL";
        else
            query = "SELECT borrow.card_no, book.ISBN, title, copy.barcode, date_borrowed, renewals_no " +
                    "FROM borrow NATURAL JOIN copy NATURAL JOIN book " +
                    "WHERE card_no = ? AND borrow.date_returned IS NULL AND renewals_no < 2";

        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Member card number: ");
        String mem = read.readLine();

        PreparedStatement stmt = connect.prepareStatement(query);
        stmt.setString(1, mem);
        ResultSet res = stmt.executeQuery();

        if (!com.helper.mysql.ResultSetParse.CheckResultsValid(res))
            return null;

        List<String> codeList = new ArrayList<String>();
        res.next();
        codeList.add(res.getString("card_no"));
        do
        {
            codeList.add(res.getString("barcode"));
        } while(res.next());

        return codeList;
    }

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

    private static boolean CheckMemberExists(Connection connect, String memCardNum) throws SQLException
    {
        String query = "SELECT * FROM member WHERE card_no = ?";

        PreparedStatement stmt = connect.prepareStatement(query);
        stmt.setString(1, memCardNum);
        ResultSet res = stmt.executeQuery();
        if (!res.next())
            return false;

        return true;
    }
}
